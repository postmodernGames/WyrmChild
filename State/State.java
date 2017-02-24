package State;

import java.util.ArrayList;

import symbol.Symbol;

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
	public String print(ArrayList<ArrayList<Symbol>> Grammar, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		String ret = "";
		try{
		for(int i=1; i<Grammar.get(ruleIndex).size(); i++){
			if(Grammar.get(ruleIndex).get(i).category=="terminal"){
				ret+=  lexicon.get(Grammar.get(ruleIndex).get(i).number) ;
			}
			else ret += dictionary.get(Grammar.get(ruleIndex).get(i).number);
		}
		ret = ret.substring(0,rulePosition) + '@' + ret.substring(rulePosition,ret.length());
		ret = dictionary.get(Grammar.get(ruleIndex).get(0).number) + "-> " + ret;
		ret += " : " + ruleIndex + " " + rulePosition + " " + inputPosition;
		return ret;
		} catch(java.lang.IndexOutOfBoundsException e) {
			System.out.println("MERROW");
		}
		return ret;
	}
}