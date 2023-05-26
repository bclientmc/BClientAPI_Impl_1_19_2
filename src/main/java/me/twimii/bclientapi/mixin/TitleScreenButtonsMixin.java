package me.twimii.bclientapi.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import me.twimii.bclientapi.GUIUtil;
import me.twimii.bclientapi.PreloadToastQueue;
import me.twimii.bclientapi.TitleScreenLoadedCallback;
import me.twimii.bclientapi.auth.BClientAuthInit;
import me.twimii.bclientapi.auth.SessionUtils;
import me.twimii.bclientapi.auth.TitleScreenAuthWidget;
import me.twimii.bclientapi.hacks.DummyOtherClientPlayerEntity;
import me.twimii.bclientapi.title.SexierButtonWidget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Mixin(Screen.class)
interface TitleScreenAccessor {
    @Invoker("addDrawableChild")
    <T extends Element & Drawable & Selectable> T invokeAddDrawableChild(T drawableElement);

    @Invoker("addSelectableChild")
    <T extends Element & Drawable & Selectable> T invokeAddSelectableChild(T drawableElement);

    @Accessor
    MinecraftClient getClient();

    @Accessor
    List<Drawable> getDrawables();

    @Accessor
    List<Selectable> getSelectables();


}

@Mixin(TitleScreen.class)
interface RealTitleScreenAccessor {
    @Accessor
    void setSplashText(String e);
}

@Mixin(TitleScreen.class)
public abstract class TitleScreenButtonsMixin {
    private final Identifier LOGO_TEXTURE = new Identifier("bclientapi", "textures/gui/logo.png");
    boolean loaded = false;
    private int counted = 0;
    private int lastSpacing = 0;
    private ArrayList<Drawable> widgets;
    private OtherClientPlayerEntity player;

