package tests;

import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import api.geo_location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdgeDataTest {
    NodeData n1;
    NodeData n2;
    NodeData n3;
    EdgeData e1;
    EdgeData e2;


    @BeforeEach
    void setUp() {
        n1=new NodeData(0);
        n2=new NodeData(1);
        n3=new NodeData(2);
        e1=new EdgeData(n1.getKey(),n2.getKey(),10);
        e2=new EdgeData(n2.getKey(),n3.getKey(),20);
    }

    @Test
    void getSrc() {
        assertEquals(e1.getSrc(),n1.getKey());
        assertEquals(e2.getSrc(),n2.getKey());
    }

    @Test
    void getDest() {
        assertEquals(e1.getDest(),n2.getKey());
        assertEquals(e2.getDest(),n3.getKey());
    }

    @Test
    void getWeight() {
        assertEquals(10.0,e1.getWeight());
        assertEquals(20.0,e2.getWeight());
    }

    @Test
    void getInfo() {
        assertNull(e1.getInfo());
        e1.setInfo("[src:"+e1.getSrc()+" ,dst:"+e1.getDest()+ ", weight:"+e1.getWeight()+"]");
        assertNotEquals(e1.getInfo(), "");
    }

    @Test
    void getTag() {
        assertEquals(e1.getTag(),e2.getTag());
        e1.setTag(2);
        assertNotEquals(e2.getTag(), e1.getTag());
    }

    @Test
    void testEquals() {
        EdgeData edge = new EdgeData(n1.getKey(),n2.getKey(),200);
        edge.setInfo("HELLO");

        EdgeData copy = new EdgeData(edge);
        assertEquals(edge,copy);
    }
}