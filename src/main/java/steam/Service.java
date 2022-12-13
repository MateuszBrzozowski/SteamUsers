package steam;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static steam.Validator.removeRangersPLTag;
import static steam.constant.ApiConstant.API_KEY;
import static steam.constant.ApiConstant.APP_ID_SQUAD;

public class Service {
    private static final String SEPARATOR = "==========================================";
    private HttpURLConnection connection;
    private final List<User> users = new ArrayList<>();

    /**
     * Read file "resources.cvs",
     * Download hours for Users from Steam API,
     * Save data (userName, hours) to file "usersHours.csv"
     */
    public void getUserHoursAndExportToFile() {
        LocalTime timeStart = LocalTime.now();
        try {
            readUsersList();
            System.out.println("[INFO] - Pobieranie godzin");
            getUsersHours();
            System.out.println("[INFO] - Pobrano godziny");
            System.out.println(SEPARATOR);
            saveDataToFile();
        } catch (Exception ignored) {
        }
        LocalTime timeStop = LocalTime.now();
        Duration between = Duration.between(timeStart, timeStop);
        System.out.println("Czas wykonania: " + between);
    }


    /**
     * Reading and create User from file data.
     * File structure - 4 columns split by ','. Export and delivered from Google Docs.
     * (tag, name, type, steamId)
     */
    private void readUsersList() {
        String fileName = "resources.csv";
        System.out.println("[INFO] - Wczytywanie użytkowników z pliku - " + fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                addUserFromData(data);
            }
            reader.close();
            System.out.println("[INFO] - Wczytano " + users.size() + " użytkowników");
            System.out.println(SEPARATOR);
        } catch (IOException e) {
            System.err.println("[ERROR] - " + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Create user from file data.
     *
     * @param data Line of user data from file.
     */
    private void addUserFromData(String[] data) {
        if (data != null) {
            String nicknameWithoutTag = removeRangersPLTag(data[1]);
            String steamId = Validator.isSteamId(data[3]);
            if (steamId != null) {
                User user = new User(nicknameWithoutTag, steamId);
                users.add(user);
            } else {
                System.out.print("[WARN] - Pominięta linia - (");
                for (String datum : data) {
                    System.out.print(" " + datum);
                }
                System.out.println(")");
            }
        }
    }


    /**
     * Saving user to file "usersHours.csv"
     */
    private void saveDataToFile() {
        System.out.println("[INFO] - Zapisywanie danych do pliku");
        try {
            String fileName = "usersHours.csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (User user : users) {
                writer.write(user.getName() + "," + user.getHours() + "\n");
            }
            writer.close();
            System.out.println("[INFO] - Zapisano do pliku - " + fileName);
        } catch (IOException e) {
            System.err.println("[ERROR] - Błąd zapisu - " + e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Getting hours for all users
     */
    private void getUsersHours() {
        for (User user : users) {
            getHoursFromJSON(user);
        }
    }

    /**
     * Connect with Steam API, download data, setting hours for User
     *
     * @param user To which we set data.
     */
    private void getHoursFromJSON(@NotNull User user) {
        String responseContent = getResponseContent(user);
        if (responseContent == null) {
            System.err.println("[INFO] - " + user.getName() + " - Nie można pobrać godzin.");
            return;
        }
        JSONObject jsonObject = new JSONObject(responseContent);
        System.out.print("[INFO] - " + user.getName() + " - ");
        try {
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray games = response.getJSONArray("games");
            for (int i = 0; i < games.length(); i++) {
                JSONObject jsonGame = games.getJSONObject(i);
                int appId = jsonGame.getInt("appid");
                if (appId == APP_ID_SQUAD) {
                    int playtimeForever = jsonGame.getInt("playtime_forever");
                    user.setPlaytimeForever(playtimeForever);
                }
            }
        } catch (JSONException ignored) {
        }
        System.out.println("Pobrano");
    }

    /**
     * Getting content from steam API for {@link User}
     *
     * @param user {@link User} To which we download content.
     * @return String content from API.
     */
    private @Nullable String getResponseContent(@NotNull User user) {
        String urlString = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" + API_KEY + "&steamid=" + user.getSteamId() + "&format=json";
        StringBuilder responseContent = new StringBuilder();
        URL url = getUrl(urlString);
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int status = connection.getResponseCode();

            if (status == 200) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                System.err.println("[WARN] - API Steam powered - BRAK DOSTĘPU.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }
        return responseContent.toString();
    }

    /**
     * Convert String url to instance of {@link URL}
     *
     * @param url String of url
     * @return {@link URL}
     */
    private URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
