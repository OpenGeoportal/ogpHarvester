package org.opengeoportal.harvester.api.metadata.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
    private final boolean restricted = false;
    private String externalId = "";
    private String collectionId = "";
    /**
     * Raw metadata as retrieved from remote source.
     */
    private String originalMetadata = "";

    public Metadata() {
        this(null);
    }

    public Metadata(final String layerId) {
        this.id = layerId;
        this.themeKeywords = new ArrayList<ThemeKeywords>();
        this.placeKeywords = new ArrayList<PlaceKeywords>();
        this.georeferenced = Boolean.FALSE;

    }

    private String combine(final String[] s, final String glue) {
        final int k = s.length;
        if (k == 0) {
            return null;
        }
        final StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Metadata other = (Metadata) obj;
        if (this.access != other.access) {
            return false;
        }
        if (this.bounds == null) {
            if (other.bounds != null) {
                return false;
            }
        } else if (!this.bounds.equals(other.bounds)) {
            return false;
        }
        if (this.collectionId == null) {
            if (other.collectionId != null) {
                return false;
            }
        } else if (!this.collectionId.equals(other.collectionId)) {
            return false;
        }
        if (this.contentDate == null) {
            if (other.contentDate != null) {
                return false;
            }
        } else if (!this.contentDate.equals(other.contentDate)) {
            return false;
        }
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.externalId == null) {
            if (other.externalId != null) {
                return false;
            }
        } else if (!this.externalId.equals(other.externalId)) {
            return false;
        }
        if (this.fullText == null) {
            if (other.fullText != null) {
                return false;
            }
        } else if (!this.fullText.equals(other.fullText)) {
            return false;
        }
        if (this.geometryType != other.geometryType) {
            return false;
        }
        if (this.georeferenced == null) {
            if (other.georeferenced != null) {
                return false;
            }
        } else if (!this.georeferenced.equals(other.georeferenced)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.institution == null) {
            if (other.institution != null) {
                return false;
            }
        } else if (!this.institution.equals(other.institution)) {
            return false;
        }
        if (this.location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!this.location.equals(other.location)) {
            return false;
        }
        if (this.originalMetadata == null) {
            if (other.originalMetadata != null) {
                return false;
            }
        } else if (!this.originalMetadata.equals(other.originalMetadata)) {
            return false;
        }
        if (this.originator == null) {
            if (other.originator != null) {
                return false;
            }
        } else if (!this.originator.equals(other.originator)) {
            return false;
        }
        if (this.owsName == null) {
            if (other.owsName != null) {
                return false;
            }
        } else if (!this.owsName.equals(other.owsName)) {
            return false;
        }
        if (this.placeKeywords == null) {
            if (other.placeKeywords != null) {
                return false;
            }
        } else if (!this.placeKeywords.equals(other.placeKeywords)) {
            return false;
        }
        if (this.publisher == null) {
            if (other.publisher != null) {
                return false;
            }
        } else if (!this.publisher.equals(other.publisher)) {
            return false;
        }
        if (this.restricted != other.restricted) {
            return false;
        }
        if (this.themeKeywords == null) {
            if (other.themeKeywords != null) {
                return false;
            }
        } else if (!this.themeKeywords.equals(other.themeKeywords)) {
            return false;
        }
        if (this.title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!this.title.equals(other.title)) {
            return false;
        }
        if (this.topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!this.topic.equals(other.topic)) {
            return false;
        }
        if (this.workspaceName == null) {
            if (other.workspaceName != null) {
                return false;
            }
        } else if (!this.workspaceName.equals(other.workspaceName)) {
            return false;
        }
        return true;
    }

    public AccessLevel getAccess() {
        return this.access;
    }

    public BoundingBox getBounds() {
        return this.bounds;
    }

    public String getCollectionId() {
        return this.collectionId;
    }

    public Date getContentDate() {
        return this.contentDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getFullText() {
        return this.fullText;
    }

    public GeometryType getGeometryType() {
        return this.geometryType;
    }

    public Boolean getGeoreferenced() {
        return this.georeferenced;
    }

    public String getId() {
        return this.id;
    }

    public String getInstitution() {
        return this.institution;
    }

    public String getLocation() {
        return this.location;
    }

    /**
     * @return the originalMetadata.
     */
    public String getOriginalMetadata() {
        return this.originalMetadata;
    }

    public String getOriginator() {
        return this.originator;
    }

    public String getOwsName() {
        return this.owsName;
    }

    public List<PlaceKeywords> getPlaceKeywords() {
        return this.placeKeywords;
    }

    public String getPlaceKeywordsAsString() {
        final Set<String> allKeywords = new HashSet<String>();
        for (final PlaceKeywords placeKeyword : this.placeKeywords) {
            allKeywords.addAll(placeKeyword.getKeywords());
        }
        return this.combine(allKeywords.toArray(new String[allKeywords.size()]),
                " ");
    }

    public String getPublisher() {
        return this.publisher;
    }

    public List<ThemeKeywords> getThemeKeywords() {
        return this.themeKeywords;
    }

    public String getThemeKeywordsAsString() {
        final Set<String> allKeywords = new HashSet<String>();
        for (final ThemeKeywords themeKeyword : this.themeKeywords) {
            allKeywords.addAll(themeKeyword.getKeywords());
        }
        return this.combine(allKeywords.toArray(new String[allKeywords.size()]),
                " ");
    }

    public String getTitle() {
        return this.title;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getWorkspaceName() {
        return this.workspaceName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((this.access == null) ? 0 : this.access.hashCode());
        result = (prime * result)
                + ((this.bounds == null) ? 0 : this.bounds.hashCode());
        result = (prime * result) + ((this.collectionId == null) ? 0
                : this.collectionId.hashCode());
        result = (prime * result) + ((this.contentDate == null) ? 0
                : this.contentDate.hashCode());
        result = (prime * result) + ((this.description == null) ? 0
                : this.description.hashCode());
        result = (prime * result)
                + ((this.externalId == null) ? 0 : this.externalId.hashCode());
        result = (prime * result)
                + ((this.fullText == null) ? 0 : this.fullText.hashCode());
        result = (prime * result) + ((this.geometryType == null) ? 0
                : this.geometryType.hashCode());
        result = (prime * result) + ((this.georeferenced == null) ? 0
                : this.georeferenced.hashCode());
        result = (prime * result)
                + ((this.id == null) ? 0 : this.id.hashCode());
        result = (prime * result) + ((this.institution == null) ? 0
                : this.institution.hashCode());
        result = (prime * result)
                + ((this.location == null) ? 0 : this.location.hashCode());
        result = (prime * result) + ((this.originalMetadata == null) ? 0
                : this.originalMetadata.hashCode());
        result = (prime * result)
                + ((this.originator == null) ? 0 : this.originator.hashCode());
        result = (prime * result)
                + ((this.owsName == null) ? 0 : this.owsName.hashCode());
        result = (prime * result) + ((this.placeKeywords == null) ? 0
                : this.placeKeywords.hashCode());
        result = (prime * result)
                + ((this.publisher == null) ? 0 : this.publisher.hashCode());
        result = (prime * result) + (this.restricted ? 1231 : 1237);
        result = (prime * result) + ((this.themeKeywords == null) ? 0
                : this.themeKeywords.hashCode());
        result = (prime * result)
                + ((this.title == null) ? 0 : this.title.hashCode());
        result = (prime * result)
                + ((this.topic == null) ? 0 : this.topic.hashCode());
        result = (prime * result) + ((this.workspaceName == null) ? 0
                : this.workspaceName.hashCode());
        return result;
    }

    public boolean hasValueForProperty(final String property) {
        String value = "";

        if (property.equals("dateOfContent")) {
            value = ((this.getContentDate() != null)
                    ? this.getContentDate().toString() : "");
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
            final GeometryType geometry = this.getGeometryType();
            return (geometry != null);
        }

        return StringUtils.isNotEmpty(value);
    }

    /**
     * @return the restricted.
     */
    public boolean isRestricted() {
        return this.restricted;
    }

    public void setAccess(final AccessLevel access) {
        this.access = access;
    }

    public void setAccessLevel(final AccessLevel access) {
        this.access = access;
    }

    public void setAccessLevel(final String accessString) {
        this.access = AccessLevel.parseString(accessString);
    }

    public void setBounds(final BoundingBox bounds) {
        this.bounds = bounds;
    }

    public void setBounds(final Double minX, final Double minY,
            final Double maxX, final Double maxY) {
        this.bounds = new BoundingBox(minX, minY, maxX, maxY);
    }

    public void setBounds(final String minX, final String minY,
            final String maxX, final String maxY) {
        this.bounds = new BoundingBox(minX, minY, maxX, maxY);
    }

    public void setCollectionId(final String collectionId) {
        this.collectionId = collectionId;
    }

    public void setContentDate(final Date contentDate) {
        this.contentDate = contentDate;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public void setFullText(final String fullText) {
        this.fullText = fullText;
    }

    public void setGeometryType(final GeometryType geometryType) {
        this.geometryType = geometryType;
    }

    public void setGeometryType(final String geometryTypeString) {
        this.geometryType = GeometryType.parseGeometryType(geometryTypeString);
    }

    public void setGeoreferenced(final Boolean georeferenced) {
        this.georeferenced = georeferenced;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setInstitution(final String institution) {
        this.institution = institution;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @param originalMetadata
     *            the originalMetadata to set.
     */
    public void setOriginalMetadata(final String originalMetadata) {
        this.originalMetadata = originalMetadata;
    }

    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    public void setOwsName(final String owsName) {
        this.owsName = owsName;
    }

    public void setPlaceKeywords(final List<PlaceKeywords> placeKeywords) {
        this.placeKeywords = placeKeywords;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public void setThemeKeywords(final List<ThemeKeywords> themeKeywords) {
        this.themeKeywords = themeKeywords;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public void setWorkspaceName(final String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String toString() {
        return "Metadata [bounds=" + this.bounds + ", id=" + this.id
                + ", title=" + this.title + ", description=" + this.description
                + ", owsName=" + this.owsName + ", workspaceName="
                + this.workspaceName + ", location=" + this.location
                + ", originator=" + this.originator + ", themeKeywords="
                + this.themeKeywords + ", placeKeywords=" + this.placeKeywords
                + ", institution=" + this.institution + ", fullText="
                + this.fullText + ", access=" + this.access + ", geometryType="
                + this.geometryType + ", publisher=" + this.publisher
                + ", georeferenced=" + this.georeferenced + ", contentDate="
                + this.contentDate + ", topic=" + this.topic + ", restricted="
                + this.restricted + ", externalId=" + this.externalId
                + ", collectionId=" + this.collectionId + ", originalMetadata="
                + this.originalMetadata + "]";
    }

}
