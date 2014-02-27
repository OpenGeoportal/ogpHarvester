package org.opengeoportal.harvester.api.metadata.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Metadata {
    BoundingBox bounds;
    String id = "";
    String title = "";
    String description = "";
    String owsName = "";
    String workspaceName = "";
    String location = "";
    String originator = "";
    List<ThemeKeywords> themeKeywords;
    List<PlaceKeywords> placeKeywords;
    String institution = "";
    String fullText = "";
    AccessLevel access;
    GeometryType geometryType;
    private String publisher = "";
    private Boolean georeferenced;
    private Date contentDate;
    private String topic = "";
    private boolean restricted = false;
    /** Raw metadata as retrieved from remote source. */
	private String originalMetadata = "";

    public Metadata(String layerId){
        setId(layerId);
        themeKeywords = new ArrayList<ThemeKeywords>();
        placeKeywords = new ArrayList<PlaceKeywords>();
        georeferenced = Boolean.FALSE;

    }
    public Metadata(){
        this(null);
    }

    public GeometryType getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(GeometryType geometryType) {
        this.geometryType = geometryType;
    }

    public void setGeometryType(String geometryTypeString) {
        this.geometryType = GeometryType.parseGeometryType(geometryTypeString);
    }

    public AccessLevel getAccess() {
        return access;
    }
    public void setAccessLevel(AccessLevel access) {
        this.access = access;
    }

    public void setAccessLevel(String accessString){
        this.access = AccessLevel.parseString(accessString);
    }

    public BoundingBox getBounds() {
        return bounds;
    }
    public void setBounds(BoundingBox bounds) {
        this.bounds = bounds;
    }

    public void setBounds(String minX, String minY, String maxX, String maxY) {
        this.bounds = new BoundingBox(minX, minY, maxX, maxY);
    }
    
    public void setBounds(Double minX, Double minY, Double maxX, Double maxY) {
        this.bounds = new BoundingBox(minX, minY, maxX, maxY);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOwsName() {
        return owsName;
    }
    public void setOwsName(String owsName) {
        this.owsName = owsName;
    }
    public String getWorkspaceName() {
        return workspaceName;
    }
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getOriginator() {
        return originator;
    }
    public void setOriginator(String originator) {
        this.originator = originator;
    }
    public List<ThemeKeywords> getThemeKeywords() {
        return themeKeywords;
    }
    public void setThemeKeywords(List<ThemeKeywords> themeKeywords) {
        this.themeKeywords = themeKeywords;
    }
    public List<PlaceKeywords> getPlaceKeywords() {
        return placeKeywords;
    }
    public void setPlaceKeywords(List<PlaceKeywords> placeKeywords) {
        this.placeKeywords = placeKeywords;
    }
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setAccess(AccessLevel access) {
        this.access = access;
    }

    public String getFullText() {
        return fullText;
    }
    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public Boolean getGeoreferenced() {
        return georeferenced;
    }

    public void setGeoreferenced(Boolean georeferenced){
        this.georeferenced = georeferenced;
    }

    public Date getContentDate() {
        return contentDate;
    }

    public void setContentDate(Date contentDate){
        this.contentDate = contentDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public String getThemeKeywordsAsString(){
        Set<String> allKeywords = new HashSet<String>();
        for (ThemeKeywords themeKeyword: this.themeKeywords){
            allKeywords.addAll(themeKeyword.getKeywords());
        }
        return combine(allKeywords.toArray(new String[allKeywords.size()]), " ");
    }

    public String getPlaceKeywordsAsString(){
        Set<String> allKeywords = new HashSet<String>();
        for (PlaceKeywords placeKeyword: this.placeKeywords){
            allKeywords.addAll(placeKeyword.getKeywords());
        }
        return combine(allKeywords.toArray(new String[allKeywords.size()]), " ");
    }


    public boolean hasValueForProperty(String property) {
        String value = "";

        if (property.equals("dateOfContent")) {
            value = this.getContentDate().toString();
        } else if (property.equals("originator")) {
            value = this.getOriginator();
        } else if (property.equals("themeKeyword")) {
            value = this.getThemeKeywordsAsString();
        } else if (property.equals("placeKeyword")) {
            value = this.getPlaceKeywordsAsString();
        } else if (property.equals("topic")) {
            value = this.getTopic();
        }  else if (property.equals("geographicExtent")) {
            if (this.getBounds() == null) {
                return false;
            } else {
                return this.getBounds().isValid();
            }
        }

        // TODO: dataType, dataRepository, webServices

        return StringUtils.isNotEmpty(value);
    }

    private String combine(String[] s, String glue)
    {
        int k=s.length;
        if (k==0)
            return null;
        StringBuilder out=new StringBuilder();
        out.append(s[0]);
        for (int x=1;x<k;++x)
            out.append(glue).append(s[x]);
        return out.toString();
    }
	/**
	 * @return the originalMetadata
	 */
	public String getOriginalMetadata() {
		return originalMetadata;
	}
	/**
	 * @param originalMetadata the originalMetadata to set
	 */
	public void setOriginalMetadata(String originalMetadata) {
		this.originalMetadata = originalMetadata;
	}
	/**
	 * @return the restricted
	 */
	public boolean isRestricted() {
		return restricted;
	}
	/**
	 * @param restricted the restricted to set
	 */
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
}
