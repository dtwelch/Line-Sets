/* This file is Copyright 2014 Dan Welch
 * 
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

public interface SubCategory {

    public Integer getColor();

    public String getParentCategoryDescriptor();
}
