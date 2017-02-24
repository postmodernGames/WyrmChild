package Earley;

import java.util.ArrayList;

import SymbolTable.SymbolTable;
import symbol.Symbol;
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

	public boolean processSymbol(Symbol y){
		boolean completeFlag = false;
		for(int stateIndex=0; stateIndex < StateSet.get(inputIndex).size(); stateIndex++){
								
			s = StateSet.get(inputIndex).get(stateIndex);
	//		System.out.println("Working state: " + s.print(Grammar, nst.dictionary, tst.dictionary) + "; InputIndex: " + inputIndex);
			if(s.rulePosition<Grammar.get(s.ruleIndex).size()-1){
				if(Grammar.get(s.ruleIndex).get(s.rulePosition+1).category != "nonTerminal"){
					// s= ( ..@t..)
					scan(y,  s,  inputIndex,  Grammar, StateSet,  nst.dictionary, tst.dictionary);
					//if(quitFlag) break;
				} else {
					predict(s,inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary);
				}
			} else {
				//s = (X-> ...@)
				completeFlag = complete(s,inputIndex, Grammar, StateSet, nst.dictionary, tst.dictionary) ||  completeFlag;
			}
		}
		inputIndex++;
		return completeFlag;
	}
	
	static void scan(Symbol y, State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet,  ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		if(y!=null){
			if((Grammar.get(s.ruleIndex).get(s.rulePosition+1).number==-1)&&(Grammar.get(s.ruleIndex).get(s.rulePosition+1).equals(y.token))||
			   (Grammar.get(s.ruleIndex).get(s.rulePosition+1).number==y.number))
			{			
				ArrayList<State> states = new ArrayList<State>();
				stateSet.add(states);
				State t = new State(s.ruleIndex, s.rulePosition+1, s.inputPosition, y.token);
				if(notInSet(t,stateSet.get(inputIndex+1))){
			//		System.out.println("Scanning, adding " + t.print(Grammar,dictionary,lexicon) + " to S[" + (inputIndex+1) + "]");
					stateSet.get(inputIndex+1).add(t);
				}
			}
		}
	}

	static boolean notInSet(State x, ArrayList<State> stateSet){
		for(State y : stateSet){
			if(		x.ruleIndex == y.ruleIndex &&
					x.rulePosition == y.rulePosition &&
					x.inputPosition == y.inputPosition)			
				return false;
		}
		return true;
	}
	
	static void predict(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet,  ArrayList<Character> dictionary, ArrayList<Character> lexicon ){
		for(int ruleIndex=0; ruleIndex < Grammar.size(); ruleIndex++){
		//	System.out.println("The LHS nonterminal " + Grammar.get(ruleIndex).get(0).number); //The LHS nonterminal 
		//	System.out.println("The symbol the state refers to " + Grammar.get(s.ruleIndex).get(s.rulePosition+1).number); //The symbol the state refers to
			if(Grammar.get(ruleIndex).get(0).number == Grammar.get(s.ruleIndex).get(s.rulePosition+1).number){
				State t = new State(ruleIndex, 0, inputIndex);
				if(notInSet(t,stateSet.get(inputIndex))){
					stateSet.get(inputIndex).add(t);
			//		System.out.println("Predicting, adding " + 	t.print(Grammar,dictionary,lexicon)  + " to S[" + inputIndex + "]");
				}
			}
		}
	}
	
	//s = (X-> ...@)
	static boolean complete(State s, int inputIndex, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<ArrayList<State>> stateSet, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		boolean completeFlag = false;
		for(int stateIndex=0; stateIndex < stateSet.get(s.inputPosition).size(); stateIndex++){
			//	System.out.println("The LHS nonterminal " + Grammar.get(ruleIndex).get(0).number); //The LHS nonterminal 
			//	System.out.println("The symbol the state refers to " + Grammar.get(s.ruleIndex).get(s.rulePosition+1).number); //The symbol the state refers to
			if(Grammar.get(stateSet.get(s.inputPosition).get(stateIndex).ruleIndex).size()>stateSet.get(s.inputPosition).get(stateIndex).rulePosition+1){  //ensure there is a character to the right of the @
				if(Grammar.get(stateSet.get(s.inputPosition).get(stateIndex).ruleIndex).get(stateSet.get(s.inputPosition).get(stateIndex).rulePosition+1).category=="nonTerminal"){
					if(Grammar.get(s.ruleIndex).get(0).number == Grammar.get(stateSet.get(s.inputPosition).get(stateIndex).ruleIndex).get(stateSet.get(s.inputPosition).get(stateIndex).rulePosition+1).number){
						State t = new State(stateSet.get(s.inputPosition).get(stateIndex).ruleIndex, stateSet.get(s.inputPosition).get(stateIndex).rulePosition+1, stateSet.get(s.inputPosition).get(stateIndex).inputPosition);
						if(notInSet(t,stateSet.get(inputIndex))){
			//				System.out.println("Completing, adding " + 	t.print(Grammar,dictionary,lexicon)  + " to S[" + inputIndex + "]");
							if(t.ruleIndex==0 && t.rulePosition==1){
								completeFlag = true;
								//break;
							}
							stateSet.get(inputIndex).add(t);
						}
					}
				}
			}
		}
		return completeFlag;
	}
}