package com.example.litslhphud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class LitslHpHudClient implements ClientModInitializer {
    public static HudConfig CONFIG;
    private static KeyBinding openEditor;
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of("litslhp_hud", "main"));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFAAAAAA;
    private static final int GREEN = 0xFF55FF55;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int RED = 0xFFFF5555;
    private static final int ORANGE = 0xFFFFAA00;
    private static final int MINT = 0xFF55FFAA;
    private static final int BLUE = 0xFF5577FF;

    // Pixel-art icons ('#' = pixel). Drawn with fill() so no textures are needed.
    private static final String[] ICON_MONEY = {
            "...#...",
            ".#####.",
            "##.#...",
            ".####..",
            "...####",
            "...#.##",
            ".#####.",
            "...#..."
    };
    private static final String[] ICON_COINS = {
            "#..#..#",
            ".#.#.#.",
            "..###..",
            "#######",
            "..###..",
            ".#.#.#.",
            "#..#..#"
    };
    private static final String[] ICON_SWORD = {
            "......#",
            ".....##",
            "....##.",
            "...##..",
            "#.##...",
            ".##....",
            ".##....",
            "#..#..."
    };
    private static final String[] ICON_SKULL = {
            ".#####.",
            "#######",
            "##.#.##",
            "#######",
            ".#####.",
            "..###..",
            "..#.#.."
    };
    private static final String[] ICON_SHIELD = {
            ".#####.",
            "#######",
            "#######",
            "#######",
            "#######",
            ".#####.",
            "..###..",
            "...#..."
    };
    private static final String[] ICON_CLOCK = {
            ".#####.",
            "#.....#",
            "#..#..#",
            "#..####",
            "#.....#",
            "#.....#",
            ".#####."
    };

    @Override
    public void onInitializeClient() {
        CONFIG = HudConfig.load();

        openEditor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litslhp_hud.open_editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditor.wasPressed()) {
                client.setScreen(new HudEditorScreen());
            }
        });

        // Hide the server's real sidebar scoreboard while the fake HUD is on
        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD, previous -> (ctx, tickCounter) -> {
            if (!CONFIG.enabled) previous.render(ctx, tickCounter);
        });

        HudElementRegistry.addLast(Identifier.of("litslhp_hud", "hud"), LitslHpHudClient::renderHud);
    }

    private static int realPing(MinecraftClient client) {
        if (client.getNetworkHandler() != null && client.player != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) return Math.max(0, entry.getLatency());
        }
        return 0;
    }

    private static void drawIcon(DrawContext ctx, String[] rows, int x, int y, int color) {
        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            for (int c = 0; c < row.length(); c++) {
                if (row.charAt(c) == '#') {
                    ctx.fill(x + c, y + r, x + c + 1, y + r + 1, color);
                }
            }
        }
    }

    private static void renderHud(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!CONFIG.enabled || client.options.hudHidden || client.player == null) return;
        TextRenderer tr = client.textRenderer;

        String name = CONFIG.name.isBlank() ? client.player.getName().getString() : CONFIG.name;
        int ping = realPing(client);
        if (!CONFIG.ping.isBlank()) {
            try {
                ping = Math.max(0, Integer.parseInt(CONFIG.ping.trim()));
            } catch (NumberFormatException ignored) {
                // keep the real ping
            }
        }
        String pingText = " (" + ping + "ms)";

        String money = CONFIG.money.startsWith("$") ? CONFIG.money : "$" + CONFIG.money;

        String[][] icons = {ICON_MONEY, ICON_COINS, ICON_SWORD, ICON_SKULL, ICON_SWORD, ICON_SHIELD};
        String[] labels = {"Money: ", "Coins: ", "Kills: ", "Deaths: ", "Pvp Rank: ", "Team: "};
        String[] values = {money, Integer.toString(CONFIG.coins), Integer.toString(CONFIG.kills),
                Integer.toString(CONFIG.deaths), CONFIG.pvpRank, CONFIG.team};
        int[] colors = {GREEN, YELLOW, RED, ORANGE, MINT, BLUE};

        Text title = Text.literal("LitslSMP").formatted(Formatting.BOLD);
        Text footerTag = Text.literal("[$]").formatted(Formatting.BOLD);
        int tagW = tr.getWidth(footerTag);

        // Width of the panel
        int widest = 3 + tr.getWidth(name + pingText);
        widest = Math.max(widest, 21 + tr.getWidth("Oct 30, 2026"));
        widest = Math.max(widest, 3 + tagW + 4 + tr.getWidth(CONFIG.footer));
        for (int i = 0; i < labels.length; i++) {
            widest = Math.max(widest, 15 + tr.getWidth(labels[i] + values[i]));
        }
        int w = Math.max(widest + 2, tr.getWidth(title) + 10);
        int h = 111;

        // Same spot as the vanilla sidebar scoreboard: flush against the right edge, vertically centered
        int px = ctx.getScaledWindowWidth() - w;
        int py = ctx.getScaledWindowHeight() / 2 - (h * 2) / 3;

        int titleY = py + 3;
        int dateY = titleY + 10;
        int nameY = dateY + 17;
        int rowY = nameY + 9;
        int footerY = rowY + 9 * (labels.length - 1) + 16;

        // Background (same transparency as the vanilla scoreboard)
        ctx.fill(px, py, px + w, py + 14, 0x66000000);
        ctx.fill(px, py + 14, px + w, py + h, 0x4D000000);

        // Title (centered, bold)
        ctx.drawTextWithShadow(tr, title, px + (w - tr.getWidth(title)) / 2, titleY, GREEN);

        // Date
        drawIcon(ctx, ICON_CLOCK, px + 11, dateY, GRAY);
        ctx.drawTextWithShadow(tr, Text.literal(LocalDate.now().format(DATE)), px + 21, dateY, GRAY);

        // Name + ping
        ctx.drawTextWithShadow(tr, Text.literal(name), px + 3, nameY, GREEN);
        ctx.drawTextWithShadow(tr, Text.literal(pingText), px + 3 + tr.getWidth(name), nameY, GRAY);

        // Rows
        for (int i = 0; i < labels.length; i++) {
            int y = rowY + 9 * i;
            drawIcon(ctx, icons[i], px + 3, y, colors[i]);
            ctx.drawTextWithShadow(tr, Text.literal(labels[i]), px + 15, y, WHITE);
            ctx.drawTextWithShadow(tr, Text.literal(values[i]), px + 15 + tr.getWidth(labels[i]), y, colors[i]);
        }

        // Footer
        ctx.drawTextWithShadow(tr, footerTag, px + 3, footerY, GREEN);
        ctx.drawTextWithShadow(tr, Text.literal(CONFIG.footer), px + 3 + tagW + 4, footerY, GRAY);
    }
}
