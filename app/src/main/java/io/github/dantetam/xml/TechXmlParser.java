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

import io.github.dantetam.world.Tech;
import io.github.dantetam.world.TechTree;

/*
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
*/

public class TechXmlParser {
    private static final String ns = null;

    public static TechTree parseTechTree(TechTree techTree, ConstructionTree constructionTree, Context context, int resourceId, int secondResourceId) {
        final InputStream techStream = context.getResources().openRawResource(
                resourceId);
        //final InputStream techLocationStream = context.getResources().openRawResource(secondResourceId);
        TechTree result = null;
        try {
            result = parseTechTree(techTree, constructionTree, techStream);
            //parseTechLocationTree(result, techLocationStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*This method parses an XML document, line by line. It individually searches tags,
    where <techroot>...</techroot> marks the first technology, and all its children
    are future techs linked to the parent tech.

    This builds the tech tree with the help of a stack, where a <tech> tag
    pushes a new tech to the stack and sets the parent if it exists, and a </tech>
    tag pops a tech off the stack. The stackCounter int represents distance from
    the tech root, where -1 indicates no tech has been parsed.*/


    public static TechTree parseTechTree(TechTree techTree, ConstructionTree constructionTree, InputStream inputStream)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        //xpp.setInput( new StringReader ( "<foo>Hello World!</foo>" ) );
        //xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(inputStream, null);
        int stackCounter = -1;
        List<Tech> stack = new ArrayList<>();
        techTree.techMap = new HashMap<>();
        HashMap<String, String> addRequirementsNames = new HashMap<>();
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                //System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                //System.out.println("Start tag " + xpp.getName());
                if (xpp.getName().equals("tech") || xpp.getName().equals("techroot")) {
                    String techName = xpp.getAttributeValue(null, "name");
                    int workNeeded = Integer.parseInt(xpp.getAttributeValue(null, "workNeeded"));
                    //System.out.println(techName + " " + workNeeded);
                    Tech newTech = new Tech(techName, 0, workNeeded);
                    if (xpp.getName().equals("techroot")) {
                        techTree.setRoot(newTech);
                    }
                    stack.add(newTech);
                    if (stackCounter >= 0) {
                        stack.get(stackCounter).unlockedTechs.add(newTech);
                        newTech.parent = stack.get(stackCounter);
                    }
                    stackCounter++;

                    String unlockBuilding = xpp.getAttributeValue(null, "building");

                    if (unlockBuilding != null) {
                        String[] buildingsToUnlock;
                        buildingsToUnlock = unlockBuilding.split("/");
                        for (String buildingString: buildingsToUnlock) {
                            if (constructionTree.copyBuilding(buildingString) == null) {
                                System.err.println("Could not find building " + buildingString + " within " + buildingString);
                            }
                            newTech.unlockedBuildings.add(buildingString);
                        }
                    }

                    techTree.techMap.put(techName, newTech);

                    //A forward declaration for requirements?
                    String extraRequirement = xpp.getAttributeValue(null, "requirement");
                    if (extraRequirement != null) {
                        addRequirementsNames.put(techName, extraRequirement);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                //System.out.println("End tag " + xpp.getName());
                if (xpp.getName().equals("tech") || xpp.getName().equals("techroot")) {
                    stack.remove(stack.size() - 1);
                    stackCounter--;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                //System.out.println("Text "+xpp.getText());
            }
            eventType = xpp.next();
        }

        for (Map.Entry<String, String> entry: addRequirementsNames.entrySet()) {
            Tech subject = techTree.techMap.get(entry.getKey());
            Tech requirement = techTree.techMap.get(entry.getValue());
            subject.extraReqs.add(requirement);
        }
        //System.out.println("End document");
        return techTree;
    }

    //Mutate a tech tree by setting all the render positions
    /*public static void parseTechLocationTree(TechTree tree, InputStream inputStream)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, null);

        int eventType = xpp.getEventType();

        //Adjust so that all the indices are not negative, this is required for the tech GridLayout
        int minX = 0, minY = 0;
        int maxX = 0, maxY = 0;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                //System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                //System.out.println("Start tag " + xpp.getName());
                if (xpp.getName().equals("tech") || xpp.getName().equals("techroot")) {
                    String techName = xpp.getAttributeValue(null, "name");
                    Tech modifyTech = tree.techMap.get(techName);

                    String offsetStringX = xpp.getAttributeValue(null, "x");
                    String offsetStringY = xpp.getAttributeValue(null, "y");
                    int offX = Integer.parseInt(offsetStringX), offY = Integer.parseInt(offsetStringY);

                    if (offX < minX) minX = offX;
                    else if (offX > maxX) maxX = offX;
                    if (offY < minY) minY = offY;
                    else if (offY > maxY) maxY = offY;

                    if (modifyTech == null) {
                        System.out.println(techName);
                    }
                    modifyTech.treeOffsetX = offX; modifyTech.treeOffsetY = offY;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                //System.out.println("End tag " + xpp.getName());
                if (xpp.getName().equals("tech") || xpp.getName().equals("techroot")) {

                }
            } else if (eventType == XmlPullParser.TEXT) {
                //System.out.println("Text "+xpp.getText());
            }
            eventType = xpp.next();
        }

        tree.globalOffsetX = minX; tree.globalOffsetY = minY;
        tree.globalOffsetMaxY = maxY;

        tree.hardGlobalMinimum = new Vector2f(minX, minY);
        tree.hardGlobalMaximum = new Vector2f(maxX - 4, maxY);


        tree.minX = minX; tree.minY = minY;
        tree.maxX = maxX; tree.maxY = maxY;

        tree.screenCenterX = 0;
        tree.screenCenterY = 0;
    }*/

}
