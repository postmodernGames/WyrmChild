package DFA;

/**
 * Created by grahamr9 on 7/28/2017.
 */

public class Edge {
    Vertex from;
    Vertex to;
    String label;
    Vertex vertex;

    Edge(Vertex from, Vertex to, String s) {
        this.from = from;
        this.to = to;
        label = s;
    }

    public Edge(Vertex to, String s) {
        this.to = to;
        label = s;
    }
}
