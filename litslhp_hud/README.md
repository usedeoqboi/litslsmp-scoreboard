# LitslHP HUD — Minecraft 1.21.11 Fabric

Client-side HUD editor based on the supplied LitslHP screenshot.

## Features
- Editable Money
- Editable Coins
- Editable Kills
- Editable Deaths
- Editable PvP Rank
- Editable Team name
- HUD on/off toggle
- Reset to screenshot defaults
- Persistent local config in `config/litslhp_hud.json`
- Right Shift opens the editor
- Uses Fabric's `HudElementRegistry` for Minecraft 1.21.11
- Client-side only; it does not modify server-side economy/stat data

## Target
- Minecraft Java Edition 1.21.11
- Fabric
- Java 21
- Fabric API 0.141.6+1.21.11
- Yarn 1.21.11+build.6
- Fabric Loader 0.19.3

Fabric's documentation notes that 1.21.11 is the last obfuscated Minecraft release and recommends the newer HUD API rather than the deprecated `HudRenderCallback`.

## Build
1. Install JDK 21.
2. Open this folder in IntelliJ IDEA or another Java/Gradle IDE.
3. Run `gradle build` (or import the project and run the Gradle `build` task).
4. The remapped mod JAR is produced in `build/libs/`.

Install the resulting JAR in `.minecraft/mods` together with Fabric API for 1.21.11.
