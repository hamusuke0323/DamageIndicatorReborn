package com.hamusuke.damageindicatorreborn.client.config;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.awt.*;

public class RGBValue {
    public final String path;
    public final IntValue red;
    public final IntValue green;
    public final IntValue blue;

    RGBValue(Builder builder, String path, int red, int green, int blue) {
        this.path = path;
        builder.push(this.path);
        this.red = builder.defineInRange("red", red, 0, 255);
        this.green = builder.defineInRange("green", green, 0, 255);
        this.blue = builder.defineInRange("blue", blue, 0, 255);
        builder.pop();
    }

    public int toRGB() {
        return new Color(this.red.get(), this.green.get(), this.blue.get()).getRGB();
    }
}
