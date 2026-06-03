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
import io.github.goldenslendermen1.skyhigh.api.OptionListener;

/**
 * <p>Similar to {@link io.github.goldenslendermen1.skyhigh.api.Option}, but provides the ability to lock the option, rejecting attempted updates</p>
 * @see io.github.goldenslendermen1.skyhigh.api.Option
 * @param <T> The value type
 */
public abstract class BaseLockingOption<T> extends Option<T> {
    private volatile boolean locked = false;

    /**
     * Constructs a new BaseLockingOption with a null value
     * @see Option#Option()
     */
    BaseLockingOption() {
        super();
    }

    /**
     * Constructs a new BaseLockingOption with the given default value
     * @param value The default value
     * @see Option#Option(Object)
     */
    BaseLockingOption(final T value) {
        super(value);
    }

    /**
     * Constructs a new BaseLockingOption with the given default priority and value
     * @param priority The default priority
     * @param value The default value
     * @see Option#Option(int, Object)
     */
    BaseLockingOption(final int priority, final T value) {
        super(priority, value);
    }

    /**
     * Sets the value based on the given priority and whether the option is locked
     * @param value The new value
     * @param priority The priority of the value
     * @return Whether the value was changed
     */
    @Override
    public boolean set(final T value, final int priority) {
        if (this.locked)
            return false;

        return super.set(value, priority);
    }

    /**
     * @return Whether the option is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Adds the given listener for value or priority changes
     * @param listener The listener
     * @return Whether the listener was added
     */
    @Override
    public boolean addListener(OptionListener<T> listener) {
        if (this.locked)
            return false;

        return super.addListener(listener);
    }

    /**
     * Locks the option preventing its value and priority from being changed
     */
    synchronized boolean lock() {
        if (this.locked)
            return false;

        listeners.clear();
        this.locked = true;
        return true;
    }

    /**
     * Locks the option and returns its value
     * @return The current value of the LockingOption
     */
    T lockAndGet() {
        lock();
        return get();
    }
}
