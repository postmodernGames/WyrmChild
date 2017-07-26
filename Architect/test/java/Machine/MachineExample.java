package Machine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by grahamr9 on 7/25/2017.
 */
public class MachineExample {


    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream input = new FileInputStream("grammar.txt");
        Machine machine = new Machine();
        machine.initialize();
        machine.run(input);

        System.out.println(machine.tokenQueue.toString());

    }
}
