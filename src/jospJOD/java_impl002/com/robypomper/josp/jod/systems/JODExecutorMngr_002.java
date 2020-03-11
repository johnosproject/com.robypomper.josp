package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.JOD;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.structure.JODComponent;

import java.util.Collection;

/**
 * ToDo: doc JODExecutorMngr_002
 */
public class JODExecutorMngr_002 implements JODExecutorMngr {

    public JODExecutorMngr_002(JOD.Settings settings) {
        System.out.println("DEB: JODExecutorMngr_002()");
    }

    @Override
    public JODPuller initPuller(JODComponent component) {
        System.out.println("DEB: JODExecutorMngr_002::initPuller()");
        return null;
    }

    @Override
    public JODListener initListener(JODComponent component) {
        System.out.println("DEB: JODExecutorMngr_002::initListener()");
        return null;
    }

    @Override
    public JODExecutor initExecutor(JODComponent component) {
        System.out.println("DEB: JODExecutorMngr_002::initExecutor()");
        return null;
    }

    @Override
    public Collection<JODPuller> getPullers() {
        System.out.println("DEB: JODExecutorMngr_002::getPullerImpls()");
        return null;
    }

    @Override
    public Collection<JODListener> getListeners() {
        System.out.println("DEB: JODExecutorMngr_002::getListenerImpls()");
        return null;
    }

    @Override
    public Collection<JODExecutor> getExecutions() {
        System.out.println("DEB: JODExecutorMngr_002::getExecutionImpls()");
        return null;
    }

    @Override
    public void activateAll() {
        System.out.println("DEB: JODExecutorMngr_002::activateAll()");
    }

    @Override
    public void deactivateAll() {
        System.out.println("DEB: JODExecutorMngr_002::deactivateAll()");
    }

    @Override
    public void startPuller(JODPuller puller) {
        System.out.println("DEB: JODExecutorMngr_002::startPullers()");
    }

    @Override
    public void stopPuller(JODPuller puller) {
        System.out.println("DEB: JODExecutorMngr_002::stopPullers()");
    }

    @Override
    public void startAllPullers() {
        System.out.println("DEB: JODExecutorMngr_002::startAllPullers()");
    }

    @Override
    public void stopAllPullers() {
        System.out.println("DEB: JODExecutorMngr_002::stopAllPullers()");
    }

    @Override
    public void connectListener(JODListener listener) {
        System.out.println("DEB: JODExecutorMngr_002::connectListener()");
    }

    @Override
    public void disconnectListener(JODListener listener) {
        System.out.println("DEB: JODExecutorMngr_002::disconnectListener()");
    }

    @Override
    public void connectAllListeners() {
        System.out.println("DEB: JODExecutorMngr_002::connectAllListeners()");
    }

    @Override
    public void disconnectAllListeners() {
        System.out.println("DEB: JODExecutorMngr_002::disconnectAllListeners()");
    }

    @Override
    public void enableExecutor(JODExecutor executor) {
        System.out.println("DEB: JODExecutorMngr_002::enableExecutor()");
    }

    @Override
    public void disableExecutor(JODExecutor executor) {
        System.out.println("DEB: JODExecutorMngr_002::disableExecutor()");
    }

    @Override
    public void enableAllExecutors() {
        System.out.println("DEB: JODExecutorMngr_002::enableAllExecutors()");
    }

    @Override
    public void disableAllExecutors() {
        System.out.println("DEB: JODExecutorMngr_002::disableAllExecutors()");
    }
}
