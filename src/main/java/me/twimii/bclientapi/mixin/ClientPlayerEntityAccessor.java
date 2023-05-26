package me.twimii.bclientapi.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.MutableWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {

    @Accessor
    @Mutable
    void setCachedScoreboardEntry(PlayerListEntry p);

}

@Mixin(AbstractClientPlayerEntity.class)
abstract class AbstractClientPlayerEntityMixin {

}
