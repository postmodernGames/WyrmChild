package NFA;

import Earley.ParseTree;
import symbol.Symbol;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashSet;


class Vertex {
    boolean accept;
    int id;
    ArrayList<Edge> edges;

    public void join(Vertex child, String label) {
        edges.add(new Edge(child, label));
    }
}


public class DFAnode extends Vertex {
    ArrayList<Vertex> Bubblenodes;
    int number;

    public DFAnode() {
        Bubblenodes = new ArrayList<Vertex>();
    }

    public static DFAnode generateDFA(ParseTree parseTree, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        Vertex start = new Vertex();//Vertex start = new Vertex();
        Vertex accept = new Vertex();
        accept.accept = true;

        Bubble bubble = new Bubble(0, start, accept);
        Thompson(bubble, Grammar, 0);
        ArrayList<ArrayList<Integer>> stack = new ArrayList<>();
        System.out.println("************ DFAnode calling printDFA for Bubble**************");
        printDFA(stack, start, dictionary, lexicon);
        System.out.println("************ DFAnode leaving printDFA for Bubble**************");


        ArrayList<DFAnode> DFA = new ArrayList<DFAnode>();
        DFAnode dnode = new DFAnode();
        dnode.Bubblenodes.add(start);
        dnode.eclosure();
        DFA.add(dnode);

        for (int i = 0; i < DFA.size(); i++) {
            DFAnode.convertBubbletoDFA(DFA, DFA.get(i));
        }

        stack.clear();
        System.out.println("************ DFAnode calling printDFA**************");
        printDFA(stack, dnode, dictionary, lexicon);
        System.out.println("************ DFAnode leaving printDFA**************");
        return dnode;
    }


    public static DFAnode easyDFA(String label) {
        Vertex start = new Vertex();//Vertex start = new Vertex();
        Vertex accept = new Vertex();
        accept.accept = true;
        Vertex u = new Vertex();
        Vertex v = new Vertex();
        start.join(u, "");
        u.join(v, label);
        v.join(accept, "");
        DFAnode dnode = new DFAnode();
        dnode.Bubblenodes.add(start);
        dnode.eclosure();

        ArrayList<DFAnode> DFA = new ArrayList<DFAnode>();
        DFA.add(dnode);
        for (int i = 0; i < DFA.size(); i++) {
            DFAnode.convertBubbletoDFA(DFA, DFA.get(i));
        }
        return dnode;
    }

    /*
    * asserts this node's symbol and its children's symbol refer to the rule
     */
    public static boolean checkProduction(Bubble v, ArrayList<Symbol> rule) {
        if (!tree.nodes(v.node).equals(rule.get(0))) return false;
        //replace below with rule.RHS == v.node.children
        if (v.node.children.size() != rule.size() - 1) return false;
        for (int j = 0; j < v.node.children.size(); j++) {
            if (v.node.children.get(j).symbol.equals(rule.get(j + 1)) == false) return false;
        }
        return true;
    }

    public static void printLevel(int level, int index, Bubble v) {
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


    public static void convertBubbletoDFA(ArrayList<DFAnode> DFA, DFAnode dnode) {
        HashSet<String> Completed = new HashSet<String>();
        for (Vertex v : dnode.Bubblenodes) {
            for (Edge e : v.edges) {
                if (!e.label.equals("")) {
                    if (!Completed.contains(e.label)) {
                        DFAnode e_succ = new DFAnode();
                        for (Vertex u : dnode.Bubblenodes) {
                            for (Edge f : u.edges) {
                                if (f.label.equals(e.label)) {
                                    e_succ.Bubblenodes.add(f.vertex);
                                }
                                break;
                            }
                        }
                        Completed.add(e.label);
                        e_succ.eclosure();
                        boolean match = false;
                        for (DFAnode y : DFA) {
                            if (y.Bubblenodes.equals(e_succ.Bubblenodes)) {
                                match = true;
                                Edge e_succEdge = new Edge(y, e.label);
                                dnode.edges.add(e_succEdge);
                                break;
                            }
                        }
                        if (!match) {
                            Edge e_succEdge = new Edge(e_succ, e.label);
                            dnode.edges.add(e_succEdge);
                            DFA.add(e_succ);
                        }
                    }
                }
            }
        }
    }

    public static void ensureSize(ArrayList<String> list, int size) {
        // Prevent excessive copying while we're adding
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add("");
        }
    }

    public static void printBubble(ArrayList<ArrayList<Integer>> stack, Vertex current, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        int a, b;
        for (Edge e : current.edges) {
            a = current.hashCode();
            b = e.vertex.hashCode();
            if (a > b) {
                int c = b;
                b = a;
                a = c;
            }
            boolean match = false;
            for (ArrayList<Integer> s : stack) {
                if ((s.get(0) == a) && (s.get(1) == b)) {
                    match = true;
                }
            }
            if (!match) {
                System.out.println(a + "-> " + b);
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(a);
                temp.add(b);
                stack.add(temp);
                for (Edge e1 : current.edges) {
                    printBubble(stack, e1.vertex, dictionary, lexicon);
                }
            }
        }
    }

