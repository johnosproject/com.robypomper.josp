package com.robypomper.discovery;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static final Logger log = LogManager.getLogger();
    private final String srvName;
    private final String srvType;
    private final int srvPort;
    private final String srvText;

    private boolean isPublishing = false;
    private boolean isDepublishing = false;
    protected Discover discover;
    private final List<DiscoveryService> discoveredPublications = new ArrayList<>();
    private final List<String> interfaces = new ArrayList<>();


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
        log.info(Mrk_Commons.DISC_PUB, String.format("Initialized AbsPublisher instance for '%s:%s' service on port '%d'", srvType, srvName, srvPort));
    }


    // Publication mngm

    /**
     * {@inheritDoc}
     *
     * Basic implementation that use only the <code>boolean isPublished</code> field set on first service (self) resolving
     * and on first service (self) lost.
     */
    @Override
    public boolean isPublishedFully() {
        // published on ALL jmdns
        return isPublishedPartially() && discoveredPublications.size() == interfaces.size();
    }

    /**
     * {@inheritDoc}
     *
     * Basic implementation that use only the <code>boolean isPublished</code> field set on first service (self) resolving
     * and on first service (self) lost.
     */
    @Override
    public boolean isPublishedPartially() {
        // published at last ONE self-publication (discovered by internal discovery)
        return discover!=null && discoveredPublications.size() > 0;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Discover getInternalDiscovered() {
        return discover;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getInterfaces(){
        return interfaces;
    }


    // Publishing methods (SubClass Support Methods)

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
            if (isPublishedPartially()) {
                log.warn(Mrk_Commons.DISC_PUB, String.format("Can't publish service '%s' because already published", getServiceName()));
                return false;
            }
            if (isPublishing() || isDepublishing()) {
                log.warn(Mrk_Commons.DISC_PUB, String.format("Can't publish service '%s' because is publishing/hiding", getServiceName()));
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
            if (!isPublishedPartially()) {
                log.warn(Mrk_Commons.DISC_PUB, String.format("Can't hide service '%s' because already hided", getServiceName()));
                return false;
            }
            if (isPublishing || isDepublishing) {
                log.warn(Mrk_Commons.DISC_PUB, String.format("Can't hide service '%s' because is publishing/hiding", getServiceName()));
                return false;
            }
        }

        isDepublishing = enable;
        return true;
    }


    // Internal discovery mngm (SubClass Support Methods)

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
            public void onServiceDiscovered(DiscoveryService discService) {
                registerPublication(discService);
                if (!interfaces.contains(discService.intf))
                    registerInterface(discService.intf);
            }

            @Override
            public void onServiceLost(DiscoveryService lostService) {
                deregisterPublication(lostService);
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
            if (discover!=null)
                discover.stop();
        } catch (Discover.DiscoveryException e) {
            log.warn(Mrk_Commons.DISC_PUB, String.format("Can't destroy service's published checks '%s' because %s", getServiceName(), e.getMessage()));
        }
        discover = null;
    }


    // Wait methods (SubClass Support Methods)

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
            Date startAt = new Date();
            while (!isPublishedFully() && count < WAIT_MAX_COUNT) {
                //noinspection BusyWait
                Thread.sleep(WAIT_LOOP_TIME);
                count++;
            }

            double timeElapsed = (double)(new Date().getTime() - startAt.getTime() ) / 1000;
            if (!isPublishedPartially()) {
                System.out.println("\n\n\n\n--\n");
                hide(false);
                throw new PublishException(String.format("ERR: service '%s' not published after %f seconds", getServiceName(), timeElapsed));
            }
            if (!isPublishedFully())
                log.warn(Mrk_Commons.DISC_PUB, String.format("WAR: service '%s' not published on all interfaces after %f seconds", getServiceName(), timeElapsed));

        } catch (InterruptedException ignore) {}
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
            Date startAt = new Date();
            while (isPublishedPartially() && count < WAIT_MAX_COUNT) {
                //noinspection BusyWait
                Thread.sleep(WAIT_LOOP_TIME);
                count++;
            }

            double timeElapsed = (double)(new Date().getTime() - startAt.getTime() ) / 1000;
            if (isPublishedFully())
                throw new PublishException(String.format("ERR: service '%s' not hided after %f seconds", getServiceName(), timeElapsed));
            if (isPublishedPartially())
                log.warn(Mrk_Commons.DISC_PUB, String.format("WAR: service '%s' not hided on all interfaces after %f seconds", getServiceName(), timeElapsed));

        } catch (InterruptedException ignore) {}
    }


    // Publications registration methods (SubClass Support Methods)

    private void registerPublication(DiscoveryService discSrv) {
        if (!discSrv.name.equalsIgnoreCase(getServiceName()))
            return;

        if (discSrv.alreadyIn(discoveredPublications))
            return;

        System.out.println(String.format("DiscovePub(%s)\tAdd %s", Integer.toHexString(discover.hashCode()), discSrv));
        discoveredPublications.add(discSrv);
        //emit discovered
    }

    private void deregisterPublication(DiscoveryService lostSrv) {
        if (!lostSrv.name.equalsIgnoreCase(getServiceName()))
            return;

        if (!lostSrv.alreadyIn(discoveredPublications))
            return;

        System.out.println(String.format("DiscovePub(%s)\tRem %s", Integer.toHexString(discover.hashCode()), lostSrv));
        discoveredPublications.remove(lostSrv.extractFrom(discoveredPublications));
        //emit lost
    }

    private void deregisterAllPublication() {
        List<DiscoveryService> toRemove = new ArrayList<>(discoveredPublications);
        for (DiscoveryService srv : toRemove) {
            deregisterPublication(srv);
        }
    }


    // Interfaces registration methods (SubClass Support Methods)

    public void registerInterface(String addIntf) {
        if (interfaces.contains(addIntf))
            return;

        interfaces.add(addIntf);
    }

    public void deregisterInterface(String remIntf) {
        if (!interfaces.contains(remIntf))
            return;

        interfaces.remove(remIntf);
    }


    // DNS-SD names de/encoding (Static Support Methods)

    public static String getServiceNameEncoded_DNSSD(String decoded) {
        return decoded
                .replaceAll(" ", "\\\\032");
    }

    public static String getServiceNameDecoded_DNSSD(String encoded) {
        return encoded
                .replaceAll("\\\\032"," ");
    }

    public static String getServiceNameEncoded_Avahi(String decoded) {
        return decoded
                .replaceAll(" ", "\\\\032")
                .replaceAll("&", "\\\\038")
                .replaceAll("\\(", "\\\\040")
                .replaceAll("\\)", "\\\\041");
    }

    public static String getServiceNameDecoded_Avahi(String encoded) {
        return encoded
                .replaceAll("\\\\032"," ")
                .replaceAll("\\\\038","&")
                .replaceAll("\\\\040","(")
                .replaceAll("\\\\041",")");
    }

}
