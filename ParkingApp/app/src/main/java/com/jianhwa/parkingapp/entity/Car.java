package com.jianhwa.parkingapp.entity;

import java.io.Serializable;

public class Car implements Serializable{
    private String carPlate;
    private String carModel;
    private String carColor;

    public Car() {
    }

    public Car(String carPlate, String carModel, String carColor) {
        this.carPlate = carPlate;
        this.carModel = carModel;
        this.carColor = carColor;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
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
