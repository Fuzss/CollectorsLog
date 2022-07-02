package at.petrak.collectorslog.forge.xplat;

import at.petrak.collectorslog.xplat.IXplatAbstractions;
import at.petrak.collectorslog.xplat.Platform;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;

public class ForgeXplatImpl implements IXplatAbstractions {
    @Override
    public Platform platform() {
        return Platform.FORGE;
    }
}
