package steam;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static String removeRangersPLTag(String nickname) {
        ArrayList<String> patterns = new ArrayList<>(List.of("<RangersPL>", "<rRangersPL>"));
        for (String pattern : patterns) {
            if (nickname.contains(pattern)) {
                nickname = nickname.replace(pattern, "");
            }
        }
        return nickname;
    }

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
