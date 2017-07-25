package Machine;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;

import static org.mockito.Mockito.*;

/**
 * Created by grahamr9 on 7/11/2017.
 */
public class MachineTest {
    Machine machine;
    FileInputStream in;


    @Before
    public void before() {
        machine = new Machine();
        machine.initialize();

        FileInputStream in = mock(FileInputStream.class);


    }

    @Test
    public void testRun() throws Exception {
        when(in.read()).thenReturn(66).thenReturn(65);
        machine.run(in);

    }


}