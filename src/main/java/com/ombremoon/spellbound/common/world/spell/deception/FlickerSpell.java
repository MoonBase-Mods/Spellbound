package com.ombremoon.spellbound.common.world.spell.deception;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.living.LivingShadow;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;

public class FlickerSpell extends AnimatedSpell implements RadialSpell {
    private static final ResourceLocation STEP_INTO_SHADOW = CommonClass.customLocation("step_into_shadow");
    private static final ResourceLocation LOOK_OVER_HERE = CommonClass.customLocation("look_over_here");
    private static final SpellDataKey<Integer> LIVING_SHADOW = SyncedSpellData.registerDataKey(FlickerSpell.class, SBDataTypes.INT.get());
    private Vec3 teleportLocation;
    private boolean down;
    private boolean right;
    private boolean left;

    private static Builder<FlickerSpell> createFlickerBuilder() {
        return createSimpleSpellBuilder(FlickerSpell.class)
                .duration(5)
                .manaCost(27)
                .castTime(5)
                .castAnimation((context, spell) -> {
                    boolean flag = !context.hasSkill(SBSkills.SWIFT_SHADOWS);
                    String castPrefix = flag ? "" : "walking_";
                    return new SpellAnimation(castPrefix + "instant_cast", SpellAnimation.Type.CAST, flag);
                })
                .castCondition((context, flickerSpell) -> {
                    double range = flickerSpell.getCastRange();
                    if (!context.hasSkill(SBSkills.DISTANT_FLICKER))
                        range /= 2;

                    Vec3 vec3;
                    if (flickerSpell.isDirectional() && !flickerSpell.isChoice(SBSkills.LOOK_OVER_HERE)) {
                        vec3 = flickerSpell.findDirectionalTeleport(context.getLevel(), context.getCaster());
                    } else {
                        vec3 = flickerSpell.findTeleportLocation(context.getLevel(), context.getCaster(), (float) range);
                    }

                    if (vec3 != null) {
                        flickerSpell.teleportLocation = vec3;
                        return true;
                    }
                    return false;
                })
                .fullRecast(true);
    }

