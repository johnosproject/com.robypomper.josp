package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ListenerFilesTest {

    @Test
    public void listenerTest() throws InterruptedException, IOException {
        String name = "listenerTest";
        String proto = "files";
        String filePath = "listenerFilesTest.txt";
        String configs = "path=" + filePath;
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("\nCREATE AND START LISTENER FOR FILES");
        ListenerFiles l = new ListenerFiles(name, proto, configs, null) {
            @Override
            protected boolean convertAndSetStatus(String newStatus) {
                System.out.println("Status received: " + newStatus);
                latch.countDown();
                return true;
            }
        };
        l.listen();

        Thread.sleep(1000);

        System.out.println("\nUPDATE FILE");
        JavaFiles.writeString(filePath, "new state value " + new Date());
        Assertions.assertTrue(latch.await(15, TimeUnit.SECONDS));

        System.out.println("\nSTOP LISTENER FOR FILES");
        l.halt();

        Paths.get(filePath).toFile().delete();
    }

}
