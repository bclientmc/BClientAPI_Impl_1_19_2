package me.twimii.bclientlanshare

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text
import java.io.BufferedReader

import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.function.Consumer
import kotlin.concurrent.thread


object BClientLanShare {
    private const val proxyAddr = "localhost"
    private const val proxyPort = 8081
    private var code: String? = null

    init {
        ClientPlayConnectionEvents.JOIN.register() {
                _, _, _ ->
            code = null

        }
        ClientPlayConnectionEvents.DISCONNECT.register() {
                _, _ ->
            code = null
        }
    }

    fun canShareToLan(): Boolean {
        val client = MinecraftClient.getInstance()
        if (!client.isInSingleplayer)
            return false
        if (!client.isMultiplayerEnabled)
            return false
        if (client.server!!.isRemote)
            return false
        return true
    }

    fun shareErr(msg: Text) {
        SystemToast.add(MinecraftClient.getInstance().toastManager, SystemToast.Type.PACK_LOAD_FAILURE,
            Text.literal("Error connecting"),
            msg)
    }

    /** Converts a 4-byte array into an uint32  */
    fun byteArrayToUInt32(data: ByteArray?): Long {
        return java.nio.ByteBuffer.wrap(data).order(java.nio.ByteOrder.LITTLE_ENDIAN).long
    }

    fun shareToLan(): Boolean {
        val client = MinecraftClient.getInstance()
        if (!canShareToLan())
            return false
        client.server!!.openToLan(client.server!!.defaultGameMode,
            client.server!!.playerManager.areCheatsAllowed(),
            29969
            )
        val s = Socket(proxyAddr, proxyPort)
        val os = s.getOutputStream()
        val ins = s.getInputStream()
        var data: ByteArray = byteArrayOf()
        os.write(0)
        os.flush()
        ins.readNBytes(data, 0, 1024)
        if (data[0] != (200).toByte()) {
            shareErr(Text.literal("Could not establish tunnel connection"))
            return false
        }
        os.write(0) // 0 is host code
        os.flush()
        data = byteArrayOf()
        ins.readNBytes(data, 0, 1024)
        code = byteArrayToUInt32(data).toString()
        println(code)
        thread {
            data = byteArrayOf()
            while (true) {
                ins.readNBytes(data, 0, 1024)
                val target = byteArrayToUInt32(data)
                println("Tunneling $target")
                thread {
                    val s1 = Socket(proxyAddr, proxyPort)
                    val os1 = s1.getOutputStream()
                    val ins1 = s1.getInputStream()
                    os1.write(0)
                    os1.flush()
                    var data1 = byteArrayOf()
                    ins1.readNBytes(data1, 0, 1024)
                    if (data1[0] != (200).toByte()) {
                        shareErr(Text.literal("Could not tunnel $target"))
                    }
                    os1.write(2) // 2 is host -> player tunnel code
                    os1.flush()
                    //os1.write()
                }
            }
        }
        return true
    }

    fun getCode(): String? {
        return code
    }

}