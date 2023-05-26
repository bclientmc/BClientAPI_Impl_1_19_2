package me.twimii.bclientapi.widget

import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed.UNION.STR
import me.twimii.bclientapi.*
import me.twimii.bclientlanshare.BClientLanShare
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import java.awt.Color

internal class OpenButton(private val l: LanWidget) : SimpleButtonWidget() {
    override var preface: Text = Text.literal("Make public")
    override var onText: MutableText = Text.literal("")
    override var offText: MutableText = Text.literal("")

    override fun getIsOn(): Boolean {
        return false
    }

    override fun wasClicked() {
        l.start()
    }
}


class LanWidget : SimpleModuleWidget() {
    override fun setup(screen: IBClientScreen) {
        super.setup(screen)
        addWidget(OpenButton(this))
        //addWidget(lw)
    }
    fun start() {
        BClientLanShare.shareToLan()
    }
}