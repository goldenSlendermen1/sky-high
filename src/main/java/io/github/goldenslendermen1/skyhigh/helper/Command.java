package io.github.goldenslendermen1.skyhigh.helper;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.goldenslendermen1.skyhigh.world.data.BankStorage;
import io.github.goldenslendermen1.skyhigh.world.data.PlayerBankData;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Command {
    @SuppressWarnings("unchecked")
    public static <S>
    WidenedCommandContext<S> widen(CommandContext<S> context) {
        return (WidenedCommandContext<S>) context;
    }

    public static Optional<UUID> getOfflinePlayerUUIDFromName(CommandContext<ServerCommandSource> context, String argumentName) {
        return getOfflinePlayerUUIDFromName(context, context.getSource(), argumentName);
    }

    public static Optional<UUID> getOfflinePlayerUUIDFromName(CommandContext<ServerCommandSource> context, ServerCommandSource source, String argumentName) {
        String targetName = StringArgumentType.getString(context, argumentName);
        UserCache cache = source.getServer().getUserCache();

        if (cache == null)
            return Optional.empty();

        Optional<GameProfile> profile = cache.findByName(targetName);
        return profile.map(GameProfile::getId);
    }

    public static Optional<PlayerBankData> getPlayerBankData(CommandContext<ServerCommandSource> context, ServerCommandSource source, @Nullable UUID uuid) {
        return (source.getServer().getPlayerManager().getPlayer(uuid) == null)
            ? BankStorage.loadBank(getOfflinePlayerUUIDFromName(context, "target").orElse(null))
            : BankStorage.getBank(uuid);
    }

    public static Optional<PlayerBankData> getPlayerBankData(CommandContext<ServerCommandSource> context, ServerCommandSource source, @Nullable ServerPlayerEntity player) {
        return (player == null)
            ? Optional.empty()
            : getPlayerBankData(context, source, player.getUuid());
    }

    public static int error(ServerCommandSource source, String message, @Nullable Formatting color) {
        if (color == null)
            source.sendError(Text.literal(message));
        else
            source.sendError(Text.literal(message).formatted(color));

        return 0;
    }

    public static int error(ServerCommandSource source, String message) {
        return error(source, message, null);
    }

    public static int success(ServerCommandSource source, String message, @Nullable Formatting color) {
        if (color == null)
            source.sendFeedback(() -> Text.literal(message), false);
        else
            source.sendFeedback(() -> Text.literal(message).formatted(color), false);

        return 1;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int success(ServerCommandSource source, String message) {
        return success(source, message, null);
    }

    public static int success(CommandContext<ServerCommandSource> context, String message, @Nullable Formatting color) {
        return success(context.getSource(), message, color);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int success(CommandContext<ServerCommandSource> context, String message) {
        return success(context, message, null);
    }

    public static Optional<PlayerBankData> getTargetBank(CommandContext<ServerCommandSource> context, ServerCommandSource source) {
        WidenedCommandContext<ServerCommandSource> widenedContext = widen(context);
        if (widenedContext.sky_high$getArguments().containsKey("target"))
            return BankStorage.loadBank(getOfflinePlayerUUIDFromName(context, "target").orElse(null));

        return getPlayerBankData(context, source, source.getPlayer());
    }
}
