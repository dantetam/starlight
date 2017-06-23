/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.dantetam.xml;

import android.content.Context;

import io.github.dantetam.person.BodyPart;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Item;
import io.github.dantetam.world.Recipe;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class BuildingXMLParser {
    private static final String ns = null;

    public static void parseBuildingTree(ConstructionTree tree, Context context, int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        try {
            parseBuildingTree(tree, inputStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseBuildingTree(ConstructionTree tree, InputStream inputStream)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, null);

        int stackCounter = -1;
        List<Building> stack = new ArrayList<>();

        //HashMap<String, String> addRequirementsNames = new HashMap<>();
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                //System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                //System.out.println("Start tag " + xpp.getName());
                if (xpp.getName().equals("impr") || xpp.getName().equals("imprupgrade")) {
                    int id = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                    String buildingName = xpp.getAttributeValue(null, "name");
                    int housingNum = xpp.getAttributeValue(null, "housing") != null ?
                            Integer.parseInt(xpp.getAttributeValue(null, "housing")) : 0;
                    String descString = xpp.getAttributeValue(null, "desc");

                    String resourceNeeded = xpp.getAttributeValue(null, "resourceneeded");
                    int resourceHoldLimit = Integer.parseInt(xpp.getAttributeValue(null, "resourcelimit"));
                    String costString = xpp.getAttributeValue(null, "cost");
                    String productionString = xpp.getAttributeValue(null, "production");
                    String jobType = xpp.getAttributeValue(null, "jobtype");
                    int buildTime = Integer.parseInt(xpp.getAttributeValue(null, "buildtime"));

                    if (!tree.skills.contains(jobType)) {
                        tree.skills.add(jobType);
                    }

                    Building building = new Building(id, buildingName, descString, resourceHoldLimit, housingNum, resourceNeeded, jobType, buildTime);

                    if (costString != null && !costString.isEmpty()) {
                        String[] recipes = costString.split(";");
                        for (String recipeString: recipes) {
                            List<Item> costInput = convertItemsStringToItems(tree, recipeString);
                            building.addBuildingCost(Recipe.newRecipeOnlyInput(new Inventory(costInput)));
                        }
                    }
                    if (productionString != null && !productionString.isEmpty()) {
                        String[] recipes = productionString.split(";");
                        for (String recipeString: recipes) {
                            if (recipeString.contains(">")) {
                                String[] tokens = recipeString.split(">");
                                String input = tokens[0];
                                String output = tokens[1];
                                List<Item> costInput = convertItemsStringToItems(tree, input);
                                List<Item> productionOutput = convertItemsStringToItems(tree, output);
                                building.addProductionRecipe(new Recipe(
                                        new Inventory(costInput),
                                        new Inventory(productionOutput)
                                ));
                            }
                            else {
                                List<Item> productionOutput = convertItemsStringToItems(tree, recipeString);
                                building.addProductionRecipe(Recipe.newRecipeOnlyOutput(new Inventory(productionOutput)));
                            }
                        }
                    }

                    String[] customData = new String[]{"productionTimeForLump", "lumpSize"};
                    for (String customAttr: customData) {
                        String attr = xpp.getAttributeValue(null, "data-" + customAttr);
                        if (attr == null) {
                            attr = xpp.getAttributeValue(null, customAttr);
                        }
                        if (attr != null) {
                            try {
                                building.putBuildingData(customAttr, Float.parseFloat(attr));
                            } catch (NumberFormatException exception) {
                                exception.printStackTrace();
                                System.err.println("Could not format string for custom data attr: " + customAttr);
                            }
                        }
                    }

                    if (xpp.getName().equals("impr")) { //Is a "root" building, unlocked by tech
                        tree.insertBaseBuilding(building);
                    }
                    else if (xpp.getName().equals("imprupgrade")) { //Is an upgraded form of another building
                        tree.insertUpgradeBuilding(building);
                    }

                    stack.add(building);
                    if (stackCounter >= 0) {
                        //building.parent = stack.get(stackCounter);
                        stack.get(stackCounter).possibleBuildingUpgrades.add(buildingName);
                    }
                    stackCounter++;

                    //System.err.println("Passed in " + id + ", " + buildingName);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                /*for (Building building: stack) {
                    System.err.println(building.name);
                }
                System.err.println("-------------");*/
                if (stack.size() > 0) {
                    stack.remove(stack.size() - 1);
                }
                stackCounter--;
            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = xpp.next();
        }

        //Temporary until this code can be added programmatically
        tree.skills.add("Construction");
        tree.skills.add("Deconstruction");
        tree.skills.add("Transporting");
        tree.skills.add("Shooting");
        tree.skills.add("Essential");

        tree.getItemByName("Iron").superClassId = -3;
        tree.getItemByName("Silver").superClassId = -3;
        tree.getItemByName("Gold").superClassId = -3;

        tree.getItemByName("Wood").superClassId = -2;
        tree.getItemByName("Tropical Wood").superClassId = -2;

        tree.getItemByName("Wheat").superClassId = -1;
        tree.getItemByName("Potato").superClassId = -1;
        tree.getItemByName("Fruit").superClassId = -1;
        tree.getItemByName("Greens").superClassId = -1;
    }

    /*
    Note that for this method to work, the items have to be parsed in before the buildings
     */
    public static List<Item> convertItemsStringToItems(ConstructionTree tree, String string) {
        List<Item> result = new ArrayList<>();
        String[] tokens = string.split(",");
        for (String token: tokens) {
            int index = indexOfFirstLetter(token.trim());
            int quantity = Integer.parseInt(token.trim().substring(0, index).trim());
            String itemName = token.trim().substring(index).trim();
            Item item = tree.copyItem(itemName, quantity);
            result.add(item);
        }
        return result;
    }

    private static int indexOfFirstLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
