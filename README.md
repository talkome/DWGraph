 DWGraph
 ======

 this project represents an infrastructure of 
 algorithms for creating of directed weighted positive graph

 ## NodeData 
 This class represents the set of operations applicable on a 
 vertex in an undirected weighted positive graph.
 * every node contain: unique key, tag and info 
 
  ## EdgeData 
  This class represents the set of operations applicable on an 
  edge in an undirected weighted positive graph.
  * every edge contain: unique key, tag and info 
 
 ## DWGraph_DS
 This class represents an undirected weighted positive graph. 
 * support over 10^6 vertices, with average degree of 10.
 * the graph's nodes saved in Hashmap 
 * the graph's edges saved in Hashmap 

 ## WGraph_Algo
 This interface represents an Undirected Positive Weighted Graph Theory algorithms including:
 0. clone();
 1. init(graph);
 2. isConnected();
 3. double shortestPathDist(int src, int dest);
 4. List<node_info> shortestPath(int src, int dest);
 5. Save(file);
 6. Load(file);
 
 ## Save
 * Let's save the graph we are working on as JSON file 
 * usually the graph will be saved in the data package in our project 
   
 ## Load
 * Let's load the graph we saved from the repository as JSON file 
 
 ## Clone
 * Compute a deep copy of the graph.
 * create a new graph which contain a copy of each node with the same data
 * Complexity: O(|V|) 
  
 ## isConnected
 * Returns true if and only if (iff) there is a valid path from EVERY node to each other node, 
   using the BFS algorithm we will mark each vertex that can be reached in the graph
   then we will count all the marked vertices,finally if all the vertices are marked then the graph is linked.
 * Complexity: O(|V|+|E|) + O(|V|) = O(|V|+|E|)
 * running BFS and then pass on the graphs vertices and count the marked vertex 
   
 ## The Shortest Path Distance 
 * Returns the length of the shortest path between src to dest 
   using BFS we find the distance of the shortest path to each vertex from the source vertex 
   then return the distance of the target vertex.
 * Complexity: O(|V|+|E|) 
 * running BFS and return the dest vertex's distance. 
  
 ## The Shortest Path List 
 * Returns the shortest path between src to dest 
   as an ordered List of nodes: src--> n1--> n2--> ... -> dest 
 * We will run BFS from the destination vertex 
   then we will start from the source vertex 
   in each iteration we choose the vertex with the minimal distance from the vertex's neighbors 
   until we reach the destination vertex
 * Complexity: O(|V|+|E|) + O(|V|) = O(|V|+|E|)
 * running BFS and then start pass from the src node, in every iteration we select the 
 node with the minimal distance and move to it until we reach the destination vertex.
 
 ## BFS Algorithm
 * A famous algorithm for calculating paths in a graph.
 * Put the given vertex in the stack,
   for each vertex in the stack we will take out the vertex at the top of the stack,
   and mark the space of each vertex as the distance from the vertex we reached plus one,
   each vertex we finished passing out of the stack
 * Complexity: O(|V|+|E|) 
 
  ## Dijkstra Algorithm
  * A famous algorithm for finding the shortest paths in a weighted positive graph.
  * Put the given vertex in the priority queue,
      priority queue sort the vertices by they tags value, 
      for each vertex we sum the current vertex's tag with his connected edge's weight, 
      each time we poll vertex with the minimal value in the priority queue 
      we go over all its neighbors, select the neighbor with the minimal value and put it in the priority queue
      mark all the vertex we passed,
      if there is a path with a minimal weight we will discover it and select this path
      each vertex we finished passing out of the priority queue
  * Complexity: O(|E|log|V| + |V|)


Pokemon Game
======

## PGame
* repres

## PGameFrame

 
 ## How to run 
 * Click the green Clone or Download button on the right. 
 * Click the Download ZIP button. 
 * Open the project on your computer 
 * run tests classes on MyTest folder
 
 ## Sources
 * https://www.softwaretestinghelp.com/dijkstras-algorithm-in-java/
 * https://www.coursera.org/lecture/advanced-data-structures/core-dijkstras-algorithm-2ctyF
 
 

