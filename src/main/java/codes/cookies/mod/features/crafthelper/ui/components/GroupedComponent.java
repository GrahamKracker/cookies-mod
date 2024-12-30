package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedComponent extends CraftHelperComponent {

	private final List<CraftHelperComponent> children = new ArrayList<>();

	public GroupedComponent(CraftHelperComponent... children) {
		super(0, 0);

		this.children.addAll(List.of(children));
		this.recalculate();
	}

	protected void recalculate() {
		this.width = children.stream().mapToInt(CraftHelperComponent::getWidth).sum();
		this.height = children.stream().mapToInt(CraftHelperComponent::getHeight).max().orElse(0);
		this.rightOffset = children.stream().mapToInt(CraftHelperComponent::getRightOffset).sum();
		this.leftOffset = children.stream().mapToInt(CraftHelperComponent::getLeftOffset).sum();
	}

	public void add(CraftHelperComponent... component) {
		children.addAll(Arrays.stream(component).toList());
		recalculate();
	}

	public void remove(CraftHelperComponent component) {
		children.remove(component);
		recalculate();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int x = getX();
		for (CraftHelperComponent child : children) {
			x += child.getLeftOffset();
			child.setX(x);
			child.setY(getY());
			child.render(context, mouseX, mouseY, delta);
			x += child.getWidth() + child.getRightOffset() - child.getLeftOffset();
		}
	}
}
