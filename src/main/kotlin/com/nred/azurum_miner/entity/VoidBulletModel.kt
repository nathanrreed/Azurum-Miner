package com.nred.azurum_miner.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.nred.azurum_miner.item.VoidBullet
import com.nred.azurum_miner.util.Helpers.azLoc
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*

class VoidBulletModel(root: ModelPart) : EntityModel<VoidBullet>() {
    private val bbMain: ModelPart = root.getChild("bb_main")

    override fun renderToBuffer(poseStack: PoseStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, color: Int) {
        bbMain.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    override fun setupAnim(entity: VoidBullet, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(azLoc("void_bullet"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition: PartDefinition = meshdefinition.root

            val bbMain: PartDefinition = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 24.0f, 0.0f))
            bbMain.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-2.0f, -2.0f, 0.0f, 0.0f, 0.0f, 1.5708f))
            bbMain.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, -2.0f, -2.0f, -1.5708f, 0.0f, 0.0f))

            return LayerDefinition.create(meshdefinition, 16, 16)
        }
    }
}