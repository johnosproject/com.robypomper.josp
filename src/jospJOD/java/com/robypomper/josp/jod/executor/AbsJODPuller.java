package com.robypomper.josp.jod.executor;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Default Puller class used by {@link JODPuller} implementations.
 */
public abstract class AbsJODPuller extends AbsJODWorker implements JODPuller {

    // Class's constants

    public static final int DEF_POLLING_TIME = 5000;


    // Internal vars

    private Timer timer;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODPuller(String name, String proto) {
        super(name, proto);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return timer != null;
    }

    /**
     * Polling time in ms.
     *
     * @return the polling time in ms.
     */
    protected long getPollingTime() {
        return DEF_POLLING_TIME;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void startTimer() {
        System.out.println(String.format("DEB: Start '%s' JOD Poller timer.", getName()));
        if (isEnabled()) return;

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pull();
            }
        }, 0, getPollingTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopTimer() {
        System.out.println(String.format("DEB: Stop '%s' JOD Poller timer.", getName()));
        if (!isEnabled()) return;

        timer.cancel();
        timer = null;
    }

}
