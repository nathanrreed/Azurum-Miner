package com.nred.azurum_miner.machine.liquifier

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import com.nred.azurum_miner.screen.GuiCommon.Companion.getBuckets
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.util.CustomFluidStackHandler
import com.nred.azurum_miner.util.Helpers
import io.netty.buffer.Unpooled
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.CommonColors
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
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
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.FluidUtil


class Liquifier(properties: Properties) : AbstractMachine(properties) {
    val LIQUIFIER_CODEC = RecordCodecBuilder.mapCodec<Liquifier>({ instance ->
        instance.group(propertiesCodec()).apply(instance, ::Liquifier)
    })

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return LIQUIFIER_CODEC
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is LiquifierEntity) {
                blockEntity.drops()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos)
            if (entity is LiquifierEntity) {
                val byteBuf = Unpooled.buffer().setLong(0, pos.asLong())
                (player as ServerPlayer).openMenu(state.getMenuProvider(level, pos)) { buf -> buf.writeBytes(byteBuf.array()) }
            } else {
                throw IllegalStateException("Missing Container Provider")
            }
        }

        return InteractionResult.SUCCESS
    }

    override fun useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hitResult: BlockHitResult): ItemInteractionResult {
        val cap = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, hitResult.direction)!!
        if (FluidUtil.interactWithFluidHandler(player, hand, cap)) {
            return ItemInteractionResult.SUCCESS
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            val fluids = CustomFluidStackHandler.listFromNBT(context.registries()!!, tag.getCompound("fluids"))
            val energyHandler = EnergyStorage(CONFIG.getInt("liquifier.energyCapacity"))
            if (tag.contains("energy"))
                energyHandler.deserializeNBT(context.registries()!!, tag.get("energy")!!)

            tooltipComponents.addAll(Helpers.itemComponentSplitColorized("tooltip.azurum_miner.liquifier.extended", intArrayOf(CommonColors.SOFT_RED, CommonColors.LIGHT_GRAY), getFE(energyHandler.energyStored), getFE(energyHandler.maxEnergyStored), if (fluids.isEmpty() || fluids[0].fluid.fluidType.isAir) Component.translatable("fluid_type.azurum_miner.empty").string else fluids[0].fluid.fluidType.description, getBuckets(if (fluids.isEmpty()) 0 else fluids[0].amount)))
            Helpers.addItemsTooltip(context, tooltipComponents, tag)
        } else {
            tooltipComponents.addAll(Helpers.itemComponentSplit("tooltip.azurum_miner.liquifier"))
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return createTickerHelper(blockEntityType, ModBlockEntities.LIQUIFIER_ENTITY.get()) { level1, pos, state1, blockEntity -> blockEntity.tick(level1, pos, state1, blockEntity) }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return LiquifierEntity(pos, state)
    }
}