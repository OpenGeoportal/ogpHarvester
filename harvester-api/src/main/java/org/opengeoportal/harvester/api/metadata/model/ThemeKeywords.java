package org.opengeoportal.harvester.api.metadata.model;

import java.util.HashSet;
import java.util.Set;

public class ThemeKeywords {

    public enum ThemeKeywordAuthority {
        ISOKeywords("ISO 19115"), FGDCKeywords("FGDC"), LCSHKeywords(
                "LCSH"), Unrecognized(
                        "unrecognized"), Unspecified("unspecified");

        private final String authorityId;

        ThemeKeywordAuthority(final String authorityId) {
            this.authorityId = authorityId;
        }

        public String getAuthorityId() {
            return this.authorityId;
        }
    }

    private ThemeKeywordAuthority themeKeywordAuthority = ThemeKeywordAuthority.Unspecified;
    private final Set<String> keywords = new HashSet<String>();

    public void addKeyword(final String keyword) {
        this.keywords.add(keyword.trim());
    }

    public ThemeKeywordAuthority getKeywordAuthority() {
        return this.themeKeywordAuthority;
    }

    public Set<String> getKeywords() {
        return this.keywords;
    }

    public void setThesaurus(String thesaurus) {
        if ((thesaurus == null) || thesaurus.isEmpty()) {
            this.themeKeywordAuthority = ThemeKeywordAuthority.Unspecified;
            return;
        }
        thesaurus = thesaurus.trim().toUpperCase();
        for (final ThemeKeywordAuthority tkauth : ThemeKeywordAuthority
                .values()) {
            if (thesaurus.contains(tkauth.getAuthorityId().toUpperCase())) {
                this.themeKeywordAuthority = tkauth;
                return;
            }
        }
        this.themeKeywordAuthority = ThemeKeywordAuthority.Unrecognized;
    }
}
