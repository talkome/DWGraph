package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class represents the engine behind the game
 * which uses the "server for moving the "Agents".
 * and place the pokemon on the graph
 * @author ko tal & Lioz akirav
 */
public class PGame implements Runnable {
    public game_service server;
    private DWGraph_Algo graph_algo;
    private static PGameFrame frame;
    private static Arena arena;

    public static void main(String[] args) {
        // Producer version
        Thread client = new Thread(new PGame(3, 311148902));
        client.start();

        // Visual version
//        PGame game = new PGame();
    }

    /*
    -------------------------------------------------------------------------------------------------
    Game initializing
    -------------------------------------------------------------------------------------------------
    */
    public PGame(int level, int userID) {
        server = Game_Server_Ex2.getServer(level);
       if (isValidID(userID))
           server.login(userID);
       else
           throw new RuntimeException("invalid id");

       arena = new Arena();
       frame = new PGameFrame("OOP Ex2" + server.toString());
       frame.setSize(1500, 1000);
       this.graph_algo = new DWGraph_Algo();
       graph_algo.load(server.getGraph());
       arena.setGraph(graph_algo.getGraph());
       init();
    }

    public PGame() {
        String[] levels = {
                "0", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23"
        };

        String selected_level = (String) JOptionPane.showInputDialog(null, "Choose a level",
                "Message", JOptionPane.INFORMATION_MESSAGE, null, levels, levels[0]);

        int level_number = Integer.parseInt(selected_level);

        String s = JOptionPane.showInputDialog(this, "Enter your ID");
        int id = Integer.parseInt(s);

        Thread client = new Thread(new PGame(level_number ,id));
        client.start();
    }

