package com.robypomper.josp.jod;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

/**
 * JOD Object factory.
 *
 * This class allow create JOD objects specifying their settings and JOD version.
 *
 * JOD and JOD.Settings versions must match.
 *
 * String versions are resolved with {@link JOD.Settings} implementation class
 * or a {@link AbsJOD} sub-class by {@link #getJODSettingsClass(String)} and
 * {@link #getJODSettingsClass(String)}.
 */
public class FactoryJOD {

    // Version constants

    public static final String JOD_VER_002 = "0.0.2";
    public static final String JOD_VER_LATEST = JOD_VER_002;


    // New instance method name

    private static final String NEW_INSTANCE_METHOD = "instance";


    // Factory methods

    /**
     * Create new JOD Settings object from given <code>fileName</code>.
     *
     * @param fileName file path of JOD config file.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(String fileName) throws JOD.FactoryException {
        return loadSettings(fileName,"");
    }

    /**
     * Create new JOD Settings object from given <code>fileName</code> and required
     * <code>jodVer</code>.
     *
     * If <code>jodVer</code> is not empty, then his value will be updated on
     * JOD.Settings loaded object. Otherwise {@link #JOD_VER_LATEST}
     * ({@value #JOD_VER_LATEST}) version will be used.
     *
     * @param fileName file path of JOD config file.
     * @param jodVer version corresponding to JOD.Settings implementation required.
     * @return JOD Settings object.
     */
    public static JOD.Settings loadSettings(String fileName, String jodVer) throws JOD.FactoryException {
        File file = Paths.get(fileName).toFile();
        if (!file.exists()) throw new JOD.FactoryException(String.format("JOD config file '%s' not found.", file.getAbsolutePath()));
        boolean updJODVerOnSettings = !jodVer.isEmpty();
        if (jodVer.isEmpty()) jodVer = JOD_VER_LATEST;

        Class<? extends JOD.Settings> jodSettingsClass = getJODSettingsClass(jodVer);

        try {
            Method method = jodSettingsClass.getMethod(NEW_INSTANCE_METHOD,file.getClass());
            Object instance = method.invoke(null,file);
            if (instance==null)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return null object.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName()));
            if (jodSettingsClass.isInstance(instance)) {
                JOD.Settings settings = jodSettingsClass.cast(instance);
                if (updJODVerOnSettings)
                    settings.setJODVersion_Required(jodVer,false);
                return settings;
            }
            if (instance instanceof JOD.Settings)
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return object of wrong sub-type '%s'.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName(),instance.getClass().getSimpleName()));
            else
                throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' return wrong object of type '%s'.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName(),instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JOD.FactoryException(String.format("JOD.Settings init method '%s::%s(%s)' not found.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName()),e);
        } catch (IllegalAccessException e) {
            throw new JOD.FactoryException(String.format("Can't access to JOD.Settings init method '%s::%s(%s)'.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName()),e);
        } catch (InvocationTargetException e) {
            throw new JOD.FactoryException(String.format("Error.Settings occurred during '%s::%s(%s)' JOD.Settings init method execution.", jodSettingsClass.getName(),NEW_INSTANCE_METHOD,file.getClass().getSimpleName()),e);
        }
    }

    /**
     * Create new JOD Object with given <code>settings</code>.
     *
     * @param settings JOD Settings object.
     * @return JOD Object.
     */
    public static JOD createJOD(JOD.Settings settings) throws JOD.FactoryException {
        return createJOD(settings,settings.getJODVersion_Required());
    }

    /**
     * Create new JOD Object with given <code>settings</code> and required
     * <code>jodVer</code>.
     *
     * @param settings JOD Settings object.
     * @param jodVer version corresponding to JOD implementation required.
     * @return JOD Object.
     */
    public static JOD createJOD(JOD.Settings settings, String jodVer) throws JOD.FactoryException {
        if (settings==null) throw new JOD.FactoryException("JOD init method require Settings param");
        if (jodVer.isEmpty()) jodVer = settings.getJODVersion_Required();

        Class<? extends AbsJOD> jodClass = getJODClass(jodVer);

        try {
            Method method = jodClass.getMethod(NEW_INSTANCE_METHOD,settings.getClass());
            Object instance = method.invoke(null,settings);
            if (instance==null)
                throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' return null object.", jodClass.getName(),NEW_INSTANCE_METHOD,settings.getClass().getSimpleName()));
            if (instance instanceof JOD)
                return (JOD)instance;
            throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' return wrong object of type '%s'.", jodClass.getName(),NEW_INSTANCE_METHOD,settings.getClass().getSimpleName(),instance.getClass().getSimpleName()));

        } catch (NoSuchMethodException e) {
            throw new JOD.FactoryException(String.format("JOD init method '%s::%s(%s)' not found.", jodClass.getName(),NEW_INSTANCE_METHOD,settings.getClass().getSimpleName()),e);
        } catch (IllegalAccessException e) {
            throw new JOD.FactoryException(String.format("Can't access to JOD init method '%s::%s(%s)'.", jodClass.getName(),NEW_INSTANCE_METHOD,settings.getClass().getSimpleName()),e);
        } catch (InvocationTargetException e) {
            throw new JOD.FactoryException(String.format("Error occurred during '%s::%s(%s)' JOD init method execution.", jodClass.getName(),NEW_INSTANCE_METHOD,settings.getClass().getSimpleName()),e);
        }
    }


    // Implementation class finders

    /**
     * <ul>
     *     <li>
     *         {@value JOD_VER_002} => {@link JODSettings_002}
     *     </li>
     * </ul>
     *
     * @param jodVer version corresponding to JOD.Settings implementation required.
     * @return JOD.Settings class corresponding to given <code>jodVer</code> version.
     */
    private static Class<? extends JOD.Settings> getJODSettingsClass(String jodVer) throws JOD.FactoryException {
        if (JOD_VER_002.compareToIgnoreCase(jodVer) == 0) return JODSettings_002.class;

        throw new JOD.FactoryException(String.format("JOD.Settings '%s' version not found.", jodVer));
    }

    /**
     * <ul>
     *     <li>
     *         {@value JOD_VER_002} => {@link JOD_002}
     *     </li>
     * </ul>
     *
     * @param jodVer version corresponding to JOD implementation required.
     * @return JOD class corresponding to given <code>jodVer</code> version.
     */
    private static Class<? extends AbsJOD> getJODClass(String jodVer) throws JOD.FactoryException {
        if (JOD_VER_002.compareToIgnoreCase(jodVer)==0) return JOD_002.class;

        throw new JOD.FactoryException(String.format("JOD.Settings '%s' version not found.", jodVer));
    }
}
