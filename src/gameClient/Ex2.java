package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;

    public static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        client.start();

//        game_service game = Game_Server_Ex2.getServer(0);
//        String g = game.getGraph();
//        DWGraph_DS gg = new DWGraph_DS();
//        DWGraph_Algo graph_algo = new DWGraph_Algo();
//        graph_algo.load(g);
//
//        System.out.println(graph_algo);

    }

    @Override
    public void run() {
          /*
        -------------------------------------------------------------------------------------------------
        Game initializing
        -------------------------------------------------------------------------------------------------
         */
        int level_number = 0; //The level of the game [0,23]
        game_service game = Game_Server_Ex2.getServer(level_number);
//        System.out.println(game); //Prints the server details
        String graph = game.getGraph();
//        System.out.println(graph); //Prints the graph details
        String pokemons = game.getPokemons();
//        System.out.println(pokemons); //Prints the pokemons details
        DWGraph_DS myGraph = new DWGraph_DS();
        //Initializing the game
        init(game);
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.init(myGraph);

        /*
        -------------------------------------------------------------------------------------------------
        Pre-launch
        -------------------------------------------------------------------------------------------------
         */
//        DWGraph_Algo graph_algo = new DWGraph_Algo();
//        graph_algo.init(myGraph);
//        String gameDetails = game.move();
//        //Loads all the data into the graph
//        graph_algo.load(graph);
//
//        //Creates a list which will contain all the agents in the game.
//        List<CL_Agent> agentsList = Arena.getAgents(gameDetails, myGraph);
//
//        //Creates a list which will contain all the pokemons in the game.
//        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(pokemons);
//
//         /*
//        Creates a priority queue which will contain all the pokimons in the game.
//        The priority queue ranks the pokimons by their values from the greater to the lesser.
//         */
//        PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokimon_Comparator());
//        //Moves all the pokimons from the list to the PQ
//        for (int i = 0; i < pokemonsList.size(); i++) {
//            pokemonsPQ.add(pokemonsList.get(i));
//            pokemonsList.remove(i);
//        }
//
//        /*
//        Locates all the agents in the graph,
//        the first agent locates in the closest node to the pokemon with the greatest value and etc.
//         */
//        for (int i = 0; i < agentsList.size(); i++) {
//            CL_Pokemon currentPokemon = pokemonsPQ.poll();
//            int pokemonSrc = getPokemonNode(currentPokemon, myGraph);
//            //locates the current agent in the nearest node to the pokemon.
//            game.addAgent(pokemonSrc);
//        }
//        System.out.println(game.getAgents()); //Prints the agents details

        /*
        -------------------------------------------------------------------------------------------------
        Game Launching
        -------------------------------------------------------------------------------------------------
         */
        game.startGame();
        _win.setTitle("Ex2 - OOP: (NONE trivial Solution) " + game.toString());
        int ind = 0;
        long dt = 100;

        //Initialize an ArrayList that contains all the targeted pokemons.
        List<CL_Pokemon> targetedPokemons = new ArrayList<CL_Pokemon>();

        //Keep running while the game is on
        while (game.isRunning()) {
//            moveAgents(game, myGraph, graph_algo, targetedPokemons);
            try {
                if (ind % 1 == 0) {
                    _win.repaint();
                }
                Thread.sleep(dt);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }


    /*
        -------------------------------------------------------------------------------------------------
        Functions
        -------------------------------------------------------------------------------------------------
         */

    /**
     * The method gets a game service and initialize the graph and the agents before the game is starting
     *
     * @param game the game
     */
    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        DWGraph_DS gg = new DWGraph_DS();
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.load(g);
        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);
        _win.show();
        String info = game.toString();
        JSONObject line;
        String gameDetails = game.move();

        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());


            //Creates a list which will contain all the pokemons in the game.
            List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(game.getPokemons());

             /*
        Creates a priority queue which will contain all the pokimons in the game.
        The priority queue ranks the pokimons by their values from the greater to the lesser.
         */
            PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokimon_Comparator());
            //Moves all the pokimons from the list to the PQ
            for (int i = 0; i < pokemonsList.size(); i++) {
                Arena.updateEdge(pokemonsList.get(i), gg);
                pokemonsPQ.add(pokemonsList.get(i));
                pokemonsList.remove(i);
            }

             /*
        Locates all the agents in the graph,
        the first agent locates in the closest node to the pokemon with the greatest value and etc.
         */
            for (int i = 0; i < rs; i++) {
                CL_Pokemon currentPokemon = pokemonsPQ.poll();
                int pokemonSrc = getPokemonNode(currentPokemon, gg);
                //locates the current agent in the nearest node to the pokemon.
                game.addAgent(pokemonSrc);
            }
