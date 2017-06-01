package org.opengeoportal.harvester.api.metadata.model;

public enum AccessLevel {
    Public, Restricted;

    public static AccessLevel parseString(final String accessString) {
        if (accessString.equalsIgnoreCase("public")) {
            return AccessLevel.Public;
        } else {
            return AccessLevel.Restricted;
        }
    }
}
