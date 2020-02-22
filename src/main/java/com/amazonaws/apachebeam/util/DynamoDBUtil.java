package com.amazonaws.apachebeam.util;

import com.amazonaws.apachebeam.config.AWSConfigConstants;
import com.amazonaws.apachebeam.provider.credential.CredentialProvider;
import com.amazonaws.apachebeam.provider.credential.factory.CredentialProviderFactory;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

import static com.amazonaws.apachebeam.config.AWSConfigConstants.*;

public class DynamoDBUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBUtil.class);

    public static final String TABLE_NAME = "ApacheBeamTable";


    private DynamoDBUtil() {

    }

    public static AmazonDynamoDB getDynamoDBClient(Properties ddbConfig, AWSConfigConstants.CredentialProviderType credProviderType) {

        CredentialProvider credentialProvider = CredentialProviderFactory.newCredentialProvider(credProviderType, ddbConfig);

        Validate.notNull(credentialProvider, "Credential Provider cannot be null.");

        String region = ddbConfig.getProperty(AWS_REGION, null);
        String ddbEndpoint = ddbConfig.getProperty(
                AWS_DYNAMODB_ENDPOINT, null);
        String ddbEndpointRegion = ddbConfig.getProperty(
                AWS_DYNAMODB_ENDPOINT_SIGNING_REGION, null);

        if (region != null) {
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(credentialProvider.getAwsCredentialsProvider())
                    .withRegion(Regions.fromName(region)).build();
        } else {
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(credentialProvider.getAwsCredentialsProvider())
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ddbEndpoint, ddbEndpointRegion)).build();
        }
    }


    public static void createDDBTable(AmazonDynamoDB dynamoDbClient, String tableName) {

        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(
                        "dept", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("dept", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)))
                .withTableName(tableName);

        try {
            ListTablesRequest listRequest = new ListTablesRequest().withLimit(10);
            ListTablesResult listTableResponse = dynamoDbClient.listTables();
            List<String> ddbTables = listTableResponse.getTableNames();
            boolean tableExists = false;

            if (ddbTables != null && ddbTables.contains(tableName)) {
                tableExists = true;
                LOGGER.info("Table " + tableName + " already exists. Not creating the new DynamoDB table");
            } else {
                LOGGER.info("Table Name " + tableName + " does not exist");
            }
            if (!tableExists) {
                CreateTableResult response = dynamoDbClient.createTable(request);
                LOGGER.info(response.getTableDescription().getTableName());
                Thread.sleep(500 * 20);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create/update DynamoDB table", e);
            System.exit(1);
        }
    }
}
