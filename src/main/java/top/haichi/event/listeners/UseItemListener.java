package top.haichi.event.listeners;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UseItemListener implements UseItemCallback {



    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);

        removeBadOmenWithSword(player, world, stackInHand);
        return TypedActionResult.pass(stackInHand);
    }

    /**
     * 用剑消除不祥之兆效果
     */
    private void removeBadOmenWithSword(PlayerEntity player, World world, ItemStack stackInHand) {
        if (stackInHand.getItem() == Items.DIAMOND_SWORD || stackInHand.getItem() == Items.NETHERITE_SWORD ||
                stackInHand.getItem() == Items.STONE_SWORD ||stackInHand.getItem() == Items.IRON_SWORD ||
                stackInHand.getItem() == Items.WOODEN_SWORD || stackInHand.getItem() == Items.GOLDEN_SWORD){
            if(player.getStatusEffect(StatusEffects.BAD_OMEN) != null ){
                player.removeStatusEffect(StatusEffects.BAD_OMEN);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS,3f,5.0f);
            }
        }
    }

}
