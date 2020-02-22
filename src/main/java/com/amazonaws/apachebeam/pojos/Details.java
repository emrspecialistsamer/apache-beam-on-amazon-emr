package com.amazonaws.apachebeam.pojos;

public class Details {
    private String fieldName;
    private int startIndex;
    private int endIndex;
    private int isMandatory;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setIsMandatory(int isMandatory) {
        this.isMandatory = isMandatory;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getIsMandatory() {
        return isMandatory;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
