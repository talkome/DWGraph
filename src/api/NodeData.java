package api;

/**
 * This interface represents the set of operations applicable on a
 * node in a directional weighted graph.
 * @author ko tal
 */
public class NodeData implements node_data{
    static int id;
    private int key, tag;
    private geo_location location;
    private double weight;
    private String info;

    public NodeData() {
        this.key = id++;
        this.tag = 0;
        this.weight = Double.POSITIVE_INFINITY;
        this.info = "WHITE";
    }

    public NodeData(int key) {
        this.key = key;
        this.tag = 0;
        this.weight = Double.POSITIVE_INFINITY;
        this.info = "WHITE";
    }

    public NodeData(node_data other) {
        this.key = other.getKey();
        this.tag = other.getTag();
        this.weight = other.getWeight();
        this.info = other.getInfo();
        this.location = other.getLocation();
    }

    /**
     * Display a vertex
     * @return vertex display
     */
    @Override
    public String toString() {
        return "V" + key + "(w = " + weight + ", t= " + tag + ", i= " + info + ")";
    }

    /**
     * Returns the key associated with this node.
     * @return key
     */
    @Override
    public int getKey() {
        return key;
    }

    /** Returns the location of this node, if
     * none return null.
     * @return location
     */
    @Override
    public geo_location getLocation() {
        return location == null ? null : location;
    }

    /** Allows changing this node's location.
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        location = p;
    }

    /**
     * Returns the weight associated with this node.
     * @return
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Allows changing this node's weight.
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     * @return
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     * @param s
     */
    @Override
    public void setInfo(String s) {
        info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     * @return
     */
    @Override
    public int getTag() {
        return tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        tag = t;
    }
}
