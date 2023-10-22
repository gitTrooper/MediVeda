package com.saidev.MediVeda.modelClass;

public class Hospital {
    private String hospital_name;
    private String hospital_rating;
    String hospital_address;
    String hospital_link;

    public Hospital() {
    }

    public Hospital(String hospital_name, String hospital_rating, String hospital_address, String hospital_link) {
        this.hospital_name = hospital_name;
        this.hospital_rating = hospital_rating;
        this.hospital_address = hospital_address;
        this.hospital_link = hospital_link;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getHospital_rating() {
        return hospital_rating;
    }

    public void setHospital_rating(String hospital_rating) {
        this.hospital_rating = hospital_rating;
    }

    public String getHospital_address() {
        return hospital_address;
    }

    public void setHospital_address(String hospital_address) {
        this.hospital_address = hospital_address;
    }

    public String getHospital_link() {
        return hospital_link;
    }

    public void setHospital_link(String hospital_link) {
        this.hospital_link = hospital_link;
    }
}

