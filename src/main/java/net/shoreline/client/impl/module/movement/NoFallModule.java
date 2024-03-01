package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import net.shoreline.client.util.string.EnumFormatter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.world.World;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoFallModule extends ToggleModule
{
    //
    Config<NoFallMode> modeConfig = new EnumConfig<>("Mode", "The mode to " +
            "prevent fall damage", NoFallMode.ANTI, NoFallMode.values());

    /**
     *
     */
    public NoFallModule()
    {
        super("NoFall", "Prevents all fall damage", ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getModuleData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE
                && modeConfig.getValue() == NoFallMode.LATENCY)
        {
            if (Globals.mc.player.fallDistance <= Globals.mc.player.getSafeFallDistance())
            {
                return;
            }
            if (Globals.mc.world.getRegistryKey() == World.NETHER)
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        Globals.mc.player.getX(), 0, Globals.mc.player.getZ(), true));
            }
            else
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        0, 64, 0, true));
            }
            Globals.mc.player.fallDistance = 0.0f;
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (Globals.mc.player == null || Globals.mc.player.fallDistance <= Globals.mc.player.getSafeFallDistance())
        {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
        {
            if (modeConfig.getValue() == NoFallMode.VANILLA)
            {
                ((AccessorPlayerMoveC2SPacket) packet).hookSetOnGround(true);
            }
            else if (modeConfig.getValue() == NoFallMode.ANTI)
            {
                double y = packet.getY(Globals.mc.player.getY());
                ((AccessorPlayerMoveC2SPacket) packet).hookSetY(y + 0.10000000149011612);
            }
        }
    }

    public enum NoFallMode
    {
        ANTI,
        LATENCY,
        VANILLA
    }
}