package sonnenlichts.tje;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonnenlichts.tje.client.config.TjeModConfig;
import sonnenlichts.tje.client.event.ClientRenderHandler;

public class TrajectoryEstimationClient implements ClientModInitializer {
    public static final String MOD_ID = "trajectory_estimation";
    public static final String VERSION = "1.1.0";
    public static final String NAME = "Trajectory Estimation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ClientRenderHandler renderHandler;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Trajectory Estimation for Fabric");

        // Load configuration
        TjeModConfig.load();

        // Create render handler
        renderHandler = new ClientRenderHandler();

        // Register world render event
        WorldRenderEvents.END.register(context -> {
            renderHandler.onWorldRender(context);
        });

        LOGGER.info("Trajectory Estimation initialized successfully");
    }
}
