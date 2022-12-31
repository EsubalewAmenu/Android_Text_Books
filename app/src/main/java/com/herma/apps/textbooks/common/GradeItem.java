package com.herma.apps.textbooks.common;

public class GradeItem {
    public int id;
    public String gradeName;

    public GradeItem(int _id, String _gradeName){
        this.id = _id;
        this.gradeName = _gradeName;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }
}