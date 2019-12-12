package com.jianhwa.parkingapp.entity;

import java.io.Serializable;

public class Car implements Serializable{
    private String carPlate;
    private String userId;
    private String carModel;
    private String carColor;

    public Car() {
    }

    public Car(String carPlate, String userId, String carModel, String carColor) {
        this.carPlate = carPlate;
        this.userId = userId;
        this.carModel = carModel;
        this.carColor = carColor;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }
}
