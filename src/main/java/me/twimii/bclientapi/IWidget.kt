package me.twimii.bclientapi

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color

interface IWidget {
    var minWidth: Int
    var height: Int
    fun preRender(manager: IBClientWidgetManager?, screen: BClientAbstractScreen)
    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, myX: Int, myY: Int,
               width: Int, backgroundColor: Color, manager: IBClientWidgetManager?, screen: BClientAbstractScreen)
    fun setup(screen: IBClientScreen)
}