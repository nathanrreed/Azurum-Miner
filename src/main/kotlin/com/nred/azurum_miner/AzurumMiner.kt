package com.nred.azurum_miner

import com.electronwill.nightconfig.core.CommentedConfig
import com.nred.azurum_miner.block.ModBlocks
import com.nred.azurum_miner.config.ModCommonConfig
import com.nred.azurum_miner.config.ModCreativeModTabs
import com.nred.azurum_miner.config.ModCreativeModTabs.MOD_TAB
import com.nred.azurum_miner.entity.ModBlockEntities
import com.nred.azurum_miner.fluid.ModFluids
import com.nred.azurum_miner.item.EmptyMatrixItemEntity
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserEntity
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierEntity
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.MinerEntity
import com.nred.azurum_miner.machine.miner.MinerScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierEntity
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Items
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

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
        NeoForge.EVENT_BUS.addListener(::onPlayerToss)

        ModCreativeModTabs.register(MOD_BUS)
        ModRecipe.register(MOD_BUS)
        ModFluids.register(MOD_BUS)
        ModBlocks.register(MOD_BUS)
        ModMachines.register(MOD_BUS)
        ModItems.register(MOD_BUS)

        ModBlockEntities.register(MOD_BUS)
        ModMenuTypes.register(MOD_BUS)

        MOD_BUS.addListener(this::addCreative)

        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.COMMON, ModCommonConfig.CONFIG_SPEC, "azurum_miner.toml")
    }

    fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.getTabKey() == MOD_TAB.key) {
            for (item in ModItems.ITEMS.entries) {
                event.accept(item.get())
            }
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SIMPLE_VOID_PROCESSOR.get())
            event.accept(ModItems.VOID_PROCESSOR.get())
            event.accept(ModItems.ELABORATE_VOID_PROCESSOR.get())
            event.accept(ModItems.COMPLEX_VOID_PROCESSOR.get())
            event.accept(ModItems.CONGLOMERATE_OF_ORE_SHARD.get())

            for (ore in OreHelper.ORES) {
                ore.ingot?.get()?.let { event.accept(it) }
                ore.nugget?.get()?.let { event.accept(it) }
                ore.gear?.get()?.let { event.accept(it) }
                ore.gem?.get()?.let { event.accept(it) }
                ore.raw?.get()?.let { event.accept(it) }
            }
        }

        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            for (ore in OreHelper.ORES) {
                event.accept(ore.ore)
                event.accept(ore.deepslate_ore)
                ore.raw_block?.let { event.accept(it) }
            }
            event.accept(ModBlocks.CONGLOMERATE_OF_ORE_BLOCK)
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            for (ore in OreHelper.ORES) {
                event.accept(ore.block)
            }
            event.accept(ModBlocks.CONGLOMERATE_OF_ORE)
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (fluid in FluidHelper.FLUIDS) {
                event.accept(fluid.bucket)
            }
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModMachines.LIQUIFIER)
            event.accept(ModMachines.INFUSER)
            for (i in 0..<5) {
                event.accept(ModMachines.MINER_BLOCK_TIERS[i])
            }
        }
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        for (fluid in FluidHelper.FLUIDS) {
            ItemBlockRenderTypes.setRenderLayer(fluid.still.get(), RenderType.TRANSLUCENT)
            ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.TRANSLUCENT)
        }
    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.MINER_MENU.get(), ::MinerScreen)
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), ::LiquifierScreen)
        event.register(ModMenuTypes.INFUSER_MENU.get(), ::InfuserScreen)
        event.register(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), ::TransmogrifierScreen)
    }

    private fun registerConfig(event: ModConfigEvent.Loading) {
        CONFIG = event.config.loadedConfig!!.config()
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        for (i in 0..<5) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MINER_ENTITY_TIERS[i].get(), { myBlockEntity: MinerEntity, _ -> myBlockEntity.itemStackHandler })
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MINER_ENTITY_TIERS[i].get(), { myBlockEntity: MinerEntity, _ -> myBlockEntity.energyHandler })
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MINER_ENTITY_TIERS[i].get(), { myBlockEntity: MinerEntity, _ -> myBlockEntity.fluidHandler })
        }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.energyHandler }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.LIQUIFIER_ENTITY.get()) { myBlockEntity: LiquifierEntity, _ -> myBlockEntity.fluidHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.energyHandler }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.INFUSER_ENTITY.get()) { myBlockEntity: InfuserEntity, _ -> myBlockEntity.fluidHandler }

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TRANSMOGRIFIER_ENTITY.get()) { myBlockEntity: TransmogrifierEntity, _ -> myBlockEntity.itemStackHandler }
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.TRANSMOGRIFIER_ENTITY.get()) { myBlockEntity: TransmogrifierEntity, _ -> myBlockEntity.energyHandler }

        event.registerItem(Capabilities.FluidHandler.ITEM, { stack, _ ->
            object : FluidBucketWrapper(stack) {
                override fun getFluid(): FluidStack {
                    return FluidStack(ModFluids.snow_still, 1000)
                }
            }
        }, Items.POWDER_SNOW_BUCKET)
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }

    @SubscribeEvent
    fun clientToServerUpdate(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToServer(Payload.TYPE, Payload.STREAM_CODEC, IPayloadHandler(ServerPayloadHandler::handleDataOnNetwork))
    }

    @SubscribeEvent
    fun serverToClientUpdate(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToClient(FluidPayload.TYPE, FluidPayload.STREAM_CODEC, IPayloadHandler(FluidPayloadHandler::handleDataOnNetwork))
    }

    @SubscribeEvent
    fun fluidLoad(event: RegisterClientExtensionsEvent) {
        for (fluid in FluidHelper.FLUIDS) {
            event.registerFluidType(fluid.client, fluid.type)
        }

        event.registerFluidType(ModFluids.snow_client, ModFluids.snow_type)
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
//        event.enqueueWork()
    }

    fun onPlayerToss(event: ItemTossEvent) {
        if (event.entity.item.`is`(ModItems.EMPTY_DIMENSIONAL_MATRIX.get())) {
            event.isCanceled = true
            val tag = CompoundTag()
            event.entity.save(tag)
            event.player.getCommandSenderWorld().addFreshEntity(EmptyMatrixItemEntity(tag, event.entity.level()))
        }
    }
}