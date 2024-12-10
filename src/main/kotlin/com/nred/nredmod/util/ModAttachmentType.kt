//package com.nred.nredmod.util
//
//import com.mojang.serialization.Codec
//import com.mojang.serialization.codecs.RecordCodecBuilder
//import com.nred.nredmod.NredMod
//import net.minecraft.core.HolderLookup
//import net.minecraft.core.registries.Registries
//import net.minecraft.nbt.CompoundTag
//import net.minecraft.world.item.ItemStack
//import net.neoforged.bus.api.IEventBus
//import net.neoforged.neoforge.attachment.AttachmentType
//import net.neoforged.neoforge.common.util.INBTSerializable
//import net.neoforged.neoforge.energy.EnergyStorage
//import net.neoforged.neoforge.fluids.FluidStack
//import net.neoforged.neoforge.fluids.capability.templates.FluidTank
//import net.neoforged.neoforge.items.ItemStackHandler
//import net.neoforged.neoforge.registries.DeferredRegister
//import net.neoforged.neoforge.registries.NeoForgeRegistries
//import java.util.function.Predicate
//
//object ModAttachmentType {
////    val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NredMod.ID)
////    val ITEM_HANDLER = ATTACHMENT_TYPES.register(
////        "item_handler", { ->
////            AttachmentType.serializable({ -> ItemStackHandler(1) }).build()
////        })
////    val ENERGY_HANDLER = ATTACHMENT_TYPES.register(
////        "energy_handler", { ->
////            AttachmentType.serializable({ -> EnergyStorage(5000) }).build()
////        })
////    val FLUID_HANDLER = ATTACHMENT_TYPES.register(
////        "fluid_handler", { ->
////            AttachmentType.serializable({ -> FluidHandler(1) { _ -> true } }).build()
////        })
//
//    val ITEM_LIST_CODEC = RecordCodecBuilder.create {
//        it.group(
//            Codec.list(ItemStack.CODEC).fieldOf("items").
//        ).apply(it, ::ItemStackHandler)
//    }
//
//    val DATA_COMPONENT_TYPES  = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NredMod.ID);
//val ITEM_DATA = DATA_COMPONENT_TYPES.registerComponentType("item_data", {it.persistent(Codec.PASSTHROUGH).networkSynchronized()})
//
//    open class FluidHandler(capacity: Int, validator: Predicate<FluidStack>) : FluidTank(capacity, validator), INBTSerializable<CompoundTag> {
//        override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
//            return this.writeToNBT(provider, CompoundTag())
//        }
//
//        override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag) {
//            this.readFromNBT(provider, nbt)
//        }
//    }
//
////    fun register(eventBus: IEventBus) {
////        ATTACHMENT_TYPES.register(eventBus)
////    }
//
//
//}