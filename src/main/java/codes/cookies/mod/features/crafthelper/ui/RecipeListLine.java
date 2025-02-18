package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.items.sources.ForgeItemSource;
import codes.cookies.mod.features.crafthelper.ItemTracker;
import codes.cookies.mod.features.crafthelper.ui.components.SpacerComponent;

import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.CraftRecipe;
import codes.cookies.mod.repository.recipes.ForgeRecipe;
import codes.cookies.mod.screen.inventory.ForgeRecipeScreen;
import codes.cookies.mod.utils.ColorUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import codes.cookies.mod.utils.maths.MathUtils;
import codes.cookies.mod.utils.minecraft.NonCacheMutableText;
import codes.cookies.mod.utils.minecraft.SupplierTextContent;
import codes.cookies.mod.utils.minecraft.TextBuilder;
import codes.cookies.mod.utils.skyblock.ForgeUtils;
import com.mojang.logging.LogUtils;
import lombok.Setter;

import lombok.extern.java.Log;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

public class RecipeListLine extends CraftHelperPanelLine {
	private final RecipeListLine parent;
	private final List<RecipeListLine> directChildren = new ArrayList<>();
	protected final int depth;
	@Setter
	private boolean collapsed = false;
	private final ItemTracker itemTracker = new ItemTracker(ItemSources.values());
	private final Ingredient ingredient;
	private boolean parentCollapsed;

	public int getAmount() {
		return Math.min(itemTracker.getAmount(ingredient.getRepositoryItem()), ingredient.getAmount());
	}

