package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.slf4j.Logger;

import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreeperMod.MODID)
public class CreeperMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "emotionalcreepers";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public CreeperMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }


//här börjar egna tillägg
    @SubscribeEvent
    public void onCreeperTick(LivingEvent.LivingTickEvent event)
    {
        //hämtar creeper instans
        if(event.getEntity() instanceof Creeper creeper)
        {
            boolean isTamed = creeper.getPersistentData().getBoolean("IsTamed");

            if (creeper.level().isClientSide) return;

            if(isTamed){
                creeper.setSwellDir(-1);
                creeper.setTarget(null);
                creeper.setSilent(true);

                if (!creeper.getPersistentData().hasUUID("OwnerUUID")) return;

                UUID ownerId = creeper.getPersistentData().getUUID("OwnerUUID");
                Player owner = creeper.level().getPlayerByUUID(ownerId);

                if (owner == null) {
                    System.out.println("DEBUG: Letar efter ägare med UUID: " + ownerId + " men hittar ingen!");
                }

                if (creeper.getPersistentData().getBoolean("IsStill")) {
                    creeper.getNavigation().stop();
                    return;
                }

                if(owner != null && creeper.distanceTo(owner) > 4.0){
                    creeper.getNavigation().moveTo(owner, 1.2);
                    return;
                }

                return;
            }

            Player player = creeper.level().getNearestPlayer(creeper, 10.0);

            if(player != null)
            {

                //hämtar main hand items
                ItemStack stackInHand = player.getMainHandItem();

                //kontrollerar om det är en blomma
                if(stackInHand.is(ItemTags.FLOWERS)){
                    System.out.println("Creeper ser blomman!");
                    //tar bort att creeper exploderar
                    creeper.setSwellDir(-1);
                    creeper.setTarget(null);

                    //creeper följer spelare med blicken
                    creeper.getLookControl().setLookAt(player, 30.0F, 30.0F);


                    //creeper följer efter spelare
                    if(creeper.distanceTo(player) < 3.0){
                        creeper.getNavigation().stop();
                    } else {
                        creeper.getNavigation().moveTo(player, 1.0);
                    }

                }
                //kontrollerar om det är ett svärd
                else if(stackInHand.is(ItemTags.SWORDS)){
                    creeper.setTarget(null);
                    //hämtar riktningen från spelare till creeper
                    Vec3 escapeDirection = creeper.position().subtract(player.position()).normalize();

                    //hämtar de specifika x- och yvärden som creeper ska fly mot (10 block bort)
                    double escapeX = creeper.getX() + escapeDirection.x * 10;
                    double escapeZ = creeper.getZ() + escapeDirection.z * 10;

                    //säger åt creeper att springa till nya position i hastighet 1.5
                    creeper.getNavigation().moveTo(escapeX, creeper.getY(), escapeZ, 1.5);

                    //lägger till tårar och ljudeffekt när creeper ser svärd
                    if(!creeper.level().isClientSide && creeper.level() instanceof ServerLevel serverLevel){

                        if(creeper.level().getGameTime() % 10 == 0){
                            serverLevel.sendParticles(ParticleTypes.FALLING_WATER,
                                    creeper.getX(), creeper.getY() + 1.5, creeper.getZ(),
                                    15, 0.3, 0.3, 0.3, 0.05);

                            creeper.level().playSound(null, creeper.blockPosition(),
                                    SoundEvents.FOX_SNIFF, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        }
                    }

                }
                //i annat fall kör vanliga minecraft koden
                else {
                }
            }
        }
    }

    @SubscribeEvent
    public void onCreeperInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (event.getLevel().isClientSide) return;

        //kontrollerar om interaktion är med en creeper
        if(event.getTarget() instanceof Creeper creeper)
        {

            boolean isTamed = creeper.getPersistentData().getBoolean("IsTamed");

            if (event.getHand() != InteractionHand.MAIN_HAND) return;

            Player player = event.getEntity();
            ItemStack itemStack = event.getItemStack();

            if(itemStack.is(ItemTags.FLOWERS) && !isTamed)
            {
                System.out.println("DEBUG: Tämjd av UUID: " + player.getUUID());
                //spara data om user id och att creepern är tamed
                creeper.getPersistentData().putUUID("OwnerUUID", player.getUUID());
                creeper.getPersistentData().putBoolean("IsTamed", true);

                //despawnar inte creeper
                creeper.setPersistenceRequired();

                creeper.setCustomName(Component.literal(player.getName() + "Creeper"));
                creeper.setCustomNameVisible(true);

                //gör hjärtpartiklar
                if(!creeper.level().isClientSide && creeper.level() instanceof ServerLevel serverLevel){

                    serverLevel.sendParticles(ParticleTypes.HEART,
                            creeper.getX(), creeper.getY() + 1.5, creeper.getZ(),
                            15, 0.3, 0.3, 0.3, 0.05);

                    //spela tämjningsljud
                    creeper.level().playSound(null, creeper.blockPosition(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }

                //kontrollerar om player inte är i creative, tar då bort ett objekt ur stack
                if(!player.getAbilities().instabuild){
                    itemStack.shrink(1);
                }

                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);

            }
            //kunna få creepern att stanna/fortsätta följa med
            else if(isTamed)
            {
                if (!itemStack.isEmpty()) {
                    event.setCanceled(true);
                    return;
                }
                //hämtar ägare
                UUID ownerId = creeper.getPersistentData().getUUID("OwnerUUID");

                if (player.getUUID().equals(ownerId) || player.getName().getString().equals("Dev")) {
                    //togglar boolean om den stannar eller följer med
                    boolean isStill = creeper.getPersistentData().getBoolean("IsStill");
                    creeper.getPersistentData().putBoolean("IsStill", !isStill);

                    //kör partiklar och ljudeffekter
                    if (creeper.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                                creeper.getX(), creeper.getY() + 1.5, creeper.getZ(),
                                5, 0.5, 0.5, 0.5, 0.02);

                        creeper.level().playSound(null, creeper.blockPosition(),
                                SoundEvents.AXOLOTL_IDLE_AIR, SoundSource.NEUTRAL, 1.0F, 1.2F);
                    }

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                } else {
                    //minecraft kör sin egna kod
                }

            }
        }
    }
}
