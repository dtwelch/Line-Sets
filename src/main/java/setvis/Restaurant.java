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

import de.fhpotsdam.unfolding.geo.Location;

/**
 * <p>A <code>Restaurant</code> encapsulates all relevant metadata pulled
 * from the {@link processing.data.JSONObject} given by the <tt>yelpAPI</tt>
 * search results.</p>
 *
 * @author dwelch <dtw.welch@gmail.com>
 */
public class Restaurant {

    public static enum RestaurantType implements Category {
        AMERICAN {

            @Override protected String[] getValidSynonyms() {
                return new String[] { "cajun", "american", "hotdogs" };
            }

            @Override public Integer getColor() {
                return 0xFFF2003C;
            }
        },
        ITALIAN {

            @Override protected String[] getValidSynonyms() {
                return new String[] { "italian", "pizza" };
            }

            @Override public Integer getColor() {
                return 0xFF984EA3;
            }
        },
        ASIAN {

            @Override protected String[] getValidSynonyms() {
                return new String[] { "korean", "japanese", "chinese",
                        "vietnamese", "asianfusion" };
            }

            @Override public Integer getColor() {
                return 0xFF4DAF4A;
            }
        },
        MEXICAN {

            @Override protected String[] getValidSynonyms() {
                return new String[] { "spanish", "mexican" };
            }

            @Override public Integer getColor() {
                return -13079376;
            }
        };

        protected abstract String[] getValidSynonyms();

        public boolean acceptableFor(String candidate) {
            String[] synonyms = getValidSynonyms();

            for (String synonym : synonyms) {
                if (candidate.toLowerCase().contains(synonym)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getCategoryDescription() {
            return "restaurant type";
        }
    }

    public static enum RestaurantRating implements Category {

        THREE {

            @Override public Integer getColor() {
                return 0xFFBF5B17;
            }
        },
        THREE_POINT_FIVE {

            @Override public Integer getColor() {
                return 0xFF38D44E;
            }
        },
        FOUR {

            @Override public Integer getColor() {
                return 0x38D44E;
            }
        },
        FOUR_POINT_FIVE {

            @Override public Integer getColor() {
                return 0xFF38D44E;
            }
        };

        @Override public String getCategoryDescription() {
            return "rating";
        }
    }

    private final String myName, myID;
    private final Location myLocation;

    private final RestaurantType myType;
    private final RestaurantRating myRating;

    private Restaurant(RestaurantBuilder builder) {
        myName = builder.myName;
        myID = builder.myID;
        myLocation = builder.myLocation;
        myType = builder.myType;
        myRating = builder.myRating;
    }

    public Location getLocation() {
        return myLocation;
    }

    public RestaurantType getType() {
        return myType;
    }

    public RestaurantRating getRating() {
        return myRating;
    }

    public int hashCode() {
        return myID.hashCode();
    }

    @Override public String toString() {
        return myName + " : " + myID;
    }

    public boolean equals(Object o) {
        boolean result = (o instanceof Restaurant);

        if (result) {
            result = ((Restaurant) o).myID.equals(myID);
        }
        return result;
    }

    public static class RestaurantBuilder implements Builder<Restaurant> {

        private String myName, myID;
        private Location myLocation;

        private RestaurantType myType;
        private RestaurantRating myRating;

        public RestaurantBuilder(String name) {
            myName = name;
        }

        public RestaurantBuilder location(float latitude, float longitude) {
            return location(new Location(latitude, longitude));
        }

        public RestaurantBuilder location(Location location) {
            myLocation = location;
            return this;
        }

        public RestaurantBuilder id(String id) {
            myID = id;
            return this;
        }

        public RestaurantBuilder type(RestaurantType type) {
            myType = type;
            return this;
        }

        public RestaurantBuilder rating(RestaurantRating rating) {
            myRating = rating;
            return this;
        }

        public RestaurantBuilder rating(double rating) {
            if (rating == 3.0) {
                myRating = RestaurantRating.THREE;
            }
            else if (rating == 3.5) {
                myRating = RestaurantRating.THREE_POINT_FIVE;
            }
            else if (rating == 4.0) {
                myRating = RestaurantRating.FOUR;
            }
            else if (rating == 4.5) {
                myRating = RestaurantRating.FOUR_POINT_FIVE;
            }
            else {
                throw new IllegalArgumentException("Unknown rating: " + rating
                        + ". Use the range (3.0 - 4.5) with increments of .5.");
            }
            return this;
        }

        @Override public Restaurant build() {
            if (myType == null) {
                throw new IllegalStateException("Null restaurant type. All"
                        + " restaurants must have a type.");
            }

            if (myLocation == null) {
                throw new IllegalStateException("Null location. All"
                        + " restaurants must have a valid location.");
            }
            return new Restaurant(this);
        }
    }
}
