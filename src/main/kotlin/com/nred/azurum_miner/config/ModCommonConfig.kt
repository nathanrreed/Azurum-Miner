package com.nred.azurum_miner.config

import com.nred.azurum_miner.AzurumMiner
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec

@EventBusSubscriber(modid = AzurumMiner.ID, bus = EventBusSubscriber.Bus.MOD)
class ModCommonConfig {
    companion object {
        val BUILDER = ModConfigSpec.Builder()

        val CONFIG_SPEC = build()

        fun build(): ModConfigSpec {
            BUILDER.comment("Miner Settings").push("miner")

            BUILDER.comment("Setting for Tiered Machines").push("options")
            BUILDER.define("modifierRFMultiplier", 1.2)
            BUILDER.defineInRange("mBUsedOnMiss", 100, 0, 9223372036854775807)
            BUILDER.defineInRange("mBUsedOnHit", 50, 0, 9223372036854775807)
            BUILDER.comment("y = 1.2^{x}+4 is the default, where x is the number of modifier points gained by this function and y = the number of buckets need for the next level").define("fluidNeedExponentialBase", 1.2)
            BUILDER.defineInRange("fluidNeedMin", 4, 0, 9223372036854775807)
            BUILDER.pop()

            BUILDER.comment("Setting for Modifiers").push("modifiers")

            BUILDER.push("speed")
            BUILDER.comment("Additive speed up (% or value)").define("1", "15%")
            BUILDER.comment("FE multiplier (x or value)").define("1FE", "1.2x")
            BUILDER.comment("Speed up when cycle is a miss (% or value)").define("2", "50%")
            BUILDER.comment("FE multiplier (x or value)").define("2FE", "1.2x")
            BUILDER.comment("Additive speed up (% or value)").define("3", "15%")
            BUILDER.comment("FE multiplier (x or value)").define("3FE", "1.2x")
            BUILDER.comment("Additive speed up (% or value)").define("4", "20%")
            BUILDER.comment("FE multiplier (x or value)").define("4FE", "1.2x")
            BUILDER.comment("Additive speed up (% or value)").define("5", "30%")
            BUILDER.comment("FE multiplier (x or value)").define("5FE", "1.2x")
            BUILDER.pop()

            BUILDER.push("filter")
            BUILDER.comment("Gain 1st filter slot. Percentage of ores that are from the filter").defineInRange("1", 25, 0, 33)
            BUILDER.comment("FE multiplier (x or value)").define("1FE", "1.2x")
            BUILDER.comment("No more materials (unless in filter)")
            BUILDER.comment("FE multiplier (x or value)").define("2FE", "1.2x")
            BUILDER.comment("Gain 2nd filter slot. Percentage of ores that are from the filter").defineInRange("3", 25, 0, 33)
            BUILDER.comment("FE multiplier (x or value)").define("3FE", "1.2x")
            BUILDER.comment("No more raw")
            BUILDER.comment("FE multiplier (x or value)").define("4FE", "1.2x")
            BUILDER.comment("Gain 3rd filter slot and unlock tag filters. Percentage of ores that are from the filter").defineInRange("5", 25, 0, 33)
            BUILDER.comment("FE multiplier (x or value)").define("5FE", "1.2x")
            BUILDER.pop()

            BUILDER.push("accuracy")
            BUILDER.comment("Percentage additive less misses").defineInRange("1", 5, 0, 33)
            BUILDER.comment("FE multiplier (x or value)").define("1FE", "1.2x")
            BUILDER.comment("Percentage additive ore from higher unlocked tiers").defineInRange("2", 25, 0, 50)
            BUILDER.comment("FE multiplier (x or value)").define("2FE", "1.2x")
            BUILDER.comment("Percentage additive less misses").defineInRange("3", 5, 0, 33)
            BUILDER.comment("FE multiplier (x or value)").define("3FE", "1.2x")
            BUILDER.comment("Percentage additive ore from higher unlocked tiers or filtered ore").defineInRange("4", 25, 0, 50)
            BUILDER.comment("FE multiplier (x or value)").define("4FE", "1.2x")
            BUILDER.comment("No more misses")
            BUILDER.comment("FE multiplier (x or value)").define("5FE", "3x")
            BUILDER.pop()

            BUILDER.push("efficiency")
            BUILDER.pop()

            BUILDER.push("production")
            BUILDER.pop()

            BUILDER.pop()

            BUILDER.comment("Setting for Tiered Machines").push("tiers")

            BUILDER.comment("The percentage chance for the miner to output something after a cycle").push("baseAccuracy")
            BUILDER.defineInRange("tier1", 80, 0, 100)
            BUILDER.defineInRange("tier2", 60, 0, 100)
            BUILDER.defineInRange("tier3", 55, 0, 100)
            BUILDER.defineInRange("tier4", 60, 0, 100)
            BUILDER.defineInRange("tier5", 65, 0, 100)
            BUILDER.pop()


            BUILDER.comment("The amount of RF need to complete a cycle").push("baseEnergyNeeded")
            BUILDER.defineInRange("tier1", 20000, 0, 9223372036854775807)
            BUILDER.defineInRange("tier2", 20000, 0, 9223372036854775807)
            BUILDER.defineInRange("tier3", 80000, 0, 9223372036854775807)
            BUILDER.defineInRange("tier4", 900000, 0, 9223372036854775807)
            BUILDER.defineInRange("tier5", 10000000, 0, 9223372036854775807)
            BUILDER.pop()

            BUILDER.comment("The energy capacity").push("energyCapacity")
            BUILDER.defineInRange("tier1", 10000, 1000, 9223372036854775807)
            BUILDER.defineInRange("tier2", 500000, 1000, 9223372036854775807)
            BUILDER.defineInRange("tier3", 2500000, 1000, 9223372036854775807)
            BUILDER.defineInRange("tier4", 500000000, 1000, 9223372036854775807)
            BUILDER.defineInRange("tier5", 1000000000, 1000, 9223372036854775807)
            BUILDER.pop()

            BUILDER.comment("The percentage chance for the miner to output a chosen filter ore instead of all from tier").push("baseFilterChance")
            BUILDER.defineInRange("tier1", 0, 0, 100)
            BUILDER.defineInRange("tier2", 0, 0, 100)
            BUILDER.defineInRange("tier3", 5, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 25, 0, 100)
            BUILDER.pop()

            BUILDER.comment("The percentage chance for the miner to output raw instead of ore").push("baseRawChance")
            BUILDER.defineInRange("tier1", 0, 0, 100)
            BUILDER.defineInRange("tier2", 15, 0, 100)
            BUILDER.defineInRange("tier3", 25, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 5, 0, 100)
            BUILDER.pop()

            BUILDER.comment("The percentage chance the miner's progress will decrease per tick without enough power").push("baseResetChance")
            BUILDER.defineInRange("tier1", 20, 0, 100)
            BUILDER.defineInRange("tier2", 50, 0, 100)
            BUILDER.defineInRange("tier3", 65, 0, 100)
            BUILDER.defineInRange("tier4", 80, 0, 100)
            BUILDER.defineInRange("tier5", 100, 0, 100)
            BUILDER.pop()

            BUILDER.comment("The time time in ticks to complete the operation").push("baseTicksPerOp")
            BUILDER.defineInRange("tier1", 3200, 1, 9223372036854775807)
            BUILDER.defineInRange("tier2", 1600, 1, 9223372036854775807)
            BUILDER.defineInRange("tier3", 800, 1, 9223372036854775807)
            BUILDER.defineInRange("tier4", 400, 1, 9223372036854775807)
            BUILDER.defineInRange("tier5", 200, 1, 9223372036854775807)
            BUILDER.pop()

            BUILDER.comment("The percentage chance to get material instead of ore").push("baseVariance")
            BUILDER.defineInRange("tier1", 50, 0, 100)
            BUILDER.defineInRange("tier2", 30, 0, 100)
            BUILDER.defineInRange("tier3", 25, 0, 100)
            BUILDER.defineInRange("tier4", 15, 0, 100)
            BUILDER.defineInRange("tier5", 10, 0, 100)
            BUILDER.pop()

            //            BUILDER.comment("The percentage chance for the miner to output raw instead of ore").push("numFilters")
            //            BUILDER.defineInRange("tier1", 0, 0, 3)
            //            BUILDER.defineInRange("tier2", 0, 0, 3)
            //            BUILDER.defineInRange("tier3", 1, 0, 3)
            //            BUILDER.defineInRange("tier4", 1, 0, 3)
            //            BUILDER.defineInRange("tier5", 2, 0, 3)
            //            BUILDER.pop()

            BUILDER.comment("The number of modifier points").push("numModifierPoints")
            BUILDER.defineInRange("tier1", 2, 0, 25)
            BUILDER.defineInRange("tier2", 4, 0, 25)
            BUILDER.defineInRange("tier3", 6, 0, 25)
            BUILDER.defineInRange("tier4", 8, 0, 25)
            BUILDER.defineInRange("tier5", 10, 0, 25)
            BUILDER.pop()

            BUILDER.comment("The number of modifier slots").push("numModifierSlots")
            BUILDER.defineInRange("tier1", 1, 0, 5)
            BUILDER.defineInRange("tier2", 2, 0, 5)
            BUILDER.defineInRange("tier3", 3, 0, 5)
            BUILDER.defineInRange("tier4", 4, 0, 5)
            BUILDER.defineInRange("tier5", 5, 0, 5)
            BUILDER.pop()

            BUILDER.comment("The percentage chance to get multiple ores").push("baseMultiChance")
            BUILDER.defineInRange("tier1", 90, 0, 100)
            BUILDER.defineInRange("tier2", 80, 0, 100)
            BUILDER.defineInRange("tier3", 75, 0, 100)
            BUILDER.defineInRange("tier4", 30, 0, 100)
            BUILDER.defineInRange("tier5", 20, 0, 100)
            BUILDER.pop()

            BUILDER.comment("The amount of ores on multi").push("baseMultiAmount")
            BUILDER.defineInRange("tier1", 8, 1, 64)
            BUILDER.defineInRange("tier2", 5, 1, 64)
            BUILDER.defineInRange("tier3", 3, 1, 64)
            BUILDER.defineInRange("tier4", 3, 1, 64)
            BUILDER.defineInRange("tier5", 5, 1, 64)
            BUILDER.pop()

            BUILDER.pop()
            BUILDER.pop()

            return BUILDER.build()
        }
    }

    @SubscribeEvent
    fun onLoad(evemt: ModConfigEvent) {

    }
}
