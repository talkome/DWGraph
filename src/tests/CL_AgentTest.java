package tests;

import api.DWGraph_DS;
import api.NodeData;
import api.edge_data;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import gameClient.util.Point3D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CL_AgentTest {
    public DWGraph_DS myGraph = new DWGraph_DS();
    String agent_str = "{\"Agents\":[{\"Agent\":{\"id\":0,\"value\":0.0,\"src\":26,\"dest\":-1," +
            "\"speed\":1.0,\"pos\":\"35.20260156093624,32.10476360672269,0.0\"}}]}";

    @BeforeEach
    // Creates a graph.
    void setUp() {
        for (int i = 0; i < 5; i++){
            myGraph.addNode(new NodeData(i));
        }
        myGraph.connect(0,1,3);
        myGraph.connect(0,2,1);
        myGraph.connect(1,2,20);
        myGraph.connect(1,3,5);
        myGraph.connect(1,4,7);
    }

    @Test
    // Test adding a pokemon as a agent's target.
    void updateTargetPokemonsList() {
        edge_data edge =  myGraph.getEdge(0, 1);
        Point3D point = new Point3D(0 , 0 , 0);
        CL_Agent agent = new CL_Agent(myGraph, 0);
        CL_Pokemon pokemon = new CL_Pokemon(point, 0, 0 ,0 , edge);
        agent.updateTargetPokemonsList(pokemon);

        String expected = pokemon.get_id();
        String actual = agent.getTargetPokemonsList().get(0);
        assertEquals(expected, actual);
    }

}