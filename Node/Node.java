package Node;

import java.util.ArrayList;

import Earley.*;
import State.State;
import symbol.Symbol;

public class Node{
	public Symbol symbol;
	public ArrayList<Node> children;
    public Node(){
    	children = new ArrayList<Node>();
    }
    public Node(Symbol s){
    	symbol = new Symbol();
    	symbol.category = s.category;
    	symbol.token = s.token;
    	symbol.number = s.number;
    	children = new ArrayList<Node>();
    }
	public int buildParseTree(ArrayList<Symbol> production, int stateSetIndex, ArrayList<ArrayList<Symbol>> Grammar, Earley Parser){
		Node current = this;
		int n = production.size()-1;
		current.children = new ArrayList<Node>();
		ensureNodeSize(current.children,n);
		for(int symbolIndex =n-1; symbolIndex >=0; symbolIndex--){
			//System.out.println(production.get(ruleIndex));
			//current.children[ruleIndex] = current;
			current.children.set(symbolIndex, new Node(production.get(symbolIndex+1)));
			//current.children[ruleIndex].value = production.get(ruleIndex);
			if(production.get(symbolIndex+1).category == "nonTerminal"){  //isnonterminal
				for(State s : Parser.StateSet.get(stateSetIndex)){
					if(s.rulePosition == Grammar.get(s.ruleIndex).size()-1){
						if(current.children.get(symbolIndex).symbol.number == Grammar.get(s.ruleIndex).get(0).number){
							stateSetIndex = current.children.get(symbolIndex).buildParseTree(Grammar.get(s.ruleIndex), stateSetIndex, Grammar, Parser);
							break;
						}
					}
				}
			}
			else {
				for(State s : Parser.StateSet.get(stateSetIndex)){
					if(s.token!=null){
						current.children.get(symbolIndex).symbol = new Symbol();
						current.children.get(symbolIndex).symbol.category = "terminal";
						current.children.get(symbolIndex).symbol.number = Grammar.get(s.ruleIndex).get(s.rulePosition).number;
						current.children.get(symbolIndex).symbol.token = s.token;
						//current.children[ruleIndex].value.value = new String(s.token);
						
						System.out.println(s.token);
						break;
					}
				}
				
				stateSetIndex--;
			}
		}
		return stateSetIndex;
	}
	public static void ensureNodeSize(ArrayList<Node> node, int size) {
	    // Prevent excessive copying while we're adding
	    node.ensureCapacity(size);
	    while (node.size() < size) {
	        node.add(new Node());
	    }
	}
	
}