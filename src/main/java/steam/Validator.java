package steam;

import org.jetbrains.annotations.Nullable;

public class Validator {

    public static @Nullable String isSteamId(String source) {
        if (source == null || source.length() != 17) {
            return null;
        }
        try {
            Long.parseLong(source);
            return source;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
