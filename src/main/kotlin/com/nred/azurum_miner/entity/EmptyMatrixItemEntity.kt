package com.nred.azurum_miner.entity

import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.item.ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.Portal
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.random.Random


// TODO kotlinforforge 3.9 functions not working!
fun Vec3.toVec3i(): Vec3i = Vec3i(Mth.floor(x), Mth.floor(y), Mth.floor(z))
fun BlockPos.toVec3(): Vec3 = Vec3.atLowerCornerOf(this)

class EmptyMatrixItemEntity : ItemEntity {
    var fill = 0
    var dest: Vec3? = null

    constructor(entityType: EntityType<EmptyMatrixItemEntity>, level: Level) : super(entityType, level)
    constructor(tag: CompoundTag, level: Level) : this(tag, level, 1)
    constructor(tag: CompoundTag, level: Level, count: Int) : this(EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), level) {
        this.load(tag)
        this.setPickUpDelay(60)
        this.setUnlimitedLifetime()
        this.item.count = count
    }

    override fun getType(): EntityType<*> {
        return EMPTY_DIMENSIONAL_MATRIX_TYPE.get()
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)

        compound.putInt("matrixFill", fill)
        if (this.dest != null)
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
        super.tick()
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
        } else if (!level().isClientSide) {
            if (this.item.count < 64) { // Merge stacks
                for (itemEntity in this.level().getEntitiesOfClass(this::class.java, this.boundingBox.inflate(0.5, 0.0, 0.5), { e -> e != this && areMergable(this.item, e.item) })) {
                    if (areMergable(this.item, itemEntity.item) && itemEntity.dest == null) {
                        this.item = merge(this.item, itemEntity.item, this.item.count + itemEntity.item.count)
                        itemEntity.discard()
                    }
                }
            }
        }
    }

    fun findPortalBounds(pos: BlockPos, axis: Direction.Axis): AABB {
        var top = pos.toVec3().toVec3i()
        var bottom = pos.toVec3().toVec3i()

        while (level().getBlockState(BlockPos(top.above())).block is NetherPortalBlock) {
            top = top.above()
        }

        while (level().getBlockState(BlockPos(bottom.below())).block is NetherPortalBlock) {
            bottom = bottom.below()
        }

        var temp = top
        do {
            top = temp
            temp = top.relative(axis, 1)
        } while (level().getBlockState(BlockPos(temp)).block is NetherPortalBlock)

        temp = bottom
        do {
            bottom = temp
            temp = bottom.relative(axis, -1)
        } while (level().getBlockState(BlockPos(temp)).block is NetherPortalBlock)

        return AABB.of(BoundingBox.fromCorners(top, bottom))
    }

    override fun setAsInsidePortal(portal: Portal, pos: BlockPos) {
        if (level().isClientSide || fill != 0 || portal !is NetherPortalBlock) return

        val axis = level().getBlockState(pos).getValue(portal.stateDefinition.getProperty("axis")) as Direction.Axis
        val portalBounds = findPortalBounds(pos, axis)
        val found = level().getEntitiesOfClass(this::class.java, portalBounds)

        if (this.item.count > 1 || found.filter { it.item.count == 1 }.size > 1) { // Make sure only one is in the portal
            this.deltaMovement = Vec3(if (axis == Direction.Axis.Z) this.deltaMovement.reverse().scale(0.5).x else this.deltaMovement.x, 0.0, if (axis == Direction.Axis.X) this.deltaMovement.reverse().scale(0.5).z else this.deltaMovement.z)
            level().playSeededSound(null, this.x, this.y, this.z, PLAYER_TELEPORT, this.soundSource, 0.5f, 0.1f, -198679135428719823)
            this.fill = 1
            this.setDefaultPickUpDelay()
            return
        }

        val bounds = this.makeBoundingBox().move(this.x, this.y, pos.center.z)
        dest = if (axis == Direction.Axis.Z)
            Vec3(AABB.ofSize(pos.center, bounds.xsize, bounds.ysize, bounds.zsize).center.x(), this.y, this.z)
        else
            Vec3(this.x, this.y, AABB.ofSize(pos.center, bounds.xsize, bounds.ysize, bounds.zsize).center.z)

        this.isNoGravity = true
        this.deltaMovement = Vec3.ZERO
    }
}