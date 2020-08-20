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

import de.fhpotsdam.unfolding.geo.Location;

/**
 * <p>A <code>Restaurant</code> encapsulates all relevant metadata pulled
 * from the {@link processing.data.JSONObject} given by the <tt>yelpAPI</tt>
 * search results.</p>
 *
 * @author dwelch <dtw.welch@gmail.com>
 */
public class Restaurant {

    public static enum RestaurantType implements RestaurantCategory {
        AMERICAN {

            @Override
            protected String[] getValidSynonyms() {
                return new String[] { "cajun", "american", "hotdogs" };
            }

            @Override
            public Integer getAssignedColor() {
                return 0xFF1b9e77;
            }
        },
        ITALIAN {

            @Override
            protected String[] getValidSynonyms() {
                return new String[] { "italian", "pizza" };
            }

            @Override
            public Integer getAssignedColor() {
                return 0xFFF2003C;
            }
        },
        ASIAN {

            @Override
            protected String[] getValidSynonyms() {
                return new String[] { "korean", "japanese", "chinese",
                        "vietnamese", "asianfusion" };
            }

            @Override
            public Integer getAssignedColor() {
                return 0xFF1f78b4;
            }
        },
        MEXICAN {

            @Override
            protected String[] getValidSynonyms() {
                return new String[] { "spanish", "mexican" };
            }

            @Override
            public Integer getAssignedColor() {
                return 0xFFb2df8a;
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

    public enum RestaurantRating implements RestaurantCategory {

        THREE {

            @Override
            public Integer getAssignedColor() {
                return 0xFFfb9a99;
            }
        },
        THREE_POINT_FIVE {

            @Override
            public Integer getAssignedColor() {
                return 0xFF6a3d9a;
            }
        },
        FOUR {

            @Override
            public Integer getAssignedColor() {
                return 0xFFfdbf6f;
            }
        },
        FOUR_POINT_FIVE {

            @Override
            public Integer getAssignedColor() {
                return 0xFFff7f00;
            }
        };

        @Override
        public String getCategoryDescription() {
            return "rating";
        }
    }

    public enum RestaurantReviewCount implements RestaurantCategory {

        SMALL_COUNT {

            @Override
            public Integer getAssignedColor() {
                return 0xFF33a02c;
            }
        },
        MEDIUM_COUNT {

            @Override
            public Integer getAssignedColor() {
                return 0xFFb15928;
            }
        },
        LARGE_COUNT {

            @Override
            public Integer getAssignedColor() {
                return 0xFFb2df8a;
            }
        };

        @Override
        public String getCategoryDescription() {
            return "review count";
        }
    }

    private final String myName, myID;

    private final Location myLocation;

    private final RestaurantReviewCount myReviewCount;
    private final RestaurantType myType;
    private final RestaurantRating myRating;

    private Restaurant(RestaurantBuilder builder) {
        myName = builder.myName;
        myID = builder.myID;
        myLocation = builder.myLocation;

        myType = builder.myType;
        myRating = builder.myRating;
        myReviewCount = builder.myReviewCount;
    }

    public String getName() {
        return myName;
    }

    public Location getLocation() {
        return myLocation;
    }

    public RestaurantReviewCount getReviewCount() {
        return myReviewCount;
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

    @Override
    public String toString() {
        return myName + " : " + myID;
    }

    @Override
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

        private RestaurantReviewCount myReviewCount;
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

        public RestaurantReviewCount getReviewCount() {
            return myReviewCount;
        }

        public RestaurantBuilder type(RestaurantType type) {
            myType = type;
            return this;
        }

        public RestaurantBuilder reviewCount(RestaurantReviewCount count) {
            myReviewCount = count;
            return this;
        }

        public RestaurantBuilder rating(RestaurantRating rating) {
            myRating = rating;
            return this;
        }

        @Override
        public Restaurant build() {
            if (myType == null || myRating == null || myReviewCount == null) {
                throw new IllegalStateException("Null category detected. All"
                        + " categories must be non-null.");
            }

            if (myLocation == null) {
                throw new IllegalStateException("Null location. All"
                        + " restaurants must have a non-null location.");
            }
            return new Restaurant(this);
        }
    }
}
