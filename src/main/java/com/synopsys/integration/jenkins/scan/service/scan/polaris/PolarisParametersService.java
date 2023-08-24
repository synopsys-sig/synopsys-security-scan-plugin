package com.synopsys.integration.jenkins.scan.service.scan.polaris;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.polaris.Polaris;
import com.synopsys.integration.jenkins.scan.service.scan.strategy.ScanStrategy;
import hudson.model.TaskListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PolarisParametersService implements ScanStrategy {
    private final TaskListener listener;

    public PolarisParametersService(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.POLARIS;
    }

    @Override
    public boolean isValidScanParameters(Map<String, Object> polarisParameters) {
        if (polarisParameters == null || polarisParameters.isEmpty()) {
            return false;
        }

        List<String> invalidParams = new ArrayList<>();

        Arrays.asList(ApplicationConstants.POLARIS_SERVER_URL_KEY,
                        ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY,
                        ApplicationConstants.POLARIS_APPLICATION_NAME_KEY,
                        ApplicationConstants.POLARIS_ASSESSMENT_TYPES_KEY)
                .forEach(key -> {
                    boolean isKeyValid = polarisParameters.containsKey(key)
                            && polarisParameters.get(key) != null
                            && !polarisParameters.get(key).toString().isEmpty();

                    if (!isKeyValid) {
                        invalidParams.add(key);
                    }
                });

        if (invalidParams.isEmpty()) {
            listener.getLogger().println("Polaris parameters are validated successfully");
            return true;
        } else {
            listener.getLogger().println(LogMessages.POLARIS_PARAMETER_VALIDATION_FAILED);
            listener.getLogger().println("Invalid Polaris parameters: " + invalidParams);
            return false;
        }
    }

    @Override
    public Polaris prepareScanInputForBridge(Map<String, Object> polarisParameters) {
        Polaris polaris = new Polaris();

        for (Map.Entry<String, Object> entry : polarisParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();

            switch (key) {
                case ApplicationConstants.POLARIS_SERVER_URL_KEY:
                    polaris.setServerUrl(value);
                    break;
                case ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY:
                    polaris.setAccessToken(value);
                    break;
                case ApplicationConstants.POLARIS_APPLICATION_NAME_KEY:
                    polaris.getApplicationName().setName(value);
                    break;
                case ApplicationConstants.POLARIS_PROJECT_NAME_KEY:
                    polaris.getProjectName().setName(value);
                    break;
                case ApplicationConstants.POLARIS_ASSESSMENT_TYPES_KEY:
                    if (!value.isEmpty()) {
                        List<String> assessmentTypes = new ArrayList<>();
                        String[] assessmentTypesInput = value.toUpperCase().split(",");

                        for (String input : assessmentTypesInput) {
                            assessmentTypes.add(input.trim());
                        }
                        polaris.getAssessmentTypes().setTypes(assessmentTypes);
                    }
                    break;
                default:
                    break;
            }
        }

        return polaris;
    }
}