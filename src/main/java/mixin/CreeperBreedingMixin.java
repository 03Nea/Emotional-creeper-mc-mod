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
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Creeper.class)
public abstract class CreeperBreedingMixin {

    @Unique
    private int loveTimer = 0;

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onCreeperInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        Creeper currentCreeper = (Creeper) (Object) this;

        if (itemStack.is(Items.GUNPOWDER)) {
            Level level = player.level();

            if (!level.isClientSide) {

                //om creeper inte redan är i love mode
                if (this.loveTimer <= 0) {
                    this.loveTimer = 600; //600 ticks/30sekunder att para sig

                    consumeGunpowder(player, itemStack); //minska item stack för player

                    //hjärtpartiklar när den är i kärleksläge
                    if (currentCreeper.level().isClientSide && currentCreeper.level() instanceof ServerLevel serverLevel) {

                        //visar partiklar
                        serverLevel.sendParticles(ParticleTypes.HEART, currentCreeper.getX(), currentCreeper.getY() + 1.5, currentCreeper.getZ(),
                                5, 0.3, 0.3, 0.3, 0.1);

                        //spelar ljud
                        currentCreeper.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 2.0F);
                        cir.setReturnValue(InteractionResult.SUCCESS);

                    }
                    } else {
                        cir.setReturnValue(InteractionResult.PASS);
                    }
                } else {
                    cir.setReturnValue(InteractionResult.CONSUME);
                }
            }
        }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Creeper currentCreeper = (Creeper) (Object) this;
        Level level = currentCreeper.level();

        if (this.loveTimer > 0) {
            if (!level.isClientSide) {
                this.loveTimer--;

                //visar hjärtan medan lovetimer har tid kvar var 0.5 sekund
                if (level.getGameTime() % 10 == 0) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.HEART,
                            currentCreeper.getX(), currentCreeper.getY() + 1.5, currentCreeper.getZ(),
                            1, 0.3, 0.3, 0.3, 0.1);
                }

                // leta efter partner varje tick
                List<Creeper> nearbyCreepers = level.getEntitiesOfClass(Creeper.class, currentCreeper.getBoundingBox().inflate(3.0D));

                for (Creeper partner : nearbyCreepers) {
                    if (partner != currentCreeper) {
                        CreeperBreedingMixin partnerMixin = (CreeperBreedingMixin) (Object) partner;

                        // Om partnern också är kär breedar de
                        if (partnerMixin.loveTimer > 0) {
                            breed((ServerLevel) level, currentCreeper, partner);

                            // Nollställ lovetimer för båda parter
                            this.loveTimer = 0;
                            partnerMixin.loveTimer = 0;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique //konsumerar 1 gunpowder => minskar med ett för player om ej creative
    private void consumeGunpowder(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    @Unique
    private void breed(ServerLevel serverLevel, Creeper currentCreeper, Creeper partner){

        double midX = (currentCreeper.getX() + partner.getX()) / 2;
        double midY = (currentCreeper.getY() + partner.getY()) / 2 + 0.5;
        double midZ = (currentCreeper.getZ() + partner.getZ()) / 2;

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, midX, midY, midZ, 1, 0, 0, 0, 0);
        serverLevel.playSound(null, midX, midY, midZ, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 1.8F);


        BabyCreeperEntity baby = new BabyCreeperEntity(ModEntities.BABY_CREEPER.get(), serverLevel);
        baby.moveTo(midX, midY, midZ, 0, 0);
        serverLevel.addFreshEntity(baby);
    }
}