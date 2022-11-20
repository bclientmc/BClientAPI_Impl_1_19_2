package me.twimii.bclientapi


fun interface ClickCallback {
    fun clicked(mouseX: Double, mouseY: Double, button: Int)
}