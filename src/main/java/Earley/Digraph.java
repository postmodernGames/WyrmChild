package Earley;

import java.util.*;


public class Digraph {

    ArrayList<ArrayList<Integer>> edges;

    class partialFunction extends HashMap<String,HashMap<Integer,Object>>{
        public void addFunction(String functionName){
            this.put(functionName,new HashMap<Integer,Object>());
        }

        public void setFunctionValue(String functionName, Integer index, Object value){
            this.get(functionName).put(index,value);
        };
    }

   public  partialFunction nodeFunctions;
   public  partialFunction edgeFunctions;

    Digraph(){
        nodeFunctions = new partialFunction();
        edgeFunctions = new partialFunction();
        edges = new ArrayList<ArrayList<Integer>>();
    }


    public int addNode(){
        edges.add(new ArrayList<>());
        return edges.size();
    }

    public void addNodeWithValue(String functionName, String value){
        int x = addNode();
        nodeFunctions.get(functionName).put(x ,value);
    };


    public Object getNodeValue(String functionName, Integer index){
        return nodeFunctions.get(functionName).get(index) ;
    }

    public void addEdge(Integer parent, Integer child){
        edges.get(parent).add(child);
    }

    public ArrayList<Integer> getChildren(Integer index) {
        return edges.get(index);
    }

    public ArrayList<Integer>  createChildren(Integer parent, Integer num){
        for(int i=0; i<num; i++){
            addEdge(parent, addNode());
        }
        return edges.get(parent);
    }

}