    /**
     * The method gets a game service and initialize the graph and the agents before the game is starting
     */
    private void init() {

        //Creates a list which will contain all the pokemons in the game.
        String pokemons = server.getPokemons();
        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(pokemons);
        arena.setPokemons(pokemonsList);

        frame.update(arena);
        frame.show();
        List<String> infoList = arena.get_info();
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
            PriorityQueue<CL_Pokemon> pokemonsPQ = new PriorityQueue<>(new Pokemon_Comparator());

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

            infoList.add(info);
            arena.set_info(infoList);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    -------------------------------------------------------------------------------------------------
    Game Launching
    -------------------------------------------------------------------------------------------------
    */
    @Override
    public void run() {
        server.startGame();
        int ind = 0;

        //Keep running while the game is on
        while (server.isRunning()) {
            int sleepTime = moveAgents();
            frame.setTimer(server.timeToEnd()/1000);
//            System.out.println("sleepTime: " + sleepTime);
            try {
                if (ind % 1 == 0)
                    frame.repaint();
                Thread.sleep(sleepTime);
                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(frame,
                "          GAME OVER!" +
                "\nYOUR SCORE IS : " + getGrade() +
                "\nNUM OF MOVES IS : " + getNumOfMoves());
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
     *
     * @return
     */
    private int moveAgents() {
        int destination = 0, sleepTime = 100;

        //Creates an ArrayList which will contain the sleep time of each of the agents.
        ArrayList<Integer> sleepList = new ArrayList<>();

        // Updates game graph
        String updatedGraph = getUpdateGraph();

        // Updates agents list
        List<CL_Agent> newAgentsList = getUpdateAgents(updatedGraph);

        // Updates pokemons list
        List<CL_Pokemon> newPokemonsList = getUpdatePokemons();

        // Updates pokemons list
        String newInfo = server.toString();
        List<String> newInfoList = arena.get_info();
        newInfoList.add(newInfo);
        arena.set_info(newInfoList);

        for (CL_Agent currentAgent : newAgentsList) {

            //Takes an agent from the agentList.
            //Checks if the agent is at a node, if it is gives him a new destination.
            if (currentAgent.getNextNode() == -1) {
                // Clears the agent's target list.
                currentAgent.clearAgentTargetList();

                //Finds the nearest pokemon with the greatest value.
                CL_Pokemon target = getNearestPokemon(currentAgent, newAgentsList, newPokemonsList);

                //If all the pokemons have already been targeted, then the agent will stay at the same node
                if (target == null) {
                    printAgentMove();
                    return sleepTime;
                }

                //Finds the dest of nearest node to the target.
                int pokemon_dest = getPokemonDest(currentAgent, target);
                destination = pokemon_dest;

                //Determines which node will be the next destination
                int newDest = nextNode(currentAgent, pokemon_dest);

                //Sets a new destination for the current agent.
                server.chooseNextEdge(currentAgent.getID(), newDest);

                //Prints the agent move.
                printAgentMove(currentAgent, newDest, pokemon_dest, target);

                //Determines the thread sleep.
                sleepTime = getSleepTime(currentAgent, destination, sleepTime);
                sleepList.add(sleepTime);
            }

//            System.out.println("agent " +currentAgent.getID() + "# target list: " + currentAgent.getTargetPokemonsList().toString());
        }

        /*
        Returns the minimum sleep time in the sleepList.
        */
        int minSleep = sleepTime;
        for (int x : sleepList)
            if (x < minSleep)
                minSleep = x;

        return minSleep;
    }

    /**
     * The method gets a graph, an agent and a destination determines the sleep time.
     *
     * @param sleepTime   the default sleep time
     * @param agent       the agent
     * @param destination the destination of the agent
     * @return the sleep time
     */
    private int getSleepTime(CL_Agent agent, int destination, int sleepTime) {
        double distance = 0, edge = 0, maxSpeed = 0;
        int result;
        boolean ans = false;
        //Calculates the distance of the edges of the agent from his designation.
        distance = graph_algo.shortestPath(agent.getSrcNode(), destination).size() - 1;

        /*
        If the agent is going to the pokemon's edge, then calculates the new sleep time.
        Otherwise, return sleepTime.
         */
        if (distance == 1) {
            maxSpeed = agent.getSpeed();
            edge = graph_algo.shortestPathDist(agent.getSrcNode(), destination);
            ans = true;
        }

        if (!ans) {
            result = sleepTime;
        } else {
            result = (int)((edge*5)/maxSpeed);
        }
        System.out.println("distance: " + distance);
        return result;
    }

    /**
     * Prints a message if the agent did not move.
     */
    private void printAgentMove() {
        System.out.println("None of the agents moved, ");
        System.out.println("All the pokemons have already been targeted.");
    }

    /**
     * Prints the agents move (if he moved).
     *
     * @param currentAgent the agent
     * @param newDest      the new agent's distance
     */
    private void printAgentMove(CL_Agent currentAgent, int newDest, int pokemon_dest, CL_Pokemon pokemon) {
        //Agent details
        int agentID = currentAgent.getID();
        double agentValue = currentAgent.getValue();
        int agentSrc = currentAgent.getSrcNode();
        System.out.println("Agent: " + agentID + ", value: " + agentValue + " is chasing after pokemon  " + pokemon + " at node " + pokemon_dest);
        System.out.println("Agent: " + agentID + ", value: " + agentValue + " is moving from node " + agentSrc + " to node " + newDest);
    }

    /**
     * Returns the update pokemons list and set the pokemons in the arena.
     *
     * @return the update pokemons list
     */
    private List<CL_Pokemon> getUpdatePokemons() {
        String pokemons = server.getPokemons();
        List<CL_Pokemon> newPokemonsList = Arena.json2Pokemons(pokemons);
        for (CL_Pokemon currentPok : newPokemonsList) {
            Arena.updateEdge(currentPok, graph_algo.getGraph());
        }
        arena.setPokemons(newPokemonsList);
//        System.out.println("Pokemon info:" + newPokemonsList.toString());
//        System.out.println("Pokemon Edge: " + newPokemonsList.get(0).get_edge());

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
     *
     * @return the update graph
     */
    private String getUpdateGraph() {
        String updatedGraph = server.move();
//        System.out.println(updatedGraph);
        return updatedGraph;
    }

    /**
     * The function gets an agent and a graph and returns the nearest pokemon with the greatest value,
     * by compute the value/the distance.
     *
     * @param agent        the agent
     * @param agentsList   the agents list
     * @param pokemonsList the pokemonList
     * @return the nearest pokemon with the greatest value
     */
    private CL_Pokemon getNearestPokemon(CL_Agent agent, List<CL_Agent> agentsList, List<CL_Pokemon> pokemonsList) {
        int srcNode = agent.getSrcNode();
        CL_Pokemon result = null;
        double distance, maxScore = 0;

        /*
        Iterates all the pokemons in the game that is not targeted yet,
        And checks which pokemon has the greatest valueForDistance.
        */
        for (CL_Pokemon currentPokemon : pokemonsList) {

            //Checks if the current pokemon is not targeted already.
            if(!checkTarget(agentsList,currentPokemon)){
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
        if(result != null) {
            agent.updateTargetPokemonsList(result);
        }

        //Returns the targeted pokemon.
        return result;
    }

    private boolean checkTarget(List<CL_Agent> agentList, CL_Pokemon pokemon) {
        for (CL_Agent currAgent : agentList) {
            if (currAgent.getTargetPokemonsList().contains(pokemon)){
                return true;
            }
        }
        return false;
    }

    /**
     * The functions gets a distance and a pokemon and returns the quotient of the distance/the speed
     * of the pokemon.
     *
     * @param distance       the distance
     * @param currentPokemon the pokemon
     * @return the quotient of the distance/the speed of the pokemon
     */
    private double getValueForDistance(double distance, CL_Pokemon currentPokemon) {
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
        edge_data pokemonEdge = currentPokemon.getEdge();
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
        edge_data pokemonEdge = currentPokemon.getEdge();
        int[] destArr = new int[2];
        int pokemonDest, alternativeDest, result;

        pokemonDest = Math.min(pokemonEdge.getSrc(), pokemonEdge.getDest());
        alternativeDest = Math.max(pokemonEdge.getSrc(), pokemonEdge.getDest());
        destArr[0] = pokemonDest;
        destArr[1] = alternativeDest;

        if (destArr[0] < agent.getSrcNode()) {
            result = destArr[0];
        } else {
            result = destArr[1];
        }

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
//        System.out.println("src = " + src);
//        System.out.println("from " + src + " to " + dest + ": " + ga.shortestPath(src, dest).toString());
//        System.out.println("next dest = " + ga.shortestPath(src, dest).get(1).getKey());
        return graph_algo.shortestPath(src, dest).get(1).getKey();
    }

    /**
     * return num of move base on server information
     * @return num of move
     */
    public double getNumOfMoves() {
        double moves = 0;
        try {
            JSONObject game_json = new JSONObject(server.toString());
            moves = game_json.getJSONObject("GameServer").getDouble("moves");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moves;
    }

    /**
     * return current games grade base on server information
     * @return current games grade
     */
    public double getGrade() {
        double grade = 0;
        try {
            JSONObject game_json = new JSONObject(server.toString());
            grade = game_json.getJSONObject("GameServer").getDouble("grade");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return grade;
    }

    /**
     * This method gets an id and check if is valid (took the pseudocode from wikipedia).
     * Source: https://he.wikipedia.org/wiki/%D7%A1%D7%A4%D7%A8%D7%AA_%D7%91%D7%99%D7%A7%D7%95%D7%A8%D7%AA
     *
     * @param id the used id
     * @return true id the id is valid
     */
    private static boolean isValidID(int id) {
        boolean ans = true;
        String stringID = Integer.toString(id);
        if (stringID.length() != 9 || isNaN(stringID) == false) {  // Make sure ID is formatted properly
            ans = false;
        }
        return ans;
    }

    private static boolean isNaN(String id) {
        int sum = 0, incNum, lastDigit;
        for (int i = 0; i < id.length()-1; i++) {
            incNum = (Integer.parseInt(String.valueOf(id.charAt(i)))) * ((i % 2) + 1);  // Multiply number by 1 or 2
            sum += (incNum > 9) ? incNum - 9 : incNum;  // Sum the digits up and add to total
        }

        lastDigit = Integer.parseInt(String.valueOf(id.charAt(id.length()-1)));

        return(10-(sum%10) == lastDigit);
    }

}