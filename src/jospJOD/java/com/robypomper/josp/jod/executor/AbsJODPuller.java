package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Default Puller class used by {@link JODPuller} implementations.
 */
public abstract class AbsJODPuller extends AbsJODWorker implements JODPuller {

    // Class's constants

    public static final String TH_PULLER_NAME_FORMAT = "_PULL_%s";
    public static final int DEF_POLLING_TIME = 5000;


    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private Timer timer;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODPuller(String name, String proto, JODComponent component) {
        super(name, proto, component);
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
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Start '%s' puller", getName()));
        if (isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Starting puller timer");
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(String.format(TH_PULLER_NAME_FORMAT, getName()));
                pull();
            }
        }, 0, getPollingTime());

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Puller timer started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopTimer() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Stop '%s' puller timer", getName()));
        if (!isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Stopping puller timer");
        timer.cancel();
        timer = null;
        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Puller timer stopped");
    }

}
