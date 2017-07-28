package Machine;

/**
 * Created by grahamr9 on 7/28/2017.
 */
//package LayerQueue;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by grahamr9 on 7/25/2017.
 */
public class LayerQueue {
    public ArrayList<ArrayList<String>> layers;

    public LayerQueue(LinkedList<String> tokenQueue) {
        layers = new ArrayList<>();
        ArrayList<String> layer = new ArrayList<>();
        for (String token : tokenQueue) {
            if (!token.equals(";")) layer.add(token);
            else {
                layers.add((ArrayList<String>) layer.clone());
                layer.clear();
            }
        }
    }
}