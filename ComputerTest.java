import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
public class ComputerTest {

    @Test
    public void testNOT() {
        Computer computer = new Computer();
        BitString instruction = new BitString();
        instruction.setBits("1001001001111111".toCharArray());
        computer.loadWord(0, instruction); // not R1, R1 holds the value of 1

        BitString halt = new BitString();       //halt
        halt.setBits("1111000000100101".toCharArray());
        computer.loadWord(1, halt);

        BitString result = new BitString();
        result.setBits("1111111111111110".toCharArray()); // result should be 10
        computer.execute();
        assertEquals(result.getValue2sComp(), computer.getRegister(1).getValue2sComp());

    }

    @Test
    public void testAdd() {
        Computer computer = new Computer();

        BitString instruction = new BitString();
        instruction.setBits("0001001001100101".toCharArray());
        computer.loadWord(0, instruction); // adds five to register one, result should be in register one

        BitString halt = new BitString();       //halt
        halt.setBits("1111000000100101".toCharArray());
        computer.loadWord(1, halt);

        BitString result = new BitString();
        result.setBits("000000000110".toCharArray()); // result should be 6
        computer.execute();
        assertEquals(result.getValue2sComp(), computer.getRegister(1).getValue2sComp());

    }

    @Test
    public void testAnd() {
        Computer computer = new Computer();

        BitString instruction = new BitString();
        instruction.setBits("0101001010000010".toCharArray());
        computer.loadWord(0, instruction);

        BitString halt = new BitString();       //halt
        halt.setBits("1111000000100101".toCharArray());
        computer.loadWord(1, halt);

        BitString result = new BitString();
        result.setBits("0000000000000010".toCharArray());       //the result of R2 and R2 should be this
        computer.execute();
        assertEquals(result.getValue2sComp(), computer.getRegister(1).getValue2sComp());

    }

    @Test
    public void testLD() {
        Computer computer = new Computer();

        BitString instruction = new BitString();
        instruction.setBits("0010001000000001".toCharArray());
        computer.loadWord(0, instruction);
        BitString data = new BitString();
        data.setBits("0011111100001010".toCharArray());
        computer.loadWord(2, data);

        BitString halt = new BitString();       //halt
        halt.setBits("1111000000100101".toCharArray());
        computer.loadWord(1, halt);

        BitString result = new BitString();
        result.setBits("0011111100001010".toCharArray());       //R1 should hold this value after the LD
        computer.execute();
        assertEquals(result.getValue2sComp(), computer.getRegister(1).getValue2sComp());

    }

    @Test
    public void testBR() {
        Computer computer = new Computer();

        BitString add = new BitString();
        add.setBits("0001001001100101".toCharArray());
        computer.loadWord(0, add);

        BitString instruction = new BitString();
        instruction.setBits("0000001000000010".toCharArray());      //if positive should down 2 positions
        computer.loadWord(1, instruction);

        BitString add2 = new BitString();
        add2.setBits("0001001001100101".toCharArray());
        computer.loadWord(4, add2);

        BitString halt = new BitString();       //halt
        halt.setBits("1111000000100101".toCharArray());
        computer.loadWord(5, halt);

        BitString result = new BitString();
        result.setBits("0000000000001011".toCharArray());   //the value in R1 after adding 5 twice

        computer.execute();
        assertEquals(result.getValue2sComp(), computer.getRegister(1).getValue2sComp());

    }
}