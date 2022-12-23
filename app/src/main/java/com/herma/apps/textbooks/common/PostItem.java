package com.herma.apps.textbooks.common;

import org.json.JSONArray;

public class PostItem {

  private String courseName, courseDepartment, courseEn;
  private JSONArray courseChapters;

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public String getCourseDepartment() {
    return courseDepartment;
  }

  public void setCourseDepartment(String courseDepartment) {
    this.courseDepartment = courseDepartment;
  }

  public JSONArray getCourseChapters() {
    return courseChapters;
  }

  public void setCourseChapters(JSONArray courseChapters) {
    this.courseChapters = courseChapters;
  }

  public String getCourseEn() {
    return courseEn;
  }

  public void setCourseEn(String courseEn) {
    this.courseEn = courseEn;
  }
}
