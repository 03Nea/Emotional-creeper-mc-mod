package com.example.examplemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class BabyCreeperRenderer extends CreeperRenderer {
    // Vi använder vanliga creeper-texturen för tillfället
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");

    public BabyCreeperRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void scale(Creeper creeper, PoseStack poseStack, float partialTickTime) {
        // Gör bebisen liten visuellt (50% av storleken)
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper entity) {
        return TEXTURE;
    }
}