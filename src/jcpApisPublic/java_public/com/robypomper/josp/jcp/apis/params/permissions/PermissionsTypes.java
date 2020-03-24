package com.robypomper.josp.jcp.apis.params.permissions;


/**
 * Messaging types to use in Permission messaging classes.
 */
public class PermissionsTypes {

    /**
     * Define witch connection type is allow in the Permission.
     */
    public enum Connection {
        /**
         * Allow only when connection is local.
         */
        OnlyLocal,
        /**
         * Allow for both local and cloud connections.
         */
        LocalAndCloud
    }

    /**
     * Define witch access type is allow in the Permission.
     */
    public enum Type {
        /**
         * Allow only access to object's basic info.
         */
        None,
        /**
         * Allow access to object's info and status updates.
         */
        Status,
        /**
         * Like {@link #Status}, plus allow action execution.
         */
        Actions,
        /**
         * Like {@link #Actions}, plus allow object's configuration.
         */
        CoOwner
    }

    /**
     * Define permission generation strategy.
     */
    public enum GenerateStrategy {
        /**
         * Permission generated following standard permissions rules.
         */
        STANDARD,
        /**
         * Permission generated following public permissions rules.
         */
        PUBLIC
    }

    /**
     * Define wildcards for service and user ids.
     */
    public enum WildCards {
        /**
         * Indicate that the permission in referred to the object owner.
         */
        USR_OWNER("#Owner"),
        /**
         * Indicate that the permission is applicable to all users.
         */
        USR_ALL("#All"),
        /**
         * Indicate that the permission is applicable to all services.
         */
        SRV_ALL("#All");

        private final String value;

        WildCards(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

    }

}
