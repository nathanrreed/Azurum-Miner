package com.nred.azurum_miner.compat.emi

import com.nred.azurum_miner.AzurumMiner
import com.nred.azurum_miner.fluid.ModFluids
import com.nred.azurum_miner.item.ModItems
import com.nred.azurum_miner.machine.ModMachines
import com.nred.azurum_miner.machine.infuser.InfuserMenu
import com.nred.azurum_miner.machine.infuser.InfuserScreen
import com.nred.azurum_miner.machine.liquifier.LiquifierMenu
import com.nred.azurum_miner.machine.liquifier.LiquifierScreen
import com.nred.azurum_miner.machine.miner.MinerScreen
import com.nred.azurum_miner.machine.miner.OptionsTab
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierMenu
import com.nred.azurum_miner.machine.transmogrifier.TransmogrifierScreen
import com.nred.azurum_miner.recipe.ModRecipe
import com.nred.azurum_miner.screen.ModMenuTypes
import com.nred.azurum_miner.util.FilterSetPayload
import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.neoforge.NeoForgeEmiIngredient
import dev.emi.emi.api.recipe.*
import dev.emi.emi.api.recipe.handler.EmiCraftContext
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.Bounds
import dev.emi.emi.api.widget.SlotWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient


@EmiEntrypoint
class EmiPlugin : EmiPlugin {
    companion object {
        val portal: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "portal")

