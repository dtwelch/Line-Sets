package setvis.gui;

import controlP5.Button;
import controlP5.ControlP5;
import processing.core.PApplet;
import setvis.Restaurant;
import setvis.Restaurant.RestaurantType;
import setvis.Restaurant.RestaurantRating;

public class Gui {

    public static void createRestaurantTypeButtons(ControlP5 cp5, float plotX1,
            float plotY1) {
        Button american = cp5.addButton("American");
        american.setPosition(plotX1 + 20, plotY1 + 35).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.AMERICAN.getSubCategoryColor())
                .setSwitch(true);

        Button italian = cp5.addButton("Italian");
        italian.setPosition(plotX1 + 20 + 110, plotY1 + 35).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.ITALIAN.getSubCategoryColor())
                .setSwitch(true);

        Button asian = cp5.addButton("Asian");
        asian.setPosition(plotX1 + 20, plotY1 + 60).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.ASIAN.getSubCategoryColor())
                .setSwitch(true);

        Button mexican = cp5.addButton("Mexican");
        mexican.setPosition(plotX1 + 20 + 110, plotY1 + 60).setSize(100, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantType.MEXICAN.getSubCategoryColor())
                .setSwitch(true);
    }

    public static void createRestaurantRatingButtons(ControlP5 cp5,
            float plotX1, float plotY1) {
        Button three = cp5.addButton("3");
        three.setPosition(plotX1 + 260, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.THREE.getSubCategoryColor())
                .setSwitch(true).setValueLabel("Three").getCaptionLabel()
                .align(PApplet.CENTER, PApplet.CENTER);

        Button threePointFive = cp5.addButton("3.5");
        threePointFive
                .setPosition(plotX1 + 290, plotY1 + 35)
                .setSize(20, 20)
                .setColorBackground(0xFF414141)
                .setColorForeground(0xFF5A5A5A)
                .setColorActive(
                        RestaurantRating.THREE_POINT_FIVE.getSubCategoryColor())
                .setSwitch(true).setValueLabel("ThreePointFive")
                .getCaptionLabel().align(PApplet.CENTER, PApplet.CENTER);

        Button four = cp5.addButton("4.0");
        four.setPosition(plotX1 + 320, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.FOUR.getSubCategoryColor())
                .setSwitch(true).setValueLabel("Four").getCaptionLabel().align(
                        PApplet.CENTER, PApplet.CENTER);

        Button fourPointFive = cp5.addButton("4.5");
        fourPointFive.setPosition(plotX1 + 350, plotY1 + 35).setSize(20, 20)
                .setColorBackground(0xFF414141).setColorForeground(0xFF5A5A5A)
                .setColorActive(RestaurantRating.FOUR.getSubCategoryColor())
                .setSwitch(true).setValueLabel("FourPointFive")
                .getCaptionLabel().align(PApplet.CENTER, PApplet.CENTER);
    }
}
