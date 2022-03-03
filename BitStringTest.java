import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import org.junit.Before;
public class BitStringTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBitStringConstructor() {
        BitString bitString = new BitString();
        assertNotNull(bitString != null);
        assertEquals(bitString.getLength(), 0);
        assertArrayEquals(bitString.getBits(), null);
    }

    @Test
    public void testSetBitsOverLength() {
        BitString bitString = new BitString();
        try {
            bitString.setBits(new char[17]);
            fail("SetBits failed");
        } catch (IllegalArgumentException ie) {

        }
    }

    @Test
    public void testSetBits() {
        BitString bitString = new BitString();
        char test[] = {'1', '0', '1', '0'};
        bitString.setBits(test);
        assertEquals(bitString.getLength(), 4);
        assertArrayEquals(bitString.getBits(), test);
    }

    @Test
    public void testInvert() {
        char allOnes[] = {'1', '1', '1', '1'};
        char allZeros[] = {'0', '0', '0', '0'};
        BitString bitString = new BitString();
        bitString.setBits(allZeros);
        bitString.invert();
        assertArrayEquals(bitString.getBits(), allOnes);
        bitString.invert();
        assertArrayEquals(bitString.getBits(), allZeros);
    }

    @Test
    public void testAddOne() {
        char allZeros[] = {'0', '0', '0', '0'};
        char one[] = {'0', '0', '0', '1'};
        char two[] = {'0', '0', '1', '0'};
        char allOnes[] = {'1', '1', '1', '1'};
        BitString bitString = new BitString();
        bitString.setBits(allZeros);
        bitString.addOne();
        assertArrayEquals(bitString.getBits(), one);
        bitString.setBits(allOnes);
        bitString.addOne();
        assertArrayEquals(bitString.getBits(), allZeros);
        bitString.setBits(one);
        bitString.addOne();
        assertArrayEquals(bitString.getBits(), two);
    }

    @Test
    public void testSetValueInvalid() {

        BitString bitString = new BitString();
        try {
            bitString.setValue(-10);
            fail("Can set negative value for unsigned");
        } catch (IllegalArgumentException e) {

        }
        try {
            bitString.setValue(65536);
            fail("Can set more than max for unsigned");
        } catch (IllegalArgumentException e) {

        }

    }

    @Test
    public void testSetValue() {
        char ten[] = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '1', '0', '1', '0'};

        BitString bitString = new BitString();
        bitString.setValue(10);
        assertArrayEquals(bitString.getBits(), ten);

    }

    @Test
    public void testSetValue2sComp() {
        char max[] = {'0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
                '1', '1', '1', '1', '1'};
        char min[] = {'1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0'};
        BitString bitString = new BitString();
        bitString.setValue2sComp(32767);
        assertArrayEquals(bitString.getBits(), max);
        bitString.setValue2sComp(-32768);
        assertArrayEquals(bitString.getBits(), min);
    }

    @Test
    public void testSetValue2sCompInvalid() {
        BitString bitString = new BitString();
        try {
            bitString.setValue2sComp(-32769);
            fail("Can set negative value for 2s comp");
        } catch (IllegalArgumentException e) {

        }
        try {
            bitString.setValue2sComp(32768);
            fail("Can set more than max for 2s comp");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetValue() {
        char ten[] = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '1', '0', '1', '0'};
        BitString bitString = new BitString();
        bitString.setBits(ten);
        assertEquals(bitString.getValue(), 10);

    }

    @Test
    public void testGetValue2sComp() {
        char ones[] = {'1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
                '1', '1', '1', '1', '1'};
        char min[] = {'1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0'};
        BitString bitString = new BitString();
        bitString.setBits(ones);
        assertEquals(bitString.getValue2sComp(), -1);
        bitString.setBits(min);
        assertEquals(bitString.getValue2sComp(), -32768);
    }

    @Test
    public void testAppend() {
        char fourBits[] = {'0', '0', '0', '0'};
        char eightBits[] = {'1', '0', '0', '0', '0', '0', '0', '0'};
        char twelveBits[] = {'0', '0', '0', '0', '1', '0', '0', '0', '0', '0',
                '0', '0'};
        BitString bitString = new BitString();
        bitString.setBits(fourBits);
        bitString.display(true);
        BitString anotherBitString = new BitString();
        anotherBitString.setBits(eightBits);
        BitString appendedString = bitString.append(anotherBitString);
        appendedString.display(true);
        assertArrayEquals(appendedString.getBits(), twelveBits);
    }

    @Test
    public void testSubstring() {
        char twelveBits[] = {'0', '0', '0', '0', '1', '0', '0', '0', '0', '0',
                '0', '0'};
        char eightBits[] = {'1', '0', '0', '0', '0', '0', '0', '0'};
        BitString bitString = new BitString();
        bitString.setBits(twelveBits);
        BitString partString = bitString.substring(4, 8);
        assertArrayEquals(partString.getBits(), eightBits);
    }

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