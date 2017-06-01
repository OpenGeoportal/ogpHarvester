package org.opengeoportal.harvester.api.client.solr;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.opengeoportal.harvester.api.metadata.model.BoundingBox;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

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

    /** Topic Category field name. */
    public static final String ISO_TOPIC_CATEGORY = "ThemeKeywordsSynonymsIso";
    /** MinX field name. */
    public static final String MINX = "MinX";
    /** MinY field name. */
    public static final String MINY = "MinY";
    /** MaxX field name. */
    public static final String MAXX = "MaxX";
    /** MaxY field name. */
    public static final String MAXY = "MaxY";

    public static final String LAYER_DISPLAY_NAME = "LayerDisplayName";

    public static final String LAYER_DISPLAY_NAME_SYNONYMS = "LayerDisplayNameSynonyms";
    public static final String THEME_KEYWORDS_SYNONYMS_LCSH = "ThemeKeywordsSynonymsLcsh";
    public static final String PLACE_KEYWORDS_SYNONYMS = "PlaceKeywordsSynonyms";

    /**
     * Build a SolrRecord with the data contained in a {@link Metadata} object.
     * 
     * @param metadata
     *            the metadata instance.
     * @return a SolrRecord built with the data contained in
     *         <code>metadata</code> parameter.
     */
    public static SolrRecord build(final Metadata metadata) {
        final SolrRecord record = new SolrRecord();

        record.setDescription(metadata.getDescription());
        record.setName(metadata.getOwsName().toUpperCase());
        record.setLayerId(SolrRecord.calculateLayerId(metadata));
        record.setInstitution(metadata.getInstitution());
        record.setLayerDisplayName(metadata.getTitle());
        record.setOriginator(metadata.getOriginator());
        record.setPublisher(metadata.getPublisher());
        record.setInstitution(metadata.getInstitution());
        record.setAccess(metadata.getAccess().toString());
        record.setFgdcText(metadata.getFullText());
        record.setLocation(metadata.getLocation());

        final BoundingBox bounds = metadata.getBounds();
        if ((bounds != null) && (bounds.isValid())) {
            record.setMinX(bounds.getMinX());
            record.setMaxX(bounds.getMaxX());
            record.setMinY(bounds.getMinY());
            record.setMaxY(bounds.getMaxY());

            // calculated fields
            record.setHalfHeight(bounds.getHeight() / 2.);
            record.setHalfWidth(bounds.getWidth() / 2.);

            record.setCenterX(bounds.getCenterX());
            record.setCenterY(bounds.getCenterY());

            record.setArea(bounds.getArea());
        } else {
            // Set default values to 0.0 if bounds are not present or are
            // invalid
            record.setMinX(0.0);
            record.setMaxX(0.0);
            record.setMinY(0.0);
            record.setMaxY(0.0);
            record.setHalfHeight(0.0);
            record.setHalfWidth(0.0);

            record.setCenterX(0.0);
            record.setCenterY(0.0);

            record.setArea(0.0);
        }

        record.setContentDate(metadata.getContentDate());
        record.setPlaceKeywords(metadata.getPlaceKeywordsAsString());
        record.setThemeKeywords(metadata.getThemeKeywordsAsString());
        record.setDataType(metadata.getGeometryType().toString());
        record.setWorkspaceName(metadata.getWorkspaceName());
        record.setGeoreferenced(metadata.getGeoreferenced());
        record.setCollectionId(metadata.getCollectionId());
        record.setExternalLayerId(metadata.getExternalId());
        if (StringUtils.isNotEmpty(metadata.getLocation())) {
            record.setAvailability("online");
        } else {
            record.setAvailability("offline");
        }

        return record;
    }

    /**
     * Calculates the value of SolrRecord LayerId based on the related Metadata
     * instance.
     *
     * @param metadata
     * @return
     */
    private static String calculateLayerId(final Metadata metadata) {
        String layerId = "";

        final String institution = metadata.getInstitution();

        // Use Metadata.Id if has a value -> {institution}.{id}
        if (StringUtils.isNotEmpty(metadata.getId())) {
            final String id = metadata.getId();
            // For OGP metadata records the id already has the format:
            // {institution}.{id} , don't add institution again
            if (id.toUpperCase().startsWith(institution.toUpperCase() + ".")) {
                layerId = metadata.getId();
            } else {
                layerId = institution + "." + metadata.getId();
            }
            // Otherwise use Metadata.LayerName -> {institution}.{layerName}
        } else {
            if (StringUtils.isNotEmpty(metadata.getOwsName())) {
                layerId = institution + "."
                        + metadata.getOwsName().toUpperCase();
            } else {
                layerId = institution + "." + UUID.randomUUID().toString();
            }
        }

        return layerId;
    }

    private String originalXmlMetadata;
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
    private Boolean georeferenced;
    @Field("Abstract")
    private String description;
    @Field("Location")
    private String location;
    @Field("MaxY")
    private Double maxY;
    @Field("MinY")
    private Double minY;
    @Field("MaxX")
    private Double maxX;
    @Field("MinX")
    private Double minX;
    @Field("CenterX")
    private Double centerX;
    @Field("CenterY")
    private Double centerY;
    @Field("HalfWidth")
    private Double halfWidth;
    @Field("HalfHeight")
    private Double halfHeight;
    @Field("Area")
    private Double area;
    @Field("WorkspaceName")
    private String workspaceName;

    @Field("ContentDate")
    private Date contentDate;

    @Field("FgdcText")
    private String fgdcText;

    /**
     * @return the access
     */
    public String getAccess() {
        return this.access;
    }

    /**
     * @return the area
     */
    public Double getArea() {
        return this.area;
    }

    /**
     * @return the availability
     */
    public String getAvailability() {
        return this.availability;
    }

    /**
     * @return the centerX
     */
    public Double getCenterX() {
        return this.centerX;
    }

    /**
     * @return the centerY
     */
    public Double getCenterY() {
        return this.centerY;
    }

    /**
     * @return the collectionId
     */
    public String getCollectionId() {
        return this.collectionId;
    }

    /**
     * @return the contentDate.
     */
    public Date getContentDate() {
        return this.contentDate;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the externalLayerId
     */
    public String getExternalLayerId() {
        return this.externalLayerId;
    }

    /**
     * @return the fgdcText
     */
    public String getFgdcText() {
        return this.fgdcText;
    }

    /**
     * @return the georeferenced
     */
    public Boolean getGeoreferenced() {
        return this.georeferenced;
    }

    /**
     * @return the halfHeight
     */
    public Double getHalfHeight() {
        return this.halfHeight;
    }

    /**
     * @return the halfWidth
     */
    public Double getHalfWidth() {
        return this.halfWidth;
    }

    /**
     * @return the institution
     */
    public String getInstitution() {
        return this.institution;
    }

    /**
     * @return the layerDisplayName
     */
    public String getLayerDisplayName() {
        return this.layerDisplayName;
    }

    /**
     * @return the layerId
     */
    public String getLayerId() {
        return this.layerId;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * @return the maxX
     */
    public Double getMaxX() {
        return this.maxX;
    }

    /**
     * @return the maxY
     */
    public Double getMaxY() {
        return this.maxY;
    }

    /**
     * @return the minX
     */
    public Double getMinX() {
        return this.minX;
    }

    /**
     * @return the minY
     */
    public Double getMinY() {
        return this.minY;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the originalXmlMetadata
     */
    public String getOriginalXmlMetadata() {
        return this.originalXmlMetadata;
    }

    /**
     * @return the originator
     */
    public String getOriginator() {
        return this.originator;
    }

    /**
     * @return the placeKeywords
     */
    public String getPlaceKeywords() {
        return this.placeKeywords;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * @return the themeKeywords
     */
    public String getThemeKeywords() {
        return this.themeKeywords;
    }

    /**
     * @return the workspaceName
     */
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    /**
     * @param access
     *            the access to set
     */
    public void setAccess(final String access) {
        this.access = access;
    }

    /**
     * @param area
     *            the area to set
     */
    public void setArea(final Double area) {
        this.area = area;
    }

    /**
     * @param availability
     *            the availability to set
     */
    public void setAvailability(final String availability) {
        this.availability = availability;
    }

    /**
     * @param centerX
     *            the centerX to set
     */
    public void setCenterX(final Double centerX) {
        this.centerX = centerX;
    }

    /**
     * @param centerY
     *            the centerY to set
     */
    public void setCenterY(final Double centerY) {
        this.centerY = centerY;
    }

    /**
     * @param collectionId
     *            the collectionId to set
     */
    public void setCollectionId(final String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param contentDate
     *            the contentDate to set
     */
    public void setContentDate(final Date contentDate) {
        this.contentDate = contentDate;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param externalLayerId
     *            the externalLayerId to set
     */
    public void setExternalLayerId(final String externalLayerId) {
        this.externalLayerId = externalLayerId;
    }

    /**
     * @param fgdcText
     *            the fgdcText to set
     */
    public void setFgdcText(final String fgdcText) {
        this.fgdcText = fgdcText;
    }

    /**
     * @param georeferenced
     *            the georeferenced to set
     */
    public void setGeoreferenced(final Boolean georeferenced) {
        this.georeferenced = georeferenced;
    }

    /**
     * @param halfHeight
     *            the halfHeight to set
     */
    public void setHalfHeight(final Double halfHeight) {
        this.halfHeight = halfHeight;
    }

    /**
     * @param halfWidth
     *            the halfWidth to set
     */
    public void setHalfWidth(final Double halfWidth) {
        this.halfWidth = halfWidth;
    }

    /**
     * @param institution
     *            the institution to set
     */
    public void setInstitution(final String institution) {
        this.institution = institution;
    }

    /**
     * @param layerDisplayName
     *            the layerDisplayName to set
     */
    public void setLayerDisplayName(final String layerDisplayName) {
        this.layerDisplayName = layerDisplayName;
    }

    /**
     * @param layerId
     *            the layerId to set
     */
    public void setLayerId(final String layerId) {
        this.layerId = layerId;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @param maxX
     *            the maxX to set
     */
    public void setMaxX(final Double maxX) {
        this.maxX = maxX;
    }

    /**
     * @param maxY
     *            the maxY to set
     */
    public void setMaxY(final Double maxY) {
        this.maxY = maxY;
    }

    /**
     * @param minX
     *            the minX to set
     */
    public void setMinX(final Double minX) {
        this.minX = minX;
    }

    /**
     * @param minY
     *            the minY to set
     */
    public void setMinY(final Double minY) {
        this.minY = minY;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param originalXmlMetadata
     *            the originalXmlMetadata to set
     */
    public void setOriginalXmlMetadata(final String originalXmlMetadata) {
        this.originalXmlMetadata = originalXmlMetadata;
    }

    /**
     * @param originator
     *            the originator to set
     */
    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    /**
     * @param placeKeywords
     *            the placeKeywords to set
     */
    public void setPlaceKeywords(final String placeKeywords) {
        this.placeKeywords = placeKeywords;
    }

    /**
     * @param publisher
     *            the publisher to set
     */
    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    /**
     * @param themeKeywords
     *            the themeKeywords to set
     */
    public void setThemeKeywords(final String themeKeywords) {
        this.themeKeywords = themeKeywords;
    }

    /**
     * @return the topicCategory
     */
    /*
     * public String getTopicCategory() { return topicCategory; }
     */

    /**
     * @param topicCategory
     *            the topicCategory to set
     */
    /*
     * public void setTopicCategory(String topicCategory) { this.topicCategory =
     * topicCategory; }
     */

    /**
     * @param workspaceName
     *            the workspaceName to set
     */
    public void setWorkspaceName(final String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("LayerId", this.layerId);
        map.put("LayerName", this.name);
        map.put("Title", this.layerDisplayName);
        map.put("DataType", this.dataType);
        map.put("Access", this.access);
        map.put("ContentDate",
                this.contentDate == null ? null : this.contentDate.toString());
        map.put("Bounds", this.minX + "," + this.minY + "," + this.maxX + ","
                + this.maxY);
        map.put("Originator", this.originator);
        map.put("Publisher", this.publisher);
        // map.put("TopicCategory", this.topicCategory);
        return map;
    }

    @Override
    public String toString() {
        final Map<String, String> map = this.toMap();
        String s = "";
        for (final String key : map.keySet()) {
            s += key;
            s += ": ";
            s += map.get(key);
            s += ",";
        }
        return s;
    }
}
