/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public class Utility {

    public static String getDirectorySeparator(FilePath workspace, TaskListener listener) {
        String os = getAgentOs(workspace, listener);

        if (os != null && os.contains("win")) {
            return "\\";
        }  else {
            return "/";
        }
    }

    public static String getAgentOs(FilePath workspace, TaskListener listener) {
        String os =  null;
        LoggerWrapper logger = new LoggerWrapper(listener);

        if (workspace.isRemote()) {
            try {
                os = workspace.act(new OsNameTask());
            } catch (IOException | InterruptedException e) {
                logger.error("An exception occurred while fetching the OS information for the agent node: " + e.getMessage());
            }
        } else {
            os = System.getProperty("os.name").toLowerCase();
        }

        return os;
    }


    public static void removeFile(String filePath, FilePath workspace, TaskListener listener) {
        LoggerWrapper logger = new LoggerWrapper(listener);
        try {
            FilePath file = new FilePath(workspace.getChannel(), filePath);
            file = file.absolutize();

            if (file.exists()) {
                file.delete();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("An exception occurred while deleting file: " + e.getMessage());
        }
    }

    public static boolean isStringNullOrBlank(String str) {
        return str == null || str.isBlank() || str.equals("null");
    }

    public static HttpURLConnection getHttpURLConnection(URL url, LoggerWrapper logger) {
        HttpURLConnection connection = null;

        try {
            String envHttpsProxy = System.getenv(ApplicationConstants.HTTPS_PROXY);
            String envHttpProxy = System.getenv(ApplicationConstants.HTTP_PROXY);

            if (envHttpsProxy != null) {
                URL httpsProxyURL = new URL(envHttpsProxy);
                setDefaultProxyAuthenticator(httpsProxyURL.getUserInfo());

                Proxy httpsProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpsProxyURL.getHost(), httpsProxyURL.getPort()));
                connection = (HttpURLConnection) url.openConnection(httpsProxy);
            } else if (envHttpProxy != null) {
                URL httpProxyURL = new URL(envHttpProxy);
                setDefaultProxyAuthenticator(httpProxyURL.getUserInfo());

                Proxy httpsProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyURL.getHost(), httpProxyURL.getPort()));
                connection = (HttpURLConnection) url.openConnection(httpsProxy);
            } else {
                connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            }
        } catch (IOException e) {
            logger.error("An exception occurred while getting HttpURLConnection: " + e.getMessage());
        }

        return connection;
    }

    public static void setDefaultProxyAuthenticator(String userInfo) {
        if (!isStringNullOrBlank(userInfo)) {
            String[] userInfoArray = userInfo.split(":");
            if (userInfoArray.length == 2) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userInfoArray[0], userInfoArray[1].toCharArray());
                    }
                });
            }
        }
    }
}
