package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.apachebeam.config.AWSConfigConstants;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import java.util.Properties;

import static com.amazonaws.apachebeam.util.AWSUtil.validateBasicProviderConfiguration;

public class BasicCredentialProvider extends CredentialProvider {

    public BasicCredentialProvider(Properties properties) {
        super(validateBasicProviderConfiguration(properties));
    }

    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(getProperties().getProperty(AWSConfigConstants.AWS_ACCESS_KEY_ID),
                        getProperties().getProperty(AWSConfigConstants.AWS_SECRET_ACCESS_KEY));
            }

            @Override
            public void refresh() {

            }
        };
    }
}
