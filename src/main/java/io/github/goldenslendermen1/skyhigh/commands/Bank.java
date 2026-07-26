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
package io.github.goldenslendermen1.skyhigh.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.goldenslendermen1.skyhigh.Components;
import io.github.goldenslendermen1.skyhigh.commands.bank.Admin;
import io.github.goldenslendermen1.skyhigh.helper.Command;
import io.github.goldenslendermen1.skyhigh.world.data.BankStorage;
import io.github.goldenslendermen1.skyhigh.world.data.PlayerBankData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.goldenslendermen1.skyhigh.helper.Command.*;

public class Bank {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> skyHighCommand) {
        LiteralArgumentBuilder<ServerCommandSource> bankCommand = CommandManager.literal("bank");

        Admin.register(dispatcher, bankCommand);

        bankCommand
            .then(createInfoSubset())
            .then(createDepositSubset())
            .then(createWithdrawSubset())
            .then(createPermissionSubset())
            .then(CommandManager.argument("target", StringArgumentType.word())
                .then(createInfoSubset())
                .then(createDepositSubset())
                .then(createWithdrawSubset()));

        skyHighCommand.then(bankCommand);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> createInfoSubset() {
        return CommandManager.literal("info")
            .then(CommandManager.literal("savings")
                .executes(Command.asLogging(Bank::savingsBalance)))
            .then(CommandManager.literal("loan")
                .executes(Command.asLogging(Bank::loanBalance)))
            .then(CommandManager.literal("credit")
                .executes(Command.asLogging(Bank::creditScore)))
            .then(CommandManager.literal("permission")
                .executes(Command.asLogging(Bank::authorized)));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> createDepositSubset() {
        return CommandManager.literal("deposit")
            .then(CommandManager.literal("savings")
                .executes(Command.asLogging(Bank::depositSavings)))
            .then(CommandManager.literal("loan")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .executes(Command.asLogging(Bank::depositLoan))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> createWithdrawSubset() {
        return CommandManager.literal("withdraw")
            .then(CommandManager.literal("savings")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .executes(Command.asLogging(Bank::withdrawSavings))))
            .then(CommandManager.literal("loan")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .executes(Command.asLogging(Bank::withdrawLoan))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> createPermissionSubset() {
        return CommandManager.literal("permission")
            .then(CommandManager.literal("authorize")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(Command.asLogging(Bank::authorize))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(Command.asLogging(Bank::authorize)))))
            .then(CommandManager.literal("unauthorize")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(Command.asLogging(Bank::unauthorize))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(Command.asLogging(Bank::unauthorize)))))
            .then(CommandManager.literal("reset")
                .executes(Command.asLogging(Bank::resetPermission)));
    }

    @Nullable
    public static List<UUID> getTargetUuids(CommandContext<ServerCommandSource> context, ServerCommandSource source) throws CommandSyntaxException {
        List<UUID> uuids = new ArrayList<>();

        if (widen(context).sky_high$getArguments().containsKey("target")) {
            Optional<UUID> uuid = getOfflinePlayerUUIDFromName(context, source, "target");

            if (uuid.isEmpty())
                return null;

            uuids.add(uuid.get());
        } else
            EntityArgumentType.getPlayers(context, "targets").forEach(player -> uuids.add(player.getUuid()));

        return uuids;
    }

    private static int playerExecutedError(ServerCommandSource source) {
        return error(source, "This command must be executed by a player!");
    }

    private static int noBankDataError(ServerCommandSource source) {
        return error(source, "Could not get bank data! Did you enter the username correctly?");
    }

    private static int notAuthorizedError(ServerCommandSource source, PlayerBankData bankData) {
        return error(source, "You are not authorized to perform this action on " + bankData.DISPLAY_NAME + "'s bank");
    }

    private static int offlinePlayerNotFoundError(ServerCommandSource source) {
        return error(source, "Could not get offline player UUID! Did you type the username correctly?");
    }

    private static int depositSavings(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        Hand activeHand = player.getActiveHand();
        ItemStack targetDenomination = player.getStackInHand(activeHand);
        double amount = targetDenomination.getOrDefault(Components.VALUE, 0.0) * targetDenomination.getCount();

        if (targetDenomination.isEmpty())
            return error(source, "You must hold a bill to add to savings!");
        else if (amount == 0)
            return error(source, "Cannot add a " + targetDenomination.getName().getString() + " to savings as it is not a bill!");

        player.setStackInHand(activeHand, ItemStack.EMPTY);
        bankData.setSavings(savings -> savings + amount);
        return success(source, "Added Ɏ" + amount + " to savings", Formatting.YELLOW);
    }

    private static int withdrawSavings(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        double currentSavings = bankData.getSavings();

        if (currentSavings < PlayerBankData.LOWEST_DENOMINATION)
            return error(source, "Not enough savings to perform a withdrawal!");
        else if (amount > currentSavings)
            return error(source, "Not enough savings to withdraw Ɏ" + amount + "!");

        double withdrawnAmount = bankData.withdrawSavings(player, amount);
        return success(source, "Withdrew Ɏ" + withdrawnAmount, Formatting.YELLOW);
    }

    private static int savingsBalance(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        double savings = bankData.getSavings();
        success(source, "Savings has Ɏ" + savings, Formatting.YELLOW);
        return (int) savings;
    }

