package Node;

import java.util.ArrayList;

import DFA.Edge;
import DFA.Vertex;
import Earley.*;

import symbol.Symbol;

public class Node{
	int id;
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


	
	 void join(Node child, String s, boolean sign){
	    	Edge e = new Edge(child,s,sign);
	    	edges.add(e);
//	    	child.parent = this;
	    }
	 
	 	
	    public void detach(Node v){
			for(Edge e : edges){
				if(e.vertex == v){
					edges.remove(e);
					break;
				}
			}
		}
	    
}