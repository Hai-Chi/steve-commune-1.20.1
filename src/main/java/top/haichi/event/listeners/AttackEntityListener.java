package top.haichi.event.listeners;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AttackEntityListener implements AttackEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        attackChickenPunish(player, entity);
        return ActionResult.PASS;
    }

    /**
     * 你干嘛哈哈哎哟（不要打坤）
     */
    private void attackChickenPunish(PlayerEntity player, Entity entity) {
        if(entity.getType()== EntityType.CHICKEN){
            player.sendMessage(Text.literal("你干嘛哈哈哎哟~"));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,20));
        }
    }
}
