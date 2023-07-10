package com.synopsys.integration.jenkins.scan.service.bitbucket;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.synopsys.integration.jenkins.scan.input.bitbucket.*;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Api;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Project;
import hudson.EnvVars;
import hudson.model.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

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


    public BitBucket fetchBitbucketRepoDetails(Map<String, Object> scanParameters) throws IOException, InterruptedException {

        // extracting the job name from the combined job_branch name
        String jobName = envVars.get("JOB_NAME").substring(0, envVars.get("JOB_NAME").indexOf("/"));

        //getting the pull number from env
        Integer projectRepositoryPullNumber = envVars.get("CHANGE_ID") != null ? Integer.valueOf(envVars.get("CHANGE_ID")) : 0;

        listener.getLogger().println("Getting bitbucket repository details");

        BitBucket bitBucket = new BitBucket();

        Jenkins jenkins = Jenkins.getInstanceOrNull();

        if (jenkins != null) {

            SCMSource scmSource = findSCMSource(jenkins, jobName);

            if (scmSource instanceof BitbucketSCMSource) {
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;

                // Creating and using them to get the bitbucket token
                FreeStyleProject freeStyleProject = new FreeStyleProject(jenkins, jobName);
                FreeStyleBuild freeStyleBuild = new FreeStyleBuild(freeStyleProject);

                // If we are not able to get bitbucket token from the input directly then we will get it through the bitbucketSCMSource CredentialId
                String bitBucketToken = (String) scanParameters.get("bitbucket_token");
                if(bitBucketToken == null) {
                    bitBucketToken = getCredentialsToken(bitbucketSCMSource.getCredentialsId(), freeStyleBuild);
                }

                BitbucketApi bitbucketApi = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

                // Access the repository details with BitbucketApi
                BitbucketRepository bitbucketRepository = bitbucketApi.getRepository();

                listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());

                Api bitBucketApi = new Api();
                bitBucketApi.setUrl(bitbucketSCMSource.getServerUrl());
                bitBucketApi.setToken(bitBucketToken);

                Pull pull = new Pull();
                pull.setNumber(projectRepositoryPullNumber);

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

    private static String getCredentialsToken(String credentialsId, FreeStyleBuild freeStyleBuild) {
        StandardCredentials credentials = CredentialsProvider.findCredentialById(credentialsId, StandardCredentials.class, freeStyleBuild, Collections.emptyList());

        if (credentials instanceof UsernamePasswordCredentialsImpl) {
            UsernamePasswordCredentialsImpl usernamePasswordCredentials = (UsernamePasswordCredentialsImpl) credentials;
            return usernamePasswordCredentials.getPassword().getPlainText();
        }

        return null;  // Handle other types of credentials if needed
    }

}
