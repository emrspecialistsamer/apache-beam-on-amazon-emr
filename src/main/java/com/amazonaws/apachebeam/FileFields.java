package com.amazonaws.apachebeam;

import java.io.Serializable;

public class FileFields implements Serializable {
    String fieldName;
    int startIndex;
    int endIndex;
    int isMandatory;
}
