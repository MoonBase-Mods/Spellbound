package com.ombremoon.spellbound.common.magic.tree;

import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.networking.clientbound.UpdateTreePayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class UpgradeTree implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = Constants.LOG;
    private final Map<ResourceLocation, SkillNode> nodes = new Object2ObjectOpenHashMap<>();
    private final Set<SkillNode> roots = new ObjectLinkedOpenHashSet<>();
    private final Set<SkillNode> children = new ObjectLinkedOpenHashSet<>();

    private void remove(SkillNode node) {
        for (SkillNode skillNode : node.children()) {
            this.remove(skillNode);
        }

        LOGGER.info("Forgot about upgrade {}", node.skill());
        this.nodes.remove(node.skill().location());
        if (node.parents().isEmpty()) {
            this.roots.remove(node);
        } else {
            this.children.remove(node);
        }
    }

    public void remove(Set<ResourceLocation> skills) {
        for (ResourceLocation location : skills) {
            SkillNode node = this.nodes.get(location);
            if (node == null) {
                LOGGER.warn("Told to remove upgrade {} but does not exist", location);
            } else {
                this.remove(node);
            }
        }
    }

    public void addAll(Collection<Skill> skills) {
        List<Skill> list = new ArrayList<>(skills);

        while (!list.isEmpty()) {
            if (!list.removeIf(this::addNode)) {
                LOGGER.error("Couldn't load skills: {}", list);
                break;
            }
        }

        LOGGER.info("Loaded {} skills", this.nodes.size());
    }

    private boolean addNode(Skill skill) {
        final List<SkillNode> nodes = new ObjectArrayList<>();
        HolderSet<Skill> parents = skill.getPrereqs();
        if (parents != null) {
            var locs = parents.stream().map(Holder::value).map(Skill::location).collect(Collectors.toSet());
            for (var loc : locs) {
                SkillNode skillNode = this.nodes.get(loc);
                if (skillNode == null && loc != null) return false;
                if (!nodes.contains(skillNode)) nodes.add(skillNode);
            }
        }

        SkillNode skillNode = new SkillNode(skill, nodes);
        nodes.forEach(node -> {
            if (node != null) node.addChild(skillNode);
        });

        this.nodes.put(skill.location(), skillNode);
        if (nodes.isEmpty()) {
            this.roots.add(skillNode);
        } else {
            this.children.add(skillNode);
        }
        return true;
    }

    private void clear() {
        this.nodes.clear();
        this.roots.clear();
        this.children.clear();
    }

    public void clear(Player player) {
        clear();
        update(player, true, List.of(), Set.of());
    }

    public void refreshTree(Player player) {
        Set<ResourceLocation> toRemove = new ObjectOpenHashSet<>();
        for (SkillNode node : this.roots) {
            Skill skill = node.skill();
            if (skill == null || skill.getSpell() == null) {
                toRemove.add(node.skill().location());
                this.remove(node);
            }
        }

        update(player, this.getUnlockedSkills(), toRemove);
    }

    public void update(Player player, boolean reset, List<Skill> added, Set<ResourceLocation> removed) {
        PayloadHandler.updateTree(player, reset, added, removed);
    }

    public void update(Player player, List<Skill> added, Set<ResourceLocation>  removed) {
        update(player, false, added, removed);
    }

    public void update(Player player, List<Skill> added) {
        update(player, added, Set.of());
    }

    public void update(Player player, Set<ResourceLocation> removed) {
        update(player, List.of(), removed);
    }

    public void update(UpdateTreePayload payload) {
        if (payload.reset()) this.clear();

        this.addAll(payload.added());
        this.remove(payload.removed());
    }

    public Set<SkillNode> roots() {
        return this.roots;
    }

    public Set<SkillNode> children() {
        return this.children;
    }

    public Collection<SkillNode> nodes() {
        return this.nodes.values();
    }

    public SkillNode get(ResourceLocation location) {
        return this.nodes.get(location);
    }

    public SkillNode get(Skill skill) {
        return this.nodes.get(skill.location());
    }

    public List<Skill> getUnlockedSkills() {
        return this.nodes().stream().map(SkillNode::skill).toList();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag nodeTag = new ListTag();
        ListTag rootTag = new ListTag();
        ListTag childTag = new ListTag();
        this.nodes.forEach((resourceLocation, node) -> {
            if (node != null) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("Id", resourceLocation.toString());
                compoundTag.put("Node", node.save());
                nodeTag.add(compoundTag);
            }
        });
        this.roots.forEach(node -> {
            if (node != null) rootTag.add(node.save());
        });
        this.children.forEach(node -> {
            if (node != null) childTag.add(node.save());
        });
        nbt.put("Nodes", nodeTag);
        nbt.put("Roots", rootTag);
        nbt.put("Children", childTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("Nodes", 9)) {
            ListTag nodeList = nbt.getList("Nodes", 10);
            for (int i = 0; i < nodeList.size(); i++) {
                CompoundTag tag = nodeList.getCompound(i);
                ResourceLocation location = ResourceLocation.tryParse(tag.getString("Id"));
                SkillNode skillNode = SkillNode.load(tag.getCompound("Node"));
                this.nodes.put(location, skillNode);
            }
        }
        if (nbt.contains("Roots", 9)) {
            ListTag rootList = nbt.getList("Roots", 10);
            for (int i = 0; i < rootList.size(); i++) {
                CompoundTag tag = rootList.getCompound(i);
                this.roots.add(SkillNode.load(tag));
            }
        }
        if (nbt.contains("Children", 9)) {
            ListTag childList = nbt.getList("Children", 10);
            for (int i = 0; i < childList.size(); i++) {
                CompoundTag tag = childList.getCompound(i);
                this.children.add(SkillNode.load(tag));
            }
        }
    }
}
