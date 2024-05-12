package com.hamusuke.damageindicatorreborn.client.gui.screen;

import com.hamusuke.damageindicatorreborn.client.config.Config;
import com.hamusuke.damageindicatorreborn.client.config.RGBValue;
import com.hamusuke.damageindicatorreborn.client.gui.component.ComponentList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ForgeSlider;

import javax.annotation.Nullable;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {
    private static final Component HIDE_INDICATOR = Component.translatable("options." + MOD_ID + ".hideIndicator");
    private static final Component FORCE_INDICATOR_RENDERING = Component.translatable("options." + MOD_ID + ".forceindicatorrendering");
    private static final Component CHANGE_COLOR_WHEN_CRIT = Component.translatable("options." + MOD_ID + ".changeColorWhenCrit");
    private static final Component SHOW_FLOATING_POINT = Component.translatable("options." + MOD_ID + ".showFloatingPoint");
    private static final Component DISABLE_BIGGER_FONT_WHEN_CRIT = Component.translatable("options." + MOD_ID + ".disableBiggerFontWhenCrit");
    private static final Component FONT_SIZE = Component.translatable("options." + MOD_ID + ".fontsize");
    private static final Component DISPLAY_DISTANCE = Component.translatable("options." + MOD_ID + ".displayDistance");
    private final Screen parent;
    private ComponentList list;

    public ConfigScreen(Minecraft ignored, Screen parent) {
        super(Component.translatable("options.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        double scroll = this.list == null ? 0.0D : this.list.getScrollAmount();
        this.list = this.addRenderableWidget(new ComponentList(this.minecraft, this.width, this.height - 40, 20, 20));
        this.list.setScrollAmount(scroll);

        this.list.addButton(CycleButton.onOffBuilder(Config.CLIENT.hideIndicator.get()).create(0, 0, 0, 20, HIDE_INDICATOR, (cycleButton, aBoolean) -> {
            Config.CLIENT.hideIndicator.set(aBoolean);
        }));

        this.list.addButton(CycleButton.onOffBuilder(Config.CLIENT.forceIndicatorRendering.get()).create(0, 0, 0, 20, FORCE_INDICATOR_RENDERING, (cycleButton, aBoolean) -> {
            Config.CLIENT.forceIndicatorRendering.set(aBoolean);
        }));

        this.list.addButton(CycleButton.onOffBuilder(Config.CLIENT.showFloatingPoint.get()).create(0, 0, 0, 20, SHOW_FLOATING_POINT, (cycleButton, aBoolean) -> {
            Config.CLIENT.showFloatingPoint.set(aBoolean);
        }));

        this.list.addButton(CycleButton.onOffBuilder(Config.CLIENT.changeColorWhenCrit.get()).create(0, 0, 0, 20, CHANGE_COLOR_WHEN_CRIT, (cycleButton, aBoolean) -> {
            Config.CLIENT.changeColorWhenCrit.set(aBoolean);
        }));

        this.list.addButton(CycleButton.onOffBuilder(Config.CLIENT.disableBiggerFontSizeWhenCrit.get()).create(0, 0, 0, 20, DISABLE_BIGGER_FONT_WHEN_CRIT, (cycleButton, aBoolean) -> {
            Config.CLIENT.disableBiggerFontSizeWhenCrit.set(aBoolean);
        }));

        this.list.addButton(new AbstractSliderButton(0, 0, 0, 20, CommonComponents.optionNameValue(FONT_SIZE, Component.literal(Math.round(Config.CLIENT.fontSize.get() * 100.0D) + "%")), Config.CLIENT.fontSize.get()) {
            @Override
            protected void updateMessage() {
                this.setMessage(CommonComponents.optionNameValue(FONT_SIZE, Component.literal(Math.round(Config.CLIENT.fontSize.get() * 100.0D) + "%")));
            }

            @Override
            protected void applyValue() {
                Config.CLIENT.fontSize.set(Mth.clamp(this.value, 0.0D, 1.0D));
            }
        });

        this.list.addButton(new AbstractSliderButton(0, 0, 0, 20, CommonComponents.optionNameValue(DISPLAY_DISTANCE, Component.literal("" + Config.CLIENT.renderDistance.get())), (double) Config.CLIENT.renderDistance.get() / 1024.0D) {
            @Override
            protected void updateMessage() {
                this.setMessage(CommonComponents.optionNameValue(DISPLAY_DISTANCE, Component.literal("" + Config.CLIENT.renderDistance.get())));
            }

            @Override
            protected void applyValue() {
                Config.CLIENT.renderDistance.set(Mth.clamp((int) (this.value * 1024.0D), 0, 1024));
            }
        });

        this.list.addButton(Button.builder(Component.translatable(MOD_ID + ".config.colorConfig.title"), p_onPress_1_ -> this.minecraft.setScreen(new ColorSettingsScreen(this))).bounds(0, 0, 0, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, p_onPress_1_ -> this.onClose()).bounds(0, this.height - 20, this.width / 2, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, p_onPress_1_ -> {
            Config.CONFIG.save();
            this.onClose();
        }).bounds(this.width / 2, this.height - 20, this.width / 2, 20).build());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        this.renderBackground(p_281549_);
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        p_281549_.drawCenteredString(this.font, this.getTitle(), this.width / 2, 10, 16777215);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @OnlyIn(Dist.CLIENT)
    private static class ColorSettingsScreen extends Screen {
        @Nullable
        private final Screen parent;
        private ColorList list;

        private ColorSettingsScreen(@Nullable Screen parent) {
            super(Component.translatable(MOD_ID + ".config.colorConfig.title"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            super.init();
            double amount = this.list != null ? this.list.getScrollAmount() : 0.0D;
            this.list = this.addRenderableWidget(new ColorList());
            this.list.setScrollAmount(amount);
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, p_onPress_1_ -> this.onClose()).bounds(this.width / 2 - this.width / 4, this.height - 20, this.width / 2, 20).build());
        }

        @Override
        public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
            this.renderBackground(p_281549_);
            super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(this.parent);
        }

        @OnlyIn(Dist.CLIENT)
        private class ColorList extends ObjectSelectionList<ColorList.Color> {
            public ColorList() {
                super(ColorSettingsScreen.this.minecraft, ColorSettingsScreen.this.width, ColorSettingsScreen.this.height, 20, ColorSettingsScreen.this.height - 20, 20);
                for (var rgb : Config.CLIENT.colorConfig.immutable()) {
                    this.addEntry(new Color(rgb));
                }
            }

            @OnlyIn(Dist.CLIENT)
            private class Color extends ObjectSelectionList.Entry<Color> {
                private final Button button;

                private Color(RGBValue rgbConfig) {
                    this.button = Button.builder(Component.translatable(MOD_ID + ".config.color." + rgbConfig.path), p_onPress_1_ -> ColorSettingsScreen.this.minecraft.setScreen(new ColorMixingScreen(ColorSettingsScreen.this, rgbConfig))).bounds(ColorSettingsScreen.this.width / 4, 0, ColorSettingsScreen.this.width / 2, 20).build();
                }

                @Override
                public void render(GuiGraphics guiGraphics, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
                    this.button.setY(i1);
                    this.button.render(guiGraphics, i5, i6, v);
                }

                @Override
                public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
                    return this.button.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
                }

                @Override
                public Component getNarration() {
                    return CommonComponents.EMPTY;
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        private static class ColorMixingScreen extends Screen {
            @Nullable
            private final Screen parent;
            private final RGBValue rgb;
            private ForgeSlider red;
            private ForgeSlider green;
            private ForgeSlider blue;

            private ColorMixingScreen(@Nullable Screen parent, RGBValue rgb) {
                super(Component.translatable(MOD_ID + ".config.color." + rgb.path));
                this.parent = parent;
                this.rgb = rgb;
            }

            @Override
            protected void init() {
                super.init();

                this.red = this.addRenderableWidget(new ForgeSlider(this.width / 4, this.height / 2 - 70, this.width / 2, 20, Component.nullToEmpty("Red: "), CommonComponents.EMPTY, 0.0D, 255.0D, this.rgb.red.get(), 1.0D, 0, true));
                this.green = this.addRenderableWidget(new ForgeSlider(this.width / 4, this.height / 2 - 45, this.width / 2, 20, Component.nullToEmpty("Green: "), CommonComponents.EMPTY, 0.0D, 255.0D, this.rgb.green.get(), 1.0D, 0, true));
                this.blue = this.addRenderableWidget(new ForgeSlider(this.width / 4, this.height / 2 - 20, this.width / 2, 20, Component.nullToEmpty("Blue: "), CommonComponents.EMPTY, 0.0D, 255.0D, this.rgb.blue.get(), 1.0D, 0, true));
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, p_onPress_1_ -> this.onClose()).bounds(0, this.height - 20, this.width / 2, 20).build());
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, p_onPress_1_ -> {
                    this.rgb.red.set(this.red.getValueInt());
                    this.rgb.green.set(this.green.getValueInt());
                    this.rgb.blue.set(this.blue.getValueInt());
                    Config.CONFIG.save();
                    this.onClose();
                }).bounds(this.width / 2, this.height - 20, this.width / 2, 20).build());
            }

            @Override
            public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
                this.renderBackground(p_281549_);
                super.render(p_281549_, p_281550_, p_282878_, p_282465_);
                p_281549_.drawCenteredString(this.font, this.title, this.width / 2, 5, 16777215);
                int color = ARGB32.color(255, this.red.getValueInt(), this.green.getValueInt(), this.blue.getValueInt()) + (255 << 24);
                p_281549_.fillGradient(this.width / 4, this.height / 2 + 5, this.width * 3 / 4, this.height - 25, color, color);
            }

            @Override
            public void onClose() {
                this.minecraft.setScreen(this.parent);
            }
        }
    }
}
