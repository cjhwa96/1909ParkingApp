package com.jianhwa.parkingapp.entity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerService {

    @GET("users/{user}/checkExist")
    Call<Boolean> checkExist(@Path("user") String userId);

    @GET("users/{user}/getUserProfile")
    Call<User> getUserProfile(@Path("user") String userId);

    @GET("users/{user}/editUserProfile")
    Call<String> editUserProfile(@Path("user") String userId, User newProfile);



    @GET("cars/{user}/getCarList")
    Call<List<Car>> getCarList(@Path("user") String user);

    @GET("cars/{car_plate}/checkCarLog")
    Call<List<ParkingTicket>> checkCarLog(@Path("car_plate") String car_plate);

    @POST("cars/deleteCar")
    Call<String> deleteCar(String car_plate);

    @POST("cars/addCar")
    Call<String> addCar(Car newCar);



    @GET("parking/{user}/driveIn")
    Call<ParkingTicket> driveIn(@Path("user") String userId, String car_plate);

    @GET("parking/{car_plate}/checkOut")
    Call<ParkingTicket> checkOut(@Path("car_plate") String car_plate);

    @GET("parking/{user}/booking")
    Call<ParkingTicket> booking(@Path("user") String userId, String car_plate);

    @GET("parking/{car_plate}/expressIn")
    Call<ParkingTicket> expressIn(@Path("car_plate") String car_plate);

    @GET("parking/{car_plate}/checkCarInParking")
    Call<ParkingTicket> checkCarInParking(@Path("car_plate") String car_plate);

    @GET("parking/{car_plate}/cancelBooking")
    Call<ParkingTicket> cancelBooking(@Path("car_plate") String car_plate);
}
