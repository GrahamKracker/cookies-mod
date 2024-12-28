package codes.cookies.mod.features.crafthelper.ui.components;

import codes.cookies.mod.features.crafthelper.ui.CraftHelperComponent;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import codes.cookies.mod.repository.recipes.calculations.RecipeResult;
import codes.cookies.mod.utils.cookies.Constants;

public class RecipeComponent extends MultiLineComponent {
	private final RecipeResult<?> recipe;
	private final int depth;

	public RecipeComponent(RecipeResult<?> recipe, int depth, CraftHelperComponent... children) {
		super(children);
		this.recipe = recipe;
		this.depth = depth;
		StringBuilder builder = new StringBuilder();
		if (depth == 0) {
			builder.append(Constants.Emojis.FLAG_EMPTY + " ");
		} else {
			builder.append("|");
		}
		builder.append(" ".repeat(depth));
		if (recipe instanceof RecipeCalculationResult subResult) {
			builder.append(subResult.getIngredient().getNameSafe());

			/*subResult.getRequired().forEach(recipeResult -> {
				getRecipe(recipeResult, depth + 1);
			});*/
		} else if (recipe instanceof Ingredient ingredient) {
			this.add(new TextComponent(" ".repeat(depth + 1) + Constants.Emojis.FLAG_EMPTY + " " + ingredient.getNameSafe()));
		}
		this.add(new TextComponent(builder.toString()));
	}

	private void addRecipe(RecipeCalculationResult recipe, int depth) {
		this.add(new RecipeComponent(recipe, depth));
	}

	private void addIngredient(Ingredient ingredient, int depth) {
		this.add(new TextComponent(" ".repeat(depth + 1) + Constants.Emojis.FLAG_EMPTY + " " + ingredient.getNameSafe()));
	}

}
