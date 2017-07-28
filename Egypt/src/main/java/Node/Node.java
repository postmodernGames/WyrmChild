package Node;

import DFA.Edge;
import symbol.Symbol;

import java.util.ArrayList;

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


}