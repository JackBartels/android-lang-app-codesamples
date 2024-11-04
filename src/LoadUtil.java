package com.jackbartels.langvillage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;

public class LoadUtil {

    public static void loadMap(Map.MapName mName, Context context) {
        // Initial values default to VILLAGE
        String mapFile = "maps/map_village.txt";
        int mapHeight = 20;
        int mapWidth = 34;

        Map.currentMapName = mName;

        switch(mName) {
            case VILLAGE:
                Map.currentMap = new String[mapHeight][mapWidth];
        }

        try {
            InputStream mapInputStream = context.getAssets().open(mapFile);
            Scanner scan = new Scanner(mapInputStream);
            String[] line;

            while(scan.hasNextLine()) {
                for(int i = 0; i < mapHeight; i++) {
                    line = scan.nextLine().trim().split(" ");
                    System.arraycopy(line, 0, Map.currentMap[i], 0, mapWidth);
                }
            }

            scan.close();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Add proper logging

            MainActivity.quit = true; // Quit app
        }
    }

    public static void loadSave(GameState gameState, Context context) {
        File fileDir = context.getFilesDir();
        File saveFile = new File(fileDir, SaveUtil.SAVE_FILE_NAME);

        if (!saveFile.exists()) { return; }; // Do not attempt load if no save present

        try {
            FileInputStream fileInStream = new FileInputStream(saveFile);
            ObjectInputStream objInStream = new ObjectInputStream(fileInStream);

            // Necessary cast for loading serialized object from file
            @SuppressWarnings("unchecked")
            HashMap<String, String> loadContent =
                    (HashMap<String, String>) objInStream.readObject();

            // Player data
            gameState.getPlayer().setPlayerX(Integer.parseInt(loadContent.get("pX")));
            gameState.getPlayer().setPlayerY(Integer.parseInt(loadContent.get("pY")));
            gameState.getPlayer().setPlayerFacing(
                    Player.PlayerFacing.valueOf(loadContent.get("pFace")));

            // Map data
            Map.currentMapName = Map.MapName.valueOf(loadContent.get("currMap"));

            // Settings data
            GameState.amoledMode = Integer.parseInt(loadContent.get("amo"));
            GameState.fpsMode = Integer.parseInt(loadContent.get("fpsM"));
            GameState.displayFps = Integer.parseInt(loadContent.get("fpsD"));

            fileInStream.close();
            objInStream.close();
        } catch(Exception e) {
            e.printStackTrace(); // TODO: Add proper logging

            MainActivity.quit = true; // Quit app
        }
    }

    public static void loadDbTable(SQLiteDatabase db, Context context, String tableName) {
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + tableName, null);

        if (cursor.getCount() != 0) { return; } // Do not attempt to load if records already present

        cursor.close();

        String fileName = "table_" + tableName + ".csv";

        ContentValues contentValues = new ContentValues();

        try {
            InputStream tableInputStream = context.getAssets().open("tables/" + fileName);
            Scanner scan = new Scanner(tableInputStream);
            String[] columnNames = scan.nextLine().trim().split(",");

            String[] line;

            while(scan.hasNextLine()) {
                line = scan.nextLine().trim().split(",");

                for(int i = 0; i < columnNames.length; i++) {
                    contentValues.put(columnNames[i], line[i]);
                }

                db.insert(tableName, null, contentValues);
            }

            scan.close();
        } catch(Exception e) {
            e.printStackTrace(); // TODO: Add proper logging

            MainActivity.quit = true;
        }
    }
}
