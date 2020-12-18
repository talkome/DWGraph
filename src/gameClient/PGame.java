package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PGame implements Runnable {
    private game_service server;
    private DWGraph_Algo graph_algo;
    private static PGameFrame frame;
    private static Arena arena;

    public static void main(String[] args) {
        Thread client = new Thread(new PGame(1));
        client.start();
    }

    /*
    -------------------------------------------------------------------------------------------------
    Game initializing
    -------------------------------------------------------------------------------------------------
    */
    public PGame(int level){
        this.server = Game_Server_Ex2.getServer(level);
        //Logging in
//        int id = 626262;
//        game.login(id);
        this.graph_algo = new DWGraph_Algo();
        graph_algo.load(server.getGraph());
        init();
    }

    /**
     * The method gets a game service and initialize the graph and the agents before the game is starting
     */
    private void init() {
        String pokemons = server.getPokemons();
        arena = new Arena();
        arena.setGraph(graph_algo.getGraph());

        //Creates a list which will contain all the pokemons in the game.
        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(pokemons);
        arena.setPokemons(pokemonsList);
        frame = new PGameFrame("OOP Ex2");
        frame.setSize(1000, 700);
        frame.update(arena);
        frame.show();
        String info = server.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject object = line.getJSONObject("GameServer");
            int numOfAgents = object.getInt("agents");

            /*
            Creates a priority queue which will contain all the pokemons in the game.
            The priority queue ranks the pokemons by their values from the greater to the lesser.
            */
            PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokimon_Comparator());

            //Moves all the pokemons from the list to the PQ
            pokemonsPQ.addAll(pokemonsList);

            /*
            Locates all the agents in the graph,
            the first agent locates in the closest node to the pokemon with the greatest value and etc.
            */
            int avgNode = graph_algo.getGraph().edgeSize();
            for (int i = 0; i < numOfAgents; i++) {
                if (pokemonsPQ.size() > 0) {
                    CL_Pokemon currentPokemon = pokemonsPQ.poll();
                    int pokemonSrc = getPokemonSrc(currentPokemon);

                    //locates the current agent in the nearest node to the pokemon.
                    server.addAgent(pokemonSrc);
                }
                /*
                    If there are more agents than pokemons, then divides the number of nodes in the graph by 2
                    and then locates the agent in the graph.
                 */
                else {
                    avgNode = avgNode / 2;
                    server.addAgent(avgNode);
                }
            }

            //Prints the agents details
            System.out.println(server.getAgents());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        server.startGame();
        frame.setTitle("Ex2 - OOP " + server.toString());
        int ind = 0;

        //Initialize an ArrayList that contains all the targeted pokemons.
        List<CL_Pokemon> targetedPokemons = new ArrayList<>();

        //Keep running while the game is on
        while (server.isRunning()) {
            moveAgents(targetedPokemons);
            try {
                if (ind % 1 == 0)
                    frame.repaint();
                Thread.sleep(200);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String game_str = server.toString();

        System.out.println(game_str);
        System.exit(0);
    }

    /*
    -------------------------------------------------------------------------------------------------
    Functions
    -------------------------------------------------------------------------------------------------
    */

    /**
     * The method gets a game and a graph and moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen by
     * an algorithm which find the most value pokemon in his area.
     * @param targetedPokemons
     */
    private void moveAgents(List<CL_Pokemon> targetedPokemons) {
        // update game graph
        String updatedGraph = getUpdateGraph();

        // update agents list
        List<CL_Agent> newAgentsList = getUpdateAgents(updatedGraph);

        // update pokemons list
        List<CL_Pokemon> newPokemonsList = getUpdatePokemons();

        for (CL_Agent currentAgent : newAgentsList) {

            //Takes an agent from the agentList.
            //Checks if the agent is at a node, if it is gives him a new destination.
            if (currentAgent.getNextNode() == -1) {

                //Finds the nearest pokemon with the greatest value.
                CL_Pokemon target = getNearestPokemon(currentAgent, targetedPokemons, newPokemonsList);

                //If all the pokemons have already been targeted, then the agent will stay at the same node
                if (target == null) {
                    moveAgents();
                    return;
                }

                //Finds the dest of nearest node to the target.
                int pokemon_dest = getPokemonDest(currentAgent, target);

                //Calculates which node will be the next destination
                int newDest = nextNode(currentAgent, pokemon_dest);

                //Sets a new destination for the current agent.
                server.chooseNextEdge(currentAgent.getID(), newDest);

                //Prints the agent move
                printAgentMove(currentAgent, newDest);
            }
        }
    }

    /**
     * Prints a message if the agent did not move.
     */
    private void moveAgents() {
        System.out.println("None of the agents moved, ");
        System.out.println("All the pokemons have already been targeted.");
    }

    /**
     * Prints the agents move (if he moved).
     *
     * @param currentAgent the agent
     * @param newDest      the new agent's distance
     */
    private void printAgentMove(CL_Agent currentAgent, int newDest) {
        //Agent details
        int agentID = currentAgent.getID();
        double agentValue = currentAgent.getValue();
        int agentSrc = currentAgent.getSrcNode();
        System.out.println("Agent: " + agentID + ", value: " + agentValue + " is moving from node " + agentSrc + " to node: " + newDest);
    }

    /**
     * Returns the update pokemons list and set the pokemons in the arena.
     * @return the update pokemons list
     */
    private List<CL_Pokemon> getUpdatePokemons() {
        String pokemons = server.getPokemons();
        List<CL_Pokemon> newPokemonsList = Arena.json2Pokemons(pokemons);
        for (CL_Pokemon currentPok : newPokemonsList) {
            Arena.updateEdge(currentPok, graph_algo.getGraph());
        }
        arena.setPokemons(newPokemonsList);
        System.out.println("Pokemon info:" + newPokemonsList.toString());
        System.out.println("Pokemon Edge: " + newPokemonsList.get(0).get_edge());

        return newPokemonsList;
    }

    /**
     * Returns the update agents list.
     *
     * @param updatedGraph the updated graph
     * @return an update agent list
     */
    private List<CL_Agent> getUpdateAgents(String updatedGraph) {
        List<CL_Agent> newAgentsList = Arena.getAgents(updatedGraph, graph_algo.getGraph());
        arena.setAgents(newAgentsList);
        return newAgentsList;
    }

    /**
     * Gets the update graph.
     * @return the update graph
     */
    private String getUpdateGraph() {
        String updatedGraph = server.move();
        System.out.println(updatedGraph);
        return updatedGraph;
    }

    /**
     * The function gets an agent and a graph and returns the nearest pokemon with the greatest value,
     * by compute the value/the distance.
     *
     * @param agent            the agent
     * @param targetedPokemons the targeted pokemon
     * @param pokemonsList     the pokemonList
     * @return the nearest pokemon with the greatest value
     */
    private CL_Pokemon getNearestPokemon(CL_Agent agent, List<CL_Pokemon> targetedPokemons, List<CL_Pokemon> pokemonsList) {
        int srcNode = agent.getSrcNode();
        CL_Pokemon result = null;
        double distance, maxScore = 0;

        /*
        Iterates all the pokemons in the game that is not targeted yet,
        And checks which pokemon has the greatest valueForDistance.
         */
        for (CL_Pokemon currentPokemon : pokemonsList) {

            //Checks if the current pokemon is not targeted already.
            if (!targetedPokemons.contains(currentPokemon)) {
                int pokemonDest = getPokemonDest(agent, currentPokemon);
                distance = graph_algo.shortestPathDist(srcNode, pokemonDest);
                if (distance > -1) {
                    double score = getValueForDistance(distance, currentPokemon);
                    if (score > maxScore) {
                        maxScore = score;
                        result = currentPokemon;
                    }
                }
            }
        }

        //Marks the pokemon as targeted (if found one) by adding it to the targeted list.
        if (result != null)
            targetedPokemons.add(result);

        //Returns the targeted pokemon.
        return result;
    }

    /**
     * The functions gets a distance and a pokemon and returns the quotient of the distance/the speed
     * of the pokemon.
     *
     * @param distance       the distance
     * @param currentPokemon the pokemon
     * @return the quotient of the distance/the speed of the pokemon
     */
    private static double getValueForDistance(double distance, CL_Pokemon currentPokemon) {
        return currentPokemon.getValue() / distance;
    }

    /**
     * The function gets a pokemon and a graph and returns the nearest src node to the pokemon
     *
     * @param currentPokemon the pokemon
     * @return the nearest node to the pokemon
     */
    private int getPokemonSrc(CL_Pokemon currentPokemon) {
        /*
            Checks the direction of the edge by its type:
            If the type is positive then the pokemon goes from the lesser to the greater node,
            so takes the minimum between src and dest.
            Else the pokemon goes from the greater to the lesser node,
            so takes the maximum between src and dest.
             */
        Arena.updateEdge(currentPokemon, graph_algo.getGraph());
        edge_data pokemonEdge = currentPokemon.get_edge();
        int pokemonSrc;
        if (currentPokemon.getType() > 0) {
            pokemonSrc = Math.min(pokemonEdge.getSrc(), pokemonEdge.getDest());
        } else {
            pokemonSrc = Math.max(pokemonEdge.getSrc(), pokemonEdge.getDest());
        }
        return pokemonSrc;
    }

    /**
     * The function gets a pokemon and a graph and returns the nearest dest node to the pokemon
     *
     * @param agent          the agent
     * @param currentPokemon the pokemon
     * @return the nearest node to the pokemon
     */
    private int getPokemonDest(CL_Agent agent, CL_Pokemon currentPokemon) {
        /*
        Checks the direction of the edge by its type:
        If the type is positive then the pokemon goes from the lesser to the greater node,
        so takes the minimum between src and dest.
            Else the pokemon goes from the greater to the lesser node,
            so takes the maximum between src and dest.
            Then checks if the Agent has to go to a greater node,
            if yes, then the pokemonDest will be the greatest node in the edge of the pokemon.
            Otherwise, the pokemonDest will be the lesser node in the edge of the pokemon.
             */

        Arena.updateEdge(currentPokemon, graph_algo.getGraph());
        edge_data pokemonEdge = currentPokemon.get_edge();
        int[] destArr = new int[2];
        int pokemonDest, alternativeDest, result;
        if (currentPokemon.getType() > 0) {
            pokemonDest = Math.max(pokemonEdge.getSrc(), pokemonEdge.getDest());
            alternativeDest = Math.min(pokemonEdge.getSrc(), pokemonEdge.getDest());
        } else {
            pokemonDest = Math.min(pokemonEdge.getSrc(), pokemonEdge.getDest());
            alternativeDest = Math.max(pokemonEdge.getSrc(), pokemonEdge.getDest());
        }
        destArr[0] = pokemonDest;
        destArr[1] = alternativeDest;

        if (destArr[0] < agent.getSrcNode())
            result = destArr[0];
        else
            result = destArr[1];

        return result;
    }

    /**
     * The function gets a pokemon and a graph and returns the next step towards that pokemon (the new dest).
     *
     * @param agent the agent
     * @param dest  the nearest node to the target pokemon
     * @return the new destination of agent
     */
    private int nextNode(CL_Agent agent, int dest) {
        int src = agent.getSrcNode();
        System.out.println("src = " + src);
        System.out.println("from " + src + " to " + dest + ": " + graph_algo.shortestPath(src, dest).toString());
        System.out.println("next dest = " + graph_algo.shortestPath(src, dest).get(1).getKey());
        return graph_algo.shortestPath(src, dest).get(1).getKey();
    }
}