package com.jianhwa.parkingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jianhwa.parkingapp.CarRecyclerViewAdapter;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.Car;
import com.jianhwa.parkingapp.entity.FirebaseDatabaseHelper;
import com.jianhwa.parkingapp.entity.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textViewBalance, textViewNameOfUser;
    private Button buttonTopUp, buttonProfile ;
    private ImageButton buttonQRScanner;
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
        recyclerView = findViewById(R.id.main_recyclerview);
        progressBar =findViewById(R.id.mainProgressBar);
        if (currentUserProfile!=null){
            textViewNameOfUser.setText(currentUserProfile.getFirstName());
        } else {
            textViewNameOfUser.setText("Profile");
        }

//        myRef = database.getReference("car");


    }

    private void checkUserProfile(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");

        myRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if(!dataSnapshot.exists()){
                    Intent intent = new Intent(MainActivity.this, UpdateUserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    currentUserProfile = dataSnapshot.getValue(User.class);
                    textViewNameOfUser.setText(currentUserProfile.getFirstName());
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("[MainActivity] Failed to read User value." + error.toException());
            }
        });
    }

    private void readData(){

        new FirebaseDatabaseHelper().readMyCars(currentUserId, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Car> cars) {

                CarRecyclerViewAdapter adapter = new CarRecyclerViewAdapter(MainActivity.this, cars, currentUserId);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}
