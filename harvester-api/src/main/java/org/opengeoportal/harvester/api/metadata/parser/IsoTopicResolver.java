package org.opengeoportal.harvester.api.metadata.parser;

import java.util.Arrays;
import java.util.HashSet;

public class IsoTopicResolver {
    HashSet<String> isoTopics;

    IsoTopicResolver() {
        this.isoTopics = new HashSet<String>(
                Arrays.asList(new String[] { "farming", "biota", "boundaries",
                        "climatologyMeteorologyAtmosphere", "economy",
                        "elevation", "environment", "imageryBaseMapsEarthCover",
                        "intelligenceMilitary", "inlandWaters", "location",
                        "oceans", "planningCadastre", "society", "structure",
                        "transportation", "utilitiesCommunication",
                        "geoscientificInformation", "health" }));
    }

    public String getIsoTopicKeyword(final String keyword) {
        for (final String topic : this.isoTopics) {
            if (topic.equalsIgnoreCase(keyword.trim())) {
                return topic;
            }
        }

        return "";
    };

}
