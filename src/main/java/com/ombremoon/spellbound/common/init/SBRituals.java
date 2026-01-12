package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.effects.CreateItem;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.effects.CreateSpellTome;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public interface SBRituals {
    ResourceKey<TransfigurationRitual> CREATE_STRIDE = key("create_stride");
    ResourceKey<TransfigurationRitual> CREATE_SHADOW_GATE = key("create_shadow_gate");
    ResourceKey<TransfigurationRitual> CREATE_MYSTIC_ARMOR = key("create_mystic_armor");
    ResourceKey<TransfigurationRitual> CREATE_TRANSFIG_HELM = key("create_transfig_helmet");
    ResourceKey<TransfigurationRitual> CREATE_TRANSFIG_CHEST = key("create_transfig_chestplate");
    ResourceKey<TransfigurationRitual> CREATE_TRANSFIG_LEGS = key("create_transfig_leggings");
    ResourceKey<TransfigurationRitual> CREATE_TRANSFIG_BOOTS = key("create_transfig_boots");
    ResourceKey<TransfigurationRitual> CREATE_TRANSFIG_STAVE = key("create_transfig_stave");
    ResourceKey<TransfigurationRitual> CREATE_MANA_TEAR = key("create_mana_tear");

    static void bootstrap(BootstrapContext<TransfigurationRitual> context) {
        register(
                context,
                CREATE_STRIDE,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.LEATHER_BOOTS))
                        .requires(Ingredient.of(Items.SUGAR))
                        .requires(Ingredient.of(Items.FEATHER))
                        .requires(Ingredient.of(Items.LAPIS_LAZULI))
                        .withEffect(new CreateSpellTome(SBSpells.STRIDE.get(), 1))

        );
        register(
                context,
                CREATE_SHADOW_GATE,
                TransfigurationRitual.ritual(2, 200, 1)
                        .requires(Ingredient.of(Items.IRON_DOOR), 2)
                        .requires(Ingredient.of(Items.ENDER_PEARL), 2)
                        .requires(Ingredient.of(Items.FEATHER), 2)
                        .requires(Ingredient.of(Items.INK_SAC), 2)
                        .requires(Ingredient.of(Items.BLACKSTONE), 2)
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()), 2)
                        .withEffect(new CreateSpellTome(SBSpells.SHADOW_GATE.get(), 2))

        );
        register(
                context,
                CREATE_MYSTIC_ARMOR,
                TransfigurationRitual.ritual(2, 200, 1)
                        .requires(Ingredient.of(Items.DIAMOND_CHESTPLATE))
                        .requires(Ingredient.of(Items.CACTUS))
                        .requires(Ingredient.of(Items.OBSIDIAN), 2)
                        .requires(Ingredient.of(Items.SHIELD), 2)
                        .requires(Ingredient.of(Items.AMETHYST_SHARD), 2)
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()), 4)
                        .withEffect(new CreateSpellTome(SBSpells.MYSTIC_ARMOR.get(), 2))

        );

        register(context,
                CREATE_MANA_TEAR,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.DIAMOND))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()), 3)
                        .withEffect(new CreateItem(SBItems.MANA_TEAR.get())));

        register(context,
                CREATE_TRANSFIG_HELM,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.LEATHER_HELMET))
                        .requires(Ingredient.of(Items.FLOWERING_AZALEA_LEAVES))
                        .requires(Ingredient.of(Items.EMERALD))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()))
                        .withEffect(new CreateItem(SBItems.CREATIONIST_HELMET.get())));
        register(context,
                CREATE_TRANSFIG_CHEST,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.LEATHER_CHESTPLATE))
                        .requires(Ingredient.of(Items.FLOWERING_AZALEA_LEAVES))
                        .requires(Ingredient.of(Items.EMERALD))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()))
                        .withEffect(new CreateItem(SBItems.CREATIONIST_CHESTPLATE.get())));
        register(context,
                CREATE_TRANSFIG_LEGS,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.LEATHER_LEGGINGS))
                        .requires(Ingredient.of(Items.FLOWERING_AZALEA_LEAVES))
                        .requires(Ingredient.of(Items.EMERALD))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()))
                        .withEffect(new CreateItem(SBItems.CREATIONIST_LEGGINGS.get())));
        register(context,
                CREATE_TRANSFIG_BOOTS,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.LEATHER_BOOTS))
                        .requires(Ingredient.of(Items.FLOWERING_AZALEA_LEAVES))
                        .requires(Ingredient.of(Items.EMERALD))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()))
                        .withEffect(new CreateItem(SBItems.CREATIONIST_BOOTS.get())));
        register(context,
                CREATE_TRANSFIG_STAVE,
                TransfigurationRitual.ritual(1)
                        .requires(Ingredient.of(Items.IRON_SWORD))
                        .requires(Ingredient.of(Items.FLOWERING_AZALEA_LEAVES))
                        .requires(Ingredient.of(Items.EMERALD))
                        .requires(Ingredient.of(SBItems.MAGIC_ESSENCE.get()))
                        .withEffect(new CreateItem(SBItems.CREATIONIST_STAFF.get())));
    }

    private static void register(BootstrapContext<TransfigurationRitual> context, ResourceKey<TransfigurationRitual> key, TransfigurationRitual.Builder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<TransfigurationRitual> key(String name) {
        return ResourceKey.create(Keys.RITUAL, CommonClass.customLocation(name));
    }
}
