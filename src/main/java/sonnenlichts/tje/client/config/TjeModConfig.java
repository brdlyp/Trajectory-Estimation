package sonnenlichts.tje.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import sonnenlichts.tje.TrajectoryEstimationClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TjeModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("trajectory_estimation.json");

    // General settings
    public static boolean renderPoint = true;
    public static boolean targetSound = true;
    public static boolean renderCube = true;
    public static boolean renderLine = false;
    public static int cubeRed = 255;
    public static int cubeGreen = 255;
    public static int cubeBlue = 255;
    public static int cubeAlpha = 40;
    public static double cubeSize = 0.1D;
    public static int lineRed = 255;
    public static int lineGreen = 255;
    public static int lineBlue = 255;
    public static int lineAlpha = 255;
    public static double lineWidth = 4D;

    // Vanilla items
    public static boolean renderBow = true;
    public static boolean renderCrossbow = true;
    public static boolean renderTrident = true;
    public static boolean renderSplashBottle = true;
    public static boolean renderExperienceBottle = true;
    public static boolean renderEgg = true;
    public static boolean renderSnowball = true;
    public static boolean renderEnderpearl = true;
    public static boolean targetSoundBow = true;
    public static boolean targetSoundCrossbow = true;
    public static boolean targetSoundTrident = true;
    public static boolean targetSoundSplashBottle = true;
    public static boolean targetSoundExperienceBottle = true;
    public static boolean targetSoundEgg = true;
    public static boolean targetSoundSnowball = true;
    public static boolean targetSoundEnderpearl = true;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    applyConfig(data);
                }
            } catch (IOException e) {
                TrajectoryEstimationClient.LOGGER.error("Failed to load config", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            ConfigData data = new ConfigData();
            data.renderPoint = renderPoint;
            data.targetSound = targetSound;
            data.renderCube = renderCube;
            data.renderLine = renderLine;
            data.cubeRed = cubeRed;
            data.cubeGreen = cubeGreen;
            data.cubeBlue = cubeBlue;
            data.cubeAlpha = cubeAlpha;
            data.cubeSize = cubeSize;
            data.lineRed = lineRed;
            data.lineGreen = lineGreen;
            data.lineBlue = lineBlue;
            data.lineAlpha = lineAlpha;
            data.lineWidth = lineWidth;
            data.renderBow = renderBow;
            data.renderCrossbow = renderCrossbow;
            data.renderTrident = renderTrident;
            data.renderSplashBottle = renderSplashBottle;
            data.renderExperienceBottle = renderExperienceBottle;
            data.renderEgg = renderEgg;
            data.renderSnowball = renderSnowball;
            data.renderEnderpearl = renderEnderpearl;
            data.targetSoundBow = targetSoundBow;
            data.targetSoundCrossbow = targetSoundCrossbow;
            data.targetSoundTrident = targetSoundTrident;
            data.targetSoundSplashBottle = targetSoundSplashBottle;
            data.targetSoundExperienceBottle = targetSoundExperienceBottle;
            data.targetSoundEgg = targetSoundEgg;
            data.targetSoundSnowball = targetSoundSnowball;
            data.targetSoundEnderpearl = targetSoundEnderpearl;

            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            TrajectoryEstimationClient.LOGGER.error("Failed to save config", e);
        }
    }

    private static void applyConfig(ConfigData data) {
        renderPoint = data.renderPoint;
        targetSound = data.targetSound;
        renderCube = data.renderCube;
        renderLine = data.renderLine;
        cubeRed = data.cubeRed;
        cubeGreen = data.cubeGreen;
        cubeBlue = data.cubeBlue;
        cubeAlpha = data.cubeAlpha;
        cubeSize = data.cubeSize;
        lineRed = data.lineRed;
        lineGreen = data.lineGreen;
        lineBlue = data.lineBlue;
        lineAlpha = data.lineAlpha;
        lineWidth = data.lineWidth;
        renderBow = data.renderBow;
        renderCrossbow = data.renderCrossbow;
        renderTrident = data.renderTrident;
        renderSplashBottle = data.renderSplashBottle;
        renderExperienceBottle = data.renderExperienceBottle;
        renderEgg = data.renderEgg;
        renderSnowball = data.renderSnowball;
        renderEnderpearl = data.renderEnderpearl;
        targetSoundBow = data.targetSoundBow;
        targetSoundCrossbow = data.targetSoundCrossbow;
        targetSoundTrident = data.targetSoundTrident;
        targetSoundSplashBottle = data.targetSoundSplashBottle;
        targetSoundExperienceBottle = data.targetSoundExperienceBottle;
        targetSoundEgg = data.targetSoundEgg;
        targetSoundSnowball = data.targetSoundSnowball;
        targetSoundEnderpearl = data.targetSoundEnderpearl;
    }

    private static class ConfigData {
        boolean renderPoint = true;
        boolean targetSound = true;
        boolean renderCube = true;
        boolean renderLine = false;
        int cubeRed = 255;
        int cubeGreen = 255;
        int cubeBlue = 255;
        int cubeAlpha = 40;
        double cubeSize = 0.1D;
        int lineRed = 255;
        int lineGreen = 255;
        int lineBlue = 255;
        int lineAlpha = 255;
        double lineWidth = 4D;
        boolean renderBow = true;
        boolean renderCrossbow = true;
        boolean renderTrident = true;
        boolean renderSplashBottle = true;
        boolean renderExperienceBottle = true;
        boolean renderEgg = true;
        boolean renderSnowball = true;
        boolean renderEnderpearl = true;
        boolean targetSoundBow = true;
        boolean targetSoundCrossbow = true;
        boolean targetSoundTrident = true;
        boolean targetSoundSplashBottle = true;
        boolean targetSoundExperienceBottle = true;
        boolean targetSoundEgg = true;
        boolean targetSoundSnowball = true;
        boolean targetSoundEnderpearl = true;
    }
}
