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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import io.github.dantetam.R;

import io.github.dantetam.android.BitmapHelper;
import io.github.dantetam.jobs.ConstructionJob;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.CombatMeleeTask;
import io.github.dantetam.tasks.CombatRangedTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.CustomGameTime;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.world.World;
import io.github.dantetam.xml.BitmapManager;

/**
 * Created by Dante on 6/2/2017.
 */
class StarlightSurfaceView extends SurfaceView {

    private MapsActivityCurrentPlace context;
    private VelocityTracker mVelocityTracker = null;

    private Canvas canvas;

    private World world;
    private Settlement activeSettlement;
    private Tile hoverTile;

    private Building hoverBuilding;
    private Person hoverPerson;

    //Variable used to store the current object being focused on,
    //ideally to switch between viewing the building and person on a tile
    private int viewMode = 0;

    private SurfaceHolder surfaceHolder;

    //Position and square "radius" of the camera
    private float centerX, centerY;
    private float width;

    //A variable used to store a constant value of height / width
    private float aspectRatio = 0f;

    private Bitmap seaTile;
    private Bitmap iceTile;
    private Bitmap taigaTile;
    private Bitmap desertTile;
    private Bitmap steppeTile;
    private Bitmap dryForestTile;
    private Bitmap forestTile;
    private Bitmap rainforestTile;
    private Bitmap[] bitmapTiles;
    private Bitmap androidTile;

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

