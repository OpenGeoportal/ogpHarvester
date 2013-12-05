package org.opengeoportal.harvester.api.metadata.parser;

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.PlaceKeywords;
import org.opengeoportal.harvester.api.metadata.model.ThemeKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;


public class Iso19139MetadataParser extends BaseXmlMetadataParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static enum Iso19139Tag implements Tag {
        Title("title", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"),
        Abstract("abstract", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString"),
        LayerName("fileIdentifier", "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"),
        Date("date", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:DateTime"),
        DataType("spatialRepresentationType", "/gmd:MD_Metadata//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode/@codeListValue"),
        WestBc("westBoundLongitude", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal"),
        EastBc("eastBoundLongitude", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal"),
        NorthBc("northBoundLatitude", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal"),
        SouthBc("southBoundLatitude", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal"),
        TopicCat("topicCategory", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode"),
        Access("accessConstraints", "/gmd:MD_Metadata/gmd:identificationInfo/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue");

        private final String tagName;
        private final String xPath; // XML xpath

        Iso19139Tag(String tagName, String xPath) {
            this.tagName = tagName;
            this.xPath = xPath;
        }

        public String getTagName() {
            return tagName;
        }

        public String getXPathName() {
            return xPath;
        }
    }


    @Override
    void handleOriginator() {
        // TODO: Implement
        //prefer CI_RoleCode originator, author, principalInvestigator, owner
        /* Xml to parse:
         * <gmd:contact>
            <gmd:CI_ResponsibleParty>
              <gmd:individualName>
                <gco:CharacterString>Francesca Perez</gco:CharacterString>
              </gmd:individualName>
              <gmd:organisationName>
                <gco:CharacterString>ITHACA - Information Technology for Humanitarian Assistance, Cooperation and Action</gco:CharacterString>
              </gmd:organisationName>
              <gmd:positionName gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:positionName>
              <gmd:contactInfo>
                <gmd:CI_Contact>
                  <gmd:phone>
                    <gmd:CI_Telephone>
                      <gmd:voice gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:voice>
                      <gmd:facsimile gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:facsimile>
                    </gmd:CI_Telephone>
                  </gmd:phone>
                  <gmd:address>
                    <gmd:CI_Address>
                      <gmd:deliveryPoint gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:deliveryPoint>
                      <gmd:city gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:city>
                      <gmd:administrativeArea gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:administrativeArea>
                      <gmd:postalCode gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:postalCode>
                      <gmd:country gco:nilReason="missing">
                        <gco:CharacterString />
                      </gmd:country>
                      <gmd:electronicMailAddress>
                        <gco:CharacterString>francesca.perez@polito.it</gco:CharacterString>
                      </gmd:electronicMailAddress>
                    </gmd:CI_Address>
                  </gmd:address>
                </gmd:CI_Contact>
              </gmd:contactInfo>
              <gmd:role>
                <gmd:CI_RoleCode codeListValue="author" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />
              </gmd:role>
            </gmd:CI_ResponsibleParty>
          </gmd:contact>
         */
    }

    @Override
    void handlePublisher() {
        // TODO: Implement
    }

    @Override
    void handleLayerName() {
        /* Xml to parse:
         * <gmd:fileIdentifier xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gmx="http://www.isotc211.org/2005/gmx">
            <gco:CharacterString>11334f95-ceee-44d9-b2f8-cd9daf08c427</gco:CharacterString>
           </gmd:fileIdentifier>
         */
        Tag tag = Iso19139Tag.LayerName;
        try {
            this.metadataParserResponse.getMetadata().setOwsName(getDocumentValue(tag));
        } catch (Exception e) {
            logger.error("handleLayerName: " + e.getMessage());
            this.metadataParserResponse.addError(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleAbstract() {
        /* Xml to parse:
         * <gmd:abstract>
            <gco:CharacterString>Global Volcano Proportional Economic Loss Risk Deciles is a 2.5 by 2.5 minute grid of volcano hazard economic loss as proportions of gross domestic product (GDP) per analytical unit. Estimates of GDP at risk are based on regional economic loss rates derived from historical records of the Emergency Events Database (EM-DAT). Loss rates are weighted by the hazard's frequency and distribution. The methodology of Sachs et al. (2003) is followed to determine baseline estimates of GDP per grid cell. To better reflect the confidence surrounding the data and procedures, the range of proportionalities is classified into deciles, 10 class of an approximately equal number of grid cells of increasing risk. The dataset is a result of the collaboration among the Center for Hazards and Risk Research (CHRR), International Bank for Reconstruction and Development/The World Bank, and the Columbia University Center for International Earth Science Information Network (CIESIN).
            The Center for Hazards and Risk Research (CHRR) draws on Columbia's acknowledged expertise in Earth and environmental sciences, engineering, social sciences, public policy, public health and business. The mission is to advance the predictive science of natural and environmental hazards and the integration of science with hazard risk assessment and risk management. The CHRR produced the core dataset of Natural Disaster Hotspots – A global risk analysis, which contains 4 libraries of data: Hazard Frequency and/or Distribution; Hazard Mortality Risks and Distribution; Hazard Total Economic Loss Risk Deciles; Hazard Proportional Economic Loss Risk Deciles for each natural disaster.</gco:CharacterString>
           </gmd:abstract>
         */
        Tag tag = Iso19139Tag.Abstract;
        try {
            this.metadataParserResponse.getMetadata().setDescription(getDocumentValue(tag));
        } catch (Exception e) {
            logger.error("handleAbstract: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleTitle() {
       /* <gmd:title>
        <gco:CharacterString>Global Volcano Proportional Economic Loss Risk Deciles_CHRR</gco:CharacterString>
      </gmd:title>*/
        Tag tag = Iso19139Tag.Title;
        try {
            this.metadataParserResponse.getMetadata().setTitle(getDocumentValue(tag));
        } catch (Exception e) {
            logger.error("handleTitle: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleDate() {
        /* Xml to parse:
         * <gmd:date>
            <gmd:CI_Date>
              <gmd:date>
                <gco:DateTime>2005-03-03T09:00:00</gco:DateTime>
              </gmd:date>
              <gmd:dateType>
                <gmd:CI_DateTypeCode codeListValue="publication" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode" />
              </gmd:dateType>
            </gmd:CI_Date>
          </gmd:date>
         */
        Tag tag = Iso19139Tag.Date;
        try {
            String date = getDocumentValue(tag);

            if (StringUtils.isNotEmpty(date) && (date.length() >= 4)) {
                this.metadataParserResponse.getMetadata().setContentDate(getDocumentValue(tag).substring(0, 4));
            }
        } catch (Exception e) {
            logger.error("handleDate: " + e.getMessage());
            this.metadataParserResponse.addWarning("date", "date", e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleDataType() {
        /* Xml to parse:
         * <gmd:MD_SpatialRepresentationTypeCode codeListValue="grid" codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_SpatialRepresentationTypeCode" />
         */
        GeometryType geomType = null;
        String dataType = null;
        Tag tag = Iso19139Tag.DataType;
        try {
            dataType = getDocumentValue(tag);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (dataType != null) {
            if (dataType.equalsIgnoreCase("grid"))
                geomType = GeometryType.Raster;
            else if (dataType.equalsIgnoreCase("tin"))
                geomType = GeometryType.Polygon;
            else if (dataType.equalsIgnoreCase("vector"))
                geomType = GeometryType.Line;
            else
                geomType = GeometryType.Undefined;
        }

        try {
            this.metadataParserResponse.getMetadata().setGeometryType(geomType);
        } catch (Exception e) {
            logger.error("handleDataType: " + e.getMessage());
            this.metadataParserResponse.addError("DataType", "MD_SpatialRepresentationTypeCode", e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleAccess() {
        /* Xml to parse:
         * <gmd:resourceConstraints>
            <gmd:MD_LegalConstraints>
              <gmd:accessConstraints>
                <gmd:MD_RestrictionCode codeListValue="" codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_RestrictionCode" />
              </gmd:accessConstraints>
              <gmd:useConstraints>
                <gmd:MD_RestrictionCode codeListValue="intellectualPropertyRights" codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_RestrictionCode" />
              </gmd:useConstraints>
              <gmd:otherConstraints>
                <gco:CharacterString>The Trustees of Columbia University in the City of New York, the Center for Hazards and Risk Research (CHRR), and the Center for International Earth Science Information Network (CIESIN) hold the copyright of this dataset. Users are prohibited from any commercial, non-free resale, or redistribution without explicit written permission from CHRR or CIESIN. Use of this data set is restricted to scientific research only. Users should acknowledge CHRR and CIESIN as the source used in the creation of any reports, publications, new data sets, derived products, or services resulting from the use of this data set. CHRR and CIESIN also request reprints of any publications and notification of any redistribution efforts.</gco:CharacterString>
              </gmd:otherConstraints>
            </gmd:MD_LegalConstraints>
          </gmd:resourceConstraints>
        */
        Tag tag = Iso19139Tag.Access;
        try {
            String accessValue$ = "";
            try {
                accessValue$ = getDocumentValue(tag);
            } catch (Exception e) {
                AccessLevel nullAccess = null;
                this.metadataParserResponse.getMetadata().setAccessLevel(nullAccess);
                return;
            }
            AccessLevel accessValue = AccessLevel.Public;
            accessValue$ = accessValue$.toLowerCase();
            if (accessValue$.startsWith("restricted")) {
                accessValue = AccessLevel.Restricted;
            }
            this.metadataParserResponse.getMetadata().setAccessLevel(accessValue);
        } catch (Exception e) {
            logger.error("handleAccess: " + e.getMessage());
            this.metadataParserResponse.addError(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleKeywords() {
        /* Xml to parse (topic category):
         * <gmd:topicCategory>
            <gmd:MD_TopicCategoryCode>environment</gmd:MD_TopicCategoryCode>
           </gmd:topicCategory>

           Xml to parse (keywords):

            <gmd:descriptiveKeywords>
                <gmd:MD_Keywords>
                  <gmd:keyword>
                    <gco:CharacterString>Natural risk zones</gco:CharacterString>
                  </gmd:keyword>
                  <gmd:type>
                    <gmd:MD_KeywordTypeCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode" codeListValue="theme" />
                  </gmd:type>
                  <gmd:thesaurusName>
                    <gmd:CI_Citation>
                      <gmd:title>
                        <gco:CharacterString>GEMET - INSPIRE themes, version 1.0</gco:CharacterString>
                      </gmd:title>
                      <gmd:date gco:nilReason="unknown" />
                    </gmd:CI_Citation>
                  </gmd:thesaurusName>
                </gmd:MD_Keywords>
            </gmd:descriptiveKeywords>
         */

        List<PlaceKeywords> placeKeywordsList = new ArrayList<PlaceKeywords>();
        List<ThemeKeywords> themeKeywordsList = new ArrayList<ThemeKeywords>();

        Tag tag = Iso19139Tag.TopicCat;
        try {
            String topicCategoryVal = getDocumentValue(tag);
            if (!StringUtils.isEmpty(topicCategoryVal)) {
                ThemeKeywords themeKeyword = new ThemeKeywords();
                themeKeyword.addKeyword(topicCategoryVal);

                themeKeywordsList.add(themeKeyword);
            }
        } catch (Exception e) {
            logger.error("handleKeywords: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }

        try {
            String keywordsXPath = "/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords";

            NodeList keywordNodes = (NodeList) xPath.evaluate(keywordsXPath, document, XPathConstants.NODESET);

            for(int i = 0 ; i < keywordNodes.getLength(); i++) {
                Node keyword = keywordNodes.item(i);

                String keywordVal = (String) xPath.evaluate("gmd:keyword/gco:CharacterString", keyword, XPathConstants.STRING);
                String keywordType = (String) xPath.evaluate("gmd:type/gmd:MD_KeywordTypeCode/@codeListValue", keyword, XPathConstants.STRING);
                if (keywordType.equals("place")) {
                    PlaceKeywords placeKeyword = new PlaceKeywords();
                    placeKeyword.addKeyword(keywordVal);

                    placeKeywordsList.add(placeKeyword);
                } else  if (keywordType.equals("theme")) {
                    ThemeKeywords themeKeyword = new ThemeKeywords();
                    themeKeyword.addKeyword(keywordVal);
                    themeKeywordsList.add(themeKeyword);
                }
            }

        } catch (Exception e) {
            logger.error("handleKeywords: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }

        this.metadataParserResponse.getMetadata().setPlaceKeywords(placeKeywordsList);
        this.metadataParserResponse.getMetadata().setThemeKeywords(themeKeywordsList);
    }

    @Override
    void handleBounds() {
        /* Xml to parse:
         * <gmd:extent>
            <gmd:EX_Extent>
              <gmd:geographicElement>
                <gmd:EX_GeographicBoundingBox>
                  <gmd:westBoundLongitude>
                    <gco:Decimal>-180</gco:Decimal>
                  </gmd:westBoundLongitude>
                  <gmd:eastBoundLongitude>
                    <gco:Decimal>180</gco:Decimal>
                  </gmd:eastBoundLongitude>
                  <gmd:southBoundLatitude>
                    <gco:Decimal>-90</gco:Decimal>
                  </gmd:southBoundLatitude>
                  <gmd:northBoundLatitude>
                    <gco:Decimal>90</gco:Decimal>
                  </gmd:northBoundLatitude>
                </gmd:EX_GeographicBoundingBox>
              </gmd:geographicElement>
            </gmd:EX_Extent>
            </gmd:extent>
         */
        Tag tag = Iso19139Tag.NorthBc;
        try {
            String maxY = getDocumentValue(tag);
            tag = Iso19139Tag.EastBc;
            String maxX = getDocumentValue(tag);
            tag = Iso19139Tag.SouthBc;
            String minY = getDocumentValue(tag);
            tag = Iso19139Tag.WestBc;
            String minX = getDocumentValue(tag);
            //should validate bounds here
            if (validateBounds(minX, minY, maxX, maxY)) {
                this.metadataParserResponse.getMetadata().setBounds(minX, minY, maxX, maxY);
            } else {
                throw new Exception("Invalid Bounds: " + minX + "," + minY + "," + maxX + "," + maxY);
            }
        } catch (Exception e) {
            logger.error("handleBounds: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(), tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    void handleFullText() {
        this.metadataParserResponse.getMetadata().setFullText(getFullText());
    }
}
