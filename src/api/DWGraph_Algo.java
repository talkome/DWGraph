package api;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    private DWGraph_DS myGraph;

    //Creates a HashSet which contains all the visited nodes.
    private HashSet<node_data> visited = new HashSet<>();

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
      /*
        Checks if the graph is not empty, then runs the BFS algorithm on the first node in the graph.
        After that, checks if all of the nodes have been visited by comparing the number of nodes in
        the graph to the number of the nodes that have been marked as visited.
        If they are not equals then return false.
         */
        if (myGraph.nodeSize() > 1) {
            node_data start = myGraph.getNode(0);
            BFS(myGraph, start);
            if (myGraph.nodeSize() == visited.size()){
                DWGraph_DS G_t = (DWGraph_DS) this.myGraph.getTransposeGraph();
                node_data start_t = G_t.getNode(0);
                BFS(G_t,start_t);
                return G_t.nodeSize() == visited.size();
            } else {
                return false;
            }
        }
        return true;
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
        NodeData result;
        if (getGraph().getV().isEmpty() || getGraph().getNode(src) == null)
            return -1;
        if (src == dest || getGraph().nodeSize() == 1)
            return 0;
        Dijkstra(src);
        if (this.getGraph().getNode(dest).getTag() == 0) {
            System.out.println("this graph is not connected");
            return -1;
        } else {
            result = (NodeData) this.getGraph().getNode(dest);
            return result.getSinker();
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
        //Creates an ArrayList which is used to contain the path.
        List<node_data> path = new ArrayList<>();

        //Return the source if both of the src and the dest are equals.
        if (src == dest) {
            path.add(this.getGraph().getNode(src));
            return path;
        }

        /*
        Calls the Dijkstra method to check if there exists a pathway between both of the given nodes.
        If the Dijkstra function returned a positive number, then adds all the numbers in the info of
        the destination node to the array (by calling isNumeric method).
        Then adds the destination node to the list and returns the path.
         */
        if (shortestPathDist(src, dest) > -1) {
            node_data destination = getGraph().getNode(dest);
            String str = destination.getInfo();
            String[] arr = str.split("->");
            for (String temp : arr) {
                if (isNumeric(temp)) {
                    int key = Integer.parseInt(temp);
                    path.add(getGraph().getNode(key));
                }
            }
            path.add(destination);
            return path;
        }
        return null;
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        boolean ans = false;
        Gson gson = new Gson(); //TODO: CHECK IF NECESSARY
        String json = gson.toJson(myGraph.toString());
        System.out.println(json);

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(json);
            pw.close();
            ans = true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("file not found");
        }

        return ans;
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
        boolean result = false;
        DWGraph_DS loadGraph = new DWGraph_DS();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = new Gson();
        try {
            String json = Files.readString(Path.of(file));
            JsonDeserializer<NodeData> deserializer = new JsonDeserializer<NodeData>() {
                @Override
                public NodeData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    int key = jsonObject.get("id").getAsInt();
                    String location_str = jsonObject.get("pos").getAsString();
                    NodeData node = new NodeData(key);
                    geo_location location = gson.fromJson(location_str,GeoLocation.class);
                    node.setLocation(location);
                    return node;
                }
            };

            gsonBuilder.registerTypeAdapter(NodeData.class,deserializer);
            Gson gson1 = gsonBuilder.create();
            NodeData node = gson1.fromJson(json,NodeData.class);
            loadGraph.graphNodes.put(node.getKey(),node);
        } catch (IOException e) {
            e.printStackTrace();
        }









//        int src = jsonArray.get(0).getAsJsonObject().get("src").getAsInt();
//        int dest = jsonArray.get(0).getAsJsonObject().get("dest").getAsInt();
//        double weight = jsonArray.get(0).getAsJsonObject().get("w").getAsDouble();
//        EdgeData edge = new EdgeData(src,dest,weight);



        try{
            FileInputStream stream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            result = true;
            inputStream.close();
            stream.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
        } catch (IOException ex){
            System.out.println("IOException is caught");
        }
        return result;
    }

    /* HELPFUL TOOLS */
    /**
     * The method gets a string and checks if its contains a number
     *
     * @param str a string
     * @return true id the string contains a number
     */
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getGraph().toString();
    }

    /*ALGORITHMS TOOLS*/
    private void Dijkstra(int src){
        myGraph.clear();
        PriorityQueue<NodeData> priorityQueue = new PriorityQueue<>(getGraph().nodeSize(), new Node_Comparator());
        NodeData startNode = (NodeData) getGraph().getNode(src);
        startNode.setTag(0);
        startNode.setSinker(0);
        priorityQueue.add(startNode);

        while (!priorityQueue.isEmpty()){
            NodeData currNode = priorityQueue.poll();
            double currNodeSinker = currNode.getSinker();
            if (currNode.getTag() == 0){ // if node is not visited

                Collection<edge_data> edges = getGraph().getE(currNode.getKey());
                for (edge_data edge : edges) {
                    NodeData neighbor = (NodeData) getGraph().getNode(edge.getDest());
                    double edgeWeight = edge.getWeight();
                    if (currNodeSinker + edgeWeight < neighbor.getSinker()){
                        neighbor.setSinker(currNodeSinker + edgeWeight);
                        String key = String.valueOf(currNode.getKey());
                        neighbor.setInfo(currNode.getInfo() + "->" + key);
                        if (neighbor.getTag() == 0)
                            priorityQueue.add(neighbor);
                    }
                }
                currNode.setTag(1); // marked
            }
        }
    }

    /**
     * The Breadth-first search (BFS) is an algorithm for traversing or searching
     * tree or graph data structures. It starts at the given node in the graph,
     * and explores all of the neighbor nodes at the present depth prior to moving on
     * to the nodes at the next depth level.
     *
     * @param g      the graph which the search will run.
     * @param source the node from which the search will start.
     */
    private void BFS(directed_weighted_graph g, node_data source) {
        //Clears the visited HashSet.
        visited.clear();

        /*
        Creates a queue which will contain the nodes that need to traverse (by their order).
         */
        Queue<node_data> queue = new LinkedList<>();
        queue.add(source);

        /*
        While the queue is not empty, the algorithm takes the first node and traverses all its neighbors.
        If this neighbor is not yet visited, it adds to the queue and marks as visited.
        After the algorithm finishes gaining with all the neighbors, it marks the current node as visited
        and continues to the next node in the queue.
         */
        while (!queue.isEmpty()) {
            node_data current = queue.poll();
            for (edge_data neighbor : g.getE(current.getKey())) {
                node_data temp = g.getNode(neighbor.getDest());
                if (!visited.contains(temp)) {
                    queue.add(temp);
                    visited.add(temp);
                }
            }
            visited.add(current);
        }
    }
}
