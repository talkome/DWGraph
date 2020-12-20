package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;

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
    private geo_location location;
    private double weight,sinker;
    private String info;

    public NodeData() {
        this.key = id++;
        this.tag = 0;
        this.weight = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.location = new Point3D(0,0,0);
    }

    public NodeData(int key) {
        this.key = key;
        this.tag = 0;
        this.weight = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.location = new Point3D(0,0,0);
    }

    public NodeData(node_data other) {
        this.key = other.getKey();
        this.tag = other.getTag();
        this.weight = other.getWeight();
        this.info = other.getInfo();
        this.location = other.getLocation();
    }

    public NodeData(NodeData other) {
        this.key = other.getKey();
        this.tag = other.getTag();
        this.weight = other.getWeight();
        this.sinker = other.sinker;
        this.info = other.getInfo();
        this.location = other.getLocation();
    }

    public NodeData(int key, geo_location location) {
        this.key = key;
        this.tag = 0;
        this.weight = 0;
        this.sinker = Double.MAX_VALUE;
        this.info = null;
        this.location = new Point3D(location.x(),location.y(),location.z());
    }

    /**
     * Display a vertex
     * @return vertex display
     */
    @Override
    public String toString() {
        if (info == null)
            return "V" + key + "(w = " + weight + ", s = " + sinker + ", pos = " + location + ", t = " + tag + ")";
        else
            return "V" + key + "(w = " + weight + ", s = " + sinker + ", pos = " + location + ", t = " + tag + ", i = " + info + ")";
    }

    public String toJSON(){
        JsonObject result = new JsonObject();
        result.addProperty("pos",location.toString());
        result.addProperty("id",key);
        Gson gson = new Gson();
        return gson.toJson(result).replaceAll("\"","\"\\");
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
        location = new Point3D(p.x(),p.y(),p.z());
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
     * @param weight - the new weight
     */
    @Override
    public void setWeight(double weight) {
        this.weight = weight;
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
    public int hashCode() {
        return Objects.hash(getKey(), getTag(), getLocation(), getWeight(), getInfo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NodeData))
            return false;
        NodeData nodeData = (NodeData) o;
        return getKey() == nodeData.getKey() &&
                getTag() == nodeData.getTag() &&
                Double.compare(nodeData.getWeight(), getWeight()) == 0 &&
                Double.compare(nodeData.getSinker(), getSinker()) == 0 &&
                getLocation().equals(nodeData.getLocation()) &&
                getInfo().equals(nodeData.getInfo());
    }
}
