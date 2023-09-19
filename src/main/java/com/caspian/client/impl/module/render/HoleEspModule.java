package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ColorConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.combat.hole.Hole;
import com.caspian.client.api.manager.combat.hole.HoleSafety;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import net.minecraft.util.math.Box;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HoleEspModule extends ToggleModule
{
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to " +
            "display holes", 3.0f, 5.0f, 25.0f);
    Config<Float> heightConfig = new NumberConfig<>("Height", "Render height " +
            "of holes", 0.0f, 1.0f, 1.0f);
    Config<Boolean> doubleConfig = new BooleanConfig("Double", "Displays " +
            "double holes where the player can stand in the middle of two " +
            "blocks to nullify explosion damage", true);
    Config<Boolean> quadConfig = new BooleanConfig("Quad", "Displays " +
            "quad holes where the player can stand in the middle of four " +
            "blocks to nullify explosion damage", true);
    Config<Boolean> voidConfig = new BooleanConfig("Void", "Displays " +
            "void holes in the world", false);
    Config<Boolean> fadeConfig = new BooleanConfig("Fade", "Fades the opacity" +
            " of holes based on distance", false);
    //
    Config<Boolean> globalConfig = new BooleanConfig("GlobalColor", "Uses the" +
            " global client color when rendering holes", false);
    Config<Color> obsidianConfig = new ColorConfig("ObsidianColor", "The " +
            "color for rendering obsidian holes", new Color(255, 0, 0, 60));
    Config<Color> mixedConfig = new ColorConfig("MixedColor", "The " +
            "color for rendering mixed holes", new Color(255, 255, 0, 60));
    Config<Color> bedrockConfig = new ColorConfig("BedrockColor", "The " +
            "color for rendering bedrock holes", new Color(0, 255, 0, 60));

    /**
     *
     */
    public HoleEspModule()
    {
        super("HoleESP", "Displays nearby blast-resistant holes",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mc.player == null)
        {
            return;
        }
        for (Hole hole : Managers.HOLE.getHoles())
        {
            double dist = hole.squaredDistanceTo(mc.player);
            if (dist > rangeConfig.getValue() * rangeConfig.getValue())
            {
                continue;
            }
            double x = hole.getX();
            double y = hole.getY();
            double z = hole.getZ();
            Color color = getHoleColor(hole);
            //
            Box render;
            if (voidConfig.getValue() && hole.getSafety() == HoleSafety.VOID)
            {
                render = new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0);
            }
            else if (doubleConfig.getValue() && hole.isDoubleX())
            {
               render = new Box(x, y, z, x + 2.0,
                       y + heightConfig.getValue(), z + 1.0);
            }
            else if (doubleConfig.getValue() && hole.isDoubleZ())
            {
                render = new Box(x, y, z, x + 1.0,
                        y + heightConfig.getValue(), z + 2.0);
            }
            else if (quadConfig.getValue() && hole.isQuad())
            {
                render = new Box(x, y, z, x + 2.0,
                        y + heightConfig.getValue(), z + 2.0);
            }
            else
            {
                render = new Box(x, y, z, x + 1.0,
                        y + heightConfig.getValue(), z + 1.0);
            }
            RenderManager.renderBox(event.getMatrices(), render,
                    color.getRGB());
            RenderManager.renderBoundingBox(event.getMatrices(), render, 1.5f,
                    color.getRGB());
        }
    }

    /**
     *
     * @param hole
     * @return
     */
    private Color getHoleColor(Hole hole)
    {
        if (globalConfig.getValue())
        {
            return Modules.COLORS.getColor(60);
        }
        return switch (hole.getSafety())
        {
            case RESISTANT -> obsidianConfig.getValue();
            case MIXED -> mixedConfig.getValue();
            case UNBREAKABLE -> bedrockConfig.getValue();
            case VOID -> Color.RED;
        };
    }
}