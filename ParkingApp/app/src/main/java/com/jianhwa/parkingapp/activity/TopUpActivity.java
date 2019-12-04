package com.jianhwa.parkingapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.User;

import java.text.DecimalFormat;

public class TopUpActivity extends AppCompatActivity {
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private TextView textView_balance;
    private Button button_10, button_20, button_50, button_100;
    private User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        textView_balance = findViewById(R.id.topup_textView_balance);
        button_10 = findViewById(R.id.topup_button_10);
        button_20 = findViewById(R.id.topup_button_20);
        button_50 = findViewById(R.id.topup_button_50);
        button_100 = findViewById(R.id.topup_button_100);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("USER_PROFILE").equals(null)){
                userProfile = (User) bundle.getSerializable("USER_PROFILE");
                textView_balance.setText("RM " + userProfile.getBalance());
            }else {
                finish();
            }
        }

        button_10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(TopUpActivity.this);
                builder.setCancelable(true)
                        .setTitle("Top Up")
                        .setMessage("Top up RM 10?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                topUp(10);
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

        button_20.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(TopUpActivity.this);
                builder.setCancelable(true)
                        .setTitle("Top Up")
                        .setMessage("Top up RM 20?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                topUp(20);
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

        button_50.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(TopUpActivity.this);
                builder.setCancelable(true)
                        .setTitle("Top Up")
                        .setMessage("Top up RM 50?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                topUp(50);
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

        button_100.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(TopUpActivity.this);
                builder.setCancelable(true)
                        .setTitle("Top Up")
                        .setMessage("Top up RM 100?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                topUp(100);
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

    private void topUp(double value){
       double newBalance = Double.parseDouble(userProfile.getBalance());
       newBalance = newBalance + value;
       userProfile.setBalance(String.format("%.2f", newBalance));

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(currentUserId).setValue(userProfile);
        progressDialog.dismiss();
        finish();
    }
}
