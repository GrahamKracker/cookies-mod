package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.screen.CookiesScreen;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.Getter;

import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

@Getter
@Setter
public abstract class CraftHelperComponent implements Element, Selectable, Drawable {
	protected int height;

	protected int x;
	protected int y;
	protected int width;
	private boolean focused;

	public int getWidth() {
		return leftOffset + width + rightOffset;
	}

	protected int leftOffset = 0;
	protected int rightOffset = 0;

	protected static TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}

	public CraftHelperComponent(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public CraftHelperComponent withLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
		return this;
	}

	public CraftHelperComponent withRightOffset(int rightOffset) {
		this.rightOffset = rightOffset;
		return this;
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
		return null;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return CookiesScreen.isInBound((int) mouseX, (int) mouseY, x, y, width, height);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return Element.super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}
}
