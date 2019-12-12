package com.jianhwa.parkingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.jianhwa.parkingapp.activity.CarProfileActivity;
import com.jianhwa.parkingapp.activity.MainActivity;
import com.jianhwa.parkingapp.activity.TopUpActivity;
import com.jianhwa.parkingapp.entity.Car;
import com.jianhwa.parkingapp.entity.FirebaseDatabaseHelper;

import java.util.List;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<CarRecyclerViewAdapter.CarViewHolder> {

    private Context mContext;
    private List<Car> mCarList;
    private String carsOwnerId;

    public CarRecyclerViewAdapter(Context mContext, List<Car> mCarList, String carsOwnerId) {
        this.mContext = mContext;
        this.mCarList = mCarList;
        this.carsOwnerId = carsOwnerId;
    }

    @NonNull
    @Override
    public CarRecyclerViewAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.car_list_item, parent, false);

        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarRecyclerViewAdapter.CarViewHolder holder, int position) {
        Car car = mCarList.get(position);
        holder.mCarPlate.setText(car.getCarPlate());
        holder.mCarColor.setText(car.getCarColor());
        holder.mCarModel.setText(car.getCarModel());
        holder.car = car;

    }

    @Override
    public int getItemCount() {
        return mCarList.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        TextView mCarPlate;
        TextView mCarColor;
        TextView mCarModel;

        Car car;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);

            mCarPlate = itemView.findViewById(R.id.car_list_carplate);
            mCarColor = itemView.findViewById(R.id.car_list_color);
            mCarModel = itemView.findViewById(R.id.car_list_model);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CarProfileActivity.class);
                    intent.putExtra("CAR_SELECTED", car);
                    mContext.startActivity(intent);




//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    builder.setCancelable(true)
//                            .setTitle("Delete Car")
//                            .setMessage("Are you sure you want to delete?")
//                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    new FirebaseDatabaseHelper().deleteCar(carsOwnerId, car.getCarPlate(), new FirebaseDatabaseHelper.DataStatus() {
//                                        @Override
//                                        public void DataIsLoaded(List<Car> cars) {
//
//                                        }
//
//                                        @Override
//                                        public void DataIsInserted() {
//
//                                        }
//
//                                        @Override
//                                        public void DataIsDeleted() {
//                                            Toast.makeText(mContext, car.getCarPlate() + "'s profile has been deleted", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            })
//                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .create()
//                            .show();
                }
            });
        }
    }
}