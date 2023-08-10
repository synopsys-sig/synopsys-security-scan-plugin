package com.synopsys.integration.jenkins.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.EnvVars;
import hudson.model.TaskListener;
import java.util.Map;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

public class SCMRepositoryService {
    private final TaskListener listener;
    private final EnvVars envVars;
    private static String jobName;

    public SCMRepositoryService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public Object fetchSCMRepositoryDetails(Map<String, Object> scanParameters) throws ScannerJenkinsException {
        jobName = envVars.get(ApplicationConstants.ENV_JOB_NAME_KEY)
                .substring(0, envVars.get(ApplicationConstants.ENV_JOB_NAME_KEY).indexOf("/"));
        Integer projectRepositoryPullNumber = envVars.get(ApplicationConstants.ENV_CHANGE_ID_KEY) != null ?
                Integer.parseInt(envVars.get(ApplicationConstants.ENV_CHANGE_ID_KEY)) : null;

        SCMSource scmSource = findSCMSource();
        if (scmSource instanceof BitbucketSCMSource) {
            BitbucketRepositoryService bitbucketRepositoryService = new BitbucketRepositoryService(listener);
            return bitbucketRepositoryService.fetchBitbucketRepositoryDetails( scanParameters, projectRepositoryPullNumber);
        }
        return null;
    }

    public static SCMSource findSCMSource() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        SCMSourceOwner owner = jenkins != null ? jenkins.getItemByFullName(jobName, SCMSourceOwner.class) : null;
        if (owner != null) {
            for (SCMSource scmSource : owner.getSCMSources()) {
                if (owner.getSCMSource(scmSource.getId()) != null) {
                    return scmSource;
                }
            }
        }
        return null;
    }

}
