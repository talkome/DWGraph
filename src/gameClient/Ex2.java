package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Ex2 implements Runnable{
    private static GameFrame gFrame;
    private static Arena arena;

    public static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {

        //Game initializing
        int level_number = 1; //The level of the game [0,24]
        game_service game = Game_Server_Ex2.getServer(level_number);
        System.out.println(game); //Prints the server details
        String gameGraph = game.getGraph();
        System.out.println(gameGraph); //Prints the graph details
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.load(gameGraph);

        //Creates a list which will contain all the pokemons in the game.
        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(game.getPokemons());

        init(game, graph_algo, pokemonsList);

        String agents = game.getAgents();
        //Creates a list which will contain all the agents in the game.
        List<CL_Agent> agentsList = Arena.getAgents(agents, graph_algo.getGraph());

        /*
        -------------------------------------------------------------------------------------------------
        Game Launching
        -------------------------------------------------------------------------------------------------
         */

        game.startGame();
        gFrame.setTitle("Ex2 - OOP " + game.toString());
        int ind = 0;

        //Initialize an ArrayList that contains all the targeted pokemons.
        List<CL_Pokemon> targetedPokemons = new ArrayList<>();

        //Keep running while the game is on
        while (game.isRunning()) {
            moveAgents(game, graph_algo.getGraph(), graph_algo, targetedPokemons, pokemonsList, agentsList);
            try {
                if (ind % 1 == 0)
                    gFrame.repaint();
                Thread.sleep(100);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String game_str = game.toString();

        System.out.println(game_str);
        System.exit(0);
    }

    /*
        -------------------------------------------------------------------------------------------------
        Functions
        -------------------------------------------------------------------------------------------------
    */
    /**
     * The method gets a game service and initialize the graph and the agents before the game is starting
     * @param game the game
     */
    private void init(game_service game, dw_graph_algorithms graph, List<CL_Pokemon> pokemonsList) {
        String pokemons = game.getPokemons();
        arena = new Arena();
        arena.setGraph(graph.getGraph());
        arena.setPokemons(Arena.json2Pokemons(pokemons));
        gFrame = new GameFrame("OOP Ex2");
        gFrame.setSize(1000, 700);
        gFrame.update(arena);
        gFrame.show();
        String info = game.toString();
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
            for (int i = 0; i < numOfAgents; i++) {
                CL_Pokemon currentPokemon = pokemonsPQ.poll();
                int pokemonSrc = getPokemonSrc(currentPokemon, graph.getGraph());

                //locates the current agent in the nearest node to the pokemon.
                game.addAgent(pokemonSrc);
            }
            System.out.println(game.getAgents()); //Prints the agents details
            System.out.println(arena.getAgents());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method gets a game and a graph and moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen by
     * an algorithm which find the most value pokemon in his area.
     * @param game    the game
     * @param graph the graph
     * @param ga
     * @param targetedPokemons
     */
    private void moveAgents(game_service game, directed_weighted_graph graph, dw_graph_algorithms ga, List<CL_Pokemon> targetedPokemons, List<CL_Pokemon> pokemonsList,List<CL_Agent> agentsList) {
        for (CL_Agent currentAgent : agentsList) {

            //Takes an agent from the agentList.
            //Checks if the agent is at a node, if it is gives him a new destination.
            if (currentAgent.getNextNode() == -1) {


                //Finds the nearest pokemon with the greatest value .
                CL_Pokemon target = getNearestPokemon(currentAgent, ga, targetedPokemons,pokemonsList);

                //Finds the dest of nearest node to the target.
                int pokemon_dest = getPokemonDest(target, graph);

                //Calculates which node will be the next destination
                int newDest = nextNode(currentAgent, pokemon_dest, ga);

                //Sets a new destination for the current agent.
                game.chooseNextEdge(currentAgent.getID(), newDest);

                //Agent details
                int agentID = currentAgent.getID();
                double agentValue = currentAgent.getValue();
                int agentSrc = currentAgent.getSrcNode();
                System.out.println("Agent: " + agentID + ", value: " + agentValue + " is moving from " + agentSrc + " to node: " + newDest);
            }

            //Moves all the agents.
            game.move();
        }
    }

    /**
     * The function gets an agent and a graph and returns the nearest pokemon with the greatest value,
     * by compute the value/the distance.
     *
     * @param agent      the agent
     * @param ga the graph
     * @return the nearest pokemon with the greatest value
     */
    private static CL_Pokemon getNearestPokemon(CL_Agent agent, dw_graph_algorithms ga, List<CL_Pokemon> targetedPokemons, List<CL_Pokemon> pokemonsList) {
        int srcNode = agent.getSrcNode();
        CL_Pokemon result = null;
        double distance, minScore = 0;

        /*
        Iterates all the pokemons in the game that is not targeted yet,
        And checks which pokemon has the greatest valueForDistance.
         */
        for (CL_Pokemon currentPokemon : pokemonsList) {

            //Checks if the current pokemon is not targeted already.
            if (!targetedPokemons.contains(currentPokemon)) {
                int pokemonDest = getPokemonDest(currentPokemon, ga.getGraph());
                distance = ga.shortestPathDist(srcNode, pokemonDest);
                if (distance > 0) {
                    double score = getValueForDistance(distance, currentPokemon);
                    if (score > minScore) {
                        minScore = score;
                        result = currentPokemon;
                    }
                }
            }
        }

        //Marks the pokemon as targeted by adding it to the targeted list.
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
     * @param graph        the graph
     * @return the nearest node to the pokemon
     */
    private static int getPokemonSrc(CL_Pokemon currentPokemon, directed_weighted_graph graph) {
        /*
            Checks the direction of the edge by its type:
            If the type is positive then the pokemon goes from the lesser to the greater node,
            so takes the minimum between src and dest.
            Else the pokemon goes from the greater to the lesser node,
            so takes the maximum between src and dest.
             */
        Arena.updateEdge(currentPokemon, graph);
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
     * @param currentPokemon the pokemon
     * @param graph        the graph
     * @return the nearest node to the pokemon
     */
    private static int getPokemonDest(CL_Pokemon currentPokemon, directed_weighted_graph graph) {
        /*
            Checks the direction of the edge by its type:
            If the type is positive then the pokemon goes from the lesser to the greater node,
            so takes the minimum between src and dest.
            Else the pokemon goes from the greater to the lesser node,
            so takes the maximum between src and dest.
             */
        Arena.updateEdge(currentPokemon, graph);
        edge_data pokemonEdge = currentPokemon.get_edge();
        int pokemonDest;
        if (currentPokemon.getType() > 0)
            pokemonDest = Math.max(pokemonEdge.getSrc(), pokemonEdge.getDest());
        else
            pokemonDest = Math.min(pokemonEdge.getSrc(), pokemonEdge.getDest());
        return pokemonDest;
    }

    /**
     * The function gets a pokemon and a graph and returns the next step towards that pokemon (the new dest).
     * @param agent the agent
     * @param dest the nearest node to the target pokemon
     * @param ga the graph
     * @return the new destination of agent
     */
    private static int nextNode(CL_Agent agent, int dest, dw_graph_algorithms ga) {
        int src = agent.getSrcNode();
        return ga.shortestPath(src, dest).get(1).getKey();
    }
}
