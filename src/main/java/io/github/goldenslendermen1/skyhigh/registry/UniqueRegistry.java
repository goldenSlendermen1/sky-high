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

import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UniqueRegistry<T> {
    private final Function<T, Identifier> IDENTIFIER_FROM_OBJECT;
    final Set<Identifier> ENTRIES = new HashSet<>();

    public UniqueRegistry(Function<T, Identifier> identifierFromObject) {
        IDENTIFIER_FROM_OBJECT = identifierFromObject;
    }

    public boolean has(Identifier identifier) {
        return ENTRIES.contains(identifier);
    }

    public boolean has(T object) {
        return ENTRIES.contains(IDENTIFIER_FROM_OBJECT.apply(object));
    }

    public boolean register(Identifier identifier) {
        return ENTRIES.add(identifier);
    }
}
