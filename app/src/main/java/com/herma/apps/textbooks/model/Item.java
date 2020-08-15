package com.herma.apps.textbooks.model;

/*
 * Created by Sambhaji Karad on 04-Jan-18
 * Mobile 9423476192
 * Email sambhaji2134@gmail.com/
*/

public class Item {

    public String chapName, fileName, en;
    public int drawable;
    public String color;

    public Item(String chapName, String fileName, String en, int drawable, String color ) {
        this.chapName = chapName;
        this.fileName = fileName;
        this.en = en;
        this.drawable = drawable;
        this.color = color;
    }

    public String getChapterName() {
        return chapName;
    }
    public String getFileName() {
        return fileName;
    }
    public String getEn() {
        return en;
    }

    public void setChapName(String chapName) {
        this.chapName = chapName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setEn(String en) {
        this.en = en;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
