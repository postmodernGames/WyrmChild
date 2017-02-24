package symbol;

import java.util.ArrayList;

import DFA.DFAnode;

public class Symbol{
	public String category;  //IsTerminal?
	public int number;
	public String token;

	public Symbol(){
		category = "nonTerminal";
	};
	
	public Symbol(char c){
		category = "terminal";
		token = "" + c;
	}

	Symbol(ArrayList<DFAnode> lexicon2, ArrayList<Character> lexicon, char c, String s){
		number = lexicon2.size();
		lexicon2.add(new DFAnode());
		category = "terminal";
		lexicon.add(c);
	}

	public boolean equals(Symbol s){
		if(s == null) return false;
		return category==s.category && number == s.number;
	}
}