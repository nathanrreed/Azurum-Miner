package com.nred.azurum_miner.machine.generator

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.AbstractMachine
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.SimpleBakedModel
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.client.NeoForgeRenderTypes
import net.neoforged.neoforge.client.RenderTypeGroup
import net.neoforged.neoforge.client.model.data.ModelData
import kotlin.random.Random

@OnlyIn(Dist.CLIENT)
class GeneratorRenderer : BlockEntityRenderer<GeneratorEntity> {
    val context: BlockEntityRendererProvider.Context
    var rads = 0f

    constructor(context: BlockEntityRendererProvider.Context) {
        this.context = context
    }

    companion object {
        fun remakeModel(ir: ItemRenderer, stack: ItemStack, rnd: RandomSource, renderType: RenderType, hasCulled: Boolean = false): BakedModel {
            val model = ir.getModel(stack, null, null, 0)
            val newModel = SimpleBakedModel.Builder(model.useAmbientOcclusion(), model.usesBlockLight(), model.isGui3d, ItemTransforms.NO_TRANSFORMS, model.overrides).particle(model.getParticleIcon(ModelData.EMPTY))
            model.getQuads(null, null, rnd, ModelData.EMPTY, null).forEach { newModel.addUnculledFace(it) }
            if (hasCulled) {
                for (dir in Direction.entries) {
                    val quad = model.getQuads(null, dir, rnd, ModelData.EMPTY, null).getOrNull(0)
                    if (quad != null) {
                        newModel.addUnculledFace(quad)
                    }
                }
            }
            return newModel.build(RenderTypeGroup(renderType, renderType, renderType))
        }
    }

    override fun render(blockEntity: GeneratorEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        val ir = context.itemRenderer
        val rnd = RandomSource.create()

        var stack = ItemStack(ModItems.DIMENSIONAL_MATRIX.get(), 1)

        val hasMatrix = !blockEntity.itemStackHandler.getStackInSlot(MATRIX_SLOT).isEmpty
        val hasBase = !blockEntity.itemStackHandler.getStackInSlot(BASE_SLOT_SAVE).isEmpty || !blockEntity.itemStackHandler.getStackInSlot(BASE_SLOT).isEmpty

        if (hasMatrix) {
            poseStack.pushPose() // DIMENSIONAL_MATRIX
            poseStack.translate(0.5, 0.6, 0.5)
            poseStack.rotateAround(Axis.YP.rotation(rads), 0F, 0.0F, 0.0F)
            rads = if (rads >= Math.TAU) 0f else rads + 0.01f
            poseStack.scale(0.4f, 0.4f, 0.4f)
            ir.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, 255, packedOverlay, remakeModel(ir, stack, rnd, RenderType.CUTOUT))
            poseStack.popPose()
        }

        if (hasBase) {
            poseStack.pushPose()
            stack = if (!blockEntity.itemStackHandler.getStackInSlot(BASE_SLOT_SAVE).isEmpty) blockEntity.itemStackHandler.getStackInSlot(BASE_SLOT_SAVE).copy() else blockEntity.itemStackHandler.getStackInSlot(BASE_SLOT).copy()
            poseStack.translate(0.5, 0.28, 0.5)
            poseStack.scale(0.3f, 0.3f, 0.3f)
            ir.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, 255, packedOverlay, remakeModel(ir, stack, rnd, RenderType.SOLID, true))
            poseStack.popPose()
        }

        if (hasBase && hasMatrix) {
            val portal = Blocks.NETHER_PORTAL.defaultBlockState()
            val model = Minecraft.getInstance().modelManager.blockModelShaper.getBlockModel(portal)
            val quads = model.getQuads(null, null, rnd, ModelData.EMPTY, null)
            val face = quads[0]
            val newFace = BakedQuad(face.vertices, 0, face.direction, face.sprite, face.isShade, face.hasAmbientOcclusion())

            for (dir in listOf(0f, 90f, 180f, 270f)) {
                poseStack.pushPose()
                poseStack.rotateAround(Axis.YP.rotationDegrees(dir), 0.5F, 0.0F, 0.5F)
                poseStack.translate(0.0, 0.0, -0.35)
                bufferSource.getBuffer(NeoForgeRenderTypes.TRANSLUCENT_ON_PARTICLES_TARGET.get()).putBulkData(poseStack.last(), newFace, 1f, 1f, 1f, 0.5f, 255, packedOverlay)
                poseStack.popPose()
            }

            poseStack.pushPose()
            poseStack.rotateAround(Axis.XP.rotationDegrees(90f), 0.5F, 0.0F, 0.5F)
            poseStack.translate(0.0, -0.5, -0.85)
            bufferSource.getBuffer(RenderType.TRANSLUCENT).putBulkData(poseStack.last(), newFace, 1f, 1f, 1f, 0.5f, 255, packedOverlay)
            poseStack.popPose()

            if (Random.nextDouble() > 0.95 && blockEntity.blockState.getValue(AbstractMachine.MACHINE_ON) == true)
                Minecraft.getInstance().level?.addParticle(ParticleTypes.PORTAL, blockEntity.blockPos.center.x + Random.nextDouble(-0.6, 0.6), blockEntity.blockPos.bottomCenter.y, blockEntity.blockPos.center.z + Random.nextDouble(-0.6, 0.6), 0.0, 0.0, 0.0)
        }
    }
}