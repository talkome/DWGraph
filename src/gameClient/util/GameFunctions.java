package gameClient.util;

import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.edge_data;
import api.game_service;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;

import java.util.List;

public class GameFunctions {
     /*
        -------------------------------------------------------------------------------------------------
        Functions
        -------------------------------------------------------------------------------------------------
         */
    /**
     * The function gets an agent and a graph and returns the nearest pokemon with the greatest value,
     * by compute the value/the distance.
     *
     * @param agent      the agent
     * @param graph_algo the graph
     * @return the nearest pokemon with the greatest value
     */
    public static CL_Pokemon getNearestPokemon(CL_Agent agent, DWGraph_Algo graph_algo, List<CL_Pokemon> targetedPokemons, game_service game) {
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
                if(distance > 0) {
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
    public static double getValueForDistance(double distance, CL_Pokemon currentPokemon) {
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
    public static int getPokemonNode(CL_Pokemon currentPokemon, DWGraph_DS myGraph) {
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
     * @param agent the agent
     * @param dest the nearest node to the target pokemon
     * @param graph_algo the graph
     * @return the new destination of agent
     */
    public static int nextNode(CL_Agent agent, int dest, DWGraph_Algo graph_algo) {
        int src = agent.getSrcNode();
        int ans = graph_algo.shortestPath(src, dest).get(1).getKey();

        return ans;
    }
}
