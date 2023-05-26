package me.twimii.bclientapi

import me.twimii.bclientapi.widget.LanWidget
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory


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
                val module = LanWidget()
                val screen = SimpleModuleScreen(Text.literal("Test module"), module)
                MinecraftClient.getInstance().setScreen(screen)
            }
        })
        logger.info("BClientAPI loaded")

    }

}

