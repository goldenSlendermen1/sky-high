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
package io.github.goldenslendermen1.skyhigh.world.data;

import io.github.goldenslendermen1.skyhigh.Components;
import io.github.goldenslendermen1.skyhigh.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerBankData {
    public static final double HIGHEST_DENOMINATION = 1000.0;
    public static final double LOWEST_DENOMINATION = 0.01;
    public static final List<Double> DENOMINATIONS = new ArrayList<>(Arrays.asList(
        LOWEST_DENOMINATION,
        0.05,
        0.25,
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
        Map.entry(LOWEST_DENOMINATION, Items.YOLLAR_VALUE_0_01),
        Map.entry(0.05, Items.YOLLAR_VALUE_0_05),
        Map.entry(0.25, Items.YOLLAR_VALUE_0_25),
        Map.entry(0.50, Items.YOLLAR_VALUE_0_50),
        Map.entry(1.0, Items.YOLLAR_VALUE_1_00),
        Map.entry(3.0, Items.YOLLAR_VALUE_3_00),
        Map.entry(5.0, Items.YOLLAR_VALUE_5_00),
        Map.entry(10.0, Items.YOLLAR_VALUE_10_00),
        Map.entry(50.0, Items.YOLLAR_VALUE_50_00),
        Map.entry(100.0, Items.YOLLAR_VALUE_100_00),
        Map.entry(500.0, Items.YOLLAR_VALUE_500_00),
        Map.entry(HIGHEST_DENOMINATION, Items.YOLLAR_VALUE_1000_00)
    );

    @NotNull
    public final UUID UUID;

    @NotNull
    public final String DISPLAY_NAME;

    private boolean dirty;

    private double savings = 100.0;

    private double creditScore = 50.0;
    private double creditScoreSquared = 2500.0;
    private double maximumAllowedLoan = 250.0;

    private double loaned = 0.0;
    private long loanStartTime = 0;
    private double loanInitialCreditScore = 0.0;

    @SuppressWarnings("unused")
    public PlayerBankData(@NotNull UUID uuid) {
        this.UUID = uuid;
        this.DISPLAY_NAME = "";
    }

    public PlayerBankData(@NotNull UUID uuid, @NotNull String displayName) {
        this.UUID = uuid;
        this.DISPLAY_NAME = displayName;
    }

    @SuppressWarnings("unused")
    public PlayerBankData(@NotNull ServerPlayerEntity player) {
        this.UUID = player.getUuid();
        this.DISPLAY_NAME = player.getName().getString();
    }

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putUuid("UUID", UUID);
        nbt.putString("DisplayName", DISPLAY_NAME);
        nbt.putDouble("Savings", savings);
        nbt.putDouble("CreditScore", creditScore);
        nbt.putDouble("Loaned", loaned);
        nbt.putDouble("LoanInitialCreditScore", loanInitialCreditScore);
        nbt.putLong("LoanStartTime", loanStartTime);
    }

    @SuppressWarnings("unused")
    public static PlayerBankData fromNbt(@Nullable PlayerBankData output, NbtCompound nbt, int version) {
        if (output == null)
            output = new PlayerBankData(
                nbt.getUuid("UUID"),
                nbt.getString("DisplayName")
            );

        output.savings = nbt.getDouble("Savings");
        output.creditScore = nbt.getDouble("CreditScore");

        if (output.creditScore != 50.0) {
            output.creditScoreSquared = -1;
            output.maximumAllowedLoan = -1;
        }

        output.loaned = nbt.getDouble("Loaned");
        output.loanInitialCreditScore = nbt.getDouble("LoanInitialCreditScore");
        output.loanStartTime = nbt.getLong("LoanStartTime");
        return output;
    }

    @SuppressWarnings("unused")
    public static PlayerBankData fromNbt(NbtCompound nbt, int version) {
        return fromNbt(null, nbt, version);
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

            amount = Math.round((amount - nextDenomination) * 100) / 100D;
            consumer.accept(new ItemStack(DENOMINATION_TO_ITEM.get(nextDenomination)));
        }

        return originalAmount - amount;
    }

    @Deprecated
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

    public void setSavings(Function<Double, Double> supplier) {
        this.savings = supplier.apply(this.savings);
        markDirty();
    }

    public void resetSavings() {
        this.savings = 100.0;
        markDirty();
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("UnusedReturnValue")
    public boolean addLoan(double loanAmount, long loanStartTime) {
        if (this.hasLoan())
            return false;

        this.loanStartTime = loanStartTime;
        this.loaned = loanAmount;
        this.loanInitialCreditScore = creditScore;

        markDirty();
        return true;
    }

    @SuppressWarnings("unused")
    public void setLoan(Function<Double, Double> supplier, long loanStartTime) {
        this.loaned = supplier.apply(this.loaned);
        this.loanStartTime = loanStartTime;
        this.loanInitialCreditScore = this.creditScore;
        markDirty();
    }

    @SuppressWarnings("UnusedReturnValue")
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

    public void setCreditScore(Function<Double, Double> supplier) {
        this.creditScore = supplier.apply(this.creditScore);
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

    public boolean authorizeUuid(UUID uuid) {
        if (uuid.equals(this.UUID))
            return false;

        return BankStorage.getBankPermissions().authorizeUuid(uuid, this.UUID);
    }

    public boolean unauthorizeUuid(UUID uuid) {
        if (uuid.equals(this.UUID))
            return false;

        return BankStorage.getBankPermissions().unauthorizeUuid(uuid, this.UUID);
    }

    public boolean isUuidAuthorized(UUID uuid) {
        if (uuid.equals(this.UUID))
            return true;

        return BankStorage.getBankPermissions().isUuidAuthorized(uuid, this.UUID);
    }

    public Set<UUID> getAuthorizedUuids() {
        Set<UUID> authorizedUuids = BankStorage.getBankPermissions().getAuthorizedUuids(this.UUID);
        authorizedUuids.add(this.UUID);
        return authorizedUuids;
    }
}