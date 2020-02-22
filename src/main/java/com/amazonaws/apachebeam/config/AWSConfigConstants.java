package com.amazonaws.apachebeam.config;

/**
 * AWS Kinesis Firehose configuration constants
 */
public class AWSConfigConstants {

    /**
     * The AWS access key for provider type basic
     */
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    /**
     * The AWS secret key for provider type basic
     */
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    /**
     * The AWS Kinesis Firehose region, if not specified defaults to us-east-1
     */
    public static final String AWS_REGION = "aws.region";
    /**
     * Optional configuration in case the provider is AwsProfileCredentialProvider
     */
    public static final String AWS_PROFILE_NAME = "aws.credentials.provider.profile.name";
    /**
     * Optional configuration in case the provider is AwsProfileCredentialProvider
     */
    public static final String AWS_PROFILE_PATH = "aws.credentials.provider.profile.path";
    /**
     * The DynamoDB  endpoint
     */
    public static final String AWS_DYNAMODB_ENDPOINT = "aws.dynamodb.endpoint";
    public static final String AWS_DYNAMODB_ENDPOINT_SIGNING_REGION = "aws.dynamodb.endpoint.signing.region";

    public enum CredentialProviderType {

        /**
         * Look for AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY into passed configuration
         */
        BASIC,

        /**
         * Look for the environment variables AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY to create AWS credentials.
         */
        ENV_VARIABLES,

        /**
         * Look for Java system properties aws.accessKeyId and aws.secretKey to create AWS credentials.
         */
        SYS_PROPERTIES,

        /**
         * Use a AWS credentials profile file to create the AWS credentials.
         */
        PROFILE,

        /**
         * A credentials provider chain will be used that searches for credentials in this order:
         * ENV_VARIABLES, SYS_PROPERTIES, PROFILE in the AWS instance metadata.
         **/
        AUTO
    }
}
