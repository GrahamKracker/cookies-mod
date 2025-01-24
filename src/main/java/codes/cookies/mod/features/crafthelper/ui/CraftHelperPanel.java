package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.features.crafthelper.CraftHelperItem;

import codes.cookies.mod.features.crafthelper.CraftHelperManager;

import codes.cookies.mod.features.crafthelper.CraftHelperPlacementScreen;
import codes.cookies.mod.features.crafthelper.ui.components.GroupedComponent;
import codes.cookies.mod.features.crafthelper.ui.components.ItemStackComponent;
import codes.cookies.mod.features.crafthelper.ui.components.ScrollableTextComponent;
import codes.cookies.mod.features.crafthelper.ui.components.SpacerComponent;

import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.minecraft.TextBuilder;
import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftHelperPanel extends ContainerWidget implements Element, Selectable {
	@Getter
	private final List<CraftHelperPanelLine> lines = new ArrayList<>();

	public final CraftHelperPanelLine Spacer = new CraftHelperPanelLine() {{
		addChildren(new SpacerComponent(15, MinecraftClient.getInstance().textRenderer.fontHeight));
	}};

	private boolean focused;

	public CraftHelperPanel(int width) {
		super(0, 0, width, 0, Text.of(""));
		var item = CraftHelperManager.getCurrentItem();
		if (item != null) {
			createHeader(item);

			addLine(Spacer);
		}
	}

	public void addLine(CraftHelperPanelLine line) {
		lines.add(line);
		line.setWidth(this.width);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		setY(getY() + scrollDelta);
		int totalHeight = 14;
		for (CraftHelperPanelLine craftHelperPanelLine : lines) {
			int line1Height = craftHelperPanelLine.getHeight();
			if (line1Height == 0) {
				continue;
			}
			totalHeight += line1Height + 1;
		}
		totalHeight += 14;

		context.getMatrices().push();
		{
			context.getMatrices().translate(0, 0, 400);
			context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("tooltip/background"), getX(), getY(), this.width, totalHeight);
			context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("tooltip/frame"), getX(), getY(), this.width, totalHeight);

			context.getMatrices().push();
			{
				context.getMatrices().translate(0, 0, 200);

				AtomicInteger widgetY = new AtomicInteger(getY() + 14);
				for (CraftHelperPanelLine line : lines) {
					line.setX(this.getX() + 15);
					var lineHeight = line.getHeight();
					line.setY(widgetY.getAndAdd(lineHeight == 0 ? 0 : lineHeight + 1));
					line.renderWidget(context, mouseX, mouseY, delta);
				}

				context.getMatrices().pop();
			}
			context.getMatrices().pop();
		}
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public List<? extends Element> children() {
		return lines;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public SelectionType getType() {
		if (isFocused()) {
			return SelectionType.FOCUSED;
		}
		if (isMouseOver(MinecraftClient.getInstance().mouse.getX(), MinecraftClient.getInstance().mouse.getY())) {
			return SelectionType.HOVERED;
		}
		return SelectionType.NONE;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= getX() + width;
	}

	private int scrollDelta = -100;

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (!isMouseOver(mouseX, mouseY)) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		scrollDelta += 2 * (int) verticalAmount;
		return true;
	}

	public void createHeader(CraftHelperItem item) {
		var line = new CraftHelperPanelLine();

		var stack = item.getRepositoryItem().constructItemStack();

		line.addChildren(new ItemStackComponent(stack).withRightOffset(5));

		var moveLeftComponent = new TextComponent(new TextBuilder("<").build()).withRightOffset(5);

		var scrollableNameComponent = new ScrollableTextComponent(Text.of(item.getRepositoryItem().getFormattedName()), 95).withRightOffset(5);
		var moveRightComponent = new TextComponent(new TextBuilder(">").build());
		var centerGroup = new GroupedComponent(moveLeftComponent, scrollableNameComponent, moveRightComponent);
		line.addChildren(centerGroup);

		centerGroup.setLeftOffset(((this.getWidth() - centerGroup.width) / 2) - centerGroup.x - 15);
		line.updateChildren();

		var moveButton = new TextComponent(new TextBuilder(Constants.Emojis.MOVE)
				.setRunnable(() -> {
					if (MinecraftClient.getInstance().currentScreen instanceof CraftHelperPlacementScreen) {
						return;
					}

					MinecraftClient.getInstance().setScreen(new CraftHelperPlacementScreen());
				})
				.formatted(Formatting.AQUA).build().styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to move").formatted(Formatting.AQUA)))));

		var closeButton = new TextComponent(new TextBuilder(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD)
				.setRunnable(CraftHelperManager::close)
				.onHover(Text.literal("Click to hide").formatted(Formatting.RED))
				.build()).withLeftOffset(4);
		var rightGroup = new GroupedComponent(moveButton, closeButton);

		line.addChildren(rightGroup);
		rightGroup.setLeftOffset(this.getWidth() - line.getWidth() - rightGroup.width - 20);
		line.updateChildren();

		addLine(line);
	}
}
