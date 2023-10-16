/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import com.synopsys.integration.jenkins.scan.exception.PluginExceptionHandler;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Pull;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Repository;
import hudson.model.TaskListener;
import java.util.Map;

public class BitbucketRepositoryService {
    private final LoggerWrapper logger;

    public BitbucketRepositoryService(TaskListener listener) {
        this.logger = new LoggerWrapper(listener);
    }

    public Bitbucket fetchBitbucketRepositoryDetails(Map<String, Object> scanParameters, 
                                                     BitbucketSCMSource bitbucketSCMSource, 
                                                     Integer projectRepositoryPullNumber,
                                                     boolean isFixPrOrPrComment) throws PluginExceptionHandler {

        String bitbucketToken = (String) scanParameters.get(ApplicationConstants.BITBUCKET_TOKEN_KEY);
        if (Utility.isStringNullOrBlank(bitbucketToken) && isFixPrOrPrComment) {
            logger.error(LogMessages.NO_BITBUCKET_TOKEN_FOUND);
            throw new PluginExceptionHandler(LogMessages.NO_BITBUCKET_TOKEN_FOUND);
        }

        BitbucketApi bitbucketApiFromSCMSource = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

        BitbucketRepository bitbucketRepository = null;
        try {
            bitbucketRepository = bitbucketApiFromSCMSource.getRepository();
        } catch (Exception e) {
            logger.error("An exception occurred while getting the BitbucketRepository from BitbucketApi: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        String serverUrl = bitbucketSCMSource.getServerUrl();
        String repositoryName = null;
        String projectKey = null;

        if (bitbucketRepository != null) {
            repositoryName = bitbucketRepository.getRepositoryName();
            projectKey = bitbucketRepository.getProject().getKey();
        }

        return createBitbucketObject(serverUrl, bitbucketToken, projectRepositoryPullNumber, repositoryName, projectKey);
    }

    public static Bitbucket createBitbucketObject(String serverUrl, String bitbucketToken, Integer projectRepositoryPullNumber, String repositoryName, String projectKey) {
        Bitbucket bitbucket = new Bitbucket();
        bitbucket.getApi().setUrl(serverUrl);
        bitbucket.getApi().setToken(bitbucketToken);

        Pull pull = new Pull();
        pull.setNumber(projectRepositoryPullNumber);

        Repository repository = new Repository();
        repository.setName(repositoryName);
        repository.setPull(pull);

        bitbucket.getProject().setKey(projectKey);
        bitbucket.getProject().setRepository(repository);

        return bitbucket;
    }

}
