package com.med_app.smartmed1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Reminder extends AppCompatActivity {
    private Button button_medicalh, button_treatment, button_account, button_home;
    private EditText titleET, messageET;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button submitButton;
    private CheckBox repeatDaily, repeatWeekly;
    private LinearLayout remindersContainer;
    private TextView remindersTitle;
    private ScrollView remindersScrollView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        titleET = findViewById(R.id.titleET);
        messageET = findViewById(R.id.messageET);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        submitButton = findViewById(R.id.submit_btn);
        repeatDaily = findViewById(R.id.repeatDaily);
        repeatWeekly = findViewById(R.id.repeatWeekly);
        remindersContainer = findViewById(R.id.reminders_container);
        remindersTitle = findViewById(R.id.reminders_title);
        remindersScrollView = findViewById(R.id.reminders_scroll_view);

        createNotificationChannel();  // Creează canalul de notificări

        submitButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms()) {
                requestExactAlarmPermission();
            } else {
                scheduleNotification();
                loadReminders(); // Load reminders after scheduling a new one
            }
        });

        button_account = findViewById(R.id.account_btn);
        button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccount();
            }
        });

        button_medicalh = findViewById(R.id.medicalh_btn);
        button_medicalh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMedicalh();
            }
        });

        button_treatment = findViewById(R.id.treatment_btn);
        button_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTreatment();
            }
        });

        button_home = findViewById(R.id.home_btn);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        // Load reminders on start
        loadReminders();
    }

    private void scheduleNotification() {
        String title = titleET.getText().toString();
        String message = messageET.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getHour(),
                timePicker.getMinute(),
                0
        );

        // Save reminder to database
        saveReminderToDatabase(title, message, calendar);

        int notificationId = new Random().nextInt(); // Generate a unique notification ID

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("titleExtra", title);
        intent.putExtra("messageExtra", message);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            try {
                if (repeatDaily.isChecked()) {
                    scheduleRepeatingNotification(alarmManager, calendar, pendingIntent, AlarmManager.INTERVAL_DAY);
                } else if (repeatWeekly.isChecked()) {
                    scheduleRepeatingNotification(alarmManager, calendar, pendingIntent, AlarmManager.INTERVAL_DAY * 7);
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }

                Toast.makeText(this, "Notification Scheduled", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(this, "Permission denied to schedule exact alarms", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "AlarmManager is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReminderToDatabase(String title, String message, Calendar calendar) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Emailul utilizatorului nu a fost găsit", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        String time = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        boolean insertSuccess = databaseHelper.insertReminder(email, title, message, date, time);
        if (insertSuccess) {
            Toast.makeText(this, "Reminder saved to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save reminder to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleRepeatingNotification(AlarmManager alarmManager, Calendar calendar, PendingIntent pendingIntent, long interval) {
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                interval,
                pendingIntent
        );
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadReminders() {
        remindersContainer.removeAllViews();

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            return;
        }

        List<HashMap<String, String>> reminders = databaseHelper.getRemindersByEmail(email);

        if (reminders.isEmpty()) {
            remindersTitle.setVisibility(View.GONE);
            remindersScrollView.setVisibility(View.GONE);
            return;
        }

        remindersTitle.setVisibility(View.VISIBLE);
        remindersScrollView.setVisibility(View.VISIBLE);

        for (HashMap<String, String> reminder : reminders) {
            LinearLayout reminderLayout = new LinearLayout(this);
            reminderLayout.setOrientation(LinearLayout.HORIZONTAL);
            reminderLayout.setPadding(16, 16, 16, 16);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    databaseHelper.deleteReminder(reminder.get("title"), reminder.get("message"), reminder.get("date"), reminder.get("time"));
                    loadReminders(); // Reload reminders after deletion
                }
            });

            TextView reminderText = new TextView(this);
            reminderText.setText(String.format("%s\n%s\n%s %s", reminder.get("title"), reminder.get("message"), reminder.get("date"), reminder.get("time")));
            reminderText.setPadding(16, 0, 0, 0);

            reminderLayout.addView(checkBox);
            reminderLayout.addView(reminderText);

            remindersContainer.addView(reminderLayout);
        }
    }

    public void openMedicalh() {
        Intent intent = new Intent(this, Medical_History.class);
        startActivity(intent);
    }

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }

    public void openTreatment() {
        Intent intent = new Intent(this, Treatment_Schedule.class);
        startActivity(intent);
    }

    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
