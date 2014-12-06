package setvis.tspandgraph;

public class WeightedEdge<V extends Comparable<V>> extends Edge<V>
        implements
            Comparable<WeightedEdge<V>> {

    protected final int myWeight;

    public WeightedEdge(V src, V dest, int weight) {
        super(src, dest);
        myWeight = weight;
    }

    public int getWeight() {
        return myWeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WeightedEdge<?>)) {
            return false;
        }

        WeightedEdge<?> e = (WeightedEdge<?>) obj;
        return e.myWeight == myWeight && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ myWeight;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + ", " + myWeight + "]";
    }

    public int compareTo(WeightedEdge<V> obj) {
        if (this.mySource.compareTo(obj.mySource) == 0) {
            return this.myDestination.compareTo(obj.myDestination);
        }
        else {
            return this.mySource.compareTo(obj.mySource);
        }
    }
}