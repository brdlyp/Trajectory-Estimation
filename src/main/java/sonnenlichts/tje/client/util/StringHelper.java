package sonnenlichts.tje.client.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static sonnenlichts.tje.TrajectoryEstimationClient.MOD_ID;

public class StringHelper {
    public static boolean isNullOrEmpty(@Nullable String targetStr) {
        return targetStr == null || targetStr.isEmpty();
    }

    public static Identifier create(String target) {
        return Identifier.of(MOD_ID, target);
    }
}
