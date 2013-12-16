package org.opengeoportal.harvester.api.client.csw.request;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.geonetwork.Xml;
import org.opengeoportal.harvester.api.client.csw.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Params:
  *  - resultType                (0..1) Can be 'hits', 'results', 'validate'. Default is 'hits'
  *  - outputFormat              (0..1) Can be only 'application/xml'
  *  - namespace                 (0..1) Used for the GET request
  *  - outputSchema              (0..1) Can be 'ogccore', 'profile'. Default is 'ogccore'
  *  - startPosition             (0..1) Default is 1
  *  - maxRecords                (0..1) Default is 10
  *  - TypeNames                 (1..1) A set of 'dataset', 'datasetcollection', 'service', 'application'
  *  - elementSetName            (0..1) Can be 'brief', 'summary', 'full'. Default is 'summary'
  *  - constraintLanguage        (1..1) Can be 'CQL_TEXT', 'FILTER'. Must be included
  *                                     when 'constraint' is specified
  *  - constraintLanguageVersion (1..1) Example '1.0.0'
  *  - constraint                (0..1) Query to execute
  *  - distributedSearch         (0..1) TRUE|FALSE
  *  - hopCount                  (0..1) default is 2
  */

public class GetRecordsRequest extends CatalogRequest {
	private String  outputFormat;
	private String  startPosition;
	private String  maxRecords;
	private String  constrLangVersion;
	private String  constraint;
	private String  hopCount = "2";
	private boolean distribSearch = false;

	private ResultType resultType;
	private ElementSetName elemSetName;
	private ConstraintLanguage constrLang;

	private Set<TypeName> hsTypeNames = new HashSet<TypeName>();
	private List<String> alSortBy    = new ArrayList<String>();

	
    public GetRecordsRequest(URL cswServerUrl) {
        super(cswServerUrl);
    }
	
    
	public void setResultType(ResultType type) {
		resultType = type;
	}


	public void setOutputFormat(String format) {
		outputFormat = format;
	}
	

	public void setStartPosition(String start) {
		startPosition = start;
	}


	public void setMaxRecords(String num) {
		maxRecords = num;
	}

	public void setElementSetName(ElementSetName name) {
		elemSetName = name;
	}

	public void addTypeName(TypeName typeName) {
		hsTypeNames.add(typeName);
	}

	public void setConstraintLanguage(ConstraintLanguage lang) {
		constrLang = lang;
	}

	public void setConstraintLangVersion(String version) {
		constrLangVersion = version;
	}

	public void setConstraint(String constr) {
		constraint = constr;
	}

	public void setHopCount(String hopCount) {
        this.hopCount = hopCount;
    }

	public void setDistribSearch(boolean distribSearch) {
        this.distribSearch = distribSearch;
    }

	protected String getRequestName() {
        return "GetRecords";
    }

	protected void setupGetParams() {
		addParam("request", getRequestName());
		addParam("service", Csw.SERVICE);
		addParam("version", getServerVersion());

		addParam("resultType",     resultType);
		addParam("namespace",      "xmlns(" + Csw.NAMESPACE_CSW.getPrefix() + "=" + Csw.NAMESPACE_CSW.getURI() + ")," 
				+ "xmlns(" + Csw.NAMESPACE_GMD.getPrefix() + "=" + Csw.NAMESPACE_GMD.getURI() + ")"
				);
		addParam("outputFormat",   outputFormat);
		addParam("outputSchema",   outputSchema);
		addParam("startPosition",  startPosition);
		addParam("maxRecords",     maxRecords);
		addParam("elementSetName", elemSetName);
		addParam("constraint",     constraint);

		if (distribSearch) {
			addParam("distributedSearch", "TRUE");

            if (hopCount != null){
                addParam("hopCount",       hopCount);
            }
		}

		addParam("constraintLanguage",          constrLang);
		addParam("constraint_language_version", constrLangVersion);

		// FIXME : default typeNames to return results
		// TODO : Check in Capabilities that typename exist
		// TODO : Check that local node support typename used
		if (hsTypeNames.size()==0)
			addParam("typeNames", "csw:Record");
		else
			fill("typeNames", hsTypeNames);
		fill("sortBy",    alSortBy);
	}

	protected Element getPostParams() {
		Element params  = new Element(getRequestName(), Csw.NAMESPACE_CSW);
        // Add queryable namespaces to POST query
        params.addNamespaceDeclaration(Csw.NAMESPACE_DC);

		//--- 'service' and 'version' are common mandatory attributes
		setAttrib(params, "service", Csw.SERVICE);
		setAttrib(params, "version", getServerVersion());

		setAttrib(params, "resultType",    resultType);
		setAttrib(params, "outputFormat",  outputFormat);
		//setAttrib(params, "outputSchema",  super.outputSchema, Csw.NAMESPACE_CSW.getPrefix() + ":");
        setAttrib(params, "outputSchema",  outputSchema);
		setAttrib(params, "startPosition", startPosition);
		setAttrib(params, "maxRecords",    maxRecords);

		if (distribSearch)
		{
			Element ds = new Element("DistributedSearch", Csw.NAMESPACE_CSW);
			ds.setText("TRUE");

			if (hopCount != null)
			{
				ds.setAttribute("hopCount", hopCount);
			}

			params.addContent(ds);
		}

		params.addContent(getQuery());

		return params;
	}

	

	private Element getQuery() {
		Element query = new Element("Query", Csw.NAMESPACE_CSW);
		// FIXME : default typeNames to return results
		// TODO : Check in Capabilities that typename exist
		// TODO : Check that local node support typename used
		if (hsTypeNames.size()==0)
			setAttrib(query, "typeNames", "csw:Record");
		else
			setAttribComma(query, "typeNames", hsTypeNames, "");
			
		addParam (query, "ElementSetName", elemSetName);

		//--- handle constraint

		if (constraint != null && constrLang != null)
		{
			Element constr = new Element("Constraint", Csw.NAMESPACE_CSW);
			query.addContent(constr);

			if (constrLang == ConstraintLanguage.CQL)
				addParam(constr, "CqlText", constraint);
			else
				addFilter(constr);

			setAttrib(constr, "version", constrLangVersion);
		}

		//--- handle sortby

		if (alSortBy.size() != 0)
		{
			Element sortBy = new Element("SortBy", Csw.NAMESPACE_OGC);
			query.addContent(sortBy);

			for(String sortInfo : alSortBy)
			{
				String  field = sortInfo.substring(0, sortInfo.length() -2);
				boolean ascen = sortInfo.endsWith(":A");

				Element sortProp = new Element("SortProperty", Csw.NAMESPACE_OGC);
				sortBy.addContent(sortProp);

				Element propName  = new Element("PropertyName", Csw.NAMESPACE_OGC).setText(field);
				Element sortOrder = new Element("SortOrder",    Csw.NAMESPACE_OGC).setText(ascen ? "ASC" : "DESC");

				sortProp.addContent(propName);
				sortProp.addContent(sortOrder);
			}
		}

		return query;
	}

	private void addFilter(Element constr) {
		try
		{
			constr.addContent(Xml.loadString(constraint, false));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
