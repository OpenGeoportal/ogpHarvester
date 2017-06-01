package org.opengeoportal.harvester.api.client.geonetwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.geonetwork.exception.BadSoapResponseEx;
import org.opengeoportal.harvester.api.client.geonetwork.exception.BadXmlResponseEx;

/**
 * Utility class used in {@link GeoNetworkClient} to send requests to the
 * GeoNetwork server.
 *
 * Code from GeoNetwork opensource project.
 *
 */
public class XmlRequest {
    public enum Method {
        GET, POST
    }

    private static final Namespace NAMESPACE_ENV = Namespace.getNamespace("env",
            "http://www.w3.org/2003/05/soap-envelope");

    private String host;

    private int port;

    private String protocol;

    private String address;

    private boolean serverAuthent;

    private String query;

    private Method method;

    private boolean useSOAP;

    private Element postParams;

    private boolean useProxy;

    private String proxyHost;

    private int proxyPort;

    private boolean proxyAuthent;

    private final HttpClient client = new HttpClient();

    // ---------------------------------------------------------------------------

    private final HttpState state = new HttpState();

    private final Cookie cookie = new Cookie();

    private final HostConfiguration config = new HostConfiguration();

    private final ArrayList<NameValuePair> alSimpleParams = new ArrayList<NameValuePair>();

    private String sentData;

    private String receivedData;

    private String postData;

    public XmlRequest() {
        this(null, 80);
    }

    public XmlRequest(final String host) {
        this(host, 80);
    }

    public XmlRequest(final String host, final int port) {
        this(host, port, "http");
    }

