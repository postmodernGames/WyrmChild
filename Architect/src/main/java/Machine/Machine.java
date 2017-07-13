package Machine;

import java.io.FileInputStream;
import java.lang.Character;
import java.util.ArrayList;
import java.util.LinkedList;

public class Machine {
    public LinkedList<String> tokenQueue;
    ArrayList<State> states;
    State current;
    String tempStack;

    public State stateFactory() {
        int x = states.size();
        return new State(x);
    }

    public String closeToken() {
        String ret = tempStack.trim();
        tempStack = "";
        if (!ret.equals("")) tokenQueue.add(ret);
        return ret;
    }

    public void next(Character c) {
        if (current.discardWhitespace && Character.isWhitespace(c))
            current = states.get(current.Whitespace);
        else {
            for (Character e : current.edges) {
                if (e == c) {
                    current = states.get(current.edges.indexOf(e));
                    tempStack += c;
                    if (current.accept) closeToken();
                    return;
                }
            }
            current = states.get(current.Default);
            tempStack += c;
            if (current.accept) closeToken();
        }
    }

    public void initialize() {
        tempStack = "";
        tokenQueue = new LinkedList<String>();
        states = new ArrayList<State>();

        states.add(stateFactory().whitespace(0).Default(1));
        states.add(stateFactory().Default(1).whitespace(2).push('\'', 4).push(';', 3));
        states.add(stateFactory().accept());
        states.add(stateFactory().accept());
        states.add(stateFactory().push('\\', 5).push('\'', 1));
        states.add(stateFactory().Default(4));


        current = states.get(0);
    }

    public void run(FileInputStream grammarFile) {

        try {
            Character c = (char) grammarFile.read();
            while (c != -1) {
                next(c);
                c = (char) grammarFile.read();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    class State {
        ArrayList<Character> edges;
        Integer Default, Whitespace;
        boolean discardWhitespace;
        boolean accept;

        State(int x) {
            accept = false;
            Default = x;
            discardWhitespace = false;
        }

        public State whitespace(Integer i) {
            Whitespace = i;
            discardWhitespace = true;
            return this;
        }

        public State push(Character c, Integer i) {
            edges.set(i, c);
            return this;
        }

        public State accept() {
            accept = true;
            return this;
        }

        public State Default(Integer i) {
            Default = i;
            return this;
        }


    }

}