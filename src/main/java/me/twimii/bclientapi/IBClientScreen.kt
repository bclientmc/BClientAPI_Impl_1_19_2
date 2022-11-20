package me.twimii.bclientapi


import net.minecraft.client.util.math.Vector2f

interface IBClientScreen {
    fun setPausesGame(p: Boolean): IBClientScreen
    fun addClickzone(topLeft: Vector2f, bottomRight: Vector2f, callback: ClickCallback)
    fun scheduleRenderEvent(eventName: String, ticks: Int)
    fun queryRenderEvent(eventName: String): Boolean
}

