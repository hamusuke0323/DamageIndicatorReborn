package com.hamusuke.damageindicatorreborn.config.values;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;

public class DoubleValue extends AbstractConfig<Double> {
    protected final double min;
    protected final double max;

    public DoubleValue(String name, double defaultValue, double min, double max) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.clamp();
    }

    @Override
    public void write(JsonWriter jsonWriter) throws IOException {
        jsonWriter.name(this.name).value(this.clamp());
    }

    @Override
    public void read(JsonObject jsonObject) {
        if (jsonObject.has(this.name)) {
            this.set(jsonObject.get(this.name).getAsDouble());
        }
    }

    protected double clamp() {
        return this.value = MathHelper.clamp(this.value, this.min, this.max);
    }

    @Override
    public Double get() {
        return this.clamp();
    }

    @Override
    public void set(Double value) {
        this.value = value;
        this.clamp();
    }

    public double getMax() {
        return this.max;
    }

    public double getMin() {
        return this.min;
    }
}
