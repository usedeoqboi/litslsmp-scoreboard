package com.example.litslhphud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HudConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Path.of("config", "litslhp_hud.json");

    public boolean enabled = true;
    public String name = "";   // empty = use your real username
    public String ping = "";   // empty = use your real ping
    public String money = "687.47M";
    public int coins = 37;
    public int kills = 105;
    public int deaths = 11;
    public String pvpRank = "Silver";
    public String team = "larp";
    public String footer = "store.litsl.net";

    public static HudConfig load() {
        HudConfig config = new HudConfig();
        try {
            if (Files.exists(FILE)) {
                JsonObject root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8)).getAsJsonObject();
                if (root.has("enabled")) config.enabled = root.get("enabled").getAsBoolean();
                if (root.has("name")) config.name = root.get("name").getAsString();
                if (root.has("ping")) config.ping = root.get("ping").getAsString();
                if (root.has("money")) config.money = root.get("money").getAsString();
                if (root.has("coins")) config.coins = root.get("coins").getAsInt();
                if (root.has("kills")) config.kills = root.get("kills").getAsInt();
                if (root.has("deaths")) config.deaths = root.get("deaths").getAsInt();
                if (root.has("pvpRank")) config.pvpRank = root.get("pvpRank").getAsString();
                if (root.has("team")) config.team = root.get("team").getAsString();
                if (root.has("footer")) config.footer = root.get("footer").getAsString();
            }
        } catch (Exception ignored) {
            // Keep defaults if the file is invalid.
        }
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("enabled", enabled);
            root.addProperty("name", name);
            root.addProperty("ping", ping);
            root.addProperty("money", money);
            root.addProperty("coins", coins);
            root.addProperty("kills", kills);
            root.addProperty("deaths", deaths);
            root.addProperty("pvpRank", pvpRank);
            root.addProperty("team", team);
            root.addProperty("footer", footer);
            Files.writeString(FILE, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public void reset() {
        enabled = true;
        name = "";
        ping = "";
        money = "687.47M";
        coins = 37;
        kills = 105;
        deaths = 11;
        pvpRank = "Silver";
        team = "larp";
        footer = "store.litsl.net";
    }
}
