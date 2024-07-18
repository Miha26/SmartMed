package com.med_app.smartmed1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class Account extends AppCompatActivity {

    private Button button_medicalh, button_treatment, button_home, button_reminder;
    private TextView gender, bloodtype;
    private int selectedGender = -1;
    private int selectedBloodType = -1;
    private String[] genderArray = {"Female", "Male"};
    private String[] bloodTypeArray = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private EditText inputFirstName, inputLastName, inputAge, inputHeight, inputWeight;
    private Button btnSubmit;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize the buttons
        button_home = findViewById(R.id.home_btn);
        button_home.setOnClickListener(view -> openHome());

        button_medicalh = findViewById(R.id.medicalh_btn);
        button_medicalh.setOnClickListener(view -> openMedicalh());

        button_treatment = findViewById(R.id.treatment_btn);
        button_treatment.setOnClickListener(view -> openTreatment());

        button_reminder = findViewById(R.id.reminder_btn);
        button_reminder.setOnClickListener(view -> openReminder());

        // Initialize text fields
        inputFirstName = findViewById(R.id.first_name);
        inputLastName = findViewById(R.id.last_name);
        inputAge = findViewById(R.id.age);
        inputHeight = findViewById(R.id.height);
        inputWeight = findViewById(R.id.weight);
        btnSubmit = findViewById(R.id.save_btn);
        gender = findViewById(R.id.id_gender);
        bloodtype = findViewById(R.id.id_btype);

        // Initialize change button
        Button changeBtn = findViewById(R.id.change_btn);
        changeBtn.setOnClickListener(view -> enableEditing());

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set onClickListener for submit button
        btnSubmit.setOnClickListener(view -> saveAccountData());

        // Set onClickListener for delete button
       // Button deleteBtn = findViewById(R.id.delete_btn);
       // deleteBtn.setOnClickListener(view -> deleteAccountData());

        // Logic for Gender dropdown
        gender.setOnClickListener(view -> showGenderDialog());

        // Logic for Blood Type dropdown
        bloodtype.setOnClickListener(view -> showBloodTypeDialog());

        loadAccountData();
    }





    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setTitle("Select Gender");
        builder.setSingleChoiceItems(genderArray, selectedGender, (dialog, which) -> selectedGender = which);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedGender != -1) {
                gender.setText(genderArray[selectedGender]);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showBloodTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setTitle("Select Blood Type");
        builder.setSingleChoiceItems(bloodTypeArray, selectedBloodType, (dialog, which) -> selectedBloodType = which);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedBloodType != -1) {
                bloodtype.setText(bloodTypeArray[selectedBloodType]);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }





    private void saveAccountData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Emailul utilizatorului nu a fost găsit", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer userId = databaseHelper.getUserIdByEmail(email);

        if (userId == null) {
            Toast.makeText(this, "Utilizatorul nu a fost găsit", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String ageStr = inputAge.getText().toString();
        String heightStr = inputHeight.getText().toString();
        String weightStr = inputWeight.getText().toString();
        String genderStr = gender.getText().toString();
        String bloodTypeStr = bloodtype.getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(ageStr) ||
                TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(genderStr) ||
                TextUtils.isEmpty(bloodTypeStr)) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            int height = Integer.parseInt(heightStr);
            int weight = Integer.parseInt(weightStr);

            Cursor cursor = databaseHelper.getAccountDataByUserId(userId);

            if (cursor != null && cursor.moveToFirst()) {
                // Data exists, update it
                boolean updateSuccess = databaseHelper.updateAccountData(userId, firstName, lastName, age, height, weight, bloodTypeStr, genderStr);

                if (updateSuccess) {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Data does not exist, insert it
                boolean insertSuccess = databaseHelper.insertAccountData(userId, firstName, lastName, age, height, weight, bloodTypeStr, genderStr);

                if (insertSuccess) {
                    Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            loadAccountData();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadAccountData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email != null) {
            Integer userId = databaseHelper.getUserIdByEmail(email);
            if (userId != null) {
                Cursor cursor = databaseHelper.getAccountDataByUserId(userId);
                if (cursor != null && cursor.moveToFirst()) {
                    String firstName = cursor.getString(cursor.getColumnIndex("first_name"));
                    String lastName = cursor.getString(cursor.getColumnIndex("last_name"));
                    int age = cursor.getInt(cursor.getColumnIndex("age"));
                    int height = cursor.getInt(cursor.getColumnIndex("height"));
                    int weight = cursor.getInt(cursor.getColumnIndex("weight"));
                    String genderStr = cursor.getString(cursor.getColumnIndex("gender"));
                    String bloodTypeStr = cursor.getString(cursor.getColumnIndex("blood_type"));

                    // Display the data
                    displayData(firstName, lastName, String.valueOf(age), String.valueOf(height), String.valueOf(weight), genderStr, bloodTypeStr);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    private void displayData(String firstName, String lastName, String age, String height, String weight, String gender, String bloodType) {
        // Set the text for TextViews
        ((TextView) findViewById(R.id.displayfn)).setText("First Name: " + firstName);
        ((TextView) findViewById(R.id.displayln)).setText("Last Name: " + lastName);
        ((TextView) findViewById(R.id.displayage)).setText("Age: " + age);
        ((TextView) findViewById(R.id.displayheight)).setText("Height: " + height);
        ((TextView) findViewById(R.id.displayweight)).setText("Weight: " + weight);
        ((TextView) findViewById(R.id.displaygender)).setText("Gender: " + gender);
        ((TextView) findViewById(R.id.displaybtype)).setText("Blood Type: " + bloodType);

        // Hide EditTexts
        inputFirstName.setVisibility(View.GONE);
        inputLastName.setVisibility(View.GONE);
        inputAge.setVisibility(View.GONE);
        inputHeight.setVisibility(View.GONE);
        inputWeight.setVisibility(View.GONE);
        findViewById(R.id.id_gender).setVisibility(View.GONE);
        findViewById(R.id.id_btype).setVisibility(View.GONE);

        // Show TextViews
        findViewById(R.id.displayfn).setVisibility(View.VISIBLE);
        findViewById(R.id.displayln).setVisibility(View.VISIBLE);
        findViewById(R.id.displayage).setVisibility(View.VISIBLE);
        findViewById(R.id.displayheight).setVisibility(View.VISIBLE);
        findViewById(R.id.displayweight).setVisibility(View.VISIBLE);
        findViewById(R.id.displaygender).setVisibility(View.VISIBLE);
        findViewById(R.id.displaybtype).setVisibility(View.VISIBLE);

        // Hide Save button and show Change button
        btnSubmit.setVisibility(View.GONE);
        findViewById(R.id.change_btn).setVisibility(View.VISIBLE);
    }



    private void enableEditing() {
        // Hide TextViews
        findViewById(R.id.displayfn).setVisibility(View.GONE);
        findViewById(R.id.displayln).setVisibility(View.GONE);
        findViewById(R.id.displayage).setVisibility(View.GONE);
        findViewById(R.id.displayheight).setVisibility(View.GONE);
        findViewById(R.id.displayweight).setVisibility(View.GONE);
        findViewById(R.id.displaygender).setVisibility(View.GONE);
        findViewById(R.id.displaybtype).setVisibility(View.GONE);

        // Show EditTexts
        inputFirstName.setVisibility(View.VISIBLE);
        inputLastName.setVisibility(View.VISIBLE);
        inputAge.setVisibility(View.VISIBLE);
        inputHeight.setVisibility(View.VISIBLE);
        inputWeight.setVisibility(View.VISIBLE);
        findViewById(R.id.id_gender).setVisibility(View.VISIBLE);
        findViewById(R.id.id_btype).setVisibility(View.VISIBLE);

        // Set EditTexts with current values
        inputFirstName.setText(((TextView) findViewById(R.id.displayfn)).getText().toString().replace("First Name: ", ""));
        inputLastName.setText(((TextView) findViewById(R.id.displayln)).getText().toString().replace("Last Name: ", ""));
        inputAge.setText(((TextView) findViewById(R.id.displayage)).getText().toString().replace("Age: ", ""));
        inputHeight.setText(((TextView) findViewById(R.id.displayheight)).getText().toString().replace("Height: ", ""));
        inputWeight.setText(((TextView) findViewById(R.id.displayweight)).getText().toString().replace("Weight: ", ""));
        gender.setText(((TextView) findViewById(R.id.displaygender)).getText().toString().replace("Gender: ", ""));
        bloodtype.setText(((TextView) findViewById(R.id.displaybtype)).getText().toString().replace("Blood Type: ", ""));

        // Hide Change button and show Save button
        findViewById(R.id.change_btn).setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
    }




    public void openMedicalh() {
        Intent intent = new Intent(this, Medical_History.class);
        startActivity(intent);
    }

    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openTreatment() {
        Intent intent = new Intent(this, Treatment_Schedule.class);
        startActivity(intent);
    }

    public void openReminder() {
        Intent intent = new Intent(this, Reminder.class);
        startActivity(intent);
    }
}

