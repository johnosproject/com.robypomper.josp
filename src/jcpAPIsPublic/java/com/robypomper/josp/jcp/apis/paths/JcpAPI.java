package com.robypomper.josp.jcp.apis.paths;


/**
 * JCP APIs definition class.
 * <p>
 * This class define base JCP APIs constants like domains, base paths...
 * Constants about APIs suites and their methods are contained in
 * <code>JCP{APIName}</code> classes.
 * <p>
 * This class and APIs suites definitions classes provide constants about APIs url.
 * Each url can be get:
 * <ul>
 *     <li>
 *         <b>partial:</b> each url is parted and each part can be accessed individually
 *         <b>full:</b> for each url is provided a full path constants
 *         <b>url:</b> the full url including the protocol, domain and host port
 *     </li>
 * </ul>
 * <p>
 * APIs suites definitions classes are implemented using following schema:
 * <code>
 *     // Path part, here set the constant string value
 *     public static final String PATH_USER    = "/admin/realms/" + version + "/users";
 *     // Full path assembly
 *     public static final String FULL_PATH_USER   = FULL_PATH_BASE + PATH_USER;
 *      // Url assembly
 *     public static final String URL_PATH_USER    = URL_PATH_BASE + PATH_USER;
 * </code>
 * <p>
 * This class provide domains and APIs basic path constants for all APIs used in
 * the JOSP project.
 */
public class JcpAPI {
//@formatter:off

    // APIs

    public static final String PATH_API_BASE    = "/apis";
    public static final String PATH_AUTH_BASE   = "/auth";

//@formatter:on
}
