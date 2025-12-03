package com.ombremoon.spellbound.common.magic.skills;

import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class Skill {
    private final int xPos;
    private final int yPos;
    @Nullable
    private final HolderSet<Skill> prerequisites;
    private String nameID;
    private String descriptionID;

    public Skill() {
        this(0, 0, null);
    }

    public Skill(int xPos, int yPos, @Nullable HolderSet<Skill> prerequisites) {
        if (xPos > 100 || xPos < -100) throw new IllegalArgumentException("X position must be between the values -100 and 100.");
        this.xPos = xPos;
        this.yPos = yPos;
        this.prerequisites = prerequisites;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public HolderSet<Skill> getPrereqs() {
        return this.prerequisites;
    }

    protected String getOrCreateNameId() {
        if (this.nameID == null) {
            this.nameID = Util.makeDescriptionId("skill", this.location());
        }
        return this.nameID;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionID == null) {
            this.descriptionID = Util.makeDescriptionId("skill.description", this.location());
        }
        return this.descriptionID;
    }

    public ResourceLocation location() {
        return SBSkills.REGISTRY.getKey(this);
    }

    public String getNameId() {
        return this.getOrCreateNameId();
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public MutableComponent getName() {
        return Component.translatable(this.getNameId());
    }

    public MutableComponent getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    public ResourceLocation getTexture() {
        String root = getSpell().location().getPath();
        return CommonClass.customLocation("textures/gui/skills/" + root + "/" + location().getPath() + ".png");
    }

    public void onSkillUnlock(Player player) {}

    public boolean canUnlockSkill(Player player, SkillHolder holder) {
        return true;
    }

    public boolean isRadial() {
        return false;
    }

    public boolean isRoot() {
        return this.prerequisites == null;
    }

    public Skill getRoot() {
        Skill root = this;
        while (true) {
            if (root.isRoot())
                return root;

            var prereqs = root.getPrereqs();
            root = prereqs.stream().map(Holder::value).toList().getFirst();
        }
    }

    public static Skill byName(ResourceLocation resourceLocation) {
        return SBSkills.REGISTRY.get(resourceLocation);
    }

    public SpellType<?> getSpell() {
        return AbstractSpell.getSpellByName(CommonClass.customLocation(getRoot().location().getPath()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof Skill skill && this.location().equals(skill.location());
    }

    @Override
    public int hashCode() {
        return this.location().hashCode();
    }

    @Override
    public String toString() {
        return this.location().toString();
    }
}
