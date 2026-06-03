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
 * <p>{@link BaseLockingOption} but with {@link BaseLockingOption#lock()} and {@link BaseLockingOption#lockAndGet()} public</p>
 * @see BaseLockingOption
 */
public class PublicLockingOption<T> extends BaseLockingOption<T> {
    /**
     * Constructs a new LockingOption with a null value
     * @see Option#Option()
     */
    public PublicLockingOption() {
        super();
    }

    /**
     * Constructs a new LockingOption with the given default value
     * @param value The default value
     * @see Option#Option(Object)
     */
    public PublicLockingOption(final T value) {
        super(value);
    }

    /**
     * Constructs a new LockingOption with the given default priority and value
     * @param priority The default priority
     * @param value The default value
     * @see Option#Option(int, Object)
     */
    public PublicLockingOption(final int priority, final T value) {
        super(priority, value);
    }

    /**
     * Locks the option preventing its value and priority from being changed
     */
    @Override
    public synchronized boolean lock() {
        return super.lock();
    }

    /**
     * Locks the option and returns its value
     * @return The current value of the LockingOption
     */
    @Override
    public T lockAndGet() {
        return super.lockAndGet();
    }
}
