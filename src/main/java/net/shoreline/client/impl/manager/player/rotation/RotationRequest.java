package net.shoreline.client.impl.manager.player.rotation;

import net.shoreline.client.api.module.RotationModule;

/**
 * @author linus, bon55
 * @since 1.0
 */
public class RotationRequest {
    //
    private final RotationModule requester;
    private final int priority;
    private long time;
    //
    private float yaw, pitch;
    //
    private final boolean clientRotation;

    /**
     * @param requester
     * @param priority
     * @param yaw
     * @param pitch
     */
    public RotationRequest(RotationModule requester, boolean clientRotation,
                           int priority, float yaw, float pitch) {
        this.requester = requester;
        this.clientRotation = clientRotation;
        this.time = System.currentTimeMillis();
        this.priority = priority;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * @param requester
     * @param yaw
     * @param pitch
     */
    public RotationRequest(RotationModule requester, boolean clientRotation,
                           float yaw, float pitch) {
        this(requester, clientRotation, 100, yaw, pitch);
    }

    public RotationModule getModule() {
        return requester;
    }

    public boolean isClientRotation() {
        return clientRotation;
    }

    /**
     * @return
     */
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
