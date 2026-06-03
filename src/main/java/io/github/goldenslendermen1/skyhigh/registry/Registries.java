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
package io.github.goldenslendermen1.skyhigh.registry;

import net.minecraft.entity.EntityType;

public class Registries {
    public static UniqueRegistry<EntityType<?>> Example = new UniqueRegistry<>(net.minecraft.registry.Registries.ENTITY_TYPE::getId);
}
