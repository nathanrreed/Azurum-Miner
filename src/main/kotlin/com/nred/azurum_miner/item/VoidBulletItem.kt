package com.nred.azurum_miner.item

import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileItem
import net.minecraft.world.level.Level

class VoidBulletItem(properties: Properties) : Item(properties), ProjectileItem {
    override fun asProjectile(level: Level, pos: Position, stack: ItemStack, direction: Direction): Projectile {
        val bullet = VoidBullet(pos.x(), pos.y(), pos.z(), level)
        bullet.pickup = AbstractArrow.Pickup.DISALLOWED
        return bullet
    }
}