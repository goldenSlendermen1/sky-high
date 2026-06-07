package io.github.goldenslendermen1.skyhigh.commands.bank;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.goldenslendermen1.skyhigh.world.data.BankStorage;
import io.github.goldenslendermen1.skyhigh.world.data.PlayerBankData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static io.github.goldenslendermen1.skyhigh.helper.Command.*;

public class Admin {
    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> bankCommand) {
        LiteralArgumentBuilder<ServerCommandSource> bankAdminCommand = CommandManager.literal("admin")
            .requires(source -> source.hasPermissionLevel(5) || source.getServer().isSingleplayer());

        bankAdminCommand.then(CommandManager.literal("savings")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                            .executes(context -> Admin.setSavings(context, true)))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                            .executes(context -> Admin.setSavings(context, false))))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> Admin.resetSavings(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.resetSavings(context, false)))))
            .then(CommandManager.literal("get")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(context -> Admin.getSavings(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.getSavings(context, false))))));


        bankAdminCommand.then(CommandManager.literal("loan")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                            .executes(context -> Admin.setLoan(context, true)))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                            .executes(context -> Admin.setLoan(context, false))))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> Admin.resetLoan(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.resetLoan(context, false)))))
            .then(CommandManager.literal("get")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(context -> Admin.getLoan(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.getLoan(context, false))))));


        bankAdminCommand.then(CommandManager.literal("credit")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0, 100))
                            .executes(context -> Admin.setCreditScore(context, true)))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0, 100))
                            .executes(context -> Admin.setCreditScore(context, false))))))
            .then(CommandManager.literal("reset")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> Admin.resetCreditScore(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.resetCreditScore(context, false)))))
            .then(CommandManager.literal("get")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(context -> Admin.getCreditScore(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.getCreditScore(context, false))))));

        bankAdminCommand.then(CommandManager.literal("permission")
            .then(CommandManager.literal("reset")
                .then(CommandManager.literal("online")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> Admin.resetPermission(context, true))))
                .then(CommandManager.literal("offline")
                    .then(CommandManager.argument("target", StringArgumentType.word())
                        .executes(context -> Admin.resetPermission(context, false))))));


        bankCommand.then(bankAdminCommand);
    }

    @Nullable
    public static List<UUID> getTargetUuids(CommandContext<ServerCommandSource> context, ServerCommandSource source, boolean targetsAreOnline) throws CommandSyntaxException {
        List<UUID> uuids = new ArrayList<>();

        if (targetsAreOnline) {
            if (widen(context).sky_high$getArguments().containsKey("target"))
                uuids.add(EntityArgumentType.getPlayer(context, "target").getUuid());
            else
                EntityArgumentType.getPlayers(context, "targets").forEach(player -> uuids.add(player.getUuid()));
        } else {
            Optional<UUID> targetUuid = getOfflinePlayerUUIDFromName(context, source, "target");

            if (targetUuid.isEmpty())
                return null;

            uuids.add(targetUuid.get());
        }

        return uuids;
    }

    public static int queryBanks(CommandContext<ServerCommandSource> context, boolean online, String actionName, Consumer<PlayerBankData> consumer) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        List<UUID> uuids = getTargetUuids(context, source, online);

        if (uuids == null) {
            error(source, "Could not get offline player UUID! Did you type the username correctly?");
            return 1;
        }

        int failures = 0;

        for (UUID uuid : uuids) {
            PlayerBankData bankData = BankStorage.getBank(uuid).orElseGet(() -> BankStorage.loadBank(uuid).orElse(null));

            if (bankData == null) {
                ++failures;
                continue;
            }

            consumer.accept(bankData);
        }

        if (failures == 1)
            error(source, "Failed to " + actionName + " for 1 bank!");
        else if (failures > 0)
            error(source, "Failed to " + actionName + " for " + failures + " banks!");

        return failures;
    }

    private static int setSavings(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "set savings",
            bankData -> bankData.setSavings(DoubleArgumentType.getDouble(context, "amount"))
        );
    }

    private static int getSavings(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "get savings",
            bankData -> success(context, bankData.DISPLAY_NAME + " has Ɏ" + bankData.getSavings() + " in savings")
        );
    }

    private static int resetSavings(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "reset savings",
            PlayerBankData::resetSavings
        );
    }

    private static int setCreditScore(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "set credit score",
            bankData -> bankData.setCreditScore(DoubleArgumentType.getDouble(context, "amount"))
        );
    }

    private static int getCreditScore(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "get credit score",
            bankData -> success(context, bankData.DISPLAY_NAME + " has a credit score of " + bankData.getCreditScore())
        );
    }

    private static int resetCreditScore(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "reset credit score",
            PlayerBankData::resetCreditScore
        );
    }
    private static int setLoan(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();

        return queryBanks(
            context,
            online,
            "set loan",
            bankData -> {
                if (bankData.hasLoan())
                    bankData.clearLoan();

                bankData.addLoan(DoubleArgumentType.getDouble(context, "amount"), world.getTimeOfDay() / 24000L);
            }
        );
    }

    private static int getLoan(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "get loan",
            bankData -> {
                if (bankData.hasLoan())
                    success(context, bankData.DISPLAY_NAME + " has 1 Ɏ" + bankData.getSavings() + " loan");
                else
                    success(context, bankData.DISPLAY_NAME + " has no loans");
            }
        );
    }

    private static int resetLoan(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "reset loan",
            PlayerBankData::clearLoan
        );
    }

    private static int resetPermission(CommandContext<ServerCommandSource> context, boolean online) throws CommandSyntaxException {
        return queryBanks(
            context,
            online,
            "reset permissions",
            bankData -> bankData.getAuthorizedUuids().forEach(bankData::unauthorizeUuid)
        );
    }
}
