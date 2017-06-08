package com.example.currentplacedetailsonmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.jobs.ConstructionJob;
import com.example.currentplacedetailsonmap.jobs.Job;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by Dante on 6/2/2017.
 */
class StarlightSurfaceView extends SurfaceView {

    private MapsActivityCurrentPlace context;

    private Canvas canvas;

    private Settlement activeSettlement;
    private Tile hoverTile;

    private SurfaceHolder surfaceHolder;

    private float centerX, centerY;
    private float width;

    public StarlightSurfaceView(Context context, AttributeSet attr) {
        super(context);
        this.context = ((MapsActivityCurrentPlace) context);
        width = 7;
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                /*canvas = holder.lockCanvas(null);
                drawSettlement(canvas);
                holder.unlockCanvasAndPost(canvas);*/
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

    public void setSettlement(Settlement settlement) {
        activeSettlement = settlement;
        centerX = activeSettlement.rows / 2.0f;
        centerY = activeSettlement.cols / 2.0f;
    }

    public void drawSettlement() {
        canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            drawSettlement(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    protected void drawSettlement(Canvas canvas) {
        if (activeSettlement == null) {
            return;
        }

        Paint whitePaint = new Paint();
        Paint bluePaint = new Paint();

        whitePaint.setColor(Color.WHITE);
        bluePaint.setColor(Color.BLUE);

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
                Tile tile = activeSettlement.getTile(r,c);
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

                    Person person = tile.people.get(0);

                    if (person.queueTasks.size() > 0) {
                        Task firstTask = person.queueTasks.get(0);
                        float percentageCompleted = 1.0f - (float) firstTask.ticksLeft / (float) firstTask.originalTicksLeft;
                        canvas.drawRect(
                                (int) ((displayR + 0) * renderWidth),
                                (int) ((displayC + 0.8) * renderHeight),
                                (int) ((displayR + 0.5) * renderWidth),
                                (int) ((displayC + 1) * renderHeight),
                                whitePaint
                        );
                        canvas.drawRect(
                                (int) ((displayR + 0) * renderWidth),
                                (int) ((displayC + 0.8) * renderHeight),
                                (int) ((displayR + percentageCompleted*0.5) * renderWidth),
                                (int) ((displayC + 1) * renderHeight),
                                bluePaint
                        );
                    }
                }
            }
        }

        //Render construction jobs by seeing if they're within bounds
        for (Job job: activeSettlement.availableJobsBySkill.get("Construction")) {
            if (job instanceof ConstructionJob) {
                ConstructionJob consJob = (ConstructionJob) job;
                if (consJob.tile.row >= startX && consJob.tile.row <= endX && consJob.tile.col >= startY && consJob.tile.col <= endY) {
                    int displayR = consJob.tile.row - startX;
                    int displayC = consJob.tile.col - startY;

                    Bitmap bmpIcon = BitmapManager.getBitmapFromName("building_in_progress_icon", context, R.drawable.building_in_progress_icon);

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

        if (hoverTile != null) {
            if (hoverTile.row >= startX && hoverTile.row <= endX && hoverTile.col >= startY && hoverTile.col <= endY) {
                int displayR = hoverTile.row - startX;
                int displayC = hoverTile.col - startY;

                Bitmap bmpIcon = BitmapManager.getBitmapFromName("tile_target", context, R.drawable.tile_target);

                canvas.drawBitmap(bmpIcon, null, new Rect(
                                (int) (displayR * renderWidth),
                                (int) (displayC * renderHeight),
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
        if (activeSettlement.inBounds(x,y)) {
            tile = activeSettlement.getTile(x,y);
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
                drawSettlement();
                showTileDetails(tile);
                hoverTile = tile;
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

        ((TextView) context.findViewById(R.id.buildingTileResource)).setVisibility(GONE);

        ((TextView) context.findViewById(R.id.buildingName)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(GONE);
        ((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(GONE);
        //((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);

        ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);
        ((Button) context.findViewById(R.id.btnProductionList)).setVisibility(GONE);
        ((LinearLayout) context.findViewById(R.id.constructionBuildingList)).removeAllViews();
        ((LinearLayout) context.findViewById(R.id.productionRecipesList)).removeAllViews();

        Building building = tile.getBuilding();
        if (building != null) {
            ((TextView) context.findViewById(R.id.buildingName)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(VISIBLE);
            ((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(VISIBLE);
            ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);

            ((TextView) context.findViewById(R.id.buildingName)).setText(building.name);
            ((TextView) context.findViewById(R.id.buildingDesc)).setText(building.desc);
            //if (!building.getProductionRecipeString().equals("Nothing")) {

            ((TextView) context.findViewById(R.id.buildingProduction)).setText("Produces: " + building.getProductionRecipeString());
            //}
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setText("Houses " + building.housingNum + " people");
            ((TextView) context.findViewById(R.id.buildingItemsList)).setText("Stored: " + building.getItemsString());

            ((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(null);
            /*((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tile.getBuilding().produce();
                    showTileDetails(tile);
                }
            });*/

            if (tile.resources.size() > 0) {
                ((TextView) context.findViewById(R.id.buildingTileResource)).setVisibility(VISIBLE);
                ((TextView) context.findViewById(R.id.buildingTileResource)).setText("Resources: " + tile.resources.getItems());
            }

            ((Button) context.findViewById(R.id.btnProductionList)).setVisibility(VISIBLE);
        }
        else {
            ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(VISIBLE);
        }
    }

    public Tile getHoverTile() {
        return hoverTile;
    }

    public Settlement getActiveSettlement() {
        return activeSettlement;
    }

}
