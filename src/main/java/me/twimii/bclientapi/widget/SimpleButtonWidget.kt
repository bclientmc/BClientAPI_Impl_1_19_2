package me.twimii.bclientapi.widget

import me.twimii.bclientapi.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector2f
import net.minecraft.text.Text
import java.awt.Color

abstract class SimpleButtonWidget : IWidget {
    override var minWidth: Int = 0
    override var height: Int = 40
    abstract var preface: Text
    open var onText = Text.literal("On")
    open var offText = Text.literal("Off")

    abstract fun getIsOn(): Boolean
    abstract fun wasClicked()
    override fun preRender(manager: IBClientWidgetManager?, screen: BClientAbstractScreen) {

    }
    override fun render(
        matrices: MatrixStack,
        mouseX: Int,
        mouseY: Int,
        myX: Int,
        myY: Int,
        width: Int,
        backgroundColor: Color,
        manager: IBClientWidgetManager?,
        screen: BClientAbstractScreen
    ) {
        if (screen.queryRenderEvent("println")) {
            //println(listOf(matrices, myX, myY, width, height))
            screen.scheduleRenderEvent("println", 60)
        }
        Screen.fill(matrices, myX, myY, myX + width, myY + height, Color.DARK_GRAY.rgb)

        GUIUtil.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer,
            preface.copy().append(if (getIsOn()) onText else offText), myX + (width/2), myY + (height/2), Color.WHITE.rgb)

        screen.addClickzone(
            Vector2f(myX.toFloat(), myY.toFloat()),
            Vector2f((myX + width).toFloat(), (myY + height).toFloat())
        )
        { x, y, btn ->
            wasClicked()
        }

    }
    override fun setup(screen: IBClientScreen) {
        screen.scheduleRenderEvent("println", 20)
    }
}