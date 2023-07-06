package com.synopsys.integration.jenkins.scan.service;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import com.synopsys.integration.jenkins.scan.input.bitbucket.*;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import java.io.IOException;

import hudson.slaves.EnvironmentVariablesNodeProperty;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

/**
 * @author akib @Date 6/14/23
 */
public class BitBucketRepositoryService {

    private final TaskListener listener;
    private final EnvVars envVars;

    public BitBucketRepositoryService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }


    public BitBucket fetchBitbucketRepoDetails() throws IOException, InterruptedException {

        String JOB_NAME = envVars.get("JOB_NAME").substring(0, envVars.get("JOB_NAME").indexOf("/"));
        Integer PROJECT_REPOSITORY_PULL_NUMBER = envVars.get("CHANGE_ID") != null ? Integer.valueOf(envVars.get("CHANGE_ID")) : 0;
        String BITBUCKET_TOKEN = envVars.get("BITBUCKET_TOKEN");

        listener.getLogger().println("Getting bitbucket repository details");

        BitBucket bitBucket = new BitBucket();

        Jenkins jenkins = Jenkins.getInstanceOrNull();

        if (jenkins != null) {

            SCMSource scmSource = findSCMSource(jenkins, JOB_NAME);

            if (scmSource instanceof BitbucketSCMSource) {
                listener.getLogger().println("Getting repository details with BitbucketSCMSource");
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;

                // Access the repository details with BitbucketSCMSource
                listener.getLogger().println("ServerUrl: " + bitbucketSCMSource.getServerUrl());

                listener.getLogger().println("Getting repository details with BitbucketApi");
                BitbucketApi bitbucketApi = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

                // Access the repository details with BitbucketApi
                BitbucketRepository bitbucketRepository = bitbucketApi.getRepository();

                listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());
                listener.getLogger().println("Repository Owner: " + bitbucketRepository.getOwnerName());
                listener.getLogger().println("Project Name: " + bitbucketRepository.getProject().getName());
                listener.getLogger().println("Project Key: " + bitbucketRepository.getProject().getKey());


                Api bitBucketApi = new Api();
                bitBucketApi.setUrl(bitbucketSCMSource.getServerUrl());
                bitBucketApi.setToken(BITBUCKET_TOKEN);

                Pull pull = new Pull();
                pull.setNumber(PROJECT_REPOSITORY_PULL_NUMBER);

                Repository repository = new Repository();
                repository.setName(bitbucketRepository.getRepositoryName());
                repository.setPull(pull);

                Project project = new Project();
                project.setKey(bitbucketRepository.getProject().getKey());
                project.setRepository(repository);

                bitBucket.setApi(bitBucketApi);
                bitBucket.setProject(project);

            } else {
                listener.getLogger().println("SCM source is not a BitbucketSCMSource.");
            }
        } else {
            listener.getLogger().println("Jenkins instance not found.");
        }

        return bitBucket;

    }

    private static SCMSource findSCMSource(Jenkins jenkins, String jobName) {
        SCMSourceOwner owner = jenkins.getItemByFullName(jobName, SCMSourceOwner.class);
        if (owner != null) {
            for (SCMSource scmSource : owner.getSCMSources()) {
                // Check if the SCM source belongs to the job
                if (owner.getSCMSource(scmSource.getId()) != null) {
                    return scmSource;
                }
            }
        }
        return null;
    }

}
