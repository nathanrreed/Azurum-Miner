package com.nred.azurum_miner

import com.electronwill.nightconfig.core.CommentedConfig
import com.nred.azurum_miner.block.ModBlocks
import com.nred.azurum_miner.compat.cct.RegisterPeripherals.registerPeripherals
import com.nred.azurum_miner.config.ModCommonConfig
import com.nred.azurum_miner.config.ModCreativeModTabs
import com.nred.azurum_miner.config.ModCreativeModTabs.MOD_TAB
import com.nred.azurum_miner.entity.*
import com.nred.azurum_miner.entity.ModBlockEntities.CRYSTALLIZER_ENTITY
import com.nred.azurum_miner.entity.ModBlockEntities.GENERATOR_ENTITY
import com.nred.azurum_miner.entity.ModBlockEntities.INFUSER_ENTITY
import com.nred.azurum_miner.entity.ModBlockEntities.LIQUIFIER_ENTITY
import com.nred.azurum_miner.entity.ModBlockEntities.MINER_ENTITY_TIERS
import com.nred.azurum_miner.entity.ModBlockEntities.SIMPLE_GENERATOR_ENTITY
import com.nred.azurum_miner.entity.ModBlockEntities.TRANSMOGRIFIER_ENTITY
import com.nred.azurum_miner.entity.ModEntities.VOID_BULLET
import com.nred.azurum_miner.fluid.ModFluids
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.crystallizer.CrystallizerEntity
import com.nred.azurum_miner.machine.crystallizer.CrystallizerScreen
import com.nred.azurum_miner.machine.generator.*
import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.MinerScreen
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorEntity
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.dimension.LevelStem.NETHER
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.*

