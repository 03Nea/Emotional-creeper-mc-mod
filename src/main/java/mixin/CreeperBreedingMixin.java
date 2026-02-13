package mixin;

import entity.BabyCreeperEntity;
import entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Creeper.class)
public abstract class CreeperBreedingMixin {

    @Inject(method = {"mobInteract", "m_6071_"}, at = @At("HEAD"), cancellable = true, remap = true)
    private void onCreeperInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
		System.out.println("Clicked creeper with: " + itemStack);
        Creeper currentCreeper = (Creeper) (Object) this;

        if (itemStack.is(Items.GUNPOWDER)) {
            Level level = player.level();

            if (!level.isClientSide) {
                List<Creeper> nearbyCreepers = level.getEntitiesOfClass(Creeper.class, currentCreeper.getBoundingBox().inflate(3.0D));

                Creeper partner = null;
                for (Creeper potentialPartner : nearbyCreepers) {
                    if (potentialPartner != currentCreeper && potentialPartner.isAlive()) {
                        partner = potentialPartner;
                        break;
                    }
                }

                if (partner != null) {
                    ServerLevel serverLevel = (ServerLevel) level;

                    double midX = (currentCreeper.getX() + partner.getX()) / 2;
                    double midY = (currentCreeper.getY() + partner.getY()) / 2 + 0.5;
                    double midZ = (currentCreeper.getZ() + partner.getZ()) / 2;

                    serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, midX, midY, midZ, 1, 0, 0, 0, 0);
                    serverLevel.playSound(null, midX, midY, midZ, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 1.8F);


                    BabyCreeperEntity baby = new BabyCreeperEntity(ModEntities.BABY_CREEPER.get(), level);
                    baby.moveTo(midX, midY, midZ, 0, 0);
                    serverLevel.addFreshEntity(baby);

                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    currentCreeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.5F);
                }
            } else {
                cir.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }
}