package at.petrak.collectorslog.fabric.xplat;

import at.petrak.collectorslog.xplat.IXplatAbstractions;
import at.petrak.collectorslog.xplat.Platform;
import net.minecraft.world.item.Item;

import java.util.Iterator;

public class FabricXplatImpl implements IXplatAbstractions {
    @Override
    public Platform platform() {
        return Platform.FABRIC;
    }
}
