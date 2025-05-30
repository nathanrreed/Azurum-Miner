package com.nred.azurum_miner.machine.miner

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.ADDED_MODIFIER_POINTS
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerEnum.USED_MODIFIER_POINTS
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.MinerVariablesEnum.TOTAL_MODIFIER_POINTS
import com.nred.azurum_miner.machine.miner.MinerEntity.Companion.getMinerConfig
import com.nred.azurum_miner.screen.GuiCommon.Companion.getBuckets
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.util.CustomFluidStackHandler
import com.nred.azurum_miner.util.Helpers
import io.netty.buffer.Unpooled
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.CommonColors
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.energy.EnergyStorage


class Miner(val tier: Int, properties: Properties) : AbstractMachine(properties) {
    override val typeName: String = "miner"

    val MINER_CODEC = RecordCodecBuilder.mapCodec<Miner> { instance ->
        instance.group(
            Codec.INT.fieldOf("tier").forGetter({ miner -> miner.tier }), propertiesCodec()
        ).apply(instance, ::Miner)
    }

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return MINER_CODEC
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is MinerEntity) {
                blockEntity.drops()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos)
            if (entity is MinerEntity) {
                val byteBuf = Unpooled.buffer().setLong(0, pos.asLong()).setInt(Long.SIZE_BYTES, tier)

                (player as ServerPlayer).openMenu(state.getMenuProvider(level, pos)) { buf -> buf.writeBytes(byteBuf.array()) }
            } else {
                throw IllegalStateException("Missing Container Provider")
            }
        }

        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            val vars = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag())).copyTag().getIntArray("vars")
            val numPoints = vars.getOrElse(TOTAL_MODIFIER_POINTS) { _ -> 0 }.coerceAtLeast(getMinerConfig("numModifierPoints", this.tier) + vars.getOrElse(ADDED_MODIFIER_POINTS) { _ -> 0 })
            val numUsed = vars.getOrElse(USED_MODIFIER_POINTS) { _ -> 0 }
            val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            val fluids = CustomFluidStackHandler.listFromNBT(context.registries()!!, tag.getCompound("fluids"))
            val energyHandler = EnergyStorage(getMinerConfig("energyCapacity", this.tier))
            if (tag.contains("energy"))
                energyHandler.deserializeNBT(context.registries()!!, tag.get("energy")!!)

            tooltipComponents.addAll(Helpers.itemComponentSplitColorized("tooltip.azurum_miner.miner.extended", intArrayOf(0xFFa66fbc.toInt(), CommonColors.SOFT_YELLOW, CommonColors.SOFT_RED, CommonColors.LIGHT_GRAY), numPoints, numUsed, getFE(energyHandler.energyStored), getFE(energyHandler.maxEnergyStored), getBuckets(if (fluids.isEmpty()) 0 else fluids.get(0).amount)))
        } else {
            tooltipComponents.addAll(Helpers.itemComponentSplit("tooltip.azurum_miner.miner", this.tier + 1))
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return createTickerHelper(blockEntityType, ModBlockEntities.MINER_ENTITY_TIERS[tier].get()) { level1, pos, state1, blockEntity -> blockEntity.tick(level1, pos, state1, blockEntity) }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return MinerEntity(pos, state, this.tier)
    }
}

private fun IntArray.getOrElse(e: Enum<*>, function: (Int) -> Int): Int {
    var idx = 0
    if (e is MinerVariablesEnum) {
        idx = e.ordinal
    } else if (e is MinerEnum) {
        idx = e.ordinal + MinerVariablesEnum.entries.size
    }
    return this.getOrElse(idx, function)
}