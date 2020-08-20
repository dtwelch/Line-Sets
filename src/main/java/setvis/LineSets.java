/*
 * This file is part of 'LineSets', a final project for cpsc804: Data
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
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Microsoft;
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
import setvis.Restaurant.RestaurantRating;
import setvis.Restaurant.RestaurantReviewCount;
import setvis.Restaurant.RestaurantBuilder;
import setvis.gui.Gui;

import java.util.*;

/**
 * <p>The main driver for an interactive, lineset-based visualization of
 * restaurants in downtown Seattle.</p>
 *
 * @author dwelch <dtw.welch@gmail.com>
*/
public class LineSets extends PApplet {

    private final Map<RestaurantCategory, List<Restaurant>> mySubCategories =
            new HashMap<>();

    private final Map<RestaurantCategory, List<Restaurant>> myActiveSelections =
            new HashMap<>();

    private final Map<Restaurant, RestaurantMarker> myMarkers =
            new HashMap<>();

    private ControlP5 myControls;

    private UnfoldingMap myBackgroundMap;
    private float plotX1, plotY1, plotX2, plotY2;

    /**
     * <p>Initializes all variables and objects used by the visualization.
     * This is only executed <em>once</em>.</p>
     */
    @Override public void setup() {
        size(700, 600);
        plotX1 = 0; plotY1 = 0; plotX2 = width; plotY2 = 60;

        //Configures the map: chooses provider, sets panning restrictions, etc
        createMapBackground();

        //Sets up the controlP5 buttons
        createCategoryControlPanels();

        //Parses the data and creates our own Restaurant objects
        preprocessInput();

        //Uses a 2-opt TSP heuristic to find a reasonable restaurant ordering
        computeAndUpdateRestaurantOrderings();

        //Adds restaurant markers to the unfolding background map
        createRestaurantMarkers();
    }

    /**
     * <p>The main draw loop. This is executed repeatedly after a single
     * execution of {@link #setup()}.</p>
     */
    @Override public void draw() {
        myBackgroundMap.draw();
        drawActiveCurves();
        drawRestaurantMarkers();    //Re-draw the markers on top of curves
        drawCategoryPanels();
    }

    private void createRestaurantMarkers() {
        Set<Restaurant> allRestaurants = new HashSet<>();
        for (List<Restaurant> restaurants : mySubCategories.values()) {
            allRestaurants.addAll(restaurants);
        }

        for (Restaurant e : allRestaurants) {
            RestaurantMarker marker = new RestaurantMarker(e);
            marker.setStrokeWeight(1);
            marker.setRadius(7);
            marker.setColor(175);
            marker.setHighlightColor(0xFFF2003C);
            myBackgroundMap.addMarker(marker);
            myMarkers.put(e, marker);
        }
    }

    /**
     * <p>Initializes and sets parameters such as default zoom levels and
     * panning boundaries for the background map.</p>
     */
    private void createMapBackground() {
        myBackgroundMap = new UnfoldingMap(this, new Microsoft.RoadProvider());

        Location defaultLocation = new Location(47.626, -122.337);
        myBackgroundMap.zoomAndPanTo(12, defaultLocation);
        myBackgroundMap.setPanningRestriction(defaultLocation, 4);
        myBackgroundMap.setZoomRange(13, 30);
        myBackgroundMap.setBackgroundColor(0);

        MapUtils.createDefaultEventDispatcher(this, myBackgroundMap);
        myBackgroundMap.setTweening(true);
    }

    private void drawRestaurantMarkers() {
        for (Marker m : myBackgroundMap.getMarkers()) {
            m.draw(myBackgroundMap);
        }
    }

    public void mouseMoved() {
        for (Marker marker : myBackgroundMap.getMarkers()) {
            marker.setSelected(false);
        }

        Marker marker = myBackgroundMap.getFirstHitMarker(mouseX, mouseY);
        if (marker != null) {
            marker.setSelected(true);
        }
    }

