package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.JSLRoot_Jackson;
import com.robypomper.josp.protocol.JOSPPermissions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JOSPStructIntegration {

    @Test
    public void integrationTest() throws IOException {
        // File containing jod structure in json
        String resourceName = "struct.jod";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

        System.out.println("\nDESERIALIZE STRUCT.JOD");
        // Support objects
        JODStructure structure = new MockupJODStructure();
        JODExecutorMngr executorMngr = new MockupJODExecutorManager();

        JODRoot_Jackson jodRoot;
        // From: JODRoot JODStructure_002::loadStructure(String)
        try {
            ObjectMapper objMapper = new ObjectMapper();
            InjectableValues.Std injectVars = new InjectableValues.Std();
            injectVars.addValue(JODStructure.class, structure);
            injectVars.addValue(JODExecutorMngr.class, executorMngr);
            objMapper.setInjectableValues(injectVars);

            jodRoot = objMapper.readerFor(JODRoot_Jackson.class).readValue(file);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("JOD struct");
        printJODRoot(jodRoot);
        System.out.println("JOD paths");
        printJODRootPaths(jodRoot);


        System.out.println("\nSERIALIZE STRUCT.JSL");
        String jodStructureStr;
        // From: String JODStructure_002::getStructForJSL()
        try {
            ObjectMapper mapper = new ObjectMapper();
            jodStructureStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jodRoot);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println(jodStructureStr);

        System.out.println("\nDESERIALIZE STRUCT.JSL");
        // Support objects
        JSLRemoteObject remoteObject = new MockupJSLRemoteObject();

        ObjectMapper mapper = new ObjectMapper();
        InjectableValues.Std injectVars = new InjectableValues.Std();
        injectVars.addValue(JSLRemoteObject.class, remoteObject);
        mapper.setInjectableValues(injectVars);
        JSLRoot jslRoot = mapper.readValue(jodStructureStr, JSLRoot_Jackson.class);

        System.out.println("JSL struct");
        printJSLRoot(jslRoot);
        System.out.println("JSL paths");
        printJSLRootPaths(jslRoot);

    }


    private void printJODRoot(JODRoot_Jackson root) {
        printJODCompRecursively(root, 0);
    }

    private void printJODCompRecursively(JODComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        System.out.printf("%s- %s%n", indentStr, comp.getName());

        if (comp instanceof JODContainer)
            for (JODComponent subComp : ((JODContainer) comp).getComponents())
                printJODCompRecursively(subComp, indent + 2);
    }

    private void printJODRootPaths(JODRoot root) {
        printJODCompRecursivelyPaths(root);
    }

    private void printJODCompRecursivelyPaths(JODComponent comp) {
        System.out.printf("- %s%n", comp.getPath().getString());

        if (comp instanceof JODContainer)
            for (JODComponent subComp : ((JODContainer) comp).getComponents())
                printJODCompRecursivelyPaths(subComp);
    }

    private void printJSLRoot(JSLRoot root) {
        printJSLCompRecursively(root, 0);
    }

    private void printJSLCompRecursively(JSLComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        System.out.printf("%s- %s%n", indentStr, comp.getName());

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printJSLCompRecursively(subComp, indent + 2);
    }

    private void printJSLRootPaths(JSLRoot root) {
        printJSLCompRecursivelyPaths(root);
    }

    private void printJSLCompRecursivelyPaths(JSLComponent comp) {
        System.out.printf("- %s%n", comp.getPath().getString());

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printJSLCompRecursivelyPaths(subComp);
    }


    private static class MockupJODStructure implements JODStructure {
        @Override
        public JODRoot getRoot() {
            return null;
        }

        @Override
        public JODComponent getComponent(String pathStr) {
            return null;
        }

        @Override
        public JODComponent getComponent(JODComponentPath path) {
            return null;
        }

        @Override
        public JODCommunication getCommunication() {
            return null;
        }

        @Override
        public void setCommunication(JODCommunication comm) {

        }

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }

        @Override
        public void syncObjStruct() {

        }

        @Override
        public String getStructForJSL() {
            return null;
        }

        @Override
        public Date getLastStructureUpdate() {
            return null;
        }
    }

    private static class MockupJODExecutorManager implements JODExecutorMngr {

        @Override
        public JODPuller initPuller(JODComponentPuller component) {
            return null;
        }

        @Override
        public JODListener initListener(JODComponentListener component) {
            return null;
        }

        @Override
        public JODExecutor initExecutor(JODComponentExecutor component) {
            return null;
        }

        @Override
        public Collection<JODPuller> getPullers() {
            return null;
        }

        @Override
        public Collection<JODListener> getListeners() {
            return null;
        }

        @Override
        public Collection<JODExecutor> getExecutors() {
            return null;
        }

        @Override
        public void activateAll() {

        }

        @Override
        public void deactivateAll() {

        }

        @Override
        public void startPuller(JODComponent component) {

        }

        @Override
        public void stopPuller(JODComponent component) {

        }

        @Override
        public void startAllPullers() {

        }

        @Override
        public void stopAllPullers() {

        }

        @Override
        public void connectListener(JODComponent component) {

        }

        @Override
        public void disconnectListener(JODComponent component) {

        }

        @Override
        public void connectAllListeners() {

        }

        @Override
        public void disconnectAllListeners() {

        }

        @Override
        public void enableExecutor(JODComponent component) {

        }

        @Override
        public void disableExecutor(JODComponent component) {

        }

        @Override
        public void enableAllExecutors() {

        }

        @Override
        public void disableAllExecutors() {

        }

    }

    private static class MockupJSLRemoteObject implements JSLRemoteObject {
        @Override
        public boolean isLocalConnected() {
            return false;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String newName) throws ObjectNotConnected {}

        @Override
        public JSLRoot getStructure() {
            return null;
        }

        @Override
        public String getOwnerId() {
            return null;
        }

        @Override
        public void setOwnerId(String newOwnerId) throws ObjectNotConnected {}

        @Override
        public String getJODVersion() {
            return null;
        }

        @Override
        public String getModel() {
            return null;
        }

        @Override
        public String getBrand() {
            return null;
        }

        @Override
        public String getLongDescr() {
            return null;
        }

        @Override
        public JSLCommunication getCommunication() {
            return null;
        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public boolean isCloudConnected() {
            return false;
        }

        @Override
        public List<JSLLocalClient> getLocalClients() {
            return null;
        }

        @Override
        public JSLLocalClient getConnectedLocalClient() {
            return null;
        }

        @Override
        public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected {

        }

        @Override
        public void addLocalClient(JSLLocalClient localClient) {

        }

        @Override
        public void replaceLocalClient(JSLLocalClient oldConnection, JSLLocalClient newConnection) {

        }


        @Override
        public boolean processFromObjectMsg(String msg, JOSPPermissions.Connection connType) {
            return false;
        }
    }

}
