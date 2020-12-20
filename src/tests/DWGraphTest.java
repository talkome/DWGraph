package tests;

import api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DWGraphTest {
    static directed_weighted_graph copyGraph;
    static dw_graph_algorithms graph_algo;

    public static void main(String[] args) {
        createGraphA();
        createGraphB();
        createGraphC();
    }

    public static void createGraphA() {
        DWGraph_DS graphA = new DWGraph_DS();
        for (int i = 0; i < 5; i++)
            graphA.addNode(new NodeData(i));

        graphA.connect(0, 1, 3);
        graphA.connect(1, 2, 1);
        graphA.connect(2, 0, 20);
        graphA.connect(1, 3, 5);
        graphA.connect(1, 4, 7);

        graph_algo = new DWGraph_Algo(graphA);
        copyGraph = graph_algo.copy();
        graph_algo.getGraph().removeEdge(2, 0);
        int numOfEdges = 4;
        int numOfNodes = 5;
        assertEquals(graph_algo.getGraph().edgeSize(), numOfEdges);
        assertEquals(graph_algo.getGraph().nodeSize(), numOfNodes);

        graph_algo.getGraph().removeEdge(1, 2);
        numOfEdges = 3;
        assertEquals(graph_algo.getGraph().edgeSize(), numOfEdges);
        graph_algo.getGraph().removeNode(2);
        numOfNodes = 4;
        assertEquals(graph_algo.getGraph().nodeSize(), numOfNodes);

        graph_algo.init(copyGraph);
        assertEquals(graph_algo.shortestPathDist(0, 3), 8);

        List<node_data> sp3to0 = graph_algo.shortestPath(3, 0);
        if (sp3to0 != null)
            System.out.println(Arrays.toString(sp3to0.toArray()));
        List<node_data> sp0to3 = graph_algo.shortestPath(0, 3);
        if (sp0to3 != null)
            System.out.println(Arrays.toString(sp0to3.toArray()));

        assertEquals(graph_algo.shortestPathDist(2, 4), 30);
        List<node_data> spSideA = graph_algo.shortestPath(2, 4);
        if (spSideA != null)
            System.out.println(Arrays.toString(spSideA.toArray()));

        assertFalse(graph_algo.isConnected());

        graph_algo.init(graphA);
        assertFalse(graph_algo.isConnected());

        assertEquals(graph_algo.shortestPathDist(0, 4), 10);
        List<node_data> spAns = graph_algo.shortestPath(0, 4);
        if (spAns != null)
            System.out.println(Arrays.toString(spAns.toArray()));
    }

    public static void createGraphB() {
        DWGraph_DS graphB = new DWGraph_DS();
        for (int i = 0; i < 9; i++)
            graphB.addNode(new NodeData(i));

        graphB.connect(0, 1, 3);
        graphB.connect(0, 2, 9);
        graphB.connect(1, 2, 7);
        graphB.connect(1, 3, 5);
        graphB.connect(1, 4, 1);
        graphB.connect(3, 4, 12);
        graphB.connect(5, 4, 11);
        graphB.connect(6, 4, 7);
        graphB.connect(7, 3, 8);
        graphB.connect(8, 2, 10);

        graph_algo = new DWGraph_Algo(graphB);
        System.out.println(graphB);
        copyGraph = graph_algo.copy();
        graph_algo.getGraph().removeNode(1);
        assertEquals(graph_algo.getGraph().edgeSize(), 6);
        assertEquals(graph_algo.getGraph().nodeSize(), 8);
        graph_algo.getGraph().removeEdge(8, 2);
        assertEquals(graph_algo.getGraph().edgeSize(), 5);
        graph_algo.init(copyGraph);
        assertFalse(graph_algo.isConnected());

        assertEquals(graph_algo.shortestPathDist(1, 6), -1);
        List<node_data> copyGraph_sp1to6 = graph_algo.shortestPath(1, 6);
        if (copyGraph_sp1to6 != null)
            System.out.println(Arrays.toString(copyGraph_sp1to6.toArray()));

        double dist0to4 = graph_algo.shortestPathDist(0, 4);
        System.out.println("shortest path: 0,4 dist = " + dist0to4);

        double dist7to8 = graph_algo.shortestPathDist(7, 8);
        System.out.println("shortest path: 7,8 dist = " + dist7to8);
        List<node_data> copyGraph_sp7to8 = graph_algo.shortestPath(7, 8);
        if (copyGraph_sp7to8 != null)
            System.out.println(Arrays.toString(copyGraph_sp7to8.toArray()));

        double dist8to7 = graph_algo.shortestPathDist(8, 7);
        System.out.println("shortest path: 8,7 dist = " + dist8to7);
        List<node_data> copyGraph_sp8to7 = graph_algo.shortestPath(8, 7);
        if (copyGraph_sp8to7 != null)
            System.out.println(Arrays.toString(copyGraph_sp8to7.toArray()));

        graph_algo.init(graphB);
        assertFalse(graph_algo.isConnected());

        double dist3to6 = graph_algo.shortestPathDist(3, 6);
        System.out.println("shortest path: 3,6 dist = " + dist3to6);
        List<node_data> myGraph_sp3to6 = graph_algo.shortestPath(3, 6);
        if (myGraph_sp3to6 != null)
            System.out.println(Arrays.toString(myGraph_sp3to6.toArray()));

        double dist6to3 = graph_algo.shortestPathDist(6, 3);
        System.out.println("shortest path: 6,3 dist = " + dist6to3);
        List<node_data> myGraph_sp6to3 = graph_algo.shortestPath(6, 3);
        if (myGraph_sp6to3 != null)
            System.out.println(Arrays.toString(myGraph_sp6to3.toArray()));

        double dist8to6 = graph_algo.shortestPathDist(8, 6);
        System.out.println("shortest path: 8,6 dist = " + dist8to6);
        List<node_data> myGraph_sp8to6 = graph_algo.shortestPath(8, 6);
        if (myGraph_sp8to6 != null)
            System.out.println(Arrays.toString(myGraph_sp8to6.toArray()));
    }

    private static void createGraphC() {
        DWGraph_DS graphC = new DWGraph_DS();
        for (int i = 0; i < 5; i++)
            graphC.addNode(new NodeData(i));

        graphC.connect(0, 1, 5);
        graphC.connect(0, 2, 30);
        graphC.connect(1, 2, 11);
        graphC.connect(4, 3, 8);

        graph_algo = new DWGraph_Algo(graphC);
        copyGraph = graph_algo.copy();
        graph_algo.init(copyGraph);

        assertFalse(graph_algo.isConnected());

        double distSideA = graph_algo.shortestPathDist(3, 4);
        System.out.println("shortest path: 3,4 dist = " + distSideA);

        List<node_data> spSideA = graph_algo.shortestPath(2, 3);
        if (spSideA != null)
            System.out.println(Arrays.toString(spSideA.toArray()));

        double distSideB = graph_algo.shortestPathDist(0, 3);
        System.out.println("shortest path: 3,2 dist = " + distSideB);
        List<node_data> spSideB = graph_algo.shortestPath(3, 2);
        if (spSideB != null)
            System.out.println(Arrays.toString(spSideB.toArray()));
    }
}
