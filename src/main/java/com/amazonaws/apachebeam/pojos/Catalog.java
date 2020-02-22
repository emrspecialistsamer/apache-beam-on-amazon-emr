package com.amazonaws.apachebeam.pojos;

import java.util.List;

public class Catalog {
    private String dept;
    protected List<Details> details;

    public void setFieldName(String dept) {
        this.dept = dept;
    }

    public String getFieldName() {
        return dept;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public List<Details> getDetails() {
        return details;
    }

}
