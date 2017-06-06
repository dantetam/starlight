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

package com.example.currentplacedetailsonmap;

import android.content.Context;

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

    //TODO: Impl. this class.

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

        //HashMap<String, String> addRequirementsNames = new HashMap<>();
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                //System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                //System.out.println("Start tag " + xpp.getName());
                if (xpp.getName().equals("impr")) {
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

                    tree.insertBuilding(building);

                    //System.err.println("Passed in " + id + ", " + buildingName);
                }
            } else if (eventType == XmlPullParser.END_TAG) {

            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = xpp.next();
        }

        tree.skills.add("Construction");
        tree.skills.add("Transporting");
        tree.skills.add("Shooting");
    }

    /*
    Note that for this method to work, the items have to be parsed in before the buildings
     */
    public static List<Item> convertItemsStringToItems(ConstructionTree tree, String string) {
        List<Item> result = new ArrayList<>();
        String[] tokens = string.split(",");
        for (String token: tokens) {
            String[] split = token.trim().split(" ");
            int quantity = Integer.parseInt(split[0]);
            String itemName = split[1];
            Item item = tree.copyItem(itemName, quantity);
            result.add(item);
        }
        return result;
    }

}
