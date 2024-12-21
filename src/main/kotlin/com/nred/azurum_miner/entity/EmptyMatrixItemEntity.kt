package com.nred.azurum_miner.entity

import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.item.ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.Portal
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import kotlin.random.Random

class EmptyMatrixItemEntity : ItemEntity {
    var fill = 0
    var dest: Vec3? = null

    constructor(entityType: EntityType<EmptyMatrixItemEntity>, level: Level) : super(entityType, level)
    constructor(tag: CompoundTag, level: Level) : this(EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), level) {
        this.load(tag)
        this.setPickUpDelay(60)
        this.setUnlimitedLifetime()
    }

    override fun getType(): EntityType<*> {
        return EMPTY_DIMENSIONAL_MATRIX_TYPE.get()
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)

        compound.putInt("matrixFill", fill)
        compound.put("matrixDest", this.newDoubleList(dest!!.x, dest!!.y, dest!!.z))
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        fill = compound.getInt("matrixFill")
        val list = compound.getList("matrixDest", 6)
        if (list.isNotEmpty())
            dest = Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2))
    }

    override fun tick() {
        if (this.dest != null) {
            if (this.fill < 4) {
                this.lerpPositionAndRotationStep(4, this.dest!!.x, this.dest!!.y, this.dest!!.z, this.rotationVector.y.toDouble(), this.rotationVector.x.toDouble())
                this.hasImpulse = true
            }

            if (this.fill % 5 == 0) {
                (level() as ServerLevel).sendParticles(ParticleTypes.DRAGON_BREATH, this.x, this.y, this.z, 3, 0.0, 0.4, 0.0, 0.02)
            }
            if (this.fill % 30 == 0 && Random.nextBoolean()) {
                this.playSound(BOTTLE_FILL, Random.nextFloat() * 0.6f + 0.4f, Random.nextFloat() * 0.5f + 0.5f)
            }


            this.fill += 1
            if (this.fill > 2400) {
                (level() as ServerLevel).sendParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.0, 0.4, 0.0, 0.05)
                this.playSound(DRAGON_FIREBALL_EXPLODE)
                this.playSound(PLAYER_LEVELUP)
                level().destroyBlock(BlockPos(dest!!.toVec3i()), false)
                this.spawnSprintParticle()
                this.item = ItemStack(ModItems.DIMENSIONAL_MATRIX.asItem(), item.count)
                this.isNoGravity = false
                this.setDefaultPickUpDelay()
                this.dest = null
            }
        }

        super.tick()
    }

    override fun setAsInsidePortal(portal: Portal, pos: BlockPos) {
        if (level().isClientSide || fill != 0 || portal !is NetherPortalBlock) return
        val bounds = this.makeBoundingBox().move(this.x, this.y, pos.center.z)

        dest = if (level().getBlockState(pos).getValue(portal.stateDefinition.getProperty("axis")) == "z")
            Vec3(AABB.ofSize(pos.center, bounds.xsize, bounds.ysize, bounds.zsize).center.x(), this.y, this.z)
        else
            Vec3(this.x, this.y, AABB.ofSize(pos.center, bounds.xsize, bounds.ysize, bounds.zsize).center.z)

        this.isNoGravity = true
        this.deltaMovement = Vec3.ZERO
    }
}