package com.jianhwa.parkingapp.entity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferemcePets;
    private List<Car> cars = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Car> cars);
        void DataIsInserted();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferemcePets = mDatabase.getReference("car");
    }

    public void readMyCars(final String userId, final DataStatus dataStatus){
        mReferemcePets.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cars.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    Car pet = keyNode.getValue(Car.class);
                    cars.add(pet);
                }
                dataStatus.DataIsLoaded(cars);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addCar(String userId, Car car, final DataStatus dataStatus){
        mReferemcePets.child(userId).child(car.getCarPlate()).setValue(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }

    public void deleteCar(String userId, String carPlate, final DataStatus dataStatus){
        mReferemcePets.child(userId).child(carPlate).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
