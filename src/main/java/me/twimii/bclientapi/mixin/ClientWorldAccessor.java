package me.twimii.bclientapi.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
interface WorldAccessor {

    @Accessor
    @Mutable
    void setProperties(MutableWorldProperties p);

    @Accessor
    @Mutable
    void setBorder(WorldBorder b);
}
@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {

    @Accessor
    @Mutable
    void setClientWorldProperties(ClientWorld.Properties p);

}
