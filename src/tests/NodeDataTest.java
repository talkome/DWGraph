package tests;

import api.DWGraph_DS;
import api.GeoLocation;
import api.NodeData;
import api.geo_location;
import gameClient.util.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeDataTest {
    NodeData[] nodes = new NodeData[3];

    @BeforeEach
    void setUp() {
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = new NodeData(i);
    }

    @Test
    void getKey() {
        DWGraph_DS g = new DWGraph_DS();
        g.addNode(nodes[0]);
        g.addNode(nodes[1]);
        assertEquals(0, nodes[0].getKey());
        assertEquals(1, nodes[1].getKey());
    }

    @Test
    void getLocation() {
        geo_location p = new GeoLocation(5,3,6);
        nodes[2].setLocation(p);
        assertEquals(p.toString(),nodes[2].getLocation().toString());
    }

    @Test
    void getInfo() {
        nodes[0].setInfo("INFO");
        assertEquals("INFO",nodes[0].getInfo());
    }

    @Test
    void getTag() {
        nodes[0].setTag(10);
        assertEquals(10,nodes[0].getTag());
    }

    @Test
    void getSinker() {
        nodes[0].setSinker(10);
        nodes[1].setSinker(30);
        nodes[2].setSinker(20);
        boolean flag1 = 10 == nodes[0].getSinker();
        boolean flag2 = 20 == nodes[1].getSinker();
        boolean flag3 = 30 == nodes[2].getSinker();
        assertTrue(flag1);
        assertFalse(flag2);
        assertFalse(flag3);
    }

    @Test
    void testEquals() { // TODO: set test
    }
}