package com.robypomper.josp.jod.executor.impls.http;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.structure.JODComponent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class EvaluatorInternal {

    // Class constants

    //@formatter:off
    private static final String SCRIPT_VAR_HTTP_RESULT  = "httpResult";

    private static final String PROP_EVAL_SCRIPT                = "eval";
    private static final String PROP_EVAL_SCRIPT_DEF            = "{" + SCRIPT_VAR_HTTP_RESULT + "}";
    //@formatter:onn

    // Internal vars
    private final AbsJODWorker worker;
    private final String name;
    private final String proto;
    private final String configsStr;
    private final JODComponent component;
    private final String evalScript;
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    ;


    // Constructor

    public EvaluatorInternal(AbsJODWorker worker, String name, String proto, String configsStr, JODComponent component) {
        this.worker = worker;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
        this.component = component;

        // Parse configs
        Map<String, String> configs = AbsJODWorker.splitConfigsStrings(configsStr);
        evalScript = worker.parseConfigString(configs, PROP_EVAL_SCRIPT, PROP_EVAL_SCRIPT_DEF);
    }

    public String evaluate(String result) throws EvaluationException {
        String evalResult;
        synchronized (engine) {
            engine.put(SCRIPT_VAR_HTTP_RESULT, result);
            try {
                evalResult = engine.eval(evalScript).toString();

            } catch (ScriptException e) {
                throw new EvaluationException(e);
            }

            engine.put(SCRIPT_VAR_HTTP_RESULT, null);
        }
        return evalResult;
    }


    // Exceptions

    public static class EvaluationException extends Throwable {

        public EvaluationException(Throwable cause) {
            super(cause);
        }

    }

}
