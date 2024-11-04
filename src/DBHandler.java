package com.jackbartels.langvillage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "languagedb";
    private static final int DB_VERSION = 1;

    private static SimpleDateFormat sdf;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /*
     * Each foreign language will have its own set of four tables:
     * x_word, x_variation, x_eng_variation, and x_progress
     *
     * category, part_of_speech, and region will be shared between all languages and should be
     * updated only as necessary (ideally never removing columns--or even rows--from the
     * aforementioned tables without careful onUpgrade logic)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // category [R]
        db.execSQL("CREATE TABLE category(" +
                   "id INTEGER PRIMARY KEY NOT NULL," +
                   "category TEXT NOT NULL)");

        // part_of_speech [R]
        db.execSQL("CREATE TABLE part_of_speech(" +
                   "id INTEGER PRIMARY KEY NOT NULL," +
                   "pos TEXT NOT NULL)");

        // region [R]
        db.execSQL("CREATE TABLE region(" +
                   "id INTEGER PRIMARY KEY NOT NULL," +
                   "region TEXT NOT NULL)");

        // esp_word [R]
        db.execSQL("CREATE TABLE esp_word(" +
                   "id INTEGER PRIMARY KEY NOT NULL," +
                   "word TEXT NOT NULL," +
                   "category_id INTEGER NOT NULL," +
                   "pos_id INTEGER NOT NULL," +
                   "group_id INTEGER NOT NULL," +
                   "UNIQUE(word, pos_id) ON CONFLICT REPLACE," +
                   "CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(id)," +
                   "CONSTRAINT fk_part_of_speech FOREIGN KEY (pos_id) REFERENCES part_of_speech(id))");

        // esp_variation [R]
        db.execSQL("CREATE TABLE esp_variation(" +
                   "esp_word_id INTEGER NOT NULL," +
                   "variation TEXT NOT NULL," +
                   "region_id INTEGER NOT NULL," +
                   "CONSTRAINT pk_esp_variation PRIMARY KEY (esp_word_id, region_id)," +
                   "CONSTRAINT fk_esp_word FOREIGN KEY (esp_word_id) REFERENCES esp_word(id)," +
                   "CONSTRAINT fk_region FOREIGN KEY (region_id) REFERENCES region(id))");

        // esp_eng_variation [R]
        db.execSQL("CREATE TABLE esp_eng_variation(" +
                   "esp_word_id INTEGER NOT NULL," +
                   "variation TEXT NOT NULL," +
                   "region_id INTEGER NOT NULL," +
                   "CONSTRAINT pk_esp_eng_variation PRIMARY KEY (esp_word_id, region_id)," +
                   "CONSTRAINT fk_esp_word FOREIGN KEY (esp_word_id) REFERENCES esp_word(id)," +
                   "CONSTRAINT fk_region FOREIGN KEY (region_id) REFERENCES region(id))");

        // esp_progress [RW]
        db.execSQL("CREATE TABLE esp_progress(" +
                   "esp_word_id INTEGER NOT NULL PRIMARY KEY," +
                   "last_succeeded TEXT," + // UTC Datetime
                   "last_failed TEXT," + // UTC Datetime
                   "times_succeeded INTEGER DEFAULT 0 NOT NULL," +
                   "times_failed INTEGER DEFAULT 0 NOT NULL," +
                   "CONSTRAINT fk_esp_word FOREIGN KEY (esp_word_id) REFERENCES esp_word(id))");
    }

    /*
     * For translation changes/corrections: reset progress for all affected records
     * Adding/removing words is safe
     * Adding categories, parts of speech, and regions is safe
     * Removing categories, parts of speech, or regions is UNSAFE (needs handling)
     * Adding/removing columns in any table is UNSAFE (needs handling)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor selectProgress(SQLiteDatabase db, int word_id) {
        String[] whereArgs = {String.valueOf(word_id)};

        return db.rawQuery("SELECT * FROM esp_progress WHERE esp_word_id = ?", whereArgs);
    }

    public void updateProgress(SQLiteDatabase db, int word_id, Boolean success) {
        Cursor cursor = selectProgress(db, word_id);
        ContentValues contentValues = new ContentValues(2);

        // Get current datetime
        String currentDateandTime = sdf.format(new Date());

        // Prep new success/failure column values
        if(success) {
            try {
                int times_succeeded =
                        cursor.getInt(cursor.getColumnIndexOrThrow("times_succeeded"));

                contentValues.put("last_succeeded", currentDateandTime);
                contentValues.put("times_succeeded", ++times_succeeded);
            } catch(Exception e) {
                e.printStackTrace(); // TODO: Add proper logging
                MainActivity.quit = true;
            }
        } else {
            try {
                int times_failed =
                        cursor.getInt(cursor.getColumnIndexOrThrow("times_failed"));

                contentValues.put("last_failed", currentDateandTime);
                contentValues.put("times_failed", ++times_failed);
            } catch(Exception e) {
                e.printStackTrace(); // TODO: Add proper logging
                MainActivity.quit = true;
            }
        }

        String[] whereArgs = {String.valueOf(word_id)};
        db.update("esp_progress", contentValues, "WHERE esp_word_id = ?", whereArgs);

        cursor.close();
    }
}
