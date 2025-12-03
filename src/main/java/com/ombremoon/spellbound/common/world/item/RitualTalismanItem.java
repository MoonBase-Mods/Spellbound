package com.ombremoon.spellbound.common.world.item;

import net.minecraft.world.item.Item;

public class RitualTalismanItem extends Item {
    public RitualTalismanItem(Properties properties) {
        super(properties);
    }

    public enum Rings {
        ONE("one"),
        TWO("two"),
        THREE("three");

        private final String name;

        Rings(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
