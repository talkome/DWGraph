package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 */
public class PGameFrame extends JFrame{
    private Arena arena;
    private Range2Range range;

    PGameFrame(String title) {
        super(title);
    }

    public void update(Arena ar) {
        this.arena = ar;
        updateFrame();
    }

    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = arena.getGraph();
        range = Arena.w2f(g,frame);
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        updateFrame();
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
    }

    private void drawInfo(Graphics g) {
        List<String> str = arena.get_info();
        String dt = "none";
        int i=0;
        while (i < str.size()) {
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
            i++;
        }
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gameGraph = arena.getGraph();
        for (node_data currNode : gameGraph.getV()) {
            g.setColor(Color.GRAY);
            drawNode(currNode, g);
            for (edge_data currEdge : gameGraph.getE(currNode.getKey()))
                drawEdge(currEdge, g);
        }
    }

    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> pokemonList = arena.getPokemons();
        int r = 30;
        if(pokemonList != null) {
            for (CL_Pokemon pokemon : pokemonList) {
                Point3D pos = pokemon.getLocation();
                geo_location fp = range.world2frame(pos);
                if (pos != null){
                    if (pokemon.getType() < 0)
                        g.drawImage(new ImageIcon("resources/scizor.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                                2*r,2*r, new ImageIcon("resources/scizor.png").getImageObserver());
                    else
                        g.drawImage(new ImageIcon("resources/pikachu.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                                2*r,2*r, new ImageIcon("resources/pikachu.png").getImageObserver());
                }
            }
        }
    }

    private void drawAgents(Graphics g) {
        List<CL_Agent> agentList = arena.getAgents();
        int i=0, r = 30;
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

    private void drawNode(node_data n, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this.range.world2frame(pos);
        int r = 20;
        g.drawImage(new ImageIcon("resources/pokeball.png").getImage(),(int)fp.x()-r, (int)fp.y()-r,
                2*r,2*r, new ImageIcon("resources/pokeball.png").getImageObserver());
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4* r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this.range.world2frame(s);
        geo_location d0 = this.range.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }
}
