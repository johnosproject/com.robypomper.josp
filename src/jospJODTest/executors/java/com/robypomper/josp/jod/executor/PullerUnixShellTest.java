package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PullerUnixShellTest {

    @Test
    public void pullerTest() throws InterruptedException {
        String echoParam = "new state value " + new Date();
        String configs = String.format("cmd=echo '%s'", echoParam);
        CountDownLatch latch = new CountDownLatch(1);


        System.out.println("\nCREATE AND START PULLER FOR UNIX SHELL");
        PullerUnixShell l = execCommand(configs, latch);

        System.out.println("\nWAIT UNIX SHELL EXECUTION");
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));

        System.out.println("\nSTOP PULLER FOR UNIX SHELL");
        l.stopTimer();
    }

    @Test
    public void pullerTestViaFile() throws InterruptedException, IOException {
        String filePath = "pullerUnixShellTest.txt";
        String configs = "cmd=cat " + filePath;
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("\nCREATE AND START PULLER FOR UNIX SHELL");
        PullerUnixShell l = execCommand(configs, latch);

        System.out.println("\nUPDATE FILE");
        JavaFiles.writeString(filePath, "new state value " + new Date());
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));

        System.out.println("\nSTOP PULLER FOR UNIX SHELL");
        l.stopTimer();

        Paths.get(filePath).toFile().delete();
    }


    public PullerUnixShell execCommand(String configs, CountDownLatch latch) {
        String name = "pullerTest";
        String proto = "shell";

        PullerUnixShell l = new PullerUnixShell(name, proto, configs, null) {
            @Override
            protected boolean convertAndSetStatus(String newStatus) {
                System.out.println("Status received: " + newStatus);
                latch.countDown();
                return true;
            }
        };
        l.startTimer();

        return l;
    }

}