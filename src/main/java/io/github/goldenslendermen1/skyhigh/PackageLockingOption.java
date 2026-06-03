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

import io.github.goldenslendermen1.skyhigh.api.Option;

/**
 * Similar to {@link BaseLockingOption}, but can only be locked by SkyHigh
 * @see BaseLockingOption
 * @param <T> The value type
 */
public class PackageLockingOption<T> extends BaseLockingOption<T> {
    /**
     * Constructs a new SkyHighLockingOption with a null value
     * @see Option#Option()
     */
    public PackageLockingOption() {
        super();
    }

    /**
     * Constructs a new SkyHighLockingOption with the given default value
     * @param value The default value
     * @see Option#Option(Object)
     */
    public PackageLockingOption(final T value) {
        super(value);
    }

    /**
     * Constructs a new SkyHighLockingOption with the given default priority and value
     * @param priority The default priority
     * @param value The default value
     * @see Option#Option(int, Object)
     */
    public PackageLockingOption(final int priority, final T value) {
        super(priority, value);
    }
}