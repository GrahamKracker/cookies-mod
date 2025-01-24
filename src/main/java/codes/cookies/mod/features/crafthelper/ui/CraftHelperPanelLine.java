package codes.cookies.mod.features.crafthelper.ui;

import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.features.crafthelper.CraftHelperItem;
import codes.cookies.mod.features.crafthelper.CraftHelperManager;
import codes.cookies.mod.features.crafthelper.CraftHelperPlacementScreen;
import codes.cookies.mod.features.crafthelper.ItemTracker;
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
	private final List<CraftHelperComponent> children = new ArrayList<>();

	public CraftHelperPanelLine(CraftHelperComponent... children) {
		super(0, 0, 0, 0, Text.of(""));
		addChildren(children);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		for (CraftHelperComponent child : children) {
			child.render(context, mouseX, mouseY, delta);
		}
	}

	public void updateChildren() {
		AtomicInteger xOffset = new AtomicInteger(getX());
		for (CraftHelperComponent child : children) {
			child.setX(xOffset.addAndGet(child.getLeftOffset()));
			xOffset.addAndGet(child.getWidth() - child.getLeftOffset());
			xOffset.addAndGet(child.getRightOffset());
			child.setY(this.getY());
		}
		this.height = this.children.stream().mapToInt(CraftHelperComponent::getHeight).max().orElse(0);
		this.width = this.children.stream().mapToInt(CraftHelperComponent::getWidth).sum();
	}

	public void addChildren(CraftHelperComponent... children) {
		this.children.addAll(List.of(children));
		updateChildren();
	}

	public void update() {
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