    // Don't render titles n shit
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target =
                    "Lnet/minecraft/client/gui/screen/TitleScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V"
    ))
    private void skiprender(MatrixStack matrixStack, int i1, int i2, float v1, float v2, int i5, int i6, int i7, int i8) {
    }

    // Don't render titles n shit
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target =
                    "Lnet/minecraft/client/gui/screen/TitleScreen;drawWithOutline(IILjava/util/function/BiConsumer;)V"
    ))
    private void skiprender(TitleScreen instance, int i1, int i2, BiConsumer biConsumer) {
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    private void init(CallbackInfo info) {
        TitleScreenLoadedCallback.EVENT.invoker().interact();
        widgets = new ArrayList<>();
        TitleScreen th = ((TitleScreen) ((Object) this));
        th.children().clear();
        ((TitleScreenAccessor) th).getDrawables().clear();
        ((TitleScreenAccessor) th).getSelectables().clear();
        ((RealTitleScreenAccessor) th).setSplashText(null);

        int sbwidth = Math.max(220, (int) (th.width * 0.4));
        int btnWidth = 160;
        int btnX = (sbwidth / 2) - 80;
        int btnStartY = lastSpacing + 72;
        int btnHeight = 20;
        int btnPadding = 5;

        SexierButtonWidget play = new SexierButtonWidget(btnX, btnStartY, btnWidth, btnHeight, Text.translatable("menu.singleplayer"), (button) -> {
            MinecraftClient.getInstance().setScreen(new SelectWorldScreen(th));
        });

        ((TitleScreenAccessor) th).invokeAddSelectableChild(play);
        widgets.add(play);

        SexierButtonWidget multiplayer = new SexierButtonWidget(btnX, btnStartY + (btnHeight + btnPadding), btnWidth, btnHeight, Text.translatable("menu.multiplayer"), (button) -> {
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(th));
        });

        ((TitleScreenAccessor) th).invokeAddSelectableChild(multiplayer);
        widgets.add(multiplayer);

        SexierButtonWidget options = new SexierButtonWidget(btnX, btnStartY + ((btnHeight + btnPadding) * 2), btnWidth, btnHeight, Text.translatable("menu.options"), (button) -> {
            MinecraftClient.getInstance().setScreen(new OptionsScreen(th, MinecraftClient.getInstance().options));
        });

        ((TitleScreenAccessor) th).invokeAddSelectableChild(options);
        widgets.add(options);

        SexierButtonWidget a11y = new SexierButtonWidget(btnX-5, btnStartY + ((btnHeight + btnPadding) * 3), btnWidth/2, btnHeight, Text.translatable("narrator.button.accessibility"), (button) -> {
            MinecraftClient.getInstance().setScreen(new AccessibilityOptionsScreen(th, MinecraftClient.getInstance().options));
        });

        ((TitleScreenAccessor) th).invokeAddSelectableChild(a11y);
        widgets.add(a11y);

        SexierButtonWidget lang = new SexierButtonWidget(btnX + (btnWidth/2), btnStartY + ((btnHeight + btnPadding) * 3), btnWidth/2, btnHeight, Text.translatable("narrator.button.language"), (button) -> {
            MinecraftClient.getInstance().setScreen(new LanguageOptionsScreen(th, MinecraftClient.getInstance().options, MinecraftClient.getInstance().getLanguageManager()));
        }, a11y.getScaleUp());

        ((TitleScreenAccessor) th).invokeAddSelectableChild(lang);
        widgets.add(lang);


        SexierButtonWidget quit = new SexierButtonWidget(btnX, th.height - (btnPadding*8), btnWidth, btnHeight, Text.translatable("menu.quit"), (button) -> {
            MinecraftClient.getInstance().scheduleStop();
        });

        ((TitleScreenAccessor) th).invokeAddSelectableChild(quit);
        widgets.add(quit);


        // Auth button
        // Above the skin render

        int x = th.width-60;
        int y = ((th.height / 2)+40)-70;

        TitleScreenAuthWidget aut = new TitleScreenAuthWidget(
                x,
                y,
                40,
                20,
                (button) -> { // Callback
                    SessionUtils.getStatus().thenAccept(s -> {
                        if (s == SessionUtils.SessionStatus.VALID) {
                            GUIUtil.INSTANCE.loggedInMessage();
                        } else {
                            TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.LOGGING_IN);
                            BClientAuthInit.INSTANCE.BClientMSLogin();
                        }
                    });
                }
        );
        ((TitleScreenAccessor) th).invokeAddSelectableChild(aut);
        widgets.add(aut);

    }

    @Inject(at = @At("RETURN"), method = {"initWidgetsNormal", "initWidgetsDemo"})
    private void addCustomButton(int y, int spacing, CallbackInfo info) {
        lastSpacing = spacing;
        TitleScreen titleScreen = (TitleScreen) (Object) this;



        counted++;
        if (counted > 1) {
            if (!loaded) {
                // start the authentication process
                System.out.println("start auth");
                ExecutorService executor = Executors.newSingleThreadExecutor();
                TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.LOGGING_IN);
                CompletableFuture<Session> future = BClientAuthInit.INSTANCE.defaultLogin(executor);

                if (future != null) {
                    future.thenAccept(session -> {
                        System.out.println("got session");
                        SessionUtils.setSession(session);
                        SessionUtils.getStatus().thenAccept(s -> {
                            if (s != SessionUtils.SessionStatus.VALID) {
                                TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.AUTH_REQUIRED);
                                PreloadToastQueue.INSTANCE.getQueue().add(SystemToast.create(
                                        MinecraftClient.getInstance(),
                                        SystemToast.Type.PACK_LOAD_FAILURE,
                                        Text.literal("Automatic login failed"),
                                        Text.literal("Invalid session")
                                ));
                            } else {
                                TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.LOGGED_IN);
                                BClientAuthInit.INSTANCE.checkPlaying();
                                PreloadToastQueue.INSTANCE.getQueue().add(GUIUtil.INSTANCE.loggedInMessageObj());
                                BClientAuthInit.INSTANCE.login();
                            }
                        });
                    });
                    future.exceptionally(err -> {
                        System.out.println("failed");
                        TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.AUTH_REQUIRED);
                        PreloadToastQueue.INSTANCE.getQueue().add(SystemToast.create(
                                MinecraftClient.getInstance(),
                                SystemToast.Type.PACK_LOAD_FAILURE,
                                Text.literal("Automatic login failed"),
                                Text.literal("Could not get minecraft session")
                        ));

                        return null;
                    });
                }
                TitleScreenAuthWidget.setStatus(TitleScreenAuthWidget.AuthButtonStatus.AUTH_REQUIRED);
            }
            loaded = true;
            AtomicInteger wait = new AtomicInteger();
            ClientTickEvents.END_CLIENT_TICK.register((client -> {
                if (wait.getAndIncrement() == 60) {
                    PreloadToastQueue.INSTANCE.getQueue().forEach((Toast a) -> {
                        MinecraftClient.getInstance().getToastManager().add(a);
                    });
                }
                PreloadToastQueue.INSTANCE.getQueue().clear();
            }));
        }
    }

    /**
     * Creates a dummy player.
     *
     * @return A dummy player
     */
    private OtherClientPlayerEntity getDummyPlayer() {
        if (player == null) {

            //hack to avoid creating a world
            Field f = null;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            f.setAccessible(true);
            Unsafe unsafe = null;
            try {
                unsafe = (Unsafe) f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            ClientWorld clientWorld = null;
            try {
                clientWorld = (ClientWorld) unsafe.allocateInstance(ClientWorld.class);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            ClientWorld.Properties props = new ClientWorld.Properties(Difficulty.PEACEFUL, false, false);
            ((ClientWorldAccessor) clientWorld).setClientWorldProperties(props);
            ((WorldAccessor) clientWorld).setProperties(props);
            ((WorldAccessor) clientWorld).setBorder(new WorldBorder());
            GameProfile profile = SessionUtils.getSession().getProfile();

            player = new DummyOtherClientPlayerEntity(clientWorld, profile);
            ((ClientPlayerEntityAccessor) player).setCachedScoreboardEntry(
                    new PlayerListEntry(
                            new PlayerListS2CPacket.Entry(profile,
                                    0,
                                    GameMode.DEFAULT,
                                    Text.literal(profile.getName()),
                                    null
                            ),
                            MinecraftClient.getInstance().getServicesSignatureVerifier(),
                            false
                    )
            );
        }

        return player;
    }


    @Inject(at = @At("TAIL"), method = {"render"})
    private void addSkin(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BClientAuthInit.INSTANCE.shouldReloadSkin()) {
            player = null;
            System.out.println("Did reload skin");
        }
        TitleScreen th = ((TitleScreen) ((Object) this));
        OtherClientPlayerEntity op = getDummyPlayer();


        int sbwidth = Math.max(220, (int) (th.width * 0.4));


        Color gray = Color.DARK_GRAY;

        // Sidebar buttons
        DrawableHelper.fill(matrices, 0, 0, sbwidth, th.height, new Color(gray.getRed(), gray.getGreen(), gray.getBlue(), 190).getRGB());

        // fill background for skin and  buttons

        int sx = th.width-90;
        int sy = ((th.height / 2)+40)-80;

        DrawableHelper.fill(matrices, sx, sy, th.width, sy + 180, new Color(gray.getRed(), gray.getGreen(), gray.getBlue(), 190).getRGB());

        // render skin


        int x = th.width-40;
        int y = (th.height / 2) + 40;
        InventoryScreen.drawEntity(x, y+70,60, x - mouseX,  y - mouseY, op);

        int lWidth = (sbwidth / 2) - 90;
        // Draw the logo centered
        RenderSystem.setShaderTexture(0, LOGO_TEXTURE);
        DrawableHelper.drawTexture(matrices, lWidth, lastSpacing, 180, (int) ((40f / 218f) * 180f),
                0, 0,
                218, 40, 218, 40);
        widgets.forEach(w -> {
            w.render(matrices, mouseX, mouseY, delta);
        });
    }
}