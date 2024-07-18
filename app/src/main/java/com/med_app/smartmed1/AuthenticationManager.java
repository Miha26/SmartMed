package com.med_app.smartmed1;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthenticationManager {

   /* private static final String PREF_FILE_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";

    private Context context;

    public Authenticator(Context context) {
        this.context = context;
    }

    // Metoda pentru a verifica dacă utilizatorul este autentificat
    public boolean isLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_EMAIL);
    }

    // Metoda pentru a returna adresa de email a utilizatorului autentificat
    public String getCurrentUserEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }

    // Metoda pentru a efectua autentificarea
    public boolean login(String email, String password) {
        // Simulăm o verificare a credențialelor
        if ("test@example.com".equals(email) && "password".equals(password)) {
            // Dacă credențialele sunt corecte, salvăm adresa de email în SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_EMAIL, email);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    // Metoda pentru a efectua deconectarea
    public void logout() {
        // Ștergem adresa de email salvată din SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_EMAIL);
        editor.apply();
    }

    */
}
