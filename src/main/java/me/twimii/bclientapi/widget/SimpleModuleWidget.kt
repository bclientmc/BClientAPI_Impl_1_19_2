package me.twimii.bclientapi.widget

import me.twimii.bclientapi.*
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color
import kotlin.math.min

open class SimpleModuleWidget : IWidget, IBClientWidgetManager {
    internal var items: MutableList<IWidget> = mutableListOf()
    override var minWidth: Int = 0
    override var height: Int = 0

    override fun preRender(manager: IBClientWidgetManager?, screen: BClientAbstractScreen) {
        val (width, lheight) = getInfoFromWidgets()
        height = lheight
        minWidth = width
    }

    override fun addWidget(widget: IWidget) = apply { items.add(widget) }


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
        // Background
        Screen.fill(matrices, myX, myY, myX + minWidth, myY + height, GUIUtil.backgroundColor.rgb)
        for (widget in items)
            widget.preRender(this, screen)
        var cHeight = 0

        for (widget in items) {
            widget.render(
                matrices, mouseX, mouseY, myX,
                myY + cHeight, minWidth, GUIUtil.backgroundColor, this, screen)
            cHeight += widget.height
        }
    }

    override fun setup(screen: IBClientScreen) {

    }


    private fun getInfoFromWidgets(): List<Int> {
        var w = GUIUtil.guiItemWidth;
        var h = 0
        for (widget in items) {
            if (widget.minWidth > w) {
                w = widget.minWidth
            }
            h += widget.height
        }
        return listOf(w, h)
    }
}