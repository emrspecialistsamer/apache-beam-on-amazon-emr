package com.amazonaws.apachebeam.provider.credential.factory;

import com.amazonaws.apachebeam.config.AWSConfigConstants;
import com.amazonaws.apachebeam.provider.credential.*;
import org.apache.commons.lang3.Validate;

import java.util.Properties;

public final class CredentialProviderFactory {

    private CredentialProviderFactory() {

    }

    public static CredentialProvider newCredentialProvider(AWSConfigConstants.CredentialProviderType credentialProviderType,
                                                           Properties awsConfigProps) {
        Validate.notNull(awsConfigProps, "AWS configuration properties cannot be null");

        AWSConfigConstants.CredentialProviderType credentialType = (credentialProviderType == null) ?
                AWSConfigConstants.CredentialProviderType.AUTO : credentialProviderType;

        switch (credentialType) {
            case AUTO:
                return new DefaultCredentialProvider(awsConfigProps);
            case BASIC:
                return new BasicCredentialProvider(awsConfigProps);
            case PROFILE:
                return new ProfileCredentialProvider(awsConfigProps);
            case ENV_VARIABLES:
                return new EnvironmentCredentialProvider(awsConfigProps);
            case SYS_PROPERTIES:
                return new SystemCredentialProvider(awsConfigProps);
            default:
                return new DefaultCredentialProvider(awsConfigProps);
        }
    }
}
