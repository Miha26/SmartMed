///Medical History v0 21:32
// Medical_History.java

package com.med_app.smartmed1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class Medical_History extends AppCompatActivity {
    private Button button_treatment, button_home, button_account, button_reminder, saveButton;
    private static final String TAG = "Medical_History";
    private Uri fileUri;
    private LinearLayout filesContainer;
    private DatabaseHelper databaseHelper;
    private EditText conditionTitle;
    private Spinner conditionTypeSpinner;

    private final int PICK_FILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        button_account = findViewById(R.id.account_btn);
        button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccount();
            }
        });

        button_home = findViewById(R.id.home_btn);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        button_treatment = findViewById(R.id.treatment_btn);
        button_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTreatment();
            }
        });

        button_reminder = findViewById(R.id.reminder_btn);
        button_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReminder();
            }
        });

        Button uploadButton = findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upload button clicked");
                openFilePicker();
            }
        });

        saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConditionToDatabase();
            }
        });

        conditionTitle = findViewById(R.id.condition_title);
        conditionTypeSpinner = findViewById(R.id.time_of_day_spinner);
        setupSpinner(conditionTypeSpinner);

        filesContainer = findViewById(R.id.files_container);

        // Load medical history files on start
        loadMedicalHistoryFiles();
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.condition_type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Log.d(TAG, "File selected: " + fileUri.toString());
            saveFileToInternalStorage(fileUri);
        } else {
            Log.d(TAG, "No file selected");
        }
    }

    private void saveFileToInternalStorage(Uri fileUri) {
        String fileName = "medical_history_" + System.currentTimeMillis();
        File destinationFile = new File(getFilesDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            Log.d(TAG, "File saved successfully to: " + destinationFile.getAbsolutePath());

            // Save file information to database
            saveMedicalHistoryToDatabase(destinationFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "File save error: " + e.getMessage(), e);
        }
    }

    private void saveMedicalHistoryToDatabase(String filePath) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Emailul utilizatorului nu a fost găsit", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertSuccess = databaseHelper.insertMedicalHistory(email, filePath);
        if (insertSuccess) {
            Toast.makeText(this, "Medical history saved to database", Toast.LENGTH_SHORT).show();
            loadMedicalHistoryFiles(); // Load files after saving a new one
        } else {
            Toast.makeText(this, "Failed to save medical history to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConditionToDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Emailul utilizatorului nu a fost găsit", Toast.LENGTH_SHORT).show();
            return;
        }

        String condition = conditionTitle.getText().toString();
        String conditionType = conditionTypeSpinner.getSelectedItem().toString();

        if (condition.isEmpty()) {
            Toast.makeText(this, "Please enter a medical problem or allergy", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertSuccess = databaseHelper.insertMedicalHistory(email, condition + " (" + conditionType + ")");

        if (insertSuccess) {
            Toast.makeText(this, "Medical condition saved to database", Toast.LENGTH_SHORT).show();
            loadMedicalHistoryFiles();
        } else {
            Toast.makeText(this, "Failed to save medical condition to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMedicalHistoryFiles() {
        filesContainer.removeAllViews();

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            return;
        }

        List<HashMap<String, String>> files = databaseHelper.getMedicalHistoryByEmail(email);

        for (HashMap<String, String> file : files) {
            LinearLayout fileLayout = new LinearLayout(this);
            fileLayout.setOrientation(LinearLayout.HORIZONTAL);
            fileLayout.setPadding(16, 16, 16, 16);

            TextView fileText = new TextView(this);
            fileText.setText(file.get("downloaded_file"));
            fileText.setPadding(16, 16, 16, 16);
            fileText.setTextColor(Color.BLACK);
            //Button downloadButton = new Button(this);
            //downloadButton.setText("Download");
            //downloadButton.setOnClickListener(v -> downloadFileFromInternalStorage(file.get("downloaded_file")));
            //fileLayout.addView(downloadButton);

            fileLayout.addView(fileText);

            filesContainer.addView(fileLayout);
        }
    }

    private void downloadFileFromInternalStorage(String filePath) {
        Log.d(TAG, "Attempting to download file: " + filePath);
        File sourceFile = new File(filePath);
        File destinationFile = new File(getExternalFilesDir(null), "downloaded_" + sourceFile.getName());

        Log.d(TAG, "Source file path: " + sourceFile.getAbsolutePath());
        Log.d(TAG, "Destination file path: " + destinationFile.getAbsolutePath());

        if (!sourceFile.exists()) {
            Log.e(TAG, "Source file does not exist: " + sourceFile.getAbsolutePath());
            return;
        }

        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            Log.d(TAG, "File downloaded successfully to: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "File download error: " + e.getMessage(), e);
        }
    }

    public void openReminder() {
        Intent intent = new Intent(this, Reminder.class);
        startActivity(intent);
    }

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
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
}
