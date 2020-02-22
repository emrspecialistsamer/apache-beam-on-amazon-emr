package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.apachebeam.util.AWSUtil;
import com.amazonaws.auth.AWSCredentialsProvider;

import java.util.Properties;

public abstract class CredentialProvider {

    final Properties properties;

    public CredentialProvider(Properties properties) {
        this.properties = AWSUtil.validateConfiguration(properties);
    }

    public abstract AWSCredentialsProvider getAwsCredentialsProvider();

    protected Properties getProperties() {
        return properties;
    }
}
