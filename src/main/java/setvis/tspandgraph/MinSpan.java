package setvis.tspandgraph;

public interface MinSpan<V extends Comparable<V>, E extends WeightedEdge<V>> {

    Graph<V, E> findMinimumSpan();
}
