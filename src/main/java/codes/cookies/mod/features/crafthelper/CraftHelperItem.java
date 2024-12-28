package codes.cookies.mod.features.crafthelper;

import codes.cookies.mod.config.data.ListData;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;
import codes.cookies.mod.features.crafthelper.ui.CraftHelperPanelLine;
import codes.cookies.mod.features.crafthelper.ui.components.RecipeComponent;
import codes.cookies.mod.features.crafthelper.ui.components.TextComponent;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import codes.cookies.mod.repository.recipes.calculations.RecipePrinter;
import codes.cookies.mod.repository.recipes.calculations.RecipeResult;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import codes.cookies.mod.utils.minecraft.TextBuilder;
import lombok.Getter;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class CraftHelperItem {
	/*public static final Codec<CraftHelperInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					RepositoryItem.ID_CODEC.fieldOf("id").forGetter(CraftHelperInstance::getRepositoryItem),
					Codecs.NON_NEGATIVE_INT.fieldOf("amount").forGetter(CraftHelperInstance::getAmount),
					Codec.STRING.listOf().fieldOf("collapsed").forGetter(CraftHelperInstance::getCollapsed))
			.apply(instance, CraftHelperInstance::new));*/

	final RepositoryItem repositoryItem;
	private final int amount;
	private boolean recalculated = false;
	private final List<CraftHelperPanelLine> lines = new ArrayList<>();

	public CraftHelperItem(RepositoryItem repositoryItem, int amount) {
		this.repositoryItem = repositoryItem;
		this.amount = amount;
		recalculate();
	}

	public void recalculate() {
		if (this.recalculated) {
			return;
		}
		this.recalculated = true;
		final var calculate = RecipeCalculator.calculate(repositoryItem);
		calculate.ifSuccess(this::finishRecalculation).ifError(errorMessage -> {
			CookiesUtils.sendFailedMessage(Optional.ofNullable(errorMessage)
					.orElse("An error occurred while evaluating the recipe tree."));
		});
	}

	private void finishRecalculation(RecipeCalculationResult recipeCalculationResult) {
		lines.clear();
		lines.add(new CraftHelperPanelLine(new RecipeComponent(recipeCalculationResult, 0)));
	}

	private void getRecipe(RecipeResult<?> calculationResult, int depth) {

		if (calculationResult instanceof RecipeCalculationResult subResult) {
			//lines.add(new CraftHelperPanelLine(new RecipeComponent(subResult, depth)));
			subResult.getRequired().forEach(recipeResult -> {
				getRecipe(recipeResult, depth + 1);
			});
		} else if (calculationResult instanceof Ingredient ingredient) {
			//lines.add(new CraftHelperPanelLine(new IngredientComponent(ingredient, depth)));
		}
	}
}
