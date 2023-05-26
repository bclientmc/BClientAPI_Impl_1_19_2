package me.twimii.bclientapi.hacks

import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier

class DummyPlayerRenderer(ctx: EntityRendererFactory.Context, slim: Boolean) : PlayerEntityRenderer(ctx, slim) {
}