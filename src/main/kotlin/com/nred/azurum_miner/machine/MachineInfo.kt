package com.nred.azurum_miner.machine

data class MachineInfoData(val numTanks: Int, val numItems: Int, val allowFluidInput: Boolean, val allowFluidOutput: Boolean, val allowEnergyInput: Boolean, val allowEnergyOutput: Boolean) {
    constructor(numTanks: Int, numItems: Int, allowFluidInput: Boolean, allowFluidOutput: Boolean) : this(numTanks, numItems, allowFluidInput, allowFluidOutput, true, false)
    constructor(numTanks: Int, numItems: Int) : this(numTanks, numItems, true, false, true, false)
}

object MachineInfo {
    val data = mapOf<String, MachineInfoData>(
        "infuser" to MachineInfoData(1, 3),
        "generator" to MachineInfoData(0, 4, false, false, false, true),
        "simple_generator" to MachineInfoData(0, 1, false, false, false, true),
        "liquifier" to MachineInfoData(1, 1, false, true),
        "crystallizer" to MachineInfoData(2, 1, true, true),
        "transmogrifier" to MachineInfoData(0, 2),
        "miner" to MachineInfoData(1, 4)
    )
}