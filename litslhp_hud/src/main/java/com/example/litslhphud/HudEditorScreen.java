package com.example.litslhphud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private static final int PANEL_W = 380;
    private static final int PANEL_H = 212;

    private TextFieldWidget name;
    private TextFieldWidget ping;
    private TextFieldWidget money;
    private TextFieldWidget coins;
    private TextFieldWidget kills;
    private TextFieldWidget deaths;
    private TextFieldWidget rank;
    private TextFieldWidget team;
    private TextFieldWidget footer;

    public HudEditorScreen() {
        super(Text.literal("LitslSMP HUD Editor"));
    }

    private int panelX() { return this.width / 2 - PANEL_W / 2; }
    private int panelY() { return this.height / 2 - PANEL_H / 2; }

    // column x offsets and row y offsets (of the field, relative to the panel)
    private static int colX(int c) { return 15 + c * 120; }
    private static int fieldY(int r) { return 57 + r * 44; }

    @Override
    protected void init() {
        HudConfig c = LitslHpHudClient.CONFIG;
        int px = panelX();
        int py = panelY();

        name = field(px + colX(0), py + fieldY(0), c.name);
        ping = field(px + colX(1), py + fieldY(0), c.ping);
        money = field(px + colX(2), py + fieldY(0), c.money);
        coins = field(px + colX(0), py + fieldY(1), Integer.toString(c.coins));
        kills = field(px + colX(1), py + fieldY(1), Integer.toString(c.kills));
        deaths = field(px + colX(2), py + fieldY(1), Integer.toString(c.deaths));
        rank = field(px + colX(0), py + fieldY(2), c.pvpRank);
        team = field(px + colX(1), py + fieldY(2), c.team);
        footer = field(px + colX(2), py + fieldY(2), c.footer);

        addDrawableChild(name);
        addDrawableChild(ping);
        addDrawableChild(money);
        addDrawableChild(coins);
        addDrawableChild(kills);
        addDrawableChild(deaths);
        addDrawableChild(rank);
        addDrawableChild(team);
        addDrawableChild(footer);

        int by = py + 182;

        addDrawableChild(ButtonWidget.builder(Text.literal(c.enabled ? "HUD: ON" : "HUD: OFF"), b -> {
            LitslHpHudClient.CONFIG.enabled = !LitslHpHudClient.CONFIG.enabled;
            b.setMessage(Text.literal(LitslHpHudClient.CONFIG.enabled ? "HUD: ON" : "HUD: OFF"));
            LitslHpHudClient.CONFIG.save();
        }).dimensions(px + colX(0), by, 110, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            LitslHpHudClient.CONFIG.reset();
            LitslHpHudClient.CONFIG.save();
            reloadValues();
        }).dimensions(px + colX(1), by, 110, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> saveAndClose())
                .dimensions(px + colX(2), by, 110, 20).build());
    }

    private TextFieldWidget field(int x, int y, String value) {
        TextFieldWidget f = new TextFieldWidget(this.textRenderer, x, y, 110, 20, Text.literal(""));
        f.setMaxLength(32);
        f.setText(value);
        return f;
    }

    private void reloadValues() {
        HudConfig c = LitslHpHudClient.CONFIG;
        name.setText(c.name);
        ping.setText(c.ping);
        money.setText(c.money);
        coins.setText(Integer.toString(c.coins));
        kills.setText(Integer.toString(c.kills));
        deaths.setText(Integer.toString(c.deaths));
        rank.setText(c.pvpRank);
        team.setText(c.team);
        footer.setText(c.footer);
    }

    private void saveAndClose() {
        HudConfig c = LitslHpHudClient.CONFIG;
        c.name = name.getText().trim();
        c.ping = ping.getText().trim();
        c.money = money.getText().trim();
        c.coins = parseInt(coins.getText(), c.coins);
        c.kills = parseInt(kills.getText(), c.kills);
        c.deaths = parseInt(deaths.getText(), c.deaths);
        c.pvpRank = rank.getText().trim();
        c.team = team.getText().trim();
        c.footer = footer.getText().trim();
        c.save();
        close();
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    // The panel is drawn as part of the background. Screen.render() already calls
    // renderBackground once, and calling it a second time crashes in 1.21.x.
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

        int px = panelX();
        int py = panelY();
        context.fill(px - 2, py - 2, px + PANEL_W + 2, py + PANEL_H + 2, 0xFF101014);
        context.fill(px, py, px + PANEL_W, py + PANEL_H, 0xFF18181E);
        context.fill(px, py, px + PANEL_W, py + 3, 0xFF35FF35);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int px = panelX();
        int py = panelY();

        context.drawTextWithShadow(this.textRenderer, Text.literal("LitslSMP HUD Editor"), px + 15, py + 10, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Leave Name / Ping empty to use your real ones"), px + 15, py + 22, 0xFF88888F);

        label(context, "Name", px + colX(0), py + fieldY(0) - 11);
        label(context, "Ping (ms)", px + colX(1), py + fieldY(0) - 11);
        label(context, "Money", px + colX(2), py + fieldY(0) - 11);
        label(context, "Coins", px + colX(0), py + fieldY(1) - 11);
        label(context, "Kills", px + colX(1), py + fieldY(1) - 11);
        label(context, "Deaths", px + colX(2), py + fieldY(1) - 11);
        label(context, "PvP Rank", px + colX(0), py + fieldY(2) - 11);
        label(context, "Team", px + colX(1), py + fieldY(2) - 11);
        label(context, "Footer text", px + colX(2), py + fieldY(2) - 11);
    }

    private void label(DrawContext context, String text, int x, int y) {
        context.drawTextWithShadow(this.textRenderer, Text.literal(text), x, y, 0xFFD8D8DE);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(null);
    }
}
