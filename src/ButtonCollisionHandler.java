package com.jackbartels.langvillage;

import android.content.Context;

public class ButtonCollisionHandler {

    public static void handleButtonCollision(
            BoundsUtil boundsUtil,double touchX, double touchY,
            GameState gameState, Context context, GameView gameView) {
        int[] aBounds = boundsUtil.getABounds();
        int[] bBounds = boundsUtil.getBBounds();
        int[] dUpBounds = boundsUtil.getDUpBounds();
        int[] dRightBounds = boundsUtil.getDRightBounds();
        int[] dDownBounds = boundsUtil.getDDownBounds();
        int[] dLeftBounds = boundsUtil.getDLeftBounds();
        int[] seBounds = boundsUtil.getSelectBounds();
        int[] stBounds = boundsUtil.getStartBounds();

        Player player = gameState.getPlayer();

        // A Pressed
        if(touchX >= aBounds[0] && touchX <= aBounds[2] &&
           touchY >= aBounds[1] && touchY <= aBounds[3]) {
            ButtonActionHandler.aPressed(gameState, context);
        }

        // B Pressed
        if(touchX >= bBounds[0] && touchX <= bBounds[2] &&
           touchY >= bBounds[1] && touchY <= bBounds[3]) {
            ButtonActionHandler.bPressed();
        }

        // D-pad Up Pressed
        if(touchX >= dUpBounds[0] && touchX <= dUpBounds[2] &&
           touchY >= dUpBounds[1] && touchY <= dUpBounds[3]) {
            ButtonActionHandler.upPressed(player);
        }

        // D-pad Right Pressed
        if(touchX >= dRightBounds[0] && touchX <= dRightBounds[2] &&
           touchY >= dRightBounds[1] && touchY <= dRightBounds[3]) {
            ButtonActionHandler.rightPressed(player, gameView);
        }

        // D-pad Down Pressed
        if(touchX >= dDownBounds[0] && touchX <= dDownBounds[2] &&
           touchY >= dDownBounds[1] && touchY <= dDownBounds[3]) {
            ButtonActionHandler.downPressed(player);
        }

        // D-pad Left Pressed
        if(touchX >= dLeftBounds[0] && touchX <= dLeftBounds[2] &&
           touchY >= dLeftBounds[1] && touchY <= dLeftBounds[3]) {
            ButtonActionHandler.leftPressed(player, gameView);
        }

        // Select Pressed
        if(touchX >= seBounds[0] && touchX <= seBounds[2] &&
           touchY >= seBounds[1] && touchY <= seBounds[3]) {
            ButtonActionHandler.selectPressed();
        }

        // Start Pressed
        if(touchX >= stBounds[0] && touchX <= stBounds[2] &&
           touchY >= stBounds[1] && touchY <= stBounds[3]) {
            ButtonActionHandler.startPressed();
        }
    }
}
