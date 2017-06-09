package io.github.dantetam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.R;

import io.github.dantetam.android.BitmapHelper;
import io.github.dantetam.jobs.ConstructionJob;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.CombatMeleeTask;
import io.github.dantetam.tasks.CombatRangedTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.xml.BitmapManager;

/**
 * Created by Dante on 6/2/2017.
 */
class StarlightSurfaceView extends SurfaceView {

    private MapsActivityCurrentPlace context;
    private VelocityTracker mVelocityTracker = null;

    private Canvas canvas;

    private Settlement activeSettlement;
    private Tile hoverTile;

    private SurfaceHolder surfaceHolder;

    private float centerX, centerY;
    private float width;

    private float aspectRatio = 0f;

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
        Paint blackTextPaint = new Paint();
        Paint redPaint = new Paint();

        whitePaint.setColor(Color.WHITE);
        bluePaint.setColor(Color.BLUE);
        blackTextPaint.setColor(Color.BLACK);
        redPaint.setColor(Color.RED);

        canvas.drawColor(Color.BLACK);

        float widthX = width;
        if (aspectRatio == 0) {
            aspectRatio = (float) getHeight() / (float) getWidth();
        }
        float widthY = aspectRatio * widthX;

        int startX = (int) Math.floor(centerX - widthX);
        int startY = (int) Math.floor(centerY - widthY);
        int endX = (int) Math.ceil(centerX + widthX);
        int endY = (int) Math.ceil(centerY + widthY);

        /*startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(endX, settlement.rows);
        endY = Math.min(endY, settlement.cols);*/

        float renderWidth = getWidth() / (2 * widthX + 1.0f);
        float renderHeight = getHeight() / (2 * widthY + 1.0f);

        setTextSizeForWidth(blackTextPaint, renderWidth, "Name");

        /*Bitmap seaTile = BitmapManager.getBitmapFromName("shallow_sea_texture", context, R.drawable.shallow_sea_texture);
        Bitmap iceTile = BitmapManager.getBitmapFromName("ice_texture", context, R.drawable.ice_texture);
        Bitmap taigaTile = BitmapManager.getBitmapFromName("taiga_texture", context, R.drawable.taiga_texture);
        Bitmap desertTile = BitmapManager.getBitmapFromName("desert_texture", context, R.drawable.desert_texture);
        Bitmap steppeTile = BitmapManager.getBitmapFromName("steppe_texture", context, R.drawable.steppe_texture);
        Bitmap dryForestTile = BitmapManager.getBitmapFromName("dryforest_texture", context, R.drawable.dryforest_texture);
        Bitmap forestTile = BitmapManager.getBitmapFromName("forest_texture", context, R.drawable.forest_texture);
        Bitmap rainforestTile = BitmapManager.getBitmapFromName("rainforest_texture", context, R.drawable.rainforest_texture);
        Bitmap[] bitmapTiles = new Bitmap[]{seaTile, iceTile, taigaTile, desertTile, steppeTile, dryForestTile, forestTile, rainforestTile};
        Bitmap androidTile = BitmapManager.getBitmapFromName("coal", context, R.drawable.coal);
        */

        Bitmap seaTile = BitmapHelper.findBitmapOrBuild(R.drawable.shallow_sea_texture);
        Bitmap iceTile = BitmapHelper.findBitmapOrBuild(R.drawable.ice_texture);
        Bitmap taigaTile = BitmapHelper.findBitmapOrBuild(R.drawable.taiga_texture);
        Bitmap desertTile = BitmapHelper.findBitmapOrBuild(R.drawable.desert_texture);
        Bitmap steppeTile = BitmapHelper.findBitmapOrBuild(R.drawable.steppe_texture);
        Bitmap dryForestTile = BitmapHelper.findBitmapOrBuild(R.drawable.dryforest_texture);
        Bitmap forestTile = BitmapHelper.findBitmapOrBuild(R.drawable.forest_texture);
        Bitmap rainforestTile = BitmapHelper.findBitmapOrBuild(R.drawable.rainforest_texture);
        Bitmap[] bitmapTiles = new Bitmap[]{seaTile, iceTile, taigaTile, desertTile, steppeTile, dryForestTile, forestTile, rainforestTile};
        Bitmap androidTile = BitmapHelper.findBitmapOrBuild(R.drawable.coal);

