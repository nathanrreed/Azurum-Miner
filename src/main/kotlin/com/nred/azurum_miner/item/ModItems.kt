package com.nred.azurum_miner.item

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.entity.ModEntities.ENTITY_TYPES
import com.nred.azurum_miner.item.ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.Portal
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import kotlin.random.Random

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AzurumMiner.ID)

    val SIMPLE_VOID_PROCESSOR: DeferredItem<Item?> = ITEMS.register("simple_void_processor") { -> Item(Properties()) }
    val VOID_PROCESSOR: DeferredItem<Item?> = ITEMS.register("void_processor") { -> Item(Properties()) }
    val ELABORATE_VOID_PROCESSOR: DeferredItem<Item?> = ITEMS.register("elaborate_void_processor") { -> Item(Properties()) }
    val COMPLEX_VOID_PROCESSOR: DeferredItem<Item?> = ITEMS.register("complex_void_processor") { -> Item(Properties()) }

    val CONGLOMERATE_OF_ORE_SHARD: DeferredItem<Item?> = ITEMS.register("conglomerate_of_ore_shard") { -> Item(Properties()) }
    val NETHER_DIAMOND: DeferredItem<Item?> = ITEMS.register("nether_diamond") { -> Item(Properties()) }
    val ENDER_DIAMOND: DeferredItem<Item?> = ITEMS.register("ender_diamond") { -> Item(Properties()) }
    val DIMENSIONAL_MATRIX: DeferredItem<Item?> = ITEMS.register("dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX: DeferredItem<Item?> = ITEMS.register("empty_dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX_TYPE = ENTITY_TYPES.register("empty_dimensional_matrix_type") { -> EntityType.Builder.of(::EmptyMatrixItemEntity, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20).build("empty_dimensional_matrix_type") }
    val EMPTY_DIMENSIONAL_MATRIX_TAG_TYPE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "empty_dimensional_matrix_type"))


    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}

class EmptyMatrixItemEntity : ItemEntity {
    var fill = 0
    var dest: Vec3? = null

    constructor(entityType: EntityType<EmptyMatrixItemEntity>, level: Level) : super(entityType, level)
    constructor(tag: CompoundTag, level: Level) : this(EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), level) {
        this.load(tag)
        this.setPickUpDelay(40)
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
            if (this.fill < 20) {
                this.lerpPositionAndRotationStep(20, this.dest!!.x, this.dest!!.y, this.dest!!.z, this.rotationVector.y.toDouble(), this.rotationVector.x.toDouble())
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
            }
        }

        super.tick()
    }

    override fun setAsInsidePortal(portal: Portal, pos: BlockPos) {
        if (level().isClientSide || fill != 0 || portal !is NetherPortalBlock) return

        dest = if (level().getBlockState(pos).getValue(portal.stateDefinition.getProperty("axis")) == "z")
            Vec3(pos.center.x, this.y, this.z)
        else
            Vec3(this.x, this.y, pos.center.z)

        this.isNoGravity = true
        this.deltaMovement = Vec3.ZERO
    }
}