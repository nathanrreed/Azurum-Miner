package com.nred.azurum_miner.render.block_entity;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nred.azurum_miner.block_entity.TankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.nred.azurum_miner.registration.BlockRegistration.TANK_BLOCK;
import static com.nred.azurum_miner.render.block_entity.TankBlockEntityRenderer.TankBlockEntityRenderState;

public class TankBlockEntityRenderer implements BlockEntityRenderer<TankBlockEntity, TankBlockEntityRenderState> {
    private List<BlockStateModelPart> fluidModelParts = null;
    private TankBlockEntityRenderState previous_state;
    private SimpleFluidContent previous_fluid_content;
    private static final ArrayList<BlockStateModelPart> modelItemParts = new ArrayList<>(1);

    public TankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public TankBlockEntityRenderer(SpecialModelRenderer.BakingContext context) {
    }

    @Override
    public TankBlockEntityRenderState createRenderState() {
        return new TankBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(TankBlockEntity blockEntity, TankBlockEntityRenderState renderState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPosition, breakProgress);

        renderState.fluidStack = blockEntity.getInternalFluidHandler().getResource(0);
        renderState.capacity = blockEntity.getInternalFluidHandler().getCapacityAsLong(0, renderState.fluidStack);
        renderState.amount = blockEntity.getInternalFluidHandler().getAmountAsLong(0);
    }

    @Override
    public void submit(TankBlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.amount == 0) return;

        if (fluidModelParts == null || !previous_state.equals(renderState)) {
            createModelParts(renderState.fluidStack.getFluid(), renderState.amount, renderState.capacity);
            previous_state = renderState;
        }
        submitNodeCollector.submitBlockModel(poseStack, RenderTypes.translucentMovingBlock(), fluidModelParts, BlockModelRenderState.EMPTY_TINTS, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

    public void createModelParts(Fluid fluid, long amount, long capacity) {
        Minecraft minecraft = Minecraft.getInstance();
        FluidState fluidState = fluid.defaultFluidState();
        ModelManager modelManager = minecraft.getModelManager();
        FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        FluidModel fluidModel = fluidStateModelSet.get(fluidState);
        FluidTintSource tintSource = fluidModel.fluidTintSource();
        int colour = tintSource == null ? -1 : tintSource.color(fluidState);
        float fluidHeight = Mth.lerp(((float) amount / (float) capacity), 0.13f, (14 / 16f));

        List<SimpleModelWrapper> model_sides = getModelSides(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST), new Vector3f(3 / 16f, 2 / 16f, 3 / 16f), new Vector3f(13 / 16f, fluidHeight, 13 / 16f), fluidModel.stillMaterial(), colour);
        List<SimpleModelWrapper> model_top = getModelSides(List.of(Direction.UP), new Vector3f(3 / 16f, fluidHeight, 3 / 16f), new Vector3f(13 / 16f, fluidHeight, 13 / 16f), fluidModel.stillMaterial(), colour);
        fluidModelParts = Arrays.stream(Stream.concat(model_sides.parallelStream(), model_top.parallelStream()).toArray(BlockStateModelPart[]::new)).toList();
    }

    public void submitSpecial(SimpleFluidContent fluidContent, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords) {
        if (modelItemParts.isEmpty()) {
            BlockStateModel blockStateModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(TANK_BLOCK.get().defaultBlockState());
            if (blockStateModel instanceof SingleVariant singleVariant) {
                singleVariant.collectParts(RandomSource.create(), modelItemParts);
            }
        }

        submitNodeCollector.submitBlockModel(poseStack, Sheets.cutoutBlockSheet(), modelItemParts, BlockModelRenderState.EMPTY_TINTS, lightCoords, overlayCoords, 0);

        if (fluidContent == null || fluidContent.getAmount() == 0) return;

        if (fluidModelParts == null || !previous_fluid_content.equals(fluidContent)) {
            createModelParts(fluidContent.getFluid(), fluidContent.getAmount(), TankBlockEntity.CAPACITY);
            previous_fluid_content = fluidContent;
        }
        submitNodeCollector.submitBlockModel(poseStack, RenderTypes.translucentMovingBlock(), fluidModelParts, BlockModelRenderState.EMPTY_TINTS, lightCoords, overlayCoords, 0);
    }

    public static class TankBlockEntityRenderState extends BlockEntityRenderState {
        public long capacity;
        public long amount;
        public FluidResource fluidStack;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TankBlockEntityRenderState other && other.capacity == this.capacity && other.amount == this.amount && other.fluidStack.equals(this.fluidStack) && other.lightCoords == this.lightCoords;
        }
    }

    public List<SimpleModelWrapper> getModelSides(List<Direction> directions, Vector3fc from, Vector3fc to, Material.Baked sprite, int colour) {
        return directions.stream().map(direction -> {
            MutableQuad quad = new MutableQuad().setCubeFace(direction, from, to);
            quad.setSprite(sprite, Transparency.TRANSLUCENT);
            quad.bakeUvsFromPosition();
            quad.setColor(colour);
            return new SimpleModelWrapper(new QuadCollection.Builder().addCulledFace(direction, quad.toBakedQuad()).build(), false, sprite);
        }).toList();
    }
}