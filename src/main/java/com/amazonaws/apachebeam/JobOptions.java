package com.amazonaws.apachebeam;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.beam.sdk.io.aws.options.AwsOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.io.aws.options.S3Options;


public interface JobOptions extends PipelineOptions, S3Options {
    
    @Description("Path to the catalog")
    String getCatalogLocation();
    
    void setCatalogLocation(String catalogLocation);
    
    @Description("Path of the file to read from")
    String getInputFile();

    void setInputFile(String inputFile);


    @Description("Path of the file to write to")
    @Required
    @Default.String("/tmp")
    String getOutput();

    void setOutput(String output);


    @Description("kind to read")
    @Required
    @Default.String("ApacheBeamTable")
    String getCatalogTable();

    void setCatalogTable(String catalogTable);

    @Description("property//field to read")
    @Required
    @Default.String("dept")
    String getCatalogTableProperty();

    void setCatalogTableProperty(String catalogTableProperty);

    @Override
    void setAwsCredentialsProvider(AWSCredentialsProvider profile);

    @Override
    @Default.InstanceFactory(value=AwsOptions.AwsUserCredentialsFactory.class)
    AWSCredentialsProvider getAwsCredentialsProvider();

    void setAwsRegion(String awsRegion);

    String getAwsRegion();
}
