package setvis.tspandgraph;

import java.util.*;

public class WeightedGraph<V extends Comparable<V>, E extends WeightedEdge<V>>
        implements
            Graph<V, E> {

    private final Map<V, Set<E>> myGraph;

    public WeightedGraph() {
        this(new LinkedList<V>(), new HashSet<E>());
    }

    public WeightedGraph(Collection<V> vertices, Collection<E> edges) {
        myGraph = new HashMap<>();
    }

    @Override
    public boolean addVertex(V vertex) {
        return false;
    }

    @Override
    public boolean addVertices(Collection<V> vertices) {
        return false;
    }

    @Override
    public boolean addEdge(E e) {
        return false;
    }

    @Override
    public boolean addEdges(Collection<E> edges) {
        return false;
    }

    @Override
    public boolean removeEdge(V source, V destination) {
        return false;
    }

    @Override
    public Set<V> getVertices() {
        return null;
    }

    @Override
    public void clearEdges() {

    }

    @Override
    public E connected(V i, V j) {
        return null;
    }
}
