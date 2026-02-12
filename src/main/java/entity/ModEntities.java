package entity;

import com.example.examplemod.CreeperMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreeperMod.MODID);

    public static final RegistryObject<EntityType<BabyCreeperEntity>> BABY_CREEPER =
            ENTITIES.register("baby_creeper", () -> EntityType.Builder.of(BabyCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.3f, 0.85f) // Sätter hitboxen (hälften av en vanlig creeper)
                    .build(new ResourceLocation(CreeperMod.MODID, "baby_creeper").toString()));
}