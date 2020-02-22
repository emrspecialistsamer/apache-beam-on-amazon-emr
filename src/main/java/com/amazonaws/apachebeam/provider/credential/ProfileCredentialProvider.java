package com.amazonaws.apachebeam.provider.credential;

import com.amazonaws.apachebeam.config.AWSConfigConstants;
import com.amazonaws.apachebeam.util.AWSUtil;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import java.util.Properties;

public class ProfileCredentialProvider extends CredentialProvider {


    public ProfileCredentialProvider(Properties properties) {
        super(AWSUtil.validateProfileProviderConfiguration(properties));
    }

    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        String profileName = getProperties().getProperty(AWSConfigConstants.AWS_PROFILE_NAME);
        String profilePath = getProperties().getProperty(AWSConfigConstants.AWS_PROFILE_PATH, null);

        return (profilePath == null) ? new ProfileCredentialsProvider(profileName) :
                new ProfileCredentialsProvider(profileName, profilePath);
    }
}