        val LIQUIFIER_WORKSTATION: EmiStack = EmiStack.of(ModMachines.LIQUIFIER)
        val INFUSER_WORKSTATION: EmiStack = EmiStack.of(ModMachines.INFUSER)
        val TRANSMOGRIFIER_WORKSTATION: EmiStack = EmiStack.of(ModMachines.TRANSMOGRIFIER)
        val MINER_WORKSTATION: EmiIngredient = EmiIngredient.of(ModMachines.MINER_BLOCK_TIERS.map { EmiIngredient.of(Ingredient.of(it.get())) })
        val LIQUIFIER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "liquifier"), LIQUIFIER_WORKSTATION)
        val INFUSER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "infuser"), INFUSER_WORKSTATION)
        val TRANSMOGRIFIER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "transmogrifier"), TRANSMOGRIFIER_WORKSTATION)
        val MINER_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "miner"), MINER_WORKSTATION)
        val PORTAL_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "portal")) { guiGraphics, x, y, _ ->
            guiGraphics.blitSprite(portal, x, y, 16, 16)
        }
    }

    override fun register(registry: EmiRegistry) {
        registry.addCategory(LIQUIFIER_CATEGORY)
        registry.addCategory(INFUSER_CATEGORY)
        registry.addCategory(TRANSMOGRIFIER_CATEGORY)
        registry.addCategory(MINER_CATEGORY)
        registry.addCategory(PORTAL_CATEGORY)

        registry.addExclusionArea(LiquifierScreen::class.java) { screen, consumer -> consumer.accept(Bounds(screen.base.left(), screen.base.top(), screen.base.width, screen.base.height)) }
        registry.addExclusionArea(InfuserScreen::class.java) { screen, consumer -> consumer.accept(Bounds(screen.base.left(), screen.base.top(), screen.base.width, screen.base.height)) }
        registry.addExclusionArea(TransmogrifierScreen::class.java) { screen, consumer -> consumer.accept(Bounds(screen.base.left(), screen.base.top(), screen.base.width, screen.base.height)) }
        registry.addExclusionArea(MinerScreen::class.java) { screen, consumer -> consumer.accept(Bounds(screen.base.left(), screen.base.top(), screen.base.width, screen.base.height)) }
        registry.addExclusionArea(MinerScreen::class.java) { screen, consumer -> consumer.accept(Bounds(screen.base.left() - 10, screen.base.top() + 6, 10, 37)) }

        registry.addRecipeHandler(ModMenuTypes.INFUSER_MENU.get(), object : StandardRecipeHandler<InfuserMenu> {
            override fun getInputSources(handler: InfuserMenu): List<Slot> {
                return handler.slots
            }

            override fun getCraftingSlots(handler: InfuserMenu): List<Slot> {
                return handler.inputSlots
            }

            override fun supportsRecipe(recipe: EmiRecipe): Boolean {
                return (recipe.category == INFUSER_CATEGORY)
            }

            override fun getInventory(screen: AbstractContainerScreen<InfuserMenu>): EmiPlayerInventory {
                val inv = super.getInventory(screen)
                val stack = EmiStack.of(screen.menu.fluidHandler.getFluidInTank(0).fluid, screen.menu.fluidHandler.getFluidInTank(0).amount.toLong())
                inv.inventory.merge(stack, stack, { a, b -> a.setAmount(a.getAmount() + b.getAmount()) })
                return inv
            }

            override fun craft(recipe: EmiRecipe, context: EmiCraftContext<InfuserMenu>): Boolean {
                if (recipe !is EmiInfuserRecipe) return false
                val clone = EmiInfuserRecipe(recipe.recipeId, recipe.inputs, recipe.output, recipe.power, recipe.processingTime)
                clone.inputs.removeAt(2)

                return super.craft(clone, context)
            }
        })

        registry.addRecipeHandler(ModMenuTypes.LIQUIFIER_MENU.get(), object : StandardRecipeHandler<LiquifierMenu> {
            override fun getInputSources(handler: LiquifierMenu): List<Slot> {
                return handler.slots
            }

            override fun getCraftingSlots(handler: LiquifierMenu): List<Slot> {
                return handler.inputSlots
            }

            override fun supportsRecipe(recipe: EmiRecipe): Boolean {
                return (recipe.category == LIQUIFIER_CATEGORY)
            }

        })

        registry.addRecipeHandler(ModMenuTypes.TRANSMOGRIFIER_MENU.get(), object : StandardRecipeHandler<TransmogrifierMenu> {
            override fun getInputSources(handler: TransmogrifierMenu): List<Slot> {
                return handler.slots
            }

            override fun getCraftingSlots(handler: TransmogrifierMenu): List<Slot> {
                return handler.inputSlots
            }

            override fun supportsRecipe(recipe: EmiRecipe): Boolean {
                return (recipe.category == TRANSMOGRIFIER_CATEGORY)
            }

        })

        // For Miner filters
        registry.addDragDropHandler(MinerScreen::class.java) { screen, stack, mouseX, mouseY ->
            if (screen.tabManager.currentTab is OptionsTab) {
                for (slot in screen.menu.filterSlots) {
                    if (slot.active && slot.mayPlace(stack.emiStacks[0].itemStack) && ScreenRectangle(slot.x + screen.x, slot.y + 32 + screen.base.top(), 18, 18).containsPoint(mouseX, mouseY)) { // Slot found
                        slot.set(stack.emiStacks[0].itemStack)
                        screen.minecraft.player!!.connection.send(FilterSetPayload(stack.emiStacks[0].itemStack, slot.slotIndex))
                        return@addDragDropHandler true
                    }
                }
            }

            return@addDragDropHandler false
        }

        registry.addWorkstation(LIQUIFIER_CATEGORY, LIQUIFIER_WORKSTATION)
        registry.addWorkstation(INFUSER_CATEGORY, INFUSER_WORKSTATION)
        registry.addWorkstation(TRANSMOGRIFIER_CATEGORY, TRANSMOGRIFIER_WORKSTATION)
        registry.addWorkstation(MINER_CATEGORY, MINER_WORKSTATION)

        val manager = registry.recipeManager

        for (recipe in manager.getAllRecipesFor(ModRecipe.SHAPED_RECIPE_TRANSFORM_TYPE.get())) {
            registry.addRecipe(EmiCraftingRecipe(recipe.value.ingredients.map { EmiIngredient.of(it) }, EmiStack.of(recipe.value.resultStack), recipe.id, false))
        }

        for ((idx, type) in listOf(ModRecipe.MINER_TIER1_RECIPE_TYPE.get(), ModRecipe.MINER_TIER2_RECIPE_TYPE.get(), ModRecipe.MINER_TIER3_RECIPE_TYPE.get(), ModRecipe.MINER_TIER4_RECIPE_TYPE.get(), ModRecipe.MINER_TIER5_RECIPE_TYPE.get()).withIndex()) {
            for (recipe in manager.getAllRecipesFor(type)) {
                registry.addRecipe(EmiMinerRecipe(recipe.id, EmiIngredient.of(recipe.value.result), ModMachines.MINER_BLOCK_TIERS.filterIndexed { i, _ -> i <= idx }.map { EmiIngredient.of(Ingredient.of(ItemStack(it.asItem(), 1))) }, idx))
            }
        }

        for (recipe in manager.getAllRecipesFor(ModRecipe.LIQUIFIER_RECIPE_TYPE.get())) {
            registry.addRecipe(EmiLiquifierRecipe(recipe.id, recipe.value.ingredients.map { EmiIngredient.of(it) }, EmiStack.of(recipe.value.result.fluid, recipe.value.result.amount.toLong()), recipe.value.power, recipe.value.processingTime))
        }

        for (recipe in manager.getAllRecipesFor(ModRecipe.INFUSER_RECIPE_TYPE.get())) {
            registry.addRecipe(EmiInfuserRecipe(recipe.id, recipe.value.ingredients.map { EmiIngredient.of(it) } + NeoForgeEmiIngredient.of(SizedFluidIngredient.of(recipe.value.inputFluid)), EmiStack.of(recipe.value.result), recipe.value.power, recipe.value.processingTime))
        }

        for (recipe in manager.getAllRecipesFor(ModRecipe.TRANSMOGRIFIER_RECIPE_TYPE.get())) {
            registry.addRecipe(EmiTransmogrifierRecipe(recipe.id, recipe.value.ingredients.map { EmiIngredient.of(it) }, EmiStack.of(recipe.value.result), recipe.value.power, recipe.value.processingTime))
        }

        registry.addRecipe(EmiPortalRecipe(ResourceLocation.parse(AzurumMiner.ID + ":/dimensional_matrix"), listOf(EmiIngredient.of(Ingredient.of(ItemStack(ModItems.EMPTY_DIMENSIONAL_MATRIX.asItem(), 1)))), EmiStack.of(ModItems.DIMENSIONAL_MATRIX, 1), 2400))

        registry.addRecipe(EmiInfoRecipe(listOf(NeoForgeEmiIngredient.of(FluidIngredient.of(ModFluids.snow_still.get())), EmiIngredient.of(Ingredient.of(Items.POWDER_SNOW_BUCKET))), listOf(Component.translatable("emi.azurum_miner.powder_snow")), ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "/powder_snow_bucket_from_fake_block")))
        registry.addRecipe(
            EmiWorldInteractionRecipe.builder().leftInput(EmiIngredient.of(Ingredient.of(Items.BUCKET))).rightInput(
                EmiIngredient.of(Ingredient.of(Blocks.POWDER_SNOW)), false
            ) { s ->
                object : SlotWidget(s.stack, s.bounds.x, s.bounds.y) {
                    val snow = Minecraft.getInstance().itemRenderer.itemModelShaper.modelManager.blockModelShaper.getBlockModel(Blocks.POWDER_SNOW.defaultBlockState())
                    override fun drawStack(draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
                        draw.pose().pushPose()
                        draw.pose().translate((x + 9).toFloat(), (y + 9).toFloat(), 150f)
                        draw.pose().scale(16.0F, -16.0F, 16.0F)
                        Minecraft.getInstance().itemRenderer.render(ItemStack(Blocks.POWDER_SNOW, 1), ItemDisplayContext.GUI, false, draw.pose(), draw.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, snow)
                        draw.flush()
                        draw.pose().popPose()
                    }

                    override fun getTooltip(mouseX: Int, mouseY: Int): List<ClientTooltipComponent?>? {
                        val tooltips = super.getTooltip(mouseX, mouseY)
                        tooltips[0] = ClientTooltipComponent.create(Component.translatable("fluid_type.azurum_miner.snow_type").withColor(net.minecraft.util.CommonColors.WHITE).visualOrderText)
                        tooltips[1] = ClientTooltipComponent.create(Component.literal("minecraft:powder_snow").withColor(net.minecraft.util.CommonColors.GRAY).visualOrderText)
                        return tooltips
                    }
                }
            }.id(ResourceLocation.fromNamespaceAndPath(AzurumMiner.ID, "/powder_snow_liquid_from_powder_snow_bucket")).output(EmiStack.of { Items.POWDER_SNOW_BUCKET }).build()
        )
    }
}
