package com.herma.apps.textbooks.common;

import org.json.JSONArray;

public class PostItem {

  private String subjectName, subjectGrade, subjectEn;
  private JSONArray subjectChapters;

  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }

  public String getSubjectGrade() {
    return subjectGrade;
  }

  public void setSubjectGrade(String subjectGrade) {
    this.subjectGrade = subjectGrade;
  }

  public JSONArray getSubjectChapters() {
    return subjectChapters;
  }

  public void setSubjectChapters(JSONArray subjectChapters) {
    this.subjectChapters = subjectChapters;
  }

  public String getSubjectEn() {
    return subjectEn;
  }

  public void setSubjectEn(String subjectEn) {
    this.subjectEn = subjectEn;
  }
}
