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
import net.minecraft.item.Items;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

@Deprecated
public class ItemValues {
    public static double SAPLING_VALUE = 1.0;
    public static double SCRAP_VALUE = 0.000001;


    /*
    0.50
    0.10
    0.05
    0.01
     */
    public static final Map<Item, Double> ITEM_TO_VALUE = Map.<Item, Double>ofEntries(
        Map.entry(Items.OAK_SAPLING, SAPLING_VALUE),
        Map.entry(Items.SPRUCE_SAPLING, SAPLING_VALUE),
        Map.entry(Items.ACACIA_SAPLING, SAPLING_VALUE),
        Map.entry(Items.BIRCH_SAPLING, SAPLING_VALUE),
        Map.entry(Items.CHERRY_SAPLING, SAPLING_VALUE),
        Map.entry(Items.DARK_OAK_SAPLING, SAPLING_VALUE),
        Map.entry(Items.JUNGLE_SAPLING, SAPLING_VALUE),
        Map.entry(Items.MANGROVE_PROPAGULE, SAPLING_VALUE),
        Map.entry(Items.CRIMSON_FUNGUS, 0.01),
        Map.entry(Items.WARPED_FUNGUS, 0.01),
        Map.entry(Items.OAK_LOG, 0.1),
        Map.entry(Items.SPRUCE_LOG, 0.035),
        Map.entry(Items.ACACIA_LOG, 0.075),
        Map.entry(Items.BIRCH_LOG, 0.1),
        Map.entry(Items.CHERRY_LOG, 0.075),
        Map.entry(Items.DARK_OAK_LOG, 0.04),
        Map.entry(Items.JUNGLE_LOG, 0.02),
        Map.entry(Items.MANGROVE_LOG, 0.1),
        Map.entry(Items.CRIMSON_STEM, 0.065),
        Map.entry(Items.WARPED_STEM, 0.065),
        Map.entry(Items.OAK_PLANKS, 0.025),
        Map.entry(Items.SPRUCE_PLANKS, 0.01),
        Map.entry(Items.ACACIA_PLANKS, 0.02),
        Map.entry(Items.BIRCH_PLANKS, 0.025),
        Map.entry(Items.CHERRY_PLANKS, 0.02),
        Map.entry(Items.DARK_OAK_PLANKS, 0.01),
        Map.entry(Items.JUNGLE_PLANKS, 0.004),
        Map.entry(Items.MANGROVE_PLANKS, 0.025),
        Map.entry(Items.CRIMSON_PLANKS, 0.015),
        Map.entry(Items.WARPED_PLANKS, 0.015),
        Map.entry(Items.OAK_LEAVES, SCRAP_VALUE),
        Map.entry(Items.SPRUCE_LEAVES, SCRAP_VALUE),
        Map.entry(Items.ACACIA_LEAVES, SCRAP_VALUE),
        Map.entry(Items.BIRCH_LEAVES, SCRAP_VALUE),
        Map.entry(Items.CHERRY_LEAVES, SCRAP_VALUE),
        Map.entry(Items.DARK_OAK_LEAVES, SCRAP_VALUE),
        Map.entry(Items.JUNGLE_LEAVES, SCRAP_VALUE),
        Map.entry(Items.MANGROVE_LEAVES, SCRAP_VALUE),
        Map.entry(Items.NETHER_WART_BLOCK, SCRAP_VALUE),
        Map.entry(Items.WARPED_WART_BLOCK, SCRAP_VALUE),
        Map.entry(Items.STICK, 0.02),
        Map.entry(Items.BAMBOO, 0.01),
        Map.entry(Items.COBBLESTONE, 0.01),
        Map.entry(Items.STONE, 0.01),
        Map.entry(Items.MOSSY_COBBLESTONE, 0.01),
        Map.entry(Items.MOSS_BLOCK, 0.01),
        Map.entry(Items.DEEPSLATE, 50.0),
        Map.entry(Items.COBBLED_DEEPSLATE, 50.0),
        Map.entry(Items.SAND, 100.0),
        Map.entry(Items.RED_SAND, 100.0),
        Map.entry(Items.NETHERRACK, 75.0),
        Map.entry(Items.BASALT, 50.0),
        Map.entry(Items.BLACKSTONE, 50.0),
        Map.entry(Items.GILDED_BLACKSTONE, 1000.0),
        Map.entry(Items.END_STONE, SCRAP_VALUE),
        Map.entry(Items.WHITE_WOOL, 0.05),
        Map.entry(Items.LIGHT_GRAY_WOOL, 0.05),
        Map.entry(Items.GRAY_WOOL, 0.05),
        Map.entry(Items.BLACK_WOOL, 0.05),
        Map.entry(Items.BROWN_WOOL, 0.05),
        Map.entry(Items.RED_WOOL, 0.05),
        Map.entry(Items.ORANGE_WOOL, 0.05),
        Map.entry(Items.YELLOW_WOOL, 0.05),
        Map.entry(Items.LIME_WOOL, 0.05),
        Map.entry(Items.GREEN_WOOL, 0.05),
        Map.entry(Items.CYAN_WOOL, 0.05),
        Map.entry(Items.LIGHT_BLUE_WOOL, 0.05),
        Map.entry(Items.BLUE_WOOL, 0.05),
        Map.entry(Items.PURPLE_WOOL, 0.05),
        Map.entry(Items.MAGENTA_WOOL, 0.05),
        Map.entry(Items.PINK_WOOL, 0.05),
        Map.entry(Items.GRAVEL, 125.0),
        Map.entry(Items.TERRACOTTA, 75.0),
        Map.entry(Items.WHITE_CONCRETE, 500.0),
        Map.entry(Items.LIGHT_GRAY_CONCRETE, 500.0),
        Map.entry(Items.GRAY_CONCRETE, 500.0),
        Map.entry(Items.BLACK_CONCRETE, 500.0),
        Map.entry(Items.BROWN_CONCRETE, 500.0),
        Map.entry(Items.RED_CONCRETE, 500.0),
        Map.entry(Items.ORANGE_CONCRETE, 500.0),
        Map.entry(Items.YELLOW_CONCRETE, 500.0),
        Map.entry(Items.LIME_CONCRETE, 500.0),
        Map.entry(Items.GREEN_CONCRETE, 500.0),
        Map.entry(Items.CYAN_CONCRETE, 500.0),
        Map.entry(Items.LIGHT_BLUE_CONCRETE, 500.0),
        Map.entry(Items.BLUE_CONCRETE, 500.0),
        Map.entry(Items.PURPLE_CONCRETE, 500.0),
        Map.entry(Items.MAGENTA_CONCRETE, 500.0),
        Map.entry(Items.PINK_CONCRETE, 500.0),
        Map.entry(Items.GLASS, 100.0),
        Map.entry(Items.TINTED_GLASS, 100.0),
        Map.entry(Items.AMETHYST_SHARD, 0.05),
        Map.entry(Items.DIRT, 25.0),
        Map.entry(Items.GRASS_BLOCK, 25.0),
        Map.entry(Items.CLAY, 25.0),
        Map.entry(Items.ICE, 0.05),
        Map.entry(Items.SNOWBALL, SCRAP_VALUE),
        Map.entry(Items.MAGMA_BLOCK, 200.0),
        Map.entry(Items.POINTED_DRIPSTONE, 100.0),
        Map.entry(Items.OBSIDIAN, 1.0),
        Map.entry(Items.SOUL_SAND, 75.0),
        Map.entry(Items.ANCIENT_DEBRIS, 200.0),
        Map.entry(Items.NETHERITE_INGOT, 2000.0),
        Map.entry(Items.SUGAR_CANE, 0.01),
        Map.entry(Items.CACTUS, 0.01),
        Map.entry(Items.WHEAT_SEEDS, 0.01),
        Map.entry(Items.COCOA_BEANS, 0.01),
        Map.entry(Items.PUMPKIN_SEEDS, 0.01),
        Map.entry(Items.MELON_SEEDS, 0.01),
        Map.entry(Items.GLOW_BERRIES, 0.01),
        Map.entry(Items.SWEET_BERRIES, 0.01),
        Map.entry(Items.NETHER_WART, 0.01),
        Map.entry(Items.SEA_PICKLE, 0.01),
        Map.entry(Items.KELP, 0.01),
        Map.entry(Items.HAY_BLOCK, 0.1),
        Map.entry(Items.SCULK, 0.01),
        Map.entry(Items.BEDROCK, Double.MAX_VALUE),
        Map.entry(Items.SKELETON_SKULL, 100.0),
        Map.entry(Items.WITHER_SKELETON_SKULL, 50.0),
        Map.entry(Items.ZOMBIE_HEAD, 100.0),
        Map.entry(Items.CREEPER_HEAD, 100.0),
        Map.entry(Items.DRAGON_HEAD, 1.0),
        Map.entry(Items.REDSTONE, 0.5),
        Map.entry(Items.WIND_CHARGE, 1.5),
        Map.entry(Items.TOTEM_OF_UNDYING, 35.0)
    );
}
