package com.nred.azurum_miner.machine

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty


abstract class AbstractMachine(properties: Properties) : BaseEntityBlock(properties) {
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
//    val FULL_BLOCK = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)

    //    abstract fun openContainer(level: Level, pos: BlockPos?, player: Player)
//    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
//        return FULL_BLOCK
//    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}