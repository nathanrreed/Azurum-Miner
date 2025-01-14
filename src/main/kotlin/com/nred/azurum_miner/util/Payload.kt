package com.nred.azurum_miner.util

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.machine.generator.BASE_SLOT_SAVE
import com.nred.azurum_miner.machine.generator.FUEL_SLOT_SAVE
import com.nred.azurum_miner.machine.generator.GeneratorEntity
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.GeneratorEnum.HAS_BASE
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.GeneratorEnum.HAS_FUEL
import com.nred.azurum_miner.machine.generator.GeneratorEntity.Companion.set
import com.nred.azurum_miner.machine.generator.GeneratorMenu
import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.MinerMenu
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.network.handling.IPayloadContext


class Payload(val index: Int, val value: Int, val name: String, val from: String, val pos: BlockPos) : CustomPacketPayload {
    constructor(enumIndex: Enum<*>, value: Int, name: String, from: String, pos: BlockPos) : this(enumIndex.ordinal, value, name, from, pos)

    companion object {
        val TYPE = CustomPacketPayload.Type<Payload>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "container_data_client_to_server"))
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

                "transmogrifier" -> when (data.name) {
                    "ENUM" -> (context.player().level().getBlockEntity(data.pos) as TransmogrifierEntity).updateEnumData(data.index, data.value)
                    else -> throw UnsupportedOperationException("Unknown packet type: ${data.name}")
                }
            }
        }
    }
}

class MinerFilterPayloadToServer(val idx: Int, val string: String, val pos: BlockPos, val reply: Boolean = false) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<MinerFilterPayloadToServer>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_filter_handler_server"))
        val STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MinerFilterPayloadToServer::idx, ByteBufCodecs.STRING_UTF8, MinerFilterPayloadToServer::string, BlockPos.STREAM_CODEC, MinerFilterPayloadToServer::pos, ByteBufCodecs.BOOL, MinerFilterPayloadToServer::reply, ::MinerFilterPayloadToServer)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class MinerFilterPayloadToPlayer(val idx: Int, val string: String) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<MinerFilterPayloadToPlayer>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner_filter_handler_player"))
        val STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MinerFilterPayloadToPlayer::idx, ByteBufCodecs.STRING_UTF8, MinerFilterPayloadToPlayer::string, ::MinerFilterPayloadToPlayer)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class MinerFilterPayloadHandler {
    companion object {
        fun handleDataOnServer(data: MinerFilterPayloadToServer, context: IPayloadContext) {
            if (data.reply) {
                context.reply(MinerFilterPayloadToPlayer(data.idx, (context.player().level().getBlockEntity(data.pos) as MinerEntity).getFilterData(data.idx)))
            } else {
                (context.player().level().getBlockEntity(data.pos) as MinerEntity).updateFilterData(data.idx, data.string)
            }

            val menu = context.player().containerMenu
            if (menu is MinerMenu) {
                menu.filters[data.idx] = data.string
            }
        }

        fun handleDataOnPlayer(data: MinerFilterPayloadToPlayer, context: IPayloadContext) {
            val menu = context.player().containerMenu
            if (menu is MinerMenu) {
                menu.filters[data.idx] = data.string
            }
        }
    }
}

class FluidPayload(val fluid: FluidStack) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<FluidPayload>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "fluid_server_to_client"))
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
        }
    }
}

class FilterSetPayload(val item: ItemStack, val idx: Int) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<FilterSetPayload>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "filter_set_client_to_server"))
        val STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, FilterSetPayload::item, ByteBufCodecs.INT, FilterSetPayload::idx, ::FilterSetPayload)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class FilterSetPayloadHandler {
    companion object {
        fun handleDataOnServer(data: FilterSetPayload, context: IPayloadContext) {
            val menu = context.player().containerMenu
            if (menu is MinerMenu) {
                menu.filterSlots[data.idx].set(data.item)
            }
        }
    }
}

class ClearPayload(val idx: Int, val pos: BlockPos) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClearPayload>(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "clear_client_to_server"))
        val STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ClearPayload::idx, BlockPos.STREAM_CODEC, ClearPayload::pos, ::ClearPayload)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}

class ClearPayloadHandler {
    companion object {
        fun handleDataOnServer(data: ClearPayload, context: IPayloadContext) {
            val menu = context.player().containerMenu
            if (menu is GeneratorMenu) {
                when (data.idx) {
                    BASE_SLOT_SAVE -> {
                        val entity = context.player().level().getBlockEntity(data.pos)
                        if (entity is GeneratorEntity) {
                            entity.currBaseRecipe = null
                            (menu.itemHandler as ItemStackHandler).setStackInSlot(BASE_SLOT_SAVE, ItemStack.EMPTY)
                            entity.data[HAS_BASE] = FALSE
                        }
                    }

                    FUEL_SLOT_SAVE -> {
                        val entity = context.player().level().getBlockEntity(data.pos)
                        if (entity is GeneratorEntity) {
                            entity.currFuelRecipe = null
                            (menu.itemHandler as ItemStackHandler).setStackInSlot(FUEL_SLOT_SAVE, ItemStack.EMPTY)
                            entity.data[HAS_FUEL] = FALSE
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}