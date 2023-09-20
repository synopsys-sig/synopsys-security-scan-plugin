package com.synopsys.integration.jenkins.scan.global;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UtilityTest {
    private FilePath workspace;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private LoggerWrapper logger;
    private URL url;

    @BeforeEach
    void setup() {
        workspace = new FilePath(new File(getHomeDirectory()));
        logger = new LoggerWrapper(listenerMock);
        when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));

        try {
            url = new URL("https://fake-url.com");
        } catch (MalformedURLException e) {
            System.out.println("Exception occurred while creating url in test");
        }
    }

    @Test
    public void getDirectorySeparatorTest() {

        String separator = Utility.getDirectorySeparator(workspace, listenerMock);
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("win")) {
            assertEquals("\\", separator);
        } else {
            assertEquals("/", separator);
        }
    }

    @Test
    public void getAgentOsTest() {
        String os = Utility.getAgentOs(workspace, listenerMock);

        assertEquals(System.getProperty("os.name").toLowerCase(), os);
    }

    @Test
    public void testRemoveFile() throws IOException {
        File tempFile = new File(getHomeDirectory(), "testfile.txt");
        tempFile.createNewFile();
        String filePath = tempFile.getAbsolutePath();

        Utility.removeFile(filePath, workspace, listenerMock);

        assertFalse(tempFile.exists());
    }

    @Test
    public void isStringNullOrBlankTest() {
        String str = null;
        String emptyString = "";
        String emptyStringContainingSpace = "   ";
        String validString = " This is a valid string  ";

        assertTrue(Utility.isStringNullOrBlank(str));
        assertTrue(Utility.isStringNullOrBlank(emptyString));
        assertTrue(Utility.isStringNullOrBlank(emptyStringContainingSpace));
        assertFalse(Utility.isStringNullOrBlank(validString));
    }
    
    @Test
    public void getHttpURLConnectionTest() {
        EnvVars envVars = new EnvVars();
        envVars.put("HTTP_PROXY", "http://fake-proxy.com:1010");

        HttpURLConnection httpProxyConnection = Utility.getHttpURLConnection(url, envVars, logger);

        assertNotNull(httpProxyConnection);
        assertEquals(url, httpProxyConnection.getURL());

        envVars.put("NO_PROXY", "https://test-url.com, https://fake-url.com");

        HttpURLConnection noProxyConnection = Utility.getHttpURLConnection(url, envVars, logger);

        assertNotNull(noProxyConnection);
        assertEquals(url, noProxyConnection.getURL());
    }

    @Test
    public void getProxyTest() {
        EnvVars envVars = new EnvVars();

        assertEquals(ApplicationConstants.NO_PROXY, Utility.getProxy(url, envVars));

        envVars.put("NO_PROXY", "https://test-url.com, https://fake-url.com");

        assertEquals(ApplicationConstants.NO_PROXY, Utility.getProxy(url, envVars));

        envVars.put("HTTP_PROXY", "https://fake-proxy.com:1010");
        envVars.replace("NO_PROXY", "https://test-url.com");
        envVars.put("HTTPS_PROXY", "https://fake-proxy.com:1010");

        assertEquals(envVars.get("HTTPS_PROXY"), Utility.getProxy(url, envVars));

        envVars.remove("HTTPS_PROXY");

        assertEquals(envVars.get("HTTP_PROXY"), Utility.getProxy(url, envVars));
    }

    @Test
    public void getEnvOrSystemProxyDetailsTest() {
        EnvVars envVars = new EnvVars();

        assertNull(Utility.getEnvOrSystemProxyDetails(ApplicationConstants.NO_PROXY, envVars));
        assertNull(Utility.getEnvOrSystemProxyDetails(ApplicationConstants.HTTP_PROXY, envVars));
        assertNull(Utility.getEnvOrSystemProxyDetails(ApplicationConstants.HTTPS_PROXY, envVars));

        envVars.put("NO_PROXY", "https://test-url.com, https://fake-url.com");
        envVars.put("HTTP_PROXY", "https://fake-proxy.com:1010");
        envVars.put("HTTPS_PROXY", "https://fake-proxy.com:1010");

        assertEquals(envVars.get("NO_PROXY"), Utility.getEnvOrSystemProxyDetails(ApplicationConstants.NO_PROXY, envVars));
        assertEquals(envVars.get("HTTP_PROXY"), Utility.getEnvOrSystemProxyDetails(ApplicationConstants.HTTP_PROXY, envVars));
        assertEquals(envVars.get("HTTPS_PROXY"), Utility.getEnvOrSystemProxyDetails(ApplicationConstants.HTTPS_PROXY, envVars));
    }
    
    @Test
    public void setDefaultProxyAuthenticatorTest() {
        Authenticator.setDefault(null);

        Utility.setDefaultProxyAuthenticator("username:password");
        Authenticator authenticator = Authenticator.getDefault();
        assertNotNull(authenticator);

        PasswordAuthentication passwordAuth = invokeGetPasswordAuthentication(authenticator);
        assertNotNull(passwordAuth);
        assertEquals("username", passwordAuth.getUserName());
        assertArrayEquals("password".toCharArray(), passwordAuth.getPassword());
        
        Authenticator.setDefault(null);
    }

    @Test
    public void testSetDefaultProxyAuthenticatorWithInvalidUserInfo() {
        Authenticator.setDefault(null);

        Utility.setDefaultProxyAuthenticator("invalidUserInfo");

        assertNull(Authenticator.getDefault());
    }

    private PasswordAuthentication invokeGetPasswordAuthentication(Authenticator authenticator) {
        try {
            Method getPasswordAuthenticationMethod = Authenticator.class.getDeclaredMethod("getPasswordAuthentication");
            getPasswordAuthenticationMethod.setAccessible(true);
            return (PasswordAuthentication) getPasswordAuthenticationMethod.invoke(authenticator);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking getPasswordAuthentication via reflection", e);
        }
    }

    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }
}
