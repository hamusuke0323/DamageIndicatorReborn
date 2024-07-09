package com.hamusuke.damageindicatorreborn.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hamusuke.damageindicatorreborn.config.values.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientConfig {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    private final File configFile;
    private final List<AbstractConfig<?>> configRegistry = Lists.newArrayList();
    public final BooleanValue hideIndicator = this.registerConfig("hide_indicator");
    public final BooleanValue forciblyRenderIndicator = this.registerConfig("forcibly_render_indicator");
    public final DoubleValue fontSize = this.registerConfig("font_size", 1.0D, 0.0D, 1.0D);
    public final IntValue renderDistance = this.registerConfig("render_distance", 64, 0, 1024);
    public final BooleanValue changeColorWhenCrit = this.registerConfig("change_color_when_crit");
    public final BooleanValue showFloatingPoint = this.registerConfig("show_floating_point");
    public final BooleanValue disableBiggerFontSizeWhenCrit = this.registerConfig("disable_bigger_font_size_when_crit");
    public final BooleanValue disableMessageWhenJoined = this.registerConfig("disable_message_when_joined");
    public final BooleanValue onlyRenderDamagePlayersAttacked = this.registerConfig("only_render_damage_players_attacked");
    private final List<RGBValue> rgbConfigRegistry = Lists.newArrayList();

    public ClientConfig(File configFile) {
        this.configFile = configFile;

        this.registerRGBConfig("in_fire_damage", 255, 150, 0);
        this.registerRGBConfig("lightning_bolt_damage", 255, 80, 255);
        this.registerRGBConfig("on_fire_damage", 255, 150, 0);
        this.registerRGBConfig("lava_damage", 255, 150, 0);
        this.registerRGBConfig("hot_floor_damage", 255, 150, 0);
        this.registerRGBConfig("in_wall_damage", 255, 225, 0);
        this.registerRGBConfig("cramming_damage", 255, 225, 0);
        this.registerRGBConfig("drown_damage", 0, 20, 255);
        this.registerRGBConfig("starve_damage", 150, 100, 0);
        this.registerRGBConfig("cactus_damage", 0, 255, 0);
        this.registerRGBConfig("fall_damage", 255, 225, 0);
        this.registerRGBConfig("fly_into_wall_damage", 255, 225, 0);
        this.registerRGBConfig("out_of_world_damage", 0, 0, 0);
        this.registerRGBConfig("generic_damage");
        this.registerRGBConfig("magic_damage", 0, 255, 160);
        this.registerRGBConfig("wither_damage", 25, 25, 25);
        this.registerRGBConfig("anvil_damage", 255, 225, 0);
        this.registerRGBConfig("falling_block_damage", 255, 225, 0);
        this.registerRGBConfig("dragon_breath_damage");
        this.registerRGBConfig("dry_out_damage");
        this.registerRGBConfig("sweet_berry_bush_damage");
        this.registerRGBConfig("freeze_damage", 0, 255, 255);
        this.registerRGBConfig("falling_stalactite_damage", 255, 225, 0);
        this.registerRGBConfig("stalagmite_damage", 255, 225, 0);
        this.registerRGBConfig("critical", 255, 255, 0);
        this.registerRGBConfig("heal", 85, 255, 85);
    }

    public synchronized void save() {
        var parent = this.configFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try (var writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(this.configFile), StandardCharsets.UTF_8))) {
            writer.setIndent("\t");
            writer.beginObject();
            for (var abstractConfig : this.configRegistry) {
                abstractConfig.write(writer);
            }
            writer.endObject();
            writer.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving config.", e);
        }
    }

    public synchronized void load() {
        if (this.configFile.exists()) {
            try {
                var jsonObject = GSON.fromJson(new InputStreamReader(new FileInputStream(this.configFile), StandardCharsets.UTF_8), JsonObject.class);
                for (var abstractConfig : this.configRegistry) {
                    abstractConfig.read(jsonObject);
                }
            } catch (Exception e) {
                LOGGER.warn("Error occurred while loading config.", e);
            }
        }
    }

    public ImmutableList<RGBValue> getRGBConfigs() {
        return ImmutableList.copyOf(this.rgbConfigRegistry);
    }

    public int getRGBFromDamageSource(String source) {
        for (var rgb : this.rgbConfigRegistry) {
            if (rgb.getName().replace("_", "").replace("damage", "").equalsIgnoreCase(source)) {
                return rgb.get().toRGB();
            }
        }

        return 16777215;
    }

    private BooleanValue registerConfig(String name) {
        var booleanValue = new BooleanValue(name);
        this.configRegistry.add(booleanValue);
        return booleanValue;
    }

    private DoubleValue registerConfig(String name, double defaultValue, double min, double max) {
        var doubleValue = new DoubleValue(name, defaultValue, min, max);
        this.configRegistry.add(doubleValue);
        return doubleValue;
    }

    private IntValue registerConfig(String name, int defaultValue, int min, int max) {
        var intValue = new IntValue(name, defaultValue, min, max);
        this.configRegistry.add(intValue);
        return intValue;
    }

    private void registerRGBConfig(String name) {
        this.registerRGBConfig(name, 255, 255, 255);
    }

    private void registerRGBConfig(String name, int red, int green, int blue) {
        var rgbValue = new RGBValue(name, red, green, blue);
        this.configRegistry.add(rgbValue);
        this.rgbConfigRegistry.add(rgbValue);
    }
}
