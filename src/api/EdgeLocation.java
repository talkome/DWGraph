package api;

public class EdgeLocation implements edge_location{
    private edge_data edge;
    private double ratio;

    public EdgeLocation(edge_data edge) {
        this.edge = edge;
        this.ratio = 0;
    }

    public EdgeLocation(edge_data edge, double ratio) {
        this.edge = edge;
        this.ratio = ratio;
    }

    @Override
    public edge_data getEdge() {
        return edge;
    }

    @Override
    public double getRatio() {
        return ratio;
    }
}
