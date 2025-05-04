package systems.thedawn.espresso;

import java.util.List;
import java.util.Set;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import systems.thedawn.espresso.datagen.*;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Espresso.MODID)
public class Espresso {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_espresso";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_espresso" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // Creates a creative tab with the id "create_espresso:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ESPRESSO_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.create_espresso")) //The language key for the title of your CreativeModeTab
        .withTabsBefore(CreativeModeTabs.COMBAT)
        .icon(() -> EspressoItems.COFFEE_BEANS.value().getDefaultInstance())
        .displayItems((parameters, output) -> {
            // blocks
            output.accept(EspressoItems.COFFEE_BRICKS);
            output.accept(EspressoItems.COFFEE_BRICK_SLAB);
            output.accept(EspressoItems.COFFEE_BRICK_STAIRS);
            output.accept(EspressoItems.COFFEE_MUG);
            // items
            output.accept(EspressoItems.COFFEE_CHERRY);
            output.accept(EspressoItems.COFFEE_PASTE);
            output.accept(EspressoItems.COFFEE_PIT);
            output.accept(EspressoItems.COFFEE_BEANS);
            output.accept(EspressoItems.COFFEE_GROUNDS);
            output.accept(EspressoItems.SPENT_COFFEE_GROUNDS);
            output.accept(EspressoItems.COFFEE_FILTER);
            output.accept(EspressoItems.USED_COFFEE_FILTER);
            output.accept(EspressoItems.COFFEE_BRICK);
            output.accept(EspressoItems.HOT_WATER_BUCKET);
            // drink bottles
            var registries = parameters.holders();
            output.accept(drinkBottle(BuiltinEspressoDrinks.DIRTY_COLD_BREW, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.COLD_BREW, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.POUR_OVER, registries));
            output.accept(drinkBottle(BuiltinEspressoDrinks.ESPRESSO, registries));
        }).build());

    private static ItemStack drinkBottle(ResourceKey<Drink> key, HolderLookup.Provider registries) {
        var component = new DrinkComponent(registries.holderOrThrow(key));
        var stack = new ItemStack(EspressoItems.DRINK_BOTTLE.value());
        stack.set(EspressoDataComponentTypes.DRINK, component);
        return stack;
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Espresso(IEventBus modEventBus, ModContainer modContainer) {
        EspressoBlocks.BLOCKS.register(modEventBus);
        EspressoItems.ITEMS.register(modEventBus);
        EspressoFluids.FLUID_TYPES.register(modEventBus);
        EspressoFluids.FLUIDS.register(modEventBus);
        EspressoDataComponentTypes.DATA_COMPONENT_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        private static final ResourceLocation STILL_WATER = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
        private static final ResourceLocation FLOWING_WATER = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
        private static final ResourceLocation WATER_OVERLAY = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay");

        @SubscribeEvent
        public static void onRegisterClientExtensions(RegisterClientExtensionsEvent ev) {
            ItemBlockRenderTypes.setRenderLayer(EspressoFluids.SOURCE_HOT_WATER.value(), RenderType.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(EspressoFluids.FLOWING_HOT_WATER.value(), RenderType.TRANSLUCENT);
            ev.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return STILL_WATER;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING_WATER;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return WATER_OVERLAY;
                }
            }, EspressoFluids.HOT_WATER);
            ev.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return 0xff6e400f;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING_WATER;
                }

                @Override
                public ResourceLocation getStillTexture() {
                    return STILL_WATER;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return WATER_OVERLAY;
                }
            }, EspressoFluids.ESPRESSO);
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class DataEvents {
        @SubscribeEvent
        public static void onDataGen(GatherDataEvent ev) {
            ev.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(EspressoRegistries.DRINKS, BuiltinEspressoDrinks::bootstrapDrinks)
            );
            var generator = ev.getGenerator();
            var output = generator.getPackOutput();
            var existingFileHelper = ev.getExistingFileHelper();
            var lookupProvider = ev.getLookupProvider();

            ev.createBlockAndItemTags(EspressoBlockTagsProvider::new, EspressoItemTagsProvider::new);

            generator.addProvider(ev.includeClient(), new EspressoBlockStateProvider(output, existingFileHelper));
            generator.addProvider(ev.includeClient(), new EspressoItemModelProvider(output, existingFileHelper));
            generator.addProvider(ev.includeClient(), new EspressoTranslationProvider(output));
            generator.addProvider(ev.includeClient(), new EspressoRecipeProvider(output, lookupProvider));
            generator.addProvider(ev.includeClient(), new LootTableProvider(output, Set.of(),
                List.of(
                    new LootTableProvider.SubProviderEntry(EspressoBlockLootProvider::new, LootContextParamSets.BLOCK)
                ),
                lookupProvider
            ));
        }
    }
}
