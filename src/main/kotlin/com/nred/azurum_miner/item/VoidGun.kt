package com.nred.azurum_miner.item

import com.nred.azurum_miner.AzurumMiner.CONFIG
import com.nred.azurum_miner.item.ModItems.VOID_BULLET
import com.nred.azurum_miner.util.CustomEnergyHandler
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.event.EventHooks
import java.util.function.Predicate

class VoidGun() : ProjectileWeaponItem(Properties().stacksTo(1)) {
    @Deprecated("Deprecated in Java")
    override fun getAllSupportedProjectiles(): Predicate<ItemStack> {
        return Predicate { stack -> stack.`is`(VOID_BULLET) }
    }

    override fun getDefaultProjectileRange(): Int {
        return 128
    }

    override fun shootProjectile(shooter: LivingEntity, projectile: Projectile, index: Int, velocity: Float, inaccuracy: Float, angle: Float, target: LivingEntity?) {
        projectile.shootFromRotation(shooter, shooter.xRot, shooter.yRot + angle, 0.0f, velocity, inaccuracy)
    }

    override fun createProjectile(level: Level, shooter: LivingEntity, weapon: ItemStack, ammo: ItemStack, isCrit: Boolean): Projectile {
        return VoidBullet(shooter, level)
    }

    override fun releaseUsing(stack: ItemStack, level: Level, entityLiving: LivingEntity, timeLeft: Int) {
        if (entityLiving is Player) {
            val itemStack = entityLiving.getProjectile(stack)
            val energyHandler: CustomEnergyHandler = stack.getCapability(Capabilities.EnergyStorage.ITEM) as CustomEnergyHandler
            val energyPerShot = CONFIG.getInt("void_gun.energyPerShot")
            val energyRequired: Boolean = CONFIG.get("void_gun.energyRequired")

            if (!itemStack.isEmpty && (!energyRequired || energyHandler.internalExtractEnergy(energyPerShot, true) == energyPerShot)) {
                if (energyRequired)
                    energyHandler.internalExtractEnergy(energyPerShot, false)

                var i = this.getUseDuration(stack, entityLiving) - timeLeft
                i = EventHooks.onArrowLoose(stack, level, entityLiving, i, !itemStack.isEmpty)
                if (i < 0) return
                val list = draw(stack, itemStack, entityLiving)
                if (level is ServerLevel && !list.isEmpty()) {
                    this.shoot(level, entityLiving, entityLiving.usedItemHand, stack, list, 6.0f, 0.0f, false, null)
                }
                entityLiving.cooldowns.addCooldown(this, 80)

                level.playSound(
                    null,
                    entityLiving.x,
                    entityLiving.y,
                    entityLiving.z,
                    SoundEvents.END_PORTAL_SPAWN,
                    SoundSource.PLAYERS,
                    0.5f,
                    0.8f + (level.getRandom().nextFloat() * 0.2f - 0.1f)
                )
                entityLiving.awardStat(Stats.ITEM_USED.get(this))
            }
        }
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.NONE
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 72000
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val itemStack = player.getItemInHand(hand)
        val flag = !player.getProjectile(itemStack).isEmpty

        val ret = EventHooks.onArrowNock(itemStack, level, player, hand, flag)
        if (ret != null) return ret

        if (!player.hasInfiniteMaterials() && !flag) {
            return InteractionResultHolder.fail<ItemStack?>(itemStack)
        } else {
            player.startUsingItem(hand)
            return InteractionResultHolder.consume<ItemStack?>(itemStack)
        }
    }

    override fun getName(stack: ItemStack): Component {
        return super.getName(stack).copy().withStyle(ChatFormatting.AQUA)
    }

    override fun isBarVisible(stack: ItemStack): Boolean {
        return CONFIG.get("void_gun.energyRequired")
    }

    override fun getBarWidth(stack: ItemStack): Int {
        val energyHandler = stack.getCapability(Capabilities.EnergyStorage.ITEM)
        if (energyHandler == null) return 0
        return Mth.ceil((energyHandler.energyStored / energyHandler.maxEnergyStored.toDouble()) * 13)
    }

    override fun isRepairable(stack: ItemStack): Boolean {
        return false
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun getBarColor(pStack: ItemStack): Int {
        return Mth.hsvToRgb(0.0f.coerceAtLeast(getBarWidth(pStack) / MAX_BAR_WIDTH.toFloat()) / 3.0f, 1.0f, 1.0f)
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        if (CONFIG.get("void_gun.energyRequired"))
            tooltipComponents.add(CustomEnergyHandler.getTooltip(stack))
    }
}