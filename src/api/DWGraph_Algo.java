package api;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This interface represents a Directed (positive) Weighted Graph Theory Algorithms including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected(); // strongly (all ordered pais connected)
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<node_data> shortestPath(int src, int dest);
 * 5. Save(file); // JSON file
 * 6. Load(file); // JSON file
 * @author ko tal
 */
public class DWGraph_Algo implements dw_graph_algorithms{
    final double INFINITY = Double.MAX_VALUE;
    public DWGraph_DS myGraph;

    public DWGraph_Algo() {
        this.myGraph = new DWGraph_DS();
    }

    public DWGraph_Algo(directed_weighted_graph g) {
        init(g);
    }

    /**
     * Init the graph on which this set of algorithms operates on.
     * @param g
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.myGraph = (DWGraph_DS) g;
    }

    /**
     * Return the underlying graph of which this class works.
     * @return
     */
    @Override
    public directed_weighted_graph getGraph() {
        return myGraph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     * @return
     */
    @Override
    public directed_weighted_graph copy() {
        return myGraph.copy();
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     * @return
     */
    @Override
    public boolean isConnected() {
        Collection<node_data> vertices = myGraph.getV();
        int count = 0, size = vertices.size();
        if (myGraph == null)
            return false;
        else if (vertices.size() == 0)
            return true;
        int first = vertices.iterator().next().getKey();
        DFS(first);
        for (node_data neighbor : this.getGraph().getV())
            if (neighbor.getTag() == 1){
                count++;
            } else {
                myGraph.clear();
                return false;
            }
        myGraph.clear();
        return (size == count);
    }

    /**
     * This function set the weight, and the tag to 0
     * @param src
     */
    private void setWeightsToMax(int src) {
        Collection <node_data> nodes = this.myGraph.getV();
        for (node_data node : nodes) {
            node.setWeight(INFINITY); //Max weight
            node.setTag(0); //unmarked
        }
        myGraph.getNode(src).setWeight(0); // set the Weight of the source to 0
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     * @param src - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest)
            return 0;
        Dijkstra(src);
        if (this.getGraph().getNode(dest).getInfo().equals("WHITE")) {
            System.out.println("this graph is not connected");
            this.myGraph.clear();
            return -1;
        } else {
            double result = this.getGraph().getNode(dest).getTag();
            this.myGraph.clear();
            return result;
        }
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     * @param src - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        double dist = shortestPathDist(src, dest);
        if (dist > 0) {
            ArrayList<node_data> result = new ArrayList<>();
            Dijkstra(src);
            int i = src, next = 0;
            while (this.getGraph().getNode(i).getKey() != dest) {
                result.add(this.getGraph().getNode(i));
                Collection<edge_data> neighbors = myGraph.getE(this.getGraph().getNode(i).getKey());
                double minVal = this.getGraph().getNode(i).getTag();
                for (edge_data currEdge : neighbors) {
                    node_data currNode = this.myGraph.getNode(currEdge.getDest());
                    double val = currNode.getTag();
                    if (minVal > val) {
                        minVal = val;
                        next = currNode.getKey();
                    }
                }
                i = next;
            }
            result.add(myGraph.getNode(dest));
            this.myGraph.clear();
            return result;
        } else {
            System.out.println("there is not path between the vertices");
            return null;
        }
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        boolean result = false;
        try{
            FileOutputStream stream = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(myGraph); // save the graph (binary) to the file
            result = true;
            out.close();
            stream.close();

        } catch (FileNotFoundException ex) {
            System.out.println("file not found");
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        DWGraph_DS load_graph = null;
        boolean result = false;
        try{
            FileInputStream stream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            load_graph = (DWGraph_DS) inputStream.readObject();
            result = true;
            inputStream.close();
            stream.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
        } catch (IOException ex){
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* HELPFUL TOOLS */
//    @Override
//    public boolean equals(Object o) {
//
//    }

    @Override
    public String toString() {
        return myGraph.toString();
    }

    /*ALGORITHMS TOOLS*/
    private void Dijkstra(int src){
        PriorityQueue<node_data> priorityQueue = new PriorityQueue<>(myGraph.nodeSize(), new Node_Comparator());
        node_data startNode = myGraph.getNode(src);
        startNode.setTag(0);
        priorityQueue.add(startNode);

        while (!priorityQueue.isEmpty()){
            node_data currNode = priorityQueue.poll();
            double currWeight = currNode.getWeight();

            if (currNode.getTag() == 0){ // if node is not visited
                currNode.setTag(1); // marked

                Collection<edge_data> edges = myGraph.getE(currNode.getKey());
                for (edge_data edge : edges) {
                    node_data neighbor = myGraph.getNode(edge.getDest());
                    double neighborWeight = neighbor.getWeight();
                    double edgeWeight = edge.getWeight();

                    if (currWeight + edgeWeight < neighborWeight){
                        neighbor.setWeight(currWeight + edgeWeight);
                        neighbor.setInfo(currNode.getKey() + "");

                        if (neighbor.getTag() == 0)
                            priorityQueue.add(neighbor);
                    }
                }
            }
        }
    }

    public void DFS(int src) {
        node_data currNode = myGraph.getNode(src);
        currNode.setTag(1); // check
        for (edge_data neighbor : myGraph.getE(src))
            if (myGraph.getNode(neighbor.getDest()).getTag() == 0)
                DFS(neighbor.getDest());
    }
}
