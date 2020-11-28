package api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * This interface represents a directional weighted graph.
 * The interface has a road-system or communication network in mind -
 * and should support a large number of nodes (over 100,000).
 * The implementation should be based on an efficient compact representation
 */
public class DWGraph_DS implements directed_weighted_graph, Serializable {
    public HashMap<Integer,node_data> graphNodes;
    public HashMap<Integer, HashMap<Integer, edge_data>> graphEdges;
    private int edgesTotal, nodesTotal, MC;

    /* CONSTRUCTORS */
    public DWGraph_DS() {
        this.graphNodes = new HashMap<>(1000000);
        this.graphEdges = new HashMap<>(10000000);
        this.edgesTotal = 0;
        this.nodesTotal = 0;
        this.MC = 0;
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Complexity: O(1).
     * @param n
     */
    @Override
    public void addNode(node_data n) {
        if (!graphNodes.containsKey(n.getKey())){
            graphNodes.put(n.getKey(), n);
            graphEdges.put(n.getKey(),new HashMap<>());
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
        return graphNodes.getOrDefault(key, null);
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
                return graphEdges.get(src).get(dest);
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
    public void connect(int src, int dest, double w) { //TODO: check if update the edge
        if (src != dest){
            if (getNode(src) != null && getNode(dest) != null){
                edge_data edge = getEdge(src,dest);
                if (edge == null){
                    graphEdges.get(src).put(dest, new EdgeData(src, dest, w));
                    edgesTotal++;
                    MC++;
                }
            }
        }
    }

    /*HELPFUL METHODS*/
    /**
     * Resets the graph vertices data (tag and info)
     */
    public void clear(){
        Collection<node_data> vertices = getV();
        for (node_data currNode : vertices){
            currNode.setInfo("WHITE");
            currNode.setTag(0);
        }
//        System.out.println("cleared");
    }

    /**
     * Display WGraph
     * @return WGraphs display
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Vertices = " + graphNodes.values().toString() + ", Edges = ");
        for (HashMap<Integer, edge_data> h : graphEdges.values()) {
            Collection<edge_data> c = h.values();
            if (!c.isEmpty())
                result.append(c).append(", ");
        }
        return result.toString();
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     * Complexity: O(1).
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_data> getV() {
        return graphNodes.values();
    }

    /**
     * Returns a copy of the graph
     * @return graph's copy
     */
    public directed_weighted_graph copy() {
        DWGraph_DS newGraph = new DWGraph_DS();

        Collection<node_data> vertices = getV();
        for (node_data currNode : vertices) {
            NodeData newNode = new NodeData(currNode);
            newGraph.addNode(newNode);
        }

        for (node_data currNode : vertices) {
            Collection<edge_data> edges = graphEdges.get(currNode.getKey()).values();
            for (edge_data edgeInfo : edges)
                newGraph.connect(edgeInfo.getSrc(), edgeInfo.getDest(), edgeInfo.getWeight());
        }

        newGraph.edgesTotal = this.edgesTotal;
//        newGraph.MC = this.getMC(); // Todo: I think that we have to delete this line
        return newGraph;
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
        return graphEdges.get(node_id).values();
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
        if (removedNode == null) {
            System.out.println("ERR The vertex do not exist");
            return null;
        } else {
            Collection<edge_data> neighbors = getE(removedNode.getKey());
            for (edge_data currEdge : neighbors) //TODO: fix for the edges
                removeEdge(removedNode.getKey(), currEdge.getDest());

            graphNodes.remove(key,removedNode);
            graphEdges.remove(key);
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
                graphEdges.get(src).remove(dest,removedEdge);
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
                graphNodes.equals(that.graphNodes) &&
                graphEdges.equals(that.graphEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphNodes, graphEdges, edgesTotal, nodesTotal);
    }
}
