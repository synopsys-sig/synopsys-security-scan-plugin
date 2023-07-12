package com.synopsys.integration.jenkins.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import com.synopsys.integration.jenkins.scan.input.bitbucket.*;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Api;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Project;
import hudson.EnvVars;
import hudson.model.*;

import java.io.IOException;
import java.util.Map;

import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;


public class BitbucketRepositoryService {
    private final TaskListener listener;

    public BitbucketRepositoryService(TaskListener listener) {
        this.listener = listener;
    }

    public Bitbucket fetchBitbucketRepositoryDetails(Jenkins jenkins, Map<String, Object> scanParameters, Integer projectRepositoryPullNumber) {
        listener.getLogger().println("Getting bitbucket repository details");

        Bitbucket bitbucket = new Bitbucket();

        if (jenkins != null) {

            SCMSource scmSource = SCMRepositoryService.findSCMSource();

            if (scmSource instanceof BitbucketSCMSource) {
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;

                String bitBucketToken = (String) scanParameters.get("bitbucket_token");
                if(bitBucketToken == null) {
                    bitBucketToken = SCMRepositoryService.getCredentialsToken(bitbucketSCMSource.getCredentialsId());
                }

                BitbucketApi bitbucketApiFromSCMSource = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

                BitbucketRepository bitbucketRepository = null;
                try {
                    bitbucketRepository = bitbucketApiFromSCMSource.getRepository();
                } catch (Exception e) {
                    listener.getLogger().println("There is an exception while getting the BitbucketRepository from BitbucketApi");
                }

                if(bitbucketRepository != null) {
                    listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());
                }

                Api bitbucketApi = new Api();
                bitbucketApi.setUrl(bitbucketSCMSource.getServerUrl());
                bitbucketApi.setToken(bitBucketToken);

                Pull pull = new Pull();
                pull.setNumber(projectRepositoryPullNumber);

                Repository repository = new Repository();
                repository.setName(bitbucketRepository.getRepositoryName());
                repository.setPull(pull);

                Project project = new Project();
                project.setKey(bitbucketRepository.getProject().getKey());
                project.setRepository(repository);

                bitbucket.setApi(bitbucketApi);
                bitbucket.setProject(project);

            } else {
                listener.getLogger().println("Ignoring bitbucket_automation_fixpr and bitbucket_automation_prcomment since couldn't find any valid Bitbucket SCM source.");
            }
        } else {
            listener.getLogger().println("Jenkins instance not found.");
        }
        return bitbucket;
    }

}