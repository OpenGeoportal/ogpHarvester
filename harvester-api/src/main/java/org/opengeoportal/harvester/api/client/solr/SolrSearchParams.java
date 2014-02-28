/**
 * SolrSearchParams.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester.
 *
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 *
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.api.client.solr;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multisets;

/**
 * This class contains the fields values used to filter Solr medatada.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class SolrSearchParams {
	/**
	 * 
	 */
	private static final String PF = "pf";

	/**
	 * The default operator for parsing queries.
	 */
	public static enum Operator {
		/** OR operator. */
		OR,
		/** AND operator. */
		AND
	}

	/**
	 * Number of result records requested per page.
	 */
	public static final int NUMBER_OF_RESULTS_PER_PAGE = 40;
	/** Content range date page. */
	private Date dateFrom;
	/** Content range date to. */
	private Date dateTo;
	/** Number of the page to retrieve (zero based). */
	private int page = 0;
	/** Data originator. */
	private String originator;
	/** Number of elements in one page. */
	private int pageSize = NUMBER_OF_RESULTS_PER_PAGE;
	/** Place keyworkds. */
	private String placeKeyword;
	/** Theme keywords. */
	private String themeKeyword;
	/** ISO topic category. */
	private String topicCategory;
	/** Data types (Line, Point, Polygon, ...). */
	private List<DataType> dataTypes;
	/** Remote repositories. */
	private List<String> dataRepositories;
	/** <code>true</code> if restricted data should be excluded. */
	private boolean excludeRestrictedData;
	/** Returned metadata can not ha a solr timestamp before this date. */
	private Date fromSolrTimestamp;
	/** Returned metadata can not ha a solr timestamp after this date. */
	private Date toSolrTimestamp;
	/** Custom Sorl Query. */
	private String customSolrQuery;
	/** Bounding box West coordinte. */
	private Double bboxWest;
	/** Bounding box East coordinate. */
	private Double bboxEast;
	/** Bounding box North coordinate. */
	private Double bboxNorth;
	/** Bounding box South coordinate. */
	private Double bboxSouth;

	/**
	 * Construct a new <code>SolrSearchParams</code> with the data contained in
	 * ingest configuration and
	 * {@link SolrSearchParams#NUMBER_OF_RESULTS_PER_PAGE} page size.
	 * 
	 * @param ingest
	 *            ingest configuration.
	 */
	public SolrSearchParams(final IngestOGP ingest) {
		this(ingest, NUMBER_OF_RESULTS_PER_PAGE);
	}

	/**
	 * Construct a new <code>SolrSearchParams</code> with the data contained in
	 * ingest configuration and
	 * {@link SolrSearchParams#NUMBER_OF_RESULTS_PER_PAGE} page size.
	 * 
	 * @param ingest
	 *            ingest configuration.
	 * 
	 * @param numberOfElementsPerPage
	 *            number of elements to be retrieved in one page.
	 */
	public SolrSearchParams(final IngestOGP ingest,
			final int numberOfElementsPerPage) {
		if (ingest == null) {
			throw new IllegalArgumentException(
					"ingest parameter cannot be null when"
							+ " creating a new SolrSearchParams");
		}
		this.pageSize = numberOfElementsPerPage;
		this.bboxEast = ingest.getBboxEast();
		this.bboxNorth = ingest.getBboxNorth();
		this.bboxSouth = ingest.getBboxSouth();
		this.bboxWest = ingest.getBboxWest();
		this.customSolrQuery = ingest.getCustomSolrQuery();
		this.dataRepositories = Lists
				.newArrayList(ingest.getDataRepositories());
		this.dataTypes = Lists.newArrayList(ingest.getDataTypes());
		this.dateFrom = ingest.getDateFrom();
		this.dateTo = ingest.getDateTo();
		this.excludeRestrictedData = ingest.isExcludeRestrictedData();
		this.fromSolrTimestamp = ingest.getFromSolrTimestamp();
		this.originator = ingest.getOriginator();
		this.placeKeyword = ingest.getPlaceKeyword();
		this.themeKeyword = ingest.getThemeKeyword();
		this.topicCategory = ingest.getTopicCategory();
		this.toSolrTimestamp = ingest.getToSolrTimestamp();
	}

	/**
	 * Transform the record in {@link SolrQuery} executable by an
	 * {@link org.apache.solr.client.solrj.impl.HttpSolrServer}.
	 * 
	 * @return the {@link SolrQuery} built with the data page this.
	 */
	SolrQuery toSolrQuery() {
		SolrQuery solrQuery = new SolrQuery();

		if (StringUtils.isNotBlank(customSolrQuery)) {
			solrQuery.setQuery(customSolrQuery);
		} else {
			solrQuery.setQuery("*:*");
			// data repositories
			if (dataRepositories != null && dataRepositories.size() > 0) {
				Criteria institutionCriteria = null;
				for (String institution : dataRepositories) {
					if (institutionCriteria == null) {
						institutionCriteria = new Criteria(
								SolrRecord.INSTITUTION).is(institution);
					} else {
						institutionCriteria = institutionCriteria
								.or(new Criteria(SolrRecord.INSTITUTION)
										.is(institution));
					}
				}

				SimpleQuery query = new SimpleQuery(institutionCriteria);
				DefaultQueryParser parser = new DefaultQueryParser();
				String queryString = parser.getQueryString(query);
				solrQuery.addFilterQuery(queryString);
			} else {
				solrQuery.addFilterQuery(SolrRecord.INSTITUTION + ":*");
			}

			// theme keywords
			if (StringUtils.isNotBlank(themeKeyword)) {
				solrQuery.addFilterQuery(SolrRecord.THEME_KEYWORDS + ":"
						+ themeKeyword);
				solrQuery.add(PF, SolrRecord.THEME_KEYWORDS + ":'"
						+ themeKeyword + "'^9.0");
				solrQuery.add(PF, SolrRecord.LAYER_DISPLAY_NAME + ":'"
						+ themeKeyword + "'^9.0");
			}
			if (StringUtils.isNotBlank(placeKeyword)) {
				solrQuery.addFilterQuery(SolrRecord.PLACE_KEYWORDS + ":"
						+ placeKeyword);
				solrQuery.add(PF, SolrRecord.PLACE_KEYWORDS + ":'"
						+ placeKeyword + "'^9.0");
			}
			if (StringUtils.isNotBlank(topicCategory)) {
				solrQuery.addFilterQuery(SolrRecord.ISO_TOPIC_CATEGORY + ":"
						+ this.topicCategory);

			}

			if (dateFrom != null || dateTo != null) {
				Criteria contentDateCriteria = Criteria.where(
						SolrRecord.CONTENT_DATE).between(dateFrom, dateTo);
				SimpleQuery query = new SimpleQuery(contentDateCriteria);
				DefaultQueryParser parser = new DefaultQueryParser();
				String queryString = parser.getQueryString(query);
				solrQuery.addFilterQuery(queryString);

			}
			if (StringUtils.isNotBlank(originator)) {
				String originatorCriteria = splitAndConcatenateUsingOperator(
						Operator.AND, SolrRecord.ORIGINATOR, originator);
				solrQuery.addFilterQuery(originatorCriteria);
				solrQuery.add(PF, SolrRecord.ORIGINATOR + ":" + originator);
			}
			if (dataTypes != null && dataTypes.size() > 0) {
				StringBuilder concatenatedType = new StringBuilder();
				for (DataType dType : dataTypes) {
					concatenatedType.append(dType.toString()).append(" ");
				}
				String dataTypeCriteria = splitAndConcatenateUsingOperator(
						Operator.OR, SolrRecord.DATA_TYPE,
						concatenatedType.toString());
				solrQuery.add("fq", dataTypeCriteria);
			}

			if (excludeRestrictedData) {
				solrQuery.addFilterQuery(SolrRecord.ACCESS + ":"
						+ AccessLevel.Public);
			}

			if (fromSolrTimestamp != null || toSolrTimestamp != null) {
				Criteria solrTimestampCriteria = Criteria.where(
						SolrRecord.TIMESTAMP).between(fromSolrTimestamp,
						toSolrTimestamp);
				SimpleQuery query = new SimpleQuery(solrTimestampCriteria);
				DefaultQueryParser parser = new DefaultQueryParser();
				String queryString = parser.getQueryString(query);
				solrQuery.addFilterQuery(queryString);
			}
			// Add bbox filter only if user has not specified a custom solr
			// query.
			buildBoundigBoxQuery(solrQuery);

			String synonymsFilter = generateSynonymsQuery();
			if (StringUtils.isNotBlank(synonymsFilter)) {
				solrQuery.addFilterQuery();
			}

		}

		solrQuery.setRows(pageSize);
		solrQuery.setStart(page * pageSize);
		solrQuery.addSort(SortClause.desc("score"));

		return solrQuery;
	}

	/**
	 * @return
	 */
	private String generateSynonymsQuery() {
		ListMultimap<String, String> fieldList = ArrayListMultimap.create();
		if (StringUtils.isNotBlank(themeKeyword)) {
			fieldList.put(SolrRecord.LAYER_DISPLAY_NAME_SYNONYMS, "("
					+ themeKeyword + ")");
			fieldList.put(SolrRecord.THEME_KEYWORDS_SYNONYMS_LCSH, "("
					+ themeKeyword + ")");
		}
		if (StringUtils.isNotBlank(placeKeyword)) {
			fieldList.put(SolrRecord.PLACE_KEYWORDS_SYNONYMS, "("
					+ placeKeyword + ")");
			fieldList.put(SolrRecord.LAYER_DISPLAY_NAME_SYNONYMS, "("
					+ placeKeyword + ")");
		}
		StringBuilder synonymsQuery = new StringBuilder();
		Iterator<Entry<String, String>> entryIterator = fieldList.entries()
				.iterator();
		while (entryIterator.hasNext()) {
			Entry<String, String> entry = entryIterator.next();
			synonymsQuery.append(entry.getKey()).append(":")
					.append(entry.getValue());

			if (entryIterator.hasNext()) {
				synonymsQuery.append(" OR ");
			}

		}
		return synonymsQuery.toString();
	}

	/**
	 * @param and
	 * @param originator2
	 * @param originator3
	 * @return
	 */
	private String splitAndConcatenateUsingOperator(Operator operator,
			String fieldName, String fieldContent) {
		String[] contentSplitted = StringUtils.split(fieldContent);
		StringBuilder sb = new StringBuilder();
		int length = contentSplitted.length;
		for (int i = 0; i < length; i++) {
			sb.append(fieldName).append(":").append(contentSplitted[i]);
			if (i != length - 1) {
				sb.append(" ").append(operator.toString());
			}
		}
		return sb.toString();
	}

	private void buildBoundigBoxQuery(SolrQuery query) {
		if (isValidBBox()) {
			String fqParam = "{!frange l=0 incl=false cache=false}$intx";
			query.addFilterQuery(fqParam);
			// @formatter:off
			// product(
			//    max(
			//       0,
			//       sub(
			//           min(180,MaxX),
			//           max(-180,MinX)
			//       )
			//    ),
			//    max(
			//       0,
			//       sub(
			//           min(41.902277040963,MaxY),
			//           max(-86.30131338825,MinY)
			//       )
			//    )
			// )
			// @formatter:on
			String intxTemplateString = "product(max(0,sub(min(%f,MaxX),"
					+ "max(%f,MinX))),max(0,sub(min(%f,MaxY),"
					+ "max(%f,MinY))))";
			String intxParam = String.format(Locale.ENGLISH,
					intxTemplateString, this.bboxEast, this.bboxWest,
					this.bboxNorth, this.bboxSouth);
			query.setParam("intx", intxParam);
		}
	}

	/**
	 * Check if bounding box coordinates are valid. Bounding Box is valid if its
	 * four components are not null and west coordinate is lesser than east
	 * coordinate and south coord is lesser than north coord.
	 * 
	 * @return <code>true</code> if bounding box is valid; otherwise return
	 *         <code>false</code>.
	 */
	private boolean isValidBBox() {
		boolean valid = false;

		if (bboxWest != null && bboxEast != null && bboxNorth != null
				&& bboxSouth != null) {
			valid = this.bboxWest < this.bboxEast
					&& this.bboxSouth < this.bboxNorth;
		}

		return valid;
	}

	/**
	 * Split wordlist by blank space and create an OR criteria with each
	 * resulting word. Words are added using wildcards.
	 * 
	 * @param wordList
	 *            a string with a list of words
	 * @param fieldName
	 *            the name of the field where criteria is applied.
	 * @return an OR criteria with each word contained in wordlist.
	 */
	private Criteria splitAndOrCriteria(String wordList, String fieldName) {
		Criteria orCriteria = null;
		String[] words = StringUtils.split(wordList);
		for (String word : words) {
			if (orCriteria == null) {
				orCriteria = new Criteria(fieldName).contains(word);
			} else {
				orCriteria = orCriteria.or(new Criteria(fieldName)
						.contains(word));
			}

		}

		if (orCriteria != null) {
			SimpleQuery query = new SimpleQuery(orCriteria);
			DefaultQueryParser parser = new DefaultQueryParser();
			String queryString = parser.getQueryString(query);
			return new SimpleStringCriteria("(" + queryString + ")");
		} else {
			return null;
		}
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @return the page.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return the originator
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the placeKeyword
	 */
	public String getPlaceKeyword() {
		return placeKeyword;
	}

	/**
	 * @return the themeKeyword
	 */
	public String getThemeKeyword() {
		return themeKeyword;
	}

	/**
	 * @return the topicCategory
	 */
	public String getTopicCategory() {
		return topicCategory;
	}

	/**
	 * @param dateFrom
	 *            the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @param dateTo
	 *            the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @param from
	 *            the page to set
	 */
	public void setPage(int from) {
		this.page = from;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param placeKeyword
	 *            the placeKeyword to set
	 */
	public void setPlaceKeyword(String placeKeyword) {
		this.placeKeyword = placeKeyword;
	}

	/**
	 * @param themeKeyword
	 *            the themeKeyword to set
	 */
	public void setThemeKeyword(String themeKeyword) {
		this.themeKeyword = themeKeyword;
	}

	/**
	 * @param topicCategory
	 *            the topicCategory to set
	 */
	public void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	/**
	 * @return the dataTypes
	 */
	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	/**
	 * @param dataTypes
	 *            the dataTypes to set
	 */
	public void setDataTypes(List<DataType> dataTypes) {
		this.dataTypes = dataTypes;
	}

	/**
	 * @return the dataRepositories
	 */
	public List<String> getDataRepositories() {
		return dataRepositories;
	}

	/**
	 * @param dataRepositories
	 *            the dataRepositories to set
	 */
	public void setDataRepositories(List<String> dataRepositories) {
		this.dataRepositories = dataRepositories;
	}

	/**
	 * @return the excludeRestrictedData
	 */
	public boolean isExcludeRestrictedData() {
		return excludeRestrictedData;
	}

	/**
	 * @param excludeRestrictedData
	 *            the excludeRestrictedData to set
	 */
	public void setExcludeRestrictedData(boolean excludeRestrictedData) {
		this.excludeRestrictedData = excludeRestrictedData;
	}

	/**
	 * @return the fromSolrTimestamp
	 */
	public Date getFromSolrTimestamp() {
		return fromSolrTimestamp;
	}

	/**
	 * @param fromSolrTimestamp
	 *            the fromSolrTimestamp to set
	 */
	public void setFromSolrTimestamp(Date fromSolrTimestamp) {
		this.fromSolrTimestamp = fromSolrTimestamp;
	}

	/**
	 * @return the toSolrTimestamp
	 */
	public Date getToSolrTimestamp() {
		return toSolrTimestamp;
	}

	/**
	 * @param toSolrTimestamp
	 *            the toSolrTimestamp to set
	 */
	public void setToSolrTimestamp(Date toSolrTimestamp) {
		this.toSolrTimestamp = toSolrTimestamp;
	}

	/**
	 * @return the customSolrQuery
	 */
	public String getCustomSolrQuery() {
		return customSolrQuery;
	}

	/**
	 * @param customSolrQuery
	 *            the customSolrQuery to set
	 */
	public void setCustomSolrQuery(String customSolrQuery) {
		this.customSolrQuery = customSolrQuery;
	}

	/**
	 * @return the bboxWest
	 */
	public Double getBboxWest() {
		return bboxWest;
	}

	/**
	 * @param bboxWest
	 *            the bboxWest to set
	 */
	public void setBboxWest(Double bboxWest) {
		this.bboxWest = bboxWest;
	}

	/**
	 * @return the bboxEast
	 */
	public Double getBboxEast() {
		return bboxEast;
	}

	/**
	 * @param bboxEast
	 *            the bboxEast to set
	 */
	public void setBboxEast(Double bboxEast) {
		this.bboxEast = bboxEast;
	}

	/**
	 * @return the bboxNorth
	 */
	public Double getBboxNorth() {
		return bboxNorth;
	}

	/**
	 * @param bboxNorth
	 *            the bboxNorth to set
	 */
	public void setBboxNorth(Double bboxNorth) {
		this.bboxNorth = bboxNorth;
	}

	/**
	 * @return the bboxSouth
	 */
	public Double getBboxSouth() {
		return bboxSouth;
	}

	/**
	 * @param bboxSouth
	 *            the bboxSouth to set
	 */
	public void setBboxSouth(Double bboxSouth) {
		this.bboxSouth = bboxSouth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrSearchParams [");
		if (dateFrom != null) {
			builder.append("dateFrom=");
			builder.append(dateFrom);
			builder.append(", ");
		}
		if (dateTo != null) {
			builder.append("dateTo=");
			builder.append(dateTo);
			builder.append(", ");
		}
		builder.append("page=");
		builder.append(page);
		builder.append(", ");
		if (originator != null) {
			builder.append("originator=");
			builder.append(originator);
			builder.append(", ");
		}
		builder.append("pageSize=");
		builder.append(pageSize);
		builder.append(", ");
		if (placeKeyword != null) {
			builder.append("placeKeyword=");
			builder.append(placeKeyword);
			builder.append(", ");
		}
		if (themeKeyword != null) {
			builder.append("themeKeyword=");
			builder.append(themeKeyword);
			builder.append(", ");
		}
		if (topicCategory != null) {
			builder.append("topicCategory=");
			builder.append(topicCategory);
			builder.append(", ");
		}
		if (dataTypes != null) {
			builder.append("dataTypes=");
			builder.append(dataTypes);
			builder.append(", ");
		}
		if (dataRepositories != null) {
			builder.append("dataRepositories=");
			builder.append(dataRepositories);
			builder.append(", ");
		}
		builder.append("excludeRestrictedData=");
		builder.append(excludeRestrictedData);
		builder.append(", ");
		if (fromSolrTimestamp != null) {
			builder.append("fromSolrTimestamp=");
			builder.append(fromSolrTimestamp);
			builder.append(", ");
		}
		if (toSolrTimestamp != null) {
			builder.append("toSolrTimestamp=");
			builder.append(toSolrTimestamp);
			builder.append(", ");
		}
		if (customSolrQuery != null) {
			builder.append("customSolrQuery=");
			builder.append(customSolrQuery);
			builder.append(", ");
		}
		if (bboxWest != null) {
			builder.append("bboxWest=");
			builder.append(bboxWest);
			builder.append(", ");
		}
		if (bboxEast != null) {
			builder.append("bboxEast=");
			builder.append(bboxEast);
			builder.append(", ");
		}
		if (bboxNorth != null) {
			builder.append("bboxNorth=");
			builder.append(bboxNorth);
			builder.append(", ");
		}
		if (bboxSouth != null) {
			builder.append("bboxSouth=");
			builder.append(bboxSouth);
		}
		builder.append("]");
		return builder.toString();
	}

}
