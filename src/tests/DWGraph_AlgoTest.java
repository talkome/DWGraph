package tests;

import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.NodeData;
import api.node_data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {
    public final double INFINITY = Double.POSITIVE_INFINITY;
    DWGraph_Algo graph_algo = new DWGraph_Algo();
    DWGraph_DS myGraph = new DWGraph_DS();
    DWGraph_DS connected_graph = new DWGraph_DS();
    DWGraph_DS otherGraph = new DWGraph_DS();


    @BeforeEach
    void setUp() {
        for (int i = 0; i < 9; i++){
            NodeData newNode = new NodeData(i);
            myGraph.addNode(newNode);
        }

        myGraph.connect(0,1,3);
        myGraph.connect(0,2,9);
        myGraph.connect(1,2,7);
        myGraph.connect(1,3,5);
        myGraph.connect(1,4,22.2);
        myGraph.connect(3,4,12);
        myGraph.connect(5,4,11);
        myGraph.connect(6,4,7);
        myGraph.connect(7,3,8);
        myGraph.connect(8,2,10);

        for (int i = 0; i < 3; i++){
            NodeData newNode = new NodeData(i);
            connected_graph.addNode(newNode); // Changed from myGraph to connected_graph
        }
        connected_graph.connect(0,1,5);
        connected_graph.connect(0,2,30);
        connected_graph.connect(1,2,11);

        for (int i = 0; i < 6; i++){
            NodeData newNode = new NodeData(i);
            otherGraph.addNode(newNode);
        }

        otherGraph.connect(0,1,2);
        otherGraph.connect(1,2,6.2);
        otherGraph.connect(1,4,6.4);
        otherGraph.connect(1,3, 9.5);
        otherGraph.connect(4,3,1);
        otherGraph.connect(2,3,2);
        otherGraph.connect(3,0,0.5);
        otherGraph.connect(3,5,100);
    }

    @Test
    void init() {
        graph_algo.init(myGraph);
        String expected = graph_algo.toString();
        graph_algo.init(connected_graph);
        String actual = graph_algo.toString();
        assertNotEquals(expected, actual);
    }

    @Test
    void getGraph() {
        graph_algo.init(myGraph);
        assertEquals(myGraph,graph_algo.getGraph());
    }

    @Test
    void copy() {
        graph_algo.init(myGraph);
        DWGraph_DS copyGraph = (DWGraph_DS) graph_algo.copy();
        assertEquals(copyGraph,myGraph);
    }

    @Test
    void isConnected1() {
        graph_algo.init(myGraph);
        assertFalse(graph_algo.isConnected());
        graph_algo.init(connected_graph);
        assertFalse(graph_algo.isConnected()); // Changed from True to False
    }

    @Test
    void isConnected2() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();
        NodeData node0 = new NodeData(0);
        NodeData node1 = new NodeData(1);
        NodeData node2 = new NodeData(2);
        g.addNode(node0);
        g.addNode(node1);
        g.addNode(node2);
        g.connect(0, 1, 10);
        g.connect(1, 2, 20);
        g.connect(2, 0, 30);

        ga.init(g);
        assertTrue(ga.isConnected());
    }

    @Test
    void isConnected3() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();
        NodeData node0 = new NodeData(0);
        NodeData node1 = new NodeData(1);
        NodeData node2 = new NodeData(2);
        NodeData node3 = new NodeData(3);

        g.addNode(node0);
        g.addNode(node1);
        g.addNode(node2);
        g.addNode(node3);
        g.connect(1, 0, 10);
        g.connect(0, 2, 20);
        g.connect(2, 1, 30);
        g.connect(1, 3, 40);

        ga.init(g);
        assertFalse(ga.isConnected()); // ToDo: Should return False! (Works only in my method)

        g.connect(3, 2, 50.5);
        assertTrue(ga.isConnected());
    }

    @Test
    void isConnected4() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();
        NodeData node0 = new NodeData(0);
        NodeData node1 = new NodeData(1);
        NodeData node2 = new NodeData(2);
        NodeData node3 = new NodeData(3);
        NodeData node4 = new NodeData(4);

        g.addNode(node0);
        g.addNode(node1);
        g.addNode(node2);
        g.addNode(node3);
        g.addNode(node4);
        g.connect(1, 0, 10);
        g.connect(0, 2, 20);
        g.connect(2, 1, 30);
        g.connect(3, 4, 40);

        ga.init(g);
        assertFalse(ga.isConnected());

        g.connect(1, 3, 50.5);
        g.connect(4, 2, 55.5);
        assertTrue(ga.isConnected());
    }

    @Test
    // Tests an empty graph
    void isConnected5() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();

        ga.init(g);
        assertTrue(ga.isConnected());
    }

    @Test
    void shortestPathDist1() {
        graph_algo.init(myGraph);
        double result = graph_algo.shortestPathDist(1, 4);
        System.out.println(result);
        assertEquals(17, result);
    }

    @Test
    void shortestPathDist2() {
        graph_algo.init(myGraph);
        double result = graph_algo.shortestPathDist(0, 4);
        System.out.println(result);
        assertEquals(20, result);
    }

    @Test
    // Tests two nodes which not connected
    void shortestPathDist3() {
        graph_algo.init(myGraph);
        double result = graph_algo.shortestPathDist(0, 7);
        System.out.println(result);
        assertEquals(-1, result);
    }

    @Test
        // Tests an empty graph
    void shortestPathDist4() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();
        ga.init(g);
        double result = ga.shortestPathDist(0, 7);
        System.out.println(result);
        assertEquals(-1, result);
    }

    @Test
    void shortestPathDist5() {
        graph_algo.init(otherGraph);
        double result = graph_algo.shortestPathDist(0, 3);
        System.out.println(result);
        assertEquals(9.4, result);

        otherGraph.connect(0, 3, 8);
        double resultTwo = graph_algo.shortestPathDist(0, 3);
        System.out.println(resultTwo);
        assertEquals(8, resultTwo);
    }

    @Test
    void shortestPath1() {
        graph_algo.init(myGraph);
        ArrayList<node_data> actual = (ArrayList<node_data>) graph_algo.shortestPath(1, 4);
        ArrayList<node_data> expected = new ArrayList<>();
        expected.add(myGraph.getNode(1));
        expected.add(myGraph.getNode(3));
        expected.add(myGraph.getNode(4));
        assertEquals(expected, actual);
    }

    @Test
    void shortestPath2() {
        graph_algo.init(myGraph);
        ArrayList<node_data> actual = (ArrayList<node_data>) graph_algo.shortestPath(0, 4);
        ArrayList<node_data> expected = new ArrayList<>();
        expected.add(myGraph.getNode(0));
        expected.add(myGraph.getNode(1));
        expected.add(myGraph.getNode(3));
        expected.add(myGraph.getNode(4));
        assertEquals(expected, actual);
        //        System.out.println("Res:" + graph_algo.shortestPath(0, 4).toString());
    }

    @Test
        // Tests two nodes which not connected
    void shortestPath3() {
        graph_algo.init(myGraph);
        ArrayList<node_data> actual = (ArrayList<node_data>) graph_algo.shortestPath(0, 7);
        assertNull(actual);
    }

    @Test
        // Tests an empty graph
    void shortestPath4() {
        DWGraph_Algo ga = new DWGraph_Algo();
        DWGraph_DS g = new DWGraph_DS();
        ga.init(g);
        ArrayList<node_data> actual = (ArrayList<node_data>) graph_algo.shortestPath(0, 7);
        assertNull(actual);
    }

    @Test
    void shortestPath5() {
        graph_algo.init(otherGraph);
        ArrayList<node_data> actual = (ArrayList<node_data>) graph_algo.shortestPath(0, 3);
        ArrayList<node_data> expected = new ArrayList<>();
        expected.add(otherGraph.getNode(0));
        expected.add(otherGraph.getNode(1));
        expected.add(otherGraph.getNode(4));
        expected.add(otherGraph.getNode(3));
        assertEquals(expected, actual);
    }

    @Test
    void saveLoad() {
//        graph_algo.load("data/A1");
//        double result = graph_algo.shortestPathDist(2,6);
//        System.out.println(result);
//        ArrayList<node_data> ans = (ArrayList<node_data>) graph_algo.shortestPath(2,6);
//        System.out.println(ans.toString());
//        graph_algo.init(connected_graph);
//        graph_algo.save("data/testCase1.txt");
//        graph_algo.load("data/testCase1.txt");
    }
}