package org.opengeoportal.harvester.api.client.csw.request;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.ConstraintLanguage;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.ElementSetName;
import org.opengeoportal.harvester.api.client.csw.ResultType;
import org.opengeoportal.harvester.api.client.csw.TypeName;
import org.opengeoportal.harvester.api.client.geonetwork.Xml;

/**
 * Params: - resultType (0..1) Can be 'hits', 'results', 'validate'. Default is
 * 'hits' - outputFormat (0..1) Can be only 'application/xml' - namespace (0..1)
 * Used for the GET request - outputSchema (0..1) Can be 'ogccore', 'profile'.
 * Default is 'ogccore' - startPosition (0..1) Default is 1 - maxRecords (0..1)
 * Default is 10 - TypeNames (1..1) A set of 'dataset', 'datasetcollection',
 * 'service', 'application' - elementSetName (0..1) Can be 'brief', 'summary',
 * 'full'. Default is 'summary' - constraintLanguage (1..1) Can be 'CQL_TEXT',
 * 'FILTER'. Must be included when 'constraint' is specified -
 * constraintLanguageVersion (1..1) Example '1.0.0' - constraint (0..1) Query to
 * execute - distributedSearch (0..1) TRUE|FALSE - hopCount (0..1) default is 2
 */

public class GetRecordsRequest extends CatalogRequest {
    private String outputFormat;
    private String startPosition;
    private String maxRecords;
    private String constrLangVersion;
    private String constraint;
    private String hopCount = "2";
    private boolean distribSearch = false;

    private ResultType resultType;
    private ElementSetName elemSetName;
    private ConstraintLanguage constrLang;

    private final Set<TypeName> hsTypeNames = new HashSet<TypeName>();
    private final List<String> alSortBy = new ArrayList<String>();

    public GetRecordsRequest(final URL cswServerUrl) {
        super(cswServerUrl);
    }

    private void addFilter(final Element constr) {
        try {
            constr.addContent(Xml.loadString(this.constraint, false));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void addTypeName(final TypeName typeName) {
        this.hsTypeNames.add(typeName);
    }

    @Override
    protected Element getPostParams() {
        final Element params = new Element(this.getRequestName(),
                Csw.NAMESPACE_CSW);
        // Add queryable namespaces to POST query
        params.addNamespaceDeclaration(Csw.NAMESPACE_DC);

        // --- 'service' and 'version' are common mandatory attributes
        this.setAttrib(params, "service", Csw.SERVICE);
        this.setAttrib(params, "version", this.getServerVersion());

        this.setAttrib(params, "resultType", this.resultType);
        this.setAttrib(params, "outputFormat", this.outputFormat);
        // setAttrib(params, "outputSchema", super.outputSchema,
        // Csw.NAMESPACE_CSW.getPrefix() + ":");
        this.setAttrib(params, "outputSchema", this.outputSchema);
        this.setAttrib(params, "startPosition", this.startPosition);
        this.setAttrib(params, "maxRecords", this.maxRecords);

        if (this.distribSearch) {
            final Element ds = new Element("DistributedSearch",
                    Csw.NAMESPACE_CSW);
            ds.setText("TRUE");

            if (this.hopCount != null) {
                ds.setAttribute("hopCount", this.hopCount);
            }

            params.addContent(ds);
        }

        params.addContent(this.getQuery());

        return params;
    }

    private Element getQuery() {
        final Element query = new Element("Query", Csw.NAMESPACE_CSW);
        // FIXME : default typeNames to return results
        // TODO : Check in Capabilities that typename exist
        // TODO : Check that local node support typename used
        if (this.hsTypeNames.size() == 0) {
            this.setAttrib(query, "typeNames", "csw:Record");
        } else {
            this.setAttribComma(query, "typeNames", this.hsTypeNames, "");
        }

        this.addParam(query, "ElementSetName", this.elemSetName);

        // --- handle constraint

        if ((this.constraint != null) && (this.constrLang != null)) {
            final Element constr = new Element("Constraint", Csw.NAMESPACE_CSW);
            query.addContent(constr);

            if (this.constrLang == ConstraintLanguage.CQL) {
                this.addParam(constr, "CqlText", this.constraint);
            } else {
                this.addFilter(constr);
            }

            this.setAttrib(constr, "version", this.constrLangVersion);
        }

        // --- handle sortby

        if (this.alSortBy.size() != 0) {
            final Element sortBy = new Element("SortBy", Csw.NAMESPACE_OGC);
            query.addContent(sortBy);

            for (final String sortInfo : this.alSortBy) {
                final String field = sortInfo.substring(0,
                        sortInfo.length() - 2);
                final boolean ascen = sortInfo.endsWith(":A");

                final Element sortProp = new Element("SortProperty",
                        Csw.NAMESPACE_OGC);
                sortBy.addContent(sortProp);

                final Element propName = new Element("PropertyName",
                        Csw.NAMESPACE_OGC).setText(field);
                final Element sortOrder = new Element("SortOrder",
                        Csw.NAMESPACE_OGC).setText(ascen ? "ASC" : "DESC");

                sortProp.addContent(propName);
                sortProp.addContent(sortOrder);
            }
        }

        return query;
    }

    @Override
    protected String getRequestName() {
        return "GetRecords";
    }

    public void setConstraint(final String constr) {
        this.constraint = constr;
    }

    public void setConstraintLanguage(final ConstraintLanguage lang) {
        this.constrLang = lang;
    }

    public void setConstraintLangVersion(final String version) {
        this.constrLangVersion = version;
    }

    public void setDistribSearch(final boolean distribSearch) {
        this.distribSearch = distribSearch;
    }

    public void setElementSetName(final ElementSetName name) {
        this.elemSetName = name;
    }

    public void setHopCount(final String hopCount) {
        this.hopCount = hopCount;
    }

    public void setMaxRecords(final String num) {
        this.maxRecords = num;
    }

    public void setOutputFormat(final String format) {
        this.outputFormat = format;
    }

    public void setResultType(final ResultType type) {
        this.resultType = type;
    }

    public void setStartPosition(final String start) {
        this.startPosition = start;
    }

    @Override
    protected void setupGetParams() {
        this.addParam("request", this.getRequestName());
        this.addParam("service", Csw.SERVICE);
        this.addParam("version", this.getServerVersion());

        this.addParam("resultType", this.resultType);
        this.addParam("namespace",
                "xmlns(" + Csw.NAMESPACE_CSW.getPrefix() + "="
                        + Csw.NAMESPACE_CSW.getURI() + ")," + "xmlns("
                        + Csw.NAMESPACE_GMD.getPrefix() + "="
                        + Csw.NAMESPACE_GMD.getURI() + ")");
        this.addParam("outputFormat", this.outputFormat);
        this.addParam("outputSchema", this.outputSchema);
        this.addParam("startPosition", this.startPosition);
        this.addParam("maxRecords", this.maxRecords);
        this.addParam("elementSetName", this.elemSetName);
        this.addParam("constraint", this.constraint);

        if (this.distribSearch) {
            this.addParam("distributedSearch", "TRUE");

            if (this.hopCount != null) {
                this.addParam("hopCount", this.hopCount);
            }
        }

        this.addParam("constraintLanguage", this.constrLang);
        this.addParam("constraint_language_version", this.constrLangVersion);

        // FIXME : default typeNames to return results
        // TODO : Check in Capabilities that typename exist
        // TODO : Check that local node support typename used
        if (this.hsTypeNames.size() == 0) {
            this.addParam("typeNames", "csw:Record");
        } else {
            this.fill("typeNames", this.hsTypeNames);
        }
        this.fill("sortBy", this.alSortBy);
    }
}
