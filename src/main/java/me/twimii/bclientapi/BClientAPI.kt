package me.twimii.bclientapi

import me.twimii.bclientapi.widget.FullbrightButton
import me.twimii.bclientapi.widget.SimpleModuleWidget
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector2f
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color

/*
class MyScreen(title: Text?) : Screen(title) {
    private var myHeight: Int? = null
    override fun renderBackground(matrices: MatrixStack) {
        val x: Int = (width/2) - (SizeUtil.guiItemWidth/2)
        val y: Int = (height/2) - (myHeight!!/2)
        fill(matrices, x, y, x + SizeUtil.guiItemWidth, y + myHeight!!, Color(
            76, 74, 72, 230).rgb)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun init() {
        super.init()
        myHeight = SizeUtil.getHeightByWidth(SizeUtil.guiItemWidth)
    }
}*/

class BClientAPI : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("bclientapi")
    private var keyBinding: KeyBinding? = null
    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        keyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.bclientapi.test",  // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_RIGHT_SHIFT,  // The keycode of the key
                "category.bclientapi.test" // The translation key of the keybinding's category.
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (keyBinding!!.wasPressed()) {
                val module = SimpleModuleWidget()
                val screen = SimpleModuleScreen(Text.literal("Test module"), module)
                module.addWidget(FullbrightButton())
                module.addWidget(FullbrightButton())
                module.addWidget(FullbrightButton())
                MinecraftClient.getInstance().setScreen(screen)
            }
        })
        logger.info("BClientAPI loaded")
    }

}

