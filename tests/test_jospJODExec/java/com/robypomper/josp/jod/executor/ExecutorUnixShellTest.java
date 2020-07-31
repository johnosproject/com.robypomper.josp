package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.test.mocks.jod.MockActionCmd;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ExecutorUnixShellTest {

    @Test
    public void executorTest() throws InterruptedException {
        String name = "executorTest";
        String proto = "shell";
        String echoParam = String.format("'read %s value at %s'", Substitutions.ACTION_VAL, new Date());
        String configs = String.format("cmd=echo %s", echoParam);

        System.out.println("\nCREATE AND START LISTENER FOR FILES");
        ExecutorUnixShell e = new ExecutorUnixShell(name, proto, configs, null);
        JOSPProtocol.ActionCmd commandAction = new MockActionCmd();

        System.out.println("\nEXECUTE RANGE ACTION");
        JODBooleanAction.JOSPBoolean cmdActionBoolean = new JODBooleanAction.JOSPBoolean(String.format("new:%s\nold:%s", true, false));
        e.exec(commandAction, cmdActionBoolean);
        Thread.sleep(1000);
        //String readFile = JavaFiles.readString(Paths.get(filePath));
        //Assertions.assertTrue(JavaFormatter.strToBoolean(readFile));

        System.out.println("\nEXECUTE RANGE ACTION");
        JODRangeAction.JOSPRange cmdActionRange = new JODRangeAction.JOSPRange(String.format("new:%s\nold:%s", JavaFormatter.doubleToStr(5.33), JavaFormatter.doubleToStr(0.0)));
        e.exec(commandAction, cmdActionRange);
        Thread.sleep(1000);
        //String readFile2 = JavaFiles.readString(Paths.get(filePath));
        //Assertions.assertEquals(new Double(5.33), JavaFormatter.strToDouble(readFile2));
    }

}
