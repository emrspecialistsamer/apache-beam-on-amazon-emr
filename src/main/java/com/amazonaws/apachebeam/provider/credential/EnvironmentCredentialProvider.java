package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;

import java.util.Properties;

public class EnvironmentCredentialProvider extends CredentialProvider {


    public EnvironmentCredentialProvider(Properties properties) {
        super(properties);
    }

    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return new EnvironmentVariableCredentialsProvider();
    }
}
