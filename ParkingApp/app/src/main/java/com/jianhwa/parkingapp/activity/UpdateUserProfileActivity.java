package com.jianhwa.parkingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateUserProfileActivity extends AppCompatActivity {
    private String userId;
    private User oldProfile;
    private String email, balance;
    private User newProfile;

    private EditText et_fname, et_lname, et_address, et_phone;
    private TextView et_bday;
    private RadioGroup rg_gender;
    private String gender;
    private Calendar myCalendar;
    private Button btn_submit;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        et_fname = findViewById(R.id.UpdateUserProfile_fname);
        et_lname = findViewById(R.id.UpdateUserProfile_lname);
        rg_gender = findViewById(R.id.UpdateUserProfile_vaccinatedGroup);
        et_bday = findViewById(R.id.UpdateUserProfile_birthday);
        et_address = findViewById(R.id.UpdateUserProfile_address);
        et_phone = findViewById(R.id.UpdateUserProfile_phone);
        btn_submit = findViewById(R.id.UpdateUserProfile_button_submit);

        balance = "0.00";


        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("USER_PROFILE").equals(null)){
                oldProfile = (User) bundle.getSerializable("USER_PROFILE");

                balance = oldProfile.getBalance();
                et_fname.setText(oldProfile.getFirstName());
                et_lname.setText(oldProfile.getLastName());
                et_bday.setText(oldProfile.getBirthday());
                et_phone.setText(oldProfile.getPhone());
                et_address.setText(oldProfile.getAddress());
                if (oldProfile.getGender().equals("Male")){
                    rg_gender.check(rg_gender.getChildAt(0).getId());
                } else if (oldProfile.getGender().equals("Female")){
                    rg_gender.check(rg_gender.getChildAt(1).getId());
                }
                gender = oldProfile.getGender();
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        myCalendar = Calendar.getInstance();

        HideSoftKeyboard(et_fname);
        HideSoftKeyboard(et_lname);
        HideSoftKeyboard(et_address);
        HideSoftKeyboard(et_phone);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        et_bday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateUserProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.UpdateUserProfile_radio_male:
                        gender = "Male";
                        break;
                    case R.id.UpdateUserProfile_radio_female:
                        gender = "Female";
                        break;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserProfileActivity.this);
                builder.setCancelable(true)
                        .setTitle("Submit User Profile")
                        .setMessage("Submit?")
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadUserProfile();
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

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_bday.setText(sdf.format(myCalendar.getTime()));
    }

    private void uploadUserProfile() {
        if(checkNewUserProfile()==true) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            databaseReference.child(userId).setValue(newProfile);
            if(getIntent()==null && getIntent().getExtras()==null) {
                startActivity(new Intent(UpdateUserProfileActivity.this, MainActivity.class));
            }
            progressDialog.dismiss();
            finish();

        }
    }

    private boolean checkNewUserProfile(){
        String fname = et_fname.getText().toString().trim();
        String lname = et_lname.getText().toString().trim();
        String bday = et_bday.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();

        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(bday)
                && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(gender) ) {
            if (phone.matches("^(?=(?:[0]){1})(?=[0-9]{9,11}).*")) {
                newProfile = new User(email, fname, lname, gender, bday, address, phone, balance);
                return true;
            }else {
                Toast.makeText(UpdateUserProfileActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(UpdateUserProfileActivity.this, "Please enter full information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
