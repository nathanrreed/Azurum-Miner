package com.nred.azurum_miner.item

import com.nred.azurum_miner.entity.ModEntities.VOID_BULLET
import com.nred.azurum_miner.item.ModItems.VOID_GUN
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class VoidBullet : AbstractArrow {
    constructor(entityType: EntityType<VoidBullet>, level: Level) : super(entityType, level)
    constructor(shooter: LivingEntity, level: Level) : super(VOID_BULLET.get(), shooter, level, ModItems.VOID_BULLET.toStack(), VOID_GUN.toStack())
    constructor(x: Double, y: Double, z: Double, level: Level) : super(VOID_BULLET.get(), x, y, z, level, ModItems.VOID_BULLET.toStack(), VOID_GUN.toStack())

    override fun getDefaultPickupItem(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun tryPickup(player: Player): Boolean {
        return false
    }

    override fun onHitEntity(result: EntityHitResult) {
        val damageSource = this.damageSources().arrow(this, (this.owner ?: this))
        result.entity.hurt(damageSource, 8f)
    }

    var roundsLeft = 27
    override fun tickDespawn() {
        super.tickDespawn()

        if (roundsLeft % 9 == 0) {
            (level() as ServerLevel).sendParticles(ParticleTypes.DRAGON_BREATH, this.x, this.y, this.z, 30, 0.5, 0.5, 0.5, 0.02)

            val damageSource = this.damageSources().arrow(this, (this.owner ?: this))
            for (entity in level().getEntitiesOfClass(LivingEntity::class.java, this.boundingBox.inflate(3.0))) {
                entity.addDeltaMovement(Vec3(x - entity.x, y - entity.y, z - entity.z).normalize().scale(entity.position().distanceTo(this.position()) / 8))
                entity.hurt(damageSource, 10f - entity.position().distanceTo(this.position()).toFloat())
            }
        }

        if (roundsLeft == 0) {
            this.discard()
            return
        }
        roundsLeft--
    }

    override fun setSoundEvent(soundEvent: SoundEvent) {
    }

    override fun getDefaultHitGroundSoundEvent(): SoundEvent {
        return SoundEvents.ANVIL_FALL
    }
}