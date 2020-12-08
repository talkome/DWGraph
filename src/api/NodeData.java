package api;

import java.io.Serializable;
import java.util.Objects;

/**
 * This interface represents the set of operations applicable on a
 * node in a directional weighted graph.
 * @author ko tal
 */
public class NodeData implements node_data, Serializable {
    static int id;
    private int key, tag;
    private geo_location pos;
    private double w,sinker;
    private String info;

    public NodeData() {
        this.key = id++;
        this.tag = 0;
        this.w = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.pos = new GeoLocation();
    }

    public NodeData(int key) {
        this.key = key;
        this.tag = 0;
        this.w = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.pos = new GeoLocation();
    }

    public NodeData(node_data other) {
        this.key = other.getKey();
        this.tag = other.getTag();
        this.w = other.getW();
        this.info = other.getInfo();
        this.pos = other.getPos();
    }

    public NodeData(NodeData other) {
        this.key = other.getKey();
        this.tag = other.getTag();
        this.w = other.getW();
        this.sinker = other.sinker;
        this.info = other.getInfo();
        this.pos = other.getPos();
    }

    public NodeData(int key, GeoLocation pos) {
        this.key = key;
        this.tag = 0;
        this.w = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.pos = new GeoLocation(pos);

    }

    /**
     * Display a vertex
     * @return vertex display
     */
    @Override
    public String toString() {
        return "{\"pos\":" + pos + ",\"id\":"+key+"}";
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
    public geo_location getPos() {
        return pos == null ? null : pos;
    }

    /** Allows changing this node's location.
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setPos(geo_location p) {
        pos = new GeoLocation(p);
    }

    /**
     * Returns the weight associated with this node.
     * @return
     */
    @Override
    public double getW() {
        return w;
    }

    /**
     * Allows changing this node's weight.
     * @param w - the new weight
     */
    @Override
    public void setW(double w) {
        this.w = w;
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

    public double getSinker() {
        return sinker;
    }

    public void setSinker(double sinker) {
        this.sinker = sinker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeData)) return false;
        NodeData nodeData = (NodeData) o;
        return getKey() == nodeData.getKey() &&
                getTag() == nodeData.getTag() &&
                Double.compare(nodeData.getW(), getW()) == 0 &&
                getPos().equals(nodeData.getPos()) &&
                getInfo().equals(nodeData.getInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getTag(), getPos(), getW(), getInfo());
    }
}
