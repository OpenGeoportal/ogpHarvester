package org.opengeoportal.harvester.api.client.csw.request;


import org.apache.commons.lang3.StringUtils;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.geonetwork.Xml;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.exception.CatalogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CatalogRequest
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public enum Method { GET, POST }

    private String  host;
    private int     port;
    private String  protocol;
    private String  address;
    private String  path;
    private Method  method;
    private boolean useSOAP;
    protected String outputSchema;

    protected String serverVersion = Csw.CSW_VERSION;  // Sets default value

    private HttpClient client = new HttpClient();

    private ArrayList<NameValuePair> alGetParams;
    private ArrayList<NameValuePair> alSetupGetParams;

    // Parameters to not take into account in GetRequest
    private static final List<String> excludedParameters = Arrays.asList("", "request", "version", "service");

    //--- transient vars

    private String sentData;
    private String receivedData;
    private String postData;


    public CatalogRequest(URL cswServerUrl) {
        this.host    = cswServerUrl.getHost();
        this.port    = cswServerUrl.getPort();
        this.protocol= cswServerUrl.getProtocol();

        setMethod(Method.POST);
        Cookie cookie = new Cookie();
        HttpState state = new HttpState();
        state.addCookie(cookie);
        client.setState(state);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getAddress() {
        return address;
    }

    public Method getMethod() {
        return method;
    }

    public String getSentData() {
        return sentData;
    }

    public String getReceivedData() {
        return receivedData;
    }

    public String getOutputSchema() {
        return outputSchema;
    }

    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Set request URL host, port, address and path.
     * If URL contains query string parameters, those parameters are
     * preserved (and {@link CatalogRequest#excludedParameters} are
     * excluded). A complete GetCapabilities URL may be used for initialization.
     */
    public void setUrl(URL url) {
        this.host    = url.getHost();
        this.port    = url.getPort();
        this.protocol= url.getProtocol();
        this.address = url.toString();
        this.path = url.getPath();

        alSetupGetParams = new ArrayList<NameValuePair>();
        String query = url.getQuery();

        if (StringUtils.isNotEmpty(query)) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] kvp = param.split("=");
                if (!excludedParameters.contains(kvp[0].toLowerCase())) {
                    this.alSetupGetParams.add(new NameValuePair(kvp[0], kvp[1]));
                }
            }
        }

        if (this.port == -1) {
            this.port = url.getDefaultPort();
        }
    }

    public void setMethod(Method m) {
        method = m;
    }

    public void setUseSOAP(boolean yesno) {
        useSOAP = yesno;
    }

    public Element execute() throws Exception {
        HttpMethodBase httpMethod = setupHttpMethod();

        Element response = doExecute(httpMethod);

        if (useSOAP) {
            response = soapUnembed(response);
        }

        //--- raises an exception if the case
        CatalogException.unmarshal(response);

        return response;
    }

    protected abstract String  getRequestName();

    protected abstract void    setupGetParams();

    protected abstract Element getPostParams ();

    //--- GET fill methods
    protected void fill(String param, Iterable iter) {
        fill(param, iter, "");
    }

    protected void fill(String param, Iterable iter, String prefix) {
        Iterator i = iter.iterator();

        if (!i.hasNext()) return;

        StringBuffer sb = new StringBuffer();

        while(i.hasNext()) {
            sb.append(prefix);
            sb.append(i.next());

            if (i.hasNext()) {
                sb.append(',');
            }
        }

        addParam(param, sb.toString());
    }

    //--- POST fill methods
    protected void fill(Element root, String parentName, String childName,
                        Iterable iter, Namespace ns) {
        Iterator i = iter.iterator();

        if (!i.hasNext()) return;

        Element parent = new Element(parentName, ns);

        while(i.hasNext()) {
            Element el = new Element(childName, ns);
            el.setText(i.next().toString());

            parent.addContent(el);
        }

        root.addContent(parent);
    }

    protected void fill(Element root, String childName, Iterable iter) {
        Iterator i = iter.iterator();

        if (!i.hasNext()) return;

        while(i.hasNext()) {
            Element el = new Element(childName, root.getNamespace());
            el.setText(i.next().toString());

            root.addContent(el);
        }
    }

    
    //--- Attribute facilities
    protected void setAttrib(Element el, String name, Object value) {
        setAttrib(el, name, value, "");
    }

    

    protected void setAttrib(Element el, String name, Object value, String prefix) {
        if (value != null)
            el.setAttribute(name, prefix + value.toString());
    }

    protected void setAttrib(Element el, String name, Iterable iter, String prefix) {
        Iterator i = iter.iterator();

        if (!i.hasNext())
            return;

        StringBuffer sb = new StringBuffer();

        while(i.hasNext())
        {
            sb.append(prefix);
            sb.append(i.next().toString());

            if (i.hasNext())
                sb.append(" ");
        }

        el.setAttribute(name, sb.toString());
    }

    protected void setAttribComma(Element el, String name, Iterable iter, String prefix) {
        Iterator i = iter.iterator();

        if (!i.hasNext()) return;

        StringBuffer sb = new StringBuffer();

        while(i.hasNext()) {
            Object value =  i.next();

            sb.append(prefix);
            sb.append(value.toString());

            if (i.hasNext())
                sb.append(',');
        }

        el.setAttribute(name, sb.toString());
    }

    //--- Parameters facilities (POST)
    protected void addParam(Element root, String name, Object value) {
        if (value != null)
            root.addContent(new Element(name, Csw.NAMESPACE_CSW).setText(value.toString()));
    }

    
    //--- Parameters facilities (GET)
    protected void addParam(String name, Object value) {
        addParam(name, value, "");
    }

    protected void addParam(String name, Object value, String prefix) {
        if (value != null)
            alGetParams.add(new NameValuePair(name, prefix+value.toString()));
    }

    private Element doExecute(HttpMethodBase httpMethod) throws IOException, JDOMException, CatalogException {
        client.getHostConfiguration().setHost(host, port, protocol);

        byte[] data = null;

        try
        {
            client.executeMethod(httpMethod);

            if (httpMethod.getStatusCode() == 200) {
                return Xml.loadStream(httpMethod.getResponseBodyAsStream());
            } else {
                throw new CatalogException("http" + httpMethod.getStatusCode(), httpMethod.getStatusText(), "");
            }
        } finally {
            httpMethod.releaseConnection();
            try {
                setupSentData(httpMethod);
                setupReceivedData(httpMethod, data);
            } catch (Throwable e) {
                logger.warn("Exception was raised during cleanup of a CSW request : " + e.getMessage());
            }
        }
    }

    private HttpMethodBase setupHttpMethod() throws UnsupportedEncodingException {
        HttpMethodBase httpMethod;

        if (method == Method.GET) {
            alGetParams = new ArrayList<NameValuePair>();

            if (alSetupGetParams.size() != 0) {
                alGetParams.addAll(alSetupGetParams);
            }

            setupGetParams();
            httpMethod = new GetMethod();
            httpMethod.setPath(path);
            httpMethod.setQueryString(alGetParams.toArray(new NameValuePair[1]));
            logger.debug("GET params:"+httpMethod.getQueryString());
            if (useSOAP) {
                httpMethod.addRequestHeader("Accept", "application/soap+xml");
            }

        } else {
            Element    params = getPostParams();
            PostMethod post   = new PostMethod();

            if (!useSOAP) {
                postData = Xml.getString(new Document(params));
                post.setRequestEntity(new StringRequestEntity(postData, "application/xml", "UTF8"));
            } else {
                postData = Xml.getString(new Document(soapEmbed(params)));
                post.setRequestEntity(new StringRequestEntity(postData, "application/soap+xml", "UTF8"));
            }

            logger.debug("POST params:"+Xml.getString(params));
            httpMethod = post;
            httpMethod.setPath(address);
        }

//		httpMethod.setFollowRedirects(true);

        return httpMethod;
    }

    private void setupSentData(HttpMethodBase httpMethod) {
        sentData = httpMethod.getName() +" "+ httpMethod.getPath();

        if (httpMethod.getQueryString() != null) {
            sentData += "?"+ httpMethod.getQueryString();
        }

        sentData += "\r\n";

        for (Header h : httpMethod.getRequestHeaders()) {
            sentData += h;
        }

        sentData += "\r\n";

        if (httpMethod instanceof PostMethod) {
            sentData += postData;
        }
    }

    private void setupReceivedData(HttpMethodBase httpMethod, byte[] response) {
        receivedData = httpMethod.getStatusText() +"\r\r";

        for (Header h : httpMethod.getResponseHeaders()) {
            receivedData += h;
        }

        receivedData += "\r\n";

        try {
            if (response != null) {
                receivedData += new String(response, "UTF8");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO
        }
    }

    private Element soapEmbed(Element elem) {
        Element envl = new Element("Envelope", Csw.NAMESPACE_ENV);
        Element body = new Element("Body",     Csw.NAMESPACE_ENV);

        envl.addContent(body);
        body.addContent(elem);

        return envl;
    }

    private Element soapUnembed(Element envelope) throws Exception {
        Namespace ns   = envelope.getNamespace();
        Element   body = envelope.getChild("Body", ns);

        if (body == null) {
            throw new Exception("Bad SOAP response");
        }

        List list = body.getChildren();

        if (list.size() == 0) {
            throw new Exception("Bas SOAP response");
        }

        return (Element) list.get(0);
    }


}


