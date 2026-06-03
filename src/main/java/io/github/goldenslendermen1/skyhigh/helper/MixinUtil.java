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
package io.github.goldenslendermen1.skyhigh.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MixinUtil {
    public static final float EPSILON = 0.001F;

    public static Entity asEntity(Object object) {
        return (Entity) object;
    }

    public static LivingEntity asLivingEntity(Object object) {
        return (LivingEntity) object;
    }

    public static boolean isVanillaEntity(Entity entity) {
        Identifier identifier = Registries.ENTITY_TYPE.getId(entity.getType());
        return identifier.getNamespace().equals("minecraft");
    }

    public static boolean isModdedEntity(Entity entity) {
        return !isVanillaEntity(entity);
    }

    public static class Reference<T> {
        @Nullable
        public T value;

        public Reference() {
            this(null);
        }

        public Reference(@Nullable T value) {
            this.value = value;
        }
    }

    public static class DoubleReference {
        private double value;
        private boolean initialized = false;

        public DoubleReference() {}

        public DoubleReference(double value) {
            this.initialized = true;
            this.value = value;
        }

        public boolean isInitialized() {
            return this.initialized;
        }

        public double get() {
            return this.value;
        }

        public void set(double value) {
            this.initialized = true;
            this.value = value;
        }
    }

    public static class FloatReference {
        private float value;
        private boolean initialized = false;

        public FloatReference() {}
        public FloatReference(float value) {
            this.initialized = true;
            this.value = value;
        }

        public boolean isInitialized() {
            return this.initialized;
        }

        public float get() {
            return this.value;
        }

        public void set(float value) {
            this.initialized = true;
            this.value = value;
        }
    }

    public static class IntReference {
        private int value;
        private boolean initialized = false;

        public IntReference() {}
        public IntReference(int value) {
            this.initialized = true;
            this.value = value;
        }

        public boolean isInitialized() {
            return this.initialized;
        }

        public int get() {
            return this.value;
        }

        public void set(int value) {
            this.initialized = true;
            this.value = value;
        }
    }
}
