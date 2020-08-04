package com.robypomper.josp.jod.structure;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.robypomper.josp.jod.executor.JODExecutorMngr;

import java.util.Map;


/**
 * JSON JODRoot's implementation.
 * <p>
 * This class allow to load an object structure from JSON string. To manage JSON
 * it use the Jackson library.
 * <p>
 * Basically it parse the root element with {@link JODRoot} type and the for any
 * contains
 */
public class JODRoot_Jackson extends AbsJODRoot {

    // Class constants

    public static final String CONTAINS = "contains";


    // Constructor

    /**
     * Default constructor for Jackson parsing.
     * <p>
     * This constructor allow Jackson params injection to be used with
     * {@link com.fasterxml.jackson.databind.ObjectReader#readValue(String)}
     * method. Example:
     *
     * <code>
     * ObjectMapper objMapper = new ObjectMapper();
     *
     * InjectableValues.Std injectVars = new InjectableValues.Std();
     * injectVars.addValue(JODStructure.class, this);
     * injectVars.addValue(JODExecutorMngr.class, executorMngr);
     * objMapper.setInjectableValues(injectVars);
     *
     * return objMapper.readerFor(JODRoot_Jackson.class).readValue(structureStr);
     * </code>
     * <p>
     * This constructor is used by the {@link JODStructure_002#loadStructure(String)}
     * method.
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     */
    @SuppressWarnings("JavadocReference")
    public JODRoot_Jackson(@JacksonInject final JODStructure structure, @JacksonInject final JODExecutorMngr execMngr) {
        super(structure, execMngr);
    }


    // Jackson properties

    /**
     * This method act as json property {@value #CONTAINS}.
     * <p>
     * It set current root's sub components and start the structure
     * initialization chain for each sub component added.
     *
     * @param containsMap the map containing the pairs name/comp's properties.
     */
    @JsonProperty(CONTAINS)
    public void setContains(Map<String, Object> containsMap) throws JODStructure.ParsingException {
        try {
            setComponents(createFromContains(getName(), containsMap));
        } catch (JODStructure.ComponentInitException e) {
            assert false;   // This is an implementation error
        }
    }

}
