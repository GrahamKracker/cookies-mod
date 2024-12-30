package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.features.crafthelper.CraftHelperItem;

import codes.cookies.mod.features.crafthelper.CraftHelperManager;

import codes.cookies.mod.features.crafthelper.ui.components.SpacerComponent;
import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;

import codes.cookies.mod.utils.cookies.CookiesUtils;

import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftHelperPanel extends ContainerWidget implements Element, Selectable {
	@Getter
	private final List<CraftHelperPanelLine> lines = new ArrayList<>();

	private final CraftHelperPanelLine Spacer = new CraftHelperPanelLine() {{
		addChildren(new SpacerComponent(15, MinecraftClient.getInstance().textRenderer.fontHeight));
	}};

	private boolean focused;

	public CraftHelperPanel(int height, int width) {
		super(0, 0, width, height, Text.of(""));
		createWidgets(CraftHelperManager.getItems().peek());
	}

	public void createWidgets(CraftHelperItem item) {
		lines.clear();
		addLine(CraftHelperPanelLine.createHeader(item, new CraftHelperPanelLine(), this));

		addLine(Spacer);
	}

	public void addLine(CraftHelperPanelLine line) {
		lines.add(line);
		this.height = 14 + lines.stream().mapToInt(CraftHelperPanelLine::getHeight).sum() + 14;
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
		context.getMatrices().translate(0, 0, 100);
		context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("tooltip/background"), getX(), getY(), this.width, totalHeight);
		context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("tooltip/frame"), getX(), getY(), this.width, totalHeight);
		context.getMatrices().pop();

		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 200);

		AtomicInteger widgetY = new AtomicInteger(getY() + 14);
		lines.forEach(line -> {
			line.setX(this.getX() + 15);
			line.setY(widgetY.getAndAdd(line.getHeight() + 1));
			line.setWidth(this.width);
			line.render(context, mouseX, mouseY, delta);
		});

		context.getMatrices().pop();
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
}
