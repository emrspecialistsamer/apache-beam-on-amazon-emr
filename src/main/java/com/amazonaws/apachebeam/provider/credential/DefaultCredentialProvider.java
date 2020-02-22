package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

import java.util.Properties;

public class DefaultCredentialProvider extends CredentialProvider {

    public DefaultCredentialProvider(Properties properties) {
        super(properties);
    }

    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }
}
