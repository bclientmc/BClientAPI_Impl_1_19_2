package me.twimii.bclientapi.auth

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.PlayerSkinDrawer
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

@Environment(EnvType.CLIENT)
class AuthToast private constructor(
    private val type: Type,
    private var title: Text,
    lines: List<OrderedText>,
    private val texture: PlayerHeadSkinGetter,
    sWidth: Int
) :
    Toast {
    private var lines: List<OrderedText>?
    private var startTime: Long = 0
    private var justUpdated = false
    private val width: Int





    init {
        this.lines = lines
        this.width = sWidth
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return 20 + lines!!.size * 12
    }

    override fun draw(matrices: MatrixStack, manager: ToastManager, startTime: Long): Toast.Visibility {
        if (justUpdated) {
            this.startTime = startTime
            justUpdated = false
        }
        val optTexture = if (texture.getDone()) texture.getSkin() else texture.getDefault()
        RenderSystem.setShaderTexture(0, Toast.TEXTURE)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val i = getWidth()
        var j: Int
        if (i == 160 && lines!!.size <= 1) {
            manager.drawTexture(matrices, 0, 0, 0, 64, i, this.height)
        } else {
            j = this.height
            val l = 4.coerceAtMost(j - 28)
            drawPart(matrices, manager, i, 0, 0, 28)
            var m = 28
            while (m < j - l) {
                drawPart(matrices, manager, i, 16, m, 16.coerceAtMost(j - m - l))
                m += 10
            }
            drawPart(matrices, manager, i, 32 - l, j - l, l)
        }
        if (lines == null) {
            manager.client.textRenderer.draw(matrices, title, 18.0f, 12.0f, -256)
        } else {
            manager.client.textRenderer.draw(matrices, title, 18.0f, 7.0f, -256)
            j = 0
            while (j < lines!!.size) {
                if (j == 1 && optTexture != null) {
                    RenderSystem.setShaderTexture(0, optTexture)
                    PlayerSkinDrawer.draw(matrices, 18, 18 + j * 12, 8)
                    RenderSystem.setShaderTexture(0, Toast.TEXTURE)
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
                }
                manager.client.textRenderer.draw(matrices, lines!![j], 18.0f, (18 + j * 12).toFloat(), -1)
                ++j
            }
        }
        return if (startTime - this.startTime < type.displayDuration) Toast.Visibility.SHOW else Toast.Visibility.HIDE
    }

    private fun drawPart(matrices: MatrixStack, manager: ToastManager, width: Int, textureV: Int, y: Int, height: Int) {
        val i = if (textureV == 0) 20 else 5
        val j = 60.coerceAtMost(width - i)
        manager.drawTexture(matrices, 0, y, 0, 64 + textureV, i, height)
        var k = i
        while (k < width - j) {
            manager.drawTexture(matrices, k, y, 32, 64 + textureV, 64.coerceAtMost(width - k - j), height)
            k += 64
        }
        manager.drawTexture(matrices, width - j, y, 160 - j, 64 + textureV, j, height)
    }

    fun setContent(title: Text, description: Text?) {
        this.title = title
        lines = getTextAsList(description)
        justUpdated = true
    }

    override fun getType(): Type {
        return type
    }

    @Environment(EnvType.CLIENT)
    enum class Type(val displayDuration: Long = 5000L) {
        AUTH_STATUS_TOAST
    }

    companion object {
        const val MIN_WIDTH = 200
        const val LINE_HEIGHT = 12
        const val PADDING_Y = 10


        fun getTextAsList(text: Text?): ImmutableList<OrderedText> {
            return if (text == null) ImmutableList.of() else ImmutableList.of(text.asOrderedText())
        }

        fun aadd(manager: ToastManager, type: Type, title: Text, description: List<OrderedText>, skinTexture: PlayerHeadSkinGetter) {
            val at = AuthToast(type, title, description, skinTexture,
                (description.maxOf(MinecraftClient.getInstance().textRenderer::getWidth).toFloat() * 1.5f).toInt()
            )
            manager.add(at)
        }
        fun create(type: Type, title: Text, description: List<OrderedText>, skinTexture: PlayerHeadSkinGetter): AuthToast {
            val at = AuthToast(type, title, description, skinTexture,
                (description.maxOf(MinecraftClient.getInstance().textRenderer::getWidth).toFloat() * 1.5f).toInt()
            )
            return at
        }

    }
}