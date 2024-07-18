package com.med_app.smartmed1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button button_medicalh,button_treatment, button_account, button_reminder;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Initialize the buttons
        button_account = (Button) findViewById(R.id.account_btn);
        button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openAccount();
            }
        });


        button_medicalh = (Button) findViewById(R.id.medicalh_btn);
        button_medicalh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){openMedicalh();}
        });

        button_treatment = (Button) findViewById(R.id.treatment_btn);
        button_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openTreatment();
            }
        });

        button_reminder = (Button) findViewById(R.id.reminder_btn);
        button_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openReminder();
            }
        });

        }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SmartMedChannel";
            String description = "Channel for SmartMed reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("SmartMedChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void displayUsername() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            String firstName = databaseHelper.getFirstNameByUserId(userId);
            if (firstName != null && !firstName.isEmpty()) {
                welcomeTextView.setText("Hello, " + firstName);
            } else {
                welcomeTextView.setText("Hello!");
            }
        } else {
            welcomeTextView.setText("Hello!");
        }
        if(userId == 1)
        {
            welcomeTextView.setText("Hello, Mihaela!");
        }
    }





    public void openMedicalh()
    {
        Intent intent = new Intent(this,Medical_History.class);
        startActivity(intent);
    }
    public void openAccount()
    {
        Intent intent =  new Intent(this,Account.class);
        startActivity(intent);

    }

    public void openTreatment()
    {
        Intent intent =  new Intent(this,Treatment_Schedule.class);
        startActivity(intent);

    }
    public void openReminder()
    {
        Intent intent =  new Intent(this,Reminder.class);
        startActivity(intent);

    }
   /* public void openLoading()
    {
        Intent intent =  new Intent(this,Loading.class);
        startActivity(intent);

    }*/
    public void openLogin()
    {
        Intent intent =  new Intent(this,Login.class);
        startActivity(intent);

    }
    public void openSignup()
    {
        Intent intent =  new Intent(this,Signup.class);
        startActivity(intent);

    }
}