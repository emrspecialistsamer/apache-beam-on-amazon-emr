package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;

import java.util.Properties;

public class SystemCredentialProvider extends CredentialProvider {


    public SystemCredentialProvider(Properties properties) {
        super(properties);
    }

    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return new SystemPropertiesCredentialsProvider();
    }
}
