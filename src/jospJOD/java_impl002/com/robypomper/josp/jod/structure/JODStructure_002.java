package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * ToDo: doc JODStructure_002
 */
public class JODStructure_002 implements JODStructure {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODObjectInfo objInfo;
    private final JODExecutorMngr executorMngr;
    private final JODRoot root;
    private JODCommunication comm;


    // Constructor

    /**
     * Create new object's structure.
     * <p>
     * Set Object Info and Executor Manager references then load and initialize
     * the object structure.
     *
     * @param objInfo      the Object Info system.
     * @param executorMngr the Executor Manager system.
     */
    public JODStructure_002(JODObjectInfo objInfo, JODExecutorMngr executorMngr) throws ParsingException {
        this.objInfo = objInfo;
        this.executorMngr = executorMngr;

        root = loadStructure(objInfo.readStructureStr());

        log.info(Mrk_JOD.JOD_STRU, String.format("Initialized JODStructure instance for '%s' object from '%s' file", objInfo.getObjName(), objInfo.getStructurePath()));
        logStructure(root, 34, 64);
    }

    private static void logStructure(JODComponent component, int level, int space) {
        String indentStr = "> ";
        if (level > 0) {
            String indentFormat = "%" + (level + 2) + "s";
            indentStr = String.format(indentFormat, "> ");
        }
        log.debug(Mrk_JOD.JOD_STRU, String.format("%-" + space + "s (%s)", indentStr + component.getName(), component.getType()));

        if (component instanceof JODContainer) {
            for (JODComponent subComp : ((JODContainer) component).getComponents())
                logStructure(subComp, level + 2, space);
        }
    }


    // JOD Component's interaction methods (from communication)

    /**
     * {@inheritDoc}
     */
    @Override
    public JODRoot getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponent(String pathStr) {
        return getComponent(new DefaultJODComponentPath(pathStr));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponent(JODComponentPath path) {
        if (!path.isUnique())
            throw new IllegalArgumentException("Can't use not unique path in getComponent() method.");
        return DefaultJODComponentPath.searchComponent(getRoot(), path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringForJSL() throws ParsingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(getRoot());

        } catch (JsonProcessingException e) {
            throw new ParsingException(String.format("Can't get JOD Structure string, error on parsing JSON: '%s'.", e.getMessage().substring(0, e.getMessage().indexOf('\n'))), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastStructureUpdate() {
        return new Date(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JODCommunication comm) throws CommunicationSetException {
        if (this.comm != null)
            throw new CommunicationSetException();
        this.comm = comm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODCommunication getCommunication() throws CommunicationSetException {
        if (this.comm == null)
            throw new CommunicationSetException();
        return this.comm;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        log.warn(Mrk_JOD.JOD_STRU, "JODStructure AutoRefresh can't started: not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        log.warn(Mrk_JOD.JOD_STRU, "JODStructure AutoRefresh can't stopped: not implemented");
    }


    // Implementation methods

    /**
     * Load object's structure from data file.
     */
    private JODRoot loadStructure(String structureStr) throws ParsingException {
        log.debug(Mrk_JOD.JOD_STRU, "Loading object structure");

        JODRoot root;
        try {
            ObjectMapper objMapper = new ObjectMapper();

            InjectableValues.Std injectVars = new InjectableValues.Std();
            injectVars.addValue(JODStructure.class, this);
            injectVars.addValue(JODExecutorMngr.class, executorMngr);
            objMapper.setInjectableValues(injectVars);

            log.trace(Mrk_JOD.JOD_STRU, String.format("Parsing '%s' object structure '%s...'", objInfo.getObjId(), structureStr.substring(0, 100).replace("\n", " ")));
            root = objMapper.readerFor(JODRoot_Jackson.class).readValue(structureStr);

        } catch (JsonProcessingException e) {
            String eMessage = e.getMessage().indexOf('\n') == -1 ? e.getMessage() : e.getMessage().substring(0, e.getMessage().indexOf('\n'));
            log.warn(Mrk_JOD.JOD_STRU, String.format("Error on loading object structure because %s", eMessage), e);
            throw new ParsingException("Error on parsing JOD Structure", e);
        }

        log.debug(Mrk_JOD.JOD_STRU, "Object structure loaded");
        return root;
    }

}
