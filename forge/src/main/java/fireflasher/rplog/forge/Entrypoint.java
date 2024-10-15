package fireflasher.rplog.forge;

import fireflasher.rplog.RPLog;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("rplog")
public class Entrypoint {
    public Entrypoint() {
        // Code here will run on both physical client and server.
        // Client classes may or may not be available - be careful!
        RPLog.LOGGER.info("Hello, Forge!");
        RPLog.init();

        // Initialize client and dedicated server entrypoints.
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EntrypointClient::init);
    }
}
