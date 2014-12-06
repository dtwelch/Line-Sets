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
package setvis.tspandgraph;

import java.util.Collection;
import java.util.Set;

public interface Graph<V extends Comparable<V>, E extends Edge<V>> {

    /**
     * <p>Add <code>vertex</code> to the graph.</p>
     *
     * @param vertex A node/vertex to add
     *
     * @throws NullPointerException if <code>vertex</code> is <code>null</code>.
     * @return <code>true</code> if <code>vertex</code> was not already present.
     */
    public boolean addVertex(V vertex);

    public boolean addVertices(Collection<V> vertices);

    /**
     * <p>Adds edge e to the graph.</p>
     *
     * @param e The edge to add.
     * @throws IllegalArgumentException If <code>e</code> is not a valid edge
     *          (e.g. it refers to vertices not in the graph).
     *
     * @throws NullPointerException If <pre>e == null</pre>.
     *
     * @return <code>true</code> if <code>e</code> was not already present;
     *      <code>false</code> otherwise.
     */
    public boolean addEdge(E e);

    /**
     * <p>Adds multiple edges to a graph.</p>
     *
     * @throws NullPointerException If <pre>edges == null</pre> or any
     *      <code>edge</code> in the list is <code></code>
     *
     * @throws IllegalArgumentException if any <code>edge</code> in the
     *      collection is invalid.
     *
     * @return <code>true</code> <strong>iff</strong> the set of
     *      edges was changed by the operation.
     */
    public boolean addEdges(Collection<E> edges);

    /**
     * <p>Remove an edge, <pre>(source, destination)</pre> from the graph.</p>
     *
     * @throws NullPointerException if <code>source</code> or
     *      <code>destination</code> is <code>null</code>.
     *
     * @throws IllegalArgumentException if <code>source</code> or
     *      <code>destination</code> is not in the graph.
     *
     * @return <code>true</code> if an edge from <code>source</code> to
     *      <code>dest</code> was present.
     */
    public boolean removeEdge(V source, V destination);

    public Set<V> getVertices();

    public void clearEdges();

    /**
     * <p>Tests if vertices <code>i</code> and <code>j</code> are connected,
     * returning the edge between them if so.</p>
     *
     * @throws IllegalArgumentException if <code>i</code> or <code>j</code> are
     *      not vertices in the graph.
     *
     * @throws NullPointerException if <code>i</code> or <code>j</code> is
     *      <code>null</code>.
     *
     * @return <code>true</code> if <pre>(i, j)</pre> exists in the graph,
     *      <code>false</code> otherwise.
     */
    public E connected(V i, V j);
}
