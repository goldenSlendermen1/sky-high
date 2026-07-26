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
    public static final Item YOLLAR_VALUE_0_01 = registerPresetYollar("yollar_value_0_01", 0.01);
    public static final Item YOLLAR_VALUE_0_05 = registerPresetYollar("yollar_value_0_05", 0.05);
    public static final Item YOLLAR_VALUE_0_25 = registerPresetYollar("yollar_value_0_25", 0.25);
    public static final Item YOLLAR_VALUE_0_50 = registerPresetYollar("yollar_value_0_50", 0.50);
    public static final Item YOLLAR_VALUE_1_00 = registerPresetYollar("yollar_value_1_00", 1.00);
    public static final Item YOLLAR_VALUE_3_00 = registerPresetYollar("yollar_value_3_00", 3.00);
    public static final Item YOLLAR_VALUE_5_00 = registerPresetYollar("yollar_value_5_00", 5.00);
    public static final Item YOLLAR_VALUE_10_00 = registerPresetYollar("yollar_value_10_00", 10.00);
    public static final Item YOLLAR_VALUE_50_00 = registerPresetYollar("yollar_value_50_00", 50.00);
    public static final Item YOLLAR_VALUE_100_00 = registerPresetYollar("yollar_value_100_00", 100.00);
    public static final Item YOLLAR_VALUE_500_00 = registerPresetYollar("yollar_value_500_00", 500.00);
    public static final Item YOLLAR_VALUE_1000_00 = registerPresetYollar("yollar_value_1000_00", 1000.00);

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
