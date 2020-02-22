package com.amazonaws.apachebeam.util;

import com.amazonaws.apachebeam.provider.credential.CredentialProvider;
import com.amazonaws.apachebeam.provider.credential.factory.CredentialProviderFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.amazonaws.apachebeam.config.AWSConfigConstants.*;


public final class AWSUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSUtil.class);

    private AWSUtil() {

    }

    public static AmazonS3 getAmazonS3Client(Properties s3Config, CredentialProviderType credProviderType) {

        CredentialProvider credentialProvider = CredentialProviderFactory.newCredentialProvider(credProviderType, s3Config);

        Validate.notNull(credentialProvider, "Credential Provider cannot be null.");

        String region = s3Config.getProperty(AWS_REGION, null);

        LOGGER.info("Using AWS region: [" + region + "].");

        if (region != null) {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(credentialProvider.getAwsCredentialsProvider())
                    .withRegion(Regions.fromName(region)).build();
        } else {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(credentialProvider.getAwsCredentialsProvider())
                    .withRegion(Regions.US_EAST_1).build();
        }
    }

    public static Properties validateConfiguration(Properties configProps) {
        Validate.notNull(configProps, "Configuration properties cannot be null.");

        if (!configProps.containsKey(AWS_REGION) ^ (configProps.containsKey(AWS_DYNAMODB_ENDPOINT) && (configProps.containsKey(AWS_DYNAMODB_ENDPOINT_SIGNING_REGION)))) {
            throw new IllegalArgumentException(
                    "Either AWS region should be specified or AWS DynamoDB endpoint and endpoint signing region.");
        }

        return configProps;
    }

    public static Properties validateBasicProviderConfiguration(Properties configProps) {
        validateConfiguration(configProps);

        Validate.isTrue(configProps.containsKey(AWS_ACCESS_KEY_ID),
                "AWS access key must be specified with credential provider BASIC.");
        Validate.isTrue(configProps.containsKey(AWS_SECRET_ACCESS_KEY),
                "AWS secret key must be specified with credential provider BASIC.");

        return configProps;
    }

    public static boolean containsBasicProperties(Properties configProps) {
        Validate.notNull(configProps);
        return configProps.containsKey(AWS_ACCESS_KEY_ID) &&
                configProps.containsKey(AWS_SECRET_ACCESS_KEY);
    }

    public static Properties validateProfileProviderConfiguration(Properties configProps) {
        validateConfiguration(configProps);

        Validate.isTrue(configProps.containsKey(AWS_PROFILE_NAME),
                "AWS profile name should be specified with credential provider PROFILE.");

        return configProps;
    }

    public static CredentialProviderType getCredentialProviderType(Properties configProps) {
        return (containsBasicProperties(configProps) ?
                CredentialProviderType.BASIC : CredentialProviderType.AUTO);
    }
}
