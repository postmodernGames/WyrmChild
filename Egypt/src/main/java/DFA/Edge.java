package DFA;

import Node.Node;

/**
 * Created by grahamr9 on 7/28/2017.
 */

public class Edge {
    Node from;
    Node to;
    String label;

    Edge(Node from, Node to, String s) {
        this.from = from;
        this.to = to;
        label = s;
    }

    public Edge(Node to, String s) {
        this.to = to;
        label = s;
    }
}
