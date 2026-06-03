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

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static io.github.goldenslendermen1.skyhigh.SkyHigh.MOD_ID;
import static io.github.goldenslendermen1.skyhigh.items.Yollar.registerPresetYollar;

public class Items {
    public static final Item YOLLAR_VALUE_1 = registerPresetYollar("yollar_value_1", 1.0);
    public static final Item YOLLAR_VALUE_3 = registerPresetYollar("yollar_value_3", 3.0);
    public static final Item YOLLAR_VALUE_5 = registerPresetYollar("yollar_value_5", 5.0);
    public static final Item YOLLAR_VALUE_10 = registerPresetYollar("yollar_value_10", 10.0);
    public static final Item YOLLAR_VALUE_50 = registerPresetYollar("yollar_value_50", 50.0);
    public static final Item YOLLAR_VALUE_100 = registerPresetYollar("yollar_value_100", 100.0);
    public static final Item YOLLAR_VALUE_500 = registerPresetYollar("yollar_value_500", 500.0);
    public static final Item YOLLAR_VALUE_1000 = registerPresetYollar("yollar_value_1000", 1000.0);

    public static void initialize() {
        SkyHigh.LOGGER.info("Registering items...");
    }

    public static <GenericItem extends Item>
    GenericItem register(String name, Function<Settings, GenericItem> itemFactory, Settings settings) {
        RegistryKey<Item> itemKey = getItemRegistryKey(name);
        GenericItem item = itemFactory.apply(settings);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static <GenericItem extends Item>
    GenericItem register(String name, GenericItem item) {
        RegistryKey<Item> itemKey = getItemRegistryKey(name);
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static RegistryKey<Item> getItemRegistryKey(String name) {
        return RegistryKey.of(Registries.ITEM.getKey(), Identifier.of(MOD_ID, name));
    }
}
