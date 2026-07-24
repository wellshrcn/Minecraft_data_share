# Data Share Mod

Data Share Mod is a **client-only** Forge mod. It collects player, inventory, container, and status data that is locally visible to the Minecraft client, formats it as pretty JSON, and streams it to external software through a Windows named pipe.

The server does not need this mod. The mod only reads data already available on the client; it cannot read hidden server-side data that has not been synchronized to the client.

## Supported Versions

- Minecraft `1.20.1` + Forge `47.4.22`
  - Project: `mod\mc\dev-1.20.1`
  - Installed jar: `mod\mc\.minecraft\versions\1.20.1-Forge_47.4.22\mods\data_share-1.0.0.jar`
- Minecraft `1.21.1` + Forge `52.1.16`
  - Project: `mod\mc\dev-1.21.1`
  - Installed jar: `mod\mc\.minecraft\versions\1.21.1-Forge_52.1.16\mods\data_share-1.0.0.jar`
- Minecraft `1.21.4` + Forge `54.1.17`
  - Project: `mod\mc\dev-1.21.4`
  - Installed jar: `mod\mc\.minecraft\versions\1.21.4-Forge_54.1.17\mods\data_share-1.0.0.jar`
- Minecraft `1.21.11` + Forge `61.1.11`
  - Project: `mod\mc\dev-1.21.11`
  - Installed jar: `mod\mc\.minecraft\versions\1.21.11-Forge_61.1.11\mods\data_share-1.0.0.jar`

The four jars are version-specific and cannot be swapped between Minecraft versions.

## Pipe Protocol

- Pipe name: `\\.\pipe\data_share`
- Role: the mod creates and writes to the pipe; external software only connects and reads
- Encoding: UTF-8
- Format: pretty JSON; each snapshot is one multi-line JSON object
- Separator: one blank line between snapshots
- Frequency: about 10Hz, once every 2 game ticks
- Boolean convention: every yes/no field is numeric; `1` means true and `0` means false

External software should read the stream continuously and treat a blank line as the end of one JSON snapshot.

## Quick Viewer

Start Minecraft and enter a world first, then run:

```powershell
powershell -ExecutionPolicy Bypass -File "f:\_a\WELLS_HRToytoolbox\mod\mc\tools\read_data_share_pipe.ps1"
```

## Root Fields

- `schema`: JSON schema version. Currently `1`.
- `ts`: local timestamp when the snapshot was generated, in milliseconds.
- `pipe`: pipe identifier. Currently `data_share`.
- `mcVersion`: present in 1.21.x builds, identifies the Minecraft build used for the snapshot.
- `game`: client window, account, server, and screen state.
- `player`: player data. It is `null` when the client is not in a world.

## `game` Fields

- `fps`: current client FPS.
- `paused`: whether the game is paused.
- `inGame`: whether the client is currently inside a world or server.
- `windowWidth` / `windowHeight`: game window size in pixels.
- `guiScale`: current GUI scale.
- `accountName`: current logged-in account name.
- `accountUuid`: account UUID; present in the 1.20.1 build.
- `serverAddress`: current server address; `singleplayer` in single-player worlds.
- `serverName`: current server name; `singleplayer` in single-player worlds.
- `screen`: current Minecraft screen class name, or `null` when no screen is open.

## Basic `player` Fields

- `name`: player display name.
- `uuid`: player UUID.
- `id`: local entity ID.
- `pos.x/y/z`: exact player coordinates, including decimals.
- `blockPos.x/y/z`: integer block coordinates.
- `dimension`: current dimension.
- `biome`: biome at the player's current position.
- `dayTime`: current day time.
- `gameTime`: total world tick time.
- `difficulty`: current world difficulty.
- `raining`: whether it is raining.
- `thundering`: whether there is thunder.

## View and Motion Fields

- `yaw`: horizontal view angle.
- `pitch`: vertical view angle.
- `bodyYaw`: body rotation.
- `headYaw`: head rotation.
- `velocity.x/y/z`: current player velocity vector.

## Health, Survival, and XP Fields

