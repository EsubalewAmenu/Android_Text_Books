package com.herma.apps.textbooks.ui.fragment;

public class RewardItem {
    private int id;
    private String date;
    private String amount;
    private String status;

    public RewardItem(int id, String date, String amount, String status) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
