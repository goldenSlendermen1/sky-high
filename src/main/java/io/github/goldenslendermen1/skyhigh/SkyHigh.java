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

import io.github.goldenslendermen1.skyhigh.world.data.PlayerBankData;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkyHigh implements ModInitializer {
	public static final String MOD_ID = "sky-high";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<UUID, PlayerBankData> BANKS = new HashMap<>();
	private long lastDay = -1;

	@Override
	public void onInitialize() {
		Components.initialize();
		Items.initialize();
		DamageSources.initialize();

		CommandRegistrationCallback.EVENT.register(Commands::register);
		ServerPlayerEvents.JOIN.register(player -> {
			if (player.getServerWorld().isClient)
				return;

			UUID uuid = player.getUuid();
			BANKS.put(uuid, PlayerBankData.get(player.getServerWorld(), uuid));
		});

		ServerPlayerEvents.LEAVE.register(player -> {
			if (player.getServerWorld().isClient)
				return;

			UUID uuid = player.getUuid();
			BANKS.remove(uuid);
		});

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world.isClient)
				return;

			long time = world.getTimeOfDay();
			long day = time / 24000L;

			if (day == lastDay)
				return;

			lastDay = day;
			onNewDay(world);
		});
	}

	private void onNewDay(ServerWorld world){
		for (Map.Entry<UUID, PlayerBankData> entry : BANKS.entrySet()) {
			PlayerBankData data = entry.getValue();

			data.setSavings(data.getSavings() * 1.106);

			if (!data.hasLoan())
				continue;

			long loanStartTime = data.getLoanStartTime();
			long timeSinceLoan = lastDay - loanStartTime;

			if (timeSinceLoan <= 5)
				continue;

			long timeSinceLoanDue = timeSinceLoan - 5;
			data.setCreditScore(Math.max(0, data.getInitialLoanCreditScore() - (timeSinceLoanDue * timeSinceLoanDue * timeSinceLoanDue) / 200.0));
		}
	}
}