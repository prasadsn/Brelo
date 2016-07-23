package com.armor.brelo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.armor.brelo.db.model.Lock;

/**
 * Created by prsn0001 on 7/23/2016.
 */
public class ApplicationSettings {

    private static final String PREF_NAME = "BRELO_DEFAULT_PREFERENCES";
    public static final String PREF_KEY_LOCK_STATUS = "PREF_KEY_LOCK_STATUS";

    public static class BreloSharedPreferences {
        private static BreloSharedPreferences breloSharedPreferences;
        private BreloSharedPreferences(){
        }

        public static BreloSharedPreferences getInstance(){
            if(breloSharedPreferences == null)
                breloSharedPreferences = new BreloSharedPreferences();
            return breloSharedPreferences;
        }

        public void putInt(String key, int data, Context context) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, data);
            editor.commit();
        }

        public int getInt(String key, Context context) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return preferences.getInt(key, Lock.LOCK_STATUS_LOCKED);
        }
    }
}
