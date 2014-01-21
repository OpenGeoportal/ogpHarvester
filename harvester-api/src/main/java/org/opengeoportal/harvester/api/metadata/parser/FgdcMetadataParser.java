package org.opengeoportal.harvester.api.metadata.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.PlaceKeywords;
import org.opengeoportal.harvester.api.metadata.model.ThemeKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FgdcMetadataParser extends BaseXmlMetadataParser {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static enum FgdcTag implements Tag {
		Title("title", "/metadata/idinfo/citation/citeinfo/title"), Abstract(
				"abstract", "/metadata/idinfo/descript/abstract"), LayerName(
				"ftname", "/metadata/idinfo/citation/citeinfo/ftname"), Publisher(
				"publish", "/metadata/idinfo/citation/citeinfo/pubinfo/publish"), Originator(
				"origin", "/metadata/idinfo/citation/citeinfo/origin"), WestBc(
				"westbc", "/metadata/idinfo/spdom/bounding/westbc"), EastBc(
				"eastbc", "/metadata/idinfo/spdom/bounding/eastbc"), NorthBc(
				"northbc", "/metadata/idinfo/spdom/bounding/northbc"), SouthBc(
				"southbc", "/metadata/idinfo/spdom/bounding/southbc"), Access(
				"accconst", "/metadata/idinfo/accconst"),
		// Keywords
		PlaceKeywordsHeader("place", "/metadata/idinfo/keywords/place"), ThemeKeywordsHeader(
				"theme", "/metadata/idinfo/keywords/theme"), PlaceKeywordsThesaurus(
				"placekt", "/metadata/idinfo/keywords/place/placekt"), ThemeKeywordsThesaurus(
				"themekt", "/metadata/idinfo/keywords/theme/placekt"), PlaceKeywords(
				"placekey", "placekey"), // relative xpath
		ThemeKeywords("themekey", "themekey"), // relative xpath
		// Data type
		DataType_Srccitea("srccitea", "/metadata/spdoinfo/srccitea"), // TODO:
																		// Verify
																		// xpath
		DataType_Direct("direct", "/metadata/spdoinfo/direct"), DataType_Sdtstype(
				"sdtstype", "/metadata/spdoinfo/ptvctinf/sdtsterm/sdtstype"), Date_Caldate(
				"caldate", "/metadata/idinfo/timeperd/timeinfo/sngdate/caldate"), Date_Begdate(
				"begdate",
				"/metadata/idinfo/timeperd/timeinfo/rngdates/begdate"), Date_DateStamp(
				"dateStamp", "/metadata/idinfo/timeperd/timeinfo/dateStamp"); // TODO:
																				// Verify
																				// xpath;

		private final String tagName;
		private final String xPath; // XML xpath

		FgdcTag(String tagName, String xPath) {
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
	protected HashMap<String, String> getNamespaces() {
		return new HashMap<String, String>();
	}

	@Override
	protected void handleOriginator() {
		Tag tag = FgdcTag.Originator;
		try {
			this.metadataParserResponse.getMetadata().setOriginator(
					getDocumentValue(tag));
		} catch (Exception e) {
			logger.error("handleOriginator: " + e.getMessage());
			this.metadataParserResponse.addWarning(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handlePublisher() {
		Tag tag = FgdcTag.Publisher;
		try {
			this.metadataParserResponse.getMetadata().setPublisher(
					getDocumentValue(tag));
		} catch (Exception e) {
			logger.error("handlePublisher: " + e.getMessage());
			this.metadataParserResponse.addWarning(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleLayerName() {
		Tag tag = FgdcTag.LayerName;
		try {
			this.metadataParserResponse.getMetadata().setOwsName(
					getDocumentValue(tag));
		} catch (Exception e) {
			logger.error("handleLayerName: " + e.getMessage());
			this.metadataParserResponse.addError(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleAbstract() {
		Tag tag = FgdcTag.Abstract;
		try {
			this.metadataParserResponse.getMetadata().setDescription(
					getDocumentValue(tag));
		} catch (Exception e) {
			logger.error("handleAbstract: " + e.getMessage());
			this.metadataParserResponse.addWarning(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleTitle() {
		Tag tag = FgdcTag.Title;
		try {
			this.metadataParserResponse.getMetadata().setTitle(
					getDocumentValue(tag));
		} catch (Exception e) {
			logger.error("handleTitle: " + e.getMessage());
			this.metadataParserResponse.addWarning(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleDate() {
		String dateString = null;
		Date dateValue = null;
		try {
			dateString = getDocumentValue(FgdcTag.Date_Caldate);
		} catch (Exception e) {
			dateString = null;
		}

		if (StringUtils.isEmpty(dateString)) {
			try {
				dateString = getDocumentValue(FgdcTag.Date_Begdate);
			} catch (Exception e) {
				dateString = null;
			}
		}

		if (StringUtils.isEmpty(dateString)) {
			try {
				dateString = getDocumentValue(FgdcTag.Date_DateStamp);
			} catch (Exception e) {
				logger.warn("No valid Content Date could be found in the document.");
				this.metadataParserResponse.getMetadata().setContentDate(null);
				return;
			}
		}

		try {
			logger.debug("DATE VALUE#######:" + dateString);
			dateValue = processDateString(dateString);
			this.metadataParserResponse.getMetadata().setContentDate(dateValue);
		} catch (Exception e) {
			try {
				dateString = dateString.substring(0, 3);
				int dateValueInt = Integer.parseInt(dateString);
				dateString = Integer.toString(dateValueInt);
				if (dateString.length() == 4) {
					this.metadataParserResponse.getMetadata().setContentDate(
							processDateString(dateString));
				}
			} catch (Exception e1) {
				logger.warn("No valid Content Date could be found in the document.");
				this.metadataParserResponse.getMetadata().setContentDate(null);
			}
		}
	}

	@Override
	protected void handleDataType() {
		String direct = null; // raster?
		String sdtsType = null; // vector type
		String srcCiteA = null; // scanned map

		try {
			srcCiteA = getDocumentValue(FgdcTag.DataType_Srccitea);
			if (srcCiteA.equalsIgnoreCase("Paper Map")) {
				this.metadataParserResponse.getMetadata().setGeometryType(
						GeometryType.ScannedMap);
				return;
			}
		} catch (Exception e) {
			// just continue to next block
		}

		try {
			direct = getDocumentValue(FgdcTag.DataType_Direct);
			if (direct.equalsIgnoreCase("raster") == true) {
				this.metadataParserResponse.getMetadata().setGeometryType(
						GeometryType.Raster);
				return;
			}
		} catch (Exception e) {
			// again, move to the next block
		}

		try {
			sdtsType = getDocumentValue(FgdcTag.DataType_Sdtstype);
			GeometryType solrType;
			if (sdtsType.equals("G-polygon") || sdtsType.contains("olygon")
					|| sdtsType.contains("chain")) {
				solrType = GeometryType.Polygon;

			} else if (sdtsType.equals("Composite")
					|| sdtsType.contains("omposite")
					|| sdtsType.equals("Entity point")) {
				solrType = GeometryType.Point;

			} else if (sdtsType.equals("String")) {
				solrType = GeometryType.Line;

			} else {
				solrType = GeometryType.Undefined;
			}
			this.metadataParserResponse.getMetadata().setGeometryType(solrType);
		} catch (Exception e) {
			logger.error("null geometry type");
			GeometryType solrType = GeometryType.Undefined;
			this.metadataParserResponse.getMetadata().setGeometryType(solrType);
			// we should make a note if the geometry type is undefined
		}
	}

	@Override
	protected void handleAccess() {
		Tag tag = FgdcTag.Access;
		try {
			String accessValueMd = "";
			try {
				accessValueMd = getDocumentValue(tag);
			} catch (Exception e) {
				AccessLevel nullAccess = null;
				this.metadataParserResponse.getMetadata().setAccessLevel(
						nullAccess);
				return;
			}

			AccessLevel accessValue = AccessLevel.Public;
			accessValueMd = accessValueMd.toLowerCase();
			if (accessValueMd.startsWith("restricted")) {
				accessValue = AccessLevel.Restricted;
			}
			this.metadataParserResponse.getMetadata().setAccessLevel(
					accessValue);
		} catch (Exception e) {
			logger.error("handleAccess: " + e.getMessage());
			this.metadataParserResponse.addError(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleKeywords() {
		List<ThemeKeywords> themeKeywordList = new ArrayList<ThemeKeywords>();
		List<PlaceKeywords> placeKeywordList = new ArrayList<PlaceKeywords>();

		try {
			// Theme keywords
			NodeList themeKeywordNodes = (NodeList) xPath.evaluate(
					FgdcTag.ThemeKeywordsHeader.getXPathName(), document,
					XPathConstants.NODESET);

			for (int i = 0; i < themeKeywordNodes.getLength(); i++) {
				Node keyword = themeKeywordNodes.item(i);

				NodeList keywordValueNodes = (NodeList) xPath.evaluate(
						FgdcTag.ThemeKeywords.getXPathName(), keyword,
						XPathConstants.NODESET);
				if (keywordValueNodes.getLength() > 0) {
					String keywordThesaurus = (String) xPath.evaluate(
							FgdcTag.ThemeKeywordsThesaurus.getXPathName(),
							keyword, XPathConstants.STRING);

					ThemeKeywords themeKeyword = new ThemeKeywords();
					themeKeyword.setThesaurus(keywordThesaurus);

					for (int j = 0; j < keywordValueNodes.getLength(); j++) {
						String keywordValue = keywordValueNodes.item(j)
								.getTextContent();
						themeKeyword.addKeyword(keywordValue);
					}

					themeKeywordList.add(themeKeyword);
				}
			}

			// place keywords
			NodeList placeKeywordNodes = (NodeList) xPath.evaluate(
					FgdcTag.PlaceKeywordsHeader.getXPathName(), document,
					XPathConstants.NODESET);

			for (int i = 0; i < placeKeywordNodes.getLength(); i++) {
				Node keyword = placeKeywordNodes.item(i);

				NodeList keywordValueNodes = (NodeList) xPath.evaluate(
						FgdcTag.PlaceKeywords.getXPathName(), keyword,
						XPathConstants.NODESET);
				if (keywordValueNodes.getLength() > 0) {
					String keywordThesaurus = (String) xPath.evaluate(
							FgdcTag.PlaceKeywordsThesaurus.getXPathName(),
							keyword, XPathConstants.STRING);

					PlaceKeywords placeKeyword = new PlaceKeywords();
					placeKeyword.setThesaurus(keywordThesaurus);

					for (int j = 0; j < keywordValueNodes.getLength(); j++) {
						String keywordValue = keywordValueNodes.item(j)
								.getTextContent();
						placeKeyword.addKeyword(keywordValue);
					}

					placeKeywordList.add(placeKeyword);
				}
			}

			this.metadataParserResponse.getMetadata().setThemeKeywords(
					themeKeywordList);
			this.metadataParserResponse.getMetadata().setPlaceKeywords(
					placeKeywordList);
		} catch (Exception e) {
			logger.error("handleKeywords: " + e.getMessage());
		}
	}

	@Override
	protected void handleBounds() {
		Tag tag = FgdcTag.NorthBc;
		try {
			String maxY = getDocumentValue(tag);
			tag = FgdcTag.EastBc;
			String maxX = getDocumentValue(tag);
			tag = FgdcTag.SouthBc;
			String minY = getDocumentValue(tag);
			tag = FgdcTag.WestBc;
			String minX = getDocumentValue(tag);
			// should validate bounds here
			if (validateBounds(minX, minY, maxX, maxY)) {
				this.metadataParserResponse.getMetadata().setBounds(minX, minY,
						maxX, maxY);
			} else {
				throw new Exception("Invalid Bounds: " + minX + "," + minY
						+ "," + maxX + "," + maxY);
			}
		} catch (Exception e) {
			logger.error("handleBounds: " + e.getMessage());
			this.metadataParserResponse.addWarning(tag.toString(),
					tag.getTagName(), e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	protected void handleFullText() {
		this.metadataParserResponse.getMetadata().setFullText(getFullText());
	}
}
