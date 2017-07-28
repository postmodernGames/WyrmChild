package Earley;

import Node.Node;
import SymbolTable.SymbolTable;
import symbol.Symbol;
import symbol.Symbol.SymbolType;

import java.util.ArrayList;
import java.util.ListIterator;

//The output of paring should be a queue that represents a parse tree
//Internally, Earley first builds StateSet
// Then buildParseTree is run to build the parse tree


public class Earley {
    final State F = new State(0, 0, 0);
    public ArrayList<ArrayList<State>> StateSet;
    public int inputIndex;
    State s;
    ArrayList<ArrayList<Symbol>> Grammar;
    SymbolTable nst, tst;
    ArrayList<State> Z = new ArrayList<State>();


    public Earley(ArrayList<ArrayList<Symbol>> grammar2, SymbolTable nonTerminalSymbolTable,
                  SymbolTable terminalSymbolTable) {
        Grammar = grammar2;
        nst = nonTerminalSymbolTable;
        tst = terminalSymbolTable;
        StateSet = new ArrayList<>();
        State.initialize(Grammar);
        reset();
    }

    public static void ensureNodeSize(ArrayList<Node> node, int size) {
        // Prevent excessive copying while we're adding
        node.ensureCapacity(size);
        while (node.size() < size) {
            node.add(new Node());
        }
    }

    public void reset() {
        Z.clear();
        Z.add(F);
        inputIndex = 0;
        StateSet.clear();
        StateSet.add(Z);
    }

    //Is y always a terminal? Shouldn'y I just pass a char
    public boolean parseSymbol(Symbol y) {
        boolean accept = false;
        //for every state in S[inputIndex]
        for (State s : StateSet.get(inputIndex)) {
            if (s.isComplete())                //s = (X-> ...@)
                accept = complete(s, inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary) || accept;
            else {
                if (Grammar.get(s.ruleIndex).get(s.rulePosition + 1).isTerminal()) {
                    // s= ( ..@t..)
                    scan(y, s, inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary);
                    //if(quitFlag) break;
                } else {
                    predict(s, inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary);
                }
            }
        }
        inputIndex++;
        return accept;
    }

    void scan(Symbol y, State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        if (y != null) {
            //	if((Grammar.get(s.ruleIndex).get(s.rulePosition+1).symbolIndex==-1)&&(Grammar.get(s.ruleIndex).get(s.rulePosition+1).equals(y.token))||
            if ((Grammar.get(s.ruleIndex).get(s.rulePosition + 1).symbolIndex == y.symbolIndex) && y.symbolType == SymbolType.terminal) {
                //Create a candidate state to add...
                State t = new State(s.ruleIndex, s.rulePosition + 1, s.inputPosition, y.token);
                //...and if it isn't in the stateSet already, add it.
                if (!inSet(t, stateSet.get(inputIndex + 1))) {
                    stateSet.get(inputIndex + 1).add(t);
                    stateSet.add(new ArrayList<State>());
                }
            }
        }
    }

    boolean inSet(State x, ArrayList<State> stateSet) {
        for (State y : stateSet) {
            if (x.ruleIndex == y.ruleIndex &&
                    x.rulePosition == y.rulePosition &&
                    x.inputPosition == y.inputPosition)
                return true;
        }
        return false;
    }

    void predict(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        for (int ruleIndex = 0; ruleIndex < Grammar.size(); ruleIndex++) {
            //for every rule that expands the nonterminal pointed to by s
            if (Grammar.get(ruleIndex).get(0).symbolIndex == Grammar.get(s.ruleIndex).get(s.rulePosition + 1).symbolIndex) {
                State t = new State(ruleIndex, 0, inputIndex);
                if (!inSet(t, stateSet.get(inputIndex))) {
                    stateSet.get(inputIndex).add(t);
                }
            }
        }
    }

    //s = (X-> ...@)
    boolean complete(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        boolean completeFlag = false;
        int j = s.inputPosition;
        ArrayList<State> Sj = stateSet.get(j);
        ArrayList<State> Sk = stateSet.get(inputIndex);

        ListIterator itr = (ListIterator) Sj.iterator();
        ListIterator itrK = (ListIterator) Sk.iterator();
        while (itr.hasNext()) {
            State t = (State) itr.next();
            //for(int stateIndex=0; stateIndex < stateSet.get(s.inputPosition).size(); stateIndex++){
            if (t.nextSymbol(Grammar) >= 0) {
                if (Grammar.get(s.ruleIndex).get(0).symbolIndex == t.nextSymbol(Grammar)) {
                    //Create a candidate for the state we're about to add
                    State u = new State(t.ruleIndex, t.rulePosition + 1, t.inputPosition);
                    if (u.ruleIndex == 0 && u.rulePosition == 1) completeFlag = true;
                    if (!inSet(u, Sk)) itrK.add(u);
                }
            }
        }
        return completeFlag;
    }

    //Tree needs to do three things:
    //Add child node with value, get current's value, point current


}

class State {
    static ArrayList<ArrayList<Symbol>> Grammar;
    public int ruleIndex;
    public int rulePosition;
    public int inputPosition;
    public String token;

    public State(int x, int y, int z) {
        ruleIndex = x;
        rulePosition = y;
        inputPosition = z;
    }

    public State(int x, int y, int z, String s) {
        ruleIndex = x;
        rulePosition = y;
        inputPosition = z;
        token = s;
    }

    public static void initialize(ArrayList<ArrayList<Symbol>> g) {
        Grammar = g;
    }

    public int nextSymbol(ArrayList<ArrayList<Symbol>> Grammar) {
        if (Grammar.get(ruleIndex).size() > rulePosition + 1) {
            if (Grammar.get(ruleIndex).get(rulePosition + 1).symbolType == SymbolType.nonTerminal)
                return Grammar.get(ruleIndex).get(rulePosition + 1).symbolIndex;
        }
        return -1;
    }

    public boolean matchSymbol(ArrayList<ArrayList<Symbol>> Grammar, Symbol y) {
        int ns = nextSymbol(Grammar);
        int ysi = y.symbolIndex;
        if (ns != -1 && ns == ysi) return true;
        else return false;
    }

    public boolean isComplete() {
        //A completed state is one where s = (X-> ...@)
        return this.rulePosition >= Grammar.get(ruleIndex).size() - 1;
    }

    public String print(ArrayList<ArrayList<Symbol>> Grammar, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        String ret = "";
        try {
            for (int i = 1; i < Grammar.get(ruleIndex).size(); i++) {
                if (Grammar.get(ruleIndex).get(i).symbolType == SymbolType.terminal) {
                    ret += lexicon.get(Grammar.get(ruleIndex).get(i).symbolIndex);
                } else ret += dictionary.get(Grammar.get(ruleIndex).get(i).symbolIndex);
            }
            ret = ret.substring(0, rulePosition) + '@' + ret.substring(rulePosition, ret.length());
            ret = dictionary.get(Grammar.get(ruleIndex).get(0).symbolIndex) + "-> " + ret;
            ret += " : " + ruleIndex + " " + rulePosition + " " + inputPosition;
            return ret;
        } catch (java.lang.IndexOutOfBoundsException e) {
            System.out.println("Error in STATE print");
        }
        return ret;
    }
}