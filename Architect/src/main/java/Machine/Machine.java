package Machine;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Machine {
    public LinkedList<String> tokenQueue;
    ArrayList<State> states;
    State current;
    String tempStack;


    public String closeToken() {
        String ret = tempStack.trim();
        tempStack = "";
        if (!ret.equals("")) tokenQueue.add(ret);
        current = states.get(0);
        return ret;
    }

    public void next(Character c) {
        for (Edge e : current.edges) {
            if (e.regex.match(c)) {
                if (e.pushFlag) {
                    tempStack += c;
                }
                current = states.get(e.stateId);
                if (current.accept) {
                    closeToken();
                    if (c == ';') {
                        tempStack += c;
                        closeToken();
                    }
                }

                return;
            }
        }
    }

    public void initialize() {
        tempStack = "";
        tokenQueue = new LinkedList<String>();
        states = new ArrayList<State>(6);

        states.add(new State().addEdge(1, "[^\\s;]", true).addEdge(0, "\\s", false).addEdge(3, ";", false));
        states.add(new State().addEdge(1, "[^\\s\\\';]", true).addEdge(2, "[\\s]", false).addEdge(3, ";", false).addEdge(4, "\\\'", false));
        states.add(new State().accept());
        states.add(new State().accept());
        states.add(new State().addEdge(5, "\\\'", true).addEdge(1, "\\\'", true));
        states.add(new State().addEdge(4, ".|\\s", true));

        current = states.get(0);
    }

    public void run(FileInputStream grammarFile) {

        try {
            Character c = (char) grammarFile.read();
            while (c != '\uFFFF') {
                next(c);
                c = (char) grammarFile.read();
            }
            closeToken();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    class Regex {
        Pattern pattern;

        Regex(String pat) {
            pattern = Pattern.compile(pat);
        }

        public boolean match(Character c) {
            Matcher matcher = pattern.matcher(c.toString());
            return matcher.matches();
        }
    }

    class Edge {
        int stateId;
        boolean pushFlag;
        Regex regex;

        Edge(int s, String r, boolean pushFlag) {
            stateId = s;
            regex = new Regex(r);
            this.pushFlag = pushFlag;
        }
    }

    class State {
        ArrayList<Edge> edges;
        boolean accept;

        State() {
            edges = new ArrayList<>(6);
        }

        public State addEdge(int s, String r, boolean c) {
            edges.add(new Edge(s, r, c));
            return this;
        }

        public State accept() {
            accept = true;
            return this;
        }


    }

}