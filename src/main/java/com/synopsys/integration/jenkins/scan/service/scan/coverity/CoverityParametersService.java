package com.synopsys.integration.jenkins.scan.service.scan.coverity;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.coverity.Coverity;
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategy;
import hudson.model.TaskListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CoverityParametersService implements ScanStrategy {
    private final TaskListener listener;

    public CoverityParametersService(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.COVERITY;
    }

    @Override
    public boolean isValidScanParameters(Map<String, Object> coverityParameters) {
        if (coverityParameters == null || coverityParameters.isEmpty()) {
            return false;
        }
        
        List<String> invalidParams = new ArrayList<>();

        Arrays.asList(ApplicationConstants.COVERITY_CONNECT_URL_KEY,
            ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY,
            ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY)
            .forEach(key -> {
                boolean isKeyValid = coverityParameters.containsKey(key)
                    && coverityParameters.get(key) != null
                    && !coverityParameters.get(key).toString().isEmpty();

                if (!isKeyValid) {
                    invalidParams.add(key);
                }
            });

        if (invalidParams.isEmpty()) {
            listener.getLogger().println("Coverity parameters are validated successfully");
            return true;
        } else {
            listener.getLogger().println(LogMessages.COVERITY_PARAMETER_VALIDATION_FAILED);
            listener.getLogger().println("Invalid Coverity parameters: " + invalidParams);
            return false;
        }
    }

    @Override
    public Coverity prepareScanInputForBridge(Map<String, Object> coverityParameters) {
        Coverity coverity = new Coverity();

        for (Map.Entry<String, Object> entry : coverityParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();

            switch (key) {
                case ApplicationConstants.COVERITY_CONNECT_URL_KEY:
                    coverity.getConnect().setUrl(value);
                    break;
                case ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY:
                    coverity.getConnect().getUser().setName(value);
                    break;
                case ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY:
                    coverity.getConnect().getUser().setPassword(value);
                    break;
                case ApplicationConstants.COVERITY_CONNECT_PROJECT_NAME_KEY:
                    coverity.getConnect().getProject().setName(value);
                    break;
                case ApplicationConstants.COVERITY_CONNECT_STREAM_NAME_KEY:
                    coverity.getConnect().getStream().setName(value);
                    break;
                case ApplicationConstants.COVERITY_CONNECT_POLICY_VIEW_KEY:
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
                default:
                    break;
            }
        }
        return coverity;
    }
}
