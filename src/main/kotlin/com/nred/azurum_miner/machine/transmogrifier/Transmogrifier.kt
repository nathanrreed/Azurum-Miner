package com.nred.azurum_miner.machine.transmogrifier

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.util.Helpers
import io.netty.buffer.Unpooled
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
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


class Transmogrifier(properties: Properties) : AbstractMachine(properties) {
    val TRANSMOGRIFIER_CODEC = RecordCodecBuilder.mapCodec<Transmogrifier>({ instance ->
        instance.group(propertiesCodec()).apply(instance, ::Transmogrifier)
    })

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return TRANSMOGRIFIER_CODEC
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is TransmogrifierEntity) {
                blockEntity.drops()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos)
            if (entity is TransmogrifierEntity) {
                val byteBuf = Unpooled.buffer().setLong(0, pos.asLong())
                (player as ServerPlayer).openMenu(state.getMenuProvider(level, pos)) { buf -> buf.writeBytes(byteBuf.array()) }
            } else {
                throw IllegalStateException("Missing Container Provider")
            }
        }

        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            val energyHandler = EnergyStorage(CONFIG.getInt("transmogrifier.energyCapacity"))
            if (tag.contains("energy"))
                energyHandler.deserializeNBT(context.registries()!!, tag.get("energy")!!)
            tooltipComponents.addAll(Helpers.itemComponentSplitColorized("tooltip.azurum_miner.transmogrifier.extended", intArrayOf(CommonColors.SOFT_RED), getFE(energyHandler.energyStored), getFE(energyHandler.maxEnergyStored)))
            Helpers.addItemsTooltip(context, tooltipComponents, tag)
        } else {
            tooltipComponents.addAll(Helpers.itemComponentSplit("tooltip.azurum_miner.transmogrifier"))
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun <T : BlockEntity?> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return createTickerHelper(blockEntityType, ModBlockEntities.TRANSMOGRIFIER_ENTITY.get(), { level1, pos, state1, blockEntity -> blockEntity.tick(level1, pos, state1, blockEntity) })
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TransmogrifierEntity(pos, state)
    }
}