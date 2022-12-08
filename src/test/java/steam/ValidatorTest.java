package steam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidatorTest {

    @Test
    void removeRangersPLTag_RemoveRangersPL() {
        String nickname = "nickname<RangersPL>";
        String result = Validator.removeRangersPLTag(nickname);
        Assertions.assertEquals("nickname", result);
    }

    @Test
    void removeRangersPLTag_RemoveRRangersPL() {
        String nickname = "nickname<rRangersPL>";
        String result = Validator.removeRangersPLTag(nickname);
        Assertions.assertEquals("nickname", result);
    }

    @Test
    void removeRangersPLTag_NothingRemoved() {
        String nickname = "nicknamerRangersPL>";
        String result = Validator.removeRangersPLTag(nickname);
        Assertions.assertEquals("nicknamerRangersPL>", result);
    }
}