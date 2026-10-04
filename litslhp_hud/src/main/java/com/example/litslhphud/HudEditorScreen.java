package com.example.litslhphud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private TextFieldWidget money;
    private TextFieldWidget coins;
    private TextFieldWidget kills;
    private TextFieldWidget deaths;
    private TextFieldWidget rank;
    private TextFieldWidget team;

    public HudEditorScreen() {
        super(Text.literal("LitslSMP HUD Editor"));
    }

    @Override
    protected void init() {
        int panelX = this.width / 2 - 190;
        int leftX = panelX + 25;
        int rightX = panelX + 215;
        int top = this.height / 2 - 105;

        money = field(leftX, top + 30, LitslHpHudClient.CONFIG.money);
        coins = field(leftX, top + 80, Integer.toString(LitslHpHudClient.CONFIG.coins));
        kills = field(leftX, top + 130, Integer.toString(LitslHpHudClient.CONFIG.kills));
        deaths = field(rightX, top + 30, Integer.toString(LitslHpHudClient.CONFIG.deaths));
        rank = field(rightX, top + 80, LitslHpHudClient.CONFIG.pvpRank);
        team = field(rightX, top + 130, LitslHpHudClient.CONFIG.team);

        addDrawableChild(money);
        addDrawableChild(coins);
        addDrawableChild(kills);
        addDrawableChild(deaths);
        addDrawableChild(rank);
        addDrawableChild(team);

        addDrawableChild(ButtonWidget.builder(Text.literal(LitslHpHudClient.CONFIG.enabled ? "HUD: ON" : "HUD: OFF"), b -> {
            LitslHpHudClient.CONFIG.enabled = !LitslHpHudClient.CONFIG.enabled;
            b.setMessage(Text.literal(LitslHpHudClient.CONFIG.enabled ? "HUD: ON" : "HUD: OFF"));
            LitslHpHudClient.CONFIG.save();
        }).dimensions(panelX + 25, top + 172, 120, 22).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            LitslHpHudClient.CONFIG.reset();
            LitslHpHudClient.CONFIG.save();
            reloadValues();
        }).dimensions(panelX + 155, top + 172, 90, 22).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> saveAndClose())
                .dimensions(panelX + 255, top + 172, 120, 22).build());
    }

    private TextFieldWidget field(int x, int y, String value) {
        TextFieldWidget f = new TextFieldWidget(this.textRenderer, x, y, 155, 22, Text.literal(""));
        f.setMaxLength(32);
        f.setText(value);
        return f;
    }

    private void reloadValues() {
        money.setText(LitslHpHudClient.CONFIG.money);
        coins.setText(Integer.toString(LitslHpHudClient.CONFIG.coins));
        kills.setText(Integer.toString(LitslHpHudClient.CONFIG.kills));
        deaths.setText(Integer.toString(LitslHpHudClient.CONFIG.deaths));
        rank.setText(LitslHpHudClient.CONFIG.pvpRank);
        team.setText(LitslHpHudClient.CONFIG.team);
    }

    private void saveAndClose() {
        LitslHpHudClient.CONFIG.money = money.getText().trim();
        LitslHpHudClient.CONFIG.coins = parseInt(coins.getText(), LitslHpHudClient.CONFIG.coins);
        LitslHpHudClient.CONFIG.kills = parseInt(kills.getText(), LitslHpHudClient.CONFIG.kills);
        LitslHpHudClient.CONFIG.deaths = parseInt(deaths.getText(), LitslHpHudClient.CONFIG.deaths);
        LitslHpHudClient.CONFIG.pvpRank = rank.getText().trim();
        LitslHpHudClient.CONFIG.team = team.getText().trim();
        LitslHpHudClient.CONFIG.save();
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

        int panelX = this.width / 2 - 190;
        int panelY = this.height / 2 - 125;
        int panelW = 380;
        int panelH = 220;

        context.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, 0xFF101014);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF18181E);
        context.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFF35FF35);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int panelX = this.width / 2 - 190;
        int panelY = this.height / 2 - 125;

        context.drawTextWithShadow(this.textRenderer, Text.literal("LitslSMP HUD Editor"), panelX + 20, panelY + 15, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Client-side display settings"), panelX + 20, panelY + 27, 0xFF88888F);

        label(context, "Money", panelX + 25, panelY + 52);
        label(context, "Coins", panelX + 25, panelY + 102);
        label(context, "Kills", panelX + 25, panelY + 152);
        label(context, "Deaths", panelX + 215, panelY + 52);
        label(context, "PvP Rank", panelX + 215, panelY + 102);
        label(context, "Team", panelX + 215, panelY + 152);
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
