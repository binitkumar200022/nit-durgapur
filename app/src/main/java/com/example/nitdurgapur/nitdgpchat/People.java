package com.example.nitdurgapur.nitdgpchat;

public class People {
    public String name, department, image;

    public People() {

    }

    public People(String name, String department, String image) {
        this.name = name;
        this.department = department;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
