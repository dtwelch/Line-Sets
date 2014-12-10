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
import de.fhpotsdam.unfolding.data.MarkerFactory;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
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

    private final Map<Category, List<Restaurant>> mySubCategories =
            new HashMap<>();

    private final Map<Category, List<Restaurant>> myActiveSelections =
            new HashMap<>();

    private final Map<Restaurant, Set<Category>> myIntersectCategories =
            new HashMap<>();

    private final Map<Restaurant, RestaurantMarker> myMarkers =
            new HashMap<>();

    private ControlP5 myControls;

    private UnfoldingMap myBackgroundMap;
    private float plotX1, plotY1, plotX2, plotY2;

    @Override public void setup() {
        size(700, 600);
        plotX1 = 0; plotY1 = 0; plotX2 = width; plotY2 = 60;

        createMapBackground();

        createCategoryControlPanels();
        preprocessInput();
        computeAndUpdateRestaurantOrderings();
        createRestaurantMarkers();
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

    @Override public void draw() {
        myBackgroundMap.draw();

        drawActiveCurves();
        drawRestaurantMarkers();
        //drawActiveCurveIntersections();
        //updateIntersections();
        drawCategoryPanels();
        clearIntersectCategoryMap();
    }

    private void drawRestaurantMarkers() {
        for (Marker m : myBackgroundMap.getMarkers()) {
            m.draw(myBackgroundMap);
        }
    }

    private void updateIntersections() {

    }

    private void drawActiveCurveIntersections() {
        for (Restaurant e : findActiveIntersections()) {
            drawConcentricIntersectGlyph(e);
            //drawRestaurantMarkers(); //maybe helps with occulsion?
        }
    }

    private void drawConcentricIntersectGlyph(Restaurant e) {

        int ringSize = 15;
        for (Category category : myIntersectCategories.get(e)) {
            //System.out.println("GETS HERE: " + category);

            stroke(category.getColor());
            strokeWeight(6);
            ellipse(toScreenPosition(e).x, toScreenPosition(e).y,
                    ringSize, ringSize);
            ringSize += 11;
        }
        //System.out.println("---");
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

    private Set<Restaurant> findActiveIntersections() {

        Set<Restaurant> allActive = new HashSet<>();
        Set<Restaurant> activeRestaurantTypes = new HashSet<>();
        Set<Restaurant> activeRestaurantRatings = new HashSet<>();
        Set<Restaurant> activeRestaurantPrices = new HashSet<>();

        //first compute the union of all active restaurant types.
        //i.e. active(American) U .. U active(Mexican)
        for (Map.Entry<Category, List<Restaurant>> entry :
                myActiveSelections.entrySet()) {

            if (entry.getKey() instanceof RestaurantType) {
                activeRestaurantTypes.addAll(entry.getValue());
            }
            else if (entry.getKey() instanceof RestaurantRating) {
                activeRestaurantRatings.addAll(entry.getValue());
            }
            allActive.addAll(entry.getValue());
        }

        allActive.retainAll(activeRestaurantTypes);
        for (Restaurant e : allActive) {
            addToIntersectCategoryMap(e, e.getType());
        }
        allActive.retainAll(activeRestaurantRatings);
        for (Restaurant e : allActive) {
            addToIntersectCategoryMap(e, e.getRating());
        }
        return allActive;
    }

    private void clearIntersectCategoryMap() {
        for (Map.Entry<Restaurant, Set<Category>> entry :
                myIntersectCategories.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().clear();
            }
        }
    }

    //generalize this with the other method that adds to the subcategory map..
    private void addToIntersectCategoryMap(Restaurant e, Category category) {
        if (myIntersectCategories.get(e) == null) {
            myIntersectCategories.put(e, new LinkedHashSet<Category>());
        } else {
            myIntersectCategories.get(e).add(category);
        }
    }

    /**
     * <p>Draws a smooth curve through every point in each of the subcategories
     * selected -- maintained by <code>myActiveSelections</code>.</p>
     */
    private void drawActiveCurves() {

        for (Map.Entry<Category, List<Restaurant>> e : myActiveSelections
                .entrySet()) {
            List<Restaurant> curRestaurants = e.getValue();

            if (curRestaurants != null && !curRestaurants.isEmpty()) {
                ScreenPosition first = toScreenPosition(curRestaurants.get(0));
                ScreenPosition last = toScreenPosition(curRestaurants
                        .get(curRestaurants.size() - 1));

                beginShape();
                stroke(e.getKey().getColor());
                strokeWeight(7);
                noFill();
                curveVertex(first.x, first.y);

                for (Restaurant r : curRestaurants) {
                    ScreenPosition curPosition = toScreenPosition(r);
                    curveVertex(curPosition.x, curPosition.y);
                    //strokeWeight(5);
                    //ellipse(curPosition.x, curPosition.y, 12, 12);
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
        myBackgroundMap = new UnfoldingMap(this, new Google.GoogleMapProvider());
        myBackgroundMap.zoomAndPanTo(12, new Location(47.626, -122.337));
        myBackgroundMap.zoomToLevel(13);
        myBackgroundMap.setBackgroundColor(0);

        MapUtils.createDefaultEventDispatcher(this, myBackgroundMap);
        myBackgroundMap.setTweening(true);
    }

    /**
     * <p></p>
     */
    private void computeAndUpdateRestaurantOrderings() {
        for (Map.Entry<Category, List<Restaurant>> e : mySubCategories
                .entrySet()) {
            computeAndUpdateRestaurantOrderings(e.getKey());
        }
    }

    private void computeAndUpdateRestaurantOrderings(Category category) {
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

        /*List<Restaurant> ordering =
                HamiltonianCycle.getApproximateOptimalForCompleteGraph(g);

        for (Restaurant r : ordering) {
            mySubCategories.get(category).add(r);
        }*/
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
        rect(plotX1 + 390, plotY1 + 10, 130, plotY2 - 5, 6);
        fill(240);
        text("Price", plotX1 + 395, plotY1 + 25);
    }

    public void createCategoryControlPanels(){
        myControls =
                new ControlP5(this, createFont("Helvetica-Bold", 8));
        frameRate(25);

        Gui.createRestaurantTypeButtons(myControls, plotX1, plotY1);
        Gui.createRestaurantRatingButtons(myControls, plotX1, plotY1);
        //Gui.createRestaurantPriceButtons(myControls, plotX1, plotY1);
    }

    private void american(int theValue) {
        updateActiveSelection("american", RestaurantType.AMERICAN);
    }

    private void italian(int theValue) {
        updateActiveSelection("italian", RestaurantType.ITALIAN);
    }

    private void asian(int theValue) {
        updateActiveSelection("asian", RestaurantType.ASIAN);
    }

    private void mexican(int theValue) {
        updateActiveSelection("mexican", RestaurantType.MEXICAN);
    }

    private void three(int theValue) {
        updateActiveSelection("three", RestaurantRating.THREE);
    }

    private void threePointFive(int theValue) {
        updateActiveSelection("threePointFive",
                RestaurantRating.THREE_POINT_FIVE);
    }

    private void four(int theValue) {
        updateActiveSelection("four", RestaurantRating.FOUR);
    }

    private void fourPointFive(int theValue) {
        updateActiveSelection("fourPointFive", RestaurantRating.FOUR_POINT_FIVE);
    }

    private void updateActiveSelection(String name, Category category) {

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
                        .rating(o.getDouble("rating"))
                        .location(coord.getFloat("latitude"),
                                coord.getFloat("longitude"));

            Restaurant complete = restaurant.build();
            addToAppropriateSets(complete, complete.getType(),
                    complete.getRating());
        }
    }

    /**
     * <p>Adds an {@link Restaurant} <code>r</code> to the appropriate slot in
     * the entry map. If <code>e</code> is first, initialize the slot.</p>
     *
     * @param e An instance of {@link Restaurant}.
     */
    private void addToAppropriateSets(Restaurant e, Category ... categories) {
        addToAppropriateSets(e, Arrays.asList(categories));
    }

    private void addToAppropriateSets(Restaurant e, List<Category> categories) {
        for (Category category : categories) {
            if (mySubCategories.get(category) == null) {
                mySubCategories.put(category, new LinkedList<Restaurant>());
            } else {
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