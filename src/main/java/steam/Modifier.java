package steam;

import java.util.ArrayList;
import java.util.List;

public class Modifier {

    public static String removeRangersPLTag(String nickname) {
        ArrayList<String> patterns = new ArrayList<>(List.of("<RangersPL>", "<rRangersPL>"));
        for (String pattern : patterns) {
            if (nickname.contains(pattern)) {
                nickname = nickname.replace(pattern, "");
            }
        }
        return nickname;
    }
}
