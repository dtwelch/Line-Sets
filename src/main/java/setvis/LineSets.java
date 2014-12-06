/* This file is part of 'LineSets', a final project for cpsc804: Data
 * Visualization.
 *
 * LineSets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LineSets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LineSets. If not, see http://www.gnu.org/licenses/.
 */
package setvis;

import controlP5.Button;
import controlP5.ControlP5;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import setvis.Restaurant.RestaurantType;
import setvis.Restaurant.RestaurantBuilder;

import java.util.*;

/**
 * <p>The main driver for an interactive, seattle restaurant lineset based
 * visualization.</p>
 *
 * @author DWelch <dtw.welch@gmail.com>
*/
public class LineSets extends PApplet {

    private final Map<SubCategory, List<Restaurant>> mySubCategories =
            new HashMap<>();

    private final HashMap<SubCategory, List<Restaurant>> myActiveSelections =
            new HashMap<>();

    private UnfoldingMap myBackgroundMap;
    private ControlP5 myControls;

    private float plotX1, plotY1, plotX2, plotY2;

    @Override public void setup() {
        size(500, 500);

        createMapBackground();

        preprocessInput();
    }

    @Override
    public void draw() {
        myBackgroundMap.draw();
        drawCategoryPanels();

    }

    private void drawCategoryPanels() {
        noStroke();
        fill(130, 130, 130, 210);
        rect(plotX1 + 10, plotY1 + 10, 230, plotY2 + 20, 6);

        textFont(createFont("Helvetica-Bold", 12));
        fill(255);
        text("RESTAURANT TYPE", plotX1 + 17, plotY1 + 25);
    }

    public void createCategoryControlPanel(){
        myControls = new ControlP5(this);
        myControls.setControlFont(createFont("Helvetica-Bold", 8));
        frameRate(25);

        Button american = myControls.addButton("American");
        american.setValue(0)
                .setPosition(plotX1 + 20, plotY1 + 35)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(Restaurant.RestaurantType.AMERICAN.getColor())
                .setSwitch(true);

        Button italian = myControls.addButton("Italian");
        italian.setValue(0)
                .setPosition(plotX1 + 20 + 110, plotY1 + 35)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(Restaurant.RestaurantType.ITALIAN.getColor())
                .setSwitch(true);

        Button asian = myControls.addButton("Asian");
        asian.setValue(0)
                .setPosition(plotX1 + 20, plotY1 + 60)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(Restaurant.RestaurantType.ASIAN.getColor())
                .setSwitch(true);

        Button mexican = myControls.addButton("Mexican");
        mexican.setValue(0)
                .setPosition(plotX1 + 20 + 110, plotY1 + 60)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(Restaurant.RestaurantType.MEXICAN.getColor())
                .setSwitch(true);
    }

    private void American(int theValue) {
        updateSelection("American", Restaurant.RestaurantType.AMERICAN);
    }

    private void updateSelection(String controllerName, SubCategory category) {
        if ( ((Button) myControls.controller(controllerName)).isOn()) {
            List<Restaurant> selected = mySubCategories.get(category);
            myActiveSelections.get(category).addAll(selected);
        }
        else {
            myActiveSelections.get(category).clear();
        }
    }

    /**
     * <p></p>
     */
    private void createMapBackground() {
        myBackgroundMap = new UnfoldingMap(this, new Google.GoogleMapProvider());
        myBackgroundMap.zoomAndPanTo(12, new Location(47.626, -122.337));
        myBackgroundMap.zoomToLevel(13);
        myBackgroundMap.setBackgroundColor(0);

        MapUtils.createDefaultEventDispatcher(this, myBackgroundMap);
        myBackgroundMap.setTweening(true);
    }

    private void preprocessInput() {
        JSONArray rawData =
                loadJSONArray("yelp_restaurants_categorized_test.json");
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < rawData.size(); i++) {
            JSONObject o = rawData.getJSONObject(i);
            JSONObject coord = o.getJSONObject("location")
                    .getJSONObject("coordinate");

            sanityCheckRestaurant(seen, o.getString("id"));

            RestaurantBuilder restaurant =
                    new RestaurantBuilder(o.getString("name"))
                        .id(o.getString("id"))
                        .type(getType(o, o.getJSONArray("categories")))
                        .location(coord.getFloat("latitude"),
                                coord.getFloat("longitude"));

            addToAppropriateSets(restaurant.build());
        }
    }

    /**
     * <p>Adds {@link Restaurant} <code>r</code> to the appropriate slot in the
     * entry map. If <code>r</code> is the first, we initialize here.</p>
     *
     * @param r An instance of {@link Restaurant}.
     */
    private void addToAppropriateSets(Restaurant r) {
        if (mySubCategories.get(r.getType()) == null) {
            mySubCategories.put(r.getType(), new LinkedList<Restaurant>());
        }
        mySubCategories.get(r.getType()).add(r);
    }

    /**
     * <p></p>
     * @param entry
     * @param categories
     * @return
     */
    private RestaurantType getType(JSONObject entry, JSONArray categories) {
        Restaurant.RestaurantType result = null;

        for (int i = 0; i < categories.size(); i++) {
            result = searchInnerCategory(categories.getJSONArray(i));
            if (result != null) { break; }
        }

        if (result == null) {
            throw new IllegalStateException("Unable to categorize restaurant: "
                    + entry.getString("name") + ".");
        }
        return result;
    }

    private Restaurant.RestaurantType searchInnerCategory(JSONArray category) {
        Restaurant.RestaurantType result = null;
        for (int i = 0; i < category.size(); i++) {
            for (Restaurant.RestaurantType t :
                    Restaurant.RestaurantType.values()) {
                if (t.acceptableFor(category.getString(i))) { result = t; }
            }
        }
        return result;
    }

    /**
     * <p>Sounds the alarm if the entry we're trying to add shares an ID
     * with an entry already added.</p>
     *
     * @param seenAlready The set of entry IDs already parsed.
     * @param id A candidate ID.
     */
    private void sanityCheckRestaurant(Set<String> seenAlready, String id) {
        if (seenAlready.contains(id)) {
            throw new IllegalStateException("Duplicate id: " + id);
        }
    }
}
