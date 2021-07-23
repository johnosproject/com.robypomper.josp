/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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
 ******************************************************************************/

package com.robypomper.josp.jod.executor.impls.http;

import com.robypomper.josp.clients.HTTPClient;
import com.robypomper.josp.jod.executor.AbsJODPuller;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.log.Mrk_JOD;

import java.net.MalformedURLException;
import java.util.Map;


/**
 * JOD Puller test.
 */
public class PullerHTTP extends AbsJODPuller {

    // Class constants

    private static final String PROP_FREQ_SEC = "freq";                 // in seconds

    // Internal vars

    private final HTTPInternal http;
    private final FormatterInternal formatter;
    private final EvaluatorInternal evaluator;
    private final int freq_ms;
    private String lastResponse = "";
    private String lastResult = "";


    // Constructor

    /**
     * Default PullerHTTP constructor.
     *
     * @param name       name of the puller.
     * @param proto      proto of the puller.
     * @param configsStr configs string, can be an empty string.
     */
    public PullerHTTP(String name, String proto, String configsStr, JODComponent component) throws ParsingPropertyException, MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML for component '%s' init with config string '%s://%s'.", getName(), proto, configsStr));

        http = new HTTPInternal(this, name, proto, configsStr, component);
        formatter = new FormatterInternal(this, name, proto, configsStr, component);
        evaluator = new EvaluatorInternal(this, name, proto, configsStr, component);
        Map<String, String> configs = splitConfigsStrings(configsStr);
        freq_ms = parseConfigInt(configs, PROP_FREQ_SEC, Integer.toString(AbsJODPuller.DEF_POLLING_TIME / 1000)) * 1000;
    }

    protected long getPollingTime() {
        return freq_ms;
    }


    // Mngm

    /**
     * Pull method: print a log message and call the {@link JODState} sub
     * class's <code>setUpdate(...)</code> method.
     */
    @Override
    public void pull() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML '%s' of proto '%s' pulling", getName(), getProto()));

        String requestUrl = http.getStateRequest();

        String response;
        try {
            response = http.execRequest(requestUrl);

        } catch (HTTPClient.RequestException | MalformedURLException | HTTPClient.ResponseException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on exec request '%s' because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
            return;
        }
        if (lastResponse.compareTo(response) == 0) {
            log.info(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' get same response as last attempt, skip it", getName(), getProto()));
            return;
        }
        lastResponse = response;

        String result;
        try {
            result = formatter.parse(response);
        } catch (FormatterInternal.ParsingException | FormatterInternal.PathNotFoundException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on parsing request '%s''s response because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
            log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on parsing response '%s'", getName(), getProto(), response));
            return;
        }

        log.info(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' read state '%s'", getName(), getProto(), result));

        if (lastResult.compareTo(result) == 0){
            log.info(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' get same result as last attempt, skip it", getName(), getProto()));
            return;
        }
        lastResult = result;

        String resultEvaluated;
        try {
            resultEvaluated = evaluator.evaluate(result);
        } catch (EvaluatorInternal.EvaluationException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on evaluating request '%s''s result because '%s'", getName(), getProto(), requestUrl, e.getMessage()), e);
            log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTTP '%s' of proto '%s' error on evaluating result '%s'", getName(), getProto(), result));
            return;
        }

        log.debug(Mrk_JOD.JOD_EXEC_IMPL, String.format("PullerHTML '%s' of proto '%s' pulling url '%s' and get '%s' value from '%s' result", getName(), getProto(), http.getLastUrl(), resultEvaluated, result));

        // For each JODState supported
        if (getComponent() instanceof JODBooleanState)
            ((JODBooleanState) getComponent()).setUpdate(Boolean.parseBoolean(resultEvaluated));
        else if (getComponent() instanceof JODRangeState)
            ((JODRangeState) getComponent()).setUpdate(Double.parseDouble(resultEvaluated));
    }

}
