package com.nred.azurum_miner.compat.cct

import com.nred.azurum_miner.machine.AbstractMachineBlockEntity
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral

abstract class MachinePeripheral(private val machine: AbstractMachineBlockEntity) : IPeripheral {
    @LuaFunction(mainThread = true)
    fun getEnergy(): Int {
        return machine.energyHandler.energyStored
    }

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Int {
        return machine.energyHandler.maxEnergyStored
    }
}