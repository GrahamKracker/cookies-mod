package codes.cookies.mod.data.profile.sub;

import java.util.Optional;
import java.util.function.Predicate;

import codes.cookies.mod.features.crafthelper.CraftHelperItem;
import codes.cookies.mod.utils.json.CodecJsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//todo
public class CraftHelperData { /*implements CodecJsonSerializable<CraftHelperInstance> {

	public CraftHelperData() {
		CraftHelperManager.setActive(CraftHelperInstance.EMPTY);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CraftHelperData.class);

	@Override
	public Codec<CraftHelperInstance> getCodec() {
		return CraftHelperInstance.CODEC;
	}

	@Override
	public void load(CraftHelperInstance value) {
		codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager.setActive(value);
		codes.cookies.mod.features.crafthelper.CraftHelperManager.pushNewCraftHelperItem(new CraftHelperItem(value.getRepositoryItem()));
	}

	@Override
	public CraftHelperInstance getValue() {
		return Optional.of(CraftHelperManager.getActive())
				.filter(Predicate.not(CraftHelperInstance.EMPTY::equals))
				.orElse(null);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}*/
}
