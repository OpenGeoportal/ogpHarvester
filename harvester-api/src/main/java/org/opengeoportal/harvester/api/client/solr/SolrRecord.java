package org.opengeoportal.harvester.api.client.solr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.opengeoportal.harvester.api.metadata.model.BoundingBox;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class SolrRecord {
	/** Name Theme Keywords field. */
	public static final String THEME_KEYWORDS = "ThemeKeywords";

	/** Name of Place Keywords field. */
	public static final String PLACE_KEYWORDS = "PlaceKeywords";
	
	/** Content date field name. */
	public static final String CONTENT_DATE = "ContentDate";
	/** Originator field name. */
	public static final String ORIGINATOR = "Originator";
	/** Data type field name. */
	public static final String DATA_TYPE = "DataType";
	/** Institution field name. */
	public static final String INSTITUTION = "Institution";
	/** Access field name. */
	public static final String ACCESS = "Access";
	/** Solr timestamp field name. */
	public static final String TIMESTAMP = "timestamp";

	@Field("LayerId")
	private String layerId;
	@Field("Name")
	private String name;
	@Field("CollectionId")
	private String collectionId;
	@Field("ExternalLayerId")
	private String externalLayerId;
	@Field("Institution")
	private String institution;
	@Field("InstitutionSort")
	private String institutionSort;
	@Field("Access")
	private String access;
	@Field("DataType")
	private String dataType;
	@Field("Availability")
	private String availability;
	@Field("LayerDisplayName")
	private String layerDisplayName;
	@Field("Publisher")
	private String publisher;
	@Field("Originator")
	private String originator;
	@Field("ThemeKeywords")
	private String themeKeywords;
	@Field("PlaceKeywords")
	private String placeKeywords;
	@Field("GeoReferenced")
	private String georeferenced;
	@Field("Abstract")
	private String description;
	@Field("Location")
	private String location;
	@Field("MaxY")
	private String maxY;
	@Field("MinY")
	private String minY;
	@Field("MaxX")
	private String maxX;
	@Field("MinX")
	private String minX;
	@Field("CenterX")
	private String centerX;
	@Field("CenterY")
	private String centerY;
	@Field("HalfWidth")
	private String halfWidth;
	@Field("HalfHeight")
	private String halfHeight;
	@Field("Area")
	private String area;
	@Field("WorkspaceName")
	private String workspaceName;
	@Field("ContentDate")
	private String contentDate;
	@Field("FgdcText")
	private String fgdcText;

	/**
	 * @return the layerId
	 */
	public String getLayerId() {
		return layerId;
	}

	/**
	 * @param layerId
	 *            the layerId to set
	 */
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the collectionId
	 */
	public String getCollectionId() {
		return collectionId;
	}

	/**
	 * @param collectionId
	 *            the collectionId to set
	 */
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	/**
	 * @return the externalLayerId
	 */
	public String getExternalLayerId() {
		return externalLayerId;
	}

	/**
	 * @param externalLayerId
	 *            the externalLayerId to set
	 */
	public void setExternalLayerId(String externalLayerId) {
		this.externalLayerId = externalLayerId;
	}

	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @param institution
	 *            the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * @return the institutionSort
	 */
	public String getInstitutionSort() {
		return institutionSort;
	}

	/**
	 * @param institutionSort
	 *            the institutionSort to set
	 */
	public void setInstitutionSort(String institutionSort) {
		this.institutionSort = institutionSort;
	}

	/**
	 * @return the access
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * @param access
	 *            the access to set
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the availability
	 */
	public String getAvailability() {
		return availability;
	}

	/**
	 * @param availability
	 *            the availability to set
	 */
	public void setAvailability(String availability) {
		this.availability = availability;
	}

	/**
	 * @return the layerDisplayName
	 */
	public String getLayerDisplayName() {
		return layerDisplayName;
	}

	/**
	 * @param layerDisplayName
	 *            the layerDisplayName to set
	 */
	public void setLayerDisplayName(String layerDisplayName) {
		this.layerDisplayName = layerDisplayName;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher
	 *            the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the originator
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * @return the themeKeywords
	 */
	public String getThemeKeywords() {
		return themeKeywords;
	}

	/**
	 * @param themeKeywords
	 *            the themeKeywords to set
	 */
	public void setThemeKeywords(String themeKeywords) {
		this.themeKeywords = themeKeywords;
	}

	/**
	 * @return the placeKeywords
	 */
	public String getPlaceKeywords() {
		return placeKeywords;
	}

	/**
	 * @param placeKeywords
	 *            the placeKeywords to set
	 */
	public void setPlaceKeywords(String placeKeywords) {
		this.placeKeywords = placeKeywords;
	}

	/**
	 * @return the georeferenced
	 */
	public String getGeoreferenced() {
		return georeferenced;
	}

	/**
	 * @param georeferenced
	 *            the georeferenced to set
	 */
	public void setGeoreferenced(String georeferenced) {
		this.georeferenced = georeferenced;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the maxY
	 */
	public String getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY
	 *            the maxY to set
	 */
	public void setMaxY(String maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the minY
	 */
	public String getMinY() {
		return minY;
	}

	/**
	 * @param minY
	 *            the minY to set
	 */
	public void setMinY(String minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxX
	 */
	public String getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX
	 *            the maxX to set
	 */
	public void setMaxX(String maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the minX
	 */
	public String getMinX() {
		return minX;
	}

	/**
	 * @param minX
	 *            the minX to set
	 */
	public void setMinX(String minX) {
		this.minX = minX;
	}

	/**
	 * @return the centerX
	 */
	public String getCenterX() {
		return centerX;
	}

	/**
	 * @param centerX
	 *            the centerX to set
	 */
	public void setCenterX(String centerX) {
		this.centerX = centerX;
	}

	/**
	 * @return the centerY
	 */
	public String getCenterY() {
		return centerY;
	}

	/**
	 * @param centerY
	 *            the centerY to set
	 */
	public void setCenterY(String centerY) {
		this.centerY = centerY;
	}

	/**
	 * @return the halfWidth
	 */
	public String getHalfWidth() {
		return halfWidth;
	}

	/**
	 * @param halfWidth
	 *            the halfWidth to set
	 */
	public void setHalfWidth(String halfWidth) {
		this.halfWidth = halfWidth;
	}

	/**
	 * @return the halfHeight
	 */
	public String getHalfHeight() {
		return halfHeight;
	}

	/**
	 * @param halfHeight
	 *            the halfHeight to set
	 */
	public void setHalfHeight(String halfHeight) {
		this.halfHeight = halfHeight;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return the workspaceName
	 */
	public String getWorkspaceName() {
		return workspaceName;
	}

	/**
	 * @param workspaceName
	 *            the workspaceName to set
	 */
	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	/**
	 * @return the contentDate.
	 */
	public String getContentDate() {
		return contentDate;
	}

	/**
	 * @param contentDate
	 *            the contentDate to set
	 */
	public void setContentDate(String contentDate) {
		this.contentDate = contentDate;
	}

	/**
	 * @return the fgdcText
	 */
	public String getFgdcText() {
		return fgdcText;
	}

	/**
	 * @param fgdcText
	 *            the fgdcText to set
	 */
	public void setFgdcText(String fgdcText) {
		this.fgdcText = fgdcText;
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("LayerId", this.layerId);
		map.put("LayerName", this.name);
		map.put("Title", this.layerDisplayName);
		map.put("DataType", this.dataType);
		map.put("Access", this.access);
		map.put("ContentDate", this.contentDate);
		map.put("Bounds", this.minX + "," + this.minY + "," + this.maxX + ","
				+ this.maxY);
		map.put("Originator", this.originator);
		map.put("Publisher", this.publisher);
		return map;
	}

	public String toString() {
		Map<String, String> map = this.toMap();
		String s = "";
		for (String key : map.keySet()) {
			s += key;
			s += ": ";
			s += map.get(key);
			s += ",";
		}
		return s;
	}

	public static SolrRecord build(Metadata metadata) {
		SolrRecord record = new SolrRecord();

		record.setDescription(metadata.getDescription());
		record.setName(metadata.getOwsName().toUpperCase());
		record.setLayerId(metadata.getInstitution() + "." + metadata.getId());
		record.setInstitution(metadata.getInstitution());
		record.setLayerDisplayName(metadata.getTitle());
		record.setOriginator(metadata.getOriginator());
		record.setPublisher(metadata.getPublisher());
		record.setInstitution(metadata.getInstitution());
		record.setAccess(metadata.getAccess().toString());

        BoundingBox bounds = metadata.getBounds();
        if ((bounds != null) && (bounds.isValid())) {
            record.setMinX(Double.toString(bounds.getMinX()));
            record.setMaxX(Double.toString(bounds.getMaxX()));
            record.setMinY(Double.toString(bounds.getMinY()));
            record.setMaxY(Double.toString(bounds.getMaxY()));

			// calculated fields
			record.setHalfHeight(Double.toString(bounds.getHeight() / 2.));
			record.setHalfWidth(Double.toString(bounds.getWidth() / 2.));

			record.setCenterX(Double.toString(bounds.getCenterX()));
			record.setCenterY(Double.toString(bounds.getCenterY()));

			record.setArea(Double.toString(bounds.getArea()));
		}

		record.setContentDate(metadata.getContentDate());
		record.setPlaceKeywords(metadata.getPlaceKeywordsAsString());
		record.setThemeKeywords(metadata.getThemeKeywordsAsString());
		record.setDataType(metadata.getGeometryType().toString());

		record.setAccess(metadata.getAccess().toString());
		if (StringUtils.isNotEmpty(metadata.getWorkspaceName())) {
			record.setWorkspaceName(metadata.getWorkspaceName().toString());
		}
		record.setGeoreferenced(Boolean.toString(metadata.getGeoreferenced()));

		// TODO: Check
		record.setAvailability("online");

		return record;
	}
}
