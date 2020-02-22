package com.amazonaws.apachebeam.util;

import com.amazonaws.apachebeam.JobOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CatalogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogUtil.class);

    private static final String catalogFileName = "Catalog.json";
    private static final String s3Key = "apache-beam/poc/resources/" + catalogFileName;

    private CatalogUtil() {

    }

    public static void loadCatalogEntries(AmazonDynamoDB dynamoDbClient, AmazonS3 s3Client, JobOptions options) {

        String catalogLocation = options.getCatalogLocation();
        LOGGER.info("Catalog Location " + catalogLocation);

        try {

            File tempFile;
            if (!catalogLocation.equals("")) {
                LOGGER.info("Catalog in S3");

                tempFile = File.createTempFile("catalog", ".json");
                s3Client.getObject(new GetObjectRequest(catalogLocation, s3Key), tempFile);
            } else {
                tempFile = new File("src/main/files/", catalogFileName);
            }

            JsonNode rootNode = new ObjectMapper().readTree(tempFile);

            Iterator<JsonNode> jsonIter = rootNode.iterator();
            Map<String, AttributeValue> items = new HashMap<>();

            while (jsonIter.hasNext()) {
                JsonNode currentNode = jsonIter.next();

                items.put("dept", new AttributeValue(currentNode.path("dept").asText()));
                items.put("details", new AttributeValue(currentNode.path("details").toString()));

                dynamoDbClient.putItem(DynamoDBUtil.TABLE_NAME, items);
            }

        } catch (Exception e) {
            System.err.println("Loading Error " + e.fillInStackTrace());
            System.exit(1);
        }

    }
}
