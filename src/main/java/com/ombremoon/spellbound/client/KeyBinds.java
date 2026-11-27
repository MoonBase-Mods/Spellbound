package com.ombremoon.spellbound.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String KEY_CATEGORY_SPELLBOUND = "key.category." + Constants.MOD_ID;
    public static final String KEY_SWITCH_MODE = "key." + Constants.MOD_ID + ".switch_mode";
    public static final String KEY_SELECT_SPELL = "key." + Constants.MOD_ID + ".select_spell";
    public static final String KEY_CYCLE_SPELL = "key." + Constants.MOD_ID + ".cycle_spell";
    public static final String KEY_CYCLE_CHOICE = "key." + Constants.MOD_ID + ".cycle_choice";

    public static final KeyMapping SWITCH_MODE_BINDING = new KeyMapping(KEY_SWITCH_MODE, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_SPELLBOUND);
    public static final KeyMapping SELECT_SPELL_BINDING = new KeyMapping(KEY_SELECT_SPELL, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, KEY_CATEGORY_SPELLBOUND);
    public static final KeyMapping CYCLE_SPELL_BINDING = new KeyMapping(KEY_CYCLE_SPELL, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY_SPELLBOUND);
    public static final KeyMapping CYCLE_CHOICE_BINDING = new KeyMapping(KEY_CYCLE_CHOICE, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, KEY_CATEGORY_SPELLBOUND);

    public static KeyMapping getSpellCastMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyUse;
    }

    public static KeyMapping getAttackMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyAttack;
    }

    public static KeyMapping getUpMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyUp;
    }

    public static KeyMapping getLeftMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyLeft;
    }

    public static KeyMapping getRightMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyRight;
    }

    public static KeyMapping getDownMapping() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.keyDown;
    }

    public enum MovementKey implements StringRepresentable {
        UP("up"),
        DOWN("down"),
        LEFT("left"),
        RIGHT("right");

        public static final StringRepresentable.EnumCodec<MovementKey> CODEC = StringRepresentable.fromEnum(MovementKey::values);
        private final String name;

        MovementKey(String name) {
            this.name = name;
        }

        public static MovementKey getFromName(String name) {
            return CODEC.byName(name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
