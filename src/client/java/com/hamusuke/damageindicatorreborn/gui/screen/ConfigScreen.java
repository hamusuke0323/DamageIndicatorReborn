package com.hamusuke.damageindicatorreborn.gui.screen;

import com.hamusuke.damageindicatorreborn.DamageIndicatorReborn;
import com.hamusuke.damageindicatorreborn.config.values.RGBValue;
import com.hamusuke.damageindicatorreborn.gui.widget.ComponentList;
import com.hamusuke.damageindicatorreborn.gui.widget.Slider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;
import static com.hamusuke.damageindicatorreborn.DamageIndicatorRebornClient.clientConfig;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private static final Text HIDE_INDICATOR = Text.translatable("options." + MOD_ID + ".hideIndicator");
    private static final Text FORCE_INDICATOR_RENDERING = Text.translatable("options." + MOD_ID + ".forceindicatorrendering");
    private static final Text CHANGE_COLOR_WHEN_CRIT = Text.translatable("options." + MOD_ID + ".changeColorWhenCrit");
    private static final Text SHOW_FLOATING_POINT = Text.translatable("options." + MOD_ID + ".showFloatingPoint");
    private static final Text DISABLE_BIGGER_FONT_WHEN_CRIT = Text.translatable("options." + MOD_ID + ".disableBiggerFontWhenCrit");
    private static final Text FONT_SIZE = Text.translatable("options." + MOD_ID + ".fontsize");
    private static final Text DISPLAY_DISTANCE = Text.translatable("options." + MOD_ID + ".displayDistance");
    private static final Text DISABLE_MESSAGE_WHEN_JOINED = Text.translatable("options." + MOD_ID + ".disableMessageWhenJoined");
    private static final Text ONLY_RENDER_DAMAGE_PLAYERS_ATTACKED = Text.translatable("options." + MOD_ID + ".onlyRenderDamagePlayersAttacked");
    private final Screen parent;
    private ComponentList list;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("options.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        double scroll = this.list == null ? 0.0D : this.list.getScrollAmount();
        this.list = this.addDrawableChild(new ComponentList(this.client, this.width, this.height - 40, 20, this.height - 20, 20));
        this.list.setScrollAmount(scroll);

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.hideIndicator.get()).build(0, 0, 0, 20, HIDE_INDICATOR, (cycleButton, aBoolean) -> {
            clientConfig.hideIndicator.set(aBoolean);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.forciblyRenderIndicator.get()).build(0, 0, 0, 20, FORCE_INDICATOR_RENDERING, (cycleButton, aBoolean) -> {
            clientConfig.forciblyRenderIndicator.set(aBoolean);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.showFloatingPoint.get()).build(0, 0, 0, 20, SHOW_FLOATING_POINT, (cycleButton, aBoolean) -> {
            clientConfig.showFloatingPoint.set(aBoolean);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.changeColorWhenCrit.get()).build(0, 0, 0, 20, CHANGE_COLOR_WHEN_CRIT, (cycleButton, aBoolean) -> {
            clientConfig.changeColorWhenCrit.set(aBoolean);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.disableBiggerFontSizeWhenCrit.get()).build(0, 0, 0, 20, DISABLE_BIGGER_FONT_WHEN_CRIT, (cycleButton, aBoolean) -> {
            clientConfig.disableBiggerFontSizeWhenCrit.set(aBoolean);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.disableMessageWhenJoined.get()).build(0, 0, 0, 20, DISABLE_MESSAGE_WHEN_JOINED, (button, value) -> {
            clientConfig.disableMessageWhenJoined.set(value);
        }));

        this.list.addButton(CyclingButtonWidget.onOffBuilder(clientConfig.onlyRenderDamagePlayersAttacked.get()).build(0, 0, 0, 20, ONLY_RENDER_DAMAGE_PLAYERS_ATTACKED, (button, value) -> {
            clientConfig.onlyRenderDamagePlayersAttacked.set(value);
        }));

        this.list.addButton(new Slider(0, 0, 0, 20, FONT_SIZE, clientConfig.fontSize.getMin(), clientConfig.fontSize.getMax(), clientConfig.fontSize.get(), slider -> clientConfig.fontSize.set(slider.getValue())) {
            @Override
            protected void updateMessage() {
                this.setMessage(ScreenTexts.composeGenericOptionText(FONT_SIZE, Text.literal(Math.round(clientConfig.fontSize.get() * 100.0D) + "%")));
            }
        });
        this.list.addButton(new Slider(0, 0, 0, 20, DISPLAY_DISTANCE, clientConfig.renderDistance.getMin(), clientConfig.renderDistance.getMax(), clientConfig.renderDistance.get(), slider -> clientConfig.renderDistance.set(slider.getIntValue())));

        this.list.addButton(new ButtonWidget(this.width / 4, this.height / 2 + 30, this.width / 2, 20, Text.translatable(DamageIndicatorReborn.MOD_ID + ".config.colorConfig.title"), p_onPress_1_ -> this.client.setScreen(new ColorSettingsScreen(this))));

        this.addDrawableChild(new ButtonWidget(0, this.height - 20, this.width / 2, 20, ScreenTexts.CANCEL, button -> this.close()));
        this.addDrawableChild(new ButtonWidget(this.width / 2, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, p_onPress_1_ -> {
            clientConfig.save();
            this.close();
        }));
    }

    @Override
    public void render(MatrixStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(p_96562_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
        drawCenteredText(p_96562_, this.textRenderer, this.getTitle(), this.width / 2, 10, 16777215);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(EnvType.CLIENT)
    private static class ColorSettingsScreen extends Screen {
        @Nullable
        private final Screen parent;
        private ColorList list;

        private ColorSettingsScreen(@Nullable Screen parent) {
            super(Text.translatable(MOD_ID + ".config.colorConfig.title"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            super.init();
            double amount = this.list != null ? this.list.getScrollAmount() : 0.0D;
            this.list = this.addDrawableChild(new ColorList());
            this.list.setScrollAmount(amount);
            this.addDrawableChild(new ButtonWidget(this.width / 2 - this.width / 4, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, p_onPress_1_ -> this.close()));
        }

        @Override
        public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            this.renderBackground(p_230430_1_);
            super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

        @Override
        public void close() {
            this.client.setScreen(this.parent);
        }

        @Environment(EnvType.CLIENT)
        private static class ColorMixingScreen extends Screen {
            @Nullable
            private final Screen parent;
            private final RGBValue rgb;
            private Slider red;
            private Slider green;
            private Slider blue;

            private ColorMixingScreen(@Nullable Screen parent, RGBValue rgb) {
                super(Text.translatable(MOD_ID + ".config.color." + rgb.getName()));
                this.parent = parent;
                this.rgb = rgb;
            }

            @Override
            protected void init() {
                super.init();

                this.red = this.addDrawableChild(new Slider(this.width / 4, this.height / 2 - 70, this.width / 2, 20, Text.of("Red: "), 0.0D, 255.0D, this.rgb.get().getRed(), slider -> {
                }));
                this.green = this.addDrawableChild(new Slider(this.width / 4, this.height / 2 - 45, this.width / 2, 20, Text.of("Green: "), 0.0D, 255.0D, this.rgb.get().getGreen(), slider -> {
                }));
                this.blue = this.addDrawableChild(new Slider(this.width / 4, this.height / 2 - 20, this.width / 2, 20, Text.of("Blue: "), 0.0D, 255.0D, this.rgb.get().getBlue(), slider -> {
                }));
                this.addDrawableChild(new ButtonWidget(0, this.height - 20, this.width / 2, 20, ScreenTexts.CANCEL, button -> this.close()));
                this.addDrawableChild(new ButtonWidget(this.width / 2, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, p_onPress_1_ -> {
                    this.rgb.get().setRed(this.red.getIntValue());
                    this.rgb.get().setGreen(this.green.getIntValue());
                    this.rgb.get().setBlue(this.blue.getIntValue());
                    clientConfig.save();
                    this.close();
                }));
            }

            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
                this.renderBackground(p_230430_1_);
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
                drawCenteredText(p_230430_1_, this.textRenderer, this.title, this.width / 2, 5, 16777215);
                int color = MathHelper.packRgb(this.red.getIntValue(), this.green.getIntValue(), this.blue.getIntValue()) + (255 << 24);
                this.fillGradient(p_230430_1_, this.width / 4, this.height / 2 + 5, this.width * 3 / 4, this.height - 25, color, color);
            }

            @Override
            public void close() {
                this.client.setScreen(this.parent);
            }
        }

        @Environment(EnvType.CLIENT)
        private class ColorList extends ElementListWidget<ColorList.Color> {
            public ColorList() {
                super(ColorSettingsScreen.this.client, ColorSettingsScreen.this.width, ColorSettingsScreen.this.height, 20, ColorSettingsScreen.this.height - 20, 20);
                for (RGBValue rgb : clientConfig.getRGBConfigs()) {
                    this.addEntry(new Color(rgb));
                }
            }

            @Environment(EnvType.CLIENT)
            private class Color extends ElementListWidget.Entry<Color> {
                private final ButtonWidget button;

                private Color(RGBValue rgbConfig) {
                    this.button = new ButtonWidget(ColorSettingsScreen.this.width / 4, 0, ColorSettingsScreen.this.width / 2, 20, Text.translatable(DamageIndicatorReborn.MOD_ID + ".config.color." + rgbConfig.getName()), p_onPress_1_ -> ColorSettingsScreen.this.client.setScreen(new ColorMixingScreen(ColorSettingsScreen.this, rgbConfig)));
                }

                @Override
                public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                    this.button.y = p_230432_3_;
                    this.button.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
                }

                @Override
                public List<? extends Element> children() {
                    return Collections.singletonList(this.button);
                }

                @Override
                public List<? extends Selectable> selectableChildren() {
                    return Collections.singletonList(this.button);
                }
            }
        }
    }
}
