package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.bitbucket.*;
import com.synopsys.integration.jenkins.scan.service.scm.BitbucketRepositoryService;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BitbucketRepositoryServiceTest {
    private final BitbucketRepositoryService bitbucketRepositoryServiceMock = Mockito.mock(BitbucketRepositoryService.class);
    private final Jenkins jenkinsMock = Mockito.mock(Jenkins.class);
    private final String TEST_BITBUCKET_URL = "https://fake.bitbucket.url";
    private final String TEST_BITBUCKET_TOKEN = "MSDFSGOIIEGWGWEGFAKEKEY" ;
    private final Integer TEST_REPOSITORY_PULL_NUMBER = 7;
    private final String TEST_REPOSITORY_NAME = "TEST_REPO";
    private final String TEST_PROJECT_KEY = "my_key";
    Map<String, Object> bitbucketParametersMap = new HashMap<>();

    @BeforeAll
    void setUp() {
        Bitbucket bitbucket = BitbucketRepositoryService.createBitbucketObject(TEST_BITBUCKET_URL, TEST_BITBUCKET_TOKEN, TEST_REPOSITORY_PULL_NUMBER, TEST_REPOSITORY_NAME, TEST_PROJECT_KEY);

        bitbucketParametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, TEST_BITBUCKET_TOKEN);

        when(bitbucketRepositoryServiceMock.fetchBitbucketRepositoryDetails(jenkinsMock, bitbucketParametersMap, TEST_REPOSITORY_PULL_NUMBER)).thenReturn(bitbucket);
    }

    @Test
    void createBitbucketObjectTest() {
        Bitbucket bitbucket = bitbucketRepositoryServiceMock.fetchBitbucketRepositoryDetails(jenkinsMock, bitbucketParametersMap, TEST_REPOSITORY_PULL_NUMBER);

        assertEquals(TEST_BITBUCKET_URL, bitbucket.getApi().getUrl());
        assertEquals(TEST_BITBUCKET_TOKEN, bitbucket.getApi().getToken());
        assertEquals(TEST_REPOSITORY_PULL_NUMBER, bitbucket.getProject().getRepository().getPull().getNumber());
        assertEquals(TEST_REPOSITORY_NAME, bitbucket.getProject().getRepository().getName());
        assertEquals(TEST_PROJECT_KEY, bitbucket.getProject().getKey());
    }
}
