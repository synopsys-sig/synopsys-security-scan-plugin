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
    private final EnvVars envVars;

    public BitbucketRepositoryService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public Bitbucket fetchBitbucketRepositoryDetails(Jenkins jenkins, Map<String, Object> scanParameters, String jobName, Integer projectRepositoryPullNumber) {
        listener.getLogger().println("Getting bitbucket repository details");

        Bitbucket bitbucket = new Bitbucket();

        if (jenkins != null) {

            SCMSource scmSource = SCMRepositoryService.findSCMSource();

            if (scmSource instanceof BitbucketSCMSource) {
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;

                // If we are not able to get bitbucket token from the input directly then we will get it through the bitbucketSCMSource CredentialId
                String bitBucketToken = (String) scanParameters.get("bitbucket_token");
                if(bitBucketToken == null) {
                    bitBucketToken = SCMRepositoryService.getCredentialsToken(bitbucketSCMSource.getCredentialsId());
                }

                BitbucketApi bitbucketApiFromSCMSource = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

                // Access the repository details with BitbucketApi
                BitbucketRepository bitbucketRepository = null;
                try {
                    bitbucketRepository = bitbucketApiFromSCMSource.getRepository();
                } catch (Exception e) {
                    listener.getLogger().println("There is an exception while getting the BitbucketRepository from BitbucketApi");
                } 

                listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());

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
