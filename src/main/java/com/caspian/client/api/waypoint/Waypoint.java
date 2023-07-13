package com.caspian.client.api.waypoint;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.ConfigContainer;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.util.math.Position;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Waypoint extends ConfigContainer implements Position
{
    //
    private final String ip;
    //
    private final Config<Double> xConfig = new NumberConfig<>("X", "X " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    private final Config<Double> yConfig = new NumberConfig<>("Y", "Y " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    private final Config<Double> zConfig = new NumberConfig<>("Z", "Z " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    private final Timer timer;

    /**
     *
     *
     * @param name
     * @param ip
     * @param x
     * @param y
     * @param z
     */
    public Waypoint(String name, String ip, double x, double y, double z)
    {
        super(name);
        this.ip = ip;
        xConfig.setValue(x);
        yConfig.setValue(y);
        zConfig.setValue(z);
        this.timer = new CacheTimer();
    }

    /**
     *
     *
     * @param time
     * @return
     */
    private boolean passedTime(long time)
    {
        return timer.passed(time);
    }

    public String getIp()
    {
        return ip;
    }

    @Override
    public double getX()
    {
        return xConfig.getValue();
    }

    @Override
    public double getY()
    {
        return yConfig.getValue();
    }

    @Override
    public double getZ()
    {
        return zConfig.getValue();
    }
}
