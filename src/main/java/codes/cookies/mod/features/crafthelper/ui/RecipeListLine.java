package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.crafthelper.ItemTracker;
import codes.cookies.mod.features.crafthelper.ui.components.SpacerComponent;

import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.utils.ColorUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import codes.cookies.mod.utils.minecraft.TextBuilder;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

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
		return Math.min(itemTracker.getAmount(ingredient.getRepositoryItem()) + getParentAmount(), ingredient.getAmount());
	}

	private int getParentAmount() {
		if (parent == null) {
			return 0;
		}
		return (int) (((float) parent.getAmount() / parent.ingredient.getAmount()) * ingredient.getAmount());
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
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

	public RecipeListLine(RecipeListLine parent, int depth, Ingredient ingredient) {
		this.parent = parent;
		this.ingredient = ingredient;

		if (parent != null) {
			parent.recipeChildren.add(this);
		}

		this.depth = depth;
	}

	private void addComponents() {
		this.addChildren(new CraftHelperComponent(1, 0) {
			@Override
			public void render(DrawContext context, int mouseX, int mouseY, float delta) {
				if (parent != null) {
					if (parent.parent == null & parent.recipeChildren.indexOf(RecipeListLine.this) != parent.recipeChildren.size() - 1) {
						context.fill(RenderLayer.getGui(), x + 1, y - 1, x + 3, y + MinecraftClient.getInstance().textRenderer.fontHeight + 1, Colors.LIGHT_GRAY);
					}
					var depth = parent.depth * 8;
					var childCount = recursiveChildrenCount() + 1;
					if (parent.recipeChildren.indexOf(RecipeListLine.this) == parent.recipeChildren.size() - 1 || collapsed) {
						context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + 4, Colors.LIGHT_GRAY);
					} else if (childCount > 2 || parent.recipeChildren.size() > 1) {
						context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + (10 * childCount) - 1, Colors.LIGHT_GRAY);
					} else {
						context.fill(RenderLayer.getGui(), depth + x + 1, y - 1, depth + x + 3, y + (4 * childCount), Colors.LIGHT_GRAY);
					}

					context.drawHorizontalLine(RenderLayer.getGui(), depth + x + 1, depth + x + 5, y + 4, Colors.LIGHT_GRAY);
				}
			}
		}, new SpacerComponent(depth * 8, 0));

		var text = Text.literal("").append(getIcon()).append(Text.literal(" (" + getAmount() + "/" + ingredient.getAmount() + ") ").withColor(getColor(getAmount(), ingredient.getAmount())).append(ingredient.getRepositoryItem().getFormattedName()));
		this.addChildren(new TextComponent(new TextBuilder(text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHoverText())))).setRunnable(this::toggleCollapse).build()));
	}

	private Text getHoverText() {
		List<MutableText> hoverText = new ArrayList<>();
		hoverText.add(Text.literal("").append(ingredient.getRepositoryItem().getFormattedName()));
		hoverText.add(Text.literal("\n"));
		var itemSources = itemTracker.get(ingredient.getRepositoryItem()).getUsedSources(ingredient.getAmount());
		if (!itemSources.isEmpty()) {
			hoverText.add(Text.literal("Item Sources").formatted(Formatting.GREEN));
			itemSources.forEach(pair -> {
				hoverText.add(Text.literal(pair.getLeft().getName().getString()).formatted(Formatting.GRAY).append(": ").append(Text.literal(pair.getRight().toString()).formatted(Formatting.YELLOW)));
			});
		}

		return hoverText.stream().reduce((mutableText, text) -> mutableText.append(text.append("\n"))).orElse(Text.literal(""));
	}

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
		if (this.getAmount() >= ingredient.getAmount()) {
			return State.CRAFTED;
		} else if (recipeChildren.isEmpty()) {
			return State.NOT_CRAFTABLE;
		} else {
			if (recipeChildren.stream().allMatch(recipeListLine -> recipeListLine.getAmount() >= recipeListLine.ingredient.getAmount())) {
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

	private void toggleCollapse() {
		collapsed = !collapsed;
	}
}
