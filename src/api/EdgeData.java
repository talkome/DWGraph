package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * This interface represents the set of operations applicable on a
 * directional edge(src,dest) in a (directional) weighted graph.
 * @author ko tal
 *
 */
public class EdgeData implements edge_data, Serializable {
    private int src,dest,tag;
    private double weight;
    private String info = null;

    public EdgeData(int src, int dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.tag = 0;
        this.weight = weight;
    }

    public EdgeData(EdgeData other) {
        this.src = other.getSrc();
        this.dest = other.getDest();
        this.tag = other.getTag();
        this.weight = other.getWeight();
        this.info = other.getInfo();
    }

    /*TOOLS*/
    /**
     * Print edge display
     * @return edge display
     */
    @Override
    public String toString() {
        if (info == null)
            return "E[" + src + "," + dest + "](w=" + weight + ", t= " + tag + ")";
         else
            return "E[" + src + "," + dest + "](w=" + weight + ", t= " + tag + ",i=" + info + ")";
    }
//
//    public String toJSON(){
//        JsonObject result = new JsonObject();
//        result.addProperty("src",src);
//        result.addProperty("w",weight);
//        result.addProperty("dest",dest);
//        Gson gson = new Gson();
//        return gson.toJson(result);
//    }

    /**
     * The id of the source node of this edge.
     * @return
     */
    @Override
    public int getSrc() {
        return src;
    }

    /**
     * The id of the destination node of this edge
     * @return
     */
    @Override
    public int getDest() {
        return dest;
    }

    /**
     * @return the weight of this edge (positive value).
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the remark (meta data) associated with this edge.
     * @return
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this edge.
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
     * This method allows setting the "tag" value for temporal marking an edge - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        tag = t;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSrc(), getDest(), getTag(), getWeight(), getInfo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeData)) return false;
        EdgeData edgeData = (EdgeData) o;
        return getSrc() == edgeData.getSrc() &&
                getDest() == edgeData.getDest() &&
                getTag() == edgeData.getTag() &&
                Double.compare(edgeData.getWeight(), getWeight()) == 0 &&
                getInfo().equals(edgeData.getInfo());
    }
}
