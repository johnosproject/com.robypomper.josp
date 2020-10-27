/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod.events;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JCPObjectInfo;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * This is the JOD Events implementation.
 * <p>
 * ...
 */
public class JODEvents_002 implements JODEvents {

    private static final String EVNTS_FILE = "cache/events.jev";
    private static final String STATS_FILE = "cache/events.jst";

    public static void main(String[] args) {
        deleteFile(EVNTS_FILE);
        deleteFile(STATS_FILE);
        JODEvents_002 e = null;

        // Create new Events
        e = create(true);
        // Register
        e.register(JOSPEvent.Type.JOD_COMM_CLOUD_CONN, "p1","a");
        e.register(JOSPEvent.Type.JOD_COMM_CLOUD_DISC, "p1","b");
        printEvents(e.events);
        // Store
        storeEvents(e, true);

        //// Load previous Events, Register new events and store
        //generateEventsCD(e,true);

        // Send registered events to cloud
        printStats(e.stats);
        e.sync();
        printStats(e.stats);

        e.register(JOSPEvent.Type.JOD_COMM_CLOUD_DISC,"p1", "c");
        e.sync();
        printStats(e.stats);
    }

    private static JODEvents_002 create(boolean print) {
        // Create
        JODEvents_002 e = new JODEvents_002(null, null);
        if (print) printEvents(e.events);
        return e;
    }

