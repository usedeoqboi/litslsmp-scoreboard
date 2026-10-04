package com.example.litslhphud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class LitslHpHudClient implements ClientModInitializer {
    public static HudConfig CONFIG;
    private static KeyBinding openEditor;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @Override
    public void onInitializeClient() {
        CONFIG = HudConfig.load();

        openEditor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.litslhp_hud.open_editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.litslhp_hud"
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

        int x = 6;
        int y = 2;
        int white = 0xFFE8E8E8;
        int gray = 0xFFAAAAAA;
        int green = 0xFF35FF35;
        int yellow = 0xFFFFFF35;
        int red = 0xFFFF3030;
        int orange = 0xFFFF9A24;
        int cyan = 0xFF31FFB0;
        int blue = 0xFF4A72FF;

        String name = client.player.getName().getString();
        int ping = 0;
        if (client.getNetworkHandler() != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) ping = Math.max(0, entry.getLatency());
        }

        ctx.drawTextWithShadow(client.textRenderer, Text.literal("LitslHP"), x + 70, y, green);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("▣"), x + 23, y + 18, gray);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(LocalDate.now().format(DATE)), x + 44, y + 18, gray);

        ctx.drawTextWithShadow(client.textRenderer, Text.literal(name), x, y + 40, green);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(" (" + ping + "ms)"), x + client.textRenderer.getWidth(name), y + 40, gray);

        int row = y + 58;
        drawRow(ctx, "$", "Money: ", CONFIG.money, x, row, green, white);
        drawRow(ctx, "✦", "Coins: ", Integer.toString(CONFIG.coins), x, row + 20, yellow, white);
        drawRow(ctx, "⚔", "Kills: ", Integer.toString(CONFIG.kills), x, row + 40, red, white);
        drawRow(ctx, "☠", "Deaths: ", Integer.toString(CONFIG.deaths), x, row + 60, orange, white);
        drawRow(ctx, "◆", "PvP Rank: ", CONFIG.pvpRank, x, row + 80, cyan, white);
        drawRow(ctx, "♦", "Team: ", CONFIG.team, x, row + 100, blue, white);

        int footerY = row + 128;
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("[$]"), x, footerY, green);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(CONFIG.footer), x + 31, footerY, gray);
    }

    private static void drawRow(DrawContext ctx, String icon, String label, String value, int x, int y, int iconColor, int valueColor) {
        MinecraftClient client = MinecraftClient.getInstance();
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(icon), x, y, iconColor);
        int textX = x + 19;
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(label), textX, y, 0xFFF0F0F0);
        int valueX = textX + client.textRenderer.getWidth(label);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(value), valueX, y, valueColor);
    }
}
