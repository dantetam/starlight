package io.github.dantetam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.R;

import io.github.dantetam.android.BitmapHelper;
import io.github.dantetam.jobs.ConstructionJob;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.jobs.UpgradeJob;
import io.github.dantetam.maps.MapResourceOverlay;
import io.github.dantetam.person.Body;
import io.github.dantetam.person.Faction;
import io.github.dantetam.person.Person;
import io.github.dantetam.quests.OverworldQuest;
import io.github.dantetam.quests.PictureOverworldQuest;
import io.github.dantetam.util.VariableListener;
import io.github.dantetam.util.Vector2f;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Item;
import io.github.dantetam.world.QuestLocation;
import io.github.dantetam.world.Recipe;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.TechTree;
import io.github.dantetam.world.Tile;
import io.github.dantetam.world.World;
import io.github.dantetam.xml.BodyXmlParser;
import io.github.dantetam.xml.BuildingXMLParser;
import io.github.dantetam.xml.ConstructionTree;
import io.github.dantetam.xml.ItemXmlParser;
import io.github.dantetam.xml.NameStorage;
import io.github.dantetam.xml.TechXmlParser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback,
                GoogleMap.OnMarkerClickListener,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private StarlightSurfaceView surfaceView;
    //private boolean renderingSettlement = false;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Address mLastKnownAddress;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private final int mMaxEntries = 5;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];

    private Geocoder geocoder;

    private World world;
    private Settlement currentSettlement;
    private QuestLocation currentQuestLocation;
    private VariableListener<Integer> gold;
    private VariableListener<Float> distTravelled;

    private Map<Marker, Settlement> settlementIndicesByMarker;
    private Map<Settlement, Boolean> discoveredSettlementMap;
    private Map<Marker, QuestLocation> questLocationsByMarker;

    private ConstructionTree constructionTree;
    private AssetManager assetManager;
    private NameStorage nameStorage;
    private List<MapResourceOverlay> mapResourceOverlays;
    private List<Inventory> worldPossibleResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetManager = this.getAssets();

        nameStorage = new NameStorage();
        nameStorage.loadNames(assetManager, "male_names.txt", "female_names.txt", "world_names.txt");

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        geocoder = new Geocoder(this, Locale.getDefault());

        constructionTree = new ConstructionTree();
        ItemXmlParser.parseResourceTree(constructionTree, this, R.raw.resource_tree);
        BuildingXMLParser.parseBuildingTree(constructionTree, this, R.raw.building_tree);

        world = new World(nameStorage.randomWorldName(), constructionTree);
        gold = new VariableListener<Integer>(100) {
            @Override
            public void callback() {
                TextView display = ((TextView) findViewById(R.id.mapGoldDisplay));;
                if (display != null && this.value() != null)
                    display.setText("Omnigold: " + gold);
            }
        };

        distTravelled = new VariableListener<Float>(0.0f) {
            @Override
            public void callback() {
                TextView display = ((TextView) findViewById(R.id.mapDistDisplay));;
                if (display != null && this.value() != null)
                    display.setText(String.format("Distance Travelled: %.2f m", distTravelled.value()));
            }
        };

        settlementIndicesByMarker = new HashMap<>();
        questLocationsByMarker = new HashMap<>();

        mHandler = new Handler();
        startRepeatingTask();

        gold.set(100);
        distTravelled.set(0.0f);

        BitmapHelper.init(this);

        mapResourceOverlays = new ArrayList<>();

        worldPossibleResources = new ArrayList<>();

        Inventory inventory = new Inventory();
        inventory.addItem(constructionTree.copyItem("Stone", 100));
        worldPossibleResources.add(inventory);

        inventory = new Inventory();
        inventory.addItem(constructionTree.copyItem("Silver", 100), constructionTree.copyItem("Gold", 30));
        worldPossibleResources.add(inventory);

        inventory = new Inventory();
        inventory.addItem(constructionTree.copyItem("Tropical Wood", 100));
        worldPossibleResources.add(inventory);

        inventory = new Inventory();
        inventory.addItem(constructionTree.copyItem("Sludge", 100), constructionTree.copyItem("Stone", 50));
        worldPossibleResources.add(inventory);

        for (Inventory specialInventory: worldPossibleResources) {
            specialInventory.addItem(constructionTree.copyItem("Wood", 300), constructionTree.copyItem("Iron", 100), constructionTree.copyItem("Stone", 50));
        }

        world.factions.add(new Faction("Colonists"));
        world.factions.add(new Faction("Pirates"));
        world.factions.add(new Faction("Tribes"));
        world.factions.add(new Faction("Officers"));
        world.factions.add(new Faction("Settlers"));

        discoveredSettlementMap = new HashMap<>();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    public boolean addressOfCurrentLocationButton(View v) {
        getDeviceLocation();
        List<String> addressFragments = findAddress(mLastKnownLocation);
        for (String addressFragment: addressFragments) {
            System.err.println(addressFragment);
        }
        return false;
    }

    public void initializeOtherSettlements(LatLng location) {
        if (world.settlements.size() < 20) {
            int numCreate = 10 - world.settlements.size();
            for (int j = 0; j < numCreate; j++) {
                float dLat = (float) (Math.random() - 0.5f);
                float dLon = (float) (Math.random() - 0.5f);
                LatLng randLoc = new LatLng(location.latitude + dLat, location.longitude + dLon);

                Faction faction;
                do {
                    faction = world.randomFaction();
                } while (faction.name.equals("Colonists"));

                createSettlement(randLoc, faction);
            }

            numCreate = 20 - world.settlements.size();
            for (int j = 0; j < numCreate; j++) {
                float dLat = (float) (Math.random() / 5.0f - 0.1f);
                float dLon = (float) (Math.random() / 5.0f - 0.1f);
                LatLng randLoc = new LatLng(location.latitude + dLat, location.longitude + dLon);

                Faction faction;
                do {
                    faction = world.randomFaction();
                } while (faction.name.equals("Colonists"));

                createSettlement(randLoc, faction);
            }
        }
    }

    public void initializeQuestLocations(LatLng location) {
        if (world.questLocations.size() < 20) {
            int numCreate = 20 - world.questLocations.size();
            for (int j = 0; j < numCreate; j++) {
                float dLat = (float) (Math.random() / 5.0f - 0.1f);
                float dLon = (float) (Math.random() / 5.0f - 0.1f);
                LatLng randLoc = new LatLng(location.latitude + dLat, location.longitude + dLon);

                Faction faction;
                do {
                    faction = world.randomFaction();
                } while (faction.name.equals("Colonists"));

                createQuestLocation(randLoc, faction);
            }
        }
    }

    private void createSettlement(LatLng location, Faction faction) {
        Inventory resources = getMapResources(location);

        Calendar calendar = Calendar.getInstance();

        Settlement settlement = world.createSettlement(
                faction.name, calendar.getTime(),
                new Vector2f(location.latitude, location.longitude),
                faction,
                constructionTree, resources);

        discoveredSettlementMap.put(settlement, true);

        if (settlement == null) {
            return;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                        .title("Settlement " + faction.name)
                        .position(location)
                        .snippet("")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        settlementIndicesByMarker.put(marker, settlement);

        //Initialize some first buildings
        settlement.nexus = constructionTree.copyBuilding("Nexus");
        settlement.getTile(settlement.rows / 2, settlement.cols / 2).addBuilding(settlement.nexus);
        Building tent = constructionTree.copyBuilding("Tent");
        settlement.getTile(settlement.rows / 2 + 1, settlement.cols / 2).addBuilding(tent);
        settlement.nexus.items.addItem(constructionTree.copyItem("Wood", 300));
        settlement.nexus.items.addItem(constructionTree.copyItem("Iron", 300));
        settlement.nexus.items.addItem(constructionTree.copyItem("Meal", 50));

        //TODO: V
        //TechXmlParser.parseTechTree(settlement.homeTechTree, constructionTree, this, R.raw.tech_tree, R.raw.tech_tree_layout);

        for (int i = 0; i < 20; i++) {
            Person person = new Person(nameStorage.randomPersonName(), constructionTree.skills, faction);
            Body parsedHumanBody = BodyXmlParser.parseBodyTree(this, R.raw.human_body);
            person.initializeBody(parsedHumanBody);
            settlement.people.add(person);
            Tile randomTile = settlement.randomTile();
            settlement.movePerson(person, randomTile);
            person.weapon = constructionTree.copyItem("Wooden Bow", 1);
        }

        for (String skill: constructionTree.skills) {
            settlement.availableJobsBySkill.put(skill, new ArrayList<Job>());
        }
    }

    private void createQuestLocation(LatLng location, Faction faction) {

        Calendar calendar = Calendar.getInstance();

        QuestLocation questLocation = world.createQuestLocation(
                faction.name,
                calendar.getTime(),
                new Vector2f(location.latitude, location.longitude),
                faction);

        if (questLocation == null) {
            return;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                        .title("Quest Location: " + faction.name)
                        .position(location)
                        .snippet("")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );
        questLocationsByMarker.put(marker, questLocation);

        for (int i = 0; i < 20; i++) {
            Person person = new Person(nameStorage.randomPersonName(), constructionTree.skills, faction);
            Body parsedHumanBody = BodyXmlParser.parseBodyTree(this, R.raw.human_body);
            person.initializeBody(parsedHumanBody);
            questLocation.people.add(person);
        }

        for (int i = 0; i < 10; i++) {
            float dLat = (float) (Math.random() - 0.1f);
            float dLon = (float) (Math.random() - 0.1f);
            LatLng randLoc = new LatLng(location.latitude + dLat, location.longitude + dLon);
            OverworldQuest quest = new PictureOverworldQuest("Quest " + i, "Take a picture near the following location.", 0, randLoc);
            questLocation.quests.add(quest);
        }
    }

    public boolean newSettlementButton(View v) {
        getDeviceLocation();

        Calendar calendar = Calendar.getInstance();

        LatLng convertedLoc = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        Inventory resources = getMapResources(convertedLoc);

        String name = "Settlement " + (world.settlements.size() + 1);
        Settlement settlement = world.createSettlement(
                name, calendar.getTime(),
                new Vector2f(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                world.getFaction("Colonists"),
                constructionTree, resources);

        discoveredSettlementMap.put(settlement, true);

        if (settlement != null && gold.value() >= 35) {
            gold.set(gold.value() - 35);
            // Add a marker for the selected place, with an info window
            // showing information about that place.
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(name)
                    .position(convertedLoc)
                    .snippet(""));
            settlementIndicesByMarker.put(marker, settlement);

            //Initialize some first buildings
            settlement.nexus = constructionTree.copyBuilding("Nexus");
            settlement.getTile(settlement.rows / 2, settlement.cols / 2).addBuilding(settlement.nexus);
            Building tent = constructionTree.copyBuilding("Tent");
            settlement.getTile(settlement.rows / 2 + 1, settlement.cols / 2).addBuilding(tent);
            settlement.nexus.items.addItem(constructionTree.copyItem("Wood", 200));
            settlement.nexus.items.addItem(constructionTree.copyItem("Tropical Wood", 100));
            settlement.nexus.items.addItem(constructionTree.copyItem("Iron", 50));
            settlement.nexus.items.addItem(constructionTree.copyItem("Meal", 50));

            for (int i = 0; i < 10; i++) {
                Person person = new Person(nameStorage.randomPersonName(), constructionTree.skills, world.getFaction("Colonists"));
                Body parsedHumanBody = BodyXmlParser.parseBodyTree(this, R.raw.human_body);
                person.initializeBody(parsedHumanBody);
                settlement.people.add(person);
                Tile randomTile = settlement.randomTile();
                settlement.movePerson(person, randomTile);
                person.weapon = constructionTree.copyItem("Wooden Composite Bow", 1);
            }

            for (int i = 0; i < 2; i++) {
                Person person = new Person(nameStorage.randomPersonName(), constructionTree.skills, world.getFaction("Pirates"));
                Body parsedHumanBody = BodyXmlParser.parseBodyTree(this, R.raw.human_body);
                person.initializeBody(parsedHumanBody);
                settlement.visitors.add(person);
                Tile randomTile = settlement.randomTile();
                settlement.movePerson(person, randomTile);
            }

            for (String skill: constructionTree.skills) {
                settlement.availableJobsBySkill.put(skill, new ArrayList<Job>());
            }
        }
        else {

        }

        if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), mMap.getCameraPosition().zoom));
        }

        return false;
    }

    private Inventory getMapResources(LatLng location) {
        for (MapResourceOverlay overlay: mapResourceOverlays) {
            if (overlay.inBounds(location)) {
                return overlay.getItemsAtCoord(location);
            }
        }

        //None of the currently existing map overlays contain this point
        byte[][] randomResourceData;
        randomResourceData = World.convertToByteArray(world.generateRandomTiles(100, 100, 0, worldPossibleResources.size() - 1), 100, 100);

        MapResourceOverlay newOverlay = new MapResourceOverlay(location,100,100,randomResourceData,worldPossibleResources);
        mapResourceOverlays.add(newOverlay);
        return newOverlay.getItemsAtCoord(location);
    }

    public void newConstructionBuildingList(View v) {
        final Context context = this;
        final LinearLayout constructionList = (LinearLayout) findViewById(R.id.constructionBuildingList);
        constructionList.setVisibility(View.VISIBLE);
        constructionList.removeAllViews();

        Collection<Building> buildings = constructionTree.getAllBuildings();
        for (final Building building: buildings) {
            if (building.buildTime < 0) {
                continue;
            }

            Button button = new Button(this);
            constructionList.addView(button);

            button.setText(building.name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int chosenCostRecipe = building.costRecipeNum; //TODO: Select this from UI
                    if (surfaceView != null && surfaceView.getHoverTile() != null) {

                        if (building.resourceNeeded != null) {
                            Item item = constructionTree.getItemByName(building.resourceNeeded);
                            if (!surfaceView.getHoverTile().resources.hasItem(item)) {
                                return;
                            }
                        }

                        if (currentSettlement.nexus != null) {
                            Inventory cost = building.getCostRecipes().get(chosenCostRecipe).input;
                            if (currentSettlement.nexus.items.hasInventory(cost)) {
                                currentSettlement.nexus.items.removeInventory(cost);
                                String jobType = "Construction";
                                Job constructionJob = new ConstructionJob(surfaceView.getActiveSettlement(), constructionTree.copyBuilding(building.name), surfaceView.getHoverTile(), chosenCostRecipe);
                                currentSettlement.availableJobsBySkill.get(jobType).add(constructionJob);
                            }
                        }

                    }
                    constructionList.setVisibility(View.GONE);
                    constructionList.removeAllViews();
                }
            });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    List<Recipe> possibleCostRecipes = building.getCostRecipes();
                    for (int i = 0; i < possibleCostRecipes.size(); i++) {
                        Recipe recipe = possibleCostRecipes.get(i);
                        String str = recipe.toString();

                        Button button = new Button(context);

                        if (building.costRecipeNum == i) {
                            button.setBackgroundColor(Color.BLUE);
                        } else {
                            button.setBackgroundColor(Color.WHITE);
                        }

                        constructionList.addView(button);
                        button.setText("Build with: " + str);

                        final int j = i;

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                building.costRecipeNum = j;
                                for (int i = 0; i < constructionList.getChildCount(); i++) {
                                    View child = constructionList.getChildAt(i);
                                    if (child instanceof Button) {
                                        Button button = (Button) child;
                                        if (button.getText().toString().startsWith("Build with: ")) {
                                            child.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    return true;
                }
            });
        }

    }

    public void newProductionRecipesList(View v) {
        final Context context = this;
        final LinearLayout productionList = (LinearLayout) findViewById(R.id.productionRecipesList);
        productionList.setVisibility(View.VISIBLE);
        productionList.removeAllViews();

        Tile hover = surfaceView.getHoverTile();
        if (hover == null || hover.getBuilding() == null) {
            return;
        }

        final Building building = hover.getBuilding();
        List<Recipe> recipes = building.getProductionRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            final int j = i;

            Recipe recipe = recipes.get(i);
            Button button = new Button(this);
            productionList.addView(button);

            if (building.getActiveRecipes().contains(i)) {
                button.setBackgroundColor(Color.BLUE);
            }
            else {
                button.setBackgroundColor(Color.WHITE);
            }

            button.setText(recipe.toString());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (building.getActiveRecipes().contains(j)) {
                        building.deactivateRecipe(j);
                    }
                    else {
                        if (building.getActiveRecipes().size() >= building.maxRecipesEnabled) {
                            building.deactivateRandom();
                        }
                        building.activateRecipe(j);
                    }
                    productionList.setVisibility(View.GONE);
                    productionList.removeAllViews();
                    newProductionRecipesList(null);
                }
            });
        }
    }

    public void newUpgradesList(View v) {
        final Context context = this;
        final LinearLayout upgradesList = ((LinearLayout) findViewById(R.id.buildingUpgradesList));

        final Tile hover = surfaceView.getHoverTile();
        if (hover == null || hover.getBuilding() == null) {
            return;
        }

        upgradesList.setVisibility(View.VISIBLE);
        upgradesList.removeAllViews();

        final Building building = hover.getBuilding();

        for (String possibleUpgrade: building.possibleBuildingUpgrades) {
            final Building upgradeBuilding = constructionTree.getBuildingByName(possibleUpgrade);

            Button button = new Button(this);
            upgradesList.addView(button);

            button.setText(possibleUpgrade);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int chosenCostRecipe = upgradeBuilding.costRecipeNum; //TODO: Select this from UI
                    if (surfaceView != null && surfaceView.getHoverTile() != null) {
                        if (upgradeBuilding.resourceNeeded != null) {
                            Item item = constructionTree.getItemByName(upgradeBuilding.resourceNeeded);
                            if (!surfaceView.getHoverTile().resources.hasItem(item)) {
                                return;
                            }
                        }
                        if (currentSettlement.nexus != null) {
                            Inventory cost = upgradeBuilding.getCostRecipes().get(chosenCostRecipe).input;
                            if (currentSettlement.nexus.items.hasInventory(cost)) {
                                currentSettlement.nexus.items.removeInventory(cost);
                                String jobType = "Construction";
                                Job upgradeJob = new UpgradeJob(currentSettlement, building, upgradeBuilding, hover, chosenCostRecipe);
                                currentSettlement.availableJobsBySkill.get(jobType).add(upgradeJob);
                            }
                        }
                    }

                    upgradesList.setVisibility(View.GONE);
                    upgradesList.removeAllViews();
                }
            });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    List<Recipe> possibleCostRecipes = upgradeBuilding.getCostRecipes();
                    for (int i = 0; i < possibleCostRecipes.size(); i++) {
                        Recipe recipe = possibleCostRecipes.get(i);
                        String str = recipe.toString();

                        Button button = new Button(context);

                        if (upgradeBuilding.costRecipeNum == i) {
                            button.setBackgroundColor(Color.BLUE);
                        } else {
                            button.setBackgroundColor(Color.WHITE);
                        }

                        upgradesList.addView(button);
                        button.setText("Build with: " + str);

                        final int j = i;

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upgradeBuilding.costRecipeNum = j;
                                for (int i = 0; i < upgradesList.getChildCount(); i++) {
                                    View child = upgradesList.getChildAt(i);
                                    if (child instanceof Button) {
                                        Button button = (Button) child;
                                        if (button.getText().toString().startsWith("Build with: ")) {
                                            child.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    return true;
                }
            });
        }
    }

    public void toggleDraft(View v) {
        if (currentSettlement != null) {
            for (Person person: currentSettlement.people) {
                if (!person.isDead()) {
                    if (person.isDrafted) {
                        person.isDrafted = false;
                    }
                    else {
                        person.isDrafted = true;
                    }
                    if (person.currentJob != null) {
                        person.currentJob.reservedPerson = null;
                        person.currentJob = null;
                    }
                    person.queueTasks.clear();
                }
            }
        }
    }

    public void openTradeResources(View v) {
        if (currentSettlement != null && currentSettlement.nexus != null) {
            findViewById(R.id.scrollTradeDialog).setVisibility(View.VISIBLE);
            findViewById(R.id.tradeDialog).setVisibility(View.VISIBLE);
            LinearLayout tradeList = (LinearLayout) findViewById(R.id.tradeDialog);
            tradeList.removeAllViews();

            List<Item> items = currentSettlement.nexus.items.getItems();

            for (final Item item: items) {
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setVisibility(View.VISIBLE);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView itemName = new TextView(this);
                itemName.setText(item.name + " (" + item.quantity + "), " + item.baseMarketPrice + ":");
                itemName.setBackgroundColor(Color.WHITE);
                itemLayout.addView(itemName);

                itemName.setPadding(5, 0, 5, 0);

                final TextView itemBuySellQuantity = new TextView(this);
                itemBuySellQuantity.setText("0");
                itemBuySellQuantity.setBackgroundColor(Color.WHITE);
                itemLayout.addView(itemBuySellQuantity);

                Button itemBuy = new Button(this);
                itemBuy.setText("+1");
                itemLayout.addView(itemBuy);

                Button itemSell = new Button(this);
                itemSell.setText("-1");
                itemLayout.addView(itemSell);

                itemBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                        quantity++;
                        itemBuySellQuantity.setText("" + quantity);
                    }
                });

                itemSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                        if (-quantity - 1 <= item.quantity) {
                            quantity--;
                            itemBuySellQuantity.setText("" + quantity);
                        }
                    }
                });

                tradeList.addView(itemLayout);
            }
        }
    }

    //TODO: Convert this trade menu dialog to a general function that takes in two inventories
    public void showLocalTradeMenu(View v) {
        final LinearLayout localTradeList = ((LinearLayout) findViewById(R.id.localTradeList));
        final Context context = this;

        localTradeList.setVisibility(View.VISIBLE);

        if (currentSettlement != null) {
            List<Settlement> settlements = world.findNearbySettlements(currentSettlement, 30000);

            for (final Settlement settlement: settlements) {
                if (settlement.equals(currentSettlement)) {
                    continue;
                }

                Button button = new Button(this);
                button.setText(settlement.name + " (" + settlement.faction.name + ")");
                button.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initTradeMenu(currentSettlement, settlement);
                            }
                        }
                );
                localTradeList.addView(button);
            }
        }
    }

    private void initTradeMenu(Settlement home, Settlement visitor) {
        final LinearLayout localTradeList = ((LinearLayout) findViewById(R.id.localTradeList));
        final Context context = this;

        localTradeList.removeAllViews();

        LinearLayout priceLayout = new LinearLayout(context);
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);

        final TextView leftArrow = new TextView(context);
        leftArrow.setText("<");
        leftArrow.setBackgroundColor(Color.WHITE);
        priceLayout.addView(leftArrow);

        final TextView priceSilver = new TextView(context);
        priceSilver.setText("0");
        priceSilver.setBackgroundColor(Color.WHITE);
        priceLayout.addView(priceSilver);

        final TextView rightArrow = new TextView(context);
        rightArrow.setText(">");
        rightArrow.setBackgroundColor(Color.WHITE);
        priceLayout.addView(rightArrow);

        Button tradeFinalize = new Button(context);
        tradeFinalize.setText("Complete Trade");
        priceLayout.addView(tradeFinalize);

        tradeFinalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localTradeList.getChildCount() > 2) {
                    for (int i = 1; i < localTradeList.getChildCount(); i++) {
                        LinearLayout row = (LinearLayout) localTradeList.getChildAt(i);
                    }
                }
            }
        });

        localTradeList.addView(priceLayout);

        List<Item> items = home.nexus.items.getItems();
        for (final Item item: items) {
            LinearLayout itemLayout = new LinearLayout(context);
            itemLayout.setVisibility(View.VISIBLE);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView itemName = new TextView(context);
            itemName.setText(item.name + " (" + item.quantity + "), " + item.baseMarketPrice);
            itemName.setBackgroundColor(Color.WHITE);
            itemLayout.addView(itemName);
            itemName.setPadding(5, 0, 5, 0);

            Button itemBuy = new Button(context);
            itemBuy.setText("+1");
            itemLayout.addView(itemBuy);

            final TextView itemBuySellQuantity = new TextView(context);
            itemBuySellQuantity.setText("0");
            itemBuySellQuantity.setBackgroundColor(Color.WHITE);
            itemLayout.addView(itemBuySellQuantity);

            Button itemSell = new Button(context);
            itemSell.setText("-1");
            itemLayout.addView(itemSell);

            itemBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                    quantity++;
                    itemBuySellQuantity.setText("" + quantity);

                    float currentSilver = Float.parseFloat(priceSilver.getText().toString());
                    currentSilver -= item.baseMarketPrice;
                    priceSilver.setText(String.format("%.2f", currentSilver));

                    if (currentSilver > 0) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.INVISIBLE);
                    }
                    else {
                        rightArrow.setVisibility(View.VISIBLE);
                        leftArrow.setVisibility(View.INVISIBLE);
                    }
                }
            });

            itemSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                    if (-quantity - 1 <= item.quantity) {
                        quantity--;
                        itemBuySellQuantity.setText("" + quantity);
                    }

                    float currentSilver = Float.parseFloat(priceSilver.getText().toString());
                    currentSilver += item.baseMarketPrice;
                    priceSilver.setText(String.format("%.2f", currentSilver));

                    if (currentSilver > 0) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.INVISIBLE);
                    }
                    else {
                        rightArrow.setVisibility(View.VISIBLE);
                        leftArrow.setVisibility(View.INVISIBLE);
                    }
                }
            });
            localTradeList.addView(itemLayout);
        }

        List<Item> otherItems = visitor.nexus.items.getItems();
        for (final Item item: otherItems) {
            LinearLayout itemLayout = new LinearLayout(context);
            itemLayout.setVisibility(View.VISIBLE);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(Gravity.RIGHT);

            Button itemBuy = new Button(context);
            itemBuy.setText("+1");
            itemLayout.addView(itemBuy);

            final TextView itemBuySellQuantity = new TextView(context);
            itemBuySellQuantity.setText("0");
            itemBuySellQuantity.setBackgroundColor(Color.WHITE);
            itemLayout.addView(itemBuySellQuantity);

            Button itemSell = new Button(context);
            itemSell.setText("-1");
            itemLayout.addView(itemSell);

            TextView itemName = new TextView(context);
            itemName.setText(item.name + " (" + item.quantity + "), " + item.baseMarketPrice);
            itemName.setBackgroundColor(Color.WHITE);
            itemLayout.addView(itemName);
            itemName.setPadding(5, 0, 5, 0);

            itemBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                    quantity++;
                    itemBuySellQuantity.setText("" + quantity);

                    float currentSilver = Float.parseFloat(priceSilver.getText().toString());
                    currentSilver -= item.baseMarketPrice;
                    priceSilver.setText(String.format("%.2f", currentSilver));

                    if (currentSilver > 0) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.INVISIBLE);
                    }
                    else {
                        rightArrow.setVisibility(View.VISIBLE);
                        leftArrow.setVisibility(View.INVISIBLE);
                    }
                }
            });

            itemSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(itemBuySellQuantity.getText().toString());
                    if (-quantity - 1 <= item.quantity) {
                        quantity--;
                        itemBuySellQuantity.setText("" + quantity);
                    }

                    float currentSilver = Float.parseFloat(priceSilver.getText().toString());
                    currentSilver += item.baseMarketPrice;
                    priceSilver.setText(String.format("%.2f", currentSilver));

                    if (currentSilver > 0) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.INVISIBLE);
                    }
                    else {
                        rightArrow.setVisibility(View.VISIBLE);
                        leftArrow.setVisibility(View.INVISIBLE);
                    }
                }
            });
            localTradeList.addView(itemLayout);
        }
    }

    public void showOverworld(View v) {

    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        initializeOtherSettlements(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        initializeQuestLocations(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));

        mMap.setOnMarkerClickListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), 9));
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (world.geoHome == null && mLastKnownLocation != null) {
                world.geoHome = new Vector2f(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            }
        }

        // Set the map's camera position to the current location of the device.
        /*if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), mMap.getCameraPosition().zoom));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }*/
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    int i = 0;
                    mLikelyPlaceNames = new String[mMaxEntries];
                    mLikelyPlaceAddresses = new String[mMaxEntries];
                    mLikelyPlaceAttributions = new String[mMaxEntries];
                    mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Build a list of likely places to show the user. Max 5.
                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                .getAttributions();
                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > (mMaxEntries - 1)) {
                            break;
                        }
                    }
                    // Release the place likelihood buffer, to avoid memory leaks.
                    likelyPlaces.release();

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog();
                }
            });
        } else {
            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The "which" argument contains the position of the selected item.
                        LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                        String markerSnippet = mLikelyPlaceAddresses[which];
                        if (mLikelyPlaceAttributions[which] != null) {
                            markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                        }

                        // Position the map's camera at the location of the marker.
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                                DEFAULT_ZOOM));
                    }
                };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /*private boolean locationIsWater(Location location) {
        if (mMap == null) {
            return false;
        }
        mMap.
    }*/

    public void buttonSaveWorld(View v) {
        ((Button) findViewById(R.id.btnSaveWorld)).setText("Saving...");
        saveWorld(world);
        ((Button) findViewById(R.id.btnSaveWorld)).setText("Save World");
    }

    public void buttonLoadWorld(View v) {
        File file = this.getFilesDir();
        File[] files = file.listFiles();
        for (File child: files) {
            System.err.println(child.toString());
        }
    }

    private void saveWorld(World world) {
        String filename = world.name + ".txt";
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            ObjectOutputStream objectStream = new ObjectOutputStream(fileOutputStream);
            objectStream.writeObject(world);
            objectStream.close();

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void saveWorld(World world) {
        String jsonJake = new Gson().toJson(world);
        System.out.println("Json: \n"+jsonJake);
        World loadedWorld = new Gson().fromJson(jsonJake, World.class);
        System.out.println("Object:"+loadedWorld.toString());
    }*/

    protected ArrayList<String> findAddress(float lat, float lon) {
        String errorMessage = "";

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    lat,
                    lon,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "Not available";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid lat/lon";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + lat +
                    ", Longitude = " + lon,
                    illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";
                Log.e(TAG, errorMessage);
            }
        } else { //Found an address, return the address fragments
            mLastKnownAddress = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= mLastKnownAddress.getMaxAddressLineIndex(); i++) {
                addressFragments.add(mLastKnownAddress.getAddressLine(i));
            }
            Log.i(TAG, "Found address");
            return addressFragments;
        }
        return null;
    }
    protected List<String> findAddress(Location location) {
        return findAddress((float) location.getLatitude(), (float) location.getLongitude());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Settlement settlement = settlementIndicesByMarker.get(marker);
        QuestLocation questLocation = questLocationsByMarker.get(marker);
        final Context context = this;

        if (settlement != null) {
            currentSettlement = settlement;
            currentQuestLocation = null;

            /*Intent i = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("settlementData", settlement);
            ArrayList<String> addrList = findAddress(settlement.realGeoCoord.x, settlement.realGeoCoord.y);
            if (addrList != null) {
                b.putSerializable("settlementAddress", addrList);
            }
            i.putExtras(b);
            i.setClass(this, SettlementDetailsActivity.class);
            startActivity(i);*/

            setContentView(R.layout.activity_settlement_live);

            surfaceView = (StarlightSurfaceView) ((ViewGroup) findViewById(R.id.tileDetailsLayout).getParent()).getChildAt(0);

            surfaceView.setCurrentWorld(world);

            surfaceView.setSettlement(settlement);
            surfaceView.setVisibility(View.VISIBLE);

            surfaceView.drawSettlement();
        }
        else if (questLocation != null) {
            currentSettlement = null;
            currentQuestLocation = questLocation;

            setContentView(R.layout.activity_quest_live);

            ((TextView) findViewById(R.id.questLocationName)).setText(questLocation.name);

            final LinearLayout questsLayout = ((LinearLayout) findViewById(R.id.availableQuestsLayout));
            questsLayout.removeAllViews();

            for (final OverworldQuest quest: questLocation.quests) {
                final Button questButton = new Button(context);
                questButton.setText(quest.name + ": " + quest.desc);
                questButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quest instanceof PictureOverworldQuest) {
                            dispatchTakePictureIntent();
                        }
                        questsLayout.removeAllViews();
                        //setContentView(R.layout.activity_maps);
                    }
                });
                questsLayout.addView(questButton);
            }
        }
        return false;
    }

    //Photo processing code

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO_FOR_QUEST = 1;
    private static String currentPhotoPath;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                currentPhotoPath = photoFile.getPath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_FOR_QUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO_FOR_QUEST && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get(MediaStore.EXTRA_OUTPUT);
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmpFactoryOptions);

            processImageBitmap(imageBitmap);


        }
    }

    private void processImageBitmap(Bitmap imageBitmap) {
        System.err.println("Processing image successfully");
    }

    //Game looping and updating code

    private Location prevLocation;
    private void updateDistance() {
        if (mMap == null) {
            return;
        }
        getDeviceLocation();
        if (prevLocation != null && mLastKnownLocation != null) {
            float distanceToLast = mLastKnownLocation.distanceTo(prevLocation);
            // if less than 10 metres, do not record
            if (distanceToLast < 4.00) {

            } else
                distTravelled.set(distTravelled.value() + distanceToLast);
        }
        prevLocation = mLastKnownLocation;
    }

    private int mInterval = 20; // in milliseconds, 5 seconds by default, can be changed later
    private Handler mHandler;
    private int frameModulo = 0;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                frameModulo++;
                frameModulo %= 6;
                //this function can change value of mInterval.
                if (frameModulo == 5) {

                }

                world.updateWorld();
                if (mLastKnownLocation != null)
                    world.updateQuests(mLastKnownLocation);
                updateDistance();
                //((TextView) findViewById(R.id.mapDistDisplay)).setText(String.format("Distance Travelled: %.2f m", distTravelled.value()));
                //((TextView) findViewById(R.id.mapGoldDisplay)).setText("Omnigold: " + gold.value());
                if (surfaceView != null) {
                    surfaceView.drawSettlement();
                }
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void startRepeatingTask() {
        mStatusChecker.run();
    }

    private void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

}
