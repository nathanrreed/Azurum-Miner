package com.nred.azurum_miner.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.nred.azurum_miner.item.VoidBullet
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation


class VoidBulletRenderer(val context: EntityRendererProvider.Context) : EntityRenderer<VoidBullet>(context) {
    private var model: VoidBulletModel? = null

    init {
        this.model = VoidBulletModel(context.bakeLayer(VoidBulletModel.LAYER_LOCATION))
    }

    override fun render(pEntity: VoidBullet, entityYaw: Float, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int) {
        poseStack.pushPose()
        poseStack.translate(0.0f, -1.25f, 0.0f)
        val vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, this.model!!.renderType(this.getTextureLocation(pEntity)), false, false)
        this.model!!.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.popPose()

        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(entity: VoidBullet): ResourceLocation {
        return azLoc("textures/entity/void_bullet/void_bullet.png")
    }
}