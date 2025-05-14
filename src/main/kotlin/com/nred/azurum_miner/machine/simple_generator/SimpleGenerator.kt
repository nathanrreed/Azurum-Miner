package com.nred.azurum_miner.machine.simple_generator

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.machine.AbstractMachine
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult


class SimpleGenerator(properties: Properties) : AbstractMachine(properties) {
    override val typeName: String = "simple_generator"

    val SIMPLE_GENERATOR_CODEC = RecordCodecBuilder.mapCodec<SimpleGenerator>({ instance ->
        instance.group(propertiesCodec()).apply(instance, ::SimpleGenerator)
    })

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return SIMPLE_GENERATOR_CODEC
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is SimpleGeneratorEntity) {
                blockEntity.drops()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos)
            if (entity is SimpleGeneratorEntity) {
                val byteBuf = Unpooled.buffer().setLong(0, pos.asLong())
                (player as ServerPlayer).openMenu(state.getMenuProvider(level, pos)) { buf -> buf.writeBytes(byteBuf.array()) }
            } else {
                throw IllegalStateException("Missing Container Provider")
            }
        }

        return InteractionResult.SUCCESS
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return createTickerHelper(blockEntityType, ModBlockEntities.SIMPLE_GENERATOR_ENTITY.get(), { level1, pos, state1, blockEntity -> blockEntity.tick(level1, pos, state1, blockEntity) })
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SimpleGeneratorEntity(pos, state)
    }
}