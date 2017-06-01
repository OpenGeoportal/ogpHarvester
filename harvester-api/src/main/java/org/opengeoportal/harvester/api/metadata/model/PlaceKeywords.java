package org.opengeoportal.harvester.api.metadata.model;

import java.util.HashSet;
import java.util.Set;

public class PlaceKeywords {

    public enum PlaceKeywordAuthority {
        GNSKeywords("GNS"), LCNHKeywords("LCNH"), GNISKeywords(
                "GNIS"), Unrecognized(
                        "unrecognized"), Unspecified("unspecified");

        private final String authorityId;

        PlaceKeywordAuthority(final String authorityId) {
            this.authorityId = authorityId;
        }

        public String getAuthorityId() {
            return this.authorityId;
        }
    }

    private PlaceKeywordAuthority placeKeywordAuthority = PlaceKeywordAuthority.Unspecified;
    private final Set<String> keywords = new HashSet<String>();

    public void addKeyword(final String keyword) {
        this.keywords.add(keyword.trim());
    }

    public PlaceKeywordAuthority getKeywordAuthority() {
        return this.placeKeywordAuthority;
    }

    public Set<String> getKeywords() {
        return this.keywords;
    }

    public void setThesaurus(String thesaurus) {
        if ((thesaurus == null) || thesaurus.isEmpty()) {
            this.placeKeywordAuthority = PlaceKeywordAuthority.Unspecified;
            return;
        }
        thesaurus = thesaurus.trim().toUpperCase();
        for (final PlaceKeywordAuthority tkauth : PlaceKeywordAuthority
                .values()) {
            if (thesaurus.contains(tkauth.getAuthorityId().toUpperCase())) {
                this.placeKeywordAuthority = tkauth;
                return;
            }
        }
        this.placeKeywordAuthority = PlaceKeywordAuthority.Unrecognized;
    }
}
