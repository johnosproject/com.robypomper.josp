package com.robypomper.josp.jod.history;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.events.CloudStats;
import com.robypomper.josp.clients.apis.obj.APIObjsClient;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Un buffer locale per tutti i componenti
 */
public class JODHistory_002 implements JODHistory {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private JCPAPIsClientObj jcpClient;
    private APIObjsClient apiObjsClient;
    private final StatusHistoryArray statuses; //statuses;
    private final CloudStats stats;
    private final File statusesFile;    //statusesFile
    private final File statsFile;
    private boolean isSyncing = false;
    private int MAX_BUFFERED = 5;
    private final int REDUCE_BUFFER = 3;
    private final int MAX_FILE = 50;

    // Constructor

    /**
     * Create new History system.
     *
     * @param settings  the JOD settings.
     * @param jcpClient the JCP client.
     */
    public JODHistory_002(JODSettings_002 settings, JCPAPIsClientObj jcpClient) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        this.jcpClient.addConnectListener(jcpConnectListener);
        this.apiObjsClient = new APIObjsClient(jcpClient);
        boolean statusesFileLoaded = false;
        boolean statsFileLoaded = false;


        //this.eventFile = locSettings.getEventPath();
        StatusHistoryArray tmpStatuses = null;
        this.statusesFile = new File(HISTORY_FILE);
        if (!statusesFile.getParentFile().exists())
            statusesFile.getParentFile().mkdirs();
        else if (statusesFile.exists())
            try {
                tmpStatuses = new StatusHistoryArray(statusesFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }

        //this.eventStatsFile = locSettings.getEventStatsPath();
        CloudStats tmpStats = null;
        this.statsFile = new File(STATS_FILE);
        if (!statsFile.getParentFile().exists())
            statsFile.getParentFile().mkdirs();
        else if (statsFile.exists()) {
            try {
                tmpStats = new CloudStats(statsFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
        }

        //  if  stats NOT readable  and  statuses NOT readable
        if (tmpStats == null && tmpStatuses == null) {
            // generate stats
            try {
                if (statsFile.exists())
                    statsFile.delete();
                tmpStats = new CloudStats(statsFile);
                tmpStats.store();
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            statsFileLoaded = false;
            // generate statuses
            statusesFile.delete();
            try {
                tmpStatuses = new StatusHistoryArray(statusesFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            statusesFileLoaded = false;

            //  else if  stats NOT readable  and  statuses readable
        } else if (tmpStats == null && tmpStatuses != null) {
            // statuses already loaded
            statusesFileLoaded = true;
            // generate stats from statuses
            JOSPStatusHistory firstEvent = tmpStatuses.getFirst();
            JOSPStatusHistory lastEvent = tmpStatuses.getFirst();
            try {
                if (statsFile.exists())
                    statsFile.delete();
                tmpStats = new CloudStats(statsFile);
                tmpStats.store();
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            tmpStats.lastUploaded = 0;
            //      ...
            statsFileLoaded = false;

            //  else if  stats readable  and  statuses NOT readable
        } else if (tmpStats != null && tmpStatuses == null) {
            // stats already loaded
            statsFileLoaded = true;
            //      generate statuses _from stats
            statusesFile.delete();
            try {
                tmpStatuses = new StatusHistoryArray(statusesFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            //          statuses non può essere generato
            //          aggiornare stats a: buffered statuses cancellati
            statusesFileLoaded = false;

            //  else if  stats readable  and  statuses readable
        } else if (tmpStats != null && tmpStatuses != null) {
            // stats already loaded
            statsFileLoaded = true;
            // statuses already loaded
            statusesFileLoaded = true;
        }

        statuses = tmpStatuses;
        stats = tmpStats;

        log.info(Mrk_JOD.JOD_HISTORY, "Initialized JODHistory instance");
        log.debug(Mrk_JOD.JOD_HISTORY, String.format("                                   History buffered %d statuses on file %d", statuses.countBuffered(), statuses.countFile()));
        log.debug(Mrk_JOD.JOD_HISTORY, String.format("                                   History stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));
    }


    // Register new status

    @Override
    public void register(JODComponent comp, JODStateUpdate update) {
        synchronized (statuses) {
            long newId = statuses.count() + 1;
            JOSPStatusHistory s = new JOSPStatusHistory(newId, comp.getPath().getString(), comp.getType(), new Date(), update.encode());
            statuses.append(s);
            stats.lastStored = s.getId();
            stats.storeIgnoreExceptions();
        }

        if (isSyncing)
            sync();

        if (statuses.countBuffered() >= MAX_BUFFERED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (statuses) {
                            int pre = statuses.countBuffered();
                            statuses.flushCache(REDUCE_BUFFER);
                            int post = statuses.countBuffered();
                            log.debug(Mrk_JOD.JOD_HISTORY, String.format("Flushed %d statuses to file", pre - post));
                            log.debug(Mrk_JOD.JOD_HISTORY, String.format("History buffered %d statuses on file %d", statuses.countBuffered(), statuses.countFile()));
                        }

                    } catch (IOException ignore) {
                        assert false;
                    }
                }
            }).start();
        }
    }

    private void sync() {
        if (stats.lastUploaded == stats.lastStored) return;
        if (jcpClient == null || !jcpClient.isConnected()) return;

        List<JOSPStatusHistory> toUpload;
        synchronized (statuses) {
            try {
                toUpload = statuses.getById(stats.lastUploaded != -1 ? stats.lastUploaded : null, stats.lastStored);
                if (stats.lastUploaded != -1 && toUpload.size() > 1) toUpload.remove(0);

            } catch (IOException e) {
                e.printStackTrace();
                assert false;
                return;
            }

            if (toUpload.size() == 0) {
                log.debug(Mrk_JOD.JOD_HISTORY, String.format("No statuses found to uploads (CloudStats values lastUpd: %d; lastStored: %d", stats.lastUploaded, stats.lastStored));
                return;
            }

            log.debug(Mrk_JOD.JOD_HISTORY, String.format("Upload from %d to %d (%d statuses)", toUpload.get(0).getId(), toUpload.get(toUpload.size() - 1).getId(), toUpload.size()));
            for (JOSPStatusHistory e : toUpload)
                log.trace(Mrk_JOD.JOD_HISTORY, String.format("- event[%d] %s", e.getId(), e.getPayload()));

            try {
                apiObjsClient.uploadStatusHistory(toUpload);
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
            }

            stats.uploaded += toUpload.size();
            stats.lastUploaded = toUpload.get(toUpload.size() - 1).getId();
            stats.storeIgnoreExceptions();
        }
    }


    // Get statuses

    @Override
    public List<JOSPStatusHistory> getHistoryStatus(JODComponent comp, HistoryLimits limits) {
        JavaJSONArrayToFile.Filter<JOSPStatusHistory> filter = new JavaJSONArrayToFile.Filter<JOSPStatusHistory>() {
            @Override
            public boolean accepted(JOSPStatusHistory o) {
                return o.getCompPath().equalsIgnoreCase(comp.getPath().getString());
            }
        };

        if (limits.isLatestCount())
            return statuses.tryLatest(filter, limits.getLatestCount());

        if (limits.isAncientCount())
            return statuses.tryAncient(filter, limits.getAncientCount());

        if (limits.isIDRange())
            return statuses.tryById(filter, limits.getFromId(), limits.getToId());

        if (limits.isDateRange())
            return statuses.tryByDate(filter, limits.getFromDate(), limits.getToDate());

        try {
            return statuses.filterAll(filter);

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


    // Mngm methods

    @Override
    public void startCloudSync() {
        isSyncing = true;
        log.info(Mrk_JOD.JOD_HISTORY, "Start statuses sync to cloud");
        log.debug(Mrk_JOD.JOD_HISTORY, String.format("Events buffered %d statuses on file %d", statuses.countBuffered(), statuses.countFile()));
        log.debug(Mrk_JOD.JOD_HISTORY, String.format("Events stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));

        sync();
    }

    @Override
    public void stopCloudSync() {
        synchronized (statuses) {
            isSyncing = false;
            try {
                int pre = statuses.countBuffered();
                statuses.storeCache();
                int post = statuses.countBuffered();
                log.debug(Mrk_JOD.JOD_HISTORY, String.format("Stored %d statuses to file", pre - post));

                log.info(Mrk_JOD.JOD_HISTORY, "Stop event sync to cloud");
                log.debug(Mrk_JOD.JOD_HISTORY, String.format("Events buffered %d statuses on file %d", statuses.countBuffered(), statuses.countFile()));
                log.debug(Mrk_JOD.JOD_HISTORY, String.format("Events stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));

            } catch (IOException ignore) {
                assert false;
            }
        }
    }

    @Override
    public void storeCache() throws IOException {
        statuses.storeCache();
    }


    // JCP Client listeners

    private final JCPClient2.ConnectListener jcpConnectListener = new JCPClient2.ConnectListener() {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            sync();
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        }

    };


    // update JODHistory_002(...) to use file paths from jod settings
    private static final String HISTORY_FILE = "cache/history.jbs";
    private static final String STATS_FILE = "cache/history.jst";

}
