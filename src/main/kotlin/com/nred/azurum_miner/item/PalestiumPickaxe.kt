package com.nred.azurum_miner.item

import com.nred.azurum_miner.AzurumMiner.CONFIG
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.Tier
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class PalestiumPickaxe(tier: Tier, properties: Properties) : PickaxeItem(tier, properties) {
    override fun mineBlock(stack: ItemStack, level: Level, state: BlockState, pos: BlockPos, miningEntity: LivingEntity): Boolean {
        val rtn = super.mineBlock(stack, level, state, pos, miningEntity)
        if (rtn && stack.isCorrectToolForDrops(state) && level.random.nextInt(100) <= CONFIG.getInt("palestium_pickaxe.chanceToHasteOnMine")) {
            miningEntity.addEffect(MobEffectInstance(MobEffects.DIG_SPEED, CONFIG.getInt("palestium_pickaxe.hasteOnMine"), 0))
        }
        return rtn
    }
}