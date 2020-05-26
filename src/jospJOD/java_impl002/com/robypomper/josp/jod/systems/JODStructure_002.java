package com.robypomper.josp.jod.systems;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.jod.structure.DefaultJODComponentPath;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODRoot;
import com.robypomper.josp.jod.structure.JODRoot_Jackson;

import java.util.Date;

/**
 * ToDo: doc JODStructure_002
 */
public class JODStructure_002 implements JODStructure {

    // Internal vars

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
        System.out.println("DEB: JOD Structure initialization...");

        this.objInfo = objInfo;
        this.executorMngr = executorMngr;

        root = loadStructure(objInfo.getStructureStr());

        System.out.println("DEB: JOD Structure initialized");
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
            throw new ParsingException(String.format("Can't get JOD Structure string, error on parsing JSON: '%s'.", e.getMessage().substring(0, e.getMessage().indexOf('\n'))), e, e.getLocation().getLineNr(), e.getLocation().getColumnNr());
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
        System.out.println("WAR: JOD Structure AutoRefresh can't started:");
        System.out.println("     AutoRefresh not implemented, structure are static");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        System.out.println("WAR: JOD Structure AutoRefresh can't stopped:");
        System.out.println("     AutoRefresh not implemented, structure are static");
    }


    // Implementation methods

    /**
     * Load object's structure from data file.
     */
    private JODRoot loadStructure(String structureStr) throws ParsingException {
        try {
            ObjectMapper objMapper = new ObjectMapper();

            InjectableValues.Std injectVars = new InjectableValues.Std();
            injectVars.addValue(JODStructure.class, this);
            injectVars.addValue(JODExecutorMngr.class, executorMngr);
            objMapper.setInjectableValues(injectVars);

            return objMapper.readerFor(JODRoot_Jackson.class).readValue(structureStr);

        } catch (JsonProcessingException e) {
            throw new ParsingException(String.format("Can't init JOD Structure, error on parsing JSON: '%s'.", e.getMessage().substring(0, e.getMessage().indexOf('\n'))), e, e.getLocation().getLineNr(), e.getLocation().getColumnNr());
        }
    }

}
