package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import codes.cookies.mod.screen.CookiesScreen;

import codes.cookies.mod.utils.accessors.ClickEventAccessor;
import codes.cookies.mod.utils.accessors.HoverEventAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public class TextComponent extends CraftHelperComponent {
	protected OrderedText text;

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

		if (CookiesScreen.isInBound(mouseX, mouseY, x + (x - mouseX), y, width, height)) {
			if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS && styleAt.getClickEvent() != null) {
				final Optional<Runnable> runnable = ClickEventAccessor.getRunnable(styleAt.getClickEvent());
				if (runnable.isPresent()) {
					runnable.ifPresent(Runnable::run);
				}
			}
			if (styleAt.getHoverEvent() != null) {
				final Optional<List<Text>> hoverText = HoverEventAccessor.getText(styleAt.getHoverEvent());
				hoverText.ifPresent(texts -> context.drawTooltip(
						MinecraftClient.getInstance().textRenderer,
						texts.stream().map(Text::asOrderedText).toList(),
						HoveredTooltipPositioner.INSTANCE,
						mouseX,
						mouseY));
			}
		}

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		/*final Style styleAt = MinecraftClient.getInstance().textRenderer.getTextHandler()
				.getStyleAt(text, (int) (x - mouseX));

		LogUtils.getLogger().error("mouseClicked: " + (int) (x - mouseX));

		if (styleAt == null || styleAt.isEmpty()) {
			LogUtils.getLogger().error("mouseClicked empty");
			return super.mouseClicked(mouseX, mouseY, button);
		}

		if (styleAt.getClickEvent() != null) {
			LogUtils.getLogger().error("mouseClicked");
			if (CookiesScreen.isInBound((int) mouseX, (int) mouseY, (int) (x + (x - mouseX)), y, width, height)) {
				//if (CookiesScreen.isInBound((int) mouseX, (int) mouseY, x, y, width, height)) {
				final Optional<Runnable> runnable = ClickEventAccessor.getRunnable(styleAt.getClickEvent());
				if (runnable.isPresent()) {
					runnable.ifPresent(Runnable::run);
					return true;
				}
			}
		}*/ //todo ???

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
