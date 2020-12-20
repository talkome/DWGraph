package api;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class DWGraphSerialization {

    @SerializedName("Nodes")
    Collection<NodeData> nodes;
    @SerializedName("Edges")
    Collection<EdgeData> edges;

    public DWGraphSerialization(Collection<NodeData> nodes, Collection<EdgeData> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public DWGraphSerialization serialization() {
        return new DWGraphSerialization(nodes,edges);
    }
}
