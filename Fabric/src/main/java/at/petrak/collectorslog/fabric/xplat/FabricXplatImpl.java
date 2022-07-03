package at.petrak.collectorslog.fabric.xplat;

import at.petrak.collectorslog.CollectorsLogConfig;
import at.petrak.collectorslog.fabric.FabricConfig;
import at.petrak.collectorslog.xplat.IXplatAbstractions;
import at.petrak.collectorslog.xplat.Platform;
import com.google.common.base.Suppliers;

import java.util.function.Supplier;

public class FabricXplatImpl implements IXplatAbstractions {
    @Override
    public Platform platform() {
        return Platform.FABRIC;
    }

    private static final Supplier<FabricConfig> CONFIG = Suppliers.memoize(FabricConfig::new);

    @Override
    public CollectorsLogConfig getConfig() {
        return CONFIG.get();
    }
}
