package steam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private String steamId;
    private int playtimeForever; // minutes

    public User(String name, String steamId) {
        this.name = name;
        this.steamId = steamId;
    }

    public int getHours() {
        return playtimeForever / 60;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", steamId='" + steamId + '\'' +
                ", playtimeForever='" + getHours() + "h'" +
                '}';
    }
}
