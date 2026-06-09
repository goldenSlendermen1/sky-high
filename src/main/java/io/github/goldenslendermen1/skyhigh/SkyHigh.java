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
package io.github.goldenslendermen1.skyhigh;

import io.github.goldenslendermen1.skyhigh.world.data.BankStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyHigh implements ModInitializer {
	public static final String MOD_ID = "sky-high";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private long lastDay = -1;

	@Override
	public void onInitialize() {
		Components.initialize();
		Items.initialize();
		ItemGroups.initialize();

		CommandRegistrationCallback.EVENT.register(Commands::register);

		ServerPlayerEvents.JOIN.register(BankStorage::onPlayerJoin);
		ServerPlayerEvents.LEAVE.register(BankStorage::onPlayerLeave);

		ServerWorldEvents.LOAD.register(BankStorage::onWorldLoad);
		ServerWorldEvents.UNLOAD.register(BankStorage::onWorldUnload);

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			long time = world.getTimeOfDay();
			long day = time / 24000L;

			if (day == lastDay)
				return;

			lastDay = day;
			onNewDay(world);
		});
	}

	@SuppressWarnings("unused")
    private void onNewDay(ServerWorld world){
		BankStorage.forEach(bankData -> {
			bankData.setCreditScore(creditScore -> Math.min(100.0, creditScore + Math.pow(bankData.getSavings(), 5.0)));
			bankData.setSavings(savings -> savings * 1.106);

			if (!bankData.hasLoan())
				return;

			long loanStartTime = bankData.getLoanStartTime();
			long timeSinceLoan = lastDay - loanStartTime;

			if (timeSinceLoan <= 5)
				return;

			long timeSinceLoanDue = timeSinceLoan - 5;
			bankData.setCreditScore(Math.max(0, bankData.getInitialLoanCreditScore() - (timeSinceLoanDue * timeSinceLoanDue * timeSinceLoanDue) / 200.0));
		});
	}
}