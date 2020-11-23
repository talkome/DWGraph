package api;

import java.util.Comparator;

public class Node_Comparator implements Comparator<node_data> {

    @Override
    public int compare(node_data n1, node_data n2) {
        return (int) ((int) n1.getWeight() - n2.getWeight());
    }
}
