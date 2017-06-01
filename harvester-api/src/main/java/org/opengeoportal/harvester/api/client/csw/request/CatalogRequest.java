package org.opengeoportal.harvester.api.client.csw.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.exception.CatalogException;
import org.opengeoportal.harvester.api.client.geonetwork.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CatalogRequest {
    public enum Method {
        GET, POST
    }

    // Parameters to not take into account in GetRequest
    private static final List<String> excludedParameters = Arrays.asList("",
            "request", "version", "service");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String host;
    private int port;
    private String protocol;
    private String address;
    private String path;
    private Method method;
    private boolean useSOAP;

    protected String outputSchema;

    protected String serverVersion = Csw.CSW_VERSION; // Sets default value

    private final HttpClient client = new HttpClient();
    private ArrayList<NameValuePair> alGetParams;

    private ArrayList<NameValuePair> alSetupGetParams;

    // --- transient vars

    private String sentData;
    private String receivedData;
    private String postData;

    public CatalogRequest(final URL cswServerUrl) {
        this.host = cswServerUrl.getHost();
        this.port = cswServerUrl.getPort();
        this.protocol = cswServerUrl.getProtocol();

        this.setMethod(Method.POST);
        final Cookie cookie = new Cookie();
        final HttpState state = new HttpState();
        state.addCookie(cookie);
        this.client.setState(state);
        this.client.getParams()
                .setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    }

    // --- Parameters facilities (POST)
    protected void addParam(final Element root, final String name,
            final Object value) {
        if (value != null) {
            root.addContent(new Element(name, Csw.NAMESPACE_CSW)
                    .setText(value.toString()));
        }
    }

    // --- Parameters facilities (GET)
    protected void addParam(final String name, final Object value) {
        this.addParam(name, value, "");
    }

    protected void addParam(final String name, final Object value,
            final String prefix) {
        if (value != null) {
            this.alGetParams
                    .add(new NameValuePair(name, prefix + value.toString()));
        }
    }

    private Element doExecute(final HttpMethodBase httpMethod)
            throws IOException, JDOMException, CatalogException {
        this.client.getHostConfiguration().setHost(this.host, this.port,
                this.protocol);

        final byte[] data = null;

        try {
            this.client.executeMethod(httpMethod);

            if (httpMethod.getStatusCode() == 200) {
                return Xml.loadStream(httpMethod.getResponseBodyAsStream());
            } else {
                throw new CatalogException("http" + httpMethod.getStatusCode(),
                        httpMethod.getStatusText(), "");
            }
        } finally {
            httpMethod.releaseConnection();
            try {
                this.setupSentData(httpMethod);
                this.setupReceivedData(httpMethod, data);
            } catch (final Throwable e) {
                this.logger
                        .warn("Exception was raised during cleanup of a CSW request : "
                                + e.getMessage());
            }
        }
    }

    public Element execute() throws Exception {
        final HttpMethodBase httpMethod = this.setupHttpMethod();

        Element response = this.doExecute(httpMethod);

        if (this.useSOAP) {
            response = this.soapUnembed(response);
        }

        // --- raises an exception if the case
        CatalogException.unmarshal(response);

        return response;
    }

    protected void fill(final Element root, final String childName,
            final Iterable iter) {
        final Iterator i = iter.iterator();

        if (!i.hasNext()) {
            return;
        }

        while (i.hasNext()) {
            final Element el = new Element(childName, root.getNamespace());
            el.setText(i.next().toString());

            root.addContent(el);
        }
    }

    // --- POST fill methods
    protected void fill(final Element root, final String parentName,
            final String childName, final Iterable iter, final Namespace ns) {
        final Iterator i = iter.iterator();

        if (!i.hasNext()) {
            return;
        }

        final Element parent = new Element(parentName, ns);

        while (i.hasNext()) {
            final Element el = new Element(childName, ns);
            el.setText(i.next().toString());

            parent.addContent(el);
        }

        root.addContent(parent);
    }

    // --- GET fill methods
    protected void fill(final String param, final Iterable iter) {
        this.fill(param, iter, "");
    }

    protected void fill(final String param, final Iterable iter,
            final String prefix) {
        final Iterator i = iter.iterator();

        if (!i.hasNext()) {
            return;
        }

        final StringBuffer sb = new StringBuffer();

        while (i.hasNext()) {
            sb.append(prefix);
            sb.append(i.next());

            if (i.hasNext()) {
                sb.append(',');
            }
        }

        this.addParam(param, sb.toString());
    }

    public String getAddress() {
        return this.address;
    }

    public String getHost() {
        return this.host;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getOutputSchema() {
        return this.outputSchema;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    protected abstract Element getPostParams();

    public String getProtocol() {
        return this.protocol;
    }

    public String getReceivedData() {
        return this.receivedData;
    }

    protected abstract String getRequestName();

    public String getSentData() {
        return this.sentData;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    protected void setAttrib(final Element el, final String name,
            final Iterable iter, final String prefix) {
        final Iterator i = iter.iterator();

        if (!i.hasNext()) {
            return;
        }

        final StringBuffer sb = new StringBuffer();

        while (i.hasNext()) {
            sb.append(prefix);
            sb.append(i.next().toString());

            if (i.hasNext()) {
                sb.append(" ");
            }
        }

        el.setAttribute(name, sb.toString());
    }

    // --- Attribute facilities
    protected void setAttrib(final Element el, final String name,
            final Object value) {
        this.setAttrib(el, name, value, "");
    }

    protected void setAttrib(final Element el, final String name,
            final Object value, final String prefix) {
        if (value != null) {
            el.setAttribute(name, prefix + value.toString());
        }
    }

    protected void setAttribComma(final Element el, final String name,
            final Iterable iter, final String prefix) {
        final Iterator i = iter.iterator();

        if (!i.hasNext()) {
            return;
        }

        final StringBuffer sb = new StringBuffer();

        while (i.hasNext()) {
            final Object value = i.next();

            sb.append(prefix);
            sb.append(value.toString());

            if (i.hasNext()) {
                sb.append(',');
            }
        }

        el.setAttribute(name, sb.toString());
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setMethod(final Method m) {
        this.method = m;
    }

    public void setOutputSchema(final String outputSchema) {
        this.outputSchema = outputSchema;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public void setServerVersion(final String serverVersion) {
        this.serverVersion = serverVersion;
    }

    protected abstract void setupGetParams();

    private HttpMethodBase setupHttpMethod()
            throws UnsupportedEncodingException {
        HttpMethodBase httpMethod;

        if (this.method == Method.GET) {
            this.alGetParams = new ArrayList<NameValuePair>();

            if (this.alSetupGetParams.size() != 0) {
                this.alGetParams.addAll(this.alSetupGetParams);
            }

            this.setupGetParams();
            httpMethod = new GetMethod();
            httpMethod.setPath(this.path);
            httpMethod.setQueryString(
                    this.alGetParams.toArray(new NameValuePair[1]));
            this.logger.debug("GET params:" + httpMethod.getQueryString());
            if (this.useSOAP) {
                httpMethod.addRequestHeader("Accept", "application/soap+xml");
            }

        } else {
            final Element params = this.getPostParams();
            final PostMethod post = new PostMethod();

            if (!this.useSOAP) {
                this.postData = Xml.getString(new Document(params));
                post.setRequestEntity(new StringRequestEntity(this.postData,
                        "application/xml", "UTF8"));
            } else {
                this.postData = Xml
                        .getString(new Document(this.soapEmbed(params)));
                post.setRequestEntity(new StringRequestEntity(this.postData,
                        "application/soap+xml", "UTF8"));
            }

            this.logger.debug("POST params:" + Xml.getString(params));
            httpMethod = post;
            httpMethod.setPath(this.address);
        }

        // httpMethod.setFollowRedirects(true);

        return httpMethod;
    }

    private void setupReceivedData(final HttpMethodBase httpMethod,
            final byte[] response) {
        this.receivedData = httpMethod.getStatusText() + "\r\r";

        for (final Header h : httpMethod.getResponseHeaders()) {
            this.receivedData += h;
        }

        this.receivedData += "\r\n";

        try {
            if (response != null) {
                this.receivedData += new String(response, "UTF8");
            }
        } catch (final UnsupportedEncodingException e) {
            // TODO
        }
    }

    private void setupSentData(final HttpMethodBase httpMethod) {
        this.sentData = httpMethod.getName() + " " + httpMethod.getPath();

        if (httpMethod.getQueryString() != null) {
            this.sentData += "?" + httpMethod.getQueryString();
        }

        this.sentData += "\r\n";

        for (final Header h : httpMethod.getRequestHeaders()) {
            this.sentData += h;
        }

        this.sentData += "\r\n";

        if (httpMethod instanceof PostMethod) {
            this.sentData += this.postData;
        }
    }

    /**
     * Set request URL host, port, address and path. If URL contains query
     * string parameters, those parameters are preserved (and
     * {@link CatalogRequest#excludedParameters} are excluded). A complete
     * GetCapabilities URL may be used for initialization.
     */
    public void setUrl(final URL url) {
        this.host = url.getHost();
        this.port = url.getPort();
        this.protocol = url.getProtocol();
        this.address = url.toString();
        this.path = url.getPath();

        this.alSetupGetParams = new ArrayList<NameValuePair>();
        final String query = url.getQuery();

        if (StringUtils.isNotEmpty(query)) {
            final String[] params = query.split("&");
            for (final String param : params) {
                final String[] kvp = param.split("=");
                if (!CatalogRequest.excludedParameters
                        .contains(kvp[0].toLowerCase())) {
                    this.alSetupGetParams
                            .add(new NameValuePair(kvp[0], kvp[1]));
                }
            }
        }

        if (this.port == -1) {
            this.port = url.getDefaultPort();
        }
    }

    public void setUseSOAP(final boolean yesno) {
        this.useSOAP = yesno;
    }

    private Element soapEmbed(final Element elem) {
        final Element envl = new Element("Envelope", Csw.NAMESPACE_ENV);
        final Element body = new Element("Body", Csw.NAMESPACE_ENV);

        envl.addContent(body);
        body.addContent(elem);

        return envl;
    }

    private Element soapUnembed(final Element envelope) throws Exception {
        final Namespace ns = envelope.getNamespace();
        final Element body = envelope.getChild("Body", ns);

        if (body == null) {
            throw new Exception("Bad SOAP response");
        }

        final List list = body.getChildren();

        if (list.size() == 0) {
            throw new Exception("Bas SOAP response");
        }

        return (Element) list.get(0);
    }

}