    /**
     * <p>Draws a smooth curve through all subcategories maintained by
     * <code>myActiveSelections</code>.</p>
     */
    private void drawActiveCurves() {

        for (Map.Entry<RestaurantCategory, List<Restaurant>> e : myActiveSelections
                .entrySet()) {
            List<Restaurant> curRestaurants = e.getValue();

            if (curRestaurants != null && !curRestaurants.isEmpty()) {
                ScreenPosition first = toScreenPosition(curRestaurants.get(0));
                ScreenPosition last = toScreenPosition(curRestaurants
                        .get(curRestaurants.size() - 1));

                beginShape();
                noFill();
                stroke(e.getKey().getAssignedColor());
                strokeWeight(7);
                curveVertex(first.x, first.y);

                for (Restaurant r : curRestaurants) {
                    curveVertex(toScreenPosition(r).x, toScreenPosition(r).y);
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
     * <p>Computes, for each category, a 2-opt ordering of the contained
     * restaurants.</p>
     */
    private void computeAndUpdateRestaurantOrderings() {
        for (Map.Entry<RestaurantCategory, List<Restaurant>> e : mySubCategories
                .entrySet()) {
            computeAndUpdateRestaurantOrderings(e.getKey());
        }
    }

    private void computeAndUpdateRestaurantOrderings(RestaurantCategory category) {
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
        textFont(createFont("Helvetica-Bold", 12));
        noStroke();

        fill(130, 130, 130, 210);
        rect(plotX1 + 10, plotY1 + 10, 230, plotY2 + 20, 6);
        fill(240);
        text("Restaurant Type", plotX1 + 17, plotY1 + 25);

        fill(130, 130, 130, 210);
        rect(plotX1 + 250, plotY1 + 10, 130, plotY2 - 5, 6);
        fill(240);
        text("Rating", plotX1 + 255, plotY1 + 25);

        fill(130, 130, 130, 210);
        rect(plotX1 + 390, plotY1 + 10, 180, plotY2 - 5, 6);
        fill(240);
        text("Review Count", plotX1 + 395, plotY1 + 25);
    }

    public void createCategoryControlPanels(){

        myControls = new ControlP5(this, createFont("Helvetica-Bold", 8));
        frameRate(25);

        Gui.createRestaurantReviewCountButtons(myControls, plotX1, plotY1);
        Gui.createRestaurantTypeButtons(myControls, plotX1, plotY1);
        Gui.createRestaurantRatingButtons(myControls, plotX1, plotY1);
    }

    // This method is invoked automatically by the global variable: myControls
    // See Gui.createRestaurantTypeButtons(..) for details. Basically, the name you give the button has to match
    // the name of the method you want invoked when it's clicked (and it has to have a single parameter of type bool)
    // Also: each of these callback methods needs to be *public*, or controlP5 will silently error and your buttons
    //       won't work
    public void american(boolean theValue) {
        updateActiveSelection("american", RestaurantType.AMERICAN);
    }

    // This method is invoked automatically by the global variable: myControls
    public void italian(boolean theValue) {
        updateActiveSelection("italian", RestaurantType.ITALIAN);
    }

    // This method is invoked automatically by the global variable: myControls
    public void asian(boolean theValue) {
        updateActiveSelection("asian", RestaurantType.ASIAN);
    }

    // This method is invoked automatically by the global variable: myControls
    public void mexican(boolean theValue) {
        updateActiveSelection("mexican", RestaurantType.MEXICAN);
    }

    // This method is invoked automatically by the global variable: myControls
    public void three(boolean theValue) {
        updateActiveSelection("three", RestaurantRating.THREE);
    }

    // This method is invoked automatically by the global variable: myControls
    public void threePointFive(boolean theValue) {
        updateActiveSelection("threePointFive",
                RestaurantRating.THREE_POINT_FIVE);
    }

    // This method is invoked automatically by the global variable: myControls
    public void four(boolean theValue) {
        updateActiveSelection("four", RestaurantRating.FOUR);
    }

    // This method is invoked automatically by the global variable: myControls
    public void fourPointFive(boolean theValue) {
        updateActiveSelection("fourPointFive", RestaurantRating.FOUR_POINT_FIVE);
    }

    // This method is invoked automatically by the global variable: myControls
    public void smallReviewCount(boolean theValue) {
        updateActiveSelection("smallReviewCount",
                RestaurantReviewCount.SMALL_COUNT);
    }

    // This method is invoked automatically by the global variable: myControls
    public void mediumReviewCount(boolean theValue) {
        updateActiveSelection("mediumReviewCount",
                RestaurantReviewCount.MEDIUM_COUNT);
    }

    // This method is invoked automatically by the global variable: myControls
    public void largeReviewCount(boolean theValue) {
        updateActiveSelection("largeReviewCount",
                RestaurantReviewCount.LARGE_COUNT);
    }

    private void updateActiveSelection(String name, RestaurantCategory category) {

        if (myActiveSelections.get(category) == null) {
            myActiveSelections.put(category, new LinkedList<Restaurant>());
        }

        if (myControls.get(Button.class, name).getBooleanValue()) {
            List<Restaurant> selected = mySubCategories.get(category);
            myActiveSelections.get(category).addAll(selected);

            for (Restaurant e : myActiveSelections.get(category)) {
                myMarkers.get(e).addIntersection(category);
            }
        }
        else {
            for (Restaurant e : myActiveSelections.get(category)) {
                myMarkers.get(e).removeIntersection(category);
            }
            myActiveSelections.get(category).clear();
        }
    }

    private void preprocessInput() {
        JSONArray rawData = loadJSONArray("yelp_restaurants_categorized_full.json");
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
                        .rating(getRating(o.getDouble("rating")))
                        .reviewCount(getReviewCt(o.getDouble("review_count")))
                        .location(coord.getFloat("latitude"),
                                coord.getFloat("longitude"));

            Restaurant complete = restaurant.build();
            addToAppropriateSets(complete, complete.getType(),
                    complete.getRating(), complete.getReviewCount());
        }
    }

    /**
     * <p>Adds an {@link Restaurant} <code>r</code> to the appropriate slot in
     * the entry map. If <code>e</code> is first, initialize the slot.</p>
     *
     * @param e An instance of {@link Restaurant}.
     */
    private void addToAppropriateSets(Restaurant e, RestaurantCategory... categories) {
        addToAppropriateSets(e, Arrays.asList(categories));
    }

    private void addToAppropriateSets(Restaurant e, List<RestaurantCategory> categories) {
        for (RestaurantCategory category : categories) {
            if (mySubCategories.get(category) == null) {
                mySubCategories.put(category, new LinkedList<Restaurant>());
            }
            else {
                mySubCategories.get(category).add(e);
            }
        }
    }

    /**
     * <p>Returns the <code>RestaurantType</code> for {@link JSONObject}
     * <code>entry</code.></p>
     *
     * @param entry The raw <tt>JSON</tt> object as provided by the yelp API.
     * @param categories Different categories for <code>entry</code>.
     *
     * @throws IllegalStateException If <code>entry</code> has an unrecognizable
     *      restaurant type.
     *
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

    public RestaurantReviewCount getReviewCt(double count) {
        RestaurantReviewCount result = null;

        if (count <= 100) {
            result = RestaurantReviewCount.SMALL_COUNT;
        }
        else if (count > 100 && count < 300) {
            result = RestaurantReviewCount.MEDIUM_COUNT;
        }
        else {
            result = RestaurantReviewCount.LARGE_COUNT;
        }
        return result;
    }

    public RestaurantRating getRating(double rating) {
        RestaurantRating result = null;

        if (rating == 3.0) {
            result = RestaurantRating.THREE;
        }
        else if (rating == 3.5) {
            result = RestaurantRating.THREE_POINT_FIVE;
        }
        else if (rating == 4.0) {
            result = RestaurantRating.FOUR;
        }
        else if (rating == 4.5) {
            result = RestaurantRating.FOUR_POINT_FIVE;
        }
        else {
            throw new IllegalArgumentException("Unknown rating: " + rating
                    + ". Use the range (3.0 - 4.5) with increments of .5.");
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