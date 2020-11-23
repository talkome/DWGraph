package tests;

import api.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WGraph_DSTest {
    private static Random rnd = new Random();
    public final double INFINITY = Double.POSITIVE_INFINITY;
    public directed_weighted_graph myGraph = new DWGraph_DS();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 5; i++){
            NodeData newNode = new NodeData(i);
            myGraph.addNode(newNode);
        }

        myGraph.connect(0,1,3);
        myGraph.connect(0,2,1);
        myGraph.connect(1,2,20);
        myGraph.connect(1,3,5);
        myGraph.connect(1,4,7);
    }

    @AfterEach
    void tearDown() {
        Collection<node_data> vertices = myGraph.getV();
        for (node_data currNode : vertices){
            currNode.setInfo("WHITE");
        }
    }

    @Test
    void addNode() {
        int nodeSize = myGraph.nodeSize();
        int edgeSize = myGraph.edgeSize();
        myGraph.removeNode(2);
        int expected = nodeSize-1;
        int actual = myGraph.nodeSize();
        assertEquals(expected, actual);
        expected = edgeSize-2;
        actual = myGraph.edgeSize();
        assertEquals(expected, actual);
    }

    @Test
    void getNode() {
        node_data node1 = myGraph.getNode(2);
        assertNotNull(node1);
        node_data node2 = myGraph.getNode(8);
        assertNull(node2);
    }

    @Test
    void hasEdge() {
        myGraph.connect(0,4,150);
        assertNotNull(myGraph.getEdge(0,4));
        myGraph.removeEdge(0,4);
        assertNull(myGraph.getEdge(0,4));
        assertNull(myGraph.getEdge(200,140));
        assertNotNull(myGraph.getEdge(4,4));
    }

    @Test
    void getEdge() {
        myGraph.connect(2,3,100);
        double w = myGraph.getEdge(2,3).getWeight();
        assertEquals(w, 100);
        assertNotNull(myGraph.getEdge(2,3));
        myGraph.removeEdge(2,3);
        assertNull(myGraph.getEdge(2,3));
        myGraph.connect(3,4,50);
        myGraph.connect(4,3,80);
        assertEquals(myGraph.getEdge(3,4).getWeight(),80);
        assertEquals(myGraph.getEdge(4,4).getWeight(),0);
    }

    @Test
    void connect() {
        int numOfEdges = myGraph.edgeSize();
        myGraph.connect(2,4,100);
        assertNotNull(myGraph.getEdge(2,4));
        assertEquals(myGraph.edgeSize(),numOfEdges+1);
        myGraph.removeEdge(2,4);
        assertNull(myGraph.getEdge(2,4));
        assertEquals(myGraph.edgeSize(),numOfEdges);
        myGraph.connect(0,1,4);
        assertEquals(myGraph.getEdge(0, 1).getWeight(), 4);
        myGraph.connect(11,14,55);
        assertNull(myGraph.getEdge(11,14));
    }

    @Test
    void getV() {
        Collection<node_data> vertices = myGraph.getV();
        int expected = vertices.size();
        int actual = myGraph.nodeSize();
        assertEquals(expected, actual);
    }

    @Test
    void getE() {
        Collection<edge_data> neighbors = myGraph.getE(1);
        HashSet<node_data> actual = new HashSet<>();
        actual.add(myGraph.getNode(0));
        actual.add(myGraph.getNode(2));
        actual.add(myGraph.getNode(3));
        actual.add(myGraph.getNode(4));
        assertEquals(neighbors, actual);
    }

    @Test
    void removeNode() {
        int numOfNodes = myGraph.nodeSize();
        int numOfEdges = myGraph.edgeSize();
        myGraph.removeNode(1);
        assertEquals(numOfNodes-1,myGraph.nodeSize());
        assertEquals(numOfEdges-4,myGraph.edgeSize());
        assertNull(myGraph.getNode(1));
    }

    @Test
    void removeEdge() {
        myGraph.removeEdge(0,2);
        assertNull(myGraph.getEdge(0,2));
    }

    @Test
    void nodeSize() {
        myGraph.removeNode(2);
        int numOfNodes = 4;
        assertEquals(myGraph.nodeSize(),numOfNodes);
    }

    @Test
    void edgeSize() {
        myGraph.removeEdge(0,2);
        int numOfEdges = 4;
        assertEquals(myGraph.edgeSize(),numOfEdges);
    }

    @Test
    void getMC() {
        int firstMC = myGraph.getMC();
        myGraph.addNode(myGraph.getNode(6));
        assertEquals(firstMC + 1, myGraph.getMC());
        myGraph.connect(myGraph.getNode(2).getKey(), myGraph.getNode(3).getKey(), 30);
        assertEquals(firstMC + 2, myGraph.getMC());
        myGraph.removeEdge(myGraph.getNode(2).getKey(), myGraph.getNode(3).getKey());
        assertEquals(firstMC + 3, myGraph.getMC());
        myGraph.removeNode(myGraph.getNode(6).getKey());
        assertEquals(firstMC + 4, myGraph.getMC());
    }

    @Test
    void testMillion() {
        long start = new Date().getTime();
        directed_weighted_graph millionGraph = new DWGraph_DS();
        int i = 0;
        while (millionGraph.nodeSize() < 1000000)
            millionGraph.addNode(new NodeData(i++));
        while (millionGraph.edgeSize() < 10000000) {
            int node1 = rnd.nextInt(1000000);
            int node2 = rnd.nextInt(1000000);
            double weight  = node2 % 10 + 1;
            millionGraph.connect(node1, node2, weight);
        }
        long end = new Date().getTime();
        double runtime = (end-start)/1000.0;
        System.out.println("runtime: " + runtime);

        assertEquals(1000000, millionGraph.nodeSize());
        assertEquals(10000000, millionGraph.edgeSize());
    }
}