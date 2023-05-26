package me.twimii.bclientapi

import net.minecraft.client.toast.SystemToast
import net.minecraft.client.toast.Toast
import net.minecraft.text.Text

object PreloadToastQueue {
    val queue: MutableList<Toast> = mutableListOf()
}