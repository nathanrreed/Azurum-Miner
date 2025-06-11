package com.nred.azurum_miner.config

import com.nred.azurum_miner.AzurumMiner
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.ModConfigSpec

@EventBusSubscriber(modid = AzurumMiner.ID, bus = EventBusSubscriber.Bus.MOD)
class ModCommonConfig {
    companion object {
        val BUILDER = ModConfigSpec.Builder()

        val CONFIG_SPEC = build()

        fun build(): ModConfigSpec {

            BUILDER.push("infuser")
            BUILDER.defineInRange("energyCapacity", 500000, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("liquifier")
            BUILDER.defineInRange("energyCapacity", 500000, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("crystallizer")
            BUILDER.defineInRange("energyCapacity", 500000, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("transmogrifier")
            BUILDER.defineInRange("energyCapacity", 5000000, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("generator")
            BUILDER.defineInRange("energyCapacity", 500000000, 0, 1000000000000)
            BUILDER.define("matrixDurability", 0)
            BUILDER.define("shardChance", 0.0005)
            BUILDER.pop()

            BUILDER.push("simple_generator")
            BUILDER.defineInRange("energyCapacity", 500000, 0, 1000000000000)
            BUILDER.defineInRange("energyProduction", 20, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("miner")

            BUILDER.translation("azurum_miner.configuration.miner.options").push("options")
            BUILDER.defineInRange("mBUsedOnMiss", 100, 0, 1000000000000)
            BUILDER.defineInRange("mBUsedOnHit", 50, 0, 1000000000000)
            BUILDER.comment("y = 1.35^{x}+4 is the default, where x is the number of modifier points gained by this function and y = the number of buckets need for the next level").define("fluidNeedExponentialBase", 1.35)
            BUILDER.comment("the +4 in above").defineInRange("bucketsNeededMin", 4, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.push("modifiers")

            BUILDER.translation("azurum_miner.configuration.modifier.speed").push("speed")
            BUILDER.translation("azurum_miner.configuration.modifier.speed.1").comment("Additive speed up (% or value)").define("1", "25%")
            BUILDER.define("1FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.speed.2").comment("Speed up when cycle is a miss").defineInRange("2", 50, 0, 100)
            BUILDER.define("2FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.speed.3").comment("Speed up (% or value)").define("3", "25%")
            BUILDER.define("3FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.speed.4").comment("Speed up (% or value)").define("4", "2s")
            BUILDER.define("4FE", 2)
            BUILDER.translation("azurum_miner.configuration.modifier.speed.5").comment("Speed up (% or value)").define("5", "3s")
            BUILDER.define("5FE", 4)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.modifier.filter").push("filter")
            BUILDER.translation("azurum_miner.configuration.modifier.filter.1").comment("Gain 1st filter slot. Percentage of ores that are from the filter").defineInRange("1", 25, 0, 33)
            BUILDER.define("1FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.filter.2").comment("No more materials (unless in filter)").define("2") { -> true }
            BUILDER.define("2FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.filter.3").comment("Gain 2nd filter slot. Percentage of ores that are from the filter").defineInRange("3", 25, 0, 33)
            BUILDER.define("3FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.filter.4").comment("No more raw").define("4") { -> true }
            BUILDER.define("4FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.filter.5").comment("Gain 3rd filter slot and unlock tag filters. Percentage of ores that are from the filter").defineInRange("5", 25, 0, 33)
            BUILDER.define("5FE", 1.2)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.modifier.accuracy").push("accuracy")
            BUILDER.translation("azurum_miner.configuration.modifier.accuracy.1").comment("Percentage additive less misses").defineInRange("1", 5, 0, 33)
            BUILDER.define("1FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.accuracy.2").comment("Percentage additive ore from higher unlocked tiers").defineInRange("2", 25, 0, 50)
            BUILDER.define("2FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.accuracy.3").comment("Percentage additive less misses").defineInRange("3", 5, 0, 33)
            BUILDER.define("3FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.accuracy.4").comment("Percentage additive ore from higher unlocked tiers").defineInRange("4", 25, 0, 50)
            BUILDER.define("4FE", 1.2)
            BUILDER.translation("azurum_miner.configuration.modifier.accuracy.5").comment("Total accuracy (100 for no misses)").defineInRange("5", 100, 0, 100)
            BUILDER.define("5FE", 3.0)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.modifier.efficiency").push("efficiency")
            BUILDER.translation("azurum_miner.configuration.modifier.efficiency.1").comment("Percentage additive less base power").defineInRange("1", 5, 0, 99)
            BUILDER.translation("azurum_miner.configuration.modifier.efficiency.2").comment("Percentage less on misses").defineInRange("2", 25, 0, 100)
            BUILDER.translation("azurum_miner.configuration.modifier.efficiency.3").comment("FE modifier reduction multiplier").define("3", 0.1)
            BUILDER.translation("azurum_miner.configuration.modifier.efficiency.4").comment("Percentage additive less base power").defineInRange("4", 5, 0, 99)
            BUILDER.translation("azurum_miner.configuration.modifier.efficiency.5").comment("Percentage additive less base power").defineInRange("5", 10, 0, 99)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.modifier.production").push("production")
            BUILDER.translation("azurum_miner.configuration.modifier.production.1").comment("Max ore output per cycle increase").defineInRange("1", 1, 0, 16)
            BUILDER.define("1FE", 1.4)
            BUILDER.translation("azurum_miner.configuration.modifier.production.2").comment("Max ore output per cycle increase").defineInRange("2", 1, 0, 16)
            BUILDER.define("2FE", 1.4)
            BUILDER.translation("azurum_miner.configuration.modifier.production.3").comment("Min ore output per cycle increase").defineInRange("3", 1, 0, 8)
            BUILDER.define("3FE", 1.8)
            BUILDER.translation("azurum_miner.configuration.modifier.production.4").comment("Max ore output per cycle increase").defineInRange("4", 1, 0, 16)
            BUILDER.define("4FE", 1.4)
            BUILDER.translation("azurum_miner.configuration.modifier.production.5").comment("Min ore output per cycle increase").defineInRange("5", 1, 0, 8)
            BUILDER.define("5FE", 3.0)
            BUILDER.pop()

            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers").comment("Setting for Miner Tiers").push("tiers")

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseAccuracy").comment("The percentage chance for the miner to output something after a cycle").push("baseAccuracy")
            BUILDER.defineInRange("tier1", 80, 0, 100)
            BUILDER.defineInRange("tier2", 60, 0, 100)
            BUILDER.defineInRange("tier3", 55, 0, 100)
            BUILDER.defineInRange("tier4", 60, 0, 100)
            BUILDER.defineInRange("tier5", 65, 0, 100)
            BUILDER.pop()


            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseEnergyNeeded").comment("The amount of RF need to complete a cycle").push("baseEnergyNeeded")
            BUILDER.defineInRange("tier1", 20000, 0, 1000000000000)
            BUILDER.defineInRange("tier2", 20000, 0, 1000000000000)
            BUILDER.defineInRange("tier3", 80000, 0, 1000000000000)
            BUILDER.defineInRange("tier4", 90000, 0, 1000000000000)
            BUILDER.defineInRange("tier5", 100000, 0, 1000000000000)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.energyCapacity").push("energyCapacity")
            BUILDER.defineInRange("tier1", 10000, 1000, 1000000000000)
            BUILDER.defineInRange("tier2", 500000, 1000, 1000000000000)
            BUILDER.defineInRange("tier3", 2500000, 1000, 1000000000000)
            BUILDER.defineInRange("tier4", 50000000, 1000, 1000000000000)
            BUILDER.defineInRange("tier5", 100000000, 1000, 1000000000000)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseFilterChance").comment("The percentage chance for the miner to output a chosen filter ore instead of all from tier").push("baseFilterChance")
            BUILDER.defineInRange("tier1", 0, 0, 100)
            BUILDER.defineInRange("tier2", 0, 0, 100)
            BUILDER.defineInRange("tier3", 5, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 25, 0, 100)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseRawChance").comment("The percentage chance for the miner to output raw instead of ore").push("baseRawChance")
            BUILDER.defineInRange("tier1", 0, 0, 100)
            BUILDER.defineInRange("tier2", 15, 0, 100)
            BUILDER.defineInRange("tier3", 25, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 5, 0, 100)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseResetChance").comment("The percentage chance the miner's progress will decrease per tick without enough power").push("baseResetChance")
            BUILDER.defineInRange("tier1", 20, 0, 100)
            BUILDER.defineInRange("tier2", 50, 0, 100)
            BUILDER.defineInRange("tier3", 65, 0, 100)
            BUILDER.defineInRange("tier4", 80, 0, 100)
            BUILDER.defineInRange("tier5", 100, 0, 100)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseTicksPerOp").comment("The time in ticks to complete the operation").push("baseTicksPerOp")
            BUILDER.defineInRange("tier1", 1800, 1, 1000000000000)
            BUILDER.defineInRange("tier2", 1200, 1, 1000000000000)
            BUILDER.defineInRange("tier3", 800, 1, 1000000000000)
            BUILDER.defineInRange("tier4", 400, 1, 1000000000000)
            BUILDER.defineInRange("tier5", 200, 1, 1000000000000)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseMaterialChance").comment("The percentage chance to get material instead of ore").push("baseMaterialChance")
            BUILDER.defineInRange("tier1", 40, 0, 100)
            BUILDER.defineInRange("tier2", 30, 0, 100)
            BUILDER.defineInRange("tier3", 25, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 8, 0, 100)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.numModifierPoints").comment("The number of modifier points").push("numModifierPoints")
            BUILDER.defineInRange("tier1", 2, 0, 25)
            BUILDER.defineInRange("tier2", 4, 0, 25)
            BUILDER.defineInRange("tier3", 6, 0, 25)
            BUILDER.defineInRange("tier4", 8, 0, 25)
            BUILDER.defineInRange("tier5", 10, 0, 25)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.numModifierSlots").comment("The number of modifier slots").push("numModifierSlots")
            BUILDER.defineInRange("tier1", 1, 0, 5)
            BUILDER.defineInRange("tier2", 2, 0, 5)
            BUILDER.defineInRange("tier3", 3, 0, 5)
            BUILDER.defineInRange("tier4", 4, 0, 5)
            BUILDER.defineInRange("tier5", 5, 0, 5)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseMultiChance").comment("The percentage chance to get multiple ores").push("baseMultiChance")
            BUILDER.defineInRange("tier1", 90, 0, 100)
            BUILDER.defineInRange("tier2", 80, 0, 100)
            BUILDER.defineInRange("tier3", 75, 0, 100)
            BUILDER.defineInRange("tier4", 30, 0, 100)
            BUILDER.defineInRange("tier5", 20, 0, 100)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseMultiMin").comment("The min amount of ores on multi").push("baseMultiMin")
            BUILDER.defineInRange("tier1", 6, 2, 64)
            BUILDER.defineInRange("tier2", 3, 2, 64)
            BUILDER.defineInRange("tier3", 2, 2, 64)
            BUILDER.defineInRange("tier4", 2, 2, 64)
            BUILDER.defineInRange("tier5", 2, 2, 64)
            BUILDER.pop()

            BUILDER.translation("azurum_miner.configuration.miner.tiers.baseMultiMax").comment("The max amount of ores on multi").push("baseMultiMax")
            BUILDER.defineInRange("tier1", 8, 2, 64)
            BUILDER.defineInRange("tier2", 5, 2, 64)
            BUILDER.defineInRange("tier3", 3, 2, 64)
            BUILDER.defineInRange("tier4", 3, 2, 64)
            BUILDER.defineInRange("tier5", 5, 2, 64)
            BUILDER.pop()

            BUILDER.pop()
            BUILDER.pop()

            return BUILDER.build()
        }
    }
}
