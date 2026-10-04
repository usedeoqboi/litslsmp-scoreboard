package com.example.litslhphud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
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

        HudElementRegistry.addLast(Identifier.of("litslhp_hud", "hud"), LitslHpHudClient::renderHud);
    }

    private static void renderHud(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!CONFIG.enabled || client.options.hudHidden || client.player == null) return;
        TextRenderer tr = client.textRenderer;

        String name = client.player.getName().getString();
        int ping = 0;
        if (client.getNetworkHandler() != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) ping = Math.max(0, entry.getLatency());
        }
        String pingText = " (" + ping + "ms)";

        String money = CONFIG.money.startsWith("$") ? CONFIG.money : "$" + CONFIG.money;

        String[] icons = {"$", "\u2600", "\u2694", "\u2620", "\u25C6", "\u2666"};
        String[] labels = {"Money: ", "Coins: ", "Kills: ", "Deaths: ", "Pvp Rank: ", "Team: "};
        String[] values = {money, Integer.toString(CONFIG.coins), Integer.toString(CONFIG.kills),
                Integer.toString(CONFIG.deaths), CONFIG.pvpRank, CONFIG.team};
        int[] iconColors = {GREEN, YELLOW, RED, ORANGE, MINT, BLUE};
        int[] valueColors = {GREEN, YELLOW, RED, ORANGE, MINT, BLUE};

        Text title = Text.literal("LitslSMP").formatted(Formatting.BOLD);

        // Width of the panel
        int widest = 3 + tr.getWidth(name + pingText);
        widest = Math.max(widest, 22 + tr.getWidth("Oct 30, 2026"));
        widest = Math.max(widest, 3 + tr.getWidth("[$] " + CONFIG.footer));
        for (int i = 0; i < labels.length; i++) {
            widest = Math.max(widest, 15 + tr.getWidth(labels[i] + values[i]));
        }
        int w = Math.max(widest + 5, tr.getWidth(title) + 10);

        int px = 4;
        int py = 4;

        int titleY = py + 4;
        int dateY = titleY + 10;
        int nameY = dateY + 17;
        int rowY = nameY + 10;
        int footerY = rowY + 9 * labels.length - 9 + 15;
        int h = footerY + 9 + 4 - py;

        // Background
        ctx.fill(px, py, px + w, py + h, 0x99000000);

        // Title (centered, bold)
        ctx.drawTextWithShadow(tr, title, px + (w - tr.getWidth(title)) / 2, titleY, GREEN);

        // Date
        ctx.drawTextWithShadow(tr, Text.literal("\u25F7"), px + 11, dateY, GRAY);
        ctx.drawTextWithShadow(tr, Text.literal(LocalDate.now().format(DATE)), px + 22, dateY, GRAY);

        // Name + ping
        ctx.drawTextWithShadow(tr, Text.literal(name), px + 3, nameY, GREEN);
        ctx.drawTextWithShadow(tr, Text.literal(pingText), px + 3 + tr.getWidth(name), nameY, GRAY);

        // Rows
        for (int i = 0; i < labels.length; i++) {
            int y = rowY + 9 * i;
            ctx.drawTextWithShadow(tr, Text.literal(icons[i]), px + 3, y, iconColors[i]);
            ctx.drawTextWithShadow(tr, Text.literal(labels[i]), px + 15, y, WHITE);
            ctx.drawTextWithShadow(tr, Text.literal(values[i]), px + 15 + tr.getWidth(labels[i]), y, valueColors[i]);
        }

        // Footer
        ctx.drawTextWithShadow(tr, Text.literal("[$]"), px + 3, footerY, GREEN);
        ctx.drawTextWithShadow(tr, Text.literal(CONFIG.footer), px + 3 + tr.getWidth("[$] "), footerY, 0xFFDDDDDD);
    }
}
