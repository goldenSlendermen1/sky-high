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
package io.github.goldenslendermen1.skyhigh.api;

import io.github.goldenslendermen1.skyhigh.SkyHigh;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.goldenslendermen1.skyhigh.SkyHigh.LOGGER;

/**
 * <p>An API option that uses the value with the highest priority</p>
 * @param <T> The value type
 */
public class Option<T> {
    private T value = null;
    private int priority = 0;

    protected final List<OptionListener<T>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new Option with a null value
     */
    public Option() {}

    /**
     * Constructs a new Option with the given default value
     * @param value The default value
     */
    public Option(final T value) {
        this.value = value;
    }

    /**
     * Constructs a new Option with the given default priority and value
     * @param priority The default priority
     * @param value The default value
     */
    public Option(final int priority, final T value) {
        this.priority = priority;
        this.value = value;
    }

    /**
     * @return The priority of the current value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return The current value
     */
    public T get() {
        return value;
    }

    /**
     * @return Whether the current value is null
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Sets the value based on the given priority
     * @param value The new value
     * @param priority The priority of the value
     * @return Whether the value was changed
     *
     * @apiNote A current priority of 0 specially allows values of priority 0 to override it
     */
    public boolean set(final T value, final int priority) {
        final T oldValue;
        final int oldPriority;

        synchronized (this) {
            oldPriority = this.priority;

            if (priority <= oldPriority && (oldPriority != 0 && priority != 0))
                return false;

            oldValue = this.value;

            this.priority = priority;
            this.value = value;
        }

        for (OptionListener<T> listener : listeners) {
            try {
                listener.onChanged(this, oldValue, value, oldPriority, priority);
            } catch (Exception exception) {
                SkyHigh.LOGGER.error("Listener for Option listener failed", exception);
            }
        }

        return true;
    }

    /**
     * Adds the given listener for value or priority changes
     * @param listener The listener
     */
    public boolean addListener(final OptionListener<T> listener) {
        return listeners.add(listener);
    }

    /**
     * Removes the given listener from value or priority changes
     * @param listener The listener
     */
    public boolean removeListener(final OptionListener<T> listener) {
        return listeners.remove(listener);
    }
}
