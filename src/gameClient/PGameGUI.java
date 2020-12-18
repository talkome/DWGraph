package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.util.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.PriorityQueue;

public class PGameGUI extends Thread {
    private JFrame frame;
    private JPanel panel;
    private DWGraph_Algo graph_algo;
    private game_service server;
    private Arena arena;// all the games info
    private Range2Range range; // match coordination to the screen

    public static void main(String[] args) {
        PGameGUI game = new PGameGUI();
    }

    public PGameGUI(){

        String[] levels = {
                "0", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23","24"
        };

        frame = new JFrame("EX2 OOP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,700);
        frame.setVisible(true);

        frame.getContentPane().setLayout(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon("resources/pokemon_opening.png"));
        background.setVerticalAlignment(JLabel.CENTER);
        background.setHorizontalAlignment(JLabel.CENTER);
        frame.add(background);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0,0,450,450));
        panel.setLayout(new GridLayout(0,1));

//        String s = JOptionPane.showInputDialog(jf, "Please enter your id");
//        int id = Integer.parseInt(s);
//        server.login(id);

        String selected_level = (String) JOptionPane.showInputDialog(null, "Choose level",
                "Message", JOptionPane.INFORMATION_MESSAGE, null, levels, levels[0]);

        int level_number = Integer.parseInt(selected_level);
        server = Game_Server_Ex2.getServer(level_number);

        this.arena = new Arena();
        this.graph_algo = new DWGraph_Algo();
        graph_algo.load(server.getGraph());
        arena.setGraph(graph_algo.getGraph());
        init();
        update(arena);
        paint();
        server.startGame();
        this.start();
    }

    public void update(Arena ar) {
        arena = ar;
        updateFrame();
    }

    public void paint() {
        updateFrame();
        drawPokemons();
        drawGraph();
        drawAgents();
    }

    private void updateFrame() {
        Range rx = new Range(20, frame.getWidth()-20);
        Range ry = new Range(frame.getHeight()-10,150); // empty
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = arena.getGraph();
        range = Arena.w2f(g,frame);
    }

    private void drawGraph() {
        directed_weighted_graph gameGraph = arena.getGraph();
        for (node_data currNode : gameGraph.getV()) {
            drawNode(currNode);
            for (edge_data currEdge : gameGraph.getE(currNode.getKey())) {
                StdDraw.setPenColor(Color.BLUE);
                drawEdge(currEdge);
            }
        }
    }

    private void drawNode(node_data n) {
        geo_location pos = n.getLocation();
        geo_location fp = range.world2frame(pos);
        StdDraw.picture(n.getLocation().x(),n.getLocation().y(),"resources/pokeball.png",
                fp.x(),fp.y());
    }

    private void drawEdge(edge_data e) {
        directed_weighted_graph gGraph = arena.getGraph();
        geo_location src = gGraph.getNode(e.getSrc()).getLocation();
        geo_location dest = gGraph.getNode(e.getDest()).getLocation();
        geo_location src_p = range.world2frame(src);
        geo_location dest_p = range.world2frame(dest);
        StdDraw.line((int)src_p.x(), (int)src_p.y(), (int)dest_p.x(), (int)dest_p.y());
    }

    private void drawPokemons() {
        String pokemon_pic = "resources/scizor.png";
        List<CL_Pokemon> pokemonsList = arena.getPokemons();
        if(pokemonsList != null) {
            for (CL_Pokemon currPokemon : pokemonsList) {
                geo_location fp = range.world2frame(currPokemon.getLocation());
                StdDraw.picture(currPokemon.getLocation().x(),
                        currPokemon.getLocation().y(), pokemon_pic,fp.x(),fp.y());
                if (currPokemon.getType() < 0)
                    pokemon_pic = "resources/pikachu.png";
            }
        }
    }

    private void drawAgents() {
        for (CL_Agent agent : arena.getAgents())
            StdDraw.picture(agent.getLocation().x(),agent.getLocation().y(),
                    agent.getPic(),0.001,0.0008);
    }

    private double getNumOfMoves(game_service server) {
        double moves = 0;
        try {
            JSONObject game_json = new JSONObject(server.toString());
            moves = game_json.getJSONObject("GameServer").getDouble("moves");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return moves;
    }

    private double getGrade(game_service server) {
        double grade = 0;
        try {
            JSONObject game_json = new JSONObject(server.toString());
            grade = game_json.getJSONObject("GameServer").getDouble("grade");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return grade;
    }

    //// PGame
    public void run(){
        while (server.isRunning()){
            drawGraph();
            drawPokemons();
            drawAgents();
            moveAgents();
            StdDraw.setPenColor(Color.YELLOW);
            StdDraw.setPenRadius(0.0005);
//            StdDraw.text(range.world2frame().x(),range.world2frame().y(),"TIMER: " + server.timeToEnd()/1000);
//            StdDraw.text(range.world2frame().x(),range.world2frame().y(), "SCORE: " + getGrade(server));
//            StdDraw.text(range.world2frame().x(),range.world2frame().y(), "MOVES: " + getNumOfMoves(server));
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(frame,"THE GAME IS OVER"+"\n"+"YOUR GRADE IS : " + getGrade(server) +"\nNUM OF MOVES IS : " + getNumOfMoves(server));
        }
    }

    /**
     * The method gets a game service and initialize the graph and the agents before the game is starting
     */
    private void init() {
        String pokemons = server.getPokemons();
        arena.setGraph(graph_algo.getGraph());

        //Creates a list which will contain all the pokemons in the game.
        List<CL_Pokemon> pokemonsList = Arena.json2Pokemons(pokemons);
        arena.setPokemons(pokemonsList);
        frame.setTitle("Ex2 OOP");
        frame.setSize(1000, 700);
        update(arena);
        StdDraw.show();
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
                    int pokemonSrc = getPokemonSrc(currentPokemon, graph_algo.getGraph());

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method gets a game and a graph and moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen by
     * an algorithm which find the most value pokemon in his area.
     *
     * @param targetedPokemons
     */
    public void moveAgents(List<CL_Pokemon> targetedPokemons) {
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
                int newDest = nextNode(currentAgent, pokemon_dest, graph_algo);

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
        if (result != null) {
            targetedPokemons.add(result);

        }

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
    private double getValueForDistance(double distance, CL_Pokemon currentPokemon) {
        return currentPokemon.getValue() / distance;
    }

    /**
     * The function gets a pokemon and a graph and returns the nearest src node to the pokemon
     *
     * @param currentPokemon the pokemon
     * @param graph          the graph
     * @return the nearest node to the pokemon
     */
    private int getPokemonSrc(CL_Pokemon currentPokemon, directed_weighted_graph graph) {
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

        Arena.updateEdge(currentPokemon, this.graph_algo.getGraph());
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
     * @param ga    the graph
     * @return the new destination of agent
     */
    private int nextNode(CL_Agent agent, int dest, dw_graph_algorithms ga) {
        int src = agent.getSrcNode();
        System.out.println("src = " + src);
        System.out.println("from " + src + " to " + dest + ": " + ga.shortestPath(src, dest).toString());
        System.out.println("next dest = " + ga.shortestPath(src, dest).get(1).getKey());
        return ga.shortestPath(src, dest).get(1).getKey();
    }
}
