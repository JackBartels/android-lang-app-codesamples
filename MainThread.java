package com.jackbartels.langvillage;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainThread extends Thread {

    private long oldTime;
    private int fpsUpdateCounter;
    private int frameDelay; // In nanoseconds
    public static int currentFps = 0;

    private final SurfaceHolder sHolder;
    private final GameView gView;
    private Canvas canvas;

    private boolean running;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();

        canvas = null;
        sHolder = surfaceHolder;
        gView = gameView;

        fpsUpdateCounter = 0;
        oldTime = System.nanoTime();
        running = true;

        setFrameDelay();
    }

    public void setRunning(boolean running) { this.running = running; }

    private void setFrameDelay() {
        switch (GameState.fpsMode) {
            case 0: // 15 FPS
                frameDelay = 66666666;
                break;
            case 1: // 30 FPS
                frameDelay = 33333333;
                break;
            case 2: // 60 FPS
                frameDelay = 16666666;
        }
    }

    private void calculateFps() {
        long newTime = System.nanoTime();
        double delta = (newTime - oldTime) / 30.0;
        double fps = 1 / (delta / 1000000000);

        oldTime = newTime;

        currentFps = (int) fps;
    }

    /*
        scheduleAtFixedRate() keeps a much more stable--and accurate to user selection--FPS.
        Attempted to use scheduleWithFixedDelay(), but was unable to adjust the frame time
        for the render time consistently.

        In short, the current implementation allows for control of the length of the entire
        frame whereas the recommended implementation only allows for control of the time between
        frames (the latter requiring significant workaround for stable FPS).

        Unstable FPS is a noticeable graphical degradation from the user perspective; thus, the
        risk of this implementation is acceptable and mainly relates to unexpected behavior when
        the process is cached and later revived by the OS.
     */
    @SuppressLint("DiscouragedApi")
    @Override
    public void run() {
        executor.scheduleAtFixedRate(() -> {
            canvas = null;

            if(running) {
                try {
                    canvas = sHolder.lockHardwareCanvas();
                    synchronized(sHolder) {
                        gView.render(canvas);
                    }
                } catch(Exception e) {
                    e.printStackTrace(); // TODO: Add proper logging
                } finally {
                    if(canvas != null) {
                        try {
                            sHolder.unlockCanvasAndPost(canvas);
                        } catch(Exception e) {
                            e.printStackTrace(); // TODO: Add proper logging
                        }
                    }
                }
            } else {
                try {
                    executor.shutdown();
                } catch(Exception e) {
                    e.printStackTrace(); // TODO: Add proper logging
                }
            }

            if(++fpsUpdateCounter >= 30) {
                fpsUpdateCounter = 0;

                calculateFps();
            }
        }, 0, frameDelay, TimeUnit.NANOSECONDS);
    }
}
