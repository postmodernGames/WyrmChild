package Earley;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import SymbolTable.SymbolTable;
import symbol.Symbol;
import symbol.Symbol.SymbolType;
import State.State;

public class Earley {
	public ArrayList<ArrayList<State>> StateSet;
	public int inputIndex;
	State s;
	ArrayList<ArrayList<Symbol >> Grammar;
	SymbolTable nst,tst;
	final State F = new State(0,0,0);
	ArrayList<State> Z = new ArrayList<State>();
	
	
	public void reset(){
		Z.clear();
		Z.add(F);
		inputIndex = 0;
		StateSet.clear();
		StateSet.add(Z);
	}
	
	public Earley(ArrayList<ArrayList<Symbol>> grammar2, SymbolTable nonTerminalSymbolTable,
		SymbolTable terminalSymbolTable){
		Grammar = grammar2;
		nst = nonTerminalSymbolTable;
		tst = terminalSymbolTable;
		StateSet = new ArrayList<>();
		reset();
	}

	public boolean isComplete(State s){
		//A completed state is one where s = (X-> ...@)
		return s.rulePosition>=Grammar.get(s.ruleIndex).size()-1;
	}
	
	
	
	public boolean processSymbol(Symbol y){
		boolean accept = false;
		//for every state in S[inputIndex] 
		for(State s : StateSet.get(inputIndex)){						
			if(isComplete(s)) 				//s = (X-> ...@)
				accept = complete(s,inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary) ||  accept;
			else{
				if(Grammar.get(s.ruleIndex).get(s.rulePosition+1).isTerminal()){
					// s= ( ..@t..)
					scan(y,  s,  inputIndex,  Grammar, StateSet,  nst.dictionary, tst.dictionary);
					//if(quitFlag) break;
				} else {
					predict(s,inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary);
				}
			}	
		}
		inputIndex++;
		return accept;
	}
	
	static void scan(Symbol y, State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet,  ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		if(y!=null){
		//	if((Grammar.get(s.ruleIndex).get(s.rulePosition+1).symbolIndex==-1)&&(Grammar.get(s.ruleIndex).get(s.rulePosition+1).equals(y.token))||
			if((Grammar.get(s.ruleIndex).get(s.rulePosition+1).symbolIndex==y.symbolIndex)&&y.symbolType == SymbolType.terminal)	{
				//Create a candidate state to add...
				State t = new State(s.ruleIndex, s.rulePosition+1, s.inputPosition, y.token);
				//...and if it isn't in the stateSet already, add it.
				if(!inSet(t,stateSet.get(inputIndex+1))){
					stateSet.get(inputIndex+1).add(t);
					stateSet.add( new ArrayList<State>());				
				}
			}
		}
	}

	static boolean inSet(State x, ArrayList<State> stateSet){
		for(State y : stateSet){
			if(		x.ruleIndex == y.ruleIndex &&
					x.rulePosition == y.rulePosition &&
					x.inputPosition == y.inputPosition)			
				return true;
		}
		return false;
	}
	
	static void predict(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet,  ArrayList<Character> dictionary, ArrayList<Character> lexicon ){
		for(int ruleIndex=0; ruleIndex < Grammar.size(); ruleIndex++){
			//for every rule that expands the nonterminal pointed to by s
			if(Grammar.get(ruleIndex).get(0).symbolIndex == Grammar.get(s.ruleIndex).get(s.rulePosition+1).symbolIndex){
				State t = new State(ruleIndex, 0, inputIndex);
				if(!inSet(t,stateSet.get(inputIndex))){
					stateSet.get(inputIndex).add(t);
				}
			}
		}
	}
	
	//s = (X-> ...@)
	static boolean complete(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		boolean completeFlag = false;
		int j = s.inputPosition;
		ArrayList<State> Sj = stateSet.get(j);
		ArrayList<State> Sk = stateSet.get(inputIndex);
		
		ListIterator itr = (ListIterator) Sj.iterator();
		ListIterator itrK = (ListIterator) Sk.iterator(); 
		while(itr.hasNext()){
			State t = (State) itr.next();
		  //for(int stateIndex=0; stateIndex < stateSet.get(s.inputPosition).size(); stateIndex++){
			if(t.nextSymbol(Grammar)>=0){
				if(Grammar.get(s.ruleIndex).get(0).symbolIndex == t.nextSymbol(Grammar)){
					//Create a candidate for the state we're about to add
					State u = new State(t.ruleIndex, t.rulePosition+1, t.inputPosition);
					if(u.ruleIndex==0 && u.rulePosition==1) completeFlag = true;
					if(!inSet(u,Sk)) itrK.add(u);
				}
			}
		}
		return completeFlag;
	}
}