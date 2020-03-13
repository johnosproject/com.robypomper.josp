package com.robypomper.josp.jod.executor;


/**
 * Convenient Listener class used by {@link JODListener} implementations that
 * provide server basic class.
 */
public abstract class AbsJODListenerLoop extends AbsJODListener {

    // Internal vars

    private Thread thread;
    private boolean mustStop = false;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODListenerLoop(String name, String proto) {
        super(name, proto);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return thread != null;
    }

    /**
     * This method can be used in JOD Listeners implementations when check the
     * server's infinite loop.
     *
     * @return <code>true</code> if and only if the listener's server must shutdown.
     */
    protected boolean mustShoutingDown() {
        return mustStop;
    }

    /**
     * Return server's infinte loop function.
     * <p>
     * Implementation of this function must execute the main loop of the listener
     * and when received updates then send the update to the corresponding JOD
     * Component via the {@link #sendUpdate()} method.
     * <p>
     * This runnable will be executed on listener start and stopped (interrupted)
     * on listener stop. On stop, the server's thread receive an interrupt and
     * the method {@link #mustShoutingDown()} return <code>true</code>.
     */
    protected abstract void getServerLoop();

    /**
     * Wait time in ms for thread terminating.
     * <p>
     * The value can be get from poller's settings or hardcoded by poller
     * implementation.
     *
     * @return the waiting time in ms.
     */
    protected long getJoinTime() {
        return DEF_JOIN_TIME;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"Anonymous2MethodRef", "Convert2Lambda"})
    @Override
    public void listen() {
        System.out.println(String.format("DEB: Start '%s' JOD Listener server.", getName()));
        if (isEnabled()) return;

        mustStop = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getServerLoop();
            }
        });
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void halt() {
        System.out.println(String.format("DEB: Halt '%s' JOD Listener server.", getName()));
        if (!isEnabled()) return;

        mustStop = true;
        thread.interrupt();
        try {
            thread.join(getJoinTime());
        } catch (InterruptedException e) {
            System.out.println(String.format("WAR: Forced halt of '%s' JOD Listener server.", getName()));
        }
    }
}