    private static int creditScore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        double creditScore = bankData.getCreditScore();
        success(source, "Credit score is " + creditScore, Formatting.YELLOW);
        return (int) creditScore;
    }

    private static int withdrawLoan(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData =  getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        if (bankData.hasLoan())
            return error(source, "The maximum amount of loans have been taken out!");
        else if (bankData.getMaximumAllowedLoan() < amount)
            return error(source, "Cannot take out a loan for Ɏ" + amount + ", the maximum loan allowed for a credit score of " + bankData.getCreditScore() + " is Ɏ" + bankData.getMaximumAllowedLoan() + "!");

        double loaned = PlayerBankData.splitAmountIntoItems(amount, player::giveItemStack);
        bankData.addLoan(loaned, player.getServerWorld().getTimeOfDay() / 24000L);
        return success(source, "Took out a loan for Ɏ" + loaned, Formatting.YELLOW);
    }

    private static int loanBalance(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        double loanedAmount = bankData.getLoan();
        success(
            source,
            (loanedAmount <= 0.0)
                ? "There are no outstanding loans"
                : "There is 1 outstanding loan for Ɏ" + loanedAmount,
            Formatting.YELLOW
        );

        return (int) loanedAmount;
    }

    private static int depositLoan(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        double amount = DoubleArgumentType.getDouble(context, "amount");

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData =  getTargetBank(context, source).orElse(null);

        if (bankData == null)
            return noBankDataError(source);
        else if (!bankData.isUuidAuthorized(player.getUuid()))
            return notAuthorizedError(source, bankData);

        if (!bankData.hasLoan())
            return error(source, "There are no outstanding loans to deposit!");

        double savings = bankData.getSavings();
        double loan = bankData.getLoan();

        if (savings < PlayerBankData.LOWEST_DENOMINATION)
            error(source, "Not have enough savings to perform a deposit!");
        else if (amount > savings && savings < loan)
            error(source, "Not have enough savings to deposit Ɏ" + amount + "!");

        double finalAmount = Math.min(amount, loan);
        bankData.payLoan(finalAmount);
        bankData.setSavings(savings - finalAmount);

        return success(
            source,
            (bankData.hasLoan())
                ? "Deposited Ɏ" + finalAmount + ", the remaining loan balance is Ɏ" + bankData.getLoan()
                : "All outstanding loans have been paid off",
            Formatting.YELLOW
        );
    }

    private static int authorize(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        List<UUID> uuids = getTargetUuids(context, source);

        if (uuids == null)
            return offlinePlayerNotFoundError(source);

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = BankStorage.getBank(player.getUuid()).orElse(null);

        if (bankData == null)
            return noBankDataError(source);

        int authorized = 0;
        int alreadyAuthorized = 0;

        for (UUID uuid : uuids) {
            if (bankData.isUuidAuthorized(uuid))
                ++alreadyAuthorized;

            if (bankData.authorizeUuid(uuid))
                ++authorized;
        }

        success(
            source,
            ((authorized == 1)
                ? "Authorized 1 player"
                : "Authorized " + authorized + " players")
                + ". " + ((alreadyAuthorized == 1)
                    ? "1 was already authorized"
                    : alreadyAuthorized + " were already authorized"),
            Formatting.YELLOW
        );

        return alreadyAuthorized;
    }

    private static int unauthorize(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        List<UUID> uuids = getTargetUuids(context, source);

        if (uuids == null)
            return offlinePlayerNotFoundError(source);

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = BankStorage.getBank(player.getUuid()).orElse(null);

        if (bankData == null)
            return noBankDataError(source);

        int unauthorized = 0;
        int alreadyUnauthorized = 0;

        for (UUID uuid : uuids) {
            if (!bankData.isUuidAuthorized(uuid))
                ++alreadyUnauthorized;

            if (bankData.unauthorizeUuid(uuid))
                ++unauthorized;
        }

        success(
            source,
            ((unauthorized == 1)
                ? "Unauthorized 1 player"
                : "Unauthorized " + unauthorized + " players")
                + ". " + ((alreadyUnauthorized == 1)
                ? "1 was already unauthorized"
                : alreadyUnauthorized + " were already unauthorized"),
            Formatting.YELLOW
        );

        return alreadyUnauthorized;
    }

    private static int resetPermission(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = BankStorage.getBank(player.getUuid()).orElse(null);

        if (bankData == null)
            return noBankDataError(source);

        Set<UUID> authorizedUuids = bankData.getAuthorizedUuids();
        int unauthorized = authorizedUuids.size() - 1;
        authorizedUuids.forEach(bankData::unauthorizeUuid);

        success(
            source,
            ((unauthorized == 1)
                ? "Unauthorized 1 player"
                : "Unauthorized " + unauthorized + " players"),
            Formatting.YELLOW
        );

        return unauthorized;
    }

    private static int authorized(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
            return playerExecutedError(source);

        PlayerBankData bankData = BankStorage.getBank(player.getUuid()).orElse(null);

        if (bankData == null)
            return noBankDataError(source);

        Set<UUID> authorizedUuids = bankData.getAuthorizedUuids();

        if (authorizedUuids.isEmpty()) {
            success(source, "No players are authorized");
            return 0;
        }

        int authorized = authorizedUuids.size();
        UserCache userCache = source.getServer().getUserCache();
        StringBuilder message = new StringBuilder()
            .append(authorizedUuids.size())
            .append(" player")
            .append(authorized > 1 ? "s are" : " is")
            .append(" authorized: ");

        boolean first = true;

        for (UUID uuid : authorizedUuids) {
            Optional<GameProfile> profile = (userCache == null)
                ? Optional.empty()
                : userCache.getByUuid(uuid);

            if (!first)
                message.append(", ");
            else
                first = false;

            message.append(
                profile.isPresent()
                    ? profile.get().getName()
                    : "Unknown player"
            );
        }

        success(source, message.toString());
        return authorized;
    }
}
