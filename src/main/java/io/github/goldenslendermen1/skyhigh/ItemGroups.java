package io.github.goldenslendermen1.skyhigh;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroups {
    public static final RegistryKey<ItemGroup> SKYHIGH_KEY = getRegistryKey("skyhigh");

    @SuppressWarnings("unused")
    public static final ItemGroup SKYHIGH = register(
        SKYHIGH_KEY,
        Items.YOLLAR_VALUE_0_01,
        Items.YOLLAR_VALUE_0_05,
        Items.YOLLAR_VALUE_0_25,
        Items.YOLLAR_VALUE_0_50,
        Items.YOLLAR_VALUE_1_00,
        Items.YOLLAR_VALUE_1_00,
        Items.YOLLAR_VALUE_3_00,
        Items.YOLLAR_VALUE_5_00,
        Items.YOLLAR_VALUE_10_00,
        Items.YOLLAR_VALUE_50_00,
        Items.YOLLAR_VALUE_100_00,
        Items.YOLLAR_VALUE_500_00,
        Items.YOLLAR_VALUE_1000_00
    );

    public static RegistryKey<ItemGroup> getRegistryKey(String name) {
        return RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SkyHigh.MOD_ID, name));
    }

    @SafeVarargs
    public static <GenericItem extends Item>
    ItemGroup register(RegistryKey<ItemGroup> registryKey, GenericItem icon, GenericItem... items) {
        ItemGroup itemGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(icon))
            .displayName(Text.translatable("itemGroup." + registryKey.getValue().getPath()))
            .build();

        Registry.register(Registries.ITEM_GROUP, registryKey, itemGroup);

        ItemGroupEvents.modifyEntriesEvent(registryKey).register(groupEntries -> {
            for (GenericItem item : items) {
                groupEntries.add(item);
            }
        });

        return itemGroup;
    }

    public static void initialize() {}
}
