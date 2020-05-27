package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import asg.cliche.Param;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.executor.factories.AbsFactoryJODWorker;
import com.robypomper.josp.jod.executor.factories.FactoryJODExecutor;
import com.robypomper.josp.jod.executor.factories.FactoryJODListener;
import com.robypomper.josp.jod.executor.factories.FactoryJODPuller;
import com.robypomper.josp.jod.structure.AbsJODAction;
import com.robypomper.josp.jod.structure.AbsJODState;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;

import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: this class works only with Puller, Listeners and Executors registered
 * via JOD Shell and ignore that once added by JOD Structure.
 */
public class CmdsJODExecutorMngr {

    private final JODExecutorMngr executorMngr;
    private final JODStructure structure;
    private final Map<String, JODPuller> pullers = new HashMap<>();
    private final Map<String, JODListener> listeners = new HashMap<>();
    private final Map<String, JODExecutor> executors = new HashMap<>();

    public CmdsJODExecutorMngr(JODStructure structure, JODExecutorMngr executor) {
        this.structure = structure;
        this.executorMngr = executor;
    }


    @Command(description = "Activate all Pullers, Listeners and Executors.")
    public String execActivate() {
        executorMngr.activateAll();
        return "Activated all Pullers, Listeners and Executors.";
    }

    @Command(description = "Deactivate all Pullers, Listeners and Executors.")
    public String execDeactivate() {
        executorMngr.deactivateAll();
        return "Deactivated all Pullers, Listeners and Executors.";
    }


    // ls protos > Puller/Listener/Executor

    @Command(description = "List JOD Puller protocols")
    public String execLsPullerProtos() {
        return execLs_Worker_Protos(FactoryJODPuller.instance());
    }

    @Command(description = "List JOD Listener protocols")
    public String execLsListenerProtos() {
        return execLs_Worker_Protos(FactoryJODListener.instance());
    }

    @Command(description = "List JOD Executor protocols")
    public String execLsExecutorProtos() {
        return execLs_Worker_Protos(FactoryJODExecutor.instance());
    }

    public String execLs_Worker_Protos(AbsFactoryJODWorker factory) {
        StringBuilder retProtosString = new StringBuilder("PROTO      | CLASS\n");
        retProtosString.append("-----------+---------------\n");
        Map<String, Class<? extends AbsJODWorker>> protos = factory.getProtocols();
        if (protos.isEmpty())
            return String.format("No %s protocols registered.", factory.getType());
        for (Map.Entry<String, Class<? extends AbsJODWorker>> p : protos.entrySet())
            retProtosString.append(String.format("%-10s | %s\n", p.getKey(), p.getValue()));
        return retProtosString.substring(0, retProtosString.length() - 1);
    }


    // ls > Puller/Listener/Executor

    @Command(description = "List added JOD Pullers.")
    public String execLsPullers() {
        return execLs_Worker(pullers);
    }

    @Command(description = "List added JOD Listeners.")
    public String execLsListeners() {
        return execLs_Worker(listeners);
    }

    @Command(description = "List added JOD Pullers.")
    public String execLsExecutors() {
        return execLs_Worker(executors);
    }

    public String execLs_Worker(Map<String, ? extends JODWorker> workers) {
        StringBuilder retProtosString = new StringBuilder("WORKER     | PROTO      | ENABLED\n");
        retProtosString.append("-----------+------------+---------\n");
        if (workers.isEmpty())
            return "No Workers (Pullers/Listeners/Executors) added.";
        for (Map.Entry<String, ? extends JODWorker> p : workers.entrySet())
            retProtosString.append(String.format("%-10s | %-10s | %s\n", p.getKey(), p.getValue().getProto(), p.getValue().isEnabled()));
        return retProtosString.substring(0, retProtosString.length() - 1);
    }


    // add > Puller/Listener/Executor

    @Command(description = "Add JOD Puller.")
    public String execAddPuller(@Param(name = "name", description = "Name of the puller to create") String name,
                                @Param(name = "proto", description = "Protocol of the puller to create") String proto,
                                @Param(name = "configStr", description = "String containing puller configs with format 'k1=v1[;k2=v2][...]") String conf_str) throws JODStructure.ComponentInitException {
        JODComponentPuller compPuller = new JODComponentPuller(new AbsJODState(structure, executorMngr, "comp-" + name, "com's description", null, proto + AbsJODWorker.CONFIG_STR_SEP + conf_str), name, proto, conf_str);

        try {
            JODPuller puller = executorMngr.initPuller(compPuller);
            pullers.put(name, puller);
            return String.format("JOD Puller '%s' create successfully.", name);

        } catch (JODExecutor.FactoryException e) {

            return String.format("Error on JOD Puller '%s' creation: '%s'.", name, e.getMessage());
        }
    }

