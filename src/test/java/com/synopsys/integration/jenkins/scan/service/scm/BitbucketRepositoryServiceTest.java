package com.synopsys.integration.jenkins.scan.service.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.synopsys.integration.jenkins.scan.exception.PluginExceptionHandler;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.bitbucket.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BitbucketRepositoryServiceTest {
    private final BitbucketRepositoryService bitbucketRepositoryServiceMock = Mockito.mock(BitbucketRepositoryService.class);
    private final String TEST_BITBUCKET_URL = "https://fake.bitbucket.url";
    private final String TEST_BITBUCKET_TOKEN = "MSDFSGOIIEGWGWEGFAKEKEY" ;
    private final Integer TEST_REPOSITORY_PULL_NUMBER = 7;
    private final String TEST_REPOSITORY_NAME = "TEST_REPO";
    private final String TEST_PROJECT_KEY = "my_key";
    Map<String, Object> bitbucketParametersMap = new HashMap<>();
    private BitbucketSCMSource bitbucketSCMSourceMock;

    @BeforeEach
    void setUp() throws PluginExceptionHandler {
        Bitbucket bitbucket = BitbucketRepositoryService.createBitbucketObject(TEST_BITBUCKET_URL, TEST_BITBUCKET_TOKEN, TEST_REPOSITORY_PULL_NUMBER, TEST_REPOSITORY_NAME, TEST_PROJECT_KEY);

        bitbucketParametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, TEST_BITBUCKET_TOKEN);

        bitbucketSCMSourceMock = mock(BitbucketSCMSource.class);

        when(bitbucketRepositoryServiceMock
            .fetchBitbucketRepositoryDetails(bitbucketParametersMap, bitbucketSCMSourceMock, TEST_REPOSITORY_PULL_NUMBER, false))
            .thenReturn(bitbucket);
    }

    @Test
    void createBitbucketObjectTest() throws PluginExceptionHandler {
        Bitbucket bitbucket = bitbucketRepositoryServiceMock.
            fetchBitbucketRepositoryDetails(bitbucketParametersMap, bitbucketSCMSourceMock, TEST_REPOSITORY_PULL_NUMBER, false);

        assertEquals(TEST_BITBUCKET_URL, bitbucket.getApi().getUrl());
        assertEquals(TEST_BITBUCKET_TOKEN, bitbucket.getApi().getToken());
        assertEquals(TEST_REPOSITORY_PULL_NUMBER, bitbucket.getProject().getRepository().getPull().getNumber());
        assertEquals(TEST_REPOSITORY_NAME, bitbucket.getProject().getRepository().getName());
        assertEquals(TEST_PROJECT_KEY, bitbucket.getProject().getKey());
    }
}
