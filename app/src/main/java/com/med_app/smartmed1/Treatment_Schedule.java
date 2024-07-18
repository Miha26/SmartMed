/// Treatment S v1 ora 15:20
package com.med_app.smartmed1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Treatment_Schedule extends AppCompatActivity {

    private Button button_medicalh, button_home, button_reminder, button_account, btnSubmit, btnAddRow;
    private EditText treatmentTitle;
    private TableLayout treatmentTable;
    private Spinner timeOfDaySpinner;

    private DatabaseHelper databaseHelper;
    private static final String TAG = "Treatment_Schedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_schedule);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize buttons
        button_home = findViewById(R.id.home_btn);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
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

        button_reminder = findViewById(R.id.reminder_btn);
        button_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReminder();
            }
        });

        // Initialize EditText and TableLayout
        treatmentTitle = findViewById(R.id.treatment_title);
        treatmentTable = findViewById(R.id.timeofday_table);

        // Initialize Spinner
        timeOfDaySpinner = findViewById(R.id.time_of_day_spinner);
        setupSpinner(timeOfDaySpinner);

        // Initialize Submit button and set OnClickListener
        btnSubmit = findViewById(R.id.save_btn);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTreatmentSchedule();
            }
        });

        // Initialize Add Row button and set OnClickListener
        btnAddRow = findViewById(R.id.addrow_btn);
        btnAddRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTableRow();
            }
        });

        // Initialize the existing row's time EditText to use TimePickerDialog
        initializeTimePicker((EditText) findViewById(R.id.initial_time));

        Button seeScheduledBtn = findViewById(R.id.see_scheduled_btn);
        seeScheduledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayScheduledTreatments();
            }
        });
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_of_day_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initializeTimePicker(EditText editTextTime) {
        editTextTime.setFocusable(false);
        editTextTime.setClickable(true);
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextTime);
            }
        });
    }

    private void addTableRow() {
        TableRow tableRow = new TableRow(this);

        EditText editTextName = new EditText(this);
        editTextName.setHint("Treatment Name");
        tableRow.addView(editTextName);

        EditText editTextTime = new EditText(this);
        editTextTime.setHint("Treatment Time");
        initializeTimePicker(editTextTime);  // Folosește metoda ta existentă pentru a inițializa picker-ul de timp
        tableRow.addView(editTextTime);

        treatmentTable.addView(tableRow);
    }


    private void showTimePickerDialog(final EditText timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        timeEditText.setText(time);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveTreatmentSchedule() {
        try {
            // Obține email-ul din SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);

            if (email == null) {
                Toast.makeText(this, "User's email could not be found", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Saving treatment schedule for email: " + email);

            String title = treatmentTitle.getText().toString();
            String timeOfDay = timeOfDaySpinner.getSelectedItem().toString();

            if (title.isEmpty() || timeOfDay.isEmpty()) {
                Toast.makeText(this, "All credentials are mandatory!", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < treatmentTable.getChildCount(); i++) {
                View row = treatmentTable.getChildAt(i);
                if (row instanceof TableRow) {
                    TableRow tableRow = (TableRow) row;

                    View nameView = tableRow.getChildAt(0);
                    View timeView = tableRow.getChildAt(1);

                    // Jurnalizează tipul vizualizărilor
                    Log.d(TAG, "Row " + i + ": NameView type - " + nameView.getClass().getName() + ", TimeView type - " + timeView.getClass().getName());

                    // Verifică dacă vizualizările sunt de tip EditText
                    if (nameView instanceof EditText && timeView instanceof EditText) {
                        EditText nameEditText = (EditText) nameView;
                        EditText timeEditText = (EditText) timeView;

                        String name = nameEditText.getText().toString();
                        String time = timeEditText.getText().toString();

                        if (name.isEmpty() || time.isEmpty()) {
                            Toast.makeText(this, "All table credentials are mandatory!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d(TAG, "Inserting row: " + name + ", " + time);

                        boolean insertSuccess = databaseHelper.insertTreatmentSchedule(email, title, timeOfDay, name, time);

                        if (insertSuccess) {
                            Toast.makeText(this, "Treatment Schedule successfully saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to save the credentials!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Doar jurnalizează eroarea fără să afișezi un Toast și fără să oprești procesarea
                        Log.e(TAG, "Unexpected view type found in TableRow. NameView: " + nameView.getClass().getName() + ", TimeView: " + timeView.getClass().getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving treatment schedule", e);
            Toast.makeText(this, "An error occurred while saving the treatment schedule.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayScheduledTreatments() {
        // Obține email-ul din SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "User's email could not be found", Toast.LENGTH_SHORT).show();
            return;
        }

        List<HashMap<String, String>> schedules = databaseHelper.getTreatmentSchedulesByEmail(email);

        // Sortare după titlu
        Collections.sort(schedules, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                return o1.get("title").compareToIgnoreCase(o2.get("title"));
            }
        });

        // Grupare după `time_of_day`
        Map<String, List<HashMap<String, String>>> groupedSchedules = new TreeMap<>();
        for (HashMap<String, String> schedule : schedules) {
            String timeOfDay = schedule.get("time_of_day");
            if (!groupedSchedules.containsKey(timeOfDay)) {
                groupedSchedules.put(timeOfDay, new ArrayList<>());
            }
            groupedSchedules.get(timeOfDay).add(schedule);
        }

        // Sortare fiecare grup după ora tratamentului
        for (Map.Entry<String, List<HashMap<String, String>>> entry : groupedSchedules.entrySet()) {
            Collections.sort(entry.getValue(), new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                    return o1.get("time").compareTo(o2.get("time"));
                }
            });
        }

        TableLayout scheduledTable = findViewById(R.id.scheduled_table);
        scheduledTable.removeAllViews(); // Golește tabelul înainte de a adăuga rânduri noi

        // Adaugă titlul tabelului
        TableRow headerRow = new TableRow(this);
        TextView headerTitle = createTextView("Treatment Title", true, Gravity.LEFT, true);
        TextView headerName = createTextView("Treatment Name", true, Gravity.LEFT, true);
        TextView headerTime = createTextView("Treatment Time", true, Gravity.LEFT, true);
        headerRow.addView(headerTitle);
        headerRow.addView(headerName);
        headerRow.addView(headerTime);
        scheduledTable.addView(headerRow);

        // Afișare tratamente sortate
        for (Map.Entry<String, List<HashMap<String, String>>> entry : groupedSchedules.entrySet()) {
            String timeOfDay = entry.getKey();
            List<HashMap<String, String>> treatments = entry.getValue();

            // Adaugă un rând pentru fiecare `time_of_day`
            TableRow timeOfDayRow = new TableRow(this);
            TextView timeOfDayText = createTextView(timeOfDay, true, Gravity.CENTER, true);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 3; // Asigură-te că textul ocupă toate cele 3 coloane
            timeOfDayText.setLayoutParams(params);
            timeOfDayRow.addView(timeOfDayText);
            scheduledTable.addView(timeOfDayRow);

            // Sortare după titlu
            Map<String, List<HashMap<String, String>>> groupedByTitle = new TreeMap<>();
            for (HashMap<String, String> treatment : treatments) {
                String title = treatment.get("title");
                if (!groupedByTitle.containsKey(title)) {
                    groupedByTitle.put(title, new ArrayList<>());
                }
                groupedByTitle.get(title).add(treatment);
            }

            for (Map.Entry<String, List<HashMap<String, String>>> titleEntry : groupedByTitle.entrySet()) {
                String title = titleEntry.getKey();
                List<HashMap<String, String>> titleTreatments = titleEntry.getValue();

                // Adaugă un rând pentru fiecare titlu
                TableRow titleRow = new TableRow(this);
                TextView titleText = createTextView(title, true, Gravity.LEFT, true);
                TableRow.LayoutParams titleParams = new TableRow.LayoutParams();
                titleParams.span = 3; // Asigură-te că textul ocupă toate cele 3 coloane
                titleText.setLayoutParams(titleParams);
                titleRow.addView(titleText);
                scheduledTable.addView(titleRow);

                for (HashMap<String, String> treatment : titleTreatments) {
                    TableRow tableRow = new TableRow(this);

                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            // Șterge tratamentul din baza de date și actualizează afișarea
                            databaseHelper.deleteTreatmentSchedule(email, title, timeOfDay, treatment.get("name"), treatment.get("time"));
                            displayScheduledTreatments();
                        }
                    });

                    TextView nameTextView = createTextView(treatment.get("name"), false, Gravity.LEFT, false);
                    TextView timeTextView = createTextView(treatment.get("time"), false, Gravity.LEFT, false);
                    tableRow.addView(checkBox);
                    tableRow.addView(nameTextView);
                    tableRow.addView(timeTextView);

                    scheduledTable.addView(tableRow);
                }
            }
        }

        // Afișează tabelul și titlul
        findViewById(R.id.scheduled_title).setVisibility(View.VISIBLE);
        scheduledTable.setVisibility(View.VISIBLE);
    }

    private TextView createTextView(String text, boolean isHeader, int gravity, boolean isTitle) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(gravity);
        textView.setTextColor(Color.BLACK); // Setează culoarea textului la negru pentru toate textview-urile
        if (isHeader || isTitle) {
            textView.setTypeface(null, Typeface.BOLD);
            if (isTitle) {
                textView.setTextSize(18); // Dimensiune text pentru titluri
            }
        }
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8); // Margini pentru a controla distanța dintre coloane
        textView.setLayoutParams(params);
        return textView;
    }












    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openMedicalh() {
        Intent intent = new Intent(this, Medical_History.class);
        startActivity(intent);
    }

    public void openReminder() {
        Intent intent = new Intent(this, Reminder.class);
        startActivity(intent);
    }

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }
}
