package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;

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
    private final JODComponent component;


    // Constructor

    /**
     * Default constructor.
     *
     * @param name  the JODWorker's name.
     * @param proto the JODWorker's protocol.
     */
    public AbsJODWorker(String name, String proto, JODComponent component) {
        this.proto = proto;
        this.name = name;
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


    // Status convert & set methods

    protected boolean convertAndSetStatus(String newStatus) {
        if (getComponent() instanceof JODBooleanState) {
            ((JODBooleanState) getComponent()).setUpdate(JavaFormatter.strToBoolean(newStatus.toUpperCase()));
            return true;
        }

        if (getComponent() instanceof JODRangeState) {
            Double val = JavaFormatter.strToDouble(newStatus);
            if (val != null)
                ((JODRangeState) getComponent()).setUpdate(val);
            return true;
        }

        return false;
    }


    // Substitution methods

    protected static String genericSubstitution(String str, JODComponent component) {
        if (component == null
                || str == null
                || str.isEmpty()
        ) return str;

        return str
                .replaceAll(Substitutions.COMP_NAME, component.getName());
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
