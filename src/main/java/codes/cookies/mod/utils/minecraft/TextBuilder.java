package codes.cookies.mod.utils.minecraft;

import java.util.List;

import codes.cookies.mod.utils.accessors.ClickEventAccessor;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextBuilder {

	private final MutableText text;
	private Style style;

	public TextBuilder(String text) {
		this(Text.literal(text));
	}

	public TextBuilder(Text text) {
		this.text = text.copy();
		this.style = text.getStyle();
	}

	public TextBuilder setRunnable(Runnable runnable) {
		final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ":C");
		ClickEventAccessor.setRunnable(clickEvent, runnable);
		this.style = this.style.withClickEvent(clickEvent);
		return this;
	}

	public TextBuilder append(Text text) {
		this.text.append(text);
		return this;
	}

	public TextBuilder append(String text) {
		this.text.append(text);
		return this;
	}

	public TextBuilder onHover(List<Text> event) {
		MutableText text = Text.empty();
		for (int i = 0; i < event.size(); i++) {
			Text line = event.get(i);
			text.append(line);
			if (i != event.size() - 1) {
				text.append(Text.of("\n"));
			}
		}

		return onHover(text);
	}

	public TextBuilder onHover(Text event) {
		this.style = this.style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, event));
		return this;
	}

	public TextBuilder onHover(String event) {
		return onHover(Text.literal(event));
	}

	public TextBuilder formatted(Formatting... formatting) {
		this.style = this.style.withFormatting(formatting);
		return this;
	}

	public MutableText build() {
		this.text.setStyle(this.style);
		return text;
	}

}
