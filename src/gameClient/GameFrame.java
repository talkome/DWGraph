package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameFrame extends JFrame{
    private int index = 0;
    private Arena arena;// all the games info
    private Range2Range range; // match coordination to the screen

    GameFrame(String title) {
        super(title);
    }

    public void update(Arena arena) {
        this.arena = arena;
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
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);

        updateFrame();
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        for (node_data n : gg.getV()) {
            g.setColor(Color.blue);
            drawNode(n, g);
            for (edge_data e : gg.getE(n.getKey())) {
                g.setColor(Color.gray);
                drawEdge(e,g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> pokemonsList = arena.getPokemons();
        if(pokemonsList != null) {
            for (CL_Pokemon currPokemon : pokemonsList) {
                Point3D point = currPokemon.getLocation();
                int r = 10;
                g.setColor(Color.green);
                if (currPokemon.getType() < 0)
                    g.setColor(Color.orange);
                if (point != null) {
                    geo_location fp = this.range.world2frame(point);
                    g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
                    //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
                }
            }
        }
    }

    private void drawAgents(Graphics g) {
        List<CL_Agent> AgentsList = arena.getAgents();
        g.setColor(Color.red);
        int i=0;
        while(AgentsList != null && i < AgentsList.size()) {
            geo_location c = AgentsList.get(i).getLocation();
            int r=8;
            i++;

            if(c != null) {
                geo_location fp = this.range.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
            }
        }
    }

    private void drawInfo(Graphics g) {
        java.util.List<String> str = arena.get_info();
        String dt = "none";
        for(int i=0;i<str.size();i++)
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
    }

    private void drawNode(node_data n, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = range.world2frame(pos);
        g.fillOval((int)fp.x()- 5, (int)fp.y()- 5, 2*5, 2*5);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*5);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = range.world2frame(s);
        geo_location d0 = range.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
        //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
    }
}
