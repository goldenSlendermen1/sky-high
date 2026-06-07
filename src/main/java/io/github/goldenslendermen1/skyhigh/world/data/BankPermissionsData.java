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

import io.github.goldenslendermen1.skyhigh.SkyHigh;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BankPermissionsData extends PersistentState {
    public static final Type<BankPermissionsData> TYPE =
        new Type<>(
            BankPermissionsData::new,
            BankPermissionsData::fromNbt,
            null
        );

    private final Map<UUID, Set<UUID>> authorizedUuidToBanks = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> bankUuidToAuthorizedUuids = new ConcurrentHashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        SkyHigh.LOGGER.warn("WriteNbt was actually called");
        for (Map.Entry<UUID, Set<UUID>> entry : authorizedUuidToBanks.entrySet()) {
            NbtList list = new NbtList();

            for (UUID uuid : entry.getValue()) {
                list.add(NbtHelper.fromUuid(uuid));
            }

            nbt.put(entry.getKey().toString(), list);
        }

        return nbt;
    }

    public static BankPermissionsData fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        SkyHigh.LOGGER.warn("fromNbt was called");
        BankPermissionsData permissionsData = new BankPermissionsData();

        for (String key : nbt.getKeys()) {
            UUID authorizedUuid = UUID.fromString(key);
            Set<UUID> uuids = new HashSet<>();
            NbtList list = (NbtList) nbt.get(key);

            permissionsData.authorizedUuidToBanks.put(authorizedUuid, uuids);

            if (list == null || list.isEmpty())
                continue;

            for (NbtElement nbtElement : list) {
                UUID bankUuid = NbtHelper.toUuid(nbtElement);
                uuids.add(bankUuid);
                Set<UUID> bankUuidToAuthorizedUuids = permissionsData.bankUuidToAuthorizedUuids.computeIfAbsent(bankUuid, k -> new HashSet<>());
                bankUuidToAuthorizedUuids.add(authorizedUuid);
            }
        }

        return permissionsData;
    }

    @Override
    public void save(File file, RegistryWrapper.WrapperLookup registryLookup) {
        BankStorage.onWorldSave();
        super.save(file, registryLookup);
    }

    @SuppressWarnings("unused")
    public Map<UUID, Set<UUID>> getAuthorizedUuidToBanksCopy() {
        return new HashMap<>(authorizedUuidToBanks);
    }

    @SuppressWarnings("unused")
    public Map<UUID, Set<UUID>> getBankUuidToAuthorizedUuidsCopy() {
        return new HashMap<>(bankUuidToAuthorizedUuids);
    }

    public boolean isUuidAuthorized(UUID testingUuid, UUID targetUuid) {
        return this.authorizedUuidToBanks.containsKey(testingUuid) && this.authorizedUuidToBanks.get(testingUuid).contains(targetUuid);
    }

    public Set<UUID> getAuthorizedUuids(UUID bankUuid) {
        return this.bankUuidToAuthorizedUuids.containsKey(bankUuid)
            ? new HashSet<>(this.bankUuidToAuthorizedUuids.get(bankUuid))
            : new HashSet<>();
    }

    @SuppressWarnings("unused")
    public Set<UUID> getAuthorizedBanks(UUID authorizedUuid) {
        return this.authorizedUuidToBanks.containsKey(authorizedUuid)
            ? new HashSet<>(this.authorizedUuidToBanks.get(authorizedUuid))
            : new HashSet<>();
    }

    public boolean authorizeUuid(UUID authorizingUuid, UUID bankUuid) {
        this.bankUuidToAuthorizedUuids
            .computeIfAbsent(bankUuid, k -> new HashSet<>())
            .add(authorizingUuid);
//        if (this.bankUuidToAuthorizedUuids.containsKey(bankUuid)) {
//            this.bankUuidToAuthorizedUuids.get(bankUuid).add(authorizingUuid);
//        } else {
//            Set<UUID> uuids = new HashSet<>();
//            uuids.add(authorizingUuid);
//            this.bankUuidToAuthorizedUuids.put(bankUuid, uuids);
//        }

        boolean result = this.authorizedUuidToBanks
            .computeIfAbsent(authorizingUuid, k -> new HashSet<>())
            .add(bankUuid);
        SkyHigh.LOGGER.warn("{}", result);
        markDirty();
        return result;
//        if (this.authorizedUuidToBanks.containsKey(authorizingUuid)) {
//            boolean result = this.authorizedUuidToBanks.get(authorizingUuid).add(bankUuid);
//            SkyHigh.LOGGER.warn("{}", result);
//            this.markDirty();
//            return result;
//        }
//
//        Set<UUID> set = new HashSet<>();
//        set.add(bankUuid);
//        this.authorizedUuidToBanks.put(authorizingUuid, set);
//        this.markDirty();
//        return true;
    }

    public boolean unauthorizeUuid(UUID unauthorizingUuid, UUID bankUuid) {
        if (!this.authorizedUuidToBanks.containsKey(unauthorizingUuid))
            return false;

        Set<UUID> bankUuidToAuthorizedUuids = this.bankUuidToAuthorizedUuids.get(bankUuid);
        Set<UUID> authorizedUuidToBanks = this.authorizedUuidToBanks.get(unauthorizingUuid);
        bankUuidToAuthorizedUuids.remove(unauthorizingUuid);
        boolean result = authorizedUuidToBanks.remove(bankUuid);

        if (bankUuidToAuthorizedUuids.isEmpty())
            this.bankUuidToAuthorizedUuids.remove(bankUuid);

        if (authorizedUuidToBanks.isEmpty())
            this.authorizedUuidToBanks.remove(unauthorizingUuid);

        this.markDirty();
        return result;
    }
}