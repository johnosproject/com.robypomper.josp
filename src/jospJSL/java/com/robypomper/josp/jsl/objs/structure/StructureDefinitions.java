package com.robypomper.josp.jsl.objs.structure;


/**
 * Definition class containing all definition for JSL Structure context.
 *
 * <ul>
 *     <li>
 *         <b>Component's type</b> are used to parse the object's structure file.
 *     </li>
 *     <li>
 *         <b>Path's elements</b> sub-strings representing path's elements like
 *         components separators and wildcards.
 *     </li>
 *     <li>
 *         <b>Root properties</b> default root component properties.
 *     </li>
 *     <li>
 *         <b>Parsing properties</b> strings that identifies the JSON/XML/...
 *         component's properties.
 *     </li>
 *     <li>
 *         <b>Default component paths</b> strings that can be used to initialize
 *         default components like Root component.
 *     </li>
 * </ul>
 */
public interface StructureDefinitions {

    // Component types

    String TYPE_CONTAINER = "Container";
    String TYPE_BOOL_STATE = "BooleanState";
    String TYPE_RANGE_STATE = "RangeState";
    String TYPE_BOOL_ACTION = "BooleanAction";
    String TYPE_RANGE_ACTION = "RangeAction";


    // Path's elements

    String PATH_SEP = ">";
    String PATH_ALL = "*";


    // Root properties

    String ROOT_NAME = "root";
    String ROOT_DESCR = "Main object's container.";
    String ROOT_PATH = "";


    // Parsing properties

    String PROP_COMPONENT_TYPE = "type";
    String PROP_COMPONENT_NAME = "name";
    String PROP_COMPONENT_DESCR = "descr";
    String PROP_COMPONENT_STATE = "state";
    String PROP_COMPONENT_LISTNER = "listener";
    String PROP_COMPONENT_PULLER = "puller";
    String PROP_COMPONENT_EXECUTOR = "executor";
    String PROP_COMPONENT_RANGE_MIN = "min";
    String PROP_COMPONENT_RANGE_MAX = "max";
    String PROP_COMPONENT_RANGE_STEP = "step";


    // Default component paths

    String PATH_STR_ROOT = ROOT_PATH;
    String PATH_STR_ALL = "*";

}
