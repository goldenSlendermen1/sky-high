/**
 * Copyright (C) 2026 goldenSlendermen1
 * <p>
 * This file is part of SkyHigh.
 * <p>
 * SkyHigh is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * SkyHigh is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with SkyHigh. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.goldenslendermen1.skyhigh.items;

import io.github.goldenslendermen1.skyhigh.Components;
import io.github.goldenslendermen1.skyhigh.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;

public class Yollar extends Item {
    public static Random RANDOM = new Random();
    private int iterations = 0;
    private int nextIteration = 0;
    private int color = 0;

    public static void addTooltip(ItemStack stack, List<Text> tooltip, Formatting color) {
        tooltip.add(
            Text.translatable("item.sky-high.yollar.tooltip.has_value", stack.get(Components.VALUE))
                .formatted(color)
        );
    }

    public static Yollar registerPresetYollar(String name, double yollarValue) {
        return Items.register(name, Yollar::new, new Settings()
                .component(Components.VALUE, yollarValue));
    }

    public Yollar(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("unused")
    public Double getValue(ItemStack stack) {
        return stack.get(Components.VALUE);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Double value = stack.get(Components.VALUE);

        if (value == null) {
            tooltip.add(
                Text.translatable("item.sky-high.yollar.tooltip.missing_value")
                    .formatted(Formatting.GRAY)
            );

            return;
        }

        if (value <= 1.0)
            addTooltip(stack, tooltip, Formatting.AQUA);
        else if (value <= 3.0) {
            if (++iterations >= nextIteration) {
                iterations = 0;
                nextIteration = RANDOM.nextInt(20, 120);
                color = RANDOM.nextInt(3);

                if (tooltip.size() > 1)
                    tooltip.remove(1);
            }

            switch (color) {
                case 0:
                    addTooltip(stack, tooltip, Formatting.RED);
                    break;
                case 1:
                    addTooltip(stack, tooltip, Formatting.DARK_PURPLE);
                    break;
                case 2:
                    addTooltip(stack, tooltip, Formatting.DARK_AQUA);
                    break;
            }
        } else if (value <= 5.0)
            addTooltip(stack, tooltip, Formatting.GREEN);
        else if (value <= 10.0)
            addTooltip(stack, tooltip, Formatting.GOLD);
        else if (value <= 50.0)
            addTooltip(stack, tooltip, Formatting.DARK_RED);
        else if (value <= 100.0)
            addTooltip(stack, tooltip, Formatting.LIGHT_PURPLE);
        else if (value <= 500.0)
            addTooltip(stack, tooltip, Formatting.DARK_GRAY);
        else
            addTooltip(stack, tooltip, Formatting.WHITE);
    }
}
