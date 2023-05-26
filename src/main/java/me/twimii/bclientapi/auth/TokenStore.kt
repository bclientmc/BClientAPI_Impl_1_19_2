package me.twimii.bclientapi.auth

import net.minecraft.client.MinecraftClient
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.util.Date

object TokenStore {
    private const val FILE = "bclientauthcache.auth"
    private const val DATEFILE = "bclientauthcache.date"

    fun getSavedToken(): String? {
        val tokenSaved = MinecraftClient.getInstance().runDirectory.resolve(FILE)
        if (!tokenSaved.exists()) {
            return null
        }
        return tokenSaved.readText(Charsets.ISO_8859_1)
    }

    fun saveToken(tok: String) {
        val tokenSaved = MinecraftClient.getInstance().runDirectory.resolve(FILE)
        tokenSaved.createNewFile()
        tokenSaved.writeText(tok, Charsets.ISO_8859_1)
    }

    fun getLastSuccessfulLoginDate(): Date? {
        val dateSaved = MinecraftClient.getInstance().runDirectory.resolve(DATEFILE)
        if (!dateSaved.exists()) {
            return null
        }
        return try {
            DateFormat.getInstance().parse(dateSaved.readText(Charsets.ISO_8859_1))
        } catch (e: ParseException) {
            null
        }
    }

    fun saveLastSuccessfulLoginDate(date: Date) {
        val dateSaved = MinecraftClient.getInstance().runDirectory.resolve(DATEFILE)
        dateSaved.createNewFile()
        dateSaved.writeText(DateFormat.getInstance().format(date))
    }

}