package com.robypomper.java;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JavaFileWatcher {

    // Internal vars

    private static boolean watcherThreadMustShutdown = false;
    private static final Map<Path, List<JavaFileWatcherListener>> listenersMap = new HashMap<>();
    private static final Map<Path, WatchService> watcherMap = new HashMap<>();


    // Listeners mngm

    public static void addListener(Path filePath, JavaFileWatcherListener listener) throws IOException {
        filePath = filePath.toAbsolutePath();
        synchronized (listenersMap) {
            if (!listenersMap.containsKey(filePath)) {
                int oldSize = listenersMap.size();
                listenersMap.put(filePath, new ArrayList<>());
                watchFile(filePath);
                if (oldSize == 0)
                    startListen();
            }
            listenersMap.get(filePath).add(listener);
        }
    }

    public static void removeListener(Path filePath, JavaFileWatcherListener listener) throws IOException {
        filePath = filePath.toAbsolutePath();
        synchronized (listenersMap) {
            if (listenersMap.containsKey(filePath))
                listenersMap.get(filePath).remove(listener);
            if (listenersMap.get(filePath).isEmpty()) {
                listenersMap.remove(filePath);
                unWatchFile(filePath);
                if (listenersMap.isEmpty())
                    stopListen();
            }
        }
    }


    // Internal Watcher mngm

    private static void watchFile(Path filePath) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        filePath = filePath.toAbsolutePath();
        Path parentPath = filePath.toFile().isDirectory() ? filePath : filePath.getParent();
        parentPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        watcherMap.put(parentPath, watchService);
    }

    private static void unWatchFile(Path filePath) throws IOException {
        Path parentPath = filePath.toFile().isDirectory() ? filePath : filePath.getParent();
        //...
        WatchService watchService = watcherMap.remove(parentPath);
        if (watchService != null)
            watchService.close();
    }


    // Watcher startup

    private static void startListen() {
        watcherThreadMustShutdown = false;
        new Thread(() -> {
            Thread.currentThread().setName("FileWatcher");

            while (!watcherThreadMustShutdown) {
                for (Map.Entry<Path, WatchService> entry : watcherMap.entrySet()) {
                    Path fileDir = entry.getKey();
                    WatchService watchService = entry.getValue();
                    WatchKey key;
                    while ((key = watchService.poll()) != null && !watcherThreadMustShutdown) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path filePath = Paths.get(fileDir.toString(), event.context().toString());
                            System.out.println(String.format("FileWatched %s", filePath));
                            try {
                                List<JavaFileWatcherListener> fileListeners;
                                synchronized (listenersMap) {
                                    fileListeners = listenersMap.get(filePath);
                                }

                                if (fileListeners == null)
                                    continue;

                                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onCreate(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onDelete(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onUpdate(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        key.reset();
                    }

                    try {
                        //noinspection BusyWait
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        }).start();
    }

    private static void stopListen() {
        watcherThreadMustShutdown = true;
    }


    // File watcher interface

    public interface JavaFileWatcherListener {

        void onCreate(Path filePath);

        void onUpdate(Path filePath);

        void onDelete(Path filePath);

        void onAnyUpdate(Path filePath);

    }

}
