package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.world.entity.projectile.BoundArrow;
import com.ombremoon.spellbound.common.world.spell.summon.BoundBowSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BoundBowItem extends ProjectileWeaponItem {
    public BoundBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            var handler = SpellUtil.getSpellHandler(player);
            BoundBowSpell spell = handler.getSpell(SBSpells.BOUND_BOW.get());
            if (spell != null) {
                SpellContext context = spell.getContext();
                int manaCost = spell.getBowManaCost(context);
                int i = this.getUseDuration(stack, livingEntity) - timeCharged;
                i = EventHooks.onArrowLoose(stack, level, player, i, handler.getMana() >= manaCost);
                if (i < 0) return;
                float f = getPowerForTime(context, i);
                if (!((double) f < 0.1)) {
                    if (level instanceof ServerLevel serverlevel) {
                        int count = context.hasSkill(SBSkills.SPECTRAL_VOLLEY) ? 3 : 1;
                        boolean isCrit = f == 1.0F || context.hasSkill(SBSkills.ARCHERY_PROWESS) && spell.getShotCount() % 3 == 0;
                        this.shoot(serverlevel, player, stack, spell, context, count, f * 3.0F, 1.0F, isCrit, null);
                        spell.incrementShotsFired();
                        spell.consumeMana(player, manaCost);
                    }

                    level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ARROW_SHOOT,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
                    );
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    protected void shoot(
            ServerLevel level,
            LivingEntity shooter,
            ItemStack weapon,
            BoundBowSpell spell,
            SpellContext context,
            int projectileCount,
            float velocity,
            float inaccuracy,
            boolean isCrit,
            @Nullable LivingEntity target
    ) {
        float f = context.hasSkill(SBSkills.SPECTRAL_VOLLEY) ? 30 : 0;
        float f1 = projectileCount == 1 ? 0.0F : 2.0F * f / (float)(projectileCount - 1);
        float f2 = (float)((projectileCount - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for (int i = 0; i < projectileCount; i++) {
            float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
            f3 = -f3;
            BoundArrow projectile = this.createProjectile(level, shooter, weapon, isCrit);
            if (context.hasSkill(SBSkills.PIERCING_SHOT)) {
                projectile.setPierceLevel((byte) context.getSpellLevel());
            }

            Imbuement imbuement = spell.createInfusedShot();
            BoundArrow.ArrowVariant variant = spell.getInfusedShot();
            projectile.setVariant(variant);
            projectile.setData(SBData.ENTITY_IMBUEMENT, imbuement);

            if (context.hasSkill(SBSkills.HEATSEEKER) && spell.getTargetEntity(shooter, spell.getCastRange()) instanceof LivingEntity living) {
                projectile.setHoming(true);
                projectile.setHomingTarget(living);
            }

            this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4, target);
            level.addFreshEntity(projectile);
        }
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
    }

    protected BoundArrow createProjectile(Level level, LivingEntity shooter, ItemStack weapon, boolean isCrit) {
        BoundArrow arrow = new BoundArrow(level, shooter, weapon);
        if (isCrit) {
            arrow.setCritArrow(true);
        }

        return arrow;
    }

    public static float getPowerForTime(SpellContext context, int charge) {
        float thresh = context.hasSkill(SBSkills.QUICKDRAW) ? 10.0F : 20.0F;
        float f = (float)charge / thresh;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        var handler = SpellUtil.getSpellHandler(player);
        BoundBowSpell spell = handler.getSpell(SBSpells.BOUND_BOW.get());
        if (spell != null) {
            boolean hasAmmo = handler.getMana() >= spell.getBowManaCost(spell.getContext());
            InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, level, player, usedHand, hasAmmo);
            if (ret != null) return ret;

            if (!player.hasInfiniteMaterials() && !hasAmmo) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                player.startUsingItem(usedHand);
                return InteractionResultHolder.consume(itemstack);
            }
        }

        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return null;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}
