package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import codes.cookies.mod.screen.CookiesScreen;
import codes.cookies.mod.utils.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemStackComponent extends CraftHelperComponent {
	private final ItemStack stack;

	public ItemStackComponent(ItemStack stack) {
		super(26, 26);
		this.stack = stack;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		RenderUtils.renderBackgroundBox(context, x, y, 26, 26, -1);

		context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("cookies-mod", "textures/gui/slot.png"), x + 4, y + 4, 0, 0, 18, 18, 18, 18);
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, -100);
		context.drawItem(stack, x + 5, y + 5, (int) System.currentTimeMillis());
		context.drawStackOverlay(textRenderer, stack, x + 1, y + 1);
		if (CookiesScreen.isInBound(mouseX, mouseY, x, y, 26, 26)) {
			context.drawItemTooltip(textRenderer, stack, mouseX, mouseY);
		}
		context.getMatrices().pop();
	}
}
