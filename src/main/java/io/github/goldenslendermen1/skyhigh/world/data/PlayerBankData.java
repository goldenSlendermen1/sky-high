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
package io.github.goldenslendermen1.skyhigh.world.data;

import io.github.goldenslendermen1.skyhigh.Components;
import io.github.goldenslendermen1.skyhigh.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class PlayerBankData extends PersistentState {
    public static final Type<PlayerBankData> TYPE =
        new Type<>(
            PlayerBankData::new,
            PlayerBankData::createFromNbt,
            null
        );


    public static final double HIGHEST_DENOMINATION = 1000.0;
    public static final double LOWEST_DENOMINATION = 0.01;
    public static final List<Double> DENOMINATIONS = new ArrayList<>(Arrays.asList(
        LOWEST_DENOMINATION,
        0.05,
        0.10,
        0.50,
        1.0,
        3.0,
        5.0,
        10.0,
        50.0,
        100.0,
        500.0,
        HIGHEST_DENOMINATION
    ));

    public static final Map<Double, Item> DENOMINATION_TO_ITEM = Map.ofEntries(
        Map.entry(LOWEST_DENOMINATION, Items.YOLLAR_VALUE_1),
        Map.entry(0.05, Items.YOLLAR_VALUE_1),
        Map.entry(0.10, Items.YOLLAR_VALUE_1),
        Map.entry(0.50, Items.YOLLAR_VALUE_1),
        Map.entry(1.0, Items.YOLLAR_VALUE_1),
        Map.entry(3.0, Items.YOLLAR_VALUE_3),
        Map.entry(5.0, Items.YOLLAR_VALUE_5),
        Map.entry(10.0, Items.YOLLAR_VALUE_10),
        Map.entry(50.0, Items.YOLLAR_VALUE_50),
        Map.entry(100.0, Items.YOLLAR_VALUE_100),
        Map.entry(500.0, Items.YOLLAR_VALUE_500),
        Map.entry(HIGHEST_DENOMINATION, Items.YOLLAR_VALUE_1000)
    );

    @NotNull
    public final UUID UUID;
    private double savings = 100.0;

    private double creditScore = 50.0;
    private double creditScoreSquared = 2500.0;
    private double maximumAllowedLoan = 250.0;

    private double loaned = 0.0;
    private long loanStartTime = 0;
    private double loanInitialCreditScore = 0.0;

    public PlayerBankData() {
        this.UUID = java.util.UUID.randomUUID();
    }

    public PlayerBankData(@NotNull UUID uuid) {
        this.UUID = uuid;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putUuid("UUID", UUID);
        nbt.putDouble("Savings", savings);
        nbt.putDouble("CreditScore", creditScore);
        nbt.putDouble("Loaned", loaned);
        nbt.putDouble("LoanInitialCreditScore", loanInitialCreditScore);
        nbt.putLong("LoanStartTime", loanStartTime);
        return nbt;
    }

    public static PlayerBankData createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        PlayerBankData data = new PlayerBankData(nbt.getUuid("UUID"));
        data.savings = nbt.getDouble("Savings");
        data.creditScore = nbt.getDouble("CreditScore");

        if (data.creditScore != 50.0) {
            data.creditScoreSquared = -1;
            data.maximumAllowedLoan = -1;
        }

        data.loaned = nbt.getDouble("Loaned");
        data.loanInitialCreditScore = nbt.getDouble("LoanInitialCreditScore");
        data.loanStartTime = nbt.getLong("LoanStartTime");
        return data;
    }

    public static double splitAmountIntoItems(double amount, Consumer<ItemStack> consumer) {
        double originalAmount = amount;

        while (amount >= LOWEST_DENOMINATION) {
            double nextDenomination = LOWEST_DENOMINATION;

            for (Double denomination : DENOMINATIONS) {
                if (denomination > amount)
                    continue;

                nextDenomination = Math.max(nextDenomination, denomination);
            }

            amount -= nextDenomination;
            consumer.accept(new ItemStack(DENOMINATION_TO_ITEM.get(nextDenomination)));
        }

        return originalAmount - amount;
    }

    public static List<ItemStack> getItemsForAmount(double amount) {
        List<ItemStack> list = new ArrayList<>();
        splitAmountIntoItems(amount, list::add);
        return list;
    }

    public boolean hasLoan() {
        return loaned > 0;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
        markDirty();
    }

    public void resetSavings() {
        this.savings = 100.0;
        markDirty();
    }

    @Deprecated
    public void addSavings(double savings) {
        this.savings += savings;
        markDirty();
    }

    public boolean canWidthdrawSavings(double amount) {
        return amount <= savings && amount >= LOWEST_DENOMINATION;
    }

    public double withdrawSavings(ServerPlayerEntity player, double amount) {
        if (amount > savings)
            return 0.0;
        else if (amount < LOWEST_DENOMINATION)
            return 0.0;

        double withdrawn = splitAmountIntoItems(amount, itemStack -> {
            //noinspection DataFlowIssue
            double denomination = itemStack.get(Components.VALUE);
            savings -= denomination;
            player.giveItemStack(itemStack);
        });

        markDirty();
        return withdrawn;
    }

    public double getLoan() {
        return loaned;
    }

    public long getLoanStartTime() {
        return loanStartTime;
    }

    public double getInitialLoanCreditScore() {
        return this.loanInitialCreditScore;
    }

    public double getMaximumAllowedLoan() {
        if (creditScoreSquared < 0.0)
            creditScoreSquared = creditScore * creditScore;

        if (maximumAllowedLoan < 0.0)
            maximumAllowedLoan = creditScoreSquared / 10.0;

        return maximumAllowedLoan;
    }

    public boolean addLoan(double loanAmount, long loanStartTime) {
        if (this.hasLoan())
            return false;

        this.loanStartTime = loanStartTime;
        this.loaned = loanAmount;
        this.loanInitialCreditScore = creditScore;

        markDirty();
        return true;
    }

    public boolean payLoan(double amount) {
        if (!this.hasLoan())
            return false;

        this.loaned -= amount;

        markDirty();
        return true;
    }

    public void clearLoan() {
        this.loaned = 0;
        markDirty();
    }

    public double getCreditScore() {
        return this.creditScore;
    }

    public void setCreditScore(double creditScore) {
        this.creditScore = creditScore;
        creditScoreSquared = -1;
        maximumAllowedLoan = -1;
        markDirty();
    }

    public void resetCreditScore() {
        this.creditScore = 50.0;
        creditScoreSquared = 2500.0;
        maximumAllowedLoan = 250.0;
        markDirty();
    }

    public static PlayerBankData get(ServerWorld world, UUID uuid) {
        PersistentStateManager manager = world.getPersistentStateManager();

        return manager.getOrCreate(
            TYPE,
            "skyhigh_playerbank_" + uuid.toString()
        );
    }
}