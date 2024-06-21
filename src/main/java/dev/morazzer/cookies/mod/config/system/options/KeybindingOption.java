package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.KeybindingEditor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a keybinding in the config.
 */
@Slf4j
public class KeybindingOption extends Option<InputUtil.Key, KeybindingOption> {

    InputUtil.Key defaultKey;

    @SuppressWarnings("MissingJavadoc")
    public KeybindingOption(Text name, Text description, InputUtil.Key value) {
        super(name, description, value);
        this.defaultKey = value;
    }

    /**
     * Sets a default key that is different from the initial key.
     *
     * @param key The new default key.
     * @return The option.
     */
    public KeybindingOption withDefault(InputUtil.Key key) {
        this.defaultKey = key;
        return this;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (expectPrimitiveString(jsonElement, log)) {
            return;
        }
        this.value = InputUtil.fromTranslationKey(jsonElement.getAsString());
    }

    @Override
    public @NotNull JsonElement write() {
        return new JsonPrimitive(value.getTranslationKey());
    }

    @Override
    public @NotNull ConfigOptionEditor<InputUtil.Key, KeybindingOption> getEditor() {
        return new KeybindingEditor(this);
    }

}
