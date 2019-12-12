package com.jianhwa.parkingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.jianhwa.parkingapp.CarRecyclerViewAdapter;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.Car;
import com.jianhwa.parkingapp.entity.ServerService;
import com.jianhwa.parkingapp.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build();
    private ServerService service = retrofit.create(ServerService.class);
    private TextView textViewBalance, textViewNameOfUser;
    private Button buttonTopUp, buttonProfile;
    private ImageButton buttonQRScanner, buttonAddCar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String currentUserId;
    private User currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        checkUserProfile();

        setContentView(R.layout.activity_main);

        textViewBalance = findViewById(R.id.mainBalanceTV);
        textViewNameOfUser = findViewById(R.id.mainUserTV);
        buttonProfile = findViewById(R.id.mainProfileB);
        buttonTopUp = findViewById(R.id.mainTopUpB);
        buttonQRScanner = findViewById(R.id.main_QR_imagebutton);
        buttonAddCar = findViewById(R.id.mainAddCarB);
        recyclerView = findViewById(R.id.main_recyclerview);
        progressBar =findViewById(R.id.mainProgressBar);
        if (currentUserProfile!=null){
            textViewNameOfUser.setText(currentUserProfile.getFirstName());
            textViewBalance.setText("RM " + currentUserProfile.getBalance());
        } else {
            textViewNameOfUser.setText("Profile");
            textViewBalance.setText("RM 0.00");
        }

        readCarData();

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra("USER_PROFILE", currentUserProfile);
                startActivity(intent);
            }
        });

        buttonTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
                intent.putExtra("USER_PROFILE", currentUserProfile);
                startActivity(intent);
            }
        });

        buttonQRScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
                startActivity(intent);
            }
        });

        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCarActivity.class);
                intent.putExtra("USER_PROFILE", currentUserProfile);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserProfile();
        readCarData();
    }

    private void checkUserProfile(){
        Call<User> callUserProfile = service.getUserProfile(currentUserId);

        callUserProfile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                currentUserProfile = response.body();

                if (currentUserProfile == null){
                    Intent intent = new Intent(MainActivity.this, UpdateUserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    textViewNameOfUser.setText(currentUserProfile.getFirstName());
                    textViewBalance.setText("RM " + currentUserProfile.getBalance());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("[MainActivity] Failed to read User value." + t.getMessage());
                call.cancel();
            }
        });

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("user");
//
//        myRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                if(!dataSnapshot.exists()){
//                    Intent intent = new Intent(MainActivity.this, UpdateUserProfileActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    currentUserProfile = dataSnapshot.getValue(User.class);
//                    textViewNameOfUser.setText(currentUserProfile.getFirstName());
//                    textViewBalance.setText("RM " + currentUserProfile.getBalance());
//                }
//
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                System.out.println("[MainActivity] Failed to read User value." + error.toException());
//            }
//        });
    }

    private void readCarData(){
        Call<List<Car>> callCarList = service.getCarList(currentUserId);

        callCarList.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                List<Car> cars = response.body();

                if (!cars.isEmpty()) {
                    CarRecyclerViewAdapter adapter = new CarRecyclerViewAdapter(MainActivity.this, cars, currentUserId);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                } else {
                    Intent intent = new Intent(MainActivity.this, AddCarActivity.class);
                    intent.putExtra("USER_PROFILE", currentUserProfile);
                    startActivity(intent);
                }
                progressBar.setVisibility(View.GONE);
                if (cars.size() >= 3){
                    buttonAddCar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                System.out.println("[MainActivity] Failed to read Cars value." + t.getMessage());
                call.cancel();
            }
        });





//        new FirebaseDatabaseHelper().readMyCars(currentUserId, new FirebaseDatabaseHelper.DataStatus() {
//            @Override
//            public void DataIsLoaded(List<Car> cars) {
//
//                CarRecyclerViewAdapter adapter = new CarRecyclerViewAdapter(MainActivity.this, cars, currentUserId);
//                recyclerView.setAdapter(adapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//                progressBar.setVisibility(View.GONE);
//                if (cars.size() >= 3){
//                    buttonAddCar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void DataIsInserted() {
//
//            }
//
//            @Override
//            public void DataIsDeleted() {
//
//            }
//        });
    }
}
