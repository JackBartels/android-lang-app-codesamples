package com.jackbartels.langvillage;

import android.content.Context;

import java.util.Arrays;

public class ButtonActionHandler {

    private static final int MENU_NUM_COL = 2;
    private static final int MENU_NUM_ROW = 3;
    private static final int SETTINGS_NUM_ROW = 3;

    public static void aPressed(GameState gameState, Context context) {
        switch(GameState.mode) {
            case MENU:
                if(Arrays.equals(GameState.menuCoords, new int[]{0, 1})) {
                    GameState.mode = GameState.Mode.MAP;
                } else if(Arrays.equals(GameState.menuCoords, new int[]{1, 1})) {
                    GameState.mode = GameState.Mode.SETTINGS;
                } else if(Arrays.equals(GameState.menuCoords, new int[]{2, 0})) {
                    GameState.mode = GameState.Mode.SAVE_CONFIRM;
                } else if(Arrays.equals(GameState.menuCoords, new int[]{2, 1})) {
                    GameState.mode = GameState.Mode.EXIT_CONFIRM;
                }
                break;
            case SAVE_CONFIRM:
                if(GameState.saveConfirm == 1) {
                    SaveUtil.save(gameState, context);
                }

                GameState.mode = GameState.Mode.MENU;

                break;
            case EXIT_CONFIRM:
                if(GameState.exitConfirm == 1) {
                    MainActivity.quit = true;
                } else {
                    GameState.mode = GameState.Mode.MENU;
                }
        }
    }

    public static void bPressed() {
        switch(GameState.mode) {
            case MENU:
                GameState.menuCoords[0] = 0;
                GameState.menuCoords[1] = 0;

                GameState.mode = GameState.Mode.GAME;
                break;
            case MAP:
                GameState.mode = GameState.Mode.MENU;
                break;
            case SETTINGS:
                GameState.settingsRow = 0;

                GameState.mode = GameState.Mode.MENU;
                break;
            case SAVE_CONFIRM:
                GameState.saveConfirm = 0;

                GameState.mode = GameState.Mode.MENU;
                break;
            case EXIT_CONFIRM:
                GameState.exitConfirm = 0;

                GameState.mode = GameState.Mode.MENU;
        }
    }

    public static void upPressed(Player player) {
        switch(GameState.mode) {
            case GAME:
                player.setPlayerFacing(Player.PlayerFacing.UP);

                int playerX = player.getPlayerX();
                int playerY = player.getPlayerY();

                if (!Map.isCollision(playerX - 1, playerY)) {
                    player.setPlayerX(--playerX);
                }
                break;
            case MENU:
                if(GameState.menuCoords[0] > 0) {
                    GameState.menuCoords[0]--;
                }
                break;
            case SETTINGS:
                if(GameState.settingsRow > 0) {
                    GameState.settingsRow--;
                }
        }
    }

    public static void rightPressed(Player player, GameView gameView) {
        switch(GameState.mode) {
            case GAME:
                player.setPlayerFacing(Player.PlayerFacing.RIGHT);

                int playerX = player.getPlayerX();
                int playerY = player.getPlayerY();

                if (!Map.isCollision(playerX, playerY + 1)) {
                    player.setPlayerY(++playerY);
                }
                break;
            case MENU:
                if(GameState.menuCoords[1] < MENU_NUM_COL - 1) {
                    GameState.menuCoords[1]++;
                }
                break;
            case SETTINGS:
                if(GameState.settingsRow == 0 && GameState.amoledMode == 0) {
                    GameState.amoledMode = 1;
                } else if(GameState.settingsRow == 1 && GameState.fpsMode < 2) {
                    GameState.fpsMode++;
                    gameView.restartMainThread();
                } else if(GameState.settingsRow == 2 && GameState.displayFps == 1) {
                    GameState.displayFps = 0;
                }
                break;
            case SAVE_CONFIRM:
                if(GameState.saveConfirm == 1) {
                    GameState.saveConfirm = 0;
                }
                break;
            case EXIT_CONFIRM:
                if(GameState.exitConfirm == 1) {
                    GameState.exitConfirm = 0;
                }
        }
    }

    public static void downPressed(Player player) {
        switch(GameState.mode) {
            case GAME:
                player.setPlayerFacing(Player.PlayerFacing.DOWN);

                int playerX = player.getPlayerX();
                int playerY = player.getPlayerY();

                if (!Map.isCollision(playerX + 1, playerY)) {
                    player.setPlayerX(++playerX);
                }
                break;
            case MENU:
                if(GameState.menuCoords[0] < MENU_NUM_ROW - 1) {
                    GameState.menuCoords[0]++;
                }
                break;
            case SETTINGS:
                if(GameState.settingsRow < SETTINGS_NUM_ROW - 1) {
                    GameState.settingsRow++;
                }
        }
    }

    public static void leftPressed(Player player, GameView gameView) {
        switch(GameState.mode) {
            case GAME:
                player.setPlayerFacing(Player.PlayerFacing.LEFT);

                int playerX = player.getPlayerX();
                int playerY = player.getPlayerY();

                if (!Map.isCollision(playerX, playerY - 1)) {
                    player.setPlayerY(--playerY);
                }
                break;
            case MENU:
                if(GameState.menuCoords[1] > 0) {
                    GameState.menuCoords[1]--;
                }
                break;
            case SETTINGS:
                if(GameState.settingsRow == 0 && GameState.amoledMode == 1) {
                    GameState.amoledMode = 0;
                } else if(GameState.settingsRow == 1 && GameState.fpsMode > 0) {
                    GameState.fpsMode--;
                    gameView.restartMainThread();
                } else if(GameState.settingsRow == 2 && GameState.displayFps == 0) {
                    GameState.displayFps = 1;
                }
                break;
            case SAVE_CONFIRM:
                if(GameState.saveConfirm == 0) {
                    GameState.saveConfirm = 1;
                }
                break;
            case EXIT_CONFIRM:
                if(GameState.exitConfirm == 0) {
                    GameState.exitConfirm = 1;
                }
        }
    }

    public static void startPressed() {
        switch(GameState.mode) {
            case GAME:
                GameState.mode = GameState.Mode.MENU;
                break;
            case MENU:
                GameState.menuCoords[0] = 0;
                GameState.menuCoords[1] = 0;

                GameState.mode = GameState.Mode.GAME;
        }
    }

    public static void selectPressed() { /* No functionality yet */ }
}
