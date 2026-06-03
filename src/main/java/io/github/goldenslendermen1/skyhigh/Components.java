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

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static io.github.goldenslendermen1.skyhigh.SkyHigh.LOGGER;
import static io.github.goldenslendermen1.skyhigh.SkyHigh.MOD_ID;

public class Components {
    public static final ComponentType<Double> VALUE = register("value", Codec.DOUBLE);

    public static <T> ComponentType<T> register(String name, Codec<T> codec) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, name),
            ComponentType.<T>builder().codec(codec).build()
        );
    }

    protected static void initialize() {
        SkyHigh.LOGGER.info("Registering components");
    }
}