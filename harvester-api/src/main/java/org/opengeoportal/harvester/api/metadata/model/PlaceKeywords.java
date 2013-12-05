package org.opengeoportal.harvester.api.metadata.model;

import java.util.HashSet;
import java.util.Set;

public class PlaceKeywords {
	
	public enum PlaceKeywordAuthority {
		GNSKeywords("GNS"),
		LCNHKeywords("LCNH"),
		GNISKeywords("GNIS"),
		Unrecognized("unrecognized"),
		Unspecified("unspecified");
		
		private final String authorityId;
		
		PlaceKeywordAuthority(String authorityId){
			this.authorityId = authorityId;
		}
		
		public String getAuthorityId(){
			return this.authorityId;
		}
	}
	private PlaceKeywordAuthority placeKeywordAuthority = PlaceKeywordAuthority.Unspecified;
	private Set<String> keywords = new HashSet<String>();
	
	public void setThesaurus(String thesaurus){
		if ((thesaurus == null)||thesaurus.isEmpty()){
			placeKeywordAuthority = PlaceKeywordAuthority.Unspecified;
			return;
		}
		thesaurus = thesaurus.trim().toUpperCase();
		for (PlaceKeywordAuthority tkauth : PlaceKeywordAuthority.values()){
			if (thesaurus.contains(tkauth.getAuthorityId().toUpperCase())){
				placeKeywordAuthority = tkauth;
				return;
			}
		}
		placeKeywordAuthority = PlaceKeywordAuthority.Unrecognized;
	}
	
	public PlaceKeywordAuthority getKeywordAuthority(){
		return placeKeywordAuthority;
	}
	
	public void addKeyword(String keyword){
		keywords.add(keyword.trim());
	}
	
	public Set<String> getKeywords(){
		return keywords;
	}
}
