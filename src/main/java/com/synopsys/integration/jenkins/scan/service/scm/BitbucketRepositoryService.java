package com.synopsys.integration.jenkins.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Pull;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Repository;
import hudson.model.TaskListener;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.Utility;
import java.util.Map;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;

public class BitbucketRepositoryService {
    private final TaskListener listener;

    public BitbucketRepositoryService(TaskListener listener) {
        this.listener = listener;
    }

    public Bitbucket fetchBitbucketRepositoryDetails(Jenkins jenkins, Map<String, Object> scanParameters, Integer projectRepositoryPullNumber) throws ScannerJenkinsException {
        listener.getLogger().println("Getting bitbucket repository details");

        Bitbucket bitbucket = new Bitbucket();

        if (jenkins != null) {

            SCMSource scmSource = SCMRepositoryService.findSCMSource();

            if (scmSource instanceof BitbucketSCMSource) {
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;

                String bitbucketToken = (String) scanParameters.get("bitbucket_token");

                if (Utility.isStringNullOrBlank(bitbucketToken)) {
                    if (!Utility.isStringNullOrBlank(getBitbucketTokenFromGlobalConfig())) {
                        bitbucketToken = getBitbucketTokenFromGlobalConfig();
                    } else {
//                        bitbucketToken = SCMRepositoryService.getCredentialsToken(bitbucketSCMSource.getCredentialsId());
                        Boolean prComment = (Boolean) scanParameters.get(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY);
                        if(prComment) {
                            throw new ScannerJenkinsException("PrComment is set true but not found any bitbucket token!");
                        }
                    }
                }

                BitbucketApi bitbucketApiFromSCMSource = bitbucketSCMSource.buildBitbucketClient(bitbucketSCMSource.getRepoOwner(), bitbucketSCMSource.getRepository());

                BitbucketRepository bitbucketRepository = null;
                try {
                    bitbucketRepository = bitbucketApiFromSCMSource.getRepository();
                } catch (Exception e) {
                    listener.getLogger().println("There is an exception while getting the BitbucketRepository from BitbucketApi");
                }

                String serverUrl = bitbucketSCMSource.getServerUrl();
                String repositoryName = null;
                String projectKey = null;

                if(bitbucketRepository != null) {
                    listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());
                    repositoryName = bitbucketRepository.getRepositoryName();
                    projectKey = bitbucketRepository.getProject().getKey();
                }

                bitbucket = createBitbucketObject(serverUrl, bitbucketToken, projectRepositoryPullNumber, repositoryName, projectKey);

            } else {
                listener.getLogger().println("Ignoring bitbucket_automation_fixpr and bitbucket_automation_prcomment since couldn't find any valid Bitbucket SCM source.");
            }
        } else {
            listener.getLogger().println("Jenkins instance not found.");
        }
        return bitbucket;
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

    private String getBitbucketTokenFromGlobalConfig() {
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);
        if (config != null && !Utility.isStringNullOrBlank(config.getBitbucketToken())) {
            return config.getBitbucketToken().trim();
        }
        return null;
    }

}
