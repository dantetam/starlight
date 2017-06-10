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

import io.github.dantetam.world.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class ItemXmlParser {
    private static final String ns = null;

    //TODO: Impl. this class.

    public static void parseResourceTree(ConstructionTree tree, Context context, int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        try {
            parseResourceTree(tree, inputStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseResourceTree(ConstructionTree tree, InputStream inputStream)
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
                if (xpp.getName().equals("resource")) {
                    int id = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                    String itemName = xpp.getAttributeValue(null, "name");
                    float marketValue = Float.parseFloat(xpp.getAttributeValue(null, "marketvalue"));
                    int maxHealth = Integer.parseInt(xpp.getAttributeValue(null, "maxhealth"));

                    Item item = new Item(id, itemName, maxHealth, marketValue);

                    String[] customData = new String[]{"combatmelee", "combatshot", "combatrange", "combatchance", "combattime"};
                    for (String customAttr: customData) {
                        String attr = xpp.getAttributeValue(null, "data-" + customAttr);
                        if (attr == null) {
                            attr = xpp.getAttributeValue(null, customAttr);
                        }
                        if (attr != null) {
                            try {
                                item.putItemData(customAttr, Float.parseFloat(attr));
                            } catch (NumberFormatException exception) {
                                exception.printStackTrace();
                                System.err.println("Could not format string for custom data attr: " + customAttr);
                            }
                        }
                    }

                    tree.insertItem(item);
                }
            } else if (eventType == XmlPullParser.END_TAG) {

            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = xpp.next();
        }
    }

}