	public int getTargetAmount() { //todo: have the parent amounts cascade down
		return ingredient.getAmount();
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.getAmount() >= getTargetAmount()) {
			this.collapsed = true;
		}
		if ((parent != null && (parent.collapsed || parent.parentCollapsed))) {
			this.parentCollapsed = true;
		} else {
			this.parentCollapsed = false;
			super.renderWidget(context, mouseX, mouseY, delta);
		}
	}

	@Override
	public int getHeight() {
		if ((parent != null && (parent.collapsed || parent.parentCollapsed))) {
			return 0;
		}
		return super.getHeight();
	}

	public RecipeListLine(RecipeListLine parent, int depth, Ingredient ingredient, int multiplier) {
		this.parent = parent;
		this.ingredient = ingredient.multiply(multiplier);

		if (parent != null) {
			parent.directChildren.add(this);
		}

		this.depth = depth;
		addComponents();
	}

	private final CraftHelperComponent lineComponent = new CraftHelperComponent(1, 0) {
		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			if (parent != null) {
				var depth = parent.depth * 8;
				if (parent.directChildren.indexOf(RecipeListLine.this) == parent.directChildren.size() - 1) {
					drawVerticalLine(context, depth + x + 1, y - 1, Colors.LIGHT_GRAY, 2, 5);
				} else {
					drawVerticalLine(context, depth + x + 1, y - 1, Colors.LIGHT_GRAY, 2, getHeightIncludingChildren(RecipeListLine.this) + 1);
				}

				context.drawHorizontalLine(RenderLayer.getGui(), depth + x + 1, depth + x + 5, y + 4, Colors.LIGHT_GRAY);
			}
		}

		public int getHeightIncludingChildren(RecipeListLine recipeChild) {
			int height = 10;
			for (RecipeListLine child : recipeChild.directChildren) {
				if (!child.parentCollapsed) {
					height += getHeightIncludingChildren(child);
				}
			}
			return height;
		}


		public void drawVerticalLine(DrawContext context, int x, int y, int color, int width, int height) {
			var y2 = y + height;
			if (y2 < y) {
				int i = y;
				y = y2;
				y2 = i;
			}

			context.fill(RenderLayer.getGui(), x, y + 1, x + width, y2, color);
		}
	};

	private void addComponents() {
		this.addChildren(lineComponent, new SpacerComponent(depth * 8, 0));

		var text = Text.empty().append(getIcon()).append(Text.literal(" " + getAmount() + "/" + getTargetAmount() + " ").withColor(getColor(getAmount(), getTargetAmount())).append(new TextBuilder(ingredient.getRepositoryItem().getFormattedName()).build()));
		this.addChildren(new TextComponent(new TextBuilder(text).setRunnable(this::onClick).build()));

		this.addChildren(new SpacerComponent(5, 0));
		
		this.addChildren(new TextComponent("") {
			public final Text unCollapsedText = new TextBuilder("▼  ").setRunnable(RecipeListLine.this::toggleCollapse)
					.onHover(Text.empty().append(ingredient.getRepositoryItem().getFormattedName()).append("\nClick to collapse!")
					)
					.formatted(Formatting.GRAY).build();
			public final Text collapsedText = new TextBuilder("◀  ").setRunnable(RecipeListLine.this::toggleCollapse)
					.onHover(Text.empty().append(ingredient.getRepositoryItem().getFormattedName()).append("\nClick to expand!"
					)).formatted(Formatting.GRAY).build();

			@Override
			public void render(DrawContext context, int mouseX, int mouseY, float delta) {
				var text = Text.empty();

				if (!directChildren.isEmpty() && getState() != State.CRAFTED) {
					if (collapsed) {
						text.append(collapsedText);
					} else {
						text.append(unCollapsedText);
					}
				}

				setText(text.asOrderedText(), true);
				super.render(context, mouseX, mouseY, delta);
			}
		});
	}

	@Override
	public void update() {
		//children().clear();
		//itemTracker = new ItemTracker(ItemSources.values());
		//addComponents();
		//super.update();
	}

	private enum State {
		CRAFTED,
		CRAFTABLE_THROUGH_CHILDREN,
		NOT_CRAFTABLE
	}

	private State getState() {
		if (this.getAmount() >= getTargetAmount()) {
			return State.CRAFTED;
		} else if (directChildren.isEmpty()) {
			return State.NOT_CRAFTABLE;
		} else {
			if (directChildren.stream().allMatch(recipeListLine -> recipeListLine.getAmount() >= recipeListLine.getTargetAmount())) {
				return State.CRAFTABLE_THROUGH_CHILDREN;
			} else {
				return State.NOT_CRAFTABLE;
			}
		}
	}

	private Text getIcon() {
		switch (getState()) {
			case CRAFTED:
				if (this.parent == null) {
					return Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.GREEN);
				} else {
					return Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN, Formatting.BOLD);
				}
			case CRAFTABLE_THROUGH_CHILDREN:
				if (this.parent == null) {
					return Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.YELLOW);
				} else {
					return Text.literal(Constants.Emojis.WARNING).formatted(Formatting.YELLOW);
				}
			case NOT_CRAFTABLE:
				if (this.parent == null) {
					return Text.literal(Constants.Emojis.FLAG_EMPTY).formatted(Formatting.RED);
				} else {
					return Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD);
				}
			default:
				CookiesUtils.sendFailedMessage("Unknown state");
				throw new IllegalStateException("Unknown state");
		}
	}

	private int getColor(int amount, int required) {
		final double percentage = (double) amount / required;
		return ColorUtils.calculateBetween(
				Formatting.RED.getColorValue(),
				Formatting.GREEN.getColorValue(),
				percentage);
	}

	private void onClick() {
		final RepositoryItem repositoryItem = this.ingredient.getRepositoryItem();
		if (repositoryItem.getRecipes().stream().anyMatch(CraftRecipe.class::isInstance)) {
			CookiesUtils.sendCommand("viewrecipe " + repositoryItem.getInternalId());
			Optional.ofNullable(MinecraftClient.getInstance().currentScreen).ifPresent(Screen::close);
		} else if (repositoryItem.getRecipes().stream().anyMatch(ForgeRecipe.class::isInstance)) {
			CookiesMod.openScreen(new ForgeRecipeScreen(repositoryItem.getRecipes()
					.stream()
					.filter(ForgeRecipe.class::isInstance)
					.map(ForgeRecipe.class::cast)
					.findFirst()
					.orElseThrow(), null));
		}
	}

	private void toggleCollapse() {
		collapsed = !collapsed;
	}
}
