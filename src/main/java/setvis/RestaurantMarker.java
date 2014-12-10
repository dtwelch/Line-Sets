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

import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PFont;
import processing.core.PGraphics;

public class RestaurantMarker extends SimplePointMarker {

    protected String myTitle;
    protected float size = 15;
    protected int space = 10;

    private PFont font;
    private float fontSize = 12;

    public RestaurantMarker(Restaurant restaurant) {
        this.location = restaurant.getLocation();
        myTitle = restaurant.getName();
    }

    /**
     * Displays this marker's name in a box.
     */
    public void draw(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.pushMatrix();

        if (!this.isHidden()) {
            if (selected) {
                pg.translate(0, 0);
            }
            pg.strokeWeight(strokeWeight);
            if (selected) {
                pg.fill(highlightColor);
                pg.stroke(highlightStrokeColor);
            } else {
                pg.fill(color);
                pg.stroke(strokeColor);
            }
            pg.ellipse(x, y, size, size);// TODO use radius in km and convert to px
        }
        // label
        if (selected && myTitle != null) {
            if (font != null) {
                pg.textFont(font);
            }
            pg.fill(highlightColor);
            pg.stroke(highlightStrokeColor);
            pg.rect(x + strokeWeight / 2, y - fontSize + strokeWeight / 2 -
                            space, pg.textWidth(myTitle) + space * 1.5f,
                    fontSize + space);
            pg.fill(255, 255, 255);
            pg.text(myTitle, Math.round(x + space * 0.75f + strokeWeight / 2),
                    Math.round(y + strokeWeight / 2 - space * 0.75f));
        }
        pg.popMatrix();
        pg.popStyle();
    }

    public String getTitle() {
        return myTitle;
    }
}
