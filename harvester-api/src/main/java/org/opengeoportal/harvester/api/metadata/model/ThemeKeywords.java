package org.opengeoportal.harvester.api.metadata.model;

import java.util.HashSet;
import java.util.Set;

public class ThemeKeywords {
	
	public enum ThemeKeywordAuthority {
		ISOKeywords("ISO 19115"),
		FGDCKeywords("FGDC"),
		LCSHKeywords("LCSH"),
		Unrecognized("unrecognized"),
		Unspecified("unspecified");
		
		private final String authorityId;
		
		ThemeKeywordAuthority(String authorityId){
			this.authorityId = authorityId;
		}
		
		public String getAuthorityId(){
			return this.authorityId;
		}
	}
	
	private ThemeKeywordAuthority themeKeywordAuthority = ThemeKeywordAuthority.Unspecified;
	private Set<String> keywords = new HashSet<String>();
	
	public void setThesaurus(String thesaurus){
		if ((thesaurus == null)||thesaurus.isEmpty()){
			themeKeywordAuthority = ThemeKeywordAuthority.Unspecified;
			return;
		}
		thesaurus = thesaurus.trim().toUpperCase();
		for (ThemeKeywordAuthority tkauth : ThemeKeywordAuthority.values()){
			if (thesaurus.contains(tkauth.getAuthorityId().toUpperCase())){
				themeKeywordAuthority = tkauth;
				return;
			}
		}
		themeKeywordAuthority = ThemeKeywordAuthority.Unrecognized;
	}
	
	public ThemeKeywordAuthority getKeywordAuthority(){
		return themeKeywordAuthority;
	}
	
	public void addKeyword(String keyword){
		keywords.add(keyword.trim());
	}
	
	public Set<String> getKeywords(){
		return keywords;
	}
}
