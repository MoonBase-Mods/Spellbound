package com.ombremoon.spellbound.client.gui;

import com.lowdragmc.lowdraglib2.test.TestJava;
import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BasicGuideScreen extends GuideBookScreen {

    public BasicGuideScreen(Component title) {
        super(title, CommonClass.customLocation("studies_in_the_arcane"), CommonClass.customLocation("textures/gui/books/studies_in_the_arcane.png"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