    private static void storeEvents(JODEvents_002 e, boolean print) {
        try {
            e.events.storeCache();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if (print) printEvents(e.events);
    }

    private static void deleteFile(String fileName) {
        new File(fileName).delete();
    }

    private static void createWrongFile(String fileName) throws IOException {
        String content = "Hello World !!";
        Files.write(Paths.get(fileName), content.getBytes());
    }

    private static void printStats(CloudStats s) {
        System.out.println("    #################### ");
        System.out.println("s.lastStored    " + s.lastStored);
        System.out.println("s.lastUploaded  " + s.lastUploaded);
        System.out.println("s.lastDelete    " + s.lastDelete);

        System.out.println("s.uploaded      " + s.uploaded);
        System.out.println("s.deleted       " + s.deleted);
        System.out.println("s.unUploaded    " + s.unUploaded);
    }

    public static void printEvents(EventsArray arrayFile) {
        System.out.println(" ############################### ");
        System.out.println("count:         " + arrayFile.count());
        System.out.println("countBuffered: " + arrayFile.countBuffered());
        System.out.println("countFile:     " + arrayFile.countFile());
        System.out.println("getFirst:      " + (arrayFile.getFirst() != null ? arrayFile.getFirst().id : "null"));
        System.out.println("getLast:       " + (arrayFile.getLast() != null ? arrayFile.getLast().id : "null"));
    }


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private JCPClient_Object jcpClient;
    private JCPEvents jcpEvents;
    private final EventsArray events;
    private final CloudStats stats;
    private final File eventsFile;
    private final File statsFile;
    private boolean isSyncing = false;
    private int maxBuffered = 5;
    private int reduceBuffer = 3;
    private int maxFile = 50;


    // Constructor

    /**
     * Create new Events system.
     * <p>
     * This constructor create an instance of {@link JODEvents} and (if doesn't
     * exist) create local events files.
     *
     * @param settings  the JOD settings.
     * @param jcpClient the JCP client.
     */
    public JODEvents_002(JODSettings_002 settings, JCPClient_Object jcpClient) {
        this.locSettings = settings;
        setJCPClient(jcpClient);
        boolean eventFileLoaded = false;
        boolean statsFileLoaded = false;


        //this.eventFile = locSettings.getEventPath();
        EventsArray tmpEvents = null;
        this.eventsFile = new File(EVNTS_FILE);
        if (!eventsFile.getParentFile().exists())
            eventsFile.getParentFile().mkdirs();
        else if (eventsFile.exists())
            try {
                tmpEvents = new EventsArray(eventsFile);
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

        //  if  stats NOT readable  and  events NOT readable
        if (tmpStats == null && tmpEvents == null) {
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
            // generate events
            eventsFile.delete();
            try {
                tmpEvents = new EventsArray(eventsFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            eventFileLoaded = false;

            //  else if  stats NOT readable  and  events readable
        } else if (tmpStats == null && tmpEvents != null) {
            // events already loaded
            eventFileLoaded = true;
            // generate stats from events
            JOSPEvent firstEvent = tmpEvents.getFirst();
            JOSPEvent lastEvent = tmpEvents.getFirst();
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

            //  else if  stats readable  and  events NOT readable
        } else if (tmpStats != null && tmpEvents == null) {
            // stats already loaded
            statsFileLoaded = true;
            //      generate events _from stats
            eventsFile.delete();
            try {
                tmpEvents = new EventsArray(eventsFile);
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            //          events non può essere generato
            //          aggiornare stats a: buffered events cancellati
            eventFileLoaded = false;

            //  else if  stats readable  and  events readable
        } else if (tmpStats != null && tmpEvents != null) {
            // stats already loaded
            statsFileLoaded = true;
            // events already loaded
            eventFileLoaded = true;
        }

        events = tmpEvents;
        stats = tmpStats;

        log.info(Mrk_JOD.JOD_EVENTS, "Initialized JODEvents instance");
        log.debug(Mrk_JOD.JOD_EVENTS, String.format("                                   Events buffered %d events on file %d", events.countBuffered(), events.countFile()));
        log.debug(Mrk_JOD.JOD_EVENTS, String.format("                                   Events stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));
    }


    private static void backup(File file) throws IOException {
        String datetime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backup = new File(file.getParentFile(), file.getName() + "_back_" + datetime);
        Files.copy(file.toPath(), backup.toPath());
    }


    @Override
    public void setJCPClient(JCPClient_Object jcpClient) {
        if (jcpClient==null) return;

        this.jcpClient = jcpClient;
        this.jcpEvents = new JCPEvents(jcpClient,locSettings);
    }

    @Override
    public void register(JOSPEvent.Type type, String phase, String payload) {
        register(type,phase,payload,null);
    }

    public void register(JOSPEvent.Type type, String phase, String payload, Throwable error) {
        synchronized (events) {
            long newId = events.count() + 1;
            String srcId = locSettings.getObjIdCloud();
            String errorPayload = null;
            if (error!=null)
                errorPayload = String.format("{\"type\": \"%s\", \"msg\": \"%s\", \"stack\": \"%s\"}", error.getClass().getSimpleName(), error.getMessage(), Arrays.toString(error.getStackTrace()));
            JOSPEvent e = new JOSPEvent(newId, type, srcId, JOSPEvent.SrcType.Obj, new Date(), phase, payload, errorPayload);
            events.append(e);
            stats.lastStored = e.id;
            stats.storeIgnoreExceptions();
        }

        if (isSyncing)
            sync();

        if (events.countBuffered()>=maxBuffered) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (events) {
                            int pre = events.countBuffered();
                            events.flushCache(reduceBuffer);
                            int post = events.countBuffered();
                            log.debug(Mrk_JOD.JOD_EVENTS, String.format("Flushed %d events to file", pre - post));
                            log.debug(Mrk_JOD.JOD_EVENTS, String.format("Events buffered %d events on file %d", events.countBuffered(), events.countFile()));
                        }

                    } catch (IOException ignore) { assert false; }
                }
            }).start();
        }
    }

    private void sync() {
        if (stats.lastUploaded == stats.lastStored) return;
        if (jcpClient==null || !jcpClient.isConnected()) return;

        List<JOSPEvent> toUpload;
        synchronized (events) {
            try {
                toUpload = events.getRange(stats.lastUploaded != -1 ? stats.lastUploaded : null, stats.lastStored);
                if (stats.lastUploaded != -1 && toUpload.size() > 1) toUpload.remove(0);

            } catch (IOException e) {
                e.printStackTrace();
                assert false;
                return;
            }

            if (toUpload.size() == 0) {
                log.debug(Mrk_JOD.JOD_EVENTS, String.format("No events found to uploads (CloudStats values lastUpd: %d; lastStored: %d", stats.lastUploaded, stats.lastStored));
                return;
            }

            log.debug(Mrk_JOD.JOD_EVENTS, String.format("Upload from %d to %d (%d events)", toUpload.get(0).id, toUpload.get(toUpload.size() - 1).id, toUpload.size()));
            for (JOSPEvent e : toUpload)
                log.trace(Mrk_JOD.JOD_EVENTS, String.format("- event[%d] %s", e.id, e.payload));

            try {
                jcpEvents.uploadEvents(toUpload);
            } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
                e.printStackTrace();
            }

            stats.uploaded += toUpload.size();
            stats.lastUploaded = toUpload.get(toUpload.size() - 1).id;
            stats.storeIgnoreExceptions();
        }
    }

    @Override
    public void startCloudSync() {
        isSyncing = true;
        log.info(Mrk_JOD.JOD_EVENTS, "Start events sync to cloud");
        log.debug(Mrk_JOD.JOD_EVENTS, String.format("Events buffered %d events on file %d", events.countBuffered(), events.countFile()));
        log.debug(Mrk_JOD.JOD_EVENTS, String.format("Events stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));

        sync();
    }

    @Override
    public void stopCloudSync() {
        synchronized (events) {
            isSyncing = false;
            try {
                int pre = events.countBuffered();
                events.storeCache();
                int post = events.countBuffered();
                log.debug(Mrk_JOD.JOD_EVENTS, String.format("Stored %d events to file", pre - post));

                log.info(Mrk_JOD.JOD_EVENTS, "Stop event sync to cloud");
                log.debug(Mrk_JOD.JOD_EVENTS, String.format("Events buffered %d events on file %d", events.countBuffered(), events.countFile()));
                log.debug(Mrk_JOD.JOD_EVENTS, String.format("Events stats lastStored: %d lastUploaded: %d", stats.lastStored, stats.lastUploaded));

            } catch (IOException ignore) {
                assert false;
            }
        }
    }

    @Override
    public void storeCache() throws IOException {
        events.storeCache();
    }

}
