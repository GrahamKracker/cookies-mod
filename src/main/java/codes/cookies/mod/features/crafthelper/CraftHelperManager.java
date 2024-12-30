package codes.cookies.mod.features.crafthelper;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.events.api.ScreenKeyEvents;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperPanel;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperPanelLine;
import codes.cookies.mod.features.crafthelper.ui.RecipeListLine;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.accessors.InventoryScreenAccessor;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.Getter;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.lang.ref.WeakReference;
import java.util.Stack;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftHelperManager {
	@Getter
	private static final Stack<CraftHelperItem> items = new Stack<>();
	@Getter
	private static CraftHelperLocation location;

	private static WeakReference<CraftHelperPanel> panelRef = new WeakReference<>(null);

	public static void init() {
		location = ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.getValue();
		ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.withCallback((oldValue, newValue) -> location = newValue);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof InventoryScreenAccessor inventoryScreenAccessor)) {
				return;
			}

			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			if (!ConfigManager.getConfig().helpersConfig.craftHelper.craftHelper.getValue()) {
				return;
			}

			if (items.isEmpty()) {
				//return;
				items.push(new CraftHelperItem(RepositoryItem.of("WAND_OF_RESTORATION"), 2)); //WAND_OF_RESTORATION
			}

			var panel = inventoryScreenAccessor.cookies$addDrawableChild(new CraftHelperPanel((screen.height - 166), calculateWidth(inventoryScreenAccessor)));
			for (var line : items.peek().getRecipeLines()) {
				panel.addLine(line);
			}
			panelRef = new WeakReference<>(panel);

			for (CraftHelperPanelLine line : panel.getLines()) {
				if (line instanceof RecipeListLine recipeListLine) {
					recipeListLine.update();
				}
			}

			ScreenEvents.beforeRender(screen).register((screen1, drawContext, i, i1, v) -> onRender(inventoryScreenAccessor, panel, i, i1, v));
			ScreenMouseEvents.allowMouseClick(screen).register(CraftHelperManager::onMouseClick);
			ScreenMouseEvents.allowMouseScroll(screen).register(CraftHelperManager::onMouseScroll);
			ScreenKeyboardEvents.allowKeyPress(screen).register(CraftHelperManager::onKeyPressed);
			ScreenKeyboardEvents.allowKeyRelease(screen).register(CraftHelperManager::onKeyReleased);
			ScreenKeyEvents.getExtension(screen).cookies$allowCharTyped().register(CraftHelperManager::onCharTyped);
			items.clear();
		});

		AtomicInteger ticksSinceLastUpdate = new AtomicInteger(0);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			var panel = panelRef.get();
			if (panel == null || ticksSinceLastUpdate.getAndIncrement() < 40) {
				return;
			}
			ticksSinceLastUpdate.set(0);
			panel.children().forEach(child -> {
				if (child instanceof RecipeListLine recipeListLine) {
					recipeListLine.update();
				}
			});
		});
	}

	public static void onRender(InventoryScreenAccessor inventoryScreenAccessor, CraftHelperPanel panel, int i, int i1, float v) {
		panel.setWidth(calculateWidth(inventoryScreenAccessor));
		var position = getPosition(inventoryScreenAccessor, panel);
		panel.setX(position.x());
		panel.setY(position.y());
	}

	private static int calculateRightEdge(InventoryScreenAccessor screen) {
		return screen.cookies$getBackgroundWidth() + screen.cookies$getX();
	}

	private static int calculateLeftEdge(InventoryScreenAccessor screen) {//todo special case for recipe book
		return (((Screen) screen).width - screen.cookies$getBackgroundWidth()) / 2;
	}

	private static int calculateWidth(InventoryScreenAccessor screen) {
		return switch (location) {
			case LEFT -> calculateLeftEdge(screen) - 3;
			case RIGHT -> MinecraftClient.getInstance().getWindow().getScaledWidth() - calculateRightEdge(screen) - 3;
		};
	}

	private static int calculateX(InventoryScreenAccessor screen) {
		return switch (location) {
			case LEFT -> 0;
			case RIGHT -> calculateRightEdge(screen);
		};
	}

	private static Vector2ic getPosition(InventoryScreenAccessor screen, CraftHelperPanel panel) {
		int x = calculateX(screen);
		int y = screen.cookies$getY() + screen.cookies$getBackgroundHeight() / 2;
		preventOverflow(MinecraftClient.getInstance().getWindow().getScaledHeight(), new Vector2i(x, y), panel.getHeight());
		return new Vector2i(x, y);
	}

	private static void preventOverflow(int screenHeight, Vector2i pos, int height) {
		int i = height + 3;
		if (pos.y + i > screenHeight) {
			pos.y = screenHeight - i;
		}
	}

	public static boolean onMouseClick(Screen screen, double v, double v1, int i) {
		return true;
	}

	public static boolean onMouseScroll(Screen screen, double v, double v1, double v2, double v3) {
		return true;
	}

	public static boolean onKeyPressed(Screen screen, int i, int i1, int i2) {
		return true;
	}

	public static boolean onKeyReleased(Screen screen, int i, int i1, int i2) {
		return true;
	}

	public static boolean onCharTyped(Screen screen, char c, int i) {
		return true;
	}

	public static void pushNewCraftHelperItem(CraftHelperItem item) {
		items.push(item);
	}

	public static void pushNewCraftHelperItem(RepositoryItem repositoryItemNotNull, int i) {
		//todo just cause im lazy
	}

	public static void close() {
	}
}
