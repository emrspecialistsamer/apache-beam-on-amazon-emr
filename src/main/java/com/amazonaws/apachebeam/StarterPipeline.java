package com.amazonaws.apachebeam;


import com.amazonaws.apachebeam.provider.credential.factory.CredentialProviderFactory;
import com.amazonaws.apachebeam.util.AWSUtil;
import com.amazonaws.apachebeam.util.CatalogUtil;
import com.amazonaws.apachebeam.util.DynamoDBUtil;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.amazonaws.apachebeam.config.AWSConfigConstants.AWS_REGION;
import static com.amazonaws.apachebeam.config.AWSConfigConstants.CredentialProviderType;
import static com.amazonaws.apachebeam.util.DynamoDBUtil.TABLE_NAME;


public class StarterPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(StarterPipeline.class);

    private static AmazonDynamoDB dynamoDbClient;

    private static AmazonS3 s3Client;

    private static Properties awsConfig;

    private static CredentialProviderType credentialProviderType;


    public static void main(String[] args) {
        StarterPipeline pipeline = new StarterPipeline();
        pipeline.initialize();
        pipeline.startPipeline(args);
    }


    /**
     * Initialize AWS service clients and configs
     **/
    private void initialize() {
        awsConfig = new Properties();
        awsConfig.setProperty(AWS_REGION, "us-east-1");
        credentialProviderType = AWSUtil.getCredentialProviderType(awsConfig);

        dynamoDbClient = DynamoDBUtil.getDynamoDBClient(awsConfig, credentialProviderType);
        s3Client = AWSUtil.getAmazonS3Client(awsConfig, credentialProviderType);
    }


    /**
     * Starts a basic Apache Beam pipeline
     **/
    private void startPipeline(String[] args) {

        JobOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(JobOptions.class);
        options.setAwsRegion(options.getAwsRegion());
        options.setAwsCredentialsProvider(CredentialProviderFactory.newCredentialProvider(credentialProviderType, awsConfig).getAwsCredentialsProvider());
        options.setRunner(SparkRunner.class);

        Pipeline p = Pipeline.create(options);

        //Create and Load catalog into DynamoDB
        DynamoDBUtil.createDDBTable(dynamoDbClient, TABLE_NAME);
        CatalogUtil.loadCatalogEntries(dynamoDbClient, s3Client, options);

        try {
            PCollection<String> result = BeamCompute.compute(p, dynamoDbClient, options);

            String outputPath = options.getOutput();
            LOGGER.info("Output path = " + outputPath);
            result.apply("write file", TextIO.write().to(outputPath).withSuffix(".csv"));

            p.run().waitUntilFinish();
        } catch (Exception e) {
            LOGGER.error(" Exception occurred ", e);
            System.exit(1);
        }
        LOGGER.info("Finished writing output data.");
    }
}
