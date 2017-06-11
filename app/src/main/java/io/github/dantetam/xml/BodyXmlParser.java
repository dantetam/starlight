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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dantetam.person.Body;
import io.github.dantetam.person.BodyPart;

/**
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class BodyXmlParser {
    private static final String ns = null;

    public static Body parseBodyTree(Context context, int resourceId) {
        final InputStream bodyDataStream = context.getResources().openRawResource(
                resourceId);
        Body result = null;
        try {
            result = parseBodyTree(bodyDataStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    This method parses an XML document, line by line. It individually searches tags,
    where <techroot>...</techroot> marks the first technology, and all its children
    are future techs linked to the parent tech.

    This builds the tech tree with the help of a stack, where a <tech> tag
    pushes a new tech to the stack and sets the parent if it exists, and a </tech>
    tag pops a tech off the stack. The stackCounter int represents distance from
    the tech root, where -1 indicates no tech has been parsed.
     */
    public static Body parseBodyTree(InputStream inputStream)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        //xpp.setInput( new StringReader ( "<foo>Hello World!</foo>" ) );
        //xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(inputStream, null);
        int stackCounter = -1;
        List<BodyPart> stack = new ArrayList<>();

        int eventType = xpp.getEventType();

        Body body = new Body();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("bodypart") || xpp.getName().equals("bodyroot")) {
                    String name = xpp.getAttributeValue(null, "name");
                    int maxHealth = Integer.parseInt(xpp.getAttributeValue(null, "maxhealth"));
                    float coverage = Float.parseFloat(xpp.getAttributeValue(null, "coverage"));
                    String desc = xpp.getAttributeValue(null, "desc");

                    BodyPart bodyPart = new BodyPart(name, desc, coverage, maxHealth);

                    body.getBodyPartsByName().put(name, bodyPart);

                    if (xpp.getName().equals("bodyroot")) {
                        body.root = bodyPart;
                    }
                    stack.add(bodyPart);
                    if (stackCounter >= 0) {
                        bodyPart.parent = stack.get(stackCounter);
                        stack.get(stackCounter).subBodyParts.add(bodyPart);
                        //bodyPart.parent
                    }
                    stackCounter++;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (xpp.getName().equals("bodypart") || xpp.getName().equals("bodyroot")) {
                    stack.remove(stack.size() - 1);
                    stackCounter--;
                }
            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = xpp.next();
        }

        return body;
    }

}
