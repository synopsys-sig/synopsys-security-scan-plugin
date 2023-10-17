package io.jenkins.plugins.synopsys.security.scan.service.scan.coverity;

import hudson.model.TaskListener;
import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;
import io.jenkins.plugins.synopsys.security.scan.global.LogMessages;
import io.jenkins.plugins.synopsys.security.scan.global.LoggerWrapper;
import io.jenkins.plugins.synopsys.security.scan.input.coverity.Coverity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CoverityParametersService {
    private final LoggerWrapper logger;

    public CoverityParametersService(TaskListener listener) {
        this.logger = new LoggerWrapper(listener);
    }

    public boolean isValidCoverityParameters(Map<String, Object> coverityParameters) {
        if (coverityParameters == null || coverityParameters.isEmpty()) {
            return false;
        }

        List<String> invalidParams = new ArrayList<>();

        Arrays.asList(
                        ApplicationConstants.COVERITY_URL_KEY,
                        ApplicationConstants.COVERITY_USER_KEY,
                        ApplicationConstants.COVERITY_PASSPHRASE_KEY)
                .forEach(key -> {
                    boolean isKeyValid = coverityParameters.containsKey(key)
                            && coverityParameters.get(key) != null
                            && !coverityParameters.get(key).toString().isEmpty();

                    if (!isKeyValid) {
                        invalidParams.add(key);
                    }
                });

        if (invalidParams.isEmpty()) {
            logger.info("Coverity parameters are validated successfully");
            return true;
        } else {
            logger.error(LogMessages.COVERITY_PARAMETER_VALIDATION_FAILED);
            logger.error("Invalid Coverity parameters: " + invalidParams);
            return false;
        }
    }

    public Coverity prepareCoverityObjectForBridge(Map<String, Object> coverityParameters) {
        Coverity coverity = new Coverity();

        for (Map.Entry<String, Object> entry : coverityParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();

            switch (key) {
                case ApplicationConstants.COVERITY_URL_KEY:
                    coverity.getConnect().setUrl(value);
                    break;
                case ApplicationConstants.COVERITY_USER_KEY:
                    coverity.getConnect().getUser().setName(value);
                    break;
                case ApplicationConstants.COVERITY_PASSPHRASE_KEY:
                    coverity.getConnect().getUser().setPassword(value);
                    break;
                case ApplicationConstants.COVERITY_PROJECT_NAME_KEY:
                    coverity.getConnect().getProject().setName(value);
                    break;
                case ApplicationConstants.COVERITY_STREAM_NAME_KEY:
                    coverity.getConnect().getStream().setName(value);
                    break;
                case ApplicationConstants.COVERITY_POLICY_VIEW_KEY:
                    coverity.getConnect().getPolicy().setView(value);
                    break;
                case ApplicationConstants.COVERITY_INSTALL_DIRECTORY_KEY:
                    coverity.getInstall().setDirectory(value);
                    break;
                case ApplicationConstants.COVERITY_AUTOMATION_PRCOMMENT_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        coverity.getAutomation().setPrComment(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.COVERITY_VERSION_KEY:
                    coverity.setVersion(value);
                    break;
                case ApplicationConstants.COVERITY_LOCAL_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        coverity.setLocal(Boolean.parseBoolean(value));
                    }
                    break;
                default:
                    break;
            }
        }
        return coverity;
    }
}
