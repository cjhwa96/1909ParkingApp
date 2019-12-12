package com.jianhwa.parkingapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.Car;
import com.jianhwa.parkingapp.entity.ParkingTicket;
import com.jianhwa.parkingapp.entity.ServerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddCarActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build();
    private ServerService service = retrofit.create(ServerService.class);

    private EditText editText_carPlate, editText_carModel, editText_carColor;
    private Button button_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        editText_carPlate = findViewById(R.id.addCar_editText_carPlate);
        editText_carModel = findViewById(R.id.addCar_editText_carModel);
        editText_carColor = findViewById(R.id.addCar_editText_carColor);
        button_submit = findViewById(R.id.addCar_submitButton);

        HideSoftKeyboard(editText_carPlate);
        HideSoftKeyboard(editText_carModel);
        HideSoftKeyboard(editText_carColor);

        button_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCarActivity.this);
                builder.setCancelable(true)
                        .setTitle("Add New Car")
                        .setMessage("Submit?")
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Car newCar = new Car(editText_carPlate.getText().toString().trim(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                        editText_carModel.getText().toString().trim(),
                                        editText_carColor.getText().toString().trim());

                                Call<String> addCarCall = service.addCar(newCar);

                                addCarCall.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(response.body().equals("Success")){
                                            Toast.makeText(AddCarActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        System.out.println("[AddCarActivity] Failed to add car." + t.getMessage());
                                        call.cancel();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    private void HideSoftKeyboard(@NotNull EditText et) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager ime = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
    }
}
