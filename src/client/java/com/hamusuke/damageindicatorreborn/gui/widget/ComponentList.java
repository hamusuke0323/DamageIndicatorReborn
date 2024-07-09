package com.hamusuke.damageindicatorreborn.gui.widget;

import com.hamusuke.damageindicatorreborn.gui.widget.ComponentList.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ComponentList extends ElementListWidget<Entry> {
    public ComponentList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
    }

    public void addString(Text text) {
        this.addEntry(new StringEntry(text));
    }

    public <B extends ClickableWidget> B addButton(B button) {
        this.addEntry(new ButtonEntry(button));
        return button;
    }

    @Environment(EnvType.CLIENT)
    public static class Entry extends ElementListWidget.Entry<Entry> {
        @Override
        public List<? extends Selectable> selectableChildren() {
            return Collections.emptyList();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    @Environment(EnvType.CLIENT)
    public class StringEntry extends Entry {
        final Text name;
        private final int width;

        public StringEntry(Text p_193886_) {
            this.name = p_193886_;
            this.width = ComponentList.this.client.textRenderer.getWidth(this.name);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            drawCenteredText(matrices, ComponentList.this.client.textRenderer, this.name, ComponentList.this.client.currentScreen.width / 2 - this.width / 2, y + height - 9 - 1, 16777215);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ButtonEntry extends Entry {
        final ClickableWidget button;

        public ButtonEntry(ClickableWidget button) {
            this.button = button;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.x = x;
            this.button.y = y;
            this.button.setWidth(entryWidth);
            this.button.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.button);
        }
    }
}
