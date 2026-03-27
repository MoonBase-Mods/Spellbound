package com.ombremoon.spellbound.client.gui.guide.renderers.init;

import com.ombremoon.spellbound.client.gui.guide.elements.IPageElement;
import com.ombremoon.spellbound.client.gui.guide.renderers.IPageElementRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ElementRenderDispatcher {
    private static int TICK_COUNT = 0;
    private static final Map<Class<?>, Object> REGISTER = new HashMap<>();
    private static Map<IPageElement, Map<String, Object>> RENDER_DATA = new HashMap<>();

    public static <T extends IPageElement, C extends IPageElementRenderer<T>> void register(Class<T> element, C renderer) {
        REGISTER.put(element, renderer);
    }

    public static void resetElements() {
        TICK_COUNT = 0;
        for (Map<String, Object> elementData : RENDER_DATA.values()) {
            for (Object data : elementData.values()) {
                if (data instanceof Entity entity) {
                    entity.discard();
                }
            }
        }

        RENDER_DATA = new HashMap<>();
    }

    public static Object getData(IPageElement element, String key) {
        return RENDER_DATA.getOrDefault(element, new HashMap<>()).get(key);
    }

    public static <T> void putData(IPageElement element, String key, T data) {
        Map<String, Object> dataMap = RENDER_DATA.getOrDefault(element, new HashMap<>());
        dataMap.put(key, data);
        RENDER_DATA.put(element, dataMap);
    }

    public static void tick() {
        TICK_COUNT += 1;
    }
    public static int getTickCount() {return TICK_COUNT;}

    public static <T extends IPageElement, C extends IPageElementRenderer<T>> void renderElement(T element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        C renderer = (C) REGISTER.get(element.getClass());
        renderer.render(element, graphics, leftPos, topPos, mouseX, mouseY, partialTick, TICK_COUNT);
    }

    public static <T extends IPageElement, C extends IPageElementRenderer<T>> boolean isHovering(T element, int mouseX, int mouseY, int leftPos, int topPos) {
        C renderer = (C) REGISTER.get(element.getClass());
        return renderer.isHovering(mouseX, mouseY, leftPos, topPos, element);
    }

    public static <T extends IPageElement, C extends IPageElementRenderer<T>> void handleHover(T element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        C renderer = (C) REGISTER.get(element.getClass());
        renderer.handleHover(element, graphics, leftPos, topPos, mouseX, mouseY, partialTick);
    }

    public static <T extends IPageElement, C extends IPageElementRenderer<T>> void handleClick(T element, Screen screen, double mouseX, double mouseY, int leftPos, int topPos) {
        C renderer = (C) REGISTER.get(element.getClass());
        renderer.handleClick(element, screen, mouseX, mouseY, leftPos, topPos);
    }

}
