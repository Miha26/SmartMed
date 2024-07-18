///Database Helper v0 21:33
package com.med_app.smartmed1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "Application.db";
    private static final String TAG = "DatabaseHelper";
    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        try {
            MyDatabase.execSQL("PRAGMA foreign_keys=ON;"); // Enable foreign key constraints
            MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS allusers (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)");
            MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS accounttable (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, first_name TEXT, last_name TEXT, age INTEGER, height INTEGER, weight INTEGER, blood_type TEXT, gender TEXT, FOREIGN KEY (user_id) REFERENCES allusers(id) ON DELETE CASCADE)");
            MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS medical_history (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, downloaded_file TEXT, condition TEXT, type TEXT, FOREIGN KEY (user_id) REFERENCES allusers(id) ON DELETE CASCADE)");
            MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS reminder (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, message TEXT, date TEXT, time TEXT, FOREIGN KEY (user_id) REFERENCES allusers(id) ON DELETE CASCADE)");
            MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS treatment_schedule (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, time_of_day TEXT, name TEXT, time TEXT, FOREIGN KEY (user_id) REFERENCES allusers(id) ON DELETE CASCADE)");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        try {
            MyDatabase.execSQL("DROP TABLE IF EXISTS allusers");
            MyDatabase.execSQL("DROP TABLE IF EXISTS accounttable");
            MyDatabase.execSQL("DROP TABLE IF EXISTS medical_history");
            MyDatabase.execSQL("DROP TABLE IF EXISTS reminder");
            MyDatabase.execSQL("DROP TABLE IF EXISTS treatment_schedule");
            onCreate(MyDatabase);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading tables", e);
        }
    }
    @Override
    public void onDowngrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        try {
            MyDatabase.execSQL("DROP TABLE IF EXISTS allusers");
            MyDatabase.execSQL("DROP TABLE IF EXISTS accounttable");
            MyDatabase.execSQL("DROP TABLE IF EXISTS medical_history");
            MyDatabase.execSQL("DROP TABLE IF EXISTS reminder");
            MyDatabase.execSQL("DROP TABLE IF EXISTS treatment_schedule");
            onCreate(MyDatabase);
        } catch (Exception e) {
            Log.e(TAG, "Error downgrading tables", e);
        }
    }
    public Boolean insertUserData(String email, String password) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert("allusers", null, contentValues);

        if (result == -1) {
            Log.e(TAG, "Failed to insert user data");
            return false;
        } else {
            Log.i(TAG, "User data inserted successfully");
            return true;
        }
    }

    public boolean insertAccountData(int userId, String firstName, String lastName, int age, int height, int weight, String bloodType, String gender) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userId);
        contentValues.put("first_name", firstName);
        contentValues.put("last_name", lastName);
        contentValues.put("age", age);
        contentValues.put("height", height);
        contentValues.put("weight", weight);
        contentValues.put("blood_type", bloodType);
        contentValues.put("gender", gender);

        Log.d(TAG, "Inserting account data: " + contentValues);

        long result = db.insert("accounttable", null, contentValues);

        if (result == -1) {
            Log.e(TAG, "Failed to insert account data");
            return false;
        } else {
            Log.i(TAG, "Account data inserted successfully");
            return true;
        }
    }

    public boolean insertMedicalHistory(String email, String downloadedFile) {
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", user_id);
            contentValues.put("downloaded_file", downloadedFile);

            long result = db.insert("medical_history", null, contentValues);

            if (result == -1) {
                Log.e(TAG, "Failed to insert medical history data");
                return false;
            } else {
                Log.i(TAG, "Medical history data inserted successfully");
                return true;
            }
        } else {
            Log.e(TAG, "User ID not found for email: " + email);
            return false;
        }
    }

    public boolean insertReminder(String email, String title, String message, String date, String time) {
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", user_id);
            contentValues.put("title", title);
            contentValues.put("message", message);
            contentValues.put("date", date);
            contentValues.put("time", time);

            long result = db.insert("reminder", null, contentValues);

            if (result == -1) {
                Log.e(TAG, "Failed to insert reminder data");
                return false;
            } else {
                Log.i(TAG, "Reminder data inserted successfully");
                return true;
            }
        } else {
            Log.e(TAG, "User ID not found for email: " + email);
            return false;
        }
    }

    public boolean insertTreatmentSchedule(String email, String title, String timeOfDay, String name, String time) {
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", user_id);
            contentValues.put("title", title);
            contentValues.put("time_of_day", timeOfDay);
            contentValues.put("name", name);
            contentValues.put("time", time);

            long result = db.insert("treatment_schedule", null, contentValues);

            if (result == -1) {
                Log.e(TAG, "Failed to insert treatment schedule data");
                return false;
            } else {
                Log.i(TAG, "Treatment schedule data inserted successfully");
                return true;
            }
        } else {
            Log.e(TAG, "User ID not found for email: " + email);
            return false;
        }
    }

    public boolean deleteReminder(String title, String message, String date, String time) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
        int result = db.delete("reminder", "title = ? AND message = ? AND date = ? AND time = ?",
                new String[]{title, message, date, time});
        return result > 0;
    }

    public boolean deleteTreatmentSchedule(String email, String title, String timeOfDay, String name, String time) {
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            int result = db.delete("treatment_schedule", "user_id = ? AND title = ? AND time_of_day = ? AND name = ? AND time = ?",
                    new String[]{String.valueOf(user_id), title, timeOfDay, name, time});
            return result > 0;
        } else {
            Log.e(TAG, "User ID not found for email: " + email);
            return false;
        }
    }

    public boolean updateAccountData(int userId, String firstName, String lastName, int age, int height, int weight, String bloodType, String gender) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", firstName);
        contentValues.put("last_name", lastName);
        contentValues.put("age", age);
        contentValues.put("height", height);
        contentValues.put("weight", weight);
        contentValues.put("blood_type", bloodType);
        contentValues.put("gender", gender);

        int result = db.update("accounttable", contentValues, "user_id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }




    public String getFirstNameByUserId(int userId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        String firstName = null;
        Cursor cursor = db.rawQuery("SELECT first_name FROM accounttable WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndex("first_name"));
        }
        cursor.close();
        return firstName;
    }






    public Integer getUserIdByEmail(String email) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery("SELECT id FROM allusers WHERE email = ?", new String[]{email});

        Integer userId = null;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return userId;
    }

    public List<HashMap<String, String>> getTreatmentSchedulesByEmail(String email) {
        List<HashMap<String, String>> schedules = new ArrayList<>();
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            Cursor cursor = db.rawQuery("SELECT title, time_of_day, name, time FROM treatment_schedule WHERE user_id = ?", new String[]{String.valueOf(user_id)});

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> schedule = new HashMap<>();
                    schedule.put("title", cursor.getString(cursor.getColumnIndex("title")));
                    schedule.put("time_of_day", cursor.getString(cursor.getColumnIndex("time_of_day")));
                    schedule.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    schedule.put("time", cursor.getString(cursor.getColumnIndex("time")));
                    schedules.add(schedule);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return schedules;
    }

    public List<HashMap<String, String>> getRemindersByEmail(String email) {
        int user_id = getUserIdByEmail(email);

        List<HashMap<String, String>> reminders = new ArrayList<>();

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            Cursor cursor = db.rawQuery("SELECT title, message, date, time FROM reminder WHERE user_id = ?", new String[]{String.valueOf(user_id)});

            while (cursor.moveToNext()) {
                HashMap<String, String> reminder = new HashMap<>();
                reminder.put("title", cursor.getString(cursor.getColumnIndex("title")));
                reminder.put("message", cursor.getString(cursor.getColumnIndex("message")));
                reminder.put("date", cursor.getString(cursor.getColumnIndex("date")));
                reminder.put("time", cursor.getString(cursor.getColumnIndex("time")));
                reminders.add(reminder);
            }
            cursor.close();
        } else {
            Log.e(TAG, "User ID not found for email: " + email);
        }
        return reminders;
    }

    public List<HashMap<String, String>> getMedicalHistoryByEmail(String email) {
        List<HashMap<String, String>> medicalHistories = new ArrayList<>();
        int user_id = getUserIdByEmail(email);

        if (user_id != -1) {
            if (!db.isOpen()) {
                db = this.getWritableDatabase();
            }
            Cursor cursor = db.rawQuery("SELECT downloaded_file FROM medical_history WHERE user_id = ?", new String[]{String.valueOf(user_id)});

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> medicalHistory = new HashMap<>();
                    medicalHistory.put("downloaded_file", cursor.getString(cursor.getColumnIndex("downloaded_file")));
                    medicalHistories.add(medicalHistory);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return medicalHistories;
    }

    public Cursor getAccountDataByUserId(int userId) {
        if (!db.isOpen()) {
            db = this.getReadableDatabase();
        }
        return db.rawQuery("SELECT * FROM accounttable WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }


    public Boolean checkEmail(String email) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ?", new String[]{email});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ? AND password = ?", new String[]{email, password});

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }
}
