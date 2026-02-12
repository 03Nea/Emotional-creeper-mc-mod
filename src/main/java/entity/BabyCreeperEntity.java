package entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class BabyCreeperEntity extends Creeper {

    private float currentSize =0.5f;
    private final int MAX_AGE = 240;
    private int age = 0;

    public BabyCreeperEntity(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    public boolean isBaby() {
        return true;
    }
    @Override
    public float getScale(){
        return currentSize; //halva storleken
    }

    @Override
    public void tick(){
        super.tick();

        if(!this.level().isClientSide){
            age++;
            if(age >= MAX_AGE && currentSize < 1.0f){
                growUp();
            }
        }
    }

    public void growUp() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            Creeper adultCreeper = EntityType.CREEPER.create(serverLevel);

            if (adultCreeper != null) {
                adultCreeper.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

                if (this.hasCustomName()) {
                    adultCreeper.setCustomName(this.getCustomName());
                    adultCreeper.setCustomNameVisible(this.isCustomNameVisible());
                }
                if (this.isPersistenceRequired()) {
                    adultCreeper.setPersistenceRequired();
                }

                serverLevel.addFreshEntity(adultCreeper);

                this.discard();
            }
        }
    }
}