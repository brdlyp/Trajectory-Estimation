package sonnenlichts.tje.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.trajectory_estimation.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General category
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.trajectory_estimation.general"));

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_point"),
                        TjeModConfig.renderPoint)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderPoint = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.target_sound"),
                        TjeModConfig.targetSound)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.targetSound = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_cube"),
                        TjeModConfig.renderCube)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderCube = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_line"),
                        TjeModConfig.renderLine)
                .setDefaultValue(false)
                .setSaveConsumer(val -> TjeModConfig.renderLine = val)
                .build());

        // Cube appearance
        ConfigCategory cubeAppearance = builder.getOrCreateCategory(Text.translatable("config.trajectory_estimation.cube_appearance"));

        cubeAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.cube_red"),
                        TjeModConfig.cubeRed, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.cubeRed = val)
                .build());

        cubeAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.cube_green"),
                        TjeModConfig.cubeGreen, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.cubeGreen = val)
                .build());

        cubeAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.cube_blue"),
                        TjeModConfig.cubeBlue, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.cubeBlue = val)
                .build());

        cubeAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.cube_alpha"),
                        TjeModConfig.cubeAlpha, 0, 255)
                .setDefaultValue(40)
                .setSaveConsumer(val -> TjeModConfig.cubeAlpha = val)
                .build());

        cubeAppearance.addEntry(entryBuilder.startDoubleField(
                        Text.translatable("config.trajectory_estimation.cube_size"),
                        TjeModConfig.cubeSize)
                .setDefaultValue(0.1D)
                .setSaveConsumer(val -> TjeModConfig.cubeSize = val)
                .build());

        // Line appearance
        ConfigCategory lineAppearance = builder.getOrCreateCategory(Text.translatable("config.trajectory_estimation.line_appearance"));

        lineAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.line_red"),
                        TjeModConfig.lineRed, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.lineRed = val)
                .build());

        lineAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.line_green"),
                        TjeModConfig.lineGreen, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.lineGreen = val)
                .build());

        lineAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.line_blue"),
                        TjeModConfig.lineBlue, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.lineBlue = val)
                .build());

        lineAppearance.addEntry(entryBuilder.startIntSlider(
                        Text.translatable("config.trajectory_estimation.line_alpha"),
                        TjeModConfig.lineAlpha, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(val -> TjeModConfig.lineAlpha = val)
                .build());

        lineAppearance.addEntry(entryBuilder.startDoubleField(
                        Text.translatable("config.trajectory_estimation.line_width"),
                        TjeModConfig.lineWidth)
                .setDefaultValue(4D)
                .setSaveConsumer(val -> TjeModConfig.lineWidth = val)
                .build());

        // Vanilla items
        ConfigCategory vanillaItems = builder.getOrCreateCategory(Text.translatable("config.trajectory_estimation.vanilla_items"));

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_bow"),
                        TjeModConfig.renderBow)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderBow = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_crossbow"),
                        TjeModConfig.renderCrossbow)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderCrossbow = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_trident"),
                        TjeModConfig.renderTrident)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderTrident = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_snowball"),
                        TjeModConfig.renderSnowball)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderSnowball = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_egg"),
                        TjeModConfig.renderEgg)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderEgg = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_enderpearl"),
                        TjeModConfig.renderEnderpearl)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderEnderpearl = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_splash_bottle"),
                        TjeModConfig.renderSplashBottle)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderSplashBottle = val)
                .build());

        vanillaItems.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("config.trajectory_estimation.render_experience_bottle"),
                        TjeModConfig.renderExperienceBottle)
                .setDefaultValue(true)
                .setSaveConsumer(val -> TjeModConfig.renderExperienceBottle = val)
                .build());

        builder.setSavingRunnable(TjeModConfig::save);

        return builder.build();
    }
}
