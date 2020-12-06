package api;

import java.util.Comparator;

public class Node_Comparator implements Comparator<NodeData> {

    @Override
    public int compare(NodeData n1, NodeData n2) {
        return (int) ((int) n1.getSinker() - n2.getSinker());
    }
}
