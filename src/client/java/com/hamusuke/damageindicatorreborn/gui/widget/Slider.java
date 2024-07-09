package com.hamusuke.damageindicatorreborn.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class Slider extends SliderWidget {
    protected final Text prefix;
    protected final double maxValue;
    protected final double minValue;
    protected final Consumer<Slider> applier;

    public Slider(int x, int y, int width, int height, Text prefix, double minValue, double maxValue, double currentValue, Consumer<Slider> applier) {
        super(x, y, width, height, Text.empty(), (currentValue - minValue) / (maxValue - minValue));
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.applier = applier;
        this.prefix = prefix;
        this.updateMessage();
    }

    public int getIntValue() {
        return (int) Math.round(this.getValue());
    }

    public double getValue() {
        return this.value * (this.maxValue - this.minValue) + this.minValue;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(ScreenTexts.composeGenericOptionText(this.prefix, Text.of("" + this.getIntValue())));
    }

    @Override
    protected void applyValue() {
        this.applier.accept(this);
    }
}
