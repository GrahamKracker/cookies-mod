package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import codes.cookies.mod.utils.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class ScrollableTextComponent extends CraftHelperComponent {
	private final Text text;

	public ScrollableTextComponent(Text text, int width) {
		super(width, MinecraftClient.getInstance().textRenderer.fontHeight);
		this.text = text;
		this.width = Math.min(width, MinecraftClient.getInstance().textRenderer.getWidth(text));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		RenderUtils.drawScrollableText(context, MinecraftClient.getInstance().textRenderer, this.text, x, y - 1, x + width, y + MinecraftClient.getInstance().textRenderer.fontHeight, Colors.WHITE);
	}
}
