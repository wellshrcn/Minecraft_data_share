package com.wells.datashare.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class PlayerDataCollector {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final int SCHEMA_VERSION = 1;

    private PlayerDataCollector() {
    }

    public static String collectJsonLine() {
        Minecraft mc = Minecraft.getInstance();
        JsonObject root = new JsonObject();
        root.addProperty("schema", SCHEMA_VERSION);
        root.addProperty("ts", System.currentTimeMillis());
        root.addProperty("pipe", "data_share");
        root.addProperty("mcVersion", "1.21.4");

        JsonObject game = new JsonObject();
        game.addProperty("fps", mc.getFps());
        addBooleanAsNumber(game, "paused", mc.isPaused());
        addBooleanAsNumber(game, "inGame", mc.player != null && mc.level != null);
        game.addProperty("windowWidth", mc.getWindow().getWidth());
        game.addProperty("windowHeight", mc.getWindow().getHeight());
        game.addProperty("guiScale", mc.getWindow().getGuiScale());
        if (mc.getUser() != null) {
            game.addProperty("accountName", mc.getUser().getName());
        }
        if (mc.getCurrentServer() != null) {
            game.addProperty("serverAddress", mc.getCurrentServer().ip);
            game.addProperty("serverName", mc.getCurrentServer().name);
        } else {
            game.addProperty("serverAddress", "singleplayer");
            game.addProperty("serverName", "singleplayer");
        }
        if (mc.screen != null) {
            game.addProperty("screen", mc.screen.getClass().getName());
        } else {
            game.add("screen", JsonNull.INSTANCE);
        }
        root.add("game", game);

        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) {
            root.add("player", JsonNull.INSTANCE);
            return GSON.toJson(root);
        }

        JsonObject p = new JsonObject();
        p.addProperty("name", player.getName().getString());
        p.addProperty("uuid", player.getUUID().toString());
        p.addProperty("id", player.getId());

        Vec3 pos = player.position();
        JsonObject posObj = new JsonObject();
        posObj.addProperty("x", pos.x);
        posObj.addProperty("y", pos.y);
        posObj.addProperty("z", pos.z);
        p.add("pos", posObj);

        BlockPos blockPos = player.blockPosition();
        JsonObject blockObj = new JsonObject();
        blockObj.addProperty("x", blockPos.getX());
        blockObj.addProperty("y", blockPos.getY());
        blockObj.addProperty("z", blockPos.getZ());
        p.add("blockPos", blockObj);

        ResourceLocation dim = level.dimension().location();
        p.addProperty("dimension", dim.toString());
        p.addProperty("biome", level.getBiome(blockPos).unwrapKey().map(k -> k.location().toString()).orElse("unknown"));
        p.addProperty("dayTime", level.getDayTime());
        p.addProperty("gameTime", level.getGameTime());
        p.addProperty("difficulty", level.getDifficulty().getSerializedName());
        addBooleanAsNumber(p, "raining", level.isRaining());
        addBooleanAsNumber(p, "thundering", level.isThundering());

        p.addProperty("yaw", player.getYRot());
        p.addProperty("pitch", player.getXRot());
        p.addProperty("bodyYaw", player.yBodyRot);
        p.addProperty("headYaw", player.yHeadRot);

        Vec3 vel = player.getDeltaMovement();
        JsonObject velObj = new JsonObject();
        velObj.addProperty("x", vel.x);
        velObj.addProperty("y", vel.y);
        velObj.addProperty("z", vel.z);
        p.add("velocity", velObj);

        p.addProperty("health", player.getHealth());
        p.addProperty("maxHealth", player.getMaxHealth());
        p.addProperty("absorption", player.getAbsorptionAmount());
        p.addProperty("armor", player.getArmorValue());
        p.addProperty("food", player.getFoodData().getFoodLevel());
        p.addProperty("saturation", player.getFoodData().getSaturationLevel());
        p.addProperty("air", player.getAirSupply());
        p.addProperty("maxAir", player.getMaxAirSupply());
        p.addProperty("xpLevel", player.experienceLevel);
        p.addProperty("xpProgress", player.experienceProgress);
        p.addProperty("xpTotal", player.totalExperience);
        p.addProperty("score", player.getScore());

        if (mc.gameMode != null) {
            GameType mode = mc.gameMode.getPlayerMode();
            p.addProperty("gamemode", mode.getName());
            p.addProperty("gamemodeId", mode.getId());
        }

        addBooleanAsNumber(p, "isAlive", player.isAlive());
        addBooleanAsNumber(p, "isDeadOrDying", player.isDeadOrDying());
        addBooleanAsNumber(p, "isCrouching", player.isCrouching());
        addBooleanAsNumber(p, "isShiftKeyDown", player.isShiftKeyDown());
        addBooleanAsNumber(p, "isSprinting", player.isSprinting());
        addBooleanAsNumber(p, "isSwimming", player.isSwimming());
        addBooleanAsNumber(p, "isUnderWater", player.isUnderWater());
        addBooleanAsNumber(p, "isInWater", player.isInWater());
        addBooleanAsNumber(p, "isInLava", player.isInLava());
        addBooleanAsNumber(p, "isOnGround", player.onGround());
        addBooleanAsNumber(p, "isOnFire", player.isOnFire());
        addBooleanAsNumber(p, "isFallFlying", player.isFallFlying());
        addBooleanAsNumber(p, "isSleeping", player.isSleeping());
        addBooleanAsNumber(p, "isPassenger", player.isPassenger());
        addBooleanAsNumber(p, "isUsingItem", player.isUsingItem());
        addBooleanAsNumber(p, "isBlocking", player.isBlocking());
        p.addProperty("fallDistance", player.fallDistance);
        p.addProperty("hurtTime", player.hurtTime);
        p.addProperty("deathTime", player.deathTime);

        addBooleanAsNumber(p, "flying", player.getAbilities().flying);
        addBooleanAsNumber(p, "mayFly", player.getAbilities().mayfly);
        addBooleanAsNumber(p, "instabuild", player.getAbilities().instabuild);
        addBooleanAsNumber(p, "invulnerable", player.getAbilities().invulnerable);
        addBooleanAsNumber(p, "mayBuild", player.getAbilities().mayBuild);
        p.addProperty("flyingSpeed", player.getAbilities().getFlyingSpeed());
        p.addProperty("walkingSpeed", player.getAbilities().getWalkingSpeed());

        Inventory inv = player.getInventory();
        p.addProperty("selectedSlot", inv.selected);
        p.add("mainHand", itemToJson(player.getMainHandItem()));
        p.add("offHand", itemToJson(player.getOffhandItem()));

        JsonArray armor = new JsonArray();
        armor.add(itemToJson(player.getItemBySlot(EquipmentSlot.FEET)));
        armor.add(itemToJson(player.getItemBySlot(EquipmentSlot.LEGS)));
        armor.add(itemToJson(player.getItemBySlot(EquipmentSlot.CHEST)));
        armor.add(itemToJson(player.getItemBySlot(EquipmentSlot.HEAD)));
        p.add("armorSlots", armor);

        JsonArray inventory = new JsonArray();
        int inventoryUsedSlots = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inventoryUsedSlots++;
            }
            JsonObject slot = itemToJson(stack);
            slot.addProperty("slot", i);
            inventory.add(slot);
        }
        p.addProperty("inventoryUsedSlots", inventoryUsedSlots);
        p.add("inventory", inventory);

        JsonArray effects = new JsonArray();
        for (MobEffectInstance effect : player.getActiveEffects()) {
            effects.add(effectToJson(effect));
        }
        p.add("effects", effects);

        AbstractContainerMenu menu = player.containerMenu;
        if (menu != null && menu != player.inventoryMenu) {
            p.add("openContainer", containerToJson(menu));
        } else {
            p.add("openContainer", JsonNull.INSTANCE);
        }
        p.add("playerContainer", containerToJson(player.inventoryMenu));

        root.add("player", p);
        return GSON.toJson(root);
    }

    private static void addBooleanAsNumber(JsonObject obj, String name, boolean value) {
        obj.addProperty(name, value ? 1 : 0);
    }

    private static JsonObject containerToJson(AbstractContainerMenu menu) {
        JsonObject obj = new JsonObject();
        obj.addProperty("menuType", menu.getClass().getName());
        obj.addProperty("containerId", menu.containerId);
        obj.addProperty("slotCount", menu.slots.size());
        JsonArray slots = new JsonArray();
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            JsonObject s = itemToJson(slot.getItem());
            s.addProperty("index", i);
            s.addProperty("x", slot.x);
            s.addProperty("y", slot.y);
            s.addProperty("containerSlot", slot.getContainerSlot());
            addBooleanAsNumber(s, "mayPickup", slot.mayPickup(Minecraft.getInstance().player));
            slots.add(s);
        }
        obj.add("slots", slots);
        return obj;
    }

    private static JsonObject effectToJson(MobEffectInstance effect) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", effect.getEffect().toString());
        obj.addProperty("amplifier", effect.getAmplifier());
        obj.addProperty("duration", effect.getDuration());
        addBooleanAsNumber(obj, "ambient", effect.isAmbient());
        addBooleanAsNumber(obj, "visible", effect.isVisible());
        addBooleanAsNumber(obj, "showIcon", effect.showIcon());
        return obj;
    }

    private static JsonObject itemToJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        if (stack == null || stack.isEmpty()) {
            addBooleanAsNumber(obj, "empty", true);
            return obj;
        }
        addBooleanAsNumber(obj, "empty", false);
        var id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) {
            id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        }
        obj.addProperty("id", id != null ? id.toString() : "unknown");
        obj.addProperty("count", stack.getCount());
        obj.addProperty("damage", stack.getDamageValue());
        obj.addProperty("maxDamage", stack.getMaxDamage());
        addBooleanAsNumber(obj, "damageable", stack.isDamageableItem());
        addBooleanAsNumber(obj, "enchanted", stack.isEnchanted());
        obj.addProperty("displayName", stack.getHoverName().getString());
        obj.addProperty("components", stack.getComponents().toString());
        return obj;
    }
}