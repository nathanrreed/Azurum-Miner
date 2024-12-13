package com.nred.azurum_miner.item

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.nred.azurum_miner.AzurumMiner
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.Portal
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import kotlin.random.Random

object ModItems {
    val ITEMS = DeferredRegister.createItems(AzurumMiner.ID)

    //val tt = ITEM_ENTITIES.register("SS", {-> ItemEntity. })
    val SIMPLE_VOID_PROCESSOR = ITEMS.register("simple_void_processor") { -> Item(Properties()) }
    val VOID_PROCESSOR = ITEMS.register("void_processor") { -> Item(Properties()) }
    val ELABORATE_VOID_PROCESSOR = ITEMS.register("elaborate_void_processor") { -> Item(Properties()) }
    val COMPLEX_VOID_PROCESSOR = ITEMS.register("complex_void_processor") { -> Item(Properties()) }

    val CONGLOMERATE_OF_ORE_SHARD = ITEMS.register("conglomerate_of_ore_shard") { -> Item(Properties()) }
    val NETHER_DIAMOND = ITEMS.register("nether_diamond") { -> Item(Properties()) }
    val ENDER_DIAMOND = ITEMS.register("ender_diamond") { -> Item(Properties()) }
    val DIMENSIONAL_MATRIX = ITEMS.register("dimensional_matrix") { -> Item(Properties()) }
    val EMPTY_DIMENSIONAL_MATRIX = ITEMS.register("empty_dimensional_matrix") { -> Item(Properties()) }

//    val EXAMPLE_ITEM: DeferredItem<Item> = ITEMS.register(
//        "test",
//        {
//            ->
//            object : Item(Properties()) {
//                override fun appendHoverText(
//                    stack: ItemStack,
//                    context: TooltipContext,
//                    tooltipComponents: MutableList<Component>,
//                    tooltipFlag: TooltipFlag
//                ) {
//                    tooltipComponents.add(Component.literal("TEST"))
//                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
//                }
//            }
//        }
//    )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}

class EmptyMatrixItemEntity(tag: CompoundTag, level: Level) : ItemEntity(EntityType.ITEM, level) {
    var fill = 0
    var inPortal = false
    var dest = Vec3.ZERO

    init {
        this.load(tag)
        this.setPickUpDelay(40)
        this.setUnlimitedLifetime()
    }

    override fun tick() {
        if (this.inPortal) {
            if (this.fill < 20) {
                this.lerpPositionAndRotationStep(20, this.dest.x, this.dest.y, this.dest.z, this.rotationVector.y.toDouble(), this.rotationVector.x.toDouble())
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
                this.inPortal = false
                (level() as ServerLevel).sendParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.0, 0.4, 0.0, 0.05)
                this.playSound(DRAGON_FIREBALL_EXPLODE)
                this.playSound(PLAYER_LEVELUP)
                level().destroyBlock(BlockPos(dest.toVec3i()), false)
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

        this.inPortal = true
        this.isNoGravity = true
        this.deltaMovement = Vec3.ZERO
    }
}