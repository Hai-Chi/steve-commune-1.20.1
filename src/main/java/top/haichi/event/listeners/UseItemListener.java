package top.haichi.event.listeners;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UseItemListener implements UseItemCallback {


    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        //一些操作
        mendWithPlayerExperience(player, world, stackInHand);
        removeBadOmenWithSword(player, world, stackInHand);

        return TypedActionResult.pass(stackInHand);
    }

    /**
     * 用剑消除不祥之兆效果
     */
    private void removeBadOmenWithSword(PlayerEntity player, World world, ItemStack stackInHand) {
        if (stackInHand.getItem() == Items.DIAMOND_SWORD || stackInHand.getItem() == Items.NETHERITE_SWORD ||
                stackInHand.getItem() == Items.STONE_SWORD || stackInHand.getItem() == Items.IRON_SWORD ||
                stackInHand.getItem() == Items.WOODEN_SWORD || stackInHand.getItem() == Items.GOLDEN_SWORD) {
            if (player.getStatusEffect(StatusEffects.BAD_OMEN) != null) {
                player.removeStatusEffect(StatusEffects.BAD_OMEN);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 3f, 5.0f);
            }
        }
    }

    /**
     * 用玩家经验修复经验修补的物品
     */
    private void mendWithPlayerExperience(PlayerEntity player, World world, ItemStack stackInHand) {
        int playerXP = getPlayerXP(player);
        int neededXP = stackInHand.getDamage() / 2;
        if (EnchantmentHelper.getLevel(Enchantments.MENDING, stackInHand) > 0
                && stackInHand.isDamaged()
                && player.isSneaking()
                && playerXP > 0) {
            if (playerXP >= neededXP) {
                addPlayerXP(player, -neededXP);
                stackInHand.setDamage(0);
            } else {
                addPlayerXP(player, -playerXP);
                stackInHand.setDamage(stackInHand.getDamage() - playerXP * 2);
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1f, 1f);
        }

    }


    //以下代码来自BetterThanMending by legobmw99
    private static int getLevelForExperience(int experience) {
        int i = 0;
        while (getExperienceForLevel(i) <= experience) {
            i++;
        }
        return i - 1;
    }

    private static int getExperienceForLevel(int level) {
        if (level == 0) {
            return 0;
        }
        if (level > 0 && level < 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level > 15 && level < 32) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        }
    }

    private static int getPlayerXP(PlayerEntity player) {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getNextLevelExperience()));
    }

    private static void addPlayerXP(PlayerEntity player, int amount) {

        int experience = getPlayerXP(player) + amount;
        if (experience < 0) {
            return;
        }
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (experience - expForLevel) / (float) player.getNextLevelExperience();
    }
}
