package com.example.currentplacedetailsonmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;

/**
 * Created by Dante on 6/2/2017.
 */
class MySurfaceView extends SurfaceView {

    private Context context;

    private Settlement settlement;

    private SurfaceHolder surfaceHolder;

    private float centerX, centerY;
    private float widthX, widthY;

    public MySurfaceView(Context context, AttributeSet attr) {
        super(context);
        this.context = context;
        this.settlement = ((SettlementBuildingsActivity) context).settlement;
        centerX = settlement.rows / 2.0f;
        centerY = settlement.cols / 2.0f;
        widthX = 5; widthY = 5;
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas(null);
                drawSettlement(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder,
                                       int format, int width, int height) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }
        });
    }

    protected void drawSettlement(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        int startX = (int) Math.floor(centerX - widthX);
        int startY = (int) Math.floor(centerY - widthY);
        int endX = (int) Math.ceil(centerX + widthX);
        int endY = (int) Math.ceil(centerY + widthY);

        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(endX, settlement.rows);
        endY = Math.min(endY, settlement.cols);

        float renderWidth = getWidth() / (endX - startX + 1.0f);
        float renderHeight = getHeight() / (endY - startY + 1.0f);

        for (int r = startX; r <= endX; r++) {
            for (int c = startY; c <= endY; c++) {
                int displayR = r - startX;
                int displayC = c - startY;

                Bitmap bmpIcon = null;

                if (r % 2 == 0 && c % 2 == 0) {
                    bmpIcon = BitmapManager.getBitmapFromName("forest_texture", context, R.drawable.forest_texture);
                }
                else {
                    bmpIcon = BitmapManager.getBitmapFromName("desert_texture", context, R.drawable.desert_texture);
                }

                canvas.drawBitmap(bmpIcon, null, new Rect(
                        (int) (displayR*renderWidth),
                        (int) (displayC*renderHeight),
                        (int) ((displayR + 1) * renderWidth),
                        (int) ((displayC + 1) * renderHeight)
                        ),
                        null
                );
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touched;
        float touchedX = event.getX();
        float touchedY = event.getY();

        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                touched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touched = true;
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                touched = false;
                break;
            default:
                touched = false;
                break;
        }
        return true; //processed
    }

}
