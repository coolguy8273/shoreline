package net.shoreline.client.impl.module.render;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.manager.combat.hole.Hole;
import net.shoreline.client.impl.manager.combat.hole.HoleType;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;

import java.awt.*;

/**
 * @author linus
 * @since 1.0
 */
public class HoleESPModule extends ToggleModule {
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to display holes", 3.0f, 5.0f, 25.0f);
    Config<Float> heightConfig = new NumberConfig<>("Size", "Render height of holes", -1.0f, 1.00f, 1.0f);
    Config<Boolean> obsidianCheckConfig = new BooleanConfig("Obsidian", "Displays obsidian holes", true);
    Config<Boolean> obsidianBedrockConfig = new BooleanConfig("Obsidian-Bedrock", "Displays mixed obsidian and bedrock holes", true);
    Config<Boolean> doubleConfig = new BooleanConfig("Double", "Displays double holes where the player can stand in the middle of two blocks to block explosion damage", true);
    Config<Boolean> quadConfig = new BooleanConfig("Quad", "Displays quad holes where the player can stand in the middle of four blocks to block explosion damage", true);
    Config<Boolean> voidConfig = new BooleanConfig("Void", "Displays void holes in the world", false);
    Config<Boolean> fadeConfig = new BooleanConfig("Fade", "Fades the opacity of holes based on distance", false);
    Config<Color> obsidianConfig = new ColorConfig("ObsidianColor", "The color for rendering obsidian holes", new Color(255, 0, 0, 60));
    Config<Color> mixedConfig = new ColorConfig("Obsidian-BedrockColor", "The color for rendering mixed holes", new Color(255, 255, 0, 60));
    Config<Color> bedrockConfig = new ColorConfig("BedrockColor", "The color for rendering bedrock holes", new Color(0, 255, 0, 60));

    public HoleESPModule() {
        super("HoleESP", "Displays nearby blast resistant holes", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null) {
            return;
        }
        for (Hole hole : Managers.HOLE.getHoles()) {
            if ((hole.isDoubleX() || hole.isDoubleZ()) && !doubleConfig.getValue()
                    || hole.isQuad() && !quadConfig.getValue()
                    || hole.getSafety() == HoleType.VOID && !voidConfig.getValue()
                    || hole.getSafety() == HoleType.OBSIDIAN && !obsidianCheckConfig.getValue()
                    || hole.getSafety() == HoleType.OBSIDIAN_BEDROCK && !obsidianBedrockConfig.getValue()) {
                continue;
            }
            double dist = hole.squaredDistanceTo(mc.player);
            if (dist > ((NumberConfig) rangeConfig).getValueSq()) {
                continue;
            }
            double x = hole.getX();
            double y = hole.getY();
            double z = hole.getZ();
            Color color = getHoleColor(hole);
            Box render = null;
            if (hole.getSafety() == HoleType.VOID) {
                render = new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0);
            } else if (hole.isDoubleX()) {
                render = new Box(x, y, z, x + 2.0,
                        y + heightConfig.getValue(), z + 1.0);
            } else if (hole.isDoubleZ()) {
                render = new Box(x, y, z, x + 1.0,
                        y + heightConfig.getValue(), z + 2.0);
            } else if (hole.isQuad()) {
                render = new Box(x, y, z, x + 2.0,
                        y + heightConfig.getValue(), z + 2.0);
            } else if (hole.isStandard()) {
                render = new Box(x, y, z, x + 1.0,
                        y + heightConfig.getValue(), z + 1.0);
            }
            if (render == null) {
                return;
            }
            double alpha = 1.0;
            if (fadeConfig.getValue()) {
                double fadeRange = rangeConfig.getValue() - 1.0;
                double fadeRangeSq = fadeRange * fadeRange;
                alpha = (fadeRangeSq + 9.0 - mc.player.squaredDistanceTo(hole.getX(),
                        hole.getY(), hole.getZ())) / fadeRangeSq;
                alpha = MathHelper.clamp(alpha, 0.0, 1.0);
            }
            RenderManager.renderBox(event.getMatrices(), render,
                    Modules.COLORS.getRGB((int) (alpha * 80.0f)));
            RenderManager.renderBoundingBox(event.getMatrices(), render, 1.5f,
                    Modules.COLORS.getRGB((int) (alpha * 125.0f)));
        }
    }

    private Color getHoleColor(Hole hole) {
        return switch (hole.getSafety()) {
            case OBSIDIAN -> obsidianConfig.getValue();
            case OBSIDIAN_BEDROCK -> mixedConfig.getValue();
            case BEDROCK -> bedrockConfig.getValue();
            case VOID -> Color.RED;
        };
    }

    public double getRange() {
        return rangeConfig.getValue();
    }
}
