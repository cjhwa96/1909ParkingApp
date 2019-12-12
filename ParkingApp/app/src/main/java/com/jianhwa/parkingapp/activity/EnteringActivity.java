package com.jianhwa.parkingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jianhwa.parkingapp.CarRecyclerViewAdapter;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.Car;
import com.jianhwa.parkingapp.entity.ParkingTicket;
import com.jianhwa.parkingapp.entity.ServerService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnteringActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build();
    private ServerService service = retrofit.create(ServerService.class);
    private String qr_type,currentUserId;
    private List<Car> cars;
    private Button buttonSubmit;
    private RadioGroup radioGroup_CarList;
    private RadioButton radioButton_Car1, radioButton_Car2, radioButton_Car3;
    private String selectedCar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering);

        qr_type = getIntent().getStringExtra("QR_TYPE");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        buttonSubmit = findViewById(R.id.entering_submitButton);
        radioGroup_CarList = findViewById(R.id.entering_radioGroup);
        radioButton_Car1 = findViewById(R.id.entering_radioButton1);
        radioButton_Car2 = findViewById(R.id.entering_radioButton2);
        radioButton_Car3 = findViewById(R.id.entering_radioButton3);

        Call<List<Car>> callCarList = service.getCarList(currentUserId);

        callCarList.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                cars = response.body();

                if (cars.isEmpty()) {
                    Intent intent = new Intent(EnteringActivity.this, AddCarActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                System.out.println("[EnteringActivity] Failed to read Cars value." + t.getMessage());
                call.cancel();
            }
        });


        if (qr_type.equals("DriveIn")){
            driveIn();
        } else if (qr_type.equals("ExpressIn")){
            expressIn();
        } else if (qr_type.equals("Exit")){
            exit();
        }

    }



    private void driveIn(){
        for (int i=cars.size()-1; i>=0; i--){
            Call<ParkingTicket> parkingTicketCall = service.checkCarInParking(cars.get(i).getCarPlate());

            final int finalI = i;
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    if(response.body().getCarPlateNum().equals(cars.get(finalI).getCarPlate())){
                        cars.remove(finalI);
                    }
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to read parking ticket value." + t.getMessage());
                    call.cancel();
                }
            });
        }

        if(cars.size()==1){
            Call<ParkingTicket> parkingTicketCall =service.driveIn(currentUserId, cars.get(0).getCarPlate());
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    ParkingTicket parkingTicket = response.body();
                    Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                    intent.putExtra("PARKING_TICKET", parkingTicket);
                    Toast.makeText(EnteringActivity.this, "Drive In gate opened", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to drive in." + t.getMessage());
                    call.cancel();
                }
            });
        } else if (cars.size() == 2){
            radioButton_Car3.setVisibility(View.GONE);

            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
        } else if (cars.size() == 3){
            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
            radioButton_Car3.setText(cars.get(2).getCarPlate());
        } else if (cars.size() == 0){
            Toast.makeText(EnteringActivity.this, "You don't have a valid car", Toast.LENGTH_SHORT).show();
            finish();
        }

        radioGroup_CarList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.entering_radioButton1:
                        selectedCar = radioButton_Car1.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton2:
                        selectedCar = radioButton_Car2.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton3:
                        selectedCar = radioButton_Car3.getText().toString().trim();
                        break;
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ParkingTicket> parkingTicketCall =service.driveIn(currentUserId, selectedCar);
                parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                    @Override
                    public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                        ParkingTicket parkingTicket = response.body();
                        Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                        intent.putExtra("PARKING_TICKET", parkingTicket);
                        Toast.makeText(EnteringActivity.this, "Drive In gate opened", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ParkingTicket> call, Throwable t) {
                        System.out.println("[EnteringActivity] Failed to drive in." + t.getMessage());
                        call.cancel();
                    }
                });
            }
        });
    }

    private void expressIn(){
        for (int i=cars.size()-1; i>=0; i--){
            Call<ParkingTicket> parkingTicketCall = service.checkCarInParking(cars.get(i).getCarPlate());

            final int finalI = i;
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    if(response.body().getCarPlateNum().equals(cars.get(finalI).getCarPlate())){
                        if (response.body().getArrivalTime() != null && response.body().getDepartureTime() != null)
                            cars.remove(finalI);
                    } else {
                        cars.remove(finalI);
                    }
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to read parking ticket value." + t.getMessage());
                    call.cancel();
                }
            });
        }

        if(cars.size()==1){
            Call<ParkingTicket> parkingTicketCall =service.expressIn(cars.get(0).getCarPlate());
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    ParkingTicket parkingTicket = response.body();
                    Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                    intent.putExtra("PARKING_TICKET", parkingTicket);
                    Toast.makeText(EnteringActivity.this, "Express gate opened", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to express in." + t.getMessage());
                    call.cancel();
                }
            });
        } else if (cars.size() == 2){
            radioButton_Car3.setVisibility(View.GONE);

            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
        } else if (cars.size() == 3){
            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
            radioButton_Car3.setText(cars.get(2).getCarPlate());
        } else if (cars.size() == 0){
            Toast.makeText(EnteringActivity.this, "You don't have a booking", Toast.LENGTH_SHORT).show();
            finish();
        }

        radioGroup_CarList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.entering_radioButton1:
                        selectedCar = radioButton_Car1.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton2:
                        selectedCar = radioButton_Car2.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton3:
                        selectedCar = radioButton_Car3.getText().toString().trim();
                        break;
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ParkingTicket> parkingTicketCall =service.expressIn(selectedCar);
                parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                    @Override
                    public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                        ParkingTicket parkingTicket = response.body();
                        Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                        intent.putExtra("PARKING_TICKET", parkingTicket);
                        Toast.makeText(EnteringActivity.this, "Express gate opened", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ParkingTicket> call, Throwable t) {
                        System.out.println("[EnteringActivity] Failed to express in." + t.getMessage());
                        call.cancel();
                    }
                });
            }
        });
    }


    private void exit() {
        for (int i=cars.size()-1; i>=0; i--){
            Call<ParkingTicket> parkingTicketCall = service.checkCarInParking(cars.get(i).getCarPlate());

            final int finalI = i;
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    if(response.body().getCarPlateNum().equals(cars.get(finalI).getCarPlate())){
                        if (response.body().getDepartureTime() != null)
                            cars.remove(finalI);
                    } else {
                        cars.remove(finalI);
                    }
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to read parking ticket value." + t.getMessage());
                    call.cancel();
                }
            });
        }

        if(cars.size()==1){
            Call<ParkingTicket> parkingTicketCall =service.checkOut(cars.get(0).getCarPlate());
            parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                @Override
                public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                    ParkingTicket parkingTicket = response.body();
                    Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                    intent.putExtra("PARKING_TICKET", parkingTicket);
                    Toast.makeText(EnteringActivity.this, "Exit gate opened", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ParkingTicket> call, Throwable t) {
                    System.out.println("[EnteringActivity] Failed to express in." + t.getMessage());
                    call.cancel();
                }
            });
        } else if (cars.size() == 2){
            radioButton_Car3.setVisibility(View.GONE);

            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
        } else if (cars.size() == 3){
            radioButton_Car1.setText(cars.get(0).getCarPlate());
            radioButton_Car2.setText(cars.get(1).getCarPlate());
            radioButton_Car3.setText(cars.get(2).getCarPlate());
        } else if (cars.size() == 0){
            Toast.makeText(EnteringActivity.this, "Please contact management office", Toast.LENGTH_SHORT).show();
            finish();
        }

        radioGroup_CarList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.entering_radioButton1:
                        selectedCar = radioButton_Car1.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton2:
                        selectedCar = radioButton_Car2.getText().toString().trim();
                        break;
                    case R.id.entering_radioButton3:
                        selectedCar = radioButton_Car3.getText().toString().trim();
                        break;
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ParkingTicket> parkingTicketCall =service.checkOut(selectedCar);
                parkingTicketCall.enqueue(new Callback<ParkingTicket>() {
                    @Override
                    public void onResponse(Call<ParkingTicket> call, Response<ParkingTicket> response) {
                        ParkingTicket parkingTicket = response.body();
                        Intent intent = new Intent(EnteringActivity.this, ParkingTicketActivity.class);
                        intent.putExtra("PARKING_TICKET", parkingTicket);
                        Toast.makeText(EnteringActivity.this, "Exit gate opened", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ParkingTicket> call, Throwable t) {
                        System.out.println("[EnteringActivity] Failed to exit." + t.getMessage());
                        call.cancel();
                    }
                });
            }
        });
    }
}
