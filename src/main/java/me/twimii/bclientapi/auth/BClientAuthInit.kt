package me.twimii.bclientapi.auth


import com.google.gson.JsonParser
import me.twimii.bclientapi.GUIUtil
import me.twimii.bclientapi.GUIUtil.loggedInMessage
import me.twimii.bclientapi.PreloadToastQueue
import me.twimii.bclientapi.auth.SessionUtils.SessionStatus
import me.twimii.bclientapi.auth.TitleScreenAuthWidget.Companion.status
import me.twimii.bclientapi.auth.TokenStore.getLastSuccessfulLoginDate
import me.twimii.bclientapi.mixin.MinecraftClientAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.toast.SystemToast
import net.minecraft.client.util.Session
import net.minecraft.text.Text
import org.apache.commons.codec.digest.DigestUtils
import org.lwjgl.glfw.GLFW
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Function
import kotlin.math.abs


object BClientAuthInit {
    private var toReloadTitleSkin: Boolean = false
    private var allowedPlay: Boolean = false
    fun login() {
        println("bclient login")
        // Send a request to bclient server
        val httpClient = HttpClient.newBuilder().build()
        val resp1 = httpClient.send(HttpRequest.newBuilder(URI("http://localhost:8000/v1/login/begin")).build(), HttpResponse.BodyHandlers.ofString())
        try {
            val resp1Body = JsonParser().parse(resp1.body()).asJsonObject.get("data").asJsonObject
            val hashTpl = resp1Body.get("hashtpl").asString
            val codeHex  = resp1Body.get("codeHex").asString
            val jwt = resp1Body.get("jwt").asString
            val hashed = DigestUtils.sha1Hex(hashTpl
                .replace("{codeHex}", codeHex)
                .replace("uuid", MinecraftClient.getInstance().session.uuid))
            PreloadToastQueue.queue.add(SystemToast.create(
                MinecraftClient.getInstance(),
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.literal("Auth"), Text.literal("Logged in to BClient")))

        } catch (e: java.lang.Exception) {
            println("auth failed")
            PreloadToastQueue.queue.add(SystemToast.create(
                MinecraftClient.getInstance(),
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.literal("Auth error"), Text.literal("Could not login to BClient servers. Some features " +
                        "may be unavailable")))
            println(e)
        }
    }

    private fun TimeUnit.getDateDiff(date1: Date, date2: Date): Long {
        val diffInMillies = date2.time - date1.time
        return convert(diffInMillies, TimeUnit.MILLISECONDS)
    }



    fun defaultLogin(executor: ExecutorService) : CompletableFuture<Session>? {
        val tok = TokenStore.getSavedToken()
        if (tok != null) {
            return MicrosoftUtils.login(tok, executor)
        }
        return null
    }

    fun BClientMSLogin() { // Callback
        if (!SessionUtils.getAuthProvider().isLoggedIn) {
            val executor = Executors.newSingleThreadExecutor()
            MicrosoftUtils.myPrompt = MicrosoftUtils.MicrosoftPrompt.SELECT_ACCOUNT
            // Start the login task
            val task = MicrosoftUtils // Acquire a Microsoft auth code
                .acquireMSAuthCode(Function { _: Boolean? ->
                    MicrosoftUtils.myPrompt = MicrosoftUtils.MicrosoftPrompt.DEFAULT
                    GLFW.glfwFocusWindow(
                        (MinecraftClient.getInstance() as MinecraftClientAccessor).window
                            .handle
                    )
                    return@Function """
<html>
    <head>
        <title>Login</title>
    </head>
    <body style="background-color: #eceff4">
        <h1 style="position: absolute;
                                            top: 50%;
                                            left: 50%;
                                            transform:
                                            translate(-50%, -50%);"
        >
            You may now close this window
        </h1>
        <script>
            setInterval(() => {
                window.close();
            }, 5000)
        </script>
    </body>
</html>
""";
                }, executor) // Exchange the Microsoft auth code for an access token
                .thenComposeAsync { msAuthCode: String? ->
                    GUIUtil.loginToast("Getting access token...")
                    MicrosoftUtils.acquireMSAccessToken(msAuthCode, executor)
                } // Exchange the Microsoft access token for an Xbox access token
                .thenComposeAsync { msAccessToken: String? ->
                    GUIUtil.loginToast("Getting xbox access token...")
                    MicrosoftUtils.acquireXboxAccessToken(msAccessToken, executor)
                } // Exchange the Xbox access token for an XSTS token
                .thenComposeAsync { xboxAccessToken: String? ->
                    GUIUtil.loginToast("Getting XSTS token...")
                    MicrosoftUtils.acquireXboxXstsToken(xboxAccessToken, executor)
                } // Exchange the Xbox XSTS token for a Minecraft access token
                .thenComposeAsync { xboxXstsData: Map<String, String> ->
                    GUIUtil.loginToast("Getting minecraft token...")
                    MicrosoftUtils.acquireMCAccessToken(
                        xboxXstsData["Token"], xboxXstsData["uhs"], executor
                    )
                } // Build a new Minecraft session with the Minecraft access token
                .thenComposeAsync { mcToken: String ->
                    GUIUtil.loginToast("Logging in to minecraft...")
                    TokenStore.saveToken(mcToken);
                    MicrosoftUtils.login(mcToken, executor)
                } // Update the game session and greet the player
                .thenAccept { session: Session? ->
                    // Apply the new session
                    println("Logged in successfully!")
                    SessionUtils.setSession(session)
                    checkPlaying()
                    loggedInMessage()
                } // On any exception, update the status and cancel button
                .exceptionally { error: Throwable? ->
                    GUIUtil.loginToast("An error occurred!")
                    status = TitleScreenAuthWidget.AuthButtonStatus.AUTH_REQUIRED
                    null // return a default value
                }
        }}

    fun checkPlaying() {
        SessionUtils.getStatus().thenAccept() {
            if (it == SessionStatus.VALID) {
                TitleScreenAuthWidget.status = TitleScreenAuthWidget.AuthButtonStatus.LOGGED_IN
                enablePlaying()
                TokenStore.saveLastSuccessfulLoginDate(Date())
            } else
                TitleScreenAuthWidget.status = TitleScreenAuthWidget.AuthButtonStatus.AUTH_REQUIRED
        }
    }

    fun enablePlaying() {
        // This used to be used to activate normal playing
        // Originally we put minecraft into demo mode until a user logged in

        // Reload the skin
        setShouldReloadSkin(true);
    }

    fun getStatus() : String {
        // TODO status
        return "Not logged in"
    }

    fun shouldReloadSkin(): Boolean {
        val toRet = toReloadTitleSkin
        if (toReloadTitleSkin)
            toReloadTitleSkin = false
        return toRet
    }
    
    fun setShouldReloadSkin(should: Boolean) {
        toReloadTitleSkin = should
    }
}