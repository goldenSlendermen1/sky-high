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
package io.github.goldenslendermen1.skyhigh.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.goldenslendermen1.skyhigh.Components;
import io.github.goldenslendermen1.skyhigh.SkyHigh;
import io.github.goldenslendermen1.skyhigh.world.data.PlayerBankData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import javax.swing.text.html.parser.Entity;

public class Bank {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> skyHighCommand) {
        LiteralArgumentBuilder<ServerCommandSource> bankCommand = CommandManager.literal("bank");
        LiteralArgumentBuilder<ServerCommandSource> bankAdminCommand = CommandManager.literal("admin").requires(source -> source.hasPermissionLevel(5) || source.getServer().isSingleplayer());

        bankAdminCommand.then(CommandManager.literal("savings")
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                        .executes(Bank::setSavings))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .executes(Bank::resetSavings)))
            .then(CommandManager.literal("get")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .executes(Bank::getSavings))));

        bankAdminCommand.then(CommandManager.literal("loan")
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                        .executes(Bank::setLoan))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .executes(Bank::resetLoan)))
            .then(CommandManager.literal("get")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .executes(Bank::getLoan))));

        bankAdminCommand.then(CommandManager.literal("credit")
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0, 100))
                        .executes(Bank::setCreditScore))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .executes(Bank::resetCreditScore)))
            .then(CommandManager.literal("get")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .executes(Bank::getCreditScore))));

        bankCommand.then(bankAdminCommand);

        bankCommand.then(CommandManager.literal("info")
            .then(CommandManager.literal("savings")
                .executes(Bank::savingsBalance))
            .then(CommandManager.literal("loan")
                .executes(Bank::loanBalance))
            .then(CommandManager.literal("credit")
                .executes(Bank::creditScore)));

        bankCommand.then(CommandManager.literal("deposit")
            .then(CommandManager.literal("savings")
                    .executes(Bank::depositSavings))
            .then(CommandManager.literal("loan")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.05))
                    .executes(Bank::depositLoan))));

        bankCommand.then(CommandManager.literal("withdraw")
            .then(CommandManager.literal("savings")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.05))
                    .executes(Bank::withdrawSavings)))
            .then(CommandManager.literal("loan")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.05))
                    .executes(Bank::withdrawLoan))));

        skyHighCommand.then(bankCommand);
    }

    private static int setSavings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double amount = DoubleArgumentType.getDouble(context, "amount");
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());
            bankData.setSavings(amount);
        }

        return (int) amount;
    }

    private static int getSavings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        PlayerBankData bankData = PlayerBankData.get(world, target.getUuid());

        source.sendFeedback(
            () -> Text.literal(target.getName().getString() + " has Ɏ" + bankData.getSavings() + " in savings"),
            false
        );

        return 1;
    }

    private static int resetSavings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());
            bankData.resetSavings();
        }

        return 1;
    }

    private static int depositSavings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return 0;

        Hand activeHand = player.getActiveHand();
        ItemStack targetDenomination = player.getStackInHand(activeHand);
        double amount = targetDenomination.getOrDefault(Components.VALUE, 0.0) * targetDenomination.getCount();

        if (targetDenomination.isEmpty()) {
            source.sendError(Text.literal("You must hold a bill to add to savings!"));
            return 0;
        } else if (amount == 0) {
            source.sendError(Text.literal("Cannot add a " + targetDenomination.getName().getString() + " to savings as it is not a bill!"));
            return 0;
        }

        player.setStackInHand(activeHand, ItemStack.EMPTY);
        SkyHigh.BANKS.get(player.getUuid()).addSavings(amount);
        source.sendFeedback(
            () -> Text.literal("Added Ɏ" + amount + " to savings")
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) amount;
    }

    private static int withdrawSavings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return 0;

        PlayerBankData bankData = SkyHigh.BANKS.get(player.getUuid());
        double currentSavings = bankData.getSavings();

        if (currentSavings < PlayerBankData.LOWEST_DENOMINATION) {
            source.sendError(Text.literal("You do not have enough savings to perform a withdrawal!"));
            return 0;
        } else if (amount > currentSavings) {
            source.sendError(Text.literal("You do not have enough savings to withdraw Ɏ" + amount + "!"));
            return 0;
        } else if (!bankData.canWidthdrawSavings(amount)) {
            source.sendError(Text.literal("Fuck you!"));
            return 0;
        }

        double withdrawnAmount = bankData.withdrawSavings(player, amount);

        source.sendFeedback(
            () -> Text.literal("Withdrew Ɏ" + withdrawnAmount)
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) withdrawnAmount;
    }

    private static int savingsBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return 0;

        double savings = SkyHigh.BANKS.get(player.getUuid()).getSavings();

        source.sendFeedback(
            () -> Text.literal("You have Ɏ" + savings + " in savings")
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) savings;
    }

    private static int setCreditScore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double amount = DoubleArgumentType.getDouble(context, "amount");
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());
            bankData.setCreditScore(amount);
        }

        return (int) amount;
    }

    private static int getCreditScore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        PlayerBankData bankData = PlayerBankData.get(world, target.getUuid());

        source.sendFeedback(
            () -> Text.literal(target.getName().getString() + " has a credit score of " + bankData.getCreditScore()),
            false
        );

        return 1;
    }

    private static int resetCreditScore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());
            bankData.resetCreditScore();
        }

        return 1;
    }

    private static int creditScore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return 0;

        double creditScore = SkyHigh.BANKS.get(player.getUuid()).getCreditScore();

        source.sendFeedback(
            () -> Text.literal("Your credit score is " + creditScore)
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) creditScore;
    }

    private static int setLoan(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double amount = DoubleArgumentType.getDouble(context, "amount");
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());

            if (bankData.hasLoan())
                bankData.clearLoan();

            bankData.addLoan(amount, world.getTimeOfDay() / 24000L);
        }

        return (int) amount;
    }

    private static int getLoan(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        PlayerBankData bankData = PlayerBankData.get(world, target.getUuid());

        if (!bankData.hasLoan()) {
            source.sendFeedback(
                () -> Text.literal(target.getName().getString() + " has no loans"),
                false
            );

            return 0;
        }

        source.sendFeedback(
            () -> Text.literal(target.getName().getString() + " has 1 Ɏ" + bankData.getLoan() + " loan"),
            false
        );

        return 1;
    }

    private static int resetLoan(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();

        for (PlayerEntity player : EntityArgumentType.getPlayers(context, "targets")) {
            PlayerBankData bankData = PlayerBankData.get(world, player.getUuid());
            bankData.clearLoan();
        }

        return 1;
    }

    private static int withdrawLoan(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return 0;

        PlayerBankData bankData = SkyHigh.BANKS.get(player.getUuid());

        if (bankData.hasLoan()) {
            source.sendError(Text.literal("Cannot take out a loan due to being at the maximum allowed loans!"));
            return 0;
        } else if (bankData.getMaximumAllowedLoan() < amount) {
            if (bankData.getMaximumAllowedLoan() <= 0) {
                source.sendError(Text.literal("Cannot take out a loan for Ɏ" + amount + " as your credit score is 0! Request an admin for a credit reset"));
                return 0;
            }

            source.sendError(Text.literal("Cannot take out a loan for Ɏ" + amount + ", the maximum loan allowed for your credit score of " + bankData.getCreditScore() + " is Ɏ" + bankData.getMaximumAllowedLoan() + "!"));
            return 0;
        }

        double loaned = PlayerBankData.splitAmountIntoItems(amount, player::giveItemStack);
        bankData.addLoan(loaned, player.getServerWorld().getTimeOfDay() / 24000L);

        source.sendFeedback(
            () -> Text.literal("Took out a loan for Ɏ" + loaned)
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) loaned;
    }

    private static int loanBalance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return 0;

        double loanedAmount = SkyHigh.BANKS.get(player.getUuid()).getLoan();

        if (loanedAmount <= 0.0) {
            source.sendFeedback(
                () -> Text.literal("You have no outstanding loans")
                    .formatted(Formatting.YELLOW),
                false
            );

            return 0;
        }

        source.sendFeedback(
            () -> Text.literal("You have 1 outstanding loan for Ɏ" + loanedAmount)
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) loanedAmount;
    }

    private static int depositLoan(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return 0;

        PlayerBankData bankData = SkyHigh.BANKS.get(player.getUuid());

        if (!bankData.hasLoan()) {
            source.sendError(Text.literal("You have no outstanding loans to deposit!"));
            return 0;
        }

        double savings = bankData.getSavings();
        double loan = bankData.getLoan();

        if (savings < PlayerBankData.LOWEST_DENOMINATION) {
            source.sendError(Text.literal("You do not have enough savings to perform a deposit!"));
            return 0;
        } else if (amount > savings && savings < loan) {
            source.sendError(Text.literal("You do not have enough savings to deposit Ɏ" + amount + "!"));
            return 0;
        } else if (!bankData.canWidthdrawSavings(amount)) {
            source.sendError(Text.literal("Fuck you!"));
            return 0;
        }

        double finalAmount = Math.min(amount, loan);
        bankData.payLoan(finalAmount);
        bankData.setSavings(savings - finalAmount);

        if (!bankData.hasLoan()) {
            source.sendFeedback(
                () -> Text.literal("All of your outstanding loans have been paid off")
                    .formatted(Formatting.YELLOW),
                false
            );

            return (int) finalAmount;
        }

        source.sendFeedback(
            () -> Text.literal("Deposited Ɏ" + finalAmount + ", your remaining loan balance is Ɏ" + bankData.getLoan())
                .formatted(Formatting.YELLOW),
            false
        );

        return (int) finalAmount;
    }
}
