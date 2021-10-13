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

package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODBooleanState;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeState;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.Map;

public class Substitutions {

    //@formatter:off
    // Object substitution placeholders (see AbsJODWorker#objectSubstitution())
    public static final String OBJ_ID       = "%OBJ_ID%";
    public static final String OBJ_NAME     = "%OBJ_NAME%";
    public static final String OBJ_OWNER    = "%OBJ_OWNER%";
    public static final String OBJ_JOD_VER  = "%OBJ_JOD_VER%";
    public static final String OBJ_MODEL    = "%OBJ_MODEL%";
    public static final String OBJ_BRAND    = "%OBJ_BRAND%";

    // Component substitution placeholders (see AbsJODWorker#componentSubstitution())
    public static final String COMP_NAME    = "%C_NAME%";
    public static final String COMP_TYPE    = "%C_TYPE%";
    public static final String COMP_PATH    = "%C_PATH%";
    public static final String PARENT_NAME  = "%PARENT_NAME%";
    public static final String PARENT_PATH  = "%PARENT_PATH%";

    // Action substitution placeholders (see AbsJODWorker#stateSubstitution())
    // Generic
    public static final String STATE_VAL        = "%VAL%";
    // Boolean
    public static final String STATE_VAL_BOOL   = "%VAL_BOOL%";
    public static final String STATE_VAL_BIN    = "%VAL_BIN%";
    // Range
    public static final String STATE_VAL_COMMA  = "%VAL_COMMA%";
    public static final String STATE_VAL_POINT  = "%VAL_POINT%";

    // Action substitution placeholders
    // Generic
    public static final String ACTION_VAL               = "%VAL%";
    public static final String ACTION_VAL_OLD           = "%VAL_OLD%";
    public static final String ACTION_SRV_ID            = "%SRV_ID%";
    public static final String ACTION_USR_ID            = "%USR_ID%";
    // Boolean
    public static final String ACTION_VAL_BOOL          = "%VAL_BOOL%";
    public static final String ACTION_VAL_OLD_BOOL      = "%VAL_OLD_BOOL%";
    public static final String ACTION_VAL_BIN           = "%VAL_BIN%";
    public static final String ACTION_VAL_OLD_BIN       = "%VAL_OLD_BIN%";
    // Range
    public static final String ACTION_VAL_COMMA         = "%VAL_COMMA%";
    public static final String ACTION_VAL_OLD_COMMA     = "%VAL_OLD_COMMA%";
    public static final String ACTION_VAL_POINT         = "%VAL_POINT%";
    public static final String ACTION_VAL_OLD_POINT     = "%VAL_OLD_POINT%";
    public static final String ACTION_VAL_INT           = "%VAL_INT%";
    public static final String ACTION_VAL_OLD_INT       = "%VAL_OLD_INT%";
    //@formatter:on


    // Constructor

    private final String str;

    public Substitutions(String str) {
        this.str = str;
    }


    // Substitution methods

    public Substitutions substituteObject(JODObjectInfo objInfo) {
        return new Substitutions(doSubstituteObject(str, objInfo));
    }

    public Substitutions substituteComponent(JODComponent component) {
        return new Substitutions(doSubstituteComponent(str, component));
    }

    public Substitutions substituteWorker(JODWorker worker) {
        return new Substitutions(doSubstituteWorker(str, worker));
    }

    public Substitutions substituteState(JODState componentState) {
        return new Substitutions(doSubstituteState(str, componentState));
    }

    public Substitutions substituteAction(JOSPProtocol.ActionCmd commandAction) {
        return new Substitutions(doSubstituteAction(str, commandAction));
    }

    private static String doSubstituteObject(String str, JODObjectInfo objInfo) {
        if (objInfo == null
                || str == null
                || str.isEmpty()
        ) return str;

        return str
                .replaceAll(Substitutions.OBJ_ID, objInfo.getObjId())
                .replaceAll(Substitutions.OBJ_NAME, objInfo.getObjName())
                .replaceAll(Substitutions.OBJ_OWNER, objInfo.getOwnerId())
                .replaceAll(Substitutions.OBJ_JOD_VER, objInfo.getJODVersion())
                .replaceAll(Substitutions.OBJ_MODEL, objInfo.getModel())
                .replaceAll(Substitutions.OBJ_BRAND, objInfo.getBrand())
                ;
    }

    private static String doSubstituteComponent(String str, JODComponent component) {
        if (component == null
                || str == null
                || str.isEmpty()
        ) return str;

        return str
                .replaceAll(Substitutions.COMP_NAME, component.getName())
                .replaceAll(Substitutions.COMP_PATH, component.getPath().getString())
                .replaceAll(Substitutions.COMP_TYPE, component.getType())
                .replaceAll(Substitutions.PARENT_NAME, component.getParent() != null ? component.getParent().getName() : "N/A")
                .replaceAll(Substitutions.PARENT_PATH, component.getParent() != null ? component.getParent().getPath().getString() : "N/A")
                ;
    }

