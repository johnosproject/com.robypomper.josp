package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFiles;
import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.test.mocks.jod.MockActionCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class ExecutorFilesTest {

    @Test
    public void executorTest() throws InterruptedException, IOException {
        String name = "executorTest";
        String proto = "files";
        String filePath = "listenerFilesTest.txt";
        String configs = "path=" + filePath;

        System.out.println("\nCREATE AND START EXECUTOR FOR FILES");
        ExecutorFiles e = new ExecutorFiles(name, proto, configs, null);
        JOSPProtocol.ActionCmd commandAction = new MockActionCmd();

        System.out.println("\nEXECUTE RANGE ACTION");
        JODBooleanAction.JOSPBoolean cmdActionBoolean = new JODBooleanAction.JOSPBoolean(String.format("new:%s\nold:%s", true, false));
        e.exec(commandAction, cmdActionBoolean);
        Thread.sleep(1000);
        String readFile = JavaFiles.readString(Paths.get(filePath));
        Assertions.assertTrue(JavaFormatter.strToBoolean(readFile));

        System.out.println("\nEXECUTE RANGE ACTION");
        JODRangeAction.JOSPRange cmdActionRange = new JODRangeAction.JOSPRange(String.format("new:%s\nold:%s", JavaFormatter.doubleToStr(5.33), JavaFormatter.doubleToStr(0.0)));
        e.exec(commandAction, cmdActionRange);
        Thread.sleep(1000);
        String readFile2 = JavaFiles.readString(Paths.get(filePath));
        Assertions.assertEquals(new Double(5.33), JavaFormatter.strToDouble(readFile2));

        Paths.get(filePath).toFile().delete();
    }

}
