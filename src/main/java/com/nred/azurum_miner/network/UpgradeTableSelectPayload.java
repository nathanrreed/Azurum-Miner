package com.nred.azurum_miner.network;

import com.nred.azurum_miner.block_entity.UpgradeTableBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.nred.azurum_miner.util.Helpers.azLoc;

public record UpgradeTableSelectPayload(BlockPos blockPos, SelectType selectType, ItemStack itemStack, FluidStack fluidStack) implements CustomPacketPayload {
    public UpgradeTableSelectPayload(BlockPos blockPos, SelectType selectType, ItemStack itemStack) {
        this(blockPos, selectType, itemStack, FluidStack.EMPTY);
    }

    public UpgradeTableSelectPayload(BlockPos blockPos, SelectType selectType, Item item) {
        this(blockPos, selectType, item.getDefaultInstance(), FluidStack.EMPTY);
    }

    public UpgradeTableSelectPayload(BlockPos blockPos, SelectType selectType, Fluid fluid) {
        this(blockPos, selectType, ItemStack.EMPTY, new FluidStack(fluid, 1));
    }

    public UpgradeTableSelectPayload(BlockPos blockPos, SelectType selectType) {
        this(blockPos, selectType, ItemStack.EMPTY, FluidStack.EMPTY);
    }

    public static final Type<UpgradeTableSelectPayload> TYPE = new Type<>(azLoc("upgrade_table_select"));

    public static final StreamCodec<ByteBuf, UpgradeTableSelectPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC), UpgradeTableSelectPayload::blockPos,
            ByteBufCodecs.fromCodec(SelectType.CODEC), UpgradeTableSelectPayload::selectType,
            ByteBufCodecs.fromCodec(ItemStack.OPTIONAL_CODEC), UpgradeTableSelectPayload::itemStack,
            ByteBufCodecs.fromCodec(FluidStack.OPTIONAL_CODEC), UpgradeTableSelectPayload::fluidStack,
            UpgradeTableSelectPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(final UpgradeTableSelectPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(data.blockPos) instanceof UpgradeTableBlockEntity upgradeTableEntity) {
                switch (data.selectType) {
                    case CARD -> upgradeTableEntity.card = data.itemStack;
                    case COOLANT -> upgradeTableEntity.coolant = data.fluidStack;
                    case MODIFIER -> {
                        upgradeTableEntity.modifiers.replace(data.itemStack.getItem(), data.itemStack.getCount() - 1);
                    }
                    case ASSEMBLE -> upgradeTableEntity.createOutput(context.player());
                }
                context.player().level().blockEntityChanged(data.blockPos);
            }
        });
    }

    public enum SelectType implements StringRepresentable {
        CARD, COOLANT, MODIFIER, ASSEMBLE;

        public static final EnumCodec<SelectType> CODEC = StringRepresentable.fromEnum(SelectType::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}