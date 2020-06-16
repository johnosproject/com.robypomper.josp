package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;

import java.util.HashMap;
import java.util.Map;


/**
 * Default basic implementation class for JOD executor's worker representations.
 */
public abstract class AbsJODWorker implements JODWorker {

    // Class constants

    public static final String CONFIG_STR_SEP = "://";


    // Internal vars

    private final String proto;
    private final String name;
    private JODComponent component;


    // Constructor

    /**
     * Default constructor.
     *
     * @param name  the JODWorker's name.
     * @param proto the JODWorker's protocol.
     */
    public AbsJODWorker(String name, String proto) {
        this.proto = proto;
        this.name = name;
    }


    // Setter

    /**
     * Set the {@link JODComponent} associated to the current JODWorker.
     * <p>
     * The owner component must be set via this method, and not via constructor,
     * because the implementation classes must not access to JODComponent
     * directly.
     * <p>
     * NB: the component owner can be set only once.
     *
     * @param component the owner {@link JODComponent}.
     */
    public void setComponent(JODComponent component) {
        if (this.component != null)
            throw new IllegalStateException(String.format("JOD Worker for component '%s' already have a JOD Component owner.", getName()));
        this.component = component;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProto() {
        return proto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODComponent getComponent() {
        return component;
    }


    // Full config string mngm

    /**
     * Return the protocol part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the protocol defined in given full configs string.
     */
    public static String extractProto(String fullConfigs) {
        return fullConfigs.substring(0, fullConfigs.indexOf(CONFIG_STR_SEP)).trim();
    }

    /**
     * Return the configs/name part of the worker fullConfigs string.
     *
     * @param fullConfigs the worker full configs string.
     * @return the configs/name defined in given full configs string.
     */
    public static String extractConfigsStr(String fullConfigs) {
        return fullConfigs.substring(fullConfigs.indexOf(CONFIG_STR_SEP) + 3).trim();
    }

    /**
     * Compose the two part of the full configs string.
     *
     * @param proto  the string used as protocol part of the full configs string.
     * @param config the string used as congis/name part of the full configs string.
     * @return the composed full configs string.
     */
    public static String mergeConfigsStr(String proto, String config) {
        return proto + CONFIG_STR_SEP + config;
    }

    public static Map<String, String> splitConfigsStrings(String config) {
        Map<String, String> configMap = new HashMap<>();

        for (String keyAndProp : config.split(";")) {

            // Boolean props
            if (keyAndProp.indexOf('=') < 0) {
                configMap.put(keyAndProp, "true");
                continue;
            }

            // Key = Value properties
            String key = keyAndProp.substring(0, keyAndProp.indexOf('='));
            String value = keyAndProp.substring(keyAndProp.indexOf('=') + 1);
            configMap.put(key, value);
        }

        return configMap;
    }

    public int parseConfigInt(Map<String, String> configMap, String key, Integer defValue) throws MissingPropertyException, ParsingPropertyException {
        String value = configMap.get(key);

        if (value == null && defValue == null)
            throw new MissingPropertyException(key, getProto(), getName(), "Listener");

        if (value == null)
            return defValue;

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new ParsingPropertyException(key, getProto(), getName(), "Listener", value, e);
        }
    }

}
