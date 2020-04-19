package com.robypomper.discovery;

import java.net.InetAddress;

/**
 * Base class for {@link Publisher} implementations.
 * <p>
 * This class manage type, name and port of the service to publish for all
 * {@link Publisher} subclasses.
 */
public abstract class AbsPublisher implements Publisher {

    // Class constants

    public static final int WAIT_MAX_COUNT = 100;
    public static final int WAIT_LOOP_TIME = 100; //ms


    // Internal vars

    private final String srvName;
    private final String srvType;
    private final int srvPort;
    private final String srvText;

    private boolean isPublishing = false;
    private boolean isDepublishing = false;
    private boolean isPublished = false;
    private Discover discover;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType   the service type to publish.
     * @param srvName   the service name to publish.
     * @param srvPort   the service port to publish.
     * @param extraText string containing extra text related to service to publish.
     */
    protected AbsPublisher(String srvType, String srvName, int srvPort, String extraText) {
        this.srvType = srvType;
        this.srvName = srvName;
        this.srvPort = srvPort;
        this.srvText = extraText;
    }


    // Publication mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublished() {
        return isPublished;
    }

    protected boolean isPublishing() {
        return isPublishing;
    }

    protected boolean isDepublishing() {
        return isDepublishing;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceName() {
        return srvName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceType() {
        return srvType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServicePort() {
        return srvPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceExtraText() {
        return srvText;
    }


    // Subclass support methods

    /**
     * Sub class must call this method to set the instance in publishing mode.
     * <p>
     * The publishing mode starts and end with the {@link #publish(boolean)} method.
     *
     * @param enable if <code>true</code> it start the publishing mode, otherwise
     *               stop it.
     * @return <code>false</code> if current instance can't set to publishing mode.
     */
    protected boolean setIsPublishing(boolean enable) {
        // Checks on publishing
        if (enable) {
            if (isPublished()) {
                LogDiscovery.logPub(String.format("WAR: can't publish service '%s' because already published", getServiceName()));
                return false;
            }
            if (isPublishing() || isDepublishing()) {
                LogDiscovery.logPub(String.format("WAR: can't publish service '%s' because is publishing/hiding", getServiceName()));
                return false;
            }
        }

        isPublishing = enable;
        return true;
    }

    /**
     * Sub class must call this method to set the instance in de-publishing mode.
     * <p>
     * The publishing mode starts and end with the {@link #hide(boolean)} method.
     *
     * @param enable if <code>true</code> it start the de-publishing mode, otherwise
     *               stop it.
     * @return <code>false</code> if current instance can't set to de-publishing mode.
     */
    protected boolean setIsDepublishing(boolean enable) {
        if (enable) {
            if (!isPublished()) {
                LogDiscovery.logPub(String.format("WAR: can't hide service '%s' because already hided", getServiceName()));
                return false;
            }
            if (isPublishing || isDepublishing) {
                LogDiscovery.logPub(String.format("WAR: can't publish service '%s' because is publishing/hiding", getServiceName()));
                return false;
            }
        }

        isDepublishing = enable;
        return true;
    }

    /**
     * Start internal auto discovery service used to detect if current service
     * is published or hided successfully.
     *
     * @param implementation the string identify the implementation required.
     */
    protected void startAutoDiscovery(String implementation) throws PublishException {
        try {
            discover = DiscoverySystemFactory.createDiscover(implementation, getServiceType());
        } catch (Discover.DiscoveryException e) {
            throw new PublishException(String.format("ERR: can't '%s' setup service's published checks on discovery initialization because %s", getServiceName(), e.getMessage()));
        }

        discover.addListener(new DiscoverListener() {
            @Override
            public void onServiceDiscovered(String type, String name, InetAddress address, int port, String extraText) {
                if (type.equalsIgnoreCase(getServiceType())
                        && name.startsWith(getServiceName())        /* Published services can include as suffix interface or other identifier */
                        && port == getServicePort())
                    isPublished = true;
            }

            @Override
            public void onServiceLost(String type, String name) {
                if (type.equalsIgnoreCase(getServiceType())
                        && name.startsWith(getServiceName()))       /* Published services can include as suffix interface or other identifier */
                    isPublished = false;
            }

        });

        try {
            discover.start();
        } catch (Discover.DiscoveryException e) {
            throw new PublishException(String.format("ERR: can't setup '%s' service's published checks on discovery start because %s", getServiceName(), e.getMessage()));
        }
    }

    /**
     * Stop internal auto discovery system.
     */
    protected void stopAutoDiscovery() {
        try {
            discover.stop();
        } catch (Discover.DiscoveryException e) {
            LogDiscovery.logPub(String.format("WAR: can't destroy service's published checks '%s' because %s", getServiceName(), e.getMessage()));
        }
        discover = null;
    }

    /**
     * Method to wait for current service publication via internal auto discovery
     * system.
     * <p>
     * This method wait at least for {@value #WAIT_LOOP_TIME} x {@value #WAIT_MAX_COUNT}
     * milliseconds, then throw an exception.
     */
    protected void waitServicePublication() throws PublishException {
        try {
            int count = 0;
            while (!isPublished() && count < WAIT_MAX_COUNT) {
                //noinspection BusyWait
                Thread.sleep(WAIT_LOOP_TIME);
                count++;
            }
            if (!isPublished())
                throw new PublishException(String.format("ERR: service '%s' not published after %d seconds", getServiceName(), WAIT_MAX_COUNT * WAIT_LOOP_TIME));

        } catch (InterruptedException e) {
            if (!isPublished())
                throw new PublishException(String.format("ERR: can't wait service '%s' publication because %s", getServiceName(), e.getMessage()));
        }
    }

    /**
     * Method to wait for current service de-publication via internal auto discovery
     * system.
     * <p>
     * This method wait at least for {@value #WAIT_LOOP_TIME} x {@value #WAIT_MAX_COUNT}
     * milliseconds, then throw an exception.
     */
    protected void waitServiceDepublication() throws PublishException {
        try {
            int count = 0;
            while (isPublished() && count < WAIT_MAX_COUNT) {
                //noinspection BusyWait
                Thread.sleep(WAIT_LOOP_TIME);
                count++;
            }
            if (isPublished())
                throw new PublishException(String.format("ERR: service '%s' not hided after %d seconds", getServiceName(), WAIT_MAX_COUNT * WAIT_LOOP_TIME));

        } catch (InterruptedException e) {
            throw new PublishException(String.format("ERR: can't wait service '%s' publication because %s", getServiceName(), e.getMessage()));
        }
    }

}
