package com.nred.azurum_miner.machine

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.screen.GuiCommon.Companion.getBuckets
import com.nred.azurum_miner.screen.GuiCommon.Companion.getFE
import com.nred.azurum_miner.util.CustomFluidStackHandler
import com.nred.azurum_miner.util.Helpers
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.neoforged.neoforge.energy.EnergyStorage

abstract class AbstractMachine(properties: Properties) : BaseEntityBlock(properties) {
    abstract val typeName: String

    companion object {
        val MACHINE_ON: BooleanProperty = BooleanProperty.create("azurum_miner_machine_on")
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    }

    init {
        this.registerDefaultState(stateDefinition.any().setValue(MACHINE_ON, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(MACHINE_ON, FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(FACING, context.horizontalDirection.opposite).setValue(
            MACHINE_ON, false
        )
    }

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, TooltipFlag.NORMAL)

        tooltipComponents[0] = tooltipComponents[0].copy().withStyle(ChatFormatting.DARK_AQUA)
        if (typeName != "miner") {
            if (Screen.hasShiftDown()) {
                val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
                val fluids = CustomFluidStackHandler.listFromNBT(context.registries()!!, tag.getCompound("fluids"))
                val energyHandler = EnergyStorage(CONFIG.getInt("$typeName.energyCapacity"))
                if (tag.contains("energy"))
                    energyHandler.deserializeNBT(context.registries()!!, tag.get("energy")!!)

                tooltipComponents.addAll(Helpers.itemComponentSplitColorized("tooltip.azurum_miner.$typeName.extended", intArrayOf(CommonColors.SOFT_RED, CommonColors.LIGHT_GRAY), getFE(energyHandler.energyStored), getFE(energyHandler.maxEnergyStored), if (fluids.isEmpty() || fluids[0].fluid.fluidType.isAir) Component.translatable("fluid_type.azurum_miner.empty").string else fluids[0].fluid.fluidType.description, getBuckets(if (fluids.isEmpty()) 0 else fluids[0].amount)))
                Helpers.addItemsTooltip(context, tooltipComponents, tag)
            } else {
                tooltipComponents.addAll(Helpers.itemComponentSplit("tooltip.azurum_miner.$typeName"))
            }
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}