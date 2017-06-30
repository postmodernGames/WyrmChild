package State;

import java.util.ArrayList;

import symbol.Symbol;
import symbol.Symbol.SymbolType;

public class State{
	public int ruleIndex;
	public int rulePosition;
	public int inputPosition;
	public String token;
	public State(int x, int y, int z){
		ruleIndex = x;
		rulePosition = y;
		inputPosition = z;
	};
	public State(int x, int y, int z, String s){
		ruleIndex = x;
		rulePosition = y;
		inputPosition = z;
		token =s;
	};
	
	
	public int nextSymbol(ArrayList<ArrayList<Symbol>> Grammar){
		if(Grammar.get(ruleIndex).size()>rulePosition+1){  
			if(Grammar.get(ruleIndex).get(rulePosition+1).symbolType == SymbolType.nonTerminal) 
				return Grammar.get(ruleIndex).get(rulePosition+1).symbolIndex;
		}
		return -1;
	}
	
	public String print(ArrayList<ArrayList<Symbol>> Grammar, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		String ret = "";
		try{
		for(int i=1; i<Grammar.get(ruleIndex).size(); i++){
			if(Grammar.get(ruleIndex).get(i).symbolType==SymbolType.terminal){
				ret+=  lexicon.get(Grammar.get(ruleIndex).get(i).symbolIndex) ;
			}
			else ret += dictionary.get(Grammar.get(ruleIndex).get(i).symbolIndex);
		}
		ret = ret.substring(0,rulePosition) + '@' + ret.substring(rulePosition,ret.length());
		ret = dictionary.get(Grammar.get(ruleIndex).get(0).symbolIndex) + "-> " + ret;
		ret += " : " + ruleIndex + " " + rulePosition + " " + inputPosition;
		return ret;
		} catch(java.lang.IndexOutOfBoundsException e) {
			System.out.println("Error in STATE print");
		}
		return ret;
	}
}