package Node;

import java.util.ArrayList;

import DFA.Edge;
import DFA.Vertex;
import Earley.*;
import symbol.Symbol;

public class Node {
    public Symbol symbol;
    public ArrayList<Node> children;
    public ArrayList<Edge> edges;
    int id;

    public Node() {
        children = new ArrayList<Node>();
    }

    public Node(Symbol s) {
        symbol = new Symbol();
        symbol.symbolType = s.symbolType;
        symbol.token = s.token;
        symbol.symbolIndex = s.symbolIndex;
        children = new ArrayList<Node>();
    }


    void join(Node child, String edgeLabel) {
        Edge e = new Edge(child, edgeLabel);
        edges.add(e);
//	    	child.parent = this;
    }


    public void detach(Node v) {
        for (Edge e : edges) {
            if (e.vertex == v) {
                edges.remove(e);
                break;
            }
        }
    }

}