package sonnenlichts.tje.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ModRenderType {

    /**
     * Creates a render layer suitable for rendering translucent cubes with the given texture.
     * Uses the entity translucent render layer which supports:
     * - Position, color, texture, overlay, light, and normal vertex attributes
     * - Translucent blending
     * - No backface culling
     */
    public static RenderLayer cube(Identifier location) {
        return RenderLayer.getEntityTranslucent(location);
    }
}
