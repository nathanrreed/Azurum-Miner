package com.nred.azurum_miner.network;

import com.nred.azurum_miner.block_entity.IFluidBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import static com.nred.azurum_miner.registration.ItemRegistration.PIPETTE;
import static com.nred.azurum_miner.util.Helpers.azLoc;

public record FluidTankTransferPayload(BlockPos blockPos, FluidTransferAction fluidTransferAction, int fluidIndex) implements CustomPacketPayload {
    public static final Type<FluidTankTransferPayload> TYPE = new Type<>(azLoc("fluid_tank_transfer"));

    public static final StreamCodec<ByteBuf, FluidTankTransferPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC), FluidTankTransferPayload::blockPos,
            ByteBufCodecs.fromCodec(FluidTransferAction.CODEC), FluidTankTransferPayload::fluidTransferAction,
            ByteBufCodecs.INT, FluidTankTransferPayload::fluidIndex,
            FluidTankTransferPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(final FluidTankTransferPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(data.blockPos) instanceof IFluidBlockEntity fluidBlockEntity) {
                ItemAccess itemAccess = ItemAccess.forPlayerCursor(context.player(), context.player().containerMenu);
                ResourceHandler<FluidResource> itemHandler = itemAccess.getCapability(Capabilities.Fluid.ITEM);

                RangedResourceHandler<FluidResource> fluidHandler;
                if (itemAccess.getResource().is(PIPETTE.asItem())) {
                    fluidHandler = RangedResourceHandler.ofSingleIndex(fluidBlockEntity.getInternalFluidHandler(), data.fluidIndex);
                } else {
                    fluidHandler = RangedResourceHandler.ofSingleIndex(fluidBlockEntity.getFluidHandler(), data.fluidIndex);
                }

                switch (data.fluidTransferAction) {
                    case FILL -> {
                        FluidResource fluidResource = fluidHandler.getResource(0);
                        if (ResourceHandlerUtil.moveStacking(fluidHandler, itemHandler, _ -> true, fluidHandler.getAmountAsInt(0), null) > 0) {
                            FluidUtil.triggerSoundAndGameEvent(fluidResource, context.player().level(), context.player().position(), context.player(), true);
                        }
                    }
                    case EMPTY -> {
                        FluidResource fluidResource = itemHandler.getResource(0);
                        if (ResourceHandlerUtil.moveStacking(itemHandler, fluidHandler, _ -> true, itemHandler.getAmountAsInt(0), null) > 0) {
                            FluidUtil.triggerSoundAndGameEvent(fluidResource, context.player().level(), context.player().position(), context.player(), false);
                        }
                    }
                    case VOID -> {
                        try (Transaction transaction = Transaction.open(null)) {
                            FluidResource fluidResource = fluidHandler.getResource(0);
                            if (fluidHandler.extract(0, fluidResource, fluidHandler.getAmountAsInt(0), transaction) > 0) {
                                transaction.commit();
                                FluidUtil.triggerSoundAndGameEvent(fluidResource, context.player().level(), context.player().position(), context.player(), true);
                            }
                        }
                    }
                }
            }
        });
    }

    public enum FluidTransferAction implements StringRepresentable {
        FILL, EMPTY, VOID;

        public static final StringRepresentable.EnumCodec<FluidTransferAction> CODEC = StringRepresentable.fromEnum(FluidTransferAction::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}