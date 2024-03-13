package com.example.parky;

public class DashboardItems {

    String  locality, address, price, image;

    public DashboardItems(String locality, String address, String price, String image) {
        this.locality = locality;
        this.address = address;
        this.price = price;
        this.image = image;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