//            System.out.println(game.getAgents()); //Prints the agents details

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
     * @param myGraph the graph
     * @param graph_algo
     * @param targetedPokemons
     */
    private void moveAgents(game_service game, DWGraph_DS myGraph, DWGraph_Algo graph_algo, List<CL_Pokemon> targetedPokemons) {
        String gameDetails = game.move();
        //Creates a list which will contain all the agents in the game.
        List<CL_Agent> agentsList = Arena.getAgents(gameDetails, myGraph);

        for (int i = 0; i < agentsList.size(); i++) {
            //Takes an agent from the agentList.
            CL_Agent currentAgent = agentsList.get(i);
            //Checks if the agent is at a node, if it is gives him a new destination.
            if (currentAgent.getNextNode() != -1) {
                //Finds the nearest pokemon with the greatest value .
                CL_Pokemon target = getNearestPokemon(currentAgent, graph_algo, targetedPokemons, game);
                //Finds the nearest node to the target.
                int pokemonNode = getPokemonNode(target, myGraph);
                //Calculates which node will be the next destination
                int newDest = nextNode(currentAgent, pokemonNode, graph_algo);
                //Sets a new destination for the current agent.
                game.chooseNextEdge(currentAgent.getID(), newDest);

                //Agent details
                int agentID = currentAgent.getID();
                double agentValue = currentAgent.getValue();
                System.out.println("Agent: " + agentID + ", value: " + agentValue + " is moving to node: " + newDest);
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
     * @param graph_algo the graph
     * @return the nearest pokemon with the greatest value
     */
    private static CL_Pokemon getNearestPokemon(CL_Agent agent, DWGraph_Algo graph_algo, List<CL_Pokemon> targetedPokemons, game_service game) {
        int srcNode = agent.getSrcNode();
        String pokemons = game.getPokemons();
        CL_Pokemon ans = null;
        double distance;
        double score = 0;
        //Creates a list which will contain all the pokemons in the game.
        List<CL_Pokemon> PokemonsList = Arena.json2Pokemons(pokemons);

        /*
        Iterates all the pokemons in the game that is not targeted yet,
        And checks which pokemon has the greatest valueForDistance.
         */
        for (int i = 0; i < PokemonsList.size(); i++) {
            CL_Pokemon currentPokemon = PokemonsList.get(i);
            //Checks if the current pokemon is not targeted already.
            if (!targetedPokemons.contains(currentPokemon)) {
                int pokemonSrc = getPokemonNode(currentPokemon, (DWGraph_DS) graph_algo.getGraph());
                distance = graph_algo.shortestPathDist(srcNode, pokemonSrc);
                if (distance > 0) {
                    double tempScore = getValueForDistance(distance, currentPokemon);
                    if (tempScore > score) {
                        score = tempScore;
                        ans = currentPokemon;
                    }
                }
            }
        }
        //Marks the pokemon as targeted by adding it to the targeted list.
        targetedPokemons.add(ans);

        //Returns the targeted pokemon.
        return ans;
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
        double ans = currentPokemon.getValue() / distance;

        return ans;
    }

    /**
     * The function gets a pokemon and a graph and returns the nearest node to the pokemon
     *
     * @param currentPokemon the pokemon
     * @param myGraph        the graph
     * @return the nearest node to the pokemon
     */
    private static int getPokemonNode(CL_Pokemon currentPokemon, DWGraph_DS myGraph) {
        /*
            Checks the direction of the edge by its type:
            If the type is positive then the pokemon goes from the lesser to the greater node,
            so takes the minimum between src and dest.
            Else the pokemon goes from the greater to the lesser node,
            so takes the maximum between src and dest.
             */
        Arena.updateEdge(currentPokemon, myGraph);
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
     * The function gets a pokemon and a graph and returns the next step towards that pokemon (the new dest).
     *
     * @param agent      the agent
     * @param dest       the nearest node to the target pokemon
     * @param graph_algo the graph
     * @return the new destination of agent
     */
    private static int nextNode(CL_Agent agent, int dest, DWGraph_Algo graph_algo) {
        int src = agent.getSrcNode();
        int ans = graph_algo.shortestPath(src, dest).get(1).getKey();

        return ans;
    }
}
