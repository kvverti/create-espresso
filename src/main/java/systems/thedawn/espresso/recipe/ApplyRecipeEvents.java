package systems.thedawn.espresso.recipe;

import java.util.Optional;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import systems.thedawn.espresso.Espresso;
import systems.thedawn.espresso.EspressoRecipeTypes;

import net.minecraft.world.item.crafting.RecipeHolder;

@EventBusSubscriber(modid = Espresso.MODID)
public class ApplyRecipeEvents {
    @SubscribeEvent
    public static void gatherRecipesForDeployer(DeployerRecipeSearchEvent ev) {
        ev.addRecipe(() -> {
            var level = ev.getBlockEntity().getLevel();
            if(level != null) {
                var drinkHolder = ev.getInventory().getItem(0);
                var appliedStack = ev.getInventory().getItem(1);
                var input = new DrinkModificationRecipeInput(drinkHolder, appliedStack, null);
                return ev.getBlockEntity().getLevel().getRecipeManager()
                    .getRecipeFor(EspressoRecipeTypes.DRINK_MODIFY.value(), input, level)
                    .map(found -> new RecipeHolder<>(found.id(),
                        new DrinkModificationRecipe.DynamicResult(found.value().assemble(input, level.registryAccess()))));
            }
            return Optional.empty();
        }, 50);
    }
}
