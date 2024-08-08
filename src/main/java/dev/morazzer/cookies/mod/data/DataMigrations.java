package dev.morazzer.cookies.mod.data;

import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.data.profile.migrations.ProfileDataMigration_0001;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration handler for the profile data.
 */
public class DataMigrations {
    private static final String KEY = "migration";
    private static final List<Migration<JsonObject>> MIGRATIONS = new LinkedList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger("cookies-mod/migrations");

    static {
        MIGRATIONS.add(new ProfileDataMigration_0001());

        MIGRATIONS.stream()
            .collect(Collectors.groupingBy(Migration::getType))
            .forEach((key, value) -> key.setLatest(value.stream()
                .max(Comparator.comparingInt(Migration::getNumber))
                .map(Migration::getNumber)
                .orElse(-1)));
    }

    private DataMigrations() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Applies all missing migrations to the {@linkplain JsonObject}.
     *
     * @param jsonObject The config object.
     * @param type       The type of migration to use.
     */
    public static void migrate(final JsonObject jsonObject, Migration.Type type) {
        if (!jsonObject.has(KEY)) {
            jsonObject.addProperty(KEY, -1);
        }

        final long lastApplied = jsonObject.get(KEY).getAsLong();
        for (final Migration<JsonObject> migration : MIGRATIONS.stream()
            .sorted(Comparator.comparingLong(Migration::getNumber))
            .toList()) {
            if (migration.getType() != type) {
                continue;
            }
            if (migration.getNumber() > lastApplied) {
                LOGGER.info("Applying migration {} for type {}", migration.getNumber(), type);
                migration.apply(jsonObject);
            }
        }
    }

    /**
     * Writes the latest migration number to the {@linkplain JsonObject}.
     *
     * @param jsonObject The config object.
     */
    public static void writeLatest(final JsonObject jsonObject, Migration.Type type) {
        jsonObject.addProperty(KEY, type.getLatest());
    }

}
