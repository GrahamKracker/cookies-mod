package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.features.crafthelper.CraftHelperItem;
import codes.cookies.mod.features.crafthelper.CraftHelperManager;
import codes.cookies.mod.features.crafthelper.CraftHelperPlacementScreen;
import codes.cookies.mod.features.crafthelper.ui.components.GroupedComponent;
import codes.cookies.mod.features.crafthelper.ui.components.ItemStackComponent;
import codes.cookies.mod.features.crafthelper.ui.components.ScrollableTextComponent;
import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.minecraft.TextBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftHelperPanelLine extends ContainerWidget {
	public static CraftHelperPanelLine createHeader(CraftHelperItem item, CraftHelperPanelLine line, CraftHelperPanel parent) {
		var stack = item.getRepositoryItem().constructItemStack();
		line.addChildren(new ItemStackComponent(stack).withRightOffset(5));

		var moveLeftComponent = new TextComponent(Text.literal("<")).withRightOffset(5);
		var scrollableNameComponent = new ScrollableTextComponent(Text.of(item.getRepositoryItem().getFormattedName()), 95).withRightOffset(5);
		var moveRightComponent = new TextComponent(Text.literal(">")).withRightOffset(5);
		var centerGroup = new GroupedComponent(moveLeftComponent, scrollableNameComponent, moveRightComponent);
		line.addChildren(centerGroup);

		centerGroup.setLeftOffset(((parent.getWidth() - centerGroup.width) / 2) - centerGroup.x - 15);
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
				.onHover(Text.literal("Close").formatted(Formatting.RED))
				.build()).withLeftOffset(2);
		var rightGroup = new GroupedComponent(moveButton, closeButton);

		line.addChildren(rightGroup);
		rightGroup.setLeftOffset(parent.getWidth() - line.getWidth() - rightGroup.width - 10);
		line.updateChildren();

		return line;
	}

	private final List<CraftHelperComponent> children = new ArrayList<>();

	public CraftHelperPanelLine(CraftHelperComponent... children) {
		super(0, 0, 0, 0, Text.of(""));
		addChildren(children);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		updateChildren();
		children.forEach(child -> {
			child.render(context, mouseX, mouseY, delta);
		});
	}

	private void updateChildren() {
		AtomicInteger xOffset = new AtomicInteger(getX());
		children.forEach(child -> {
			child.setX(xOffset.addAndGet(child.getLeftOffset()));
			xOffset.addAndGet(child.getWidth() - child.getLeftOffset());
			child.setY(this.getY());
		});
		this.height = this.children.stream().mapToInt(CraftHelperComponent::getHeight).max().orElse(0);
		this.width = this.children.stream().mapToInt(CraftHelperComponent::getWidth).sum();
	}

	public void addChildren(CraftHelperComponent... children) {
		this.children.addAll(List.of(children));
		updateChildren();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public List<CraftHelperComponent> children() {
		return children;
	}
}
