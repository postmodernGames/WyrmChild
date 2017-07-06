package Machine;

import java.lang.Character;
import java.util.ArrayList;

public  class Machine {
    class State {
        ArrayList<Char> out;
        ArrayList<Char> close;
        Integer auto;

        public State out(Character c, Integer i){
            out.set(i, c);

            return this;
        }

        public State close(Character c){
            ignore.add(c);
            return this;
        }

        public State auto(Integer i) {
            auto = i;
            return this;
        }

        public State stateFactory(){
            return new State();
        }
    }


    ArrayList<State> states;
    State current;
    String tempStack;
    LinkedList<String> tokenQueue;

    public String closeToken(Character c) {
        String ret = tempStack.clone().trim();
        tempStack = "";
        if(!ret.equals("")) tokenQueue.add(ret);
        return ret;
    }

    public next(Character c){
        if(current.close.contains(c)){
            closeToken(c);
            tempStack += c;
            current = states.get(0);
        }
        else {
            tempStack += c;
            if(current.out.contains(c)){
                current = states.get(current.out.indexOf(c));
            }
            else current = states.get(current.auto);
        }
    }

    public initialize(){
        tempStack = "";
        tokenQueue = new LinkedList<String>();
        states = new ArrayList<State>();

        states.add(stateFactory().out('\'', 1).close(' ').auto(0).close(';');
        states.add(stateFactory().out('\'',0).out('\\',2).auto(1));
        states.add(stateFactory().auto(1));

        current = states.get(0);
    }


}