- `health`: current health.
- `maxHealth`: maximum health.
- `absorption`: absorption shield health.
- `armor`: armor value.
- `food`: hunger level, usually 0 to 20.
- `saturation`: saturation level.
- `exhaustion`: exhaustion level; present in the 1.20.1 build.
- `air`: current air supply.
- `maxAir`: maximum air supply.
- `xpLevel`: experience level.
- `xpProgress`: progress within the current level, from 0 to 1.
- `xpTotal`: total experience points.
- `score`: player score.
- `gamemode`: game mode name, such as survival or creative.
- `gamemodeId`: numeric game mode ID.

## State 0/1 Fields

- `isAlive`: whether the player is alive.
- `isDeadOrDying`: whether the player is dead or dying.
- `isCrouching`: whether the player is visually crouching.
- `isShiftKeyDown`: whether the sneak key is down.
- `isSprinting`: whether the player is sprinting.
- `isSwimming`: whether the player is swimming.
- `isUnderWater`: whether the player is underwater.
- `isInWater`: whether the player is in water.
- `isInLava`: whether the player is in lava.
- `isOnGround`: whether the player is on the ground.
- `isOnFire`: whether the player is on fire.
- `isFallFlying`: whether the player is flying with elytra.
- `isSleeping`: whether the player is sleeping.
- `isPassenger`: whether the player is riding another entity.
- `isUsingItem`: whether the player is using an item.
- `isBlocking`: whether the player is blocking.
- `fallDistance`: accumulated fall distance.
- `hurtTime`: invulnerability/flashing timer after taking damage.
- `deathTime`: death timer.

## Ability Fields

- `flying`: whether the player is currently flying.
- `mayFly`: whether the player is allowed to fly.
- `instabuild`: whether the player has creative instant-build ability.
- `invulnerable`: whether the player is invulnerable.
- `mayBuild`: whether the player is allowed to build.
- `flyingSpeed`: flying speed.
- `walkingSpeed`: walking speed.

## Item Fields

These fields appear in `mainHand`, `offHand`, `armorSlots`, `inventory`, `openContainer.slots`, and similar places.

- `empty`: whether the slot is empty.
- `id`: item ID, such as `minecraft:diamond_sword`.
- `count`: item stack count.
- `damage`: current durability damage.
- `maxDamage`: maximum durability.
- `damageable`: whether the item has durability.
- `enchanted`: whether the item has the enchanted glint.
- `displayName`: item display name.
- `enchantments`: enchantment list; present in the 1.20.1 build.
- `nbt`: item NBT string; present in the 1.20.1 build.
- `components`: 1.21.x item component string, replacing most old-style item NBT/component details.

## Inventory and Equipment Fields

- `selectedSlot`: selected hotbar slot.
- `inventoryUsedSlots`: number of used inventory slots, meaning non-empty slots inside `inventory`.
- `mainHand`: item in the main hand.
- `offHand`: item in the off hand.
- `armorSlots`: equipped armor slots, ordered as feet, legs, chest, head.
- `inventory`: player inventory slot list.
- `offhandInventory`: offhand inventory list; present in the 1.20.1 build.
- `armorInventory`: armor inventory list; present in the 1.20.1 build.

## Effect Fields

- `id`: potion/status effect ID.
- `amplifier`: effect level, starting from 0.
- `duration`: remaining duration in ticks.
- `ambient`: whether the effect is ambient.
- `visible`: whether particles are visible.
- `showIcon`: whether the effect icon is shown.

## Container Fields

- `openContainer`: currently opened non-player inventory container. It is `null` when no chest, furnace, or other container is open.
- `playerContainer`: snapshot of the player's own inventory container.

Container object fields:

- `menuType`: container menu class name.
- `containerId`: container ID.
- `slotCount`: total number of slots.
- `slots`: slot list.

Slot fields:

- `index`: slot index within the menu.
- `x` / `y`: slot position in the GUI.
- `containerSlot`: underlying container slot number.
- `mayPickup`: whether the current player can pick up the item from this slot.
- Other item fields are the same as the item fields above.

## Reading from External Software

Do not parse this stream as one-line JSON. The output is multi-line JSON. Read until a blank line, then parse the collected text as one JSON object.

Pseudo-code:

```text
buffer = ""
while pipe has line:
    if line is empty:
        parse buffer as JSON
        buffer = ""
    else:
        buffer += line + "\n"
```