    @Command(description = "Add JOD Listener.")
    public String execAddListener(@Param(name = "name", description = "Name of the listener to create") String name,
                                  @Param(name = "proto", description = "Protocol of the listener to create") String proto,
                                  @Param(name = "configStr", description = "String containing listener configs with format 'k1=v1[;k2=v2][...]") String conf_str) throws JODStructure.ComponentInitException {
        JODComponentListener compListener = new JODComponentListener(new AbsJODState(structure, executorMngr, "comp-" + name, "com's description", proto + AbsJODWorker.CONFIG_STR_SEP + conf_str, null), name, proto, conf_str);

        try {
            JODListener listener = executorMngr.initListener(compListener);
            listeners.put(name, listener);
            return String.format("JOD Listener '%s' create successfully.", name);

        } catch (JODExecutor.FactoryException e) {
            return String.format("Error on JOD Listener '%s' creation: '%s'.", name, e.getMessage());
        }
    }

    @Command(description = "Add JOD Executor.")
    public String execAddExecutor(@Param(name = "name", description = "Name of the executor to create") String name,
                                  @Param(name = "proto", description = "Protocol of the executor to create") String proto,
                                  @Param(name = "configStr", description = "String containing executor configs with format 'k1=v1[;k2=v2][...]") String conf_str) throws JODStructure.ComponentInitException {
        JODComponentExecutor compExecutor = new JODComponentExecutor(new AbsJODAction(structure, executorMngr, "comp-" + name, "com's description", null, null, proto + AbsJODWorker.CONFIG_STR_SEP + conf_str), name, proto, conf_str);

        try {
            JODExecutor executor = executorMngr.initExecutor(compExecutor);
            executors.put(name, executor);
            return String.format("JOD Executor '%s' create successfully.", name);

        } catch (JODExecutor.FactoryException e) {

            return String.format("Error on JOD Executor '%s' creation: '%s'.", name, e.getMessage());
        }
    }


    // rm > Puller/Listener/Executor

    @Command(description = "Remove JOD Puller.")
    public String execRmPuller(@Param(name = "name", description = "Name of the puller to remove") String name) {
        if (pullers.get(name).isEnabled())
            pullers.get(name).stopTimer();

        pullers.remove(name);
        return String.format("JOD Puller '%s' removed successfully.", name);
    }

    @Command(description = "Remove JOD Listener.")
    public String execRmListener(@Param(name = "name", description = "Name of the listener to remove") String name) {
        if (listeners.get(name).isEnabled())
            listeners.get(name).halt();

        listeners.remove(name);
        return String.format("JOD Listener '%s' removed successfully.", name);
    }

    @Command(description = "Remove JOD Executor.")
    public String execRmExecutor(@Param(name = "name", description = "Name of the executor to remove") String name) {
        if (executors.get(name).isEnabled())
            executors.get(name).disable();

        executors.remove(name);
        return String.format("JOD Executor '%s' removed successfully.", name);
    }

    // start > Puller/Listener/Executor

    @Command(description = "Start JOD Puller.")
    public String execEnablePuller(@Param(name = "name", description = "Name of the puller to start") String name) {
        if (!pullers.containsKey(name))
            return String.format("No Puller with '%s' name.", name);

        pullers.get(name).startTimer();
        return String.format("Puller '%s' started timer successfully.", name);
    }

    @Command(description = "Start JOD Listener.")
    public String execEnableListener(@Param(name = "name", description = "Name of the listener to start") String name) {
        if (!listeners.containsKey(name))
            return String.format("No Listener with '%s' name.", name);

        listeners.get(name).listen();
        return String.format("Listener '%s' server started successfully.", name);
    }

    @Command(description = "Enable JOD Executor.")
    public String execEnableExecutor(@Param(name = "name", description = "Name of the executor to start") String name) {
        if (!executors.containsKey(name))
            return String.format("No Executor with '%s' name.", name);

        executors.get(name).enable();
        return String.format("Executor '%s' enabled successfully.", name);
    }


    // stop > Puller/Listener/Executor

    @Command(description = "Stop JOD Puller.")
    public String execDisablePuller(@Param(name = "name", description = "Name of the puller to stop") String name) {
        if (!pullers.containsKey(name))
            return String.format("No Puller with '%s' name.", name);

        pullers.get(name).stopTimer();
        return String.format("Puller '%s' stopped timer successfully.", name);
    }

    @Command(description = "Halt JOD Listener.")
    public String execDisableListener(@Param(name = "name", description = "Name of the listener to stop") String name) {
        if (!listeners.containsKey(name))
            return String.format("No Listener with '%s' name.", name);

        listeners.get(name).halt();
        return String.format("Listener '%s' server halted successfully.", name);
    }

    @Command(description = "Disable JOD Executor.")
    public String execDisableExecutor(@Param(name = "name", description = "Name of the executor to stop") String name) {
        if (!executors.containsKey(name))
            return String.format("No Executor with '%s' name.", name);

        executors.get(name).disable();
        return String.format("Executor '%s' disabled successfully.", name);
    }


    // exec > Executor

    @Command(description = "Execute JOD Executor action.")
    public String execActionExecutor(@Param(name = "name", description = "Name of the executor to exec") String name) {
        if (!executors.containsKey(name))
            return String.format("No Executor with '%s' name.", name);

        if (executors.get(name).exec())
            return String.format("Executor '%s' exec successfully.", name);
        else
            return String.format("Executor '%s' not exec because disabled.", name);
    }

}