    public FlickerSpell() {
        super(SBSpells.FLICKER.get(), createFlickerBuilder());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(LIVING_SHADOW, 0);
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.FLICKER,
                SkillTooltip.RANGE.tooltip(this.getCastRange() / 2),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.DURATION.tooltip(this.getDuration()),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.DISTANT_FLICKER,
                SkillTooltip.MODIFY_RANGE.tooltip(100F)
        );
        this.addSkillDetails(SBSkills.STEP_INTO_SHADOW,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.MAGI_INVISIBILITY),
                SkillTooltip.EFFECT_DURATION.tooltip(80),
                SkillTooltip.INVISIBILITY_NO_STACK.tooltip()
        );
        this.addSkillDetails(SBSkills.BLINDING_MIRAGE,
                SkillTooltip.MOB_EFFECT.tooltip(MobEffects.BLINDNESS),
                SkillTooltip.EFFECT_DURATION.tooltip(40)
        );
        this.addSkillDetails(SBSkills.PHANTOM_LURE,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.TAUNT),
                SkillTooltip.EFFECT_DURATION.tooltip(40)
        );
        this.addSkillDetails(SBSkills.LOOK_OVER_HERE,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.MAGI_INVISIBILITY),
                SkillTooltip.DURATION.tooltip(80),
                SkillTooltip.CHOICE.tooltip(),
                SkillTooltip.SKILL.tooltip(SBSkills.STEP_INTO_SHADOW)
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.hasSkill(SBSkills.HALL_OF_MIRRORS) && context.isRecast())
                return;

            if (this.isChoice(SBSkills.LOOK_OVER_HERE)) {
                this.summonEntity(context, SBEntities.LIVING_SHADOW.get(), caster.position(), livingShadow -> {
                    this.setLivingShadow(livingShadow.getId());
                    livingShadow.setSetTarget(true);
                });
                if (!caster.hasEffect(SBEffects.MAGI_INVISIBILITY)) {
                    this.addSkillBuff(
                            caster,
                            SBSkills.LOOK_OVER_HERE,
                            LOOK_OVER_HERE,
                            BuffCategory.BENEFICIAL,
                            SkillBuff.MOB_EFFECT,
                            new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 80),
                            80
                    );
                }

                this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("shadow_teleport"), caster.getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setPos(Vec3.atBottomCenterOf(context.getBlockPos()))
                );
            } else {
                Vec3 vec3 = this.teleportLocation;
                if (context.hasSkill(SBSkills.CONFUSION)) {
                    this.summonEntity(context, SBEntities.LIVING_SHADOW.get(), caster.position(), livingShadow -> this.setLivingShadow(livingShadow.getId()));
                }

                caster.teleportTo(vec3.x, vec3.y, vec3.z);
                if (context.hasSkill(SBSkills.STEP_INTO_SHADOW) && !caster.hasEffect(SBEffects.MAGI_INVISIBILITY)) {
                    this.addSkillBuff(
                            caster,
                            SBSkills.STEP_INTO_SHADOW,
                            STEP_INTO_SHADOW,
                            BuffCategory.BENEFICIAL,
                            SkillBuff.MOB_EFFECT,
                            new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 80),
                            80
                    );
                }

                if (!context.hasSkill(SBSkills.SILENT_STEP))
                    level.playSound(null, caster.xo, caster.yo, caster.zo, SoundEvents.ENDERMAN_TELEPORT, caster.getSoundSource(), 1.0F, 1.0F);

                this.triggerSpellFX(EffectData.StaticEntity.of(CommonClass.customLocation("shadow_teleport"), caster.getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setPos(Vec3.atBottomCenterOf(context.getBlockPos()))
                );
            }
        }
    }

    @Override
    protected void onSpellRecast(SpellContext context) {
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        if (!level.isClientSide) {
            LivingShadow livingShadow = getLivingShadow(context);
            if (livingShadow != null) {
                this.swapTargets(caster, livingShadow);
            }
        }
    }

    private void swapTargets(LivingEntity caster, LivingShadow shadow) {
        Vec3 firstPos = caster.position();
        Vec3 secondPosPos = shadow.position();
        caster.teleportTo(secondPosPos.x, secondPosPos.y, secondPosPos.z);
        shadow.teleportTo(firstPos.x, firstPos.y, firstPos.z);
        this.spawnTeleportParticles(caster, 40);
        this.spawnTeleportParticles(shadow, 40);
        this.removeSkillBuff(caster, SBSkills.STEP_INTO_SHADOW);
        this.removeSkillBuff(caster, SBSkills.LOOK_OVER_HERE);
        this.endSpell();
    }

    private void spawnTeleportParticles(Entity entity, int amount) {
        for (int j = 0; j < amount; j++) {
            this.createSurroundingServerParticles(entity, ParticleTypes.PORTAL, 0.5);
        }
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.HALL_OF_MIRRORS) ? 80 : super.getDuration(context);
    }

    public Vec3 getTeleportLocation() {
        return this.teleportLocation;
    }

    private void setLivingShadow(int livingShadow) {
        this.spellData.set(LIVING_SHADOW, livingShadow);
    }

    private LivingShadow getLivingShadow(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(LIVING_SHADOW));
        return entity instanceof LivingShadow livingShadow ? livingShadow : null;
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public boolean shouldRender(SpellContext context) {
        return context.hasSkill(SBSkills.HALL_OF_MIRRORS);
    }

    private boolean isDirectional() {
        return this.left || this.right || this.down;
    }

    private Vec3 findDirectionalTeleport(Level level, LivingEntity caster) {
        Vec3 lookAngle = caster.getForward();
        Vec3 direction = null;
        if (this.left) {
            direction = lookAngle.cross(new Vec3(0, 1, 0)).normalize().scale(-1);
        } else if (this.right) {
            direction = lookAngle.cross(new Vec3(0, 1, 0)).normalize();
        } else if (this.down) {
            direction = lookAngle.normalize().scale(-1);
        }

        if (direction == null)
            return null;

        Vec3 targetPos = caster.position().add(direction.scale(3.0));
        Vec3 offset = direction.multiply(caster.getBbWidth() / 3, 0, caster.getBbHeight() / 3);
        Vec3 bbImpact = targetPos.subtract(offset);
        BlockPos pos = BlockPos.containing(targetPos.x, targetPos.y, targetPos.z);
        double ledgeY = targetPos.y;
        boolean isAir = level.getBlockState(BlockPos.containing(pos.getX(), ledgeY, pos.getZ()).above()).isAir();
        boolean isMiss = level.clip(new ClipContext(bbImpact, bbImpact.add(0, ledgeY - pos.getY(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster)).getType() == HitResult.Type.MISS;

        if (isAir && isMiss && Math.abs(ledgeY - pos.getY()) <= 3) {
            Vec3 resultPos = new Vec3(pos.getX() + .5, ledgeY + 0.001, pos.getZ() + .5);
            return this.checkForBlockCollision(level, resultPos, pos);
        }

        BlockHitResult teleportResult = level.clip(new ClipContext(bbImpact, bbImpact.add(0, -caster.getBbHeight(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        Vec3 resultPos = this.checkForBlockCollision(level, teleportResult.getLocation(), teleportResult.getBlockPos());
        return resultPos != null ? resultPos.add(0, 0.001, 0) : null;
    }

    private Vec3 checkForBlockCollision(Level level, Vec3 pos, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        if (this.isDirectional() && blockState.isSolid()) {
            blockState = level.getBlockState(blockPos.above());
            if (blockState.isSolid())
                return null;

            return pos.add(0, 2, 0);
        }

        return pos;
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        nbt.putInt("LivingShadowId", this.spellData.get(LIVING_SHADOW));
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.spellData.set(LIVING_SHADOW, nbt.getInt("LivingShadowId"));
    }

    @Override
    public @UnknownNullability CompoundTag initializeFromClient(SpellContext context, CompoundTag compoundTag) {
        CompoundTag nbt = super.initializeFromClient(context, compoundTag);
        if (KeyBinds.getLeftMapping().isDown())
            compoundTag.putBoolean("Left", true);

        if (KeyBinds.getRightMapping().isDown())
            compoundTag.putBoolean("Right", true);

        if (KeyBinds.getDownMapping().isDown())
            compoundTag.putBoolean("Down", true);

        return nbt;
    }

    @Override
    public void loadFromClient(CompoundTag nbt) {
        super.loadFromClient(nbt);
        if (nbt.contains("Left", 99)) {
            this.left = nbt.getBoolean("Left");
        } else if (nbt.contains("Right", 99)) {
            this.right = nbt.getBoolean("Right");
        } else if (nbt.contains("Down", 99)) {
            this.down = nbt.getBoolean("Down");
        }
    }
}
