package com.ombremoon.spellbound.common.world.spell.ruin.ice;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.BuildUpEffect;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.SBAnimation;
import com.ombremoon.spellbound.common.magic.api.buff.*;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.block.entity.IceSheetBlockEntity;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IceSkateSpell extends ChanneledSpell {
    private static final ResourceLocation ICE_SKATE_BUFF = CommonClass.customLocation("ice_skate_buff");
    public static final ResourceLocation SNOW_BOOTS = CommonClass.customLocation("snow_boots");
    private static final ResourceLocation KINETIC_FLOW = CommonClass.customLocation("kinetic_flow");
    private static final ResourceLocation CRYSTAL_CLEATS = CommonClass.customLocation("crystal_cleats");
    private static final ResourceLocation CRYSTAL_CLEATS_SLOW = CommonClass.customLocation("crystal_cleats_slow");
    private static final BlockPredicate BASE_PREDICATE =
            BlockPredicate.anyOf(
                    BlockPredicate.allOf(
                            BlockPredicate.anyOf(
                                    BlockPredicate.matchesTag(new Vec3i(0, 0, 0), BlockTags.AIR),
                                    BlockPredicate.replaceable()
                            ),
                            BlockPredicate.not(BlockPredicate.matchesTag(new Vec3i(0, -1, 0), BlockTags.AIR)),
                            BlockPredicate.not(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), SBBlocks.ICE_SHEET.get())),
                            BlockPredicate.not(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.WATER)),
                            BlockPredicate.not(BlockPredicate.matchesFluids(new Vec3i(0, -1, 0), Fluids.WATER)),
                            BlockPredicate.not(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.ICE)),
                            BlockPredicate.not(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.BASALT)),
                            BlockPredicate.unobstructed()
                    )
            );
    private static final BlockPredicate WATER_PREDICATE =
            BlockPredicate.allOf(
                    BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                    BlockPredicate.matchesBlocks(Blocks.WATER),
                    BlockPredicate.matchesFluids(Fluids.WATER),
                    BlockPredicate.unobstructed()
            );
    private static final BlockPredicate LAVA_PREDICATE =
            BlockPredicate.allOf(
                    BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                    BlockPredicate.matchesBlocks(Blocks.LAVA),
                    BlockPredicate.matchesFluids(Fluids.LAVA),
                    BlockPredicate.unobstructed()
            );
    private static final BlockPredicate AIR_PREDICATE =
            BlockPredicate.allOf(
                    BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                    BlockPredicate.matchesBlocks(Blocks.AIR),
                    BlockPredicate.matchesFluids(Fluids.EMPTY),
                    BlockPredicate.unobstructed()
            );
    private BlockPos position = BlockPos.ZERO;
    private int offGroundTicks;
    private int movementTicks;
    private int stopMovementTicks;
    private int bridgeTicks = 60;

    public static Builder<IceSkateSpell> createIceSkateBuilder() {
        return createChannelledSpellBuilder(IceSkateSpell.class)
                .castCondition((context, iceSkateSpell) -> context.getCaster().onGround() && !context.getLevel().dimensionType().ultraWarm())
                .castAnimation((context, spell) -> new SBAnimation("solar_ray_cast", SBAnimation.Type.CAST, true))
                .channelAnimation(context -> new SBAnimation("solar_ray_channel", SBAnimation.Type.CHANNEL, true))
                .spellLayer((context, spell) -> CommonClass.customLocation("ice_skate"))
                .stopChannelAnimation(new SBAnimation("solar_ray_end", SBAnimation.Type.CAST, true));

    }
    public IceSkateSpell() {
        super(SBSpells.ICE_SKATE.get(), createIceSkateBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.ICE_SKATE,
                SkillTooltip.MANA_TICK_COST.tooltip(this.getManaTickCost()),
                SkillTooltip.RADIUS.tooltip(3F),
                SkillTooltip.NEW_LINE.tooltip(),
                SkillTooltip.CHANNELED.tooltip());
        this.addSkillDetails(SBSkills.FRICTIONLESS,
                MODIFY_SPEED.tooltip(5F));
        this.addSkillDetails(SBSkills.ICE_CLEATS,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FROST, this.getModifiedDamage(1.5F))));
        this.addSkillDetails(SBSkills.FROZEN_FEET,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FROST, this.getModifiedDamage(1F))),
                SkillTooltip.PROC_DURATION.tooltip(20));
        this.addSkillDetails(SBSkills.ICE_SNARE,
                SkillTooltip.FROST_BUILD_UP.tooltip(potency(5F)),
                SkillTooltip.POTENCY_SCALING.tooltip());
        this.addSkillDetails(SBSkills.SNOW_BOOTS,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(SNOW_BOOTS, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.EFFECT_DURATION.tooltip(40));
        this.addSkillDetails(SBSkills.KINETIC_FLOW,
                SkillTooltip.POTENCY.tooltip(20F),
                SkillTooltip.PROC_DURATION.tooltip(100));
        this.addSkillDetails(SBSkills.SHARDS_OF_MOMENTUM,
                SkillTooltip.SPELL.tooltip(SBSpells.ICE_BOLT.get()),
                SkillTooltip.PROJECTILE_COUNT.tooltip(8));
        this.addSkillDetails(SBSkills.CRYSTAL_CLEATS,
                SkillTooltip.ATTRIBUTE_PER_CHARGE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(CRYSTAL_CLEATS, -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.EFFECT_DURATION.tooltip(40),
                SkillTooltip.FROST_BUILD_UP_PER_CHARGE.tooltip(potency(5F)),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.PROC_DURATION.tooltip(20),
                SkillTooltip.MAX_CHARGES.tooltip(5));
        this.addSkillDetails(SBSkills.ICE_BRIDGE,
                SkillTooltip.EFFECT_DURATION.tooltip(60));
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.addSkillBuff(
                    caster,
                    SBSkills.ICE_SKATE,
                    ICE_SKATE_BUFF,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(Attributes.STEP_HEIGHT, new AttributeModifier(ICE_SKATE_BUFF, 0.6, AttributeModifier.Operation.ADD_VALUE))
            );
            this.position = caster.blockPosition();
            BlockState state = level.getBlockState(this.position.below());
            if (!state.is(Blocks.ICE) && !state.is(Blocks.BASALT) && level.setBlockAndUpdate(this.position, SBBlocks.ICE_SHEET.get().defaultBlockState())) {
                BlockEntity blockEntity = level.getBlockEntity(this.position);
                if (blockEntity instanceof IceSheetBlockEntity iceSheet) {
                    iceSheet.setOwner(caster, context.hasSkill(SBSkills.FROZEN_FEET), context.hasSkill(SBSkills.ICE_SNARE));
                }
            }

            if (context.hasSkill(SBSkills.CRYSTAL_CLEATS)) {
                this.addEventBuff(
                        caster,
                        SBSkills.CRYSTAL_CLEATS,
                        BuffCategory.BENEFICIAL,
                        SpellEventListener.Events.DEALT_DAMAGE_POST,
                        CRYSTAL_CLEATS,
                        post -> {
                            int cleatCharges = Math.min(this.movementTicks / 20, 5);
                            LivingEntity target = post.getTarget();
                            if (cleatCharges > 0 && target != null) {
                                this.addSkillBuff(
                                        target,
                                        SBSkills.CRYSTAL_CLEATS,
                                        CRYSTAL_CLEATS_SLOW,
                                        BuffCategory.HARMFUL,
                                        SkillBuff.ATTRIBUTE_MODIFIER,
                                        new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(CRYSTAL_CLEATS_SLOW, -0.05 * cleatCharges, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                        40
                                );

                                var effects = SpellUtil.getSpellEffects(target);
                                effects.incrementBuildEffects(caster, BuildUpEffect.FROST, potency(5) * cleatCharges);
                            }
                            this.removeSkillBuff(caster, SBSkills.CRYSTAL_CLEATS);
                        });
            }
            this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("ice_skate"),
                    caster.getId(), EntityEffectExecutor.AutoRotate.NONE).setOffset(0, -0.3, 0));
            level.playSound(null, context.getCaster().blockPosition(), SoundEvents.GLASS_PLACE,
                    SoundSource.PLAYERS,0.4F + level.random.nextFloat() * 0.2F ,0.8F + level.random.nextFloat() * 0.2F);
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        double yaw = Math.toRadians(caster.getYRot());
        Vec3 horizontalDirection = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
        if (horizontalDirection.lengthSqr() > 1.0) {
            horizontalDirection = horizontalDirection.normalize();
        }

        double speedMod = context.hasSkill(SBSkills.FRICTIONLESS) ? 1.3 : 1.25;
        double strength = 0.98F * speedMod;
        Vec3 motion = horizontalDirection.scale(strength).add(0, -0.75, 0);
        caster.setDeltaMovement(motion);
        caster.hurtMarked = true;

        if (!level.isClientSide) {
            Vec3 origin = caster.position();
            BlockPos blockpos = BlockPos.containing(origin);
            int i = 3;
            boolean flag = false;
            if (caster.blockPosition() != this.position) {
                for (BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, 0, -i), blockpos.offset(i, 0, i))) {
                    if (blockpos1.distToCenterSqr(origin.x(), (double) blockpos1.getY() + 0.5, origin.z()) < (double) Mth.square(i)) {
                        if (WATER_PREDICATE.test((ServerLevel) level, blockpos1.below())) {
                            level.setBlockAndUpdate(blockpos1.below(), Blocks.ICE.defaultBlockState());
                        } else if (context.hasSkill(SBSkills.FROSTIER_WALKER) && LAVA_PREDICATE.test((ServerLevel) level, blockpos1.below())) {
                            level.setBlockAndUpdate(blockpos1.below(), Blocks.BASALT.defaultBlockState());
                        } else if (context.hasSkill(SBSkills.ICE_BRIDGE) && caster.isCrouching() && this.bridgeTicks > 0 && AIR_PREDICATE.test((ServerLevel) level, blockpos1.below())) {
                            level.setBlockAndUpdate(blockpos1.below(), Blocks.ICE.defaultBlockState());
                            flag = true;
                        } else if (BASE_PREDICATE.test((ServerLevel) level, blockpos1)
                                && level.setBlockAndUpdate(blockpos1, SBBlocks.ICE_SHEET.get().defaultBlockState())) {
                            BlockEntity blockEntity = level.getBlockEntity(blockpos1);
                            if (blockEntity instanceof IceSheetBlockEntity iceSheet) {
                                iceSheet.setOwner(caster, context.hasSkill(SBSkills.FROZEN_FEET), context.hasSkill(SBSkills.ICE_SNARE));
                            }
                        }
                    }
                }

                if (flag) {
                    this.bridgeTicks--;
                } else if (this.movementTicks % 100 == 0){
                    this.bridgeTicks = Math.min(this.bridgeTicks + 5, 60);
                }

                this.position = caster.blockPosition();
                this.movementTicks++;
                this.stopMovementTicks = 0;
            } else {
                this.stopMovementTicks++;
                this.movementTicks = 0;
                if (this.stopMovementTicks > 5) {
                    this.endSpell();
                    return;
                }
            }

            if (context.hasSkill(SBSkills.ICE_CLEATS)) {
                var entities = this.getAttackableEntities(1.5);
                for (LivingEntity living : entities) {
                    living.knockback(0.4, caster.getX() - living.getX(), caster.getZ() - living.getZ());
                    living.hurtMarked = true;
                    this.hurt(living, 1.5F);
                    this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("ice_cleats"),
                            living.getId(), EntityEffectExecutor.AutoRotate.NONE).setOffset(0, -0.1, 0));
                    level.playSound(null, context.getCaster().blockPosition(), SoundEvents.GLASS_BREAK,
                            SoundSource.PLAYERS,0.4F + level.random.nextFloat() * 0.2F ,1.0F + level.random.nextFloat() * 0.2F );
                }
            }

            if (!caster.onGround()) {
                this.offGroundTicks++;
                if (this.offGroundTicks > 10) {
                    this.endSpell();
                }
            } else {
                this.offGroundTicks = 0;
            }

            if (this.movementTicks % 100 == 0 && context.hasSkill(SBSkills.KINETIC_FLOW)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.KINETIC_FLOW,
                        KINETIC_FLOW,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.SPELL_MODIFIER,
                        SpellModifier.KINETIC_FLOW,
                        100
                );
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide) {
            this.removeSkillBuff(context.getCaster(), SBSkills.ICE_SKATE);
            removeSpellFX(CommonClass.customLocation("ice_skate"));

            if (context.hasSkill(SBSkills.SHARDS_OF_MOMENTUM)) {
                IceBoltSpell spell = SBSpells.ICE_BOLT.get().createSpellWithData(caster);
                int count = 8;
                for (int i = 0; i < count; i++) {
                    float yRot = this.getBoltAngle(caster.getYRot(), i, count);
                    spell.shootProjectile(context, SBEntities.ICE_BOLT.get(), 0, yRot, 1.25F, 1.0F, iceBolt -> iceBolt.setSize(level.random.nextInt(3)));
                }
            }
        }
    }

    private float getBoltAngle(float startAngle, int i, int count) {
        float spread = 15.0F;
        float totalSpread = spread * (count - 1);
        float startOffset = -totalSpread / 2.0F;
        return startAngle + startOffset + (i * spread);
    }

    public static SkillTooltip<Float> MODIFY_SPEED = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.modify_skate_speed", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
