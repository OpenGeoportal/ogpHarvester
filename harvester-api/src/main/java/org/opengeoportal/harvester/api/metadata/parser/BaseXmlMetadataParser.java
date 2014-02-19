package org.opengeoportal.harvester.api.metadata.parser;

import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.time.DateUtils;
import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Multimap;

public abstract class BaseXmlMetadataParser extends BaseMetadataParser
		implements MetadataParser {
	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Medadata XML document. */
	protected Document document;

	protected XPath xPath;

	protected MetadataParserResponse metadataParserResponse;

	public interface Tag {
		String getTagName();

		String getXPathName();
	};

	@Override
	public MetadataParserResponse parse(Document document) {
		if (document == null) {
			logger.error("document is null");
		}

		XPathFactory factory = XPathFactory.newInstance();
		xPath = factory.newXPath();

		HashMap<String, String> prefMap = getNamespaces();
		SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
		xPath.setNamespaceContext(namespaces);

		this.document = document;
		this.metadataParserResponse = new MetadataParserResponse();
		handleTitle();
		handleAbstract();
		handleLayerName();
		handlePublisher();
		handleOriginator();
		handleBounds();
		handleKeywords();
		handleAccess();
		handleDataType();
		handleFullText();
		handleDate();
		handleLocation();
		metadataParserResponse.setMetadataParsed(true);
		return metadataParserResponse;
	}

	protected abstract HashMap<String, String> getNamespaces();

	protected abstract void handleOriginator();

	protected abstract void handlePublisher();

	protected abstract void handleLayerName();

	protected abstract void handleAbstract();

	protected abstract void handleTitle();

	protected abstract void handleDate();

	protected abstract void handleDataType();

	protected abstract void handleAccess();

	protected abstract void handleKeywords();

	protected abstract void handleBounds();

	protected abstract void handleFullText();

	protected abstract LocationResolver getLocationResolver();

	public String getDocumentValue(Tag tag) throws Exception {
		return (String) xPath.evaluate(tag.getXPathName(), document,
				XPathConstants.STRING);
	}

	protected String getFullText() {
		try {
			Source xmlSource = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlSource, streamResult);
			String fileContents = stringWriter.toString();

			return fileContents;

		} catch (TransformerConfigurationException e) {
			logger.error("transformer configuration error", e);
		} catch (TransformerException e) {
			logger.error("transformer error", e);
		} catch (Exception e) {
			logger.error("Problem processing full text: " + e.getMessage());
		}
		return null;
	}

	protected Date processDateString(String passedDate) throws ParseException {
		// can't do anything if there's no value passed
		if ((passedDate == null) || (passedDate.equalsIgnoreCase("unknown"))) {
			return null;
		}
		List<String> formatsList = new ArrayList<String>();
		// add likely formats in order of likelihood

		formatsList.add("yyyyMMdd");
		formatsList.add("yyyyMM");
		formatsList.add("MM/yyyy");
		formatsList.add("MM/dd/yyyy");
		formatsList.add("MM/dd/yy");
		formatsList.add("MM-dd-yyyy");
		formatsList.add("MMMM yyyy");
		formatsList.add("MMM yyyy");
		formatsList.add("dd MMMM yyyy");
		formatsList.add("dd MMM yyyy");
		formatsList.add("yyyy");

		String[] parsePatterns = formatsList.toArray(new String[formatsList
				.size()]);
		// String returnYear = null;

		passedDate = passedDate.trim();
		Date date = DateUtils.parseDate(passedDate, parsePatterns);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		logger.debug("Document date: " + passedDate + ", Parsed date: "
				+ calendar.get(Calendar.YEAR));
		// returnYear = Integer.toString(calendar.get(Calendar.YEAR));

		return date;
	}

	protected void handleLocation() {
		Multimap<LocationType, LocationLink> locationMap = getLocationResolver()
				.resolveLocation(document);
		String locationJson = buildLocationJsonFromLocationLinks(locationMap);
		this.metadataParserResponse.getMetadata().setLocation(locationJson);

	}

	private class SimpleNamespaceContext implements NamespaceContext {

		private final Map<String, String> PREF_MAP = new HashMap<String, String>();

		public SimpleNamespaceContext(final Map<String, String> prefMap) {
			PREF_MAP.putAll(prefMap);
		}

		public String getNamespaceURI(String prefix) {
			return PREF_MAP.get(prefix);
		}

		public String getPrefix(String uri) {
			throw new UnsupportedOperationException();
		}

		public Iterator getPrefixes(String uri) {
			throw new UnsupportedOperationException();
		}

	}
}