    public XmlRequest(final String host, final int port,
            final String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;

        this.setMethod(Method.GET);
        this.state.addCookie(this.cookie);
        this.client.setState(this.state);
        this.client.getParams()
                .setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        this.client.setHostConfiguration(this.config);
        final List<String> authPrefs = new ArrayList<String>(2);
        authPrefs.add(AuthPolicy.DIGEST);
        authPrefs.add(AuthPolicy.BASIC);
        // This will exclude the NTLM authentication scheme
        this.client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY,
                authPrefs);
    }

    /**
     * Build a {@link XmlRequest} based on the URL passed.
     * 
     * @param url
     *            the URL to be requested.
     */
    public XmlRequest(final URL url) {
        this(url.getHost(),
                url.getPort() == -1 ? url.getDefaultPort() : url.getPort(),
                url.getProtocol());

        this.address = url.getPath();
        this.query = url.getQuery();
    }

    public void addParam(final String name, final Object value) {
        if (value != null) {
            this.alSimpleParams.add(new NameValuePair(name, value.toString()));
        }

        this.method = Method.GET;
    }

    public void clearParams() {
        this.alSimpleParams.clear();
        this.postParams = null;
    }

    private Element doExecute(final HttpMethodBase httpMethod)
            throws IOException, BadXmlResponseEx {
        this.config.setHost(this.host, this.port,
                Protocol.getProtocol(this.protocol));

        if (this.useProxy) {
            this.config.setProxy(this.proxyHost, this.proxyPort);
        }

        byte[] data = null;

        try {
            this.client.executeMethod(httpMethod);
            data = httpMethod.getResponseBody();

            // HttpClient is unable to automatically handle redirects of entity
            // enclosing methods such as POST and PUT.
            // Get the location header and run the request against it.
            String redirectLocation;
            final Header locationHeader = httpMethod
                    .getResponseHeader("location");
            if (locationHeader != null) {
                redirectLocation = locationHeader.getValue();
                httpMethod.setPath(redirectLocation);
                this.client.executeMethod(httpMethod);
                data = httpMethod.getResponseBody();
            }
            return Xml.loadStream(new ByteArrayInputStream(data));
        }

        catch (final JDOMException e) {
            throw new BadXmlResponseEx(new String(data, "UTF8"));
        }

        finally {
            httpMethod.releaseConnection();

            this.sentData = this.getSentData(httpMethod);
            this.receivedData = this.getReceivedData(httpMethod, data);
        }
    }

    /**
     * Sends a request and obtains an xml response. The request can be a GET or
     * a POST depending on the method used to set parameters. Calls to the
     * 'addParam' method set a GET request while the setRequest method sets a
     * POST/xml request.
     */
    public Element execute()
            throws IOException, BadXmlResponseEx, BadSoapResponseEx {
        final HttpMethodBase httpMethod = this.setupHttpMethod();

        Element response = this.doExecute(httpMethod);

        if (this.useSOAP) {
            response = this.soapUnembed(response);
        }

        return response;
    }

    /**
     * Sends an xml request and obtains an xml response
     */
    public Element execute(final Element request)
            throws IOException, BadXmlResponseEx, BadSoapResponseEx {
        this.setRequest(request);
        return this.execute();
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

    public int getPort() {
        return this.port;
    }

    public String getReceivedData() {
        return this.receivedData;
    }

    private String getReceivedData(final HttpMethodBase httpMethod,
            final byte[] response) {
        String receivedData = "";

        try {
            // --- if there is a connection error (the server is unreachable)
            // this
            // --- call causes a NullPointerEx

            receivedData = httpMethod.getStatusText() + "\r\r";

            for (final Header h : httpMethod.getResponseHeaders()) {
                receivedData += h;
            }

            receivedData += "\r\n";

            if (response != null) {
                receivedData += new String(response, "UTF8");
            }
        } catch (final Exception e) {
            receivedData = "";
        }

        return receivedData;
    }

    public String getSentData() {
        return this.sentData;
    }

    private String getSentData(final HttpMethodBase httpMethod) {
        String sentData = httpMethod.getName() + " " + httpMethod.getPath();

        if (httpMethod.getQueryString() != null) {
            sentData += "?" + httpMethod.getQueryString();
        }

        sentData += "\r\n";

        for (final Header h : httpMethod.getRequestHeaders()) {
            sentData += h;
        }

        sentData += "\r\n";

        if (httpMethod instanceof PostMethod) {
            sentData += this.postData;
        }

        return sentData;
    }

    /**
     * Sends the content of a file using a POST request and gets the response in
     * xml format.
     */
    public Element send(final String name, final File inFile)
            throws IOException, BadXmlResponseEx, BadSoapResponseEx {
        final Part[] parts = new Part[this.alSimpleParams.size() + 1];

        int partsIndex = 0;

        parts[partsIndex] = new FilePart(name, inFile);

        for (final NameValuePair nv : this.alSimpleParams) {
            parts[++partsIndex] = new StringPart(nv.getName(), nv.getValue());
        }

        final PostMethod post = new PostMethod();
        post.setRequestEntity(
                new MultipartRequestEntity(parts, post.getParams()));
        post.addRequestHeader("Accept",
                !this.useSOAP ? "application/xml" : "application/soap+xml");
        post.setPath(this.address);
        post.setDoAuthentication(this.useAuthent());

        // --- execute request

        Element response = this.doExecute(post);

        if (this.useSOAP) {
            response = this.soapUnembed(response);
        }

        return response;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setCredentials(final String username, final String password) {
        final Credentials cred = new UsernamePasswordCredentials(username,
                password);
        final AuthScope scope = new AuthScope(AuthScope.ANY_HOST,
                AuthScope.ANY_PORT, AuthScope.ANY_REALM);

        this.client.getState().setCredentials(scope, cred);
        this.client.getParams().setAuthenticationPreemptive(true);
        this.serverAuthent = true;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setMethod(final Method m) {
        this.method = m;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setProxyCredentials(final String username,
            final String password) {
        if ((username == null) || (username.trim().length() == 0)) {
            return;
        }

        final Credentials cred = new UsernamePasswordCredentials(username,
                password);
        final AuthScope scope = new AuthScope(AuthScope.ANY_HOST,
                AuthScope.ANY_PORT, AuthScope.ANY_REALM);

        this.client.getState().setProxyCredentials(scope, cred);

        this.proxyAuthent = true;
    }

    public void setProxyHost(final String host) {
        this.proxyHost = host;
    }

    public void setProxyPort(final int port) {
        this.proxyPort = port;
    }

    public void setRequest(final Element request) {
        this.postParams = (Element) request.detach();
        this.method = Method.POST;
    }

    private HttpMethodBase setupHttpMethod()
            throws UnsupportedEncodingException {
        HttpMethodBase httpMethod;

        if (this.method == Method.GET) {
            httpMethod = new GetMethod();

            if ((this.query != null) && !this.query.equals("")) {
                httpMethod.setQueryString(this.query);
            } else if (this.alSimpleParams.size() != 0) {
                httpMethod.setQueryString(this.alSimpleParams.toArray(
                        new NameValuePair[this.alSimpleParams.size()]));
            }

            httpMethod.addRequestHeader("Accept",
                    !this.useSOAP ? "application/xml" : "application/soap+xml");
            httpMethod.setFollowRedirects(true);
        } else {
            final PostMethod post = new PostMethod();

            if (!this.useSOAP) {
                this.postData = (this.postParams == null) ? ""
                        : Xml.getString(new Document(this.postParams));
                post.setRequestEntity(new StringRequestEntity(this.postData,
                        "application/xml", "UTF8"));
            } else {
                this.postData = Xml.getString(
                        new Document(this.soapEmbed(this.postParams)));
                post.setRequestEntity(new StringRequestEntity(this.postData,
                        "application/soap+xml", "UTF8"));
            }

            httpMethod = post;
        }

        httpMethod.setPath(this.address);
        httpMethod.setDoAuthentication(this.useAuthent());

        return httpMethod;
    }

    public void setUrl(final URL url) {
        this.host = url.getHost();
        this.port = (url.getPort() == -1) ? url.getDefaultPort()
                : url.getPort();
        this.protocol = url.getProtocol();
        this.address = url.getPath();
        this.query = url.getQuery();
    }

    public void setUseProxy(final boolean yesno) {
        this.useProxy = yesno;
    }

    public void setUseSOAP(final boolean yesno) {
        this.useSOAP = yesno;
    }

    // --- transient vars

    private Element soapEmbed(final Element elem) {
        final Element envl = new Element("Envelope", XmlRequest.NAMESPACE_ENV);
        final Element body = new Element("Body", XmlRequest.NAMESPACE_ENV);

        envl.addContent(body);
        body.addContent(elem);

        return envl;
    }

    @SuppressWarnings("unchecked")
    private Element soapUnembed(final Element envelope)
            throws BadSoapResponseEx {
        final Namespace ns = envelope.getNamespace();
        final Element body = envelope.getChild("Body", ns);

        if (body == null) {
            throw new BadSoapResponseEx(Xml.getString(envelope));
        }

        final List<Element> list = body.getChildren();

        if (list.size() == 0) {
            throw new BadSoapResponseEx(Xml.getString(envelope));
        }

        return list.get(0);
    }

    private boolean useAuthent() {
        return this.proxyAuthent || this.serverAuthent;
    }
}