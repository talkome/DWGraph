 DWGraph
 ======

 This project represents an infrastructure of 
 algorithms for creating of a directed weighted positive graph.

 ## NodeData 
 This class represents the set of operations applicable to a 
 vertex in an undirected weighted positive graph.
 * every node contain: unique key, tag and info.
 
  ## EdgeData 
This interface represents the set of operations applicable to a 
directional edge in a directional weighted graph.
  * every edge contain: src, dest, tag, info and weight.  
 
 ## DWGraph_DS
 This class represents a directional weighted positive graph. 
 * support over 10^6 vertices, with an average degree of 10.
 * the graph's nodes saved in Hashmap.
 * the graph's edges saved in Hashmap.

 ## WGraph_Algo
 This interface represents an Undirected Positive Weighted Graph Theory algorithms including:
 0. clone().
 1. init(graph).
 2. isConnected().
 3. double shortestPathDist(int src, int dest).
 4. List<node_info> shortestPath(int src, int dest).
 5. Save(JSON file).
 6. Load(JSON file).
 
 ## Save
 * Saves the graph we are working on as a JSON file.
 * Usually the graph will be saved in the data package in our project.
   
 ## Load
 * Loads the graph we saved from the repository as JSON file.
 
 ## Clone
 * Compute a deep copy of the graph.
 * create a new graph which contain a copy of each node with the same data.
 * Complexity: O(|V|).
  
 ## isConnected
* Returns true if and only if (iff) there is a valid path from each node to each other node.
* Returns true if and only if (iff) there is a valid path from EVERY node to each other node, 
  Using the BFS algorithm we will mark each vertex that can be reached in the graph.
  Then we will activate BFS on the transpose graph.
  Finally, count all the marked vertices,finally if all the vertices are marked then the graph is linked.
* Complexity: O(|V|+|E|) + O(|V|) +  O(|V|+|E|) + O(|V|) = O(|V|+|E|).
* running BFS on the graph, then pass on the vertices of the graph and count the marked vertex, after create a transpose graph and run BFS on the transpose graph.
  and then pass on the vertices of the graph and count the marked vertex.
   
 ## The Shortest Path Distance 
 * Returns the length of the shortest path between src to dest 
   using BFS we find the distance of the shortest path to each vertex from the source vertex 
   then return the distance of the target vertex.
 * Complexity: O(|V|+|E|) 
 * running BFS and return the dest vertex's distance. 
  
 ## The Shortest Path List
 * Returns the shortest path between src to dest as an ordered List of nodes: src--> n1--> n2--> ... -> dest.
   If no such path - returns null.
 * Calls the Dijkstra method to check if there exists a pathway between both of the given nodes.
   If the Dijkstra function returned a positive number, then adds all the numbers in the info of
   the destination node to the array (by calling the isNumeric method).
   Then adds the destination node to the list and returns the path.
   Return the source if both of the src and dest are equals.
 * Complexity: O(|E| + |V|log|V|).
 
 ## BFS Algorithm
 * The Breadth-first search (BFS) is an algorithm for traversing or searching
   tree or graph data structures. It starts at the given node in the graph,
   and explores all the neighbor nodes at the present depth prior to moving on
   to the nodes at the next depth level.
 * Complexity: O(|V|+|E|).
 
  ## Dijkstra Algorithm
  * A famous algorithm for finding the shortest paths in a weighted positive graph.
  *  The algorithm put the given vertex in the priority queue, priority queue sort the vertices by they tag value.
     For each vertex we sum the current vertex's tag with his connected edge's weight.
     Each time we poll vertex with the minimal value in the priority queue,
     go over all its neighbors, select the neighbor with the minimal value and put it in the priority queue
     mark all the vertex we passed.
     If there is a path with a minimal weight we will discover it and select this path
     each vertex we finished passing out of the priority queue.
  * Complexity: O(|E|log|V| + |V|).


Pokémon Game
======

PGame
=====
* This class represents the engine behind the game which uses the 
  "server for moving the "Agents" and place the Pokémon on the graph
  
## game algorithm
1. Creates a list that will contain all the pokemon in the game.
2. Creates a priority queue that will contain all the pokemon in the game.
   The priority queue ranks the pokemon by their values from the greater to the lesser.
3. Locates all the agents in the graph,
   the first agent locates in the closest node to the pokemon with the greatest value and etc.
4. locates the current agent in the nearest node to the pokemon.
5.  If there are more agents than pokemon, then divides the number of nodes in the graph by 2
    and then locates the agent in the graph.
6. Creates an ArrayList which will contain the sleep time of each of the agents.
7. Takes an agent from the agents list. Checks if the agent is at a node, if it is gives him a new destination.
8. Finds the nearest pokemon with the greatest value.
9. If all the pokemon have already been targeted, then the agent will stay at the same node
10. Finds the dest of the nearest node to the target.
11. Determines which node will be the next destination.
12. Sets a new destination for the current agent.
13. Determines the thread sleep.

## PGameFrame
* This class represents the Pokémon game graphic UI base on 
  JFrame drawing the main graph include nodes and edges and also 
  drawing Pokémon and agents based on game server information.

## Arena
This class represents a multi Agents Arena which moves on a graph - 
grabs Pokémon and avoid the Zombies.

## Agent
This class represents the agents in the game.
The agent rule is to collect all the pokemon in the game.
Each agent has:
 * speed - which represents the speed of the agent.
 * The speed is changed according to the number of pokemon who eats, the greater the value, the greater the speed.
 * pos - which represents the position of the agent in the graph.
 * currEdge - which represents the current agent's edge.
 * currNode - which represents the current agent's node.
 * value - which represents the agent's value, which sums the value of tall the pokemon that the agent ate.
 * targetPokemonsList - which contains the ID of all the pokemon  that the agent is chasing.

## Pokemon
This class represents the pokemon in the game.
Each pokemon has:
 * value - which represents the pokemon's value.
 * edge - which represents the current pokemon's edge.
 * type - which represents the pokemon's type.
 * If the type is 1: the pokemon is on the edge from  the lesser node to the greater node,
 * (according to the ID's value of the node).
 * If the type is -1: the pokemon is on the edge from  the greater node to the lesser node.
 * pos - a 3D point that represents the position of the agent in the graph.
 * currNode - which represents the current agent's node.
 * id - which represents the pokemon's ID, which combines its coordinates.

 
 ## How to run ##
 1. Click the green Clone or Download button on the right. 
 2. Click the Download ZIP button. 
 3. Open the project on your computer.
 4. Run tests classes on MyTest folder.
 
 ## Sources ##
 * https://www.softwaretestinghelp.com/dijkstras-algorithm-in-java/
 * https://www.coursera.org/lecture/advanced-data-structures/core-dijkstras-algorithm-2ctyF
