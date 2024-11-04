package com.jackbartels.langvillage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread thread;

    private final BoundsUtil boundsUtil;
    private final Context context;
    private final DBHandler dbHandler;
    private final DrawablePreloader drawable;
    private final DrawUtil drawUtil;
    private final GameState gameState;

    private int viewHeight;
    private int viewWidth;

    public GameView(Context context) {
        super(context);
        this.context = context;

        boundsUtil = new BoundsUtil();
        dbHandler = new DBHandler(context);
        drawable = new DrawablePreloader(context); // Preload drawables
        drawUtil = new DrawUtil(context);

        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);

        viewHeight = 0;
        viewWidth = 0;
        setFocusable(true);

        gameState = new GameState();
        LoadUtil.loadSave(gameState, context);
        LoadUtil.loadMap(Map.currentMapName, context);

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        LoadUtil.loadDbTable(db, context,"part_of_speech");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        viewWidth = this.getMeasuredWidth();
        viewHeight = this.getMeasuredHeight();

        // Recalculate bounds relative to new viewport dimensions
        boundsUtil.updateBounds(viewWidth, viewHeight, drawable);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;

        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch(Exception e) {
                e.printStackTrace(); // TODO: Add proper logging
            }
            retry = false;
        }
    }

    public void restartMainThread() {
        boolean retry = true;

        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch(Exception e) {
                e.printStackTrace(); // TODO: Add proper logging
            }
            retry = false;
        }

        thread = new MainThread(getHolder(), this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        double touchX = touchEvent.getX();
        double touchY = touchEvent.getY();

        int action = touchEvent.getAction();

        // TODO: Implement press and hold (probably alongside animations)
        if(action == MotionEvent.ACTION_DOWN) {
            ButtonCollisionHandler.handleButtonCollision(boundsUtil, touchX, touchY,
                                                         gameState, context, this);
        } else if (action == MotionEvent.ACTION_UP) {
            // Presently no action on button release
        }

        performClick();
        return super.onTouchEvent(touchEvent);
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }

    public void render(Canvas canvas) {
        drawUtil.drawUI(canvas, drawable, viewWidth, viewHeight);

        if(GameState.mode == GameState.Mode.GAME) {
            drawUtil.drawMap(canvas, drawable, gameState.getPlayer(), boundsUtil, false);
        }

        if(GameState.mode == GameState.Mode.MENU) {
            drawUtil.drawMap(canvas, drawable, gameState.getPlayer(), boundsUtil, false);
            drawUtil.drawMenu(canvas, drawable, boundsUtil);
        }

        if(GameState.mode == GameState.Mode.MAP) {
            drawUtil.drawOverheadMap(canvas, drawable, gameState.getPlayer(), boundsUtil);
        }

        if(GameState.mode == GameState.Mode.SETTINGS) {
            drawUtil.drawMap(canvas, drawable, gameState.getPlayer(), boundsUtil, false);
            drawUtil.drawSettings(canvas, drawable, boundsUtil);
        }

        if(GameState.mode == GameState.Mode.SAVE_CONFIRM) {
            drawUtil.drawMap(canvas, drawable, gameState.getPlayer(), boundsUtil, false);
            drawUtil.drawConfirmation(canvas, drawable, boundsUtil, false);
        }

        if(GameState.mode == GameState.Mode.EXIT_CONFIRM) {
            drawUtil.drawMap(canvas, drawable, gameState.getPlayer(), boundsUtil, false);
            drawUtil.drawConfirmation(canvas, drawable, boundsUtil, true);
        }
    }
}
