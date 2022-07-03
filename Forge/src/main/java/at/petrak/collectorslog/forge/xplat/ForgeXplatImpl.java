package at.petrak.collectorslog.forge.xplat;

import at.petrak.collectorslog.CollectorsLogConfig;
import at.petrak.collectorslog.forge.ForgeInitializer;
import at.petrak.collectorslog.xplat.IXplatAbstractions;
import at.petrak.collectorslog.xplat.Platform;

public class ForgeXplatImpl implements IXplatAbstractions {
    @Override
    public Platform platform() {
        return Platform.FORGE;
    }

    @Override
    public CollectorsLogConfig getConfig() {
        return ForgeInitializer.CONFIG;
    }
}
