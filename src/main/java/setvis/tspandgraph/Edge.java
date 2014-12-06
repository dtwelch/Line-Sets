package setvis.tspandgraph;

public class Edge<V extends Comparable<V>> {

    protected final V mySource, myDestination;

    public Edge(V source, V destination) {
        mySource = source;
        myDestination = destination;
    }

    public V getSource() {
        return mySource;
    }

    public V getDestination() {
        return myDestination;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Edge<?>))
            return false;

        Edge<?> e = (Edge<?>) obj;
        return (mySource == null && e.mySource == null || mySource != null
                && e.mySource.equals(mySource))
                && (myDestination == null && e.myDestination == null || myDestination != null
                        && e.myDestination.equals(myDestination));
    }

    @Override
    public int hashCode() {
        return mySource.hashCode() ^ (myDestination.hashCode() << 1);
    }

    @Override
    public String toString() {
        return "(" + mySource + "," + myDestination + ")";
    }
}
