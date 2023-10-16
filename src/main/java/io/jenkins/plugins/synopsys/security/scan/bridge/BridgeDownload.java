/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.bridge;

import io.jenkins.plugins.synopsys.security.scan.exception.PluginExceptionHandler;
import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;
import io.jenkins.plugins.synopsys.security.scan.global.LogMessages;
import io.jenkins.plugins.synopsys.security.scan.global.LoggerWrapper;
import io.jenkins.plugins.synopsys.security.scan.global.Utility;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BridgeDownload {
    private final LoggerWrapper logger;
    private final FilePath workspace;
    private final EnvVars envVars;

    public BridgeDownload(FilePath workspace, TaskListener listener, EnvVars envVars) {
        this.workspace = workspace;
        this.logger = new LoggerWrapper(listener);
        this.envVars = envVars;
    }

    public FilePath downloadSynopsysBridge(String bridgeDownloadUrl, String bridgeInstallationPath) throws Exception {
        FilePath bridgeZipFilePath = null;
        FilePath bridgeInstallationFilePath = new FilePath(workspace.getChannel(), bridgeInstallationPath);

        if (!checkIfBridgeUrlExists(bridgeDownloadUrl)) {
            logger.error(LogMessages.INVALID_SYNOPSYS_BRIDGE_DOWNLOAD_URL, bridgeDownloadUrl);
        }

        int retryCount = 1;
        boolean downloadSuccess = false;

        while (!downloadSuccess && retryCount <= ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES) {
            try {
                logger.info("Downloading Synopsys Bridge from: " + bridgeDownloadUrl);
                bridgeZipFilePath = downloadBridge(bridgeDownloadUrl, bridgeInstallationFilePath);

                if (bridgeZipFilePath != null) {
                    downloadSuccess = true;
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting to retry Synopsys Bridge download");
                throw e;
            } catch (Exception e) {
                handleDownloadException(e, bridgeDownloadUrl, retryCount);
                retryCount++;
            }
        }

        if (!downloadSuccess) {
            logger.error("Synopsys Bridge download failed after %s attempts", ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES);
        }

        if (bridgeZipFilePath == null) {
            throw new PluginExceptionHandler(LogMessages.SYNOPSYS_BRIDGE_DOWNLOAD_FAILED);
        }

        return bridgeZipFilePath;
    }

    private FilePath downloadBridge(String bridgeDownloadUrl, FilePath bridgeInstallationFilePath) throws Exception {
        FilePath bridgeZipFilePath = bridgeInstallationFilePath.child(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT);
        HttpURLConnection connection = Utility.getHttpURLConnection(new URL(bridgeDownloadUrl), envVars, logger);

        if (connection != null) {
            bridgeZipFilePath.copyFrom(connection.getURL());
            logger.info("Synopsys Bridge successfully downloaded in: " + bridgeZipFilePath);
        }

        return bridgeZipFilePath;
    }

    private void handleDownloadException(Exception e, String bridgeDownloadUrl, int retryCount) throws Exception {
        int statusCode = getHttpStatusCode(bridgeDownloadUrl);

        if (terminateRetry(statusCode)) {
            logger.error("Synopsys Bridge download failed with status code: %s and plugin won't retry to download.", statusCode);
            throw e;
        }

        Thread.sleep(ApplicationConstants.INTERVAL_BETWEEN_CONSECUTIVE_RETRY_ATTEMPTS);
        logger.warn("Synopsys Bridge download failed and attempt#%s to download again.", retryCount);
    }


    public int getHttpStatusCode(String url) {
        int statusCode = -1;

        try {
            HttpURLConnection connection = Utility.getHttpURLConnection(new URL(url), envVars, logger);
            if (connection != null) {
                connection.setRequestMethod("HEAD");
                statusCode = connection.getResponseCode();
                connection.disconnect();
            }
        } catch (IOException e) {
            logger.error("An exception occurred while checking the http status code: " + e.getMessage());
        }

        return statusCode;
    }

    public boolean terminateRetry(int statusCode) {
        return statusCode == HttpURLConnection.HTTP_UNAUTHORIZED ||
                statusCode == HttpURLConnection.HTTP_FORBIDDEN ||
                statusCode == HttpURLConnection.HTTP_OK ||
                statusCode == HttpURLConnection.HTTP_CREATED ||
                statusCode == 416;
    }

    public boolean checkIfBridgeUrlExists(String bridgeDownloadUrl) {
        try {
            URL url = new URL(bridgeDownloadUrl);

            HttpURLConnection connection = Utility.getHttpURLConnection(url, envVars, logger);
            if (connection != null) {
                connection.setRequestMethod("HEAD");
                return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
        } catch (Exception e) {
            logger.error("An exception occurred while checking bridge url exists or not: " + e.getMessage());
        }
        return false;
    }
}
