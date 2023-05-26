package me.twimii.bclientapi.title

import me.twimii.bclientapi.mixin.FontManagerAccessor
import me.twimii.bclientapi.mixin.MinecraftClientAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.FontManager
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color
import kotlin.math.min

// This is a minecraft widget not a BClient widget

const val DebugDrawBackground = false


open class SexierButtonWidget @JvmOverloads constructor(x: Int, y: Int, width: Int, height: Int, message: Text, onPress: PressAction, private val presetScaleUp: Float = -1f) :
    ButtonWidget(x, y, width, height, message, onPress) {

    private fun createTextRenderer(): TextRenderer {
        val fm: FontManager = (MinecraftClient.getInstance() as MinecraftClientAccessor).fontManager;
        return TextRenderer({ id: Identifier? ->
            (fm as FontManagerAccessor).fontStorages.getOrDefault(
                MinecraftClient.UNICODE_FONT_ID,
                (fm as FontManagerAccessor).missingStorage
            )
        }, false)
    }

    private val tr: TextRenderer = createTextRenderer()

    private var lineBack: Int = 0
    private var lineForward: Int = 0

    fun getScaleUp(): Float {
        val scaleUpH: Float = height.toFloat() / tr.fontHeight
        val scaleUpW: Float = width.toFloat() / tr.getWidth(message)
        return min(scaleUpW, scaleUpH)
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (DebugDrawBackground) {
            fill(matrices, x, y, x + width, y + height, Color.RED.rgb)
        }
        // We can find how much we can scale the text based on height
        val scaleUpH: Float = height.toFloat() / tr.fontHeight
        val scaleUpW: Float = width.toFloat() / tr.getWidth(message)
        val scaleUp = if (presetScaleUp != -1f) presetScaleUp else min(scaleUpW, scaleUpH)
        matrices.push()
        matrices.translate((x + width / 2).toDouble(), (y + (height - (tr.fontHeight.toFloat()*scaleUp) - 2) / 2).toDouble(), 0.0)
        val tw = tr.getWidth(message) * scaleUp
        matrices.push()
        matrices.scale(scaleUp, scaleUp, scaleUp)
        val col = Color(Color.WHITE.red, Color.WHITE.green, Color.WHITE.blue, 255).rgb
        drawUnderlinedCenteredTextNoShadow(
            matrices, tr,
            message, col,
            ((tr.fontHeight.toFloat()*scaleUp)*scaleUp).toInt(),
            tw.toInt()
        )
        matrices.pop()
        val scaledTextHeight = ((tr.fontHeight.toFloat()*scaleUp)*scaleUp).toInt()
        fill(matrices, -lineBack, scaledTextHeight/2, lineForward, (scaledTextHeight/2)+2, col)
        matrices.pop()
    }


    private fun drawUnderlinedCenteredTextNoShadow(
        matrices: MatrixStack?,
        textRenderer: TextRenderer,
        text: Text,
        color: Int,
        scaledTextHeight: Int,
        targetWidth: Int
    ) {
        drawCenteredTextNoShadow(matrices, textRenderer, text, color)
        if (isHovered) {
            if (lineBack < targetWidth/2 || lineForward < targetWidth/2) {
                lineBack += 3
                lineForward += 3
            }
        } else if (lineBack > 0 || lineForward > 0) {
            lineBack -= 3
            lineForward -= 3
        }
    }

    private fun drawCenteredTextNoShadow(
        matrices: MatrixStack?,
        textRenderer: TextRenderer,
        text: Text,
        color: Int
    ) {
        val orderedText = text.asOrderedText()
        textRenderer.drawWithShadow(
            matrices,
            orderedText,
            (0 - textRenderer.getWidth(orderedText) / 2).toFloat(),
            0f,
            color
        )
    }
}