    public static void printDFA(ArrayList<ArrayList<Integer>> stack, Vertex current, ArrayList<Character> dictionary, ArrayList<Character> lexicon) {
        int a, b, c;

        for (Edge e : current.edges) {
            a = current.id;
            b = e.vertex.id;
            c = e.label.hashCode();
            boolean match = false;
            for (ArrayList<Integer> s : stack) {
                if ((s.get(0) == a) && (s.get(1) == b) && (s.get(2) == c)) {
                    match = true;
                }
            }
            if (!match) {
                if (current.accept)
                    System.out.println(a + "--> " + (e.label.equals(" ") ? "_" : e.label) + " -->" + b + "   *");
                System.out.println(a + "--> " + (e.label.equals(" ") ? "_" : e.label) + " -->" + b);
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(a);
                temp.add(b);
                temp.add(c);
                stack.add(temp);
                for (Edge e1 : current.edges) {
                    printDFA(stack, e1.vertex, dictionary, lexicon);
                }
            }
        }
    }

    public static Symbol giveToken(PushbackInputStream data, ArrayList<DFAnode> recognizers) {
        Symbol y = new Symbol();
        y.symbolType = Symbol.SymbolType.terminal;

        boolean match = true;
        String lastgood = "";
        String accum = "";
        char c = '0';
        int index = 0;
        Vertex v = (Vertex) recognizers.get(index);
        String j = "";
        int i = 0;

        outer:
        while (true) {
            if (match) {
                j = "";
                c = readChar(data);
                while (c == '\r' || c == '\n' || c == '\t' || (accum.equals("") && c == ' ')) {
                    c = readChar(data);
                }
                if (c == '\\') {
                    c = readChar(data);
                    j = "\\" + c;
                } else j = "" + c;
                accum += c;
                match = false;
            }
            if (!(i == -1 || i == 255)) {
                for (Edge e : v.edges) {
                    if (e.label.equals(j) || (e.label.charAt(0) == '^' && e.label.charAt(1) != c) || ((c >= e.label.charAt(0) && c <= e.label.charAt(1)))) {  //so far so good
                        if (e.vertex.accept) {     //so far, so better: we are at an acceptance state, so save the accumulated string--it will be our token if we can't make a longer one
                            lastgood = accum;
                        }
                        v = e.vertex;
                        match = true;
                        break;  //don't bother testing the remaining edges; this is a DFA, so there is only one edge with this label.
                    }
                }
            }
            if (!match) {  //accum isn't recognized
                int z = accum.length() - lastgood.length();
                while (z > 0) {
                    try {
                        data.unread(accum.charAt(accum.length() - z));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    z--;
                }
                accum = "";
                match = true;
                if (lastgood != "") {  //this is our recognizers, we've just come to far
                    y.token = lastgood;
                    y.symbolIndex = index;
                    break outer;  //we've got our token; let's exit
                } else {
                    index++;
                    if (index == recognizers.size()) return null;
                    v = (Vertex) recognizers.get(index);
                }
            }
        } //end while

        return y;
    }

    static char readChar(PushbackInputStream data) {
        char c = 0xffff;
        byte b;
        int j, k;
        String text = null;
        int count = 0;

        try {
            b = (byte) data.read();
            text = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            j = (b < 0 ? 256 + b : b);
            if (((1 << 7) & j) != 0) {  //1xxxxxx
                if (((1 << 5) & j) != 0) { //111xxxxx
                    if (((1 << 4) & j) != 0) { //1111xxxx
                        count = 4;
                    } else { //1110
                        count = 3;
                    }
                } else { //110
                    count = 2;
                }
            } else { //0
                count = 1;
            }
            byte[] bytes = new byte[count];
            bytes[0] = b;
            for (int i = 1; i < count; i++) {
                b = (byte) data.read();
                bytes[i] = b;
            }
            text = new String(bytes, "UTF-8");
            c = text.charAt(0);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return c;
    }

    public void eclosure() {
        for (int v = 0; v < this.Bubblenodes.size(); v++) {
            for (int e = 0; e < this.Bubblenodes.get(v).edges.size(); e++) {
                if ((this.Bubblenodes.get(v).edges.get(e).label == "") && !this.Bubblenodes.contains(this.Bubblenodes.get(v).edges.get(e).vertex)) {
                    this.Bubblenodes.add(this.Bubblenodes.get(v).edges.get(e).vertex);
                    if (this.Bubblenodes.get(v).edges.get(e).vertex.accept) this.accept = true;
                    this.eclosure();
                }
            }
        }
    }

}
