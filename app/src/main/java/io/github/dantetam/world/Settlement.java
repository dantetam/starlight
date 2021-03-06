package io.github.dantetam.world;

import io.github.dantetam.person.Faction;
import io.github.dantetam.util.Vector2f;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.person.Pathfinder;
import io.github.dantetam.person.Person;
import io.github.dantetam.xml.ConstructionTree;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dante on 5/18/2017.
 */
public class Settlement implements Serializable { //implements Parcelable {

    public Vector2f realGeoCoord, gameCoord;
    private Tile[][] tiles;
    public final int rows, cols;
    public List<Person> people;

    public int gold;

    public String name;
    public Date foundDate;
    public String formattedDate;

    public Map<String, List<Job>> availableJobsBySkill;
    //public List<Job> inProgressJobs;

    public Building nexus; //Where resources are stored

    public static Pathfinder<Tile> pathfinder = new Pathfinder<>();

    public CombatHandler combatHandler;
    public List<Person> visitors;

    public ConstructionTree constructionTree;
    public Faction faction;

    public TechTree homeTechTree;

    public Settlement(String name, Date foundDate, Vector2f realGeoCoord, Vector2f gameCoord, Faction faction, int r, int c, ConstructionTree tree) {
        this.name = name;
        this.foundDate = foundDate;
        this.realGeoCoord = realGeoCoord;
        this.gameCoord = gameCoord;
        this.faction = faction;
        rows = r; cols = c;
        tiles = new Tile[r][c];
        people = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("MM dd yyyy");
        formattedDate = df.format(foundDate);
        availableJobsBySkill = new HashMap<>();
        gold = 50;
        combatHandler = new CombatHandler(this, tree);
        visitors = new ArrayList<>();
        this.constructionTree = tree;
        homeTechTree = new TechTree(faction, this);
    }

    public void initializeNeighbors() {
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                Tile tile = tiles[r][c];
                int[] dr = new int[]{r-1, r-1, r-1, r, r+1, r+1, r+1, r};
                int[] dc = new int[]{c-1, c, c+1, c+1, c+1, c, c-1, c-1};
                for (int i = 0; i < 8; i++) {
                    if (inBounds(dr[i], dc[i])) {
                        Tile neighbor = tiles[dr[i]][dc[i]];
                        tile.storedNeighbors.add(neighbor);
                    }
                }
            }
        }
    }

    public void initializeSettlementTileTypes(int[][] resources) {
        if (resources.length != tiles.length || tiles.length == 0) {
            throw new IllegalArgumentException("Resources array not aligned with world or world is of size 0");
        }
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                tiles[r][c] = new Tile(r,c);
                tiles[r][c].tileType = resources[r][c];
            }
        }
    }

    public void initializeSettlementTileResources(int[][] resourcesData, List<Item> possibleResources) {
        if (resourcesData.length != tiles.length || tiles.length == 0) {
            throw new IllegalArgumentException("Resources array not aligned with world or world is of size 0");
        }
        if (possibleResources.size() == 0) {
            return;
        }

        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                if (resourcesData[r][c] >= 0) {
                    int index;
                    if (resourcesData[r][c] >= possibleResources.size()) {
                        index = (int) (Math.random() * possibleResources.size());
                    }
                    else {
                        index = resourcesData[r][c];
                    }
                    Item initResource = possibleResources.get(index);
                    tiles[r][c].resources.addItem(new Item(initResource, initResource.quantity));
                }
            }
        }
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public Tile getTile(int r, int c) {
        if (inBounds(r,c)) {
            return tiles[r][c];
        }
        return null;
    }

    public Tile randomTile() {
        int randomR = (int) (Math.random() * this.rows);
        int randomC = (int) (Math.random() * this.cols);
        return getTile(randomR, randomC);
    }

    public void movePerson(Person person, Tile dest) {
        if (person.tile != null) {
            person.tile.people.remove(person);
            person.tile = null;
        }
        person.tile = dest;
        dest.people.add(person);
    }

    public void upgradeBuilding(Building building, String upgradeName) {
        Building upgrade = constructionTree.getBuildingByName(upgradeName);

        building.possibleBuildingUpgrades = new ArrayList<>(upgrade.possibleBuildingUpgrades);
        building.currentUpgrades.add(upgradeName);

        for (Recipe recipe: upgrade.getProductionRecipes()) {
            building.addProductionRecipe(recipe);
        }
    }

    public void upgradeBuilding(Building building, Building upgrade) {
        building.possibleBuildingUpgrades = new ArrayList<>(upgrade.possibleBuildingUpgrades);
        building.currentUpgrades.add(upgrade.name);

        for (Recipe recipe: upgrade.getProductionRecipes()) {
            building.addProductionRecipe(recipe);
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof Settlement)) {
            return false;
        }
        return this.realGeoCoord.equals(((Settlement) other).realGeoCoord);
    }

}
