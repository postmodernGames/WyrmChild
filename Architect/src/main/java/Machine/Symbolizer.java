package Machine;

import symbol.Symbol;

import java.util.ArrayList;

/**
 * Created by grahamr9 on 7/28/2017.
 */
public class Symbolizer {
    public ArrayList<ArrayList<Symbol>> symbolMatrix;
    ArrayList<ArrayList<String>> layerQueue;

    public Symbolizer(ArrayList<ArrayList<String>> layerQueue) {
        this.layerQueue = layerQueue;
    }

    public void categorizeTokens() {
        for (ArrayList<String> al1 : layerQueue) {
            symbolMatrix.add(new ArrayList<Symbol>());
            for (String token : al1) {
                token = token.trim();
                if (token.charAt(0) == '\'') {
                    symbolMatrix.get(symbolMatrix.size() - 1).add(new Symbol(token.charAt(1)));
                } else
                    symbolMatrix.get(symbolMatrix.size() - 1).add(new Symbol(token));
            }
        }
    }
}
