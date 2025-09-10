package com.nred.azurum_miner

import com.nred.azurum_miner.AzurumMiner.ID
import com.nred.azurum_miner.entity.ModBlockEntities.GENERATOR_ENTITY
import com.nred.azurum_miner.entity.ModEntities.VOID_BULLET
import com.nred.azurum_miner.entity.VoidBulletRenderer
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.crystallizer.CrystallizerScreen
import com.nred.azurum_miner.machine.generator.GeneratorRenderer
import com.nred.azurum_miner.machine.generator.GeneratorScreen
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.MinerScreen
import com.nred.azurum_miner.machine.simple_generator.SimpleGeneratorScreen
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.FluidHelper
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@EventBusSubscriber(modid = ID, value = [Dist.CLIENT])
@OnlyIn(Dist.CLIENT)
object ClientSetup {
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        for (fluid in FluidHelper.FLUIDS) {
            ItemBlockRenderTypes.setRenderLayer(fluid.still.get(), RenderType.TRANSLUCENT)
            ItemBlockRenderTypes.setRenderLayer(fluid.flowing.get(), RenderType.TRANSLUCENT)
        }

        BlockEntityRenderers.register(GENERATOR_ENTITY.get(), ::GeneratorRenderer)
        EntityRenderers.register(VOID_BULLET.get(), ::VoidBulletRenderer)
        EntityRenderers.register(ModItems.EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), ::ItemEntityRenderer)

        ModList.get().getModContainerById(ID).orElseThrow().registerExtensionPoint(IConfigScreenFactory::class.java, IConfigScreenFactory { container: ModContainer, last: net.minecraft.client.gui.screens.Screen -> ConfigurationScreen(container, last) })
    }

    @SubscribeEvent
    private fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.MINER_MENU.get(), ::MinerScreen)
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), ::LiquifierScreen)
        event.register(ModMenuTypes.CRYSTALLIZER_MENU.get(), ::CrystallizerScreen)
        event.register(ModMenuTypes.INFUSER_MENU.get(), ::InfuserScreen)
        event.register(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), ::TransmogrifierScreen)
        event.register(ModMenuTypes.GENERATOR_MENU.get(), ::GeneratorScreen)
        event.register(ModMenuTypes.SIMPLE_GENERATOR_MENU.get(), ::SimpleGeneratorScreen)
    }
}