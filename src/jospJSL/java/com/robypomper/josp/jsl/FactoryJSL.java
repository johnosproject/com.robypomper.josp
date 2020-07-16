package com.robypomper.josp.jsl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

/**
 * JSL Library factory.
 * <p>
 * This class allow initialize JSL library specifying his settings and JSL version.
 * <p>
 * JSL and JSL.Settings versions must match.
 * <p>
 * String versions are resolved with {@link JSL.Settings} implementation class
 * or a {@link AbsJSL} sub-class by {@link #getJSLSettingsClass(String)} and
 * {@link #getJSLSettingsClass(String)}.
 */
public class FactoryJSL {

    // Version constants

    public static final String JSL_VER_002 = "0.0.2";
    private static final Class<? extends AbsJSL> JSL_VER_002_CLASS = JSL_002.class;
    private static final Class<? extends JSL.Settings> JSL_VER_002_CONFIG_CLASS = JSLSettings_002.class;
    public static final String JSL_VER_2_0_0 = "2.0.0";
    private static final Class<? extends AbsJSL> JSL_VER_2_0_0_CLASS = JSL_002.class;
    private static final Class<? extends JSL.Settings> JSL_VER_2_0_0_CONFIG_CLASS = JSLSettings_002.class;

    public static final String JSL_VER_LATEST = JSL_VER_2_0_0;


    // New instance method name

    private static final String NEW_INSTANCE_METHOD = "instance";


    // Factory methods

    /**
     * Create new JSL Settings object from given <code>fileName</code>.
     *
     * @param fileName file path of JSL config file.
     * @return JSL Settings object.
     */
    public static JSL.Settings loadSettings(String fileName) throws JSL.FactoryException {
        return loadSettings(fileName, "");
    }

    /**
     * Create new JSL Settings object from given <code>fileName</code> and required
     * <code>jslVer</code>.
     * <p>
     * If <code>jslVer</code> is not empty, then his value will be updated on
     * JSL.Settings loaded object. Otherwise {@link #JSL_VER_LATEST}
     * ({@value #JSL_VER_LATEST}) version will be used.
     *
     * @param fileName file path of JSL config file.
     * @param jslVer   version corresponding to JSL.Settings implementation required.
     * @return JSL Settings object.
     */
    public static JSL.Settings loadSettings(String fileName, String jslVer) throws JSL.FactoryException {
        File file = Paths.get(fileName).toFile();
        if (!file.exists())
            throw new JSL.FactoryException(String.format("JSL config file '%s' not found.", file.getAbsolutePath()));
        boolean updJSLVerOnSettings = !jslVer.isEmpty();
        if (jslVer.isEmpty()) jslVer = JSL_VER_LATEST;

        Class<? extends JSL.Settings> jslSettingsClass = getJSLSettingsClass(jslVer);

        try {
            Method method = jslSettingsClass.getMethod(NEW_INSTANCE_METHOD, file.getClass());
            Object instance = method.invoke(null, file);
            if (instance == null)
                throw new JSL.FactoryException(String.format("JSL.Settings init method '%s::%s(%s)' return null object.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()));
            if (jslSettingsClass.isInstance(instance))
                return jslSettingsClass.cast(instance);
            if (instance instanceof JSL.Settings)
                throw new JSL.FactoryException(String.format("JSL.Settings init method '%s::%s(%s)' return object of wrong sub-type '%s'.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName(), instance.getClass().getSimpleName()));
            else
                throw new JSL.FactoryException(String.format("JSL.Settings init method '%s::%s(%s)' return wrong object of type '%s'.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName(), instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JSL.FactoryException(String.format("JSL.Settings init method '%s::%s(%s)' not found.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new JSL.FactoryException(String.format("Can't access to JSL.Settings init method '%s::%s(%s)'.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new JSL.FactoryException(String.format("Error.Settings occurred during '%s::%s(%s)' JSL.Settings init method execution.", jslSettingsClass.getName(), NEW_INSTANCE_METHOD, file.getClass().getSimpleName()), e);
        }
    }

    /**
     * Create new JSL Object with given <code>settings</code>.
     *
     * @param settings JSL Settings object.
     * @return JSL Object.
     */
    public static JSL createJSL(JSL.Settings settings) throws JSL.FactoryException {
        return createJSL(settings, JSL_VER_LATEST);
    }

    /**
     * Create new JSL Object with given <code>settings</code> and required
     * <code>jslVer</code>.
     *
     * @param settings JSL Settings object.
     * @param jslVer   version corresponding to JSL implementation required.
     * @return JSL Object.
     */
    public static JSL createJSL(JSL.Settings settings, String jslVer) throws JSL.FactoryException {
        if (settings == null) throw new JSL.FactoryException("JSL init method require Settings param");
        if (jslVer.isEmpty()) jslVer = JSL_VER_LATEST;

        Class<? extends AbsJSL> jslClass = getJSLClass(jslVer);

        try {
            Method method = jslClass.getMethod(NEW_INSTANCE_METHOD, settings.getClass());
            Object instance = method.invoke(null, settings);
            if (instance == null)
                throw new JSL.FactoryException(String.format("JSL init method '%s::%s(%s)' return null object.", jslClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()));
            if (instance instanceof JSL)
                return (JSL) instance;
            throw new JSL.FactoryException(String.format("JSL init method '%s::%s(%s)' return wrong object of type '%s'.", jslClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName(), instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JSL.FactoryException(String.format("JSL init method '%s::%s(%s)' not found.", jslClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new JSL.FactoryException(String.format("Can't access to JSL init method '%s::%s(%s)'.", jslClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new JSL.FactoryException(String.format("Error occurred during '%s::%s(%s)' JSL init method execution.", jslClass.getName(), NEW_INSTANCE_METHOD, settings.getClass().getSimpleName()), e);
        }
    }


    // Implementation class finders

    /**
     * <ul>
     *     <li>
     *         {@value JSL_VER_002} => {@link JSLSettings_002}
     *     </li>
     * </ul>
     *
     * @param jslVer version corresponding to JSL.Settings implementation required.
     * @return JSL.Settings class corresponding to given <code>jslVer</code> version.
     */
    private static Class<? extends JSL.Settings> getJSLSettingsClass(String jslVer) throws JSL.FactoryException {
        if (JSL_VER_002.compareToIgnoreCase(jslVer) == 0) return JSL_VER_002_CONFIG_CLASS;
        if (JSL_VER_2_0_0.compareToIgnoreCase(jslVer) == 0) return JSL_VER_2_0_0_CONFIG_CLASS;

        throw new JSL.FactoryException(String.format("JSL.Settings '%s' version not found.", jslVer));
    }

    /**
     * <ul>
     *     <li>
     *         {@value JSL_VER_002} => {@link JSL_002}
     *     </li>
     * </ul>
     *
     * @param jslVer version corresponding to JSL implementation required.
     * @return JSL class corresponding to given <code>jslVer</code> version.
     */
    private static Class<? extends AbsJSL> getJSLClass(String jslVer) throws JSL.FactoryException {
        if (JSL_VER_002.compareToIgnoreCase(jslVer) == 0) return JSL_VER_002_CLASS;
        if (JSL_VER_2_0_0.compareToIgnoreCase(jslVer) == 0) return JSL_VER_2_0_0_CLASS;

        throw new JSL.FactoryException(String.format("JSL.Settings '%s' version not found.", jslVer));
    }

}
