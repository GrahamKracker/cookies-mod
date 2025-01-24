package codes.cookies.mod.features.crafthelper;

import codes.cookies.mod.features.crafthelper.ui.RecipeListLine;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	private final List<RecipeListLine> recipeLines = new ArrayList<>();


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
		addSubRecipe(recipeCalculationResult, null, 0, recipeCalculationResult);
	}

	private void addSubRecipe(RecipeCalculationResult subRecipe, RecipeListLine parent, int depth, RecipeCalculationResult topRecipe) {
		var recipeLine = new RecipeListLine(parent, depth, subRecipe.getIngredient(), amount);
		recipeLines.add(recipeLine);
		subRecipe.getRequired().forEach(recipeResult -> {
			if (recipeResult instanceof RecipeCalculationResult subRecipe2) {
				addSubRecipe(subRecipe2, recipeLine, depth + 1, topRecipe);
			} else if (recipeResult instanceof Ingredient ingredient) {
				recipeLines.add(new RecipeListLine(recipeLine, depth + 1, ingredient, amount));
			}
		});
	}
}
