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

import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.geo.*;
import processing.core.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>A <code>RestaurantMarker</code> is a point marker designed to illustrate
 * intersection points via concentric, colored circles.</p>
 *
 * @author dwelch <dtw.welch@gmail.com>
 */
public class RestaurantMarker extends SimplePointMarker {

    private Set<Category> myActiveIntersections = new LinkedHashSet<>();
    protected String myName;
    protected int space = 10;

    private float fontSize = 12;

    public RestaurantMarker(Restaurant restaurant) {
        this(restaurant.getLocation(), restaurant.getName());
    }

    public RestaurantMarker(Location location, String name) {
        this.location = location;
        myName = name;
    }

    public String getName() {
        return myName;
    }

    public void addIntersection(Category category) {
        myActiveIntersections.add(category);
    }

    public void removeIntersection(Category category) {
        myActiveIntersections.remove(category);
    }

    /**
     * <p>Displays both this markers name and the active intersections
     * textually (pop up box) and visually (concentric, colored circles).</p>
     *
     * @param pg
     * @param x An x coordinate.
     * @param y A y coordinate.
     */
    public void draw(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.pushMatrix();

        if (selected) { pg.translate(0, 0); }

        int initialSize = 13;
        for (Category category : myActiveIntersections) {
            pg.noFill();
            pg.strokeWeight(5);
            pg.stroke(category.getColor());
            pg.ellipse(x, y, initialSize, initialSize);
            initialSize += 10;
        }
        pg.strokeWeight(strokeWeight);
        if (selected) {
            pg.fill(highlightColor);
            pg.stroke(highlightStrokeColor);
        } else {
            pg.fill(color);
            pg.stroke(strokeColor);
        }
        pg.ellipse(x, y, 7, 7);

        // label
        if (selected && myName != null) {
            pg.fill(130, 130, 130, 150);
            pg.noStroke();
            pg.rect(10 + x + strokeWeight / 2,
                    y - fontSize + strokeWeight / 2 - space,
                    pg.textWidth(myName) + space * 1.5f,
                    fontSize + space, 4);
            pg.fill(255, 255, 255);
            pg.text(myName, Math.round(10+x + space * 0.75f + strokeWeight / 2),
                    Math.round(y + strokeWeight / 2 - space * 0.75f));
        }
        pg.popMatrix();
        pg.popStyle();
    }
}
