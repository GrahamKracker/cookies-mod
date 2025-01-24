package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import codes.cookies.mod.screen.CookiesScreen;

import codes.cookies.mod.utils.accessors.ClickEventAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.Optional;

public class TextComponent extends CraftHelperComponent {
	private OrderedText text;

	protected void setText(OrderedText text, boolean updateWidth) {
		this.text = text;
		if (updateWidth) {
			this.width = getTextRenderer().getWidth(text);
		}
	}

	public TextComponent(Text text) {
		super(getTextRenderer().getWidth(text), MinecraftClient.getInstance().textRenderer.fontHeight);
		this.text = text.asOrderedText();
	}

	public TextComponent(String text) {
		this(Text.of(text));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawText(getTextRenderer(), text, x, y, Colors.WHITE, true);

		final Style styleAt = MinecraftClient.getInstance().textRenderer.getTextHandler()
				.getStyleAt(text, x - mouseX);

		if (styleAt == null) {
			return;
		}

		if (CookiesScreen.isInBound(mouseX, mouseY, x + (x - mouseX), y, width * 2, height)) {
			context.drawHoverEvent(MinecraftClient.getInstance().textRenderer, styleAt, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		final Style styleAt = MinecraftClient.getInstance().textRenderer.getTextHandler()
				.getStyleAt(text, (int) (x - mouseX));

		if (styleAt == null || styleAt.isEmpty()) {
			return super.mouseClicked(mouseX, mouseY, button);
		}

		if (styleAt.getClickEvent() != null) {
			if (CookiesScreen.isInBound((int) mouseX, (int) mouseY, (int) (x + (x - mouseX)), y, width * 2, height)) {
				final Optional<Runnable> runnable = ClickEventAccessor.getRunnable(styleAt.getClickEvent());
				if (runnable.isPresent()) {
					runnable.ifPresent(Runnable::run);
					return true;
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
