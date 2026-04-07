package com.ombremoon.spellbound.common.world.spell.summon;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import com.ombremoon.spellbound.common.magic.api.buff.*;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SummonVillagerSpell extends SummonSpell implements RadialSpell, ChargeableSpell {
    private static final SpellDataKey<Integer> VILLAGER_ID = SyncedSpellData.registerDataKey(SummonVillagerSpell.class, SBDataTypes.INT.get());
    public static final ResourceLocation FARMER = CommonClass.customLocation("farmer");
    public static final ResourceLocation LIBRARIAN = CommonClass.customLocation("librarian");
    public static final ResourceLocation CARTOGRAPHER = CommonClass.customLocation("cartographer");
    public static final ResourceLocation TOOLSMITH_DAMAGE = CommonClass.customLocation("toolsmith_damage");
    public static final ResourceLocation TOOLSMITH_SPEED = CommonClass.customLocation("toolsmith_speed");
    public static final ResourceLocation CLERIC = CommonClass.customLocation("cleric");
    private boolean usesCartographer = false;

    private static Builder<?> createSummonVillagerBuilder() {
        return createSummonBuilder(SummonVillagerSpell.class)
                .duration(6000)
                .fullRecast(true);
    }

    public SummonVillagerSpell() {
        super(SBSpells.SUMMON_VILLAGER.get(), createSummonVillagerBuilder());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(VILLAGER_ID, 0);
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.SUMMON_VILLAGER,
                SkillTooltip.DURATION.tooltip(6000),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip(),
                SkillTooltip.CAST_SCALES.tooltip()
        );
        this.addSkillDetails(SBSkills.FARMER_VILLAGER,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.LIBRARIAN_VILLAGER,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.TOOLSMITH_VILLAGER,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.CARTOGRAPHER_VILLAGER,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.CLERIC_VILLAGER,
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.CHARGED.tooltip()
        );
        this.addSkillDetails(SBSkills.EXTENDED_SERVICE,
                SkillTooltip.MODIFY_DURATION.tooltip(50F)
        );
        this.addSkillDetails(SBSkills.SHOW_ME_THE_ROPES,
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.FARMER_VILLAGER, INCREASE_CROP_YIELD.tooltip())
                ),
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.LIBRARIAN_VILLAGER, SkillTooltip.MODIFY_SPELL_XP.tooltip(25F))
                ),
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.TOOLSMITH_VILLAGER,
                                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ATTACK_DAMAGE, new AttributeModifier(TOOLSMITH_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE))),
                                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.ATTACK_SPEED, new AttributeModifier(TOOLSMITH_SPEED, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                        )
                ),
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.CARTOGRAPHER_VILLAGER, SkillTooltip.MOB_EFFECT.tooltip(SBEffects.TARGET_AURA))
                ),
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.FARMER_VILLAGER, SkillTooltip.MOB_EFFECT.tooltip(MobEffects.REGENERATION))
                )
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.summonEntity(context, EntityType.VILLAGER, villager -> {
                VillagerData data = villager.getVillagerData();
                VillagerProfession profession = VillagerProfession.NONE;
                if (this.isChoice(SBSkills.FARMER_VILLAGER)) {
                    profession = VillagerProfession.FARMER;
                } else if (this.isChoice(SBSkills.LIBRARIAN_VILLAGER)) {
                    profession = VillagerProfession.LIBRARIAN;
                } else if (this.isChoice(SBSkills.TOOLSMITH_VILLAGER)) {
                    profession = VillagerProfession.TOOLSMITH;
                } else if (this.isChoice(SBSkills.CARTOGRAPHER_VILLAGER)) {
                    profession = VillagerProfession.CARTOGRAPHER;
                } else if (this.isChoice(SBSkills.CLERIC_VILLAGER)) {
                    profession = VillagerProfession.CLERIC;
                }
                this.spellData.set(VILLAGER_ID, villager.getId());

                villager.setVillagerData(data.setProfession(profession).setLevel(this.getCharges()));
                villager.setVillagerXp(1000);
                villager.refreshBrain((ServerLevel)level);

                MerchantOffers offers = villager.getOffers();
                if (context.hasSkill(SBSkills.WHOLESALE)) {
                    for (MerchantOffer offer : offers) {
                        double d0 = 0.3 + 0.0625 ;
                        int j = (int)Math.floor(d0 * (double)offer.getBaseCostA().getCount());
                        offer.addToSpecialPriceDiff(-Math.max(j, 1));
                    }
                }

                if (context.hasSkill(SBSkills.LOYAL_PROTECTOR) && context.hasCatalyst(SBItems.SOUL_SHARD.get())) {
                    Vec3 pos = LandRandomPos.getPos(villager, 3, 3);
                    this.summonEntity(context, EntityType.IRON_GOLEM, pos);
                }

                if (context.hasSkill(SBSkills.SHOW_ME_THE_ROPES) && context.hasCatalyst(SBItems.SOUL_SHARD.get())){
                    if (profession == VillagerProfession.FARMER) {
                        this.addEventBuff(
                                caster,
                                SBSkills.SHOW_ME_THE_ROPES,
                                BuffCategory.BENEFICIAL,
                                SpellEventListener.Events.BLOCK_DROPS,
                                FARMER,
                                dropsEvent -> {
                                    if (dropsEvent.getBlockState().is(BlockTags.CROPS)) {
                                        dropsEvent.getDrops().forEach(itemEntity -> {
                                            int i = level.getRandom().nextInt(3);
                                            for (int j = 0; j < i; j++) {
                                                ItemEntity entity = itemEntity.copy();
                                                level.addFreshEntity(entity);
                                            }
                                        });
                                    }
                                }
                        );
                    } else if (profession == VillagerProfession.LIBRARIAN) {
                        this.addSkillBuff(
                                caster,
                                SBSkills.SHOW_ME_THE_ROPES,
                                LIBRARIAN,
                                BuffCategory.BENEFICIAL,
                                SkillBuff.SPELL_MODIFIER,
                                SpellModifier.LIBRARIAN
                        );
                    } else if (profession == VillagerProfession.TOOLSMITH) {
                        this.addSkillBuff(
                                caster,
                                SBSkills.SHOW_ME_THE_ROPES,
                                TOOLSMITH_DAMAGE,
                                BuffCategory.BENEFICIAL,
                                SkillBuff.ATTRIBUTE_MODIFIER,
                                new ModifierData(Attributes.ATTACK_DAMAGE, new AttributeModifier(TOOLSMITH_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE))
                        );
                        this.addSkillBuff(
                                caster,
                                SBSkills.SHOW_ME_THE_ROPES,
                                TOOLSMITH_SPEED,
                                BuffCategory.BENEFICIAL,
                                SkillBuff.ATTRIBUTE_MODIFIER,
                                new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(TOOLSMITH_SPEED, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        );
                    } else if (profession == VillagerProfession.CARTOGRAPHER) {
                        this.usesCartographer = true;
                    } else if (profession == VillagerProfession.CLERIC) {
                        this.addSkillBuff(
                                caster,
                                SBSkills.SHOW_ME_THE_ROPES,
                                CLERIC,
                                BuffCategory.BENEFICIAL,
                                SkillBuff.MOB_EFFECT,
                                new MobEffectInstance(MobEffects.REGENERATION, -1, 0, false, false)
                        );
                    }
                }
            });
            level.playSound(null, context.getCaster().blockPosition(), SoundEvents.VILLAGER_YES,
                    SoundSource.PLAYERS, 0.4F, 1F);
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide && this.usesCartographer) {
            var list = this.getAttackableEntities(20);
            for (LivingEntity entity : list) {
                this.addSkillBuff(
                        entity,
                        SBSkills.SHOW_ME_THE_ROPES,
                        CARTOGRAPHER,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.MOB_EFFECT,
                        new SBEffectInstance(caster, SBEffects.TARGET_AURA, 60, true,  0, false, false),
                        60
                );
            }
        }
    }

    @Override
    public void onMobRemoved(LivingEntity entity, SpellContext context, @Nullable DamageSource source, Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.KILLED && entity instanceof Villager villager) {
            if (context.hasSkill(SBSkills.BOUNTIFUL) && source != null && source.getEntity() instanceof LivingEntity attacker && !SpellUtil.IS_ALLIED.test(villager, attacker)) {
                MerchantOffer offer = villager.getOffers().get(villager.level().getRandom().nextInt(villager.getOffers().size()));
                ItemStack itemStack = offer.assemble();
                villager.spawnAtLocation(itemStack);
            }
        }
    }

    @Override
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return this.tickCount % 60 == 0;
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.EXTENDED_SERVICE) ? 8400 : super.getDuration(context);
    }

    @Override
    public int getCastTime(SpellContext context) {
        return context.getSpellLevel() > 0 ? 10 * context.getSpellLevel() : super.getCastTime(context);
    }

    @Override
    public int maxCharges(SpellContext context) {
        return context.getSpellLevel();
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return true;
    }

    public static SkillTooltip<Unit> INCREASE_CROP_YIELD = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Unit arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.farmer_villager").withStyle(ChatFormatting.GRAY)));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