        seaTile = BitmapHelper.findBitmapOrBuild(R.drawable.shallow_sea_texture);
        iceTile = BitmapHelper.findBitmapOrBuild(R.drawable.ice_texture);
        taigaTile = BitmapHelper.findBitmapOrBuild(R.drawable.taiga_texture);
        desertTile = BitmapHelper.findBitmapOrBuild(R.drawable.desert_texture);
        steppeTile = BitmapHelper.findBitmapOrBuild(R.drawable.steppe_texture);
        dryForestTile = BitmapHelper.findBitmapOrBuild(R.drawable.dryforest_texture);
        forestTile = BitmapHelper.findBitmapOrBuild(R.drawable.forest_texture);
        rainforestTile = BitmapHelper.findBitmapOrBuild(R.drawable.rainforest_texture);
        bitmapTiles = new Bitmap[]{seaTile, iceTile, taigaTile, desertTile, steppeTile, dryForestTile, forestTile, rainforestTile};
        androidTile = BitmapHelper.findBitmapOrBuild(R.drawable.coal);
    }

    //The two methods below are provided to handle orientation changes
    public void updateAspectHeightWidth() {
        aspectRatio = (float) getHeight() / (float) getWidth();
    }

    public void updateAspectWidthHeight() {
        aspectRatio = (float) getWidth() / (float) getHeight();
    }

    public void setCurrentWorld(World world) {
        this.world = world;
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

                if (tile.resources.size() > 0) {
                    bmpIcon = BitmapHelper.findBitmapOrBuild(R.drawable.coal);
                    canvas.drawBitmap(bmpIcon, null, new Rect(
                                    (int) ((displayR + 0.5) * renderWidth),
                                    (int) ((displayC + 0.5) * renderHeight),
                                    (int) ((displayR + 1) * renderWidth),
                                    (int) ((displayC + 1) * renderHeight)
                            ),
                            null
                    );
                }

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

                    float healthPercent = (float) person.getHealth() / (float) person.getMaxHealth();

                    if (person.queueTasks.size() > 0) {
                        Task firstTask = person.queueTasks.get(0);
                        float percentageCompleted = 1.0f - (float) firstTask.ticksLeft / (float) firstTask.originalTicksLeft;
                        //percentageCompleted -= 0.5f / (float) firstTask.originalTicksLeft;
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

                //hoverTile = tile;
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
                if (tile != null) {
                    //Behavior: If the same tile is clicked on, cycle through the various objects at that one tile
                    //Restart the cycle of items (building, person 1, person 2, etc.) when clicking on a new tile
                    if (hoverTile == tile) {
                        if (viewMode == 0) viewMode = 1;
                        else viewMode = 0;
                    }
                    else {
                        viewMode = 0;
                    }

                    tileDetailsClearUi(tile);
                    hoverBuilding = null;
                    hoverPerson = null;

                    if (viewMode == 0) {
                        hoverBuilding = tile.getBuilding();
                        showTileBuildingDetails(tile);
                    }
                    else if (viewMode == 1) {
                        hoverPerson = tile.people.size() > 0 ? tile.people.get(0) : null;
                        showTilePersonDetails(tile);
                    }
                }
                else {
                    viewMode = 0;
                }
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

    private void tileDetailsClearUi(Tile tile) {
        //Default, display current location's game coords
        ((TextView) context.findViewById(R.id.buildingLocation)).setVisibility(VISIBLE);
        ((TextView) context.findViewById(R.id.buildingLocation)).setText(tile.row + " " + tile.col);

        //Clear all the UI related to buildings
        ((TextView) context.findViewById(R.id.buildingTileResource)).setVisibility(GONE);

        ((TextView) context.findViewById(R.id.buildingName)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(GONE);
        //((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(GONE);
        //((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);

        ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);
        ((Button) context.findViewById(R.id.btnProductionList)).setVisibility(GONE);

        ((LinearLayout) context.findViewById(R.id.constructionBuildingList)).setVisibility(GONE);
        ((LinearLayout) context.findViewById(R.id.productionRecipesList)).setVisibility(GONE);
        ((LinearLayout) context.findViewById(R.id.buildingUpgradesList)).setVisibility(GONE);
        ((LinearLayout) context.findViewById(R.id.localTradeList)).setVisibility(GONE);
        ((LinearLayout) context.findViewById(R.id.personDataList)).setVisibility(GONE);

        ((LinearLayout) context.findViewById(R.id.constructionBuildingList)).removeAllViews();
        ((LinearLayout) context.findViewById(R.id.productionRecipesList)).removeAllViews();
        ((LinearLayout) context.findViewById(R.id.buildingUpgradesList)).removeAllViews();
        ((LinearLayout) context.findViewById(R.id.localTradeList)).removeAllViews();
        ((LinearLayout) context.findViewById(R.id.personDataList)).removeAllViews();

        ((Button) context.findViewById(R.id.btnUpgradesList)).setVisibility(GONE);

        ((Button) context.findViewById(R.id.btnTradeResources)).setVisibility(GONE);

        context.findViewById(R.id.scrollTradeDialog).setVisibility(GONE);
        context.findViewById(R.id.tradeDialog).setVisibility(GONE);

        //Clear all the UI related to people
        ((TextView) context.findViewById(R.id.personName)).setVisibility(GONE);
        ((TextView) context.findViewById(R.id.personHealth)).setVisibility(GONE);
    }

    //Display UI details when trying to look at any buildings at a tile (may not exist)
    private void showTileBuildingDetails(final Tile tile) {
        if (tile.resources.size() > 0) {
            ((TextView) context.findViewById(R.id.buildingTileResource)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingTileResource)).setText("Resources: " + tile.resources.toString());
        }

        //TODO: Work on making layout beautiful and useful and informative, also scroll views go through buttons at bottom (draft, trade, overworld)

        if (hoverBuilding != null) {
            ((TextView) context.findViewById(R.id.buildingName)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingDesc)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingItemsList)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setVisibility(VISIBLE);

            ((TextView) context.findViewById(R.id.buildingProduction)).setVisibility(VISIBLE);
            //((Button) context.findViewById(R.id.btnProduceTest)).setVisibility(VISIBLE);
            ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(GONE);

            String nameString = hoverBuilding.name;
            int numUpgrades = hoverBuilding.currentUpgrades.size();
            if (numUpgrades > 0) {
                nameString += " +" + hoverBuilding.currentUpgrades.get(numUpgrades - 1);
            }

            ((TextView) context.findViewById(R.id.buildingName)).setText(nameString);
            ((TextView) context.findViewById(R.id.buildingDesc)).setText(hoverBuilding.desc);
            //if (!building.getProductionRecipeString().equals("Nothing")) {

            ((TextView) context.findViewById(R.id.buildingProduction)).setText("Produces: " + hoverBuilding.getProductionRecipeString());
            //}
            ((TextView) context.findViewById(R.id.buildingHousingNum)).setText("Houses " + hoverBuilding.housingNum + " people");
            ((TextView) context.findViewById(R.id.buildingItemsList)).setText("Stored: " + hoverBuilding.getItemsString());

            //((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(null);
            /*((Button) context.findViewById(R.id.btnProduceTest)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tile.getBuilding().produce();
                    showTileDetails(tile);
                }
            });*/

            if (hoverBuilding.getProductionRecipes().size() > 0)
                ((Button) context.findViewById(R.id.btnProductionList)).setVisibility(VISIBLE);

            if (hoverBuilding.possibleBuildingUpgrades.size() > 0)
                ((Button) context.findViewById(R.id.btnUpgradesList)).setVisibility(VISIBLE);

            if (hoverBuilding.name.equals("Nexus")) {
                ((Button) context.findViewById(R.id.btnTradeResources)).setVisibility(VISIBLE);
            }
        }
        else {
            ((Button) context.findViewById(R.id.btnConstructionBuilding)).setVisibility(VISIBLE);
        }
    }

    //Display UI details when trying to look at any people at a tile (may not exist)
    private void showTilePersonDetails(final Tile tile) {
        if (hoverPerson != null) {
            ((TextView) context.findViewById(R.id.personName)).setVisibility(VISIBLE);
            ((TextView) context.findViewById(R.id.personHealth)).setVisibility(VISIBLE);
            ((LinearLayout) context.findViewById(R.id.personDataList)).setVisibility(VISIBLE);

            ((TextView) context.findViewById(R.id.personName)).setText(hoverPerson.name + " of " + hoverPerson.faction.name);
            String healthString = "Health: " + hoverPerson.getHealth() + "/" + hoverPerson.getMaxHealth();
            if (!hoverPerson.hasNoUntreatedInjuries()) {
                int[] numInjuries = hoverPerson.getNumInjuries();
                if (numInjuries[0] + numInjuries[1] > 1) {
                    healthString += " (" + numInjuries[0] + "/" + (numInjuries[0] + numInjuries[1]) + " injuries treated)";
                }
                else {
                    healthString += " (" + numInjuries[0] + "/" + (numInjuries[0] + numInjuries[1]) + " injury treated)";
                }
            }
            ((TextView) context.findViewById(R.id.personHealth)).setText(healthString);
            LinearLayout personDetails = ((LinearLayout) context.findViewById(R.id.personDataList));

            for (Map.Entry<String, Integer> entry: hoverPerson.skillLevels.entrySet()) {
                String skillName = entry.getKey();
                int level = hoverPerson.skillLevels.get(skillName);
                int[] expDisplay = hoverPerson.reportExpForSkill(skillName);

                TextView skillTextView = new TextView(context);
                skillTextView.setBackgroundColor(Color.WHITE);
                skillTextView.setText(skillName + " | Level " + level + ", " + expDisplay[0] + "/" + expDisplay[1]);
                personDetails.addView(skillTextView);
            }
        }
        else {
            //Do nothing, the UI should have been cleared
        }
    }

    public Tile getHoverTile() {
        return hoverTile;
    }

    public Settlement getActiveSettlement() {
        return activeSettlement;
    }

    public void updateInGameCustomTimeUI() {
        View view = context.findViewById(R.id.inGameTimeDisplay);
        if (view != null && view.getVisibility() == View.VISIBLE) {
            ((TextView) context.findViewById(R.id.inGameTimeDisplayYear)).setText("Year " + world.customGameTime.year);
            ((TextView) context.findViewById(R.id.inGameTimeDisplaySeason)).setText(world.customGameTime.getSeasonName());
            ((TextView) context.findViewById(R.id.inGameTimeDisplayHour)).setText("Day " + (world.customGameTime.day + 1) + ", " + world.customGameTime.hour + "h");
        }
    }

}
