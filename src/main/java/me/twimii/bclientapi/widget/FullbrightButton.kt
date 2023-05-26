package me.twimii.bclientapi.widget

import me.twimii.bclientapi.BClientAbstractScreen
import me.twimii.bclientapi.IBClientWidgetManager
import me.twimii.bclientapi.module.GammaController
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class FullbrightButton : SimpleButtonWidget() {
    override var preface: Text = Text.literal("Fullbright: ")
    override var onText: MutableText = Text.literal("Enabled")
    override var offText: MutableText = Text.literal("Disabled")
    override fun getIsOn(): Boolean {
        return GammaController.customGamma
    }

    override fun wasClicked() {
        GammaController.customGamma = !GammaController.customGamma
    }
}