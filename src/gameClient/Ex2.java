package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.util.Functions;

import java.util.List;
import java.util.PriorityQueue;

public class Ex2 {

    public static void main(String[] args) {
        int level_number = 0; //The level of the game [0,24]
        game_service game = Game_Server_Ex2.getServer(level_number);
        System.out.println(game); //Prints the server details
        String graph = game.getGraph();
        System.out.println(graph); //Prints the graph details
        String pokemons = game.getPokemons();
        System.out.println(pokemons); //Prints the pokemons details

        DWGraph_DS myGraph = new directed_weighted_graph;
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.init(myGraph);
        //Loads all the data into the graph
        graph_algo.load(graph);
        String gameDetails = game.move();
        //Creates a list which will contain all the agents in the game.
        List<CL_Agent> agentsList = Arena.getAgents(gameDetails, myGraph);
         /*
        Creates a priority queue which will contain all the pokimons in the game.
        The priority queue ranks the pokimons by their values from the greater to the lesser.
         */
        PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokimon_Comparator());
        //Supposed to add all the pokimons in the game to the PQ

        /*
        Locates all the agents in the graph,
        the first agent locates in the closest node to the pokemon with the greatest value and etc.
         */
        for (int i=0; i<agentsList.size(); i++){
            CL_Agent currentAgent = agentsList.get(i);
            int agentLocation = pokemonsPQ.poll().getLocation();
            game.addAgent(agentLocation);
        }
        System.out.println(game.getAgents()); //Prints the agents details

        game.startGame();
        int i=0;

        while (game.isRunning()){ //Keep running while the game is on
//            int agentCurrentLocation = game.getAgents()
            game.chooseNextEdge(0,5);
            game.move();
            Arena.getAgents()
        }
    }

    /*Functions*/

//    /**
//     * The function returns the amount of agents in the current game
//     * @param game
//     * @return the number of agents in the current game
//     */
//    private static int getNumOfAgents(game_service game){
//        String gameDetails = game.toString();
//        int index = gameDetails.indexOf("agents");
//        int ans = gameDetails.charAt(index);
//
//        return ans;
//    }
}
