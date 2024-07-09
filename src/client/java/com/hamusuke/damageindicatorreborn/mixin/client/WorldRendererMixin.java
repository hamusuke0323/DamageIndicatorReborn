package com.hamusuke.damageindicatorreborn.mixin.client;

import com.hamusuke.damageindicatorreborn.DamageIndicatorRebornClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        if (!DamageIndicatorRebornClient.clientConfig.hideIndicator.get() && !DamageIndicatorRebornClient.queue.isEmpty()) {
            this.client.getProfiler().push("damage indicator rendering");
            var impl = this.client.getBufferBuilders().getEntityVertexConsumers();
            matrices.push();
            DamageIndicatorRebornClient.queue.forEach(indicatorRenderer -> indicatorRenderer.render(matrices, impl, this.client.getEntityRenderDispatcher().camera, tickDelta));
            matrices.pop();
            impl.draw();
            this.client.getProfiler().pop();
        }
    }
}
