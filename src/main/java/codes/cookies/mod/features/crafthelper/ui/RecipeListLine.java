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
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.screen.inventory.ForgeRecipeScreen;
import codes.cookies.mod.utils.ColorUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import codes.cookies.mod.utils.maths.MathUtils;
import codes.cookies.mod.utils.minecraft.NonCacheMutableText;
import codes.cookies.mod.utils.minecraft.SupplierTextContent;
import codes.cookies.mod.utils.minecraft.TextBuilder;
import codes.cookies.mod.utils.skyblock.ForgeUtils;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
	private final List<RecipeListLine> recipeChildren = new ArrayList<>();
	protected final int depth;
	@Setter
	private boolean collapsed = false;
	private ItemTracker itemTracker = new ItemTracker(ItemSources.values());
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
			parent.recipeChildren.add(this);
		}

		this.depth = depth;
	}

	private final CraftHelperComponent lineComponent = new CraftHelperComponent(1, 0) {
		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			if (parent != null) {
				if (parent.parent == null && parent.recipeChildren.indexOf(RecipeListLine.this) != parent.recipeChildren.size() - 1) {
					context.fill(RenderLayer.getGui(), x + 1, y - 1, x + 3, y + MinecraftClient.getInstance().textRenderer.fontHeight + 1, Colors.LIGHT_GRAY);
					//|
				}

				var depth = parent.depth * 8;
				var childCount = recursiveChildrenCount() + 1;

				if (parent.recipeChildren.indexOf(RecipeListLine.this) == parent.recipeChildren.size() - 1) {
					context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + 4, Colors.LIGHT_GRAY);
					//L
				} else if ((childCount > 2 || parent.recipeChildren.size() > 1) && !collapsed) {
					context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + (10 * childCount) - 1, Colors.LIGHT_GRAY);
					//|
				}

				if (collapsed) {
					if (parent.recipeChildren.size() > 1 && parent.recipeChildren.indexOf(RecipeListLine.this) != parent.recipeChildren.size() - 1) {
						context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + 10, Colors.LIGHT_GRAY);
						//|
					} else {
						context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + 4, Colors.LIGHT_GRAY);
						//L
					}
				}

				context.drawHorizontalLine(RenderLayer.getGui(), depth + x + 1, depth + x + 5, y + 4, Colors.LIGHT_GRAY);
			}
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

				/*var sourcesText = new TextBuilder("");

				var itemSources = itemTracker.get(ingredient.getRepositoryItem()).getUsedSources(getTargetAmount());
				var sources = itemSources.stream().map(Pair::getLeft).toList();
				if (!itemSources.isEmpty()) {
					var hover = getItemSourcesHoverText(itemTracker.getAmount(ingredient.getRepositoryItem()), sources);

					if (sources.contains(ItemSources.CHESTS)) {
						sourcesText.append("\uD83E\uDDF0 ");
					}
					if (sources.contains(ItemSources.FORGE)) {
						final OptionalLong lastForgeStarted = itemTracker
								.get(ingredient.getRepositoryItem())
								.getLastForgeStarted();
						sourcesText.append("(");
						final TextBuilder forgeTime = getForgeTime(lastForgeStarted);
						forgeTime.onHover(hover).setRunnable(RecipeListLine.this::onClick).formatted(Formatting.DARK_GRAY);
						sourcesText.append(forgeTime.build());
						sourcesText.append(") ");
					}
					sourcesText.onHover(hover);
					text.append(sourcesText.build());
				}*/

				if (!recipeChildren.isEmpty() && getState() != State.CRAFTED) {
					if (collapsed) {
						text.append(collapsedText);
					} else {
						text.append(unCollapsedText);
					}
				}

				setText(text.asOrderedText(), true);
				super.render(context, mouseX, mouseY, delta);
			}

			private MutableText getItemSourcesHoverText(int amount, List<ItemSources> itemSources) {
				var hover = new ArrayList<Text>();

				final ItemTracker.TrackedItem trackedItem = itemTracker.get(ingredient.getRepositoryItem());

				final List<Pair<ItemSources, Integer>> usedSources = trackedItem.getUsedSources(amount);
				hover.add(Text.literal("Item Sources").formatted(Formatting.GREEN));
				for (Pair<ItemSources, Integer> usedSource : usedSources) {
					ItemSources sources = usedSource.getLeft();
					int usedAmount = usedSource.getRight();
					hover.add(sources.getName()
							.copy()
							.formatted(Formatting.GRAY)
							.append(": ")
							.append(Text.literal(MathUtils.NUMBER_FORMAT.format(usedAmount))
									.formatted(Formatting.YELLOW)));
				}

				hover.add(Text.empty());

				if (itemSources.contains(ItemSources.FORGE)) {
					final List<ForgeItemSource.Context> allForgeStart = itemTracker
							.get(ingredient.getRepositoryItem())
							.getAllForgeStart();
					hover.add(Text.literal("Forge Slots").formatted(Formatting.GREEN));
					for (ForgeItemSource.Context context : allForgeStart) {
						final TextBuilder forgeTime = getForgeTime(OptionalLong.of(context.startTime()));
						hover.add(new NonCacheMutableText(Text.literal("Slot #%s: ".formatted(context.slot() + 1)).formatted(Formatting.GRAY).append(forgeTime.build().formatted(Formatting.DARK_GRAY))));
					}

					hover.add(Text.empty());
				}

				var finalText = Text.empty();
				for (int i = 0; i < hover.size(); i++) {
					Text line = hover.get(i);
					finalText.append(line);
					if (i != hover.size() - 1) {
						finalText.append(Text.literal("\n"));
					}
				}

				return finalText;
			}

			private @NotNull TextBuilder getForgeTime(OptionalLong lastForgeStarted) {
				TextBuilder forgeTime;
				if (lastForgeStarted.isEmpty()) {
					forgeTime = new TextBuilder(Text.literal("Unknown"));
				} else {
					final Supplier<String> supplier = getSupplier(
							() -> ForgeUtils.getForgeTime(ingredient.getRepositoryItem()),
							lastForgeStarted.getAsLong());
					final SupplierTextContent supplierTextContent = new SupplierTextContent(supplier);
					final NonCacheMutableText nonCacheMutableText = new NonCacheMutableText(MutableText.of(
							supplierTextContent));
					forgeTime = new TextBuilder(nonCacheMutableText);
				}
				return forgeTime;
			}

			private static Supplier<String> getSupplier(Supplier<Long> forgeTime, long lastForgeStartedSeconds) {
				return () -> {
					final long time = forgeTime.get();
					if (time == -1) {
						return "unknown";
					}
					final long delta = (System.currentTimeMillis() / 1000) - lastForgeStartedSeconds;
					int remaining = (int) (time - delta);
					if (remaining <= 0) {
						return "Done";
					}

					return CookiesUtils.formattedMs(remaining * 1000L);
				};
			}
		});
	}

	@Override
	public void update() {
		children().clear();
		itemTracker = new ItemTracker(ItemSources.values());
		addComponents();
	}

	private enum State {
		CRAFTED,
		CRAFTABLE_THROUGH_CHILDREN,
		NOT_CRAFTABLE
	}

	private State getState() {
		if (this.getAmount() >= getTargetAmount()) {
			return State.CRAFTED;
		} else if (recipeChildren.isEmpty()) {
			return State.NOT_CRAFTABLE;
		} else {
			if (recipeChildren.stream().allMatch(recipeListLine -> recipeListLine.getAmount() >= recipeListLine.getTargetAmount())) {
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

	private int recursiveChildrenCount() {
		int count = 0;
		for (RecipeListLine recipeChild : recipeChildren) {
			count += recipeChild.recursiveChildrenCount();
		}
		return count + recipeChildren.size();
	}

	private void onClick() {
		CookiesUtils.sendMessage("t");
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
