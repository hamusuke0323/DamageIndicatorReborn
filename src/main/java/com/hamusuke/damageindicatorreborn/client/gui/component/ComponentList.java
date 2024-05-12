package com.hamusuke.damageindicatorreborn.client.gui.component;

import com.hamusuke.damageindicatorreborn.client.gui.component.ComponentList.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ComponentList extends ContainerObjectSelectionList<Entry> {
    public ComponentList(Minecraft mc, int width, int height, int y, int itemHeight) {
        super(mc, width, height, y, y + height, itemHeight);
    }

    public void addString(Component component) {
        this.addEntry(new StringEntry(component));
    }

    public <B extends AbstractWidget> B addButton(B button) {
        this.addEntry(new ButtonEntry(button));
        return button;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(PoseStack poseStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class StringEntry extends Entry {
        final Component name;
        private final int width;

        public StringEntry(Component p_193886_) {
            this.name = p_193886_;
            this.width = ComponentList.this.minecraft.font.width(this.name);
        }

        @Override
        public void render(PoseStack poseStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
            drawString(poseStack, ComponentList.this.minecraft.font, this.name, ComponentList.this.minecraft.screen.width / 2 - this.width / 2, i1 + height - 9 - 1, 16777215);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ButtonEntry extends Entry {
        final AbstractWidget button;

        public ButtonEntry(AbstractWidget button) {
            this.button = button;
        }

        @Override
        public void render(PoseStack poseStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
            this.button.x = i2;
            this.button.y = i1;
            this.button.setWidth(i3);
            this.button.render(poseStack, i5, i6, v);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.button);
        }
    }
}
