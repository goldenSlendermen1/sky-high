/**
 * Copyright (C) 2026 goldenSlendermen1
 *
 * This file is part of SkyHigh.
 *
 * SkyHigh is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SkyHigh is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyHigh. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.goldenslendermen1.skyhigh;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static io.github.goldenslendermen1.skyhigh.SkyHigh.MOD_ID;

public class DamageSources {
    public static final RegistryKey<DamageType> EXAMPLE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "example"));

    public static DamageSource getExampleDamage(World world) {
        return get(world, EXAMPLE);
    }

    public static DamageSource getExampleDamage(World world, Entity attacker) {
        return get(world, EXAMPLE, attacker);
    }

    public static DamageSource get(World world, RegistryKey<DamageType> registryKey) {
        return new DamageSource(
            world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(registryKey)
        );
    }

    public static DamageSource get(World world, RegistryKey<DamageType> registryKey, Entity attacker) {
        return new DamageSource(
            world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(registryKey),
            attacker
        );
    }

    public static void initialize() {}
}
