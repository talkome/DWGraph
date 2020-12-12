package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.util.Functions;
import gameClient.util.Point3D;

import java.util.List;
import java.util.PriorityQueue;

public class Ex2 {

    public static void main(String[] args) {
        /*
        -----------------------------------------------------------------------------------------------------------
        Initializing the game.
        -----------------------------------------------------------------------------------------------------------
         */
        int level_number = 0; //The level of the game [0,24]
        game_service game = Game_Server_Ex2.getServer(level_number);
        System.out.println(game); //Prints the server details
        String graph = game.getGraph();
        System.out.println(graph); //Prints the graph details
        String pokemons = game.getPokemons();
        System.out.println(pokemons); //Prints the pokemons details

        /*
        -----------------------------------------------------------------------------------------------------------
        pre-launch
        -----------------------------------------------------------------------------------------------------------
         */
        DWGraph_DS myGraph = new directed_weighted_graph;
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.init(myGraph);
        //Loads all the data into the graph
        graph_algo.load(graph);
        String gameDetails = game.move();

        //Creates a list which will contain all the agents in the game.
        List<CL_Agent> agentsList = Arena.getAgents(gameDetails, myGraph);

        //Creates a list which will contain all the pokemons in the game.
        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(pokemons);

         /*
        Creates a priority queue which will contain all the pokimons in the game.
        The priority queue ranks the pokimons by their values from the greater to the lesser.
         */
        PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokimon_Comparator());
        //Moves all the pokimons from the list to the PQ
        for (int i = 0; i < pokemonsList.size(); i++) {
            pokemonsPQ.add(pokemonsList.get(i));
            pokemonsList.remove(i);
        }

        /*
        Locates all the agents in the graph,
        the first agent locates in the closest node to the pokemon with the greatest value and etc.
         */
        for (int i = 0; i < agentsList.size(); i++) {
            CL_Pokemon currentPokemon = pokemonsPQ.poll();
            int pokemonSrc = getPokemonNode(currentPokemon, myGraph);
            //locates the current agent in the nearest node to the pokemon.
            game.addAgent(pokemonSrc);
        }
        System.out.println(game.getAgents()); //Prints the agents details

        /*
        -----------------------------------------------------------------------------------------------------------
        Launching the game
        -----------------------------------------------------------------------------------------------------------
         */
        game.startGame();
        int j = 0;

        while (game.isRunning()) { //Keep running while the game is on
            for (int i = 0; i < agentsList.size(); i++) {
                CL_Agent currentAgent = agentsList.get(i);
                //Checks it the agent is in a node
                if (currentAgent.getNextNode() != -1) {
                    CL_Pokemon target = getNearestPokemon(currentAgent.getSrcNode(), myGraph);
                    int dest =
                    game.chooseNextEdge(currentAgent.getID(), dest);
                }
            }

            game.chooseNextEdge(0, 5);
            game.move();
        }

        /*
        -----------------------------------------------------------------------------------------------------------
        Functions
        -----------------------------------------------------------------------------------------------------------
         */

        /**
         * The function gets an agent and a graph and returns the nearest pokemon
         * @param srcNode the source of the agent
         * @param myGraph the graph
         * @return the nearest pokemon
         */
        private static CL_Pokemon getNearestPokemon(int srcNode, DWGraph_DS myGraph) {
            CL_Pokemon ans;
            double distance = 0;
            //Creates a list which will contain all the pokemons in the game.
            List<CL_Pokemon> tempPokemonsList = Arena.json2Pokemons(pokemons);
            for (int i = 0; i < tempPokemonsList.size(); i++) {
                CL_Pokemon currentPokemon = tempPokemonsList.get(i);
                graph_algo.shortestPath(srcNode, currentPokemon.);
                pokemonsList.remove(i);
            }
        }
    }

    /**
     * The function gets a pokemon and a graph and returns the nearest node to the pokemon
     * @param currentPokemon the pokemon
     * @param myGraph the graph
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
    }
}
