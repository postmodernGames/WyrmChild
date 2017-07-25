package LayerQueue;

import Machine.Machine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by grahamr9 on 7/25/2017.
 */
public class LayerQueueTest {

    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream input = new FileInputStream("grammar.txt");
        Machine machine = new Machine();
        machine.initialize();
        machine.run(input);

        LayerQueue layerQueue = new LayerQueue(machine.tokenQueue);
        System.out.println(layerQueue.layers.toString());
    }
}