    private static String doSubstituteWorker(String str, JODWorker worker) {
        if (worker == null
                || str == null
                || str.isEmpty()
        ) return str;

        for (Map.Entry<String, String> conf : worker.getConfigs().entrySet())
            str = str.replace(conf.getKey(), conf.getValue());

        return str;
    }

    private static String doSubstituteState(String str, JODState componentState) {
        if (componentState == null
                || str == null
                || str.isEmpty()
        ) return str;

        if (componentState instanceof JODBooleanState)
            return str
                    .replaceAll(Substitutions.STATE_VAL, ((JODBooleanState) componentState).getStateBoolean() ? "TRUE" : "FALSE")
                    .replaceAll(Substitutions.STATE_VAL_BOOL, ((JODBooleanState) componentState).getStateBoolean() ? "TRUE" : "FALSE")
                    .replaceAll(Substitutions.STATE_VAL_BIN, ((JODBooleanState) componentState).getStateBoolean() ? "1" : "0")
                    ;

        if (componentState instanceof JODRangeState)
            return str
                    .replaceAll(Substitutions.STATE_VAL, JavaFormatter.doubleToStr(((JODRangeState) componentState).getStateRange()))
                    .replaceAll(Substitutions.STATE_VAL_POINT, JavaFormatter.doubleToStr_Point(((JODRangeState) componentState).getStateRange()))
                    .replaceAll(Substitutions.STATE_VAL_COMMA, JavaFormatter.doubleToStr_Comma(((JODRangeState) componentState).getStateRange()))
                    ;

        return str;
    }

    private static String doSubstituteAction(String str, JOSPProtocol.ActionCmd commandAction) {
        if (commandAction == null
                || str == null
                || str.isEmpty()
        ) return str;

        str = str
                .replaceAll(Substitutions.ACTION_SRV_ID, commandAction.getServiceId())
                .replaceAll(Substitutions.ACTION_USR_ID, commandAction.getUserId())
        ;

        if (commandAction.getCommand() instanceof JODBooleanAction.JOSPBoolean)
            return str
                    .replaceAll(Substitutions.ACTION_VAL, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).newState ? "true" : "false")
                    .replaceAll(Substitutions.ACTION_VAL_BOOL, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).newState ? "TRUE" : "FALSE")
                    .replaceAll(Substitutions.ACTION_VAL_BIN, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).newState ? "1" : "0")
                    .replaceAll(Substitutions.ACTION_VAL_OLD, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).oldState ? "TRUE" : "FALSE")
                    .replaceAll(Substitutions.ACTION_VAL_OLD_BOOL, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).oldState ? "TRUE" : "FALSE")
                    .replaceAll(Substitutions.ACTION_VAL_OLD_BIN, ((JODBooleanAction.JOSPBoolean) commandAction.getCommand()).oldState ? "1" : "0")
                    ;

        if (commandAction.getCommand() instanceof JODRangeAction.JOSPRange)
            return str
                    .replaceAll(Substitutions.ACTION_VAL, JavaFormatter.doubleToStr(((JODRangeAction.JOSPRange) commandAction.getCommand()).newState))
                    .replaceAll(Substitutions.ACTION_VAL_POINT, JavaFormatter.doubleToStr_Point(((JODRangeAction.JOSPRange) commandAction.getCommand()).newState))
                    .replaceAll(Substitutions.ACTION_VAL_COMMA, JavaFormatter.doubleToStr_Comma(((JODRangeAction.JOSPRange) commandAction.getCommand()).newState))
                    .replaceAll(Substitutions.ACTION_VAL_INT, JavaFormatter.doubleToStr_Truncated(((JODRangeAction.JOSPRange) commandAction.getCommand()).newState))
                    .replaceAll(Substitutions.ACTION_VAL_OLD, JavaFormatter.doubleToStr(((JODRangeAction.JOSPRange) commandAction.getCommand()).oldState))
                    .replaceAll(Substitutions.ACTION_VAL_OLD_POINT, JavaFormatter.doubleToStr_Point(((JODRangeAction.JOSPRange) commandAction.getCommand()).oldState))
                    .replaceAll(Substitutions.ACTION_VAL_OLD_COMMA, JavaFormatter.doubleToStr_Comma(((JODRangeAction.JOSPRange) commandAction.getCommand()).oldState))
                    .replaceAll(Substitutions.ACTION_VAL_OLD_INT, JavaFormatter.doubleToStr_Truncated(((JODRangeAction.JOSPRange) commandAction.getCommand()).oldState))
                    ;

        return str;
    }


    // toString()

    @Override
    public String toString() {
        return str;
    }

}