@Mod(AzurumMiner.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object AzurumMiner {
    const val ID = "azurum_miner"
    var CONFIG: CommentedConfig = CommentedConfig.inMemory()

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })

        MOD_BUS.addListener(::registerScreens)
        MOD_BUS.addListener(::registerCapabilities)
        MOD_BUS.addListener(::registerConfig)
        MOD_BUS.addListener(::onCommonSetup)
        FORGE_BUS.addListener(::addTooltips)
        NeoForge.EVENT_BUS.addListener(::onEntityTravelToDimension)

        ModCreativeModTabs.register(MOD_BUS)
        ModRecipe.register(MOD_BUS)
        ModFluids.register(MOD_BUS)
        ModBlocks.register(MOD_BUS)
        ModMachines.register(MOD_BUS)
        ModItems.register(MOD_BUS)
        ModEntities.register(MOD_BUS)

        ModBlockEntities.register(MOD_BUS)
        ModMenuTypes.register(MOD_BUS)

        MOD_BUS.addListener(this::addCreative)

        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.SERVER, ModCommonConfig.CONFIG_SPEC, "azurum_miner.toml")
    }

    fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == MOD_TAB.key) {
            for (item in ModItems.ITEMS.entries) {
                event.accept(item.get())
            }
        }
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        for (fluid in FluidHelper.FLUIDS) {
            ItemBlockRenderTypes.setRenderLayer(fluid.still.get(), RenderType.TRANSLUCENT)
            ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.TRANSLUCENT)
        }

        BlockEntityRenderers.register(GENERATOR_ENTITY.get(), ::GeneratorRenderer)
        EntityRenderers.register(VOID_BULLET.get(), ::VoidBulletRenderer)
        EntityRenderers.register(ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), ::ItemEntityRenderer)

        ModList.get().getModContainerById(ID).orElseThrow().registerExtensionPoint(IConfigScreenFactory::class.java, IConfigScreenFactory { container: ModContainer, last: Screen -> ConfigurationScreen(container, last) })
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.MINER_MENU.get(), ::MinerScreen)
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), ::LiquifierScreen)
        event.register(ModMenuTypes.CRYSTALLIZER_MENU.get(), ::CrystallizerScreen)
        event.register(ModMenuTypes.INFUSER_MENU.get(), ::InfuserScreen)
        event.register(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), ::TransmogrifierScreen)
        event.register(ModMenuTypes.GENERATOR_MENU.get(), ::GeneratorScreen)
        event.register(ModMenuTypes.SIMPLE_GENERATOR_MENU.get(), ::SimpleGeneratorScreen)
    }

    private fun registerConfig(event: ModConfigEvent) {
        CONFIG = event.config.loadedConfig?.config() ?: CommentedConfig.inMemory()
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        for (i in 0..<5) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MINER_ENTITY_TIERS[i].get()) { myBlockEntity: MinerEntity, _ -> myBlockEntity.itemStackHandler }
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, MINER_ENTITY_TIERS[i].get()) { myBlockEntity: MinerEntity, _ -> myBlockEntity.energyHandler }
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, MINER_ENTITY_TIERS[i].get()) { myBlockEntity: MinerEntity, _ -> myBlockEntity.fluidHandler }
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.energyHandler }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.fluidHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRYSTALLIZER_ENTITY.get()) { myBlockEntity: CrystallizerEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CRYSTALLIZER_ENTITY.get()) { myBlockEntity: CrystallizerEntity, _ -> myBlockEntity.energyHandler }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CRYSTALLIZER_ENTITY.get()) { myBlockEntity: CrystallizerEntity, _ -> myBlockEntity.fluidHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.energyHandler }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.fluidHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TRANSMOGRIFIER_ENTITY.get()) { myBlockEntity: TransmogrifierEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TRANSMOGRIFIER_ENTITY.get()) { myBlockEntity: TransmogrifierEntity, _ -> myBlockEntity.energyHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, GENERATOR_ENTITY.get()) { myBlockEntity: GeneratorEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, GENERATOR_ENTITY.get()) { myBlockEntity: GeneratorEntity, _ -> myBlockEntity.energyHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SIMPLE_GENERATOR_ENTITY.get()) { myBlockEntity: SimpleGeneratorEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, SIMPLE_GENERATOR_ENTITY.get()) { myBlockEntity: SimpleGeneratorEntity, _ -> myBlockEntity.energyHandler }

        event.registerItem(Capabilities.FluidHandler.ITEM, { stack, _ ->
            object : FluidBucketWrapper(stack) {
                override fun getFluid(): FluidStack {
                    return FluidStack(ModFluids.snow_still, 1000)
                }
            }
        }, Items.POWDER_SNOW_BUCKET)

        event.registerItem(Capabilities.EnergyStorage.ITEM, { stack, _ ->
            val energyHandler = object : CustomEnergyHandler(CONFIG.getIntOrElse("void_gun.energyCapacity", 0), true, false) {
                override fun onContentsChanged() {
                    updateStackComponent(stack)
                }
            }
            val energy = if (stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("energy")) stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("energy") else 0
            energyHandler.internalInsertEnergy(energy, false)
            return@registerItem energyHandler
        }, ModItems.VOID_GUN)

        if (ModList.get().isLoaded("computercraft")) {
            registerPeripherals(event)
        }
    }

    @SubscribeEvent
    fun clientToServerUpdate(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToServer(Payload.TYPE, Payload.STREAM_CODEC, IPayloadHandler(ServerPayloadHandler::handleDataOnNetwork))
        registrar.playToServer(MinerFilterPayloadToServer.TYPE, MinerFilterPayloadToServer.STREAM_CODEC, IPayloadHandler(MinerFilterPayloadHandler::handleDataOnServer))
        registrar.playToServer(FilterSetPayload.TYPE, FilterSetPayload.STREAM_CODEC, IPayloadHandler(FilterSetPayloadHandler::handleDataOnServer))
        registrar.playToServer(ClearPayload.TYPE, ClearPayload.STREAM_CODEC, IPayloadHandler(ClearPayloadHandler::handleDataOnServer))
    }

    @SubscribeEvent
    fun serverToClientUpdate(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToClient(MinerFilterPayloadToPlayer.TYPE, MinerFilterPayloadToPlayer.STREAM_CODEC, IPayloadHandler(MinerFilterPayloadHandler::handleDataOnPlayer))
    }

    @SubscribeEvent
    fun registerLayers(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(VoidBulletModel.LAYER_LOCATION, VoidBulletModel::createBodyLayer)
    }

    @SubscribeEvent
    fun fluidLoad(event: RegisterClientExtensionsEvent) {
        for (fluid in FluidHelper.FLUIDS) {
            event.registerFluidType(fluid.client, fluid.type)
        }

        event.registerFluidType(ModFluids.snow_client, ModFluids.snow_type)
    }

    fun addTooltips(event: AddAttributeTooltipsEvent) {
        val menu = event.context.player()?.containerMenu
        // Adds tooltip for locked slots in generator
        if (menu is GeneratorMenu && ((!menu.itemHandler!!.getStackInSlot(0 + FUEL_SLOT_SAVE).isEmpty && menu.itemHandler.getStackInSlot(0).equals(event.stack)) || (!menu.itemHandler.getStackInSlot(1 + FUEL_SLOT_SAVE).isEmpty && menu.itemHandler.getStackInSlot(1).equals(event.stack)))) {
            event.addTooltipLines(*Helpers.itemComponentSplit("tooltip.azurum_miner.generator.clear").map { mutableComponent -> mutableComponent as Component }.toTypedArray())
        }
    }

    @SubscribeEvent
    fun bucketLoad(event: RegisterColorHandlersEvent.Item) {
        for (fluid in FluidHelper.FLUIDS) {
            event.register(({ stack, tintIndex -> if (tintIndex == 0) -1 else fluid.client.tintColor }), fluid.bucket.asItem())
        }
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
    }

    fun onEntityTravelToDimension(event: EntityTravelToDimensionEvent) {
        val entity = event.entity
        if (entity is ItemEntity && entity.item.`is`(ModItems.EMPTY_DIMENSIONAL_MATRIX.get()) && event.dimension == NETHER) {
            event.isCanceled = true

            val tag = CompoundTag()
            entity.save(tag)
            entity.remove(Entity.RemovalReason.KILLED)

            (entity.level() as ServerLevel).addFreshEntity(EmptyMatrixItemEntity(tag, entity.level()))
            if (entity.item.count > 1) {
                val e = EmptyMatrixItemEntity(tag, entity.level(), entity.item.count - 1)
                e.uuid = UUID.randomUUID()
                (entity.level() as ServerLevel).addFreshEntity(e)
            }
        }
    }
}