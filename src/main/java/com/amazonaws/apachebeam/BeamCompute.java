package com.amazonaws.apachebeam;

import com.amazonaws.apachebeam.jackson.JacksonConverterImpl;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.client.util.Lists;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.amazonaws.apachebeam.util.DynamoDBUtil.TABLE_NAME;


public class BeamCompute {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeamCompute.class);

    /**
     * Get info -- implement as external service
     * this can return List/array that would be loaded to PCollection
     **/
    private static List<FileFields> getFileInfo(JobOptions options, AmazonDynamoDB dynamoDbClient) {

        String catalogKind = options.getCatalogTable();

        List<Map<String, AttributeValue>> results;
        List<FileFields> catalogInfo = new ArrayList<>();

        String partitionKeyName = options.getCatalogTableProperty();
        String partitionKeyValue = "Engineering";
        String partitionAlias = "#" + partitionKeyName;

        LOGGER.info("CatalogTable: " + catalogKind + " Property: " + partitionKeyName);

        //Set up an alias for the partition key name in case it's a reserved word
        HashMap<String, String> expressionAttributeNames =
                new HashMap<>();
        expressionAttributeNames.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> expressionAttributeValues =
                new HashMap<>();
        expressionAttributeValues.put(":" + partitionKeyName, new AttributeValue(partitionKeyValue));

        QueryRequest queryReq = new QueryRequest()
                .withTableName(TABLE_NAME)
                .withKeyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        try {
            QueryResult response = dynamoDbClient.query(queryReq);
            results = response.getItems();

            if (response.getCount() < 1) {
                return catalogInfo;
            }

            Iterator<Map<String, AttributeValue>> resultsIter = results.iterator();
            ObjectMapper mapper = new ObjectMapper();

            while (resultsIter.hasNext()) {

                Map<String, AttributeValue> current = resultsIter.next();
                JsonNode node = new JacksonConverterImpl().mapToJsonObject(current);

                String details = node.path("details").asText();
                JsonNode fieldArray = mapper.readTree(details);

                if (fieldArray.isArray()) {

                    ArrayNode arrNode = (ArrayNode) fieldArray;

                    Iterator<JsonNode> arrIter = arrNode.iterator();

                    while (arrIter.hasNext()) {
                        JsonNode fieldNode = arrIter.next();
                        FileFields fileFields = new FileFields();

                        String fName = fieldNode.path("fieldname").toString();

                        fileFields.fieldName = fName.replaceAll("^\"|\"$", "");

                        LOGGER.info(" File fieldsName = " + fileFields.fieldName);

                        fileFields.startIndex = fieldNode.get("startindex").asInt();

                        fileFields.endIndex = fieldNode.get("endindex").asInt();
                        fileFields.isMandatory = fieldNode.get("isMandatory").asInt();
                        catalogInfo.add(fileFields);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get file info", e);
            System.exit(1);
        }
        return catalogInfo;
    }


    public static PCollection<String> compute(Pipeline p, AmazonDynamoDB dynamoDbClient, JobOptions options) {

        String inputFile = options.getInputFile();

        PCollection<String> fileData = p.apply(TextIO.read().from(inputFile));
        List<FileFields> fieldFlags = getFileInfo(options, dynamoDbClient);

        PCollection<FileFields> sideCollection = p.apply("fieldindexandflag", Create.of(fieldFlags));

        PCollectionView<Iterable<FileFields>> sideCollectionView = sideCollection.apply(View.asIterable());

        return fileData.apply(ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c, @Element String line, OutputReceiver<String> out) {
                String[] inputArray = line.split(",");

                Iterable<FileFields> fieldsLists = c.sideInput(sideCollectionView);
                List<FileFields> fList = Lists.newArrayList(fieldsLists);
                boolean valid = true;

                for (FileFields f : fList) {
                    if (f.isMandatory == 0) {
                        LOGGER.info("FieldName = " + f.fieldName);
                        if (f.fieldName.equals("LastName")) {
                            LOGGER.info("LastName = " + inputArray[2] + " Length = " + inputArray[2].length());
                        }

                        if ((f.fieldName.equals("EmpId") && (inputArray[0].substring(f.startIndex).equals(""))) ||
                                (f.fieldName.equals("FirstName") && inputArray[1].substring(f.startIndex).equals("")) ||
                                (f.fieldName.equals("LastName") && inputArray[2].substring(f.startIndex).equals("")) ||
                                (f.fieldName.equals("Street") && inputArray[3].substring(f.startIndex).equals("")) ||
                                (f.fieldName.equals("City") && inputArray[4].substring(f.startIndex).equals("")) ||
                                (f.fieldName.equals("State") && inputArray[5].substring(f.startIndex).equals("")) ||
                                (f.fieldName.equals("Zipcode") && inputArray[6].substring(f.startIndex).equals(""))
                                ) {
                            valid = false;
                        }
                    }
                }

                if (valid) { // Write only if mandatory fields exists
                    out.output(line);
                }
            }
        }).withSideInputs(sideCollectionView));
    }
}
