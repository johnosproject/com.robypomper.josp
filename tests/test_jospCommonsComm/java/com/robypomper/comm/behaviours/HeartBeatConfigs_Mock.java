package com.robypomper.comm.behaviours;

public class HeartBeatConfigs_Mock implements HeartBeatConfigs {
    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public void setTimeout(int ms) {
    }

    @Override
    public int getHBTimeout() {
        return 0;
    }

    @Override
    public void setHBTimeout(int ms) {
    }

    @Override
    public boolean isHBResponseEnabled() {
        return false;
    }

    @Override
    public void enableHBResponse(boolean enabled) {
    }

    @Override
    public void addListener(HeartBeatListener listener) {
    }

    @Override
    public void removeListener(HeartBeatListener listener) {
    }
}
