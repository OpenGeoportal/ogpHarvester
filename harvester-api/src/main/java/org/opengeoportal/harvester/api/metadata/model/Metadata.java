package org.opengeoportal.harvester.api.metadata.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Metadata {

    private BoundingBox bounds;
    private String id = "";
    private String title = "";
    private String description = "";
    private String owsName = "";
    private String workspaceName = "";
    private String location = "";
    private String originator = "";
    private List<ThemeKeywords> themeKeywords;
    private List<PlaceKeywords> placeKeywords;
    private String institution = "";
    private String fullText = "";
    private AccessLevel access;
    private GeometryType geometryType;
    private String publisher = "";
    private Boolean georeferenced;
    private Date contentDate;
    private String topic = "";
    private boolean restricted = false;
    private String externalId = "";
    private String collectionId = "";
    /**
     * Raw metadata as retrieved from remote source.
     */
    private String originalMetadata = "";

    public Metadata(String layerId) {
        this.id = layerId;
        this.themeKeywords = new ArrayList<ThemeKeywords>();
        this.placeKeywords = new ArrayList<PlaceKeywords>();
        this.georeferenced = Boolean.FALSE;

    }

    public Metadata() {
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

    public void setAccessLevel(String accessString) {
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

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Boolean getGeoreferenced() {
        return georeferenced;
    }

    public void setGeoreferenced(Boolean georeferenced) {
        this.georeferenced = georeferenced;
    }

    public Date getContentDate() {
        return contentDate;
    }

    public void setContentDate(Date contentDate) {
        this.contentDate = contentDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getThemeKeywordsAsString() {
        Set<String> allKeywords = new HashSet<String>();
        for (ThemeKeywords themeKeyword : this.themeKeywords) {
            allKeywords.addAll(themeKeyword.getKeywords());
        }
        return combine(allKeywords.toArray(new String[allKeywords.size()]), " ");
    }

    public String getPlaceKeywordsAsString() {
        Set<String> allKeywords = new HashSet<String>();
        for (PlaceKeywords placeKeyword : this.placeKeywords) {
            allKeywords.addAll(placeKeyword.getKeywords());
        }
        return combine(allKeywords.toArray(new String[allKeywords.size()]), " ");
    }

    public boolean hasValueForProperty(String property) {
        String value = "";

        if (property.equals("dateOfContent")) {
            value = ((this.getContentDate() != null) ? this.getContentDate().toString() : "");
        } else if (property.equals("originator")) {
            value = this.getOriginator();
        } else if (property.equals("themeKeyword")) {
            value = this.getThemeKeywordsAsString();
        } else if (property.equals("placeKeyword")) {
            value = this.getPlaceKeywordsAsString();
        } else if (property.equals("topic")) {
            value = this.getTopic();
        } else if (property.equals("dataRepository")) {
            value = this.getInstitution();
        } else if (property.equals("webServices")) {
            value = this.getLocation();
        } else if (property.equals("geographicExtent")) {
            if (this.getBounds() == null) {
                return false;
            } else {
                return this.getBounds().isValid();
            }
        } else if (property.equals("dataType")) {
            GeometryType geometry = this.getGeometryType();
            return (geometry != null);
        }

        return StringUtils.isNotEmpty(value);
    }

    private String combine(String[] s, String glue) {
        int k = s.length;
        if (k == 0) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }

    /**
     * @return the originalMetadata.
     */
    public String getOriginalMetadata() {
        return originalMetadata;
    }

    /**
     * @param originalMetadata the originalMetadata to set.
     */
    public void setOriginalMetadata(String originalMetadata) {
        this.originalMetadata = originalMetadata;
    }

    /**
     * @return the restricted.
     */
    public boolean isRestricted() {
        return restricted;
    }
    
    @Override
    public String toString() {
        return "Metadata [bounds=" + bounds + ", id=" + id + ", title=" + title
                + ", description=" + description + ", owsName=" + owsName
                + ", workspaceName=" + workspaceName + ", location=" + location
                + ", originator=" + originator + ", themeKeywords="
                + themeKeywords + ", placeKeywords=" + placeKeywords
                + ", institution=" + institution + ", fullText=" + fullText
                + ", access=" + access + ", geometryType=" + geometryType
                + ", publisher=" + publisher + ", georeferenced="
                + georeferenced + ", contentDate=" + contentDate + ", topic="
                + topic + ", restricted=" + restricted + ", externalId="
                + externalId + ", collectionId=" + collectionId
                + ", originalMetadata=" + originalMetadata + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((access == null) ? 0 : access.hashCode());
        result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
        result = prime * result
                + ((collectionId == null) ? 0 : collectionId.hashCode());
        result = prime * result
                + ((contentDate == null) ? 0 : contentDate.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((externalId == null) ? 0 : externalId.hashCode());
        result = prime * result
                + ((fullText == null) ? 0 : fullText.hashCode());
        result = prime * result
                + ((geometryType == null) ? 0 : geometryType.hashCode());
        result = prime * result
                + ((georeferenced == null) ? 0 : georeferenced.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((institution == null) ? 0 : institution.hashCode());
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((originalMetadata == null) ? 0
                : originalMetadata.hashCode());
        result = prime * result
                + ((originator == null) ? 0 : originator.hashCode());
        result = prime * result + ((owsName == null) ? 0 : owsName.hashCode());
        result = prime * result
                + ((placeKeywords == null) ? 0 : placeKeywords.hashCode());
        result = prime * result
                + ((publisher == null) ? 0 : publisher.hashCode());
        result = prime * result + (restricted ? 1231 : 1237);
        result = prime * result
                + ((themeKeywords == null) ? 0 : themeKeywords.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result
                + ((workspaceName == null) ? 0 : workspaceName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Metadata other = (Metadata) obj;
        if (access != other.access)
            return false;
        if (bounds == null) {
            if (other.bounds != null)
                return false;
        } else if (!bounds.equals(other.bounds))
            return false;
        if (collectionId == null) {
            if (other.collectionId != null)
                return false;
        } else if (!collectionId.equals(other.collectionId))
            return false;
        if (contentDate == null) {
            if (other.contentDate != null)
                return false;
        } else if (!contentDate.equals(other.contentDate))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
        if (fullText == null) {
            if (other.fullText != null)
                return false;
        } else if (!fullText.equals(other.fullText))
            return false;
        if (geometryType != other.geometryType)
            return false;
        if (georeferenced == null) {
            if (other.georeferenced != null)
                return false;
        } else if (!georeferenced.equals(other.georeferenced))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (institution == null) {
            if (other.institution != null)
                return false;
        } else if (!institution.equals(other.institution))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (originalMetadata == null) {
            if (other.originalMetadata != null)
                return false;
        } else if (!originalMetadata.equals(other.originalMetadata))
            return false;
        if (originator == null) {
            if (other.originator != null)
                return false;
        } else if (!originator.equals(other.originator))
            return false;
        if (owsName == null) {
            if (other.owsName != null)
                return false;
        } else if (!owsName.equals(other.owsName))
            return false;
        if (placeKeywords == null) {
            if (other.placeKeywords != null)
                return false;
        } else if (!placeKeywords.equals(other.placeKeywords))
            return false;
        if (publisher == null) {
            if (other.publisher != null)
                return false;
        } else if (!publisher.equals(other.publisher))
            return false;
        if (restricted != other.restricted)
            return false;
        if (themeKeywords == null) {
            if (other.themeKeywords != null)
                return false;
        } else if (!themeKeywords.equals(other.themeKeywords))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        if (workspaceName == null) {
            if (other.workspaceName != null)
                return false;
        } else if (!workspaceName.equals(other.workspaceName))
            return false;
        return true;
    }
    
    
}
