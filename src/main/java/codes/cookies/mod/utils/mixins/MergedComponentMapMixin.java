package codes.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import codes.cookies.mod.utils.accessors.CustomComponentMapAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

/**
 * Allows for addition of custom components without interfering with the real component map.
 */
@Mixin(MergedComponentMap.class)
public class MergedComponentMapMixin implements CustomComponentMapAccessor {
	@Unique
	private MergedComponentMap cookies$componentMap;

	/**
	 * Redirects all custom component types to the {@linkplain MergedComponentMapMixin#cookies$componentMap}.
	 *
	 * @param dataComponentType The component type.
	 * @param object            The value of the component.
	 * @param cir               The callback.
	 * @param <T>               The type of the component.
	 */
	@Inject(method = "set", at = @At("HEAD"), cancellable = true)
	public <T> void set(
			ComponentType<? super T> dataComponentType, @Nullable T object, CallbackInfoReturnable<T> cir) {
		if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
			if (dataComponentType == CookiesDataComponentTypes.OVERRIDE_ITEM && object != null) {
				ItemStack itemStack = (ItemStack) object;
				if (this.cookies$componentMap.contains(CookiesDataComponentTypes.ORIGINAL_ITEM)) {
					final ItemStack remove = this.cookies$componentMap.remove(CookiesDataComponentTypes.ORIGINAL_ITEM);
					if (remove != null) {
						remove.set(CookiesDataComponentTypes.OVERRIDE_ITEM, itemStack);
						return;
					}
				}
				itemStack.set(
						CookiesDataComponentTypes.ORIGINAL_ITEM,
						this.cookies$componentMap.get(CookiesDataComponentTypes.SELF));
			}
			this.cookies$componentMap.set(dataComponentType, object);
			cir.setReturnValue(null);
		}
	}

	/**
	 * Redirects all custom component types to the {@linkplain MergedComponentMapMixin#cookies$componentMap}.
	 *
	 * @param dataComponentType The component type.
	 * @param cir               The callback.
	 * @param <T>               The type of the component.
	 */
	@Inject(method = "remove", at = @At("HEAD"), cancellable = true)
	public <T> void remove(
			ComponentType<? extends T> dataComponentType, CallbackInfoReturnable<T> cir) {
		if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
			if (dataComponentType == CookiesDataComponentTypes.OVERRIDE_ITEM) {
				final ItemStack itemStack = cookies$componentMap.get(CookiesDataComponentTypes.ORIGINAL_ITEM);
				if (itemStack != null) {
					itemStack.remove(CookiesDataComponentTypes.OVERRIDE_ITEM);
				}
			}
			cir.setReturnValue(this.cookies$componentMap.remove(dataComponentType));
		}
	}

	/**
	 * Creates a copy of the custom map and attaches it to the normal copy.
	 *
	 * @param original The original copy.
	 * @return The copy with the extra components attached.
	 */
	@ModifyReturnValue(method = "copy", at = @At("RETURN"))
	public MergedComponentMap copy(MergedComponentMap original) {
		if (this.cookies$componentMap != null) {
			final MergedComponentMap copy = new MergedComponentMap(ComponentMap.EMPTY);
			copy.setAll(this.cookies$componentMap);
			((CustomComponentMapAccessor) (Object) original).cookies$setMergedComponentMap(copy);
		}

		return original;
	}

	/**
	 * Redirects all custom component types to the {@linkplain MergedComponentMapMixin#cookies$componentMap}.
	 *
	 * @param dataComponentType The component type.
	 * @param cir               The callback.
	 * @param <T>               The type of the component.
	 */
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public <T> void get(
			ComponentType<? extends T> dataComponentType, CallbackInfoReturnable<T> cir) {
		if (this.cookies$componentMap != null && CookiesDataComponentTypes.isCustomType(dataComponentType)) {
			cir.setReturnValue(this.cookies$componentMap.get(dataComponentType));
		}
	}


	@Override
	@Unique
	public void cookies$setMergedComponentMap(MergedComponentMap componentMap) {
		cookies$componentMap = componentMap;
	}

	@Override
	@Unique
	public MergedComponentMap cookies$getMergedComponentMap() {
		return cookies$componentMap;
	}
}
