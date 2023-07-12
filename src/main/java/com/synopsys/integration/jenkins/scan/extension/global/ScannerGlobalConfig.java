package com.synopsys.integration.jenkins.scan.extension.global;


import hudson.Extension;
import hudson.Functions;
import hudson.util.IOUtils;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jenkins.model.GlobalConfiguration;

import jenkins.model.Jenkins;
import jenkins.util.xml.XMLUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Extension
public class ScannerGlobalConfig extends GlobalConfiguration implements Serializable {
    private static final long serialVersionUID = -3129542889827231427L;

    private String blackDuckUrl;

    private String blackDuckCredentialsId;

    private String bitbucketApiToken;

    private boolean trustBlackDuckCertificates;

    private int blackDuckTimeout = 120;

    @DataBoundConstructor
    public ScannerGlobalConfig() {
        load();
    }

    public String getBlackDuckUrl() {
        return blackDuckUrl;
    }

    @DataBoundSetter
    public void setBlackDuckUrl(String blackDuckUrl) {
        this.blackDuckUrl = blackDuckUrl;
        save();
    }

    public int getBlackDuckTimeout() {
        return blackDuckTimeout;
    }

    @DataBoundSetter
    public void setBlackDuckTimeout(int blackDuckTimeout) {
        this.blackDuckTimeout = blackDuckTimeout;
        save();
    }

    public String getBlackDuckCredentialsId() {
        return blackDuckCredentialsId;
    }

    @DataBoundSetter
    public void setBlackDuckCredentialsId(String blackDuckCredentialsId) {
        this.blackDuckCredentialsId = blackDuckCredentialsId;
        save();
    }

    public String getBitbucketApiToken() {
        return bitbucketApiToken;
    }

    @DataBoundSetter
    public void setBitbucketApiToken(String bitbucketApiToken) {
        this.bitbucketApiToken = bitbucketApiToken;
        save();
    }

    public boolean getTrustBlackDuckCertificates() {
        return trustBlackDuckCertificates;
    }

    @DataBoundSetter
    public void setTrustBlackDuckCertificates(boolean trustBlackDuckCertificates) {
        this.trustBlackDuckCertificates = trustBlackDuckCertificates;
        save();
    }

    @WebMethod(name = "config.xml")
    public void doConfigDotXml(StaplerRequest req, StaplerResponse rsp) throws IOException,
        ServletException, ParserConfigurationException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        boolean changed = false;
        try {
            if (this.getClass().getClassLoader() != originalClassLoader) {
                changed = true;
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }

            Functions.checkPermission(Jenkins.ADMINISTER);
            if (req.getMethod().equals("GET")) {
                // read
                rsp.setContentType("application/xml");
                IOUtils.copy(getConfigFile().getFile(), rsp.getOutputStream());
                return;
            }
            Functions.checkPermission(Jenkins.ADMINISTER);
            if (req.getMethod().equals("POST")) {
                // submission
                updateByXml(new StreamSource(req.getReader()));
                return;
            }
            // huh?
            rsp.sendError(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            if (changed) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    private void updateByXml(Source source) throws IOException, ParserConfigurationException {
        Document doc;
        try (StringWriter out = new StringWriter()) {
            // this allows us to use UTF-8 for storing data,
            // plus it checks any well-formedness issue in the submitted
            // data
            XMLUtils.safeTransform(source, new StreamResult(out));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(out.toString()));

            doc = builder.parse(is);
        } catch (TransformerException | SAXException e) {
            throw new IOException("Failed to persist configuration.xml", e);
        }

        String url = getNodeValue(doc, "blackDuckUrl").orElse(StringUtils.EMPTY);
        String credentialsId = getNodeValue(doc, "blackDuckCredentialsId").orElse(StringUtils.EMPTY);
        String bitbucketToken = getNodeValue(doc, "bitbucketApiToken").orElse(StringUtils.EMPTY);
        int timeout = getNodeIntegerValue(doc, "blackDuckTimeout").orElse(120);
        boolean trustCerts = getNodeBooleanValue(doc, "trustBlackDuckCertificates").orElse(false);

        setBlackDuckUrl(url);
        setBlackDuckCredentialsId(credentialsId);
        setBitbucketApiToken(bitbucketToken);
        setBlackDuckTimeout(timeout);
        setTrustBlackDuckCertificates(trustCerts);
        save();
    }

    private Optional<String> getNodeValue(Document doc, String tagName) {
        return Optional.ofNullable(doc.getElementsByTagName(tagName).item(0))
            .map(Node::getFirstChild)
            .map(Node::getNodeValue)
            .map(String::trim);
    }

    private Optional<Integer> getNodeIntegerValue(Document doc, String tagName) {
        try {
            return getNodeValue(doc, tagName).map(Integer::valueOf);
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private Optional<Boolean> getNodeBooleanValue(Document doc, String tagName) {
        return getNodeValue(doc, tagName).map(Boolean::valueOf);
    }

}
