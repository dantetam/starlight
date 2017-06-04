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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.logging.Handler;

/**
 * Created by Dante on 6/2/2017.
 */
class StarlightSurfaceView extends SurfaceView {

    private SettlementBuildingsActivity context;

    private Canvas canvas;

    private Settlement settlement;
    private Tile hoverTile;

    private SurfaceHolder surfaceHolder;

    private float centerX, centerY;
    private float width;

    public StarlightSurfaceView(Context context, AttributeSet attr) {
        super(context);
        this.context = ((SettlementBuildingsActivity) context);
        this.settlement = ((SettlementBuildingsActivity) context).settlement;
        centerX = settlement.rows / 2.0f;
        centerY = settlement.cols / 2.0f;
        width = 3;
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                canvas = holder.lockCanvas(null);
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

    public void drawSettlement() {
        if (canvas != null)
            drawSettlement(canvas);
    }
    protected void drawSettlement(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        float widthX = width;
        float aspectRatio = (float) getHeight() / (float) getWidth();
        float widthY = aspectRatio * widthX;

        int startX = (int) Math.floor(centerX - widthX);
        int startY = (int) Math.floor(centerY - widthY);
        int endX = (int) Math.ceil(centerX + widthX);
        int endY = (int) Math.ceil(centerY + widthY);

        /*startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(endX, settlement.rows);
        endY = Math.min(endY, settlement.cols);*/

        float renderWidth = getWidth() / (endX - startX + 1.0f);
        float renderHeight = getHeight() / (endY - startY + 1.0f);

        for (int r = startX; r <= endX; r++) {
            for (int c = startY; c <= endY; c++) {
                Tile tile = settlement.getTile(r,c);
                if (tile == null) {
                    continue;
                }

                int displayR = r - startX;
                int displayC = c - startY;

                Bitmap bmpIcon;

                if ((r + c) % 2 == 0) {
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

                Building building = tile.getBuilding();
                if (building != null) {
                    bmpIcon = BitmapManager.getBitmapFromName("building_icon", context, R.drawable.building_icon);
                    canvas.drawBitmap(bmpIcon, null, new Rect(
                                    (int) ((displayR + 0.25) * renderWidth),
                                    (int) ((displayC + 0.25) * renderHeight),
                                    (int) ((displayR + 0.75) * renderWidth),
                                    (int) ((displayC + 0.75) * renderHeight)
                            ),
                            null
                    );
                }

                if (tile.people.size() > 0) {
                    bmpIcon = BitmapManager.getBitmapFromName("person", context, R.drawable.person);
                    canvas.drawBitmap(bmpIcon, null, new Rect(
                                    (int) ((displayR + 0) * renderWidth),
                                    (int) ((displayC + 0.5) * renderHeight),
                                    (int) ((displayR + 0.5) * renderWidth),
                                    (int) ((displayC + 1) * renderHeight)
                            ),
                            null
                    );
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touched;
        float touchedX = event.getX();
        float touchedY = event.getY();

        float widthX = width;
        float aspectRatio = (float) getHeight() / (float) getWidth();
        float widthY = aspectRatio * widthX;

        int startX = (int) Math.floor(centerX - widthX);
        int startY = (int) Math.floor(centerY - widthY);
        int endX = (int) Math.ceil(centerX + widthX);
        int endY = (int) Math.ceil(centerY + widthY);

        float screenWidthX = getWidth() / (endX - startX + 1);
        float screenWidthY = getHeight() / (endY - startY + 1);

        int x = (int) Math.floor(touchedX / screenWidthX + startX);
        int y = (int) Math.floor(touchedY / screenWidthY + startY);

        Tile tile = null;
        if (settlement.inBounds(x,y)) {
            tile = settlement.getTile(x,y);
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                hoverTile = tile;
                touched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touched = true;
                break;
            case MotionEvent.ACTION_UP:
                showTileDetails(tile);
                hoverTile = null;
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

    private void showTileDetails(final Tile tile) {
        ((TextView) context.findViewById(R.id.buildingLocation)).setVisibility(VISIBLE);
        ((TextView) context.findViewById(R.id.buildingLocation)).setText(tile.row + " " + tile.col);

        ((TextView) context.findViewById(R.id.buildingName)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(GONE);
        ((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(GONE);

        Building building = tile.getBuilding();
        if (building != null) {
            ((TextView) context.findViewById(R.id.buildingName)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(VISIBLE);
            ((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(VISIBLE);

            ((TextView) context.findViewById(R.id.buildingName)).setText(building.name);
            ((TextView) context.findViewById(R.id.buildingDesc)).setText(building.desc);
            //if (!building.getProductionRecipeString().equals("Nothing")) {

            ((TextView) context.findViewById(R.id.buildingProduction)).setText("Produces: " + building.getProductionRecipeString());
            //}
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setText("Houses " + building.housingNum + " people");
            ((TextView) context.findViewById(R.id.buildingItemsList)).setText("Stored: " + building.getItemsString());

            ((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(null);
            ((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tile.getBuilding().produce();
                    showTileDetails(tile);
                }
            });
        }
    }

}
