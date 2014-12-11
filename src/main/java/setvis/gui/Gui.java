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
package setvis.gui;

import controlP5.Button;
import controlP5.ControlP5;
import processing.core.PApplet;
import setvis.Restaurant.RestaurantReviewCount;
import setvis.Restaurant.RestaurantType;
import setvis.Restaurant.RestaurantRating;

/**
 * <p>A small collection of static helper methods to help hide some ugliness
 * involved in creating and configuring {@link ControlP5} buttons.</p>
 *
 * @author dwelch <dtw.welch@gmail.com>
 */
public class Gui {

    public static void createRestaurantTypeButtons(ControlP5 cp5, float plotX1,
            float plotY1) {
        Button american = cp5.addButton("american");
        american.setPosition(plotX1 + 20, plotY1 + 35).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.AMERICAN.getColor()).setSwitch(
                        true);

        Button italian = cp5.addButton("italian");
        italian.setPosition(plotX1 + 20 + 110, plotY1 + 35).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.ITALIAN.getColor()).setSwitch(
                        true);

        Button asian = cp5.addButton("asian");
        asian.setPosition(plotX1 + 20, plotY1 + 60).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.ASIAN.getColor())
                .setSwitch(true);

        Button mexican = cp5.addButton("mexican");
        mexican.setPosition(plotX1 + 20 + 110, plotY1 + 60).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.MEXICAN.getColor()).setSwitch(
                        true);
    }

    public static void createRestaurantRatingButtons(ControlP5 cp5,
            float plotX1, float plotY1) {
        Button three = cp5.addButton("three");
        three.setPosition(plotX1 + 260, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.THREE.getColor()).setSwitch(
                        true).setCaptionLabel("3").getCaptionLabel().align(
                        PApplet.CENTER, PApplet.CENTER);

        Button threePointFive = cp5.addButton("threePointFive");
        threePointFive.setPosition(plotX1 + 290, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.THREE_POINT_FIVE.getColor())
                .setSwitch(true).setCaptionLabel("3.5").getCaptionLabel()
                .align(PApplet.CENTER, PApplet.CENTER);

        Button four = cp5.addButton("four");
        four.setPosition(plotX1 + 320, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.FOUR.getColor()).setSwitch(
                        true).setCaptionLabel("4.0").getCaptionLabel().align(
                        PApplet.CENTER, PApplet.CENTER);

        Button fourPointFive = cp5.addButton("fourPointFive");
        fourPointFive.setPosition(plotX1 + 350, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.FOUR_POINT_FIVE.getColor())
                .setSwitch(true).setCaptionLabel("4.5").getCaptionLabel()
                .align(PApplet.CENTER, PApplet.CENTER);
    }

    public static void createRestaurantReviewCountButtons(ControlP5 cp5,
            float plotX1, float plotY1) {
        Button smallReviewCt = cp5.addButton("smallReviewCount");
        smallReviewCt.setPosition(plotX1 + 400, plotY1 + 35).setSize(50, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantReviewCount.SMALL_COUNT.getColor())
                .setSwitch(true).setCaptionLabel("100 .. 100");

        Button mediumReviewCt = cp5.addButton("mediumReviewCount");
        mediumReviewCt.setPosition(plotX1 + 455, plotY1 + 35).setSize(50, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantReviewCount.MEDIUM_COUNT.getColor())
                .setSwitch(true).setCaptionLabel("101 .. 300");

        Button largeReviewCt = cp5.addButton("largeReviewCount");
        largeReviewCt.setPosition(plotX1 + 510, plotY1 + 35).setSize(50, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantReviewCount.LARGE_COUNT.getColor())
                .setSwitch(true).setCaptionLabel("301 .. 800");
    }
}
