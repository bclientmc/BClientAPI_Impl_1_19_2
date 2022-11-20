package me.twimii.bclientapi

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector2f
import net.minecraft.text.Text
import java.util.concurrent.atomic.AtomicInteger


open class SimpleModuleScreen(title: Text, private val widget: IWidget) : BClientAbstractScreen(title) {
    private var clickCallbacks: MutableList<Pair<Pair<Vector2f, Vector2f>, ClickCallback>> = mutableListOf()
    private var renderEvents: MutableList<Pair<String, AtomicInteger>> = mutableListOf()
    private var finishedEvents: MutableList<String> = mutableListOf()
    var position: Vector2f = Vector2f(0f, 0f)
    private var pausesGame = false

    override fun shouldPause(): Boolean {
        return pausesGame
    }
    override fun setPausesGame(p: Boolean) = apply { pausesGame = p }


    override fun addClickzone(topLeft: Vector2f, bottomRight: Vector2f, callback: ClickCallback) {
        clickCallbacks.add(Pair(Pair(topLeft, bottomRight), callback))
    }

    override fun scheduleRenderEvent(eventName: String, ticks: Int) {
        renderEvents.add(eventName to AtomicInteger(ticks))
    }

    override fun queryRenderEvent(eventName: String): Boolean {
        if (finishedEvents.contains(eventName)) {
            finishedEvents.remove(eventName)
            return true
        }
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for ((position: Pair<Vector2f, Vector2f>, runnable: ClickCallback) in clickCallbacks) {
            val (tl, br) = position
            if (mouseX > tl.x && mouseX < br.x && mouseY > tl.y && mouseY < br.y)
                runnable.clicked(mouseX, mouseY, button)
        }
        return true
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        clickCallbacks.clear()
        widget.preRender(null, this)
        // Background
        widget.render(matrices!!, mouseX, mouseY, position.x.toInt(),
            position.y.toInt(), width, GUIUtil.backgroundColor, null, this)
    }


    override fun init() {
        ClientTickEvents.END_CLIENT_TICK.register() {
            renderEvents.removeIf {
                val (eventName, ticks) = it
                if (ticks.decrementAndGet() < 0) {
                    finishedEvents.add(eventName)
                    return@removeIf true
                }
                return@removeIf false
            }
        }
        widget.setup(this)
        super.init()
    }
}