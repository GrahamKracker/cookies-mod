package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class MultiLineComponent extends GroupedComponent {
	protected final List<CraftHelperComponent> children = new ArrayList<>();

	public MultiLineComponent(CraftHelperComponent... children) {
		super(children);
	}

	@Override
	protected void recalculate() {
		this.width = children.stream().mapToInt(CraftHelperComponent::getWidth).max().orElse(0);
		this.height = children.stream().mapToInt(CraftHelperComponent::getHeight).sum();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int x = getX();
		int y = getY();
		for (CraftHelperComponent child : children) {
			x += child.getLeftOffset();
			child.setX(x);
			if (child instanceof NewLineComponent) {
				x = getX();
				y += child.getHeight();
				continue;
			}
			child.setY(getY());
			child.render(context, mouseX, mouseY, delta);
			x += child.getWidth() + child.getRightOffset() - child.getLeftOffset();
		}
	}

	public static class NewLineComponent extends CraftHelperComponent {
		public NewLineComponent(int height) {
			super(0, height);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		}
	}
}
