package NFA;

import Earley.ParseTree;
import symbol.Symbol;

import java.util.ArrayList;

/**
 * Created by grahamr9 on 7/28/2017.
 */


class Bubble {
    Vertex start;
    Vertex end;
    Vertex left;
    Vertex right;

    Bubble(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
        left = new Vertex();
        right = new Vertex();
        start.join(left, "");
        right.join(end, "");
    }
}


public class NFABuilder {
    ParseTree parseTree;
    ArrayList<ArrayList<Symbol>> Grammar;


    public NFABuilder(ArrayList<ArrayList<Symbol>> Grammar, ParseTree parseTree) {
        this.parseTree = parseTree;
        this.Grammar = Grammar;
        Vertex start = new Vertex();//Vertex start = new Vertex();
        Vertex accept = new Vertex();
        accept.accept = true;
        Bubble bubble = new Bubble(start, accept);
        Thompson(bubble, 0);

    }


    public boolean checkProduction(Bubble v, ArrayList<Symbol> rule) {
        if (!tree.nodes(v.node).equals(rule.get(0))) return false;
        //replace below with rule.RHS == v.node.children
        if (v.node.children.size() != rule.size() - 1) return false;
        for (int j = 0; j < v.node.children.size(); j++) {
            if (v.node.children.get(j).symbol.equals(rule.get(j + 1)) == false) return false;
        }
        return true;
    }

    public void printLevel(int level, int index, Bubble v) {
        String space = "";
        for (int i = 0; i < level; i++) {
            space += ".";
        }
        System.out.print(space + v.start.id + " " + v.end.id + " r " + index + ", " + tree.nodes(v.node).token + "-->");
        for (int j = 0; j < v.node.children.size(); j++) {
            System.out.print(v.node.children.get(j).symbol.token + " ");
        }
        System.out.println("");
    }

    public void Thompson(int node, Bubble v, int level) {
        outer:
        for (int index = 0; index < Grammar.size(); index++) {   //for each Grammar rule
            if (checkProduction(v, Grammar.get(index))) {  //the node and its children represent rule index
                switch (index) {
                    case 0:  //S->R
                    case 1: //R->D
                    case 3: //D->K
                    case 7: //K->C
                        printLevel(level, index, v);
                        node = parseTree.nodes.get(node);

                        v.node.children.get(0);
                        Thompson(v, level + 1);
                        break outer;
                    case 2:  //Disjunct
                        printLevel(level, index, v);
                        Bubble sibling1 = new Bubble(v.node.children.get(0), v.start, v.end);
                        Bubble sibling2 = new Bubble(v.node.children.get(2), v.start, v.end);

                        Thompson(sibling1, level + 1);
                        Thompson(sibling2, level + 1);
                        break outer;
                    case 5:  //Star
                        printLevel(level, index, v);
                        Bubble insideS = new Bubble(v.node.children.get(0), v.start, v.end);
                        v.start.join(insideS.end, "");
                        insideS.end.join(v.end, "");
                        v.end.join(insideS.start, "");
                        insideS.end.join(v.start, "");
                        Thompson(insideS, level + 1);
                        break outer;
                    case 6:  //Parentheses
                        printLevel(level, index, v);
                        v.node = v.node.children.get(1);
                        Thompson(v, level + 1);
                        break outer;
                    case 4:   //Concatenation
                        printLevel(level, index, v);
                        Bubble second = new Bubble(v.node.children.get(1), v.start, v.end);
                        v.node = v.node.children.get(0);
                        for (Edge e : v.end.edges)
                            second.end.edges.add(e);
                        v.end.edges.clear();
                        v.end.join(second.start, "");
                        Thompson(v, level + 1);
                        System.out.println(v.end.id + "-> " + second.start.id);
                        Thompson(second, level + 1);

                        break outer;
                    case 8:   //Terminal
                        printLevel(level, index, v);
                        v.start.join(v.end, v.node.children.get(0).symbol.token);
                        break outer;
                    case 9:  //Question
                        printLevel(level, index, v);
                        v.start.join(v.end, "");
                        Bubble insideQ = new Bubble(v.node.children.get(0), v.start, v.end);
                        v.start.join(insideQ.start, "");
                        insideQ.end.join(v.end, "");
                        Thompson(insideQ, level + 1);
                        break outer;
                    case 10:   //Not
                        printLevel(level, index, v);
                        v.start.join(v.end, "^" + v.node.children.get(2).symbol.token);
                        break outer;
                    case 11:   //
                        printLevel(level, index, v);
                        v.node = v.node.children.get(1);
                        Thompson(v, level + 1);
                        break outer;
                    case 12:   //Range
                        printLevel(level, index, v);
                        v.start.join(v.end, v.node.children.get(0).symbol.token + v.node.children.get(2).symbol.token);
                        break outer;
                }
            }
        }
    }
}
