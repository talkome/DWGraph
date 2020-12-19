package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class represents the pokemon game graphic UI base on JFrame
 * drawing the main graph include nodes and edges
 * and also drawing pokemons and agents
 * base on game server information
 * @author ko tal & Lioz akirav
 */
public class PGameFrame extends JFrame{
    private long timer;
    private Arena arena; // games arena
    private Range2Range range;
    private Image image;
    private Graphics graphics;

    /**
     * constructor
     * @param title - games title
     */
    public PGameFrame(String title) {
        super(title);
        this.getContentPane().setLayout(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon("resources/pokemon_opening.png"));
        background.setVerticalAlignment(JLabel.CENTER);
        background.setHorizontalAlignment(JLabel.CENTER);
        this.add(background);
    }

    /**
     * empty constructor
     */
    public PGameFrame() {
        super("EX2 OOP");

        String[] levels = {
                "0", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23"
        };
        
        this.setBackground(Color.CYAN);
        this.getContentPane().setLayout(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon("resources/pokemon_opening.png"));
        background.setVerticalAlignment(JLabel.CENTER);
        background.setHorizontalAlignment(JLabel.CENTER);
        this.add(background);

        String s = JOptionPane.showInputDialog(this, "Please enter your id");
        int id = Integer.parseInt(s);

        String selected_level = (String) JOptionPane.showInputDialog(null, "Choose level",
                "Message", JOptionPane.INFORMATION_MESSAGE, null, levels, levels[0]);

        int level_number = Integer.parseInt(selected_level);

        PGame game = new PGame(level_number,id);
    }

    /**
     * update the graph paint base on games arena
     * @param ar - games arena
     */
    public void update(Arena ar) {
        this.arena = ar;
        updateFrame();
    }

    /**
     * rescale the screen base on games arena
     */
    private void updateFrame() {
        Range rx = new Range(20,getWidth()-20);
        Range ry = new Range(getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = arena.getGraph();
        range = Arena.w2f(g,frame);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        paintComponent(graphics);
        g.drawImage(image,0,0,this);
    }

    /**
     * paint the game graph
     * @param g - graphics
     */
    public void paintComponent(Graphics g){
        g.drawImage(new ImageIcon("resources/pgame_background.jpeg")
                .getImage(), 0,0,getWidth(),getHeight(),new ImageIcon(
                "resources/pgame_background.jpeg").
                        getImageObserver());
        updateFrame();
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
    }

    /**
     * display game information on screen
     * base on java graphics
     * @param g - graphics
     */
    private void drawInfo(Graphics g) {
        List<String> info = arena.get_info();
        if (info.size() != 0){
            g.setColor(Color.BLUE);
            g.drawString("TIMER: " + getTimer(),100, 60);
            g.drawString("GRADE: " + getGrade(info.get(info.size()-1)),200, 60);
            g.drawString("MOVES: " + getNumOfMoves(info.get(info.size()-1)), 300, 60);
            g.drawString("LEVEL: " + getLevel(info.get(info.size()-1)), 400, 60);
        }
    }

    /**
     * return current games level base on server information
     * @param info - server information
     * @return current games level
     */
    private double getLevel(String info) {
        double level = 0;
        try {
            JSONObject game_json = new JSONObject(info);
            level = game_json.getJSONObject("GameServer").getDouble("game_level");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return level;
    }

    /**
     * return num of move base on server information
     * @param info - server information
     * @return num of move
     */
    public double getNumOfMoves(String info) {
        double moves = 0;
        try {
            JSONObject game_json = new JSONObject(info);
            moves = game_json.getJSONObject("GameServer").getDouble("moves");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return moves;
    }

    /**
     * return current games grade base on server information
     * @param info - server information
     * @return current games grade
     */
    public double getGrade(String info) {
        double grade = 0;
        try {
            JSONObject game_json = new JSONObject(info);
            grade = game_json.getJSONObject("GameServer").getDouble("grade");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return grade;
    }

    /**
     * drawing the game graph include nodes and edges
     * base on java graphics
     * @param g - graphics
     */
    private void drawGraph(Graphics g) {
        directed_weighted_graph gameGraph = arena.getGraph();
        for (node_data currNode : gameGraph.getV()) {
            g.setColor(Color.GRAY);
            drawNode(currNode, g);
            for (edge_data currEdge : gameGraph.getE(currNode.getKey()))
                drawEdge(currEdge, g);
        }
    }

    /**
     * drawing pokemons on graph
     * base on java graphics
     * @param g - graphics
     */
    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> pokemonList = arena.getPokemons();
        int r = 20;
        if(pokemonList != null) {
            for (CL_Pokemon pokemon : pokemonList) {
                Point3D pos = pokemon.getLocation();
                geo_location fp = range.world2frame(pos);
                if (pos != null){
                    if (pokemon.getType() < 0)
                        g.drawImage(new ImageIcon("resources/pikachu.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                                2*r,2*r, new ImageIcon("resources/pikachu.png").getImageObserver());
                    else
                        g.drawImage(new ImageIcon("resources/scizor.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                            2*r,2*r, new ImageIcon("resources/scizor.png").getImageObserver());
                }
            }
        }
    }

    /**
     * drawing agents on graph
     * base on java graphics
     * @param g - graphics
     */
    private void drawAgents(Graphics g) {
        List<CL_Agent> agentList = arena.getAgents();
        int i=0, r = 20;
        while(agentList != null && i < agentList.size()) {
            geo_location pos = agentList.get(i).getLocation();
            i++;
            if(pos != null) {
                geo_location fp = this.range.world2frame(pos);
                g.drawImage(new ImageIcon("resources/agent.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                        2*r,2*r, new ImageIcon("resources/agent.png").getImageObserver());
            }
        }
    }

    /**
     * drawing graph nodes
     * base on java graphics
     * @param n - current node
     * @param g - graphics
     */
    private void drawNode(node_data n, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this.range.world2frame(pos);
        int r = 10;
        g.drawImage(new ImageIcon("resources/pokeball.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                2*r,2*r, new ImageIcon("resources/pokeball.png").getImageObserver());
        g.setColor(Color.BLACK);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-r);
    }

    /**
     * drawing graph edges
     * base on java graphics
     * @param e - current edge
     * @param g - graphics
     */
    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this.range.world2frame(s);
        geo_location d0 = this.range.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }

    /**
     * get games timer
     * @return timer
     */
    public long getTimer() {
        return timer;
    }

    /**
     * set games timer
     * @param l - time
     */
    public void setTimer(long l) {
        timer = l;
    }
}
