package dev.morazzer.cookies.mod.features.dungeons;

import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.packets.DungeonSyncPlayerLocation;
import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomIdPacket;
import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomSecrets;
import dev.morazzer.cookies.mod.events.IslandChangeEvent;
import dev.morazzer.cookies.mod.events.ScoreboardUpdateEvent;
import dev.morazzer.cookies.mod.events.WebsocketEvent;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonMapRenderer;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonPhase;
import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;

import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import net.minecraft.text.Text;

/**
 * All listeners that are used for dungeon features, these will always be invoked on the currently active dungeon
 * instance. So this is more or less a type of middleware.
 */
public class DungeonListeners {

	private static int ticks = 0;

	/**
	 * Registers all listeners.
	 */
	public static void initialize() {
		ScoreboardUpdateEvent.EVENT.register(DungeonListeners::updateScoreboard);
		ClientTickEvents.END_CLIENT_TICK.register(DungeonListeners::clientTick);
		HudRenderCallback.EVENT.register(DungeonListeners::hudRenderCallback);
		IslandChangeEvent.EVENT.register(DungeonListeners::onIslandChange);
		Packet.onReceive(DungeonUpdateRoomIdPacket.class, DungeonListeners::updateRoomId);
		Packet.onReceive(DungeonSyncPlayerLocation.class, DungeonListeners::syncPlayerLocation);
		Packet.onReceive(DungeonUpdateRoomSecrets.class, DungeonListeners::updateRoomSecrets);
		ClientReceiveMessageEvents.GAME.register(DungeonListeners::receiveGameMessage);
		WebsocketEvent.CONNECT.register(DungeonListeners::connectWebsocket);
	}

	private static void connectWebsocket() {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}
		instance.subscribe();
	}

	private static void syncPlayerLocation(DungeonSyncPlayerLocation packet) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}
		instance.updatePlayer(packet);
	}

	private static void receiveGameMessage(Text text, boolean isOverlay) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}
		final String string = text.getString();
		if (isOverlay) {
			if (!string.endsWith("Secrets")) {
				instance.processSecrets(null);
				return;
			}
			final String s = string.replaceAll("(?i)§[a-f0-9]", "").replaceAll(".* (\\d+/\\d+ Secrets).*", "$1");
			final String[] split = s.split(" ");
			instance.processSecrets(split[0]);
		} else {
			if (string.replaceAll("§[a-f0-9lmnopr]", "").contains("> EXTRA STATS <")) {
				instance.setPhase(DungeonPhase.AFTER);
			}
		}
	}

	private static void onIslandChange(LocationUtils.Island previous, LocationUtils.Island current) {
		if (current == LocationUtils.Island.CATACOMBS) {
			DungeonFeatures.getInstance().startDungeon(LocationUtils.getLastServer());
		} else {
			DungeonFeatures.getInstance().exitDungeon();
		}
	}

	private static void updateRoomId(DungeonUpdateRoomIdPacket packet) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}

		instance.getDungeonMap().setRoom(packet.roomMapX, packet.roomMapY, instance.getRoom(packet.roomIdentifier));
	}

	private static void updateRoomSecrets(DungeonUpdateRoomSecrets packet) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}

		final DungeonRoom roomAt = instance.getDungeonMap().getRoomAt(packet.roomMapX, packet.roomMapY);
		if (roomAt != null) {
			if (roomAt.getMaxSecrets() < packet.maxSecrets) {
				roomAt.setMaxSecrets(packet.maxSecrets);
			}
			roomAt.setCollectedSecrets(packet.collectedSecrets);
		}
	}


	private static void hudRenderCallback(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}
		instance.updatePlayersFromWorld();
		final DungeonMapRenderer mapRenderer = instance.getMapRenderer();
		if (mapRenderer == null) {
			return;
		}
		mapRenderer.render(drawContext);
	}

	private static void clientTick(MinecraftClient minecraftClient) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}

		ticks++;
		if (ticks % 20 == 0) {
			ticks = 0;
		}

		instance.updatePlayers();
		if (ticks % 5 == 0) {
			instance.periodicalTicks5();
		}
		if (ticks % 2 == 0) {
			instance.syncPlayers();
		}
	}

	private static void updateScoreboard(int line, String string) {
		final DungeonInstance instance = getInstance();
		if (instance == null) {
			return;
		}
		if (string.contains(instance.serverId().replace("mini", "m"))) {
			instance.onServerLineUpdate(string);
		}
	}

	/**
	 * @return The current dungeon instance or null if empty.
	 */
	private static DungeonInstance getInstance() {
		return DungeonFeatures.getInstance().getCurrentInstance();
	}
}