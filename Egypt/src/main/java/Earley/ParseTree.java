package Earley;

import symbol.Symbol;

import java.util.ArrayList;

/**
 * Created by grahamr9 on 7/28/2017.
 */

class Tree {
    public ArrayList<Symbol> nodes;
    public ArrayList<ArrayList<Integer>> edges;

    ArrayList<Integer> createChildren(int current, int childNum) {
        edges.add(new ArrayList<Integer>());
        for (int i = 0; i < childNum; i++)
            edges.get(current).add(new Integer(-1));
        return edges.get(current);
    }

}

public class ParseTree {
    Tree tree = null;
    ArrayList<ArrayList<State>> StateSet;

    public ParseTree(ArrayList<ArrayList<State>> StateSet) {
        this.StateSet = StateSet;
        tree = new Tree();
        tree.nodes = new ArrayList<Symbol>();
    }

    public void buildParseTree(ArrayList<ArrayList<Symbol>> Grammar, Symbol P, int inputIndex) {
        tree.nodes.add(P);
        buildParseTreeRecursion(0, P, Grammar.get(0), inputIndex - 1, Grammar);
    }


    public int buildParseTreeRecursion(int current, Symbol S, ArrayList<Symbol> production, int stateSetIndex, ArrayList<ArrayList<Symbol>> Grammar) {
        int childNum = production.size() - 1;
        tree.createChildren(current, childNum);
        for (int childIndex = childNum - 1; childIndex >= 0; childIndex--) {
            Symbol C = production.get(childIndex + 1);

            if (C.isNonTerminal()) {
                for (State s : StateSet.get(stateSetIndex)) {
                    if (s.isComplete()) {
                        if (C.symbolIndex == Grammar.get(s.ruleIndex).get(0).symbolIndex) {  //look for completed rules for this nonterminal within the same S[j]  //EXISTENCE AND UNIQUENESS
                            tree.nodes.add(C);
                            tree.edges.get(current).add(new Integer(tree.nodes.size() - 1));
                            stateSetIndex = buildParseTreeRecursion(tree.nodes.size() - 1, C, Grammar.get(s.ruleIndex), stateSetIndex, Grammar);
                            break;
                        }
                    }
                }
            } else {
                for (State s : StateSet.get(stateSetIndex)) {
                    if (s.token != null) {
                        int symbolIndex = Grammar.get(s.ruleIndex).get(s.rulePosition).symbolIndex;
                        Symbol X = new Symbol().terminal().symbolIndex(symbolIndex).token(s.token);
                        tree.nodes.add(X);
                        tree.edges.get(current).add(new Integer(tree.nodes.size() - 1));
                        System.out.println(s.token);
                        break;
                    }
                }

                stateSetIndex--;
            }
        }
        return stateSetIndex;
    }

}
