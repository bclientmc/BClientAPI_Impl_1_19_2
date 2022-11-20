package me.twimii.bclientapi.widget

import me.twimii.bclientapi.*
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color
import kotlin.math.min

class SimpleModuleWidget : IWidget, IBClientWidgetManager {
    private var items: MutableList<IWidget> = mutableListOf()
    override var minWidth: Int = 0
    override var height: Int = 0
    var unExpandedWidth: Int = 0

    override fun preRender(manager: IBClientWidgetManager?, screen: BClientAbstractScreen) {
        val (width, lheight) = getInfoFromWidgets(screen)
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
        var cHeight = -1
        var cX = 0

        for (widget in items) {
            if (cHeight == -1)
                cHeight = -widget.height

            if (cHeight + (2*widget.height) > screen.height) {
                cX += unExpandedWidth
                cHeight = 0
            } else {
                cHeight += widget.height
            }
            widget.render(
                matrices, mouseX, mouseY, cX + myX,
                myY + cHeight, unExpandedWidth, GUIUtil.backgroundColor, this, screen)


        }
    }

    override fun setup(screen: IBClientScreen) {

    }


    private fun getInfoFromWidgets(screen: BClientAbstractScreen): List<Int> {
        var w = GUIUtil.guiItemWidth;
        var ch = 0
        var h = 0
        var pushed = 1
        for (widget in items) {
            if (widget.minWidth > w) {
                w = widget.minWidth
            }
            if (h + widget.height > screen.height) {
                pushed++
                h = 0
            } else {
                h += widget.height
            }
            if (h > ch)
                ch = h
        }
        unExpandedWidth = w
        w *= pushed
        return listOf(w, ch)
    }
}