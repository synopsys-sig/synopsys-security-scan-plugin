package com.synopsys.integration.jenkins.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class SCMRepositoryService {
    private final TaskListener listener;
    private final EnvVars envVars;
    private static Jenkins jenkins;
    private static String jobName;

    public SCMRepositoryService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public Object fetchSCMRepositoryDetails(Map<String, Object> scanParameters) {
        jenkins = Jenkins.getInstanceOrNull();
        // extracting the job name from the combined job_branch name
        jobName = envVars.get("JOB_NAME").substring(0, envVars.get("JOB_NAME").indexOf("/"));
        // getting the pull number from env
        Integer projectRepositoryPullNumber = envVars.get("CHANGE_ID") != null ? Integer.valueOf(envVars.get("CHANGE_ID")) : 0;

        SCMSource scmSource = findSCMSource();
        // getting the bitbucket repository details
        if (scmSource instanceof BitbucketSCMSource) {
            BitbucketRepositoryService bitbucketRepositoryService = new BitbucketRepositoryService(listener, envVars);
            return bitbucketRepositoryService.fetchBitbucketRepositoryDetails(jenkins, scanParameters, jobName, projectRepositoryPullNumber);
        }
        return null;
    }

    public static SCMSource findSCMSource() {
        SCMSourceOwner owner = jenkins != null ? jenkins.getItemByFullName(jobName, SCMSourceOwner.class) : null;
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

    public static String getCredentialsToken(String credentialsId) {
        // Creating FreeStyleProject, FreeStyleBuild and using these to get the bitbucket token
        FreeStyleProject freeStyleProject = new FreeStyleProject(jenkins, jobName);
        FreeStyleBuild freeStyleBuild = null;

        try {
            freeStyleBuild = new FreeStyleBuild(freeStyleProject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StandardCredentials credentials = CredentialsProvider.findCredentialById(credentialsId, StandardCredentials.class, freeStyleBuild, Collections.emptyList());
        if (credentials instanceof UsernamePasswordCredentialsImpl) {
            UsernamePasswordCredentialsImpl usernamePasswordCredentials = (UsernamePasswordCredentialsImpl) credentials;
            return usernamePasswordCredentials.getPassword().getPlainText();
        }
        return null;   /// Todo: Handle other types of credentials if needed
    }

}
