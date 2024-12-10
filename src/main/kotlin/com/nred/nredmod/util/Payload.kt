package com.nred.nredmod.util

import com.nred.nredmod.NredMod
import com.nred.nredmod.machine.infuser.InfuserEntity
import com.nred.nredmod.machine.infuser.InfuserScreen
import com.nred.nredmod.machine.liquifier.LiquifierEntity
import com.nred.nredmod.machine.liquifier.LiquifierScreen
import com.nred.nredmod.machine.miner.MinerEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.handling.IPayloadContext


class Payload(val index: Int, val value: Int, val name: String, val from: String, val pos: BlockPos) : CustomPacketPayload {
    constructor(enumIndex: Enum<*>, value: Int, name: String, from: String, pos: BlockPos) : this(enumIndex.ordinal, value, name, from, pos)

    companion object {
        val TYPE: CustomPacketPayload.Type<Payload> = CustomPacketPayload.Type<Payload>(ResourceLocation.fromNamespaceAndPath(NredMod.ID, "container_data_client_to_server"))
        val STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, Payload::index, ByteBufCodecs.INT, Payload::value, ByteBufCodecs.STRING_UTF8, Payload::name, ByteBufCodecs.STRING_UTF8, Payload::from, BlockPos.STREAM_CODEC, Payload::pos, ::Payload)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class ServerPayloadHandler {
    companion object {
        fun handleDataOnNetwork(data: Payload, context: IPayloadContext) {
            when (data.from) {
                "miner" -> when (data.name) {
                    "POINTS" -> (context.player().level().getBlockEntity(data.pos) as MinerEntity).updateModifierPoints(data.index, data.value)
                    "ENUM" -> (context.player().level().getBlockEntity(data.pos) as MinerEntity).updateEnumData(data.index, data.value)
                    "OTHERS" -> (context.player().level().getBlockEntity(data.pos) as MinerEntity).updateEnumOthersData(data.index, data.value)
                    else -> throw UnsupportedOperationException("Unknown packet type: ${data.name}")
                }

                "liquifier" -> when (data.name) {
                    "ENUM" -> (context.player().level().getBlockEntity(data.pos) as LiquifierEntity).updateEnumData(data.index, data.value)
                    else -> throw UnsupportedOperationException("Unknown packet type: ${data.name}")
                }

                "infuser" -> when (data.name) {
                    "ENUM" -> (context.player().level().getBlockEntity(data.pos) as InfuserEntity).updateEnumData(data.index, data.value)
                    else -> throw UnsupportedOperationException("Unknown packet type: ${data.name}")
                }
            }

            // Do something with the data, on the main thread
            context.enqueueWork {
            }
                .exceptionally { e: Throwable ->
                    // Handle exception
                    context.disconnect(Component.translatable("nred_mod.networking.failed", e.message))
                    null
                }
        }
    }
}


class FluidPayload(val fluid: FluidStack) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<FluidPayload> = CustomPacketPayload.Type<FluidPayload>(ResourceLocation.fromNamespaceAndPath(NredMod.ID, "fluid_server_to_client"))
        val STREAM_CODEC = StreamCodec.composite(FluidStack.OPTIONAL_STREAM_CODEC, FluidPayload::fluid, ::FluidPayload)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class FluidPayloadHandler {
    companion object {
        fun handleDataOnNetwork(data: FluidPayload, context: IPayloadContext) {
            val screen = Minecraft.getInstance().screen
            if (screen is LiquifierScreen) {
                screen.fluid = data.fluid
            } else if (screen is InfuserScreen) {
                screen.fluid = data.fluid
            }

            // Do something with the data, on the main thread
            context.enqueueWork {
            }
                .exceptionally { e: Throwable ->
                    // Handle exception
                    context.disconnect(Component.translatable("nred_mod.networking.failed", e.message))
                    null
                }
        }
    }
}