        Bitmap combatIcon = BitmapHelper.findBitmapOrBuild(R.drawable.shock);

        for (int r = startX; r <= endX; r++) {
            for (int c = startY; c <= endY; c++) {
                Tile tile = activeSettlement.getTile(r,c);
                if (tile == null) {
                    continue;
                }

                int displayR = tile.row - startX;
                int displayC = tile.col - startY;

                /*displayR -= centerX % 1.0f;
                displayC -= centerY % 1.0f;*/

                Bitmap bmpIcon;
                if (tile.tileType >= 0 && tile.tileType < 8) {
                    bmpIcon = bitmapTiles[tile.tileType];
                }
                else {
                    bmpIcon = androidTile;
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
                                    (int) ((displayR + 0.25) * renderWidth),
                                    (int) ((displayC + 0.25) * renderHeight),
                                    (int) ((displayR + 0.75) * renderWidth),
                                    (int) ((displayC + 0.75) * renderHeight)
                            ),
                            null
                    );
                }
            }
        }

        for (int r = startX; r <= endX; r++) {
            for (int c = startY; c <= endY; c++) {
                Tile tile = activeSettlement.getTile(r,c);
                if (tile == null) {
                    continue;
                }

                int displayR = tile.row - startX;
                int displayC = tile.col - startY;

                /*displayR -= centerX % 1.0f;
                displayC -= centerY % 1.0f;*/

                Bitmap bmpIcon = BitmapHelper.findBitmapOrBuild(R.drawable.person);

                if (tile.people.size() > 0) {
                    Person person = tile.people.get(0);

                    if (person.faction.name.equals("Pirates")) {
                        bmpIcon = BitmapHelper.findBitmapOrBuild(R.drawable.pirate);
                    }

                    float healthPercent = person.body.root.getHealth() / person.body.root.maxHealth;

                    if (person.queueTasks.size() > 0) {
                        Task firstTask = person.queueTasks.get(0);
                        float percentageCompleted = 1.0f - (float) firstTask.ticksLeft / (float) firstTask.originalTicksLeft;
                        if (!(firstTask instanceof MoveTask)) {
                            //Draw the person standing still,
                            canvas.drawBitmap(bmpIcon, null, new Rect(
                                            (int) ((displayR + 0) * renderWidth),
                                            (int) ((displayC + 0.5) * renderHeight),
                                            (int) ((displayR + 0.5) * renderWidth),
                                            (int) ((displayC + 1) * renderHeight)
                                    ),
                                    null
                            );
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
                                    (int) ((displayR + percentageCompleted * 0.5) * renderWidth),
                                    (int) ((displayC + 1) * renderHeight),
                                    bluePaint
                            );
                            canvas.drawText(
                                    person.name,
                                    (int) ((displayR + 0) * renderWidth),
                                    (int) ((displayC + 1.1f) * renderHeight),
                                    blackTextPaint
                            );
                            if (firstTask instanceof CombatMeleeTask || firstTask instanceof CombatRangedTask) {
                                canvas.drawBitmap(combatIcon, null, new Rect(
                                                (int) ((displayR + 0.25) * renderWidth),
                                                (int) ((displayC + 0.25) * renderHeight),
                                                (int) ((displayR + 0.75) * renderWidth),
                                                (int) ((displayC + 0.75) * renderHeight)
                                        ),
                                        null
                                );
                            }
                        }
                        else {
                            Tile current = person.tile;
                            Tile dest = ((MoveTask) firstTask).dest;
                            //TODO: Tween the person sprite by the proportion percentageCompleted between these two tiles
                            float trueR = (1 - percentageCompleted) * current.row + percentageCompleted * dest.row;
                            float trueC = (1 - percentageCompleted) * current.col + percentageCompleted * dest.col;
                            float displayTweenR = trueR - startX;
                            float displayTweenC = trueC - startY;
                            canvas.drawBitmap(bmpIcon, null, new Rect(
                                            (int) ((displayTweenR + 0) * renderWidth),
                                            (int) ((displayTweenC + 0.5) * renderHeight),
                                            (int) ((displayTweenR + 0.5) * renderWidth),
                                            (int) ((displayTweenC + 1) * renderHeight)
                                    ),
                                    null
                            );
                            //Draw the person's name near the bottom of the tile
                            canvas.drawText(
                                    person.name,
                                    (int) ((displayTweenR + 0) * renderWidth),
                                    (int) ((displayTweenC + 1.1f) * renderHeight),
                                    blackTextPaint
                            );
                            //System.err.println(current.toString() + ";" + dest.toString() + ";" + trueR + "," + trueC);
                        }
                    }
                    else {
                        canvas.drawBitmap(bmpIcon, null, new Rect(
                                        (int) ((displayR + 0) * renderWidth),
                                        (int) ((displayC + 0.5) * renderHeight),
                                        (int) ((displayR + 0.5) * renderWidth),
                                        (int) ((displayC + 1) * renderHeight)
                                ),
                                null
                        );
                        //Draw the person's name near the bottom of the tile
                        canvas.drawText(
                                person.name,
                                (int) ((displayR + 0) * renderWidth),
                                (int) ((displayC + 1.1f) * renderHeight),
                                blackTextPaint
                        );
                    }

                    if (healthPercent < 1) {
                        canvas.drawRect(
                                (int) ((displayR + 0) * renderWidth),
                                (int) ((displayC + 1) * renderHeight),
                                (int) ((displayR + 0.5) * renderWidth),
                                (int) ((displayC + 1.2) * renderHeight),
                                whitePaint
                        );
                        canvas.drawRect(
                                (int) ((displayR + 0) * renderWidth),
                                (int) ((displayC + 1) * renderHeight),
                                (int) ((displayR + healthPercent * 0.5) * renderWidth),
                                (int) ((displayC + 1.2) * renderHeight),
                                redPaint
                        );
                    }
                }
            }
        }

        if (hoverTile != null) {
            if (hoverTile.row >= startX && hoverTile.row <= endX && hoverTile.col >= startY && hoverTile.col <= endY) {
                int displayR = hoverTile.row - startX;
                int displayC = hoverTile.col - startY;

                /*displayR -= centerX % 1.0f;
                displayC -= centerY % 1.0f;*/

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

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     *            the Paint to set the text size for
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    private static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touched;
        float touchedX = event.getX();
        float touchedY = event.getY();

        float widthX = width;
        if (aspectRatio == 0) {
            aspectRatio = (float) getHeight() / (float) getWidth();
        }
        float widthY = aspectRatio * widthX;

        int startX = (int) Math.floor(centerX - widthX);
        int startY = (int) Math.floor(centerY - widthY);
        int endX = (int) Math.ceil(centerX + widthX);
        int endY = (int) Math.ceil(centerY + widthY);

        float screenWidthX = getWidth() / (2 * widthX + 1);
        float screenWidthY = getHeight() / (2 * widthY + 1);

        int x = (int) Math.floor(touchedX / screenWidthX + startX);
        int y = (int) Math.floor(touchedY / screenWidthY + startY);

        Tile tile = null;
        if (activeSettlement.inBounds(x,y)) {
            tile = activeSettlement.getTile(x,y);
        }

        //int action = event.getAction();
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);

                hoverTile = tile;
                touched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.

                float dx = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                float dy = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                centerX -= dx / 600.0f;
                centerY -= dy / 600.0f;

                centerX = Math.min(Math.max(0, centerX), activeSettlement.rows - 1);
                centerY = Math.min(Math.max(0, centerY), activeSettlement.cols - 1);

                drawSettlement();
                /*if (tile != null)
                    showTileDetails(tile);*/

                invalidate();

                //hoverTile = tile;
                touched = true;
                break;
            case MotionEvent.ACTION_UP:
                drawSettlement();
                if (tile != null)
                    showTileDetails(tile);
                hoverTile = tile;
                touched = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();

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
