package com.hamusuke.damageindicatorreborn.client.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.awt.*;
import java.util.List;

public class Config {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CONFIG;

    static {
        var pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = pair.getLeft();
        CONFIG = pair.getRight();
    }

    public static class ClientConfig {
        public final BooleanValue hideIndicator;
        public final BooleanValue forceIndicatorRendering;
        public final DoubleValue fontSize;
        public final IntValue renderDistance;
        public final BooleanValue changeColorWhenCrit;
        public final BooleanValue disableBiggerFontSizeWhenCrit;
        public final BooleanValue showFloatingPoint;
        public final ColorConfig colorConfig;

        public ClientConfig(Builder builder) {
            builder.push("client");
            this.hideIndicator = builder.define("hide_indicator", false);
            this.forceIndicatorRendering = builder.define("force_indicator_rendering", false);
            this.fontSize = builder.defineInRange("font_size", 1.0D, 0.0D, 1.0D);
            this.renderDistance = builder.defineInRange("render_distance", 64, 0, 1024);
            this.changeColorWhenCrit = builder.define("change_color_when_crit", false);
            this.disableBiggerFontSizeWhenCrit = builder.define("disable_bigger_font_size_when_crit", false);
            this.showFloatingPoint = builder.define("show_floating_point", false);
            this.colorConfig = new ColorConfig(builder);
            builder.pop();
        }

        public static class ColorConfig {
            private final List<RGBValue> colorConfigs = Lists.newArrayList();

            private RGBValue register(Builder builder, String path) {
                return this.register(builder, path, 255, 255, 255);
            }

            private RGBValue register(Builder builder, String path, int red, int green, int blue) {
                return this.register(new RGBValue(builder, path, red, green, blue));
            }

            private RGBValue register(RGBValue rgb) {
                this.colorConfigs.add(rgb);
                return rgb;
            }

            public int getRGBFromDamageSource(String source) {
                for (RGBValue rgb : this.colorConfigs) {
                    if (rgb.path.replace("_", "").replace("damage", "").equalsIgnoreCase(source)) {
                        return rgb.toRGB();
                    }
                }

                return 16777215;
            }

            public ImmutableList<RGBValue> immutable() {
                return ImmutableList.copyOf(this.colorConfigs);
            }

            public ColorConfig(Builder builder) {
                builder.push("color");
                this.register(builder, "in_fire_damage", 255, 150, 0);
                this.register(builder, "lightning_bolt_damage", 255, 80, 255);
                this.register(builder, "on_fire_damage", 255, 150, 0);
                this.register(builder, "lava_damage", 255, 150, 0);
                this.register(builder, "hot_floor_damage", 255, 150, 0);
                this.register(builder, "in_wall_damage", 255, 225, 0);
                this.register(builder, "cramming_damage", 255, 225, 0);
                this.register(builder, "drown_damage", 0, 20, 255);
                this.register(builder, "starve_damage", 150, 100, 0);
                this.register(builder, "cactus_damage", 0, 255, 0);
                this.register(builder, "fall_damage", 255, 225, 0);
                this.register(builder, "fly_into_wall_damage", 255, 225, 0);
                this.register(builder, "out_of_world_damage", 0, 0, 0);
                this.register(builder, "generic_damage");
                this.register(builder, "magic_damage", 0, 255, 160);
                this.register(builder, "wither_damage", 25, 25, 25);
                this.register(builder, "anvil_damage", 255, 225, 0);
                this.register(builder, "falling_block_damage", 255, 225, 0);
                this.register(builder, "dragon_breath_damage");
                this.register(builder, "dry_out_damage");
                this.register(builder, "sweet_berry_bush_damage");
                this.register(builder, "freeze_damage", 0, 255, 255);
                this.register(builder, "falling_stalactite_damage", 255, 225, 0);
                this.register(builder, "stalagmite_damage", 255, 225, 0);
                this.register(builder, "critical", 255, 255, 0);
                this.register(builder, "heal", 85, 255, 85);
                builder.pop();
            }
        }
    }
}
