package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This interface represents a directional weighted graph.
 * The interface has a road-system or communication network in mind -
 * and should support a large number of nodes (over 100,000).
 * The implementation should be based on an efficient compact representation
 */
public class DWGraph_DS implements directed_weighted_graph, Serializable {
    public HashMap<Integer,node_data> Nodes;
    public HashMap<Integer, HashMap<Integer, edge_data>> Edges;
    private int edgesTotal, nodesTotal, MC;

    /* CONSTRUCTORS */
    public DWGraph_DS() {
        this.Nodes = new HashMap<>(1000000);
        this.Edges = new HashMap<>(10000000);
        this.edgesTotal = 0;
        this.nodesTotal = 0;
        this.MC = 0;
    }

    public DWGraph_DS(ArrayList<NodeData> nodes, ArrayList<EdgeData> edges) {
        this.Nodes = new HashMap<>(1000000);
        this.Edges = new HashMap<>(10000000);
        this.edgesTotal = 0;
        this.nodesTotal = 0;
        this.MC = 0;
        convertNodes(nodes);
        convertEdges(edges);
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Complexity: O(1).
     * @param n
     */
    @Override
    public void addNode(node_data n) {
        if (!Nodes.containsKey(n.getKey())){
            Nodes.put(n.getKey(), n);
            Edges.put(n.getKey(),new HashMap<>());
            nodesTotal++;
            MC++;
        }
    }

    /**
     * returns the node_data by the node_id,
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return Nodes.getOrDefault(key, null);
    }

    /**
     * returns the data of the edge (src,dest), null if none.
     * Complexity: O(1).
     * @param src
     * @param dest
     * @return
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (src != dest)
            if (getNode(src) != null && getNode(dest) != null)
                return Edges.get(src).get(dest);
        return null;
    }

    /**
     * Connects an edge with weight w between node src to node dest.
     * Complexity: O(1).
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (src != dest){
            if (getNode(src) != null && getNode(dest) != null){
                edge_data edge = getEdge(src,dest);
                if (edge == null){
                    Edges.get(src).put(dest, new EdgeData(src, dest, w));
                    edgesTotal++;
                    MC++;
                }
            }
        }
    }

    /*HELPFUL METHODS*/
    private void convertEdges(ArrayList<EdgeData> edges) {
        for (EdgeData currEdge : edges)
            this.connect(currEdge.getSrc(),currEdge.getDest(),currEdge.getWeight());
    }

    private void convertNodes(ArrayList<NodeData> nodes) {
        for (NodeData currNode : nodes)
            this.addNode(currNode);
    }

    /**
     * Resets the graph vertices data (tag and info)
     */
    public void clear(){
        HashSet<NodeData> vertices = getV().stream().map(n -> (NodeData) n).collect(Collectors.toCollection(HashSet::new));
        for (NodeData currNode : vertices){
            currNode.setTag(0);
            currNode.setInfo(null);
            currNode.setSinker(Double.POSITIVE_INFINITY);
        }
        System.out.println("cleared");
    }

    /**
     * Display WGraph
     * @return WGraphs display
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{\"Edges\":");
        for (HashMap<Integer, edge_data> h : Edges.values()) {
            Collection<edge_data> c = h.values();
            if (!c.isEmpty())
                result.append(c).append(",");
        }
        result.append("\"Nodes\":").append(Nodes.values().toString()).append("}");
        return result.toString();
    }

//    public JsonObject toJSON() {
//        JsonObject result = new JsonObject();
//        StringBuilder builder = new StringBuilder();
//
//        for (HashMap<Integer, edge_data> h : Edges.values()) {
//            Collection<edge_data> c = h.values();
//            for (edge_data currEdge : c) {
//                EdgeData edge = (EdgeData) currEdge;
//                builder.append(edge.toJSON()).append(",");
//            }
//            if (!c.isEmpty())
//                builder.append(",");
//        }
//        result.addProperty("");
//
//        for (node_data currNode : getV()) {
//            NodeData node = (NodeData) currNode;
//            builder.append(node.toJSON()).append(",");
//        }
//        result.addProperty("Nodes",builder.toString());
//        return result;
//    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     * Complexity: O(1).
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_data> getV() {
        return Nodes.values();
    }

    /**
     * The method returns a collection containing all the nodes connected to node_id.
     * Complexity: O(k) - hash map access complexity is O(1)
     * k which represents the degree of node_id.
     * @param node_id the key(id) of the node.
     * @return Collection<node_data>.
     */
    public Collection<node_data> getV(int node_id) {
        /*
        The function checks if exists any edge which associated to the given node_id If yes,
        then iterates all this edges and adds the neighbors of this node to a new ArrayList,
        then return that list which contains all the neighbors of the node
        */
        ArrayList<node_data> neighborsArray = new ArrayList<>();
        if (Edges.containsKey(node_id)) {
            for (Integer key : Edges.get(node_id).keySet()) {
                neighborsArray.add(getNode(key)); } }
        return neighborsArray; }

    /**
     * Returns a copy of the graph
     * @return graph's copy
     */
    public directed_weighted_graph copy() {
        DWGraph_DS newGraph = new DWGraph_DS();

        Collection<node_data> vertices = getV();
        for (node_data currNode : vertices)
            newGraph.addNode(new NodeData(currNode));

        for (node_data currNode : vertices) {
            Collection<edge_data> edges = Edges.get(currNode.getKey()).values();
            for (edge_data currEdge : edges)
                newGraph.connect(currEdge.getSrc(), currEdge.getDest(), currEdge.getWeight());
        }

        newGraph.edgesTotal = this.edgesTotal;
        newGraph.nodesTotal = this.nodesTotal;
        return newGraph;
    }

    public directed_weighted_graph getTransposeGraph(){
        DWGraph_DS transposeGraph = new DWGraph_DS();

        Collection<node_data> vertices = getV();
        for (node_data currNode : vertices)
            transposeGraph.addNode(new NodeData(currNode));

        for (node_data currNode : vertices) {
            Collection<edge_data> edges = Edges.get(currNode.getKey()).values();
            for (edge_data currEdge : edges)
                transposeGraph.connect(currEdge.getDest(), currEdge.getSrc(), currEdge.getWeight());
        }

        transposeGraph.edgesTotal = this.edgesTotal;
        transposeGraph.nodesTotal = this.nodesTotal;
        return transposeGraph;
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the edges getting out of
     * the given node
     * (all the edges starting (source) at the given node).
     * Note: this method should run in O(k) time, k being the collection size.
     * @return Collection<edge_data>
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        return Edges.get(node_id).values();
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * This method should run in O(V.degree), as all the edges should be removed.
     * @return the data of the removed node (null if none).
     * @param key
     */
    @Override
    public node_data removeNode(int key) {
        node_data removedNode = getNode(key);
        DWGraph_DS G_t = (DWGraph_DS) this.getTransposeGraph();
        if (removedNode == null) {
            System.out.println("ERR The vertex do not exist");
            return null;
        } else {
            Collection<node_data> neighbors = getV(removedNode.getKey());
            for (node_data currNode : neighbors)
                removeEdge(removedNode.getKey(), currNode.getKey());

            Collection<node_data> neighbors2 = G_t.getV(removedNode.getKey());
            for (node_data currNode : neighbors2)
                removeEdge(currNode.getKey(), removedNode.getKey());

            Nodes.remove(key,removedNode);
            Edges.remove(key);
            nodesTotal--;
            MC++;
            return removedNode;
        }
    }

    /**
     * Deletes the edge from the graph,
     * Complexity: O(1).
     * @param src
     * @param dest
     * @return the data of the removed edge (null if none).
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        if (src == dest) {
            System.out.println("ERR cannot remove edge");
            return null;
        } else {
            edge_data removedEdge = getEdge(src,dest);
            if (removedEdge != null) {
                Edges.get(src).remove(dest,removedEdge);
                edgesTotal--;
                MC++;
            } else {
                System.out.println("ERR this edge is already not connected");
                return null;
            }
            return removedEdge;
        }
    }

    /**
     * Returns the number of vertices (nodes) in the graph.
     * Complexity: O(1).
     * @return nodesTotal
     */
    @Override
    public int nodeSize() {
        return nodesTotal;
    }

    /**
     * Returns the number of edges (assume directional graph).
     * Complexity: O(1).
     * @return edgesTotal
     */
    @Override
    public int edgeSize() {
        return edgesTotal;
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     * @return MC
     */
    @Override
    public int getMC() {
        return MC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DWGraph_DS)) return false;
        DWGraph_DS that = (DWGraph_DS) o;
        return edgesTotal == that.edgesTotal &&
                nodesTotal == that.nodesTotal &&
                Nodes.equals(that.Nodes) &&
                Edges.equals(that.Edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Nodes, Edges, edgesTotal, nodesTotal);
    }
}
