package com.nred.azurum_miner.machine.generator

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
import net.neoforged.neoforge.items.ItemStackHandler


class Generator(properties: Properties) : AbstractMachine(properties) {
    val GENERATOR_CODEC = RecordCodecBuilder.mapCodec<Generator>({ instance ->
        instance.group(propertiesCodec()).apply(instance, ::Generator)
    })

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return GENERATOR_CODEC
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is GeneratorEntity) {
                blockEntity.drops()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos)
            if (entity is GeneratorEntity) {
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
            val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag())).copyTag()
            val vars = tag.getIntArray("vars")
            val energy = vars.getOrElse(GeneratorEntity.Companion.GeneratorEnum.ENERGY_LEVEL.ordinal) { _ -> 0 }
            val energyCap = vars.getOrElse(GeneratorEntity.Companion.GeneratorEnum.ENERGY_CAPACITY.ordinal) { _ -> CONFIG.getInt("generator.energyCapacity") }
            val list = ArrayList(Helpers.itemComponentSplitColorized("tooltip.azurum_miner.generator.extended", intArrayOf(CommonColors.SOFT_RED), getFE(energy), getFE(energyCap)))
            val itemHandler = ItemStackHandler()
            itemHandler.deserializeNBT(context.registries()!!, tag.getCompound("inventory"))
            for (i in 0..itemHandler.slots - 1) {
                if (!itemHandler.getStackInSlot(i).isEmpty) {
                    list.add(itemHandler.getStackInSlot(i).getHoverName().copy().append(" x " + itemHandler.getStackInSlot(i).count).withColor(CommonColors.SOFT_YELLOW))
                }
            }
            tooltipComponents.addAll(list)
        } else {
            tooltipComponents.addAll(Helpers.itemComponentSplit("tooltip.azurum_miner.generator"))
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return createTickerHelper(blockEntityType, ModBlockEntities.GENERATOR_ENTITY.get(), { level1, pos, state1, blockEntity -> blockEntity.tick(level1, pos, state1, blockEntity) })
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return GeneratorEntity(pos, state)
    }
}