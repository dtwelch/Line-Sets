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
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import org.jgrapht.Graphs;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import setvis.Restaurant.RestaurantType;
import setvis.Restaurant.RestaurantBuilder;

import java.util.*;

/**
 * <p>The main driver for an interactive, lineset-based visualization of
 * restaurants in downtown Seattle.</p>
 *
 * @author Dwelch <dtw.welch@gmail.com>
*/
public class LineSets extends PApplet {

    private final Map<SubCategory, List<Restaurant>> mySubCategories =
            new HashMap<>();

    private final Map<SubCategory, List<Restaurant>> myActiveSelections =
            new HashMap<>();

    private ControlP5 myControls;

    private UnfoldingMap myBackgroundMap;
    private float plotX1, plotY1, plotX2, plotY2;

    @Override public void setup() {
        size(700, 600);
        plotX1 = 0; plotY1 = 0; plotX2 = width; plotY2 = 60;

        createMapBackground();
        createCategoryControlPanel();
        preprocessInput();
        computeAndUpdateRestaurantOrderings();
    }

    @Override public void draw() {
        myBackgroundMap.draw();
        drawActiveCurves();

        drawRestaurantMarkers();
        drawCategoryPanels();

    }

    /**
     * <p>Draws a smooth curve through every point in each of the subcategories
     * selected -- maintained by <code>myActiveSelections</code>.</p>
     */
    private void drawActiveCurves() {

        for (Map.Entry<SubCategory, List<Restaurant>> e : myActiveSelections
                .entrySet()) {
            List<Restaurant> curRestaurants = e.getValue();

            if (curRestaurants != null && !curRestaurants.isEmpty()) {
                ScreenPosition first = toScreenPosition(curRestaurants.get(0));
                ScreenPosition last = toScreenPosition(curRestaurants
                        .get(curRestaurants.size() - 1));

                beginShape();
                stroke(e.getKey().getColor());
                strokeWeight(5);
                noFill();
                curveVertex(first.x, first.y);

                for (Restaurant r : curRestaurants) {
                    ScreenPosition curPosition = toScreenPosition(r);
                    curveVertex(curPosition.x, curPosition.y);

                    ellipse(curPosition.x, curPosition.y, 7, 7);
                }
                curveVertex(last.x, last.y);
                endShape();
            }
        }
    }

    private ScreenPosition toScreenPosition(Restaurant e) {
        return toScreenPosition(e.getLocation());
    }

    private ScreenPosition toScreenPosition(Location l) {
        return myBackgroundMap.getScreenPosition(l);
    }

    /**
     * <p>Initializes and sets parameters such as default zoom levels and
     * panning boundaries for the background citymap.</p>
     */
    private void createMapBackground() {
        myBackgroundMap = new UnfoldingMap(this, new Google.GoogleMapProvider
                ());
        //myBackgroundMap = new UnfoldingMap(this, new
        //MapBox.LacquerProvider());
        myBackgroundMap.zoomAndPanTo(12, new Location(47.626, -122.337));
        myBackgroundMap.zoomToLevel(13);
        myBackgroundMap.setBackgroundColor(0);

        MapUtils.createDefaultEventDispatcher(this, myBackgroundMap);
        myBackgroundMap.setTweening(true);
    }

    private void drawRestaurantMarkers() {
        for (List<Restaurant> restaurants : mySubCategories.values()) {
            for (Restaurant r : restaurants) {
                fill(175);
                stroke(0);
                strokeWeight(1);
                ellipse(toScreenPosition(r).x, toScreenPosition(r).y, 7, 7);
            }
        }
    }

    private void computeAndUpdateRestaurantOrderings() {
        for (Map.Entry<SubCategory, List<Restaurant>> e : mySubCategories
                .entrySet()) {
            computeAndUpdateRestaurantOrderings(e.getKey());
        }
    }

    /**
     * <p>Writeme.</p>
     */
    private void computeAndUpdateRestaurantOrderings(SubCategory category) {
        SimpleWeightedGraph<Restaurant, DefaultWeightedEdge> g =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, mySubCategories.get(category));

        //build a complete graph
        for (Restaurant r : mySubCategories.get(category)) {
            for (Restaurant e : mySubCategories.get(category)) {
                if (!e.equals(r)) {
                    g.addEdge(r, e);
                    g.setEdgeWeight(g.getEdge(r, e),
                            r.getLocation().getDistance(e.getLocation()));
                }
            }
        }

        KruskalMinimumSpanningTree<Restaurant, DefaultWeightedEdge> mst =
                new KruskalMinimumSpanningTree<>(g);

        SimpleWeightedGraph<Restaurant, DefaultWeightedEdge> subgraph =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for (DefaultWeightedEdge e : mst.getMinimumSpanningTreeEdgeSet()) {
            Graphs.addEdgeWithVertices(subgraph, g.getEdgeSource(e),
                    g.getEdgeTarget(e));
        }

        DepthFirstIterator<Restaurant, DefaultWeightedEdge> treeIter =
                new DepthFirstIterator<>(subgraph);

        mySubCategories.get(category).clear();

        while (treeIter.hasNext()) {
            mySubCategories.get(category).add(treeIter.next());
        }
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
        myControls =
                new ControlP5(this, createFont("Helvetica-Bold", 8));
        frameRate(25);

        Button american = myControls.addButton("American");
        american.setPosition(plotX1 + 20, plotY1 + 35)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(RestaurantType.AMERICAN.getColor())
                .setSwitch(true);


        Button italian = myControls.addButton("Italian");
        italian.setPosition(plotX1 + 20 + 110, plotY1 + 35)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(RestaurantType.ITALIAN.getColor())
                .setSwitch(true);

        Button asian = myControls.addButton("Asian");
        asian.setPosition(plotX1 + 20, plotY1 + 60)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(RestaurantType.ASIAN.getColor())
                .setSwitch(true);

        Button mexican = myControls.addButton("Mexican");
        mexican.setPosition(plotX1 + 20 + 110, plotY1 + 60)
                .setSize(100, 20)
                .setColorBackground(color(65, 65, 65))
                .setColorForeground(color(90, 90, 90))
                .setColorActive(RestaurantType.MEXICAN.getColor())
                .setSwitch(true);
    }

    private void American(int theValue) {
        updateActiveSelection("American", RestaurantType.AMERICAN);
    }

    private void updateActiveSelection(String name, SubCategory category) {

        if (myActiveSelections.get(category) == null) {
            myActiveSelections.put(category, new LinkedList<Restaurant>());
        }

        if (myControls.get(Button.class, name).getBooleanValue()) {
            System.out.println("Adding American restaurants to selection");
            List<Restaurant> selected = mySubCategories.get(category);
            myActiveSelections.get(category).addAll(selected);
        }
        else {
            System.out.println("Removing American restaurants from selection");

            myActiveSelections.get(category).clear();
        }
    }

    private void preprocessInput() {
        JSONArray rawData = loadJSONArray("yelp_restaurants_categorized4.json");
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
