package io.github.goldenslendermen1.skyhigh.world.data;

import io.github.goldenslendermen1.skyhigh.SkyHigh;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class BankStorage {
    private static Path bankDirectory;

    @SuppressWarnings("unused")
    private static ServerWorld world;

    private static BankPermissionsData bankPermissions;
    private static final Map<UUID, PlayerBankData> banks = new HashMap<>();

    public static void forEach(java.util.function.Consumer<? super PlayerBankData> action) {
        banks.values().forEach(action);
    }

    @SuppressWarnings({"unused"})
    public static void onWorldLoad(@NotNull MinecraftServer server, @NotNull ServerWorld world) {
        if (world.getRegistryKey() != World.OVERWORLD)
            return;

        BankStorage.world = world;

        Path data = world.getServer().getSavePath(WorldSavePath.ROOT).resolve("data");
        bankDirectory = data.resolve(SkyHigh.MOD_ID).resolve("banks");

        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        bankPermissions = persistentStateManager.getOrCreate(BankPermissionsData.TYPE, "skyhigh_permissions_bank");

        try {
            Files.createDirectories(bankDirectory);
        } catch (IOException ioException) {
            SkyHigh.LOGGER.error("Could not create bank storage directory, this could cause data migration to throw an exception!", ioException);
        }

        try (Stream<Path> files = Files.list(data)) {
            files.forEach(path -> {
                File file = path.toFile();

                if (!file.isFile())
                    return;

                final String name = file.getName();
                final String prefix = "skyhigh_playerbank_";
                final String suffix = ".dat";

                if (!name.endsWith(suffix) || !name.startsWith(prefix))
                    return;

                throw new RuntimeException("This version of SkyHigh cannot migrate world data created before 1.0.0. The existing data is not supported and cannot be migrated.");

                /* Attempt at 0.9.0 world data migration, does not work because stored uuid is often incorrect
                UUID uuid = UUID.fromString(name.substring(prefix.length(), name.length() - suffix.length()));
                Optional<PlayerBankData> bankData = loadBank(file, uuid, null);

                if (!file.delete())
                    throw new RuntimeException("(" + SkyHigh.MOD_ID + ") Could not delete bank file during migration: " + file.getAbsolutePath());

                bankData.ifPresent(BankStorage::forceSaveBank);
                 */
            });
        } catch (IOException ioException) {
            throw new RuntimeException("(" + SkyHigh.MOD_ID + ") Could not read files under " + data + ", this can cause migration issues", ioException);
        }
    }

    @SuppressWarnings("unused")
    public static void onWorldUnload(@NotNull MinecraftServer server, @NotNull ServerWorld world) {
        if (world.getRegistryKey() != World.OVERWORLD)
            return;

        banks.values().forEach(BankStorage::saveAndRemove);
    }

    public static void onWorldSave() {
        banks.values().forEach(BankStorage::saveBank);
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        Optional<PlayerBankData> bankData = loadBank(
            uuid,
            new PlayerBankData(
                player.getUuid(),
                player.getName().getString()
            )
        );

        if (bankData.isPresent())
            banks.put(uuid, bankData.get());
        else
            banks.put(uuid, new PlayerBankData(uuid, player.getName().getString()));
    }

    public static void onPlayerLeave(ServerPlayerEntity player) {
        getBank(player.getUuid()).ifPresent(BankStorage::saveAndRemove);
    }

    public static boolean forceSaveBank(@NotNull PlayerBankData bankData) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound writtenNbt = new NbtCompound();

        bankData.writeNbt(writtenNbt);
        nbtCompound.put("data", writtenNbt);
        NbtHelper.putDataVersion(nbtCompound, 1);

        try {
            Path bankPath = getBankPath(bankData.UUID);

            if (!Files.exists(bankPath))
                Files.createFile(bankPath);

            NbtIo.writeCompressed(nbtCompound, bankPath);
            bankData.setDirty(false);
            return true;
        } catch (IOException iOException) {
            //noinspection StringConcatenationArgumentToLogCall
            SkyHigh.LOGGER.error("Could not save bank data for " + bankData.UUID, iOException);
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean saveBank(@NotNull PlayerBankData bankData) {
        if (!bankData.isDirty())
            return false;

        return forceSaveBank(bankData);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean saveAndRemove(@NotNull PlayerBankData bankData) {
        banks.remove(bankData.UUID);
        return saveBank(bankData);
    }

    public static BankPermissionsData getBankPermissions() {
        return bankPermissions;
    }

    @SuppressWarnings("unused")
    public static Path getBankDirectory() {
        return bankDirectory;
    }

    public static Path getBankPath(@NotNull UUID uuid) {
        return bankDirectory.resolve(uuid + ".dat");
    }

    public static File getBankFile(@NotNull UUID uuid) {
        return getBankPath(uuid).toFile();
    }

    public static Optional<PlayerBankData> loadBank(@NotNull File bankFile, @NotNull UUID uuid, @Nullable PlayerBankData output) {
        NbtCompound nbtCompound;

        if (!bankFile.exists())
            return Optional.empty();

        try {
            nbtCompound = NbtIo.readCompressed(bankFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
        } catch (IOException ioException) {
            //noinspection StringConcatenationArgumentToLogCall
            SkyHigh.LOGGER.error("Could not read bank data file for " + uuid, ioException);
            return Optional.empty();
        }

        int version = NbtHelper.getDataVersion(nbtCompound, 1);
        PlayerBankData bankData = PlayerBankData.fromNbt(output, nbtCompound.getCompound("data"), version);
        return Optional.of(bankData);
    }

    public static Optional<PlayerBankData> loadBank(@Nullable UUID uuid, @Nullable PlayerBankData output) {
        if (uuid == null)
            return Optional.empty();

        return loadBank(getBankFile(uuid), uuid, output);
    }

    public static Optional<PlayerBankData> loadBank(@Nullable UUID uuid) {
        return loadBank(uuid, null);
    }

    public static Optional<PlayerBankData> getBank(@Nullable UUID uuid) {
        return Optional.ofNullable(banks.get(uuid));
    }
}
