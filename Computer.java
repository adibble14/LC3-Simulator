/**
 * Computer class comprises of memory, registers, cc and
 * can execute the instructions based on PC and IR 
 * @author mmuppa
 *
 */
public class Computer {
    private final static int MAX_MEMORY = 50;
    private final static int MAX_REGISTERS = 8;
    private BitString mRegisters[];     //registers
    private BitString mMemory[];        //memory
    private BitString mPC;      //program counter
    private BitString mIR;      //instruction register
    private BitString mCC;      //condition code

    /**
     * Initializes all the memory to 0, registers to 0 to 7
     * PC, IR to 16 bit 0s and CC to 000
     * Represents the initial state
     */
    public Computer() {
        mPC = new BitString();
        mPC.setValue(0);
        mIR = new BitString();
        mIR.setValue(0);
        mCC = new BitString();
        mCC.setBits(new char[]{'0', '0', '0'});
        mRegisters = new BitString[MAX_REGISTERS];
        for (int i = 0; i < MAX_REGISTERS; i++) {
            mRegisters[i] = new BitString();
            mRegisters[i].setValue(i);
        }

        mMemory = new BitString[MAX_MEMORY];
        for (int i = 0; i < MAX_MEMORY; i++) {
            mMemory[i] = new BitString();
            mMemory[i].setValue(0);
        }
    }

    /**
     * Loads a 16 bit word into memory at the given address.
     *
     * @param address memory address
     * @param word    data or instruction or address to be loaded into memory
     */
    public void loadWord(int address, BitString word) {
        if (address < 0 || address >= MAX_MEMORY) {
            throw new IllegalArgumentException("Invalid address");
        }
        mMemory[address] = word;
    }


    /**
     * gets a register specified by the int
     * @param i     the int
     * @return  a bitstring that represents the register
     */
    public BitString getRegister(int i) {
        BitString register = new BitString();
        register = mRegisters[i];
        return register;
    }
    /**
     * Performs not operation by using the data from the register based on
     * bits[7:9]
     * and inverting and storing in the register based on bits[4:6]
     */
    public void executeNot() {
        BitString destBS = mIR.substring(4, 3);
        BitString sourceBS = mIR.substring(7, 3);
        mRegisters[destBS.getValue()] = mRegisters[sourceBS.getValue()].copy();
        mRegisters[destBS.getValue()].invert();


        BitString b = mRegisters[destBS.getValue()];
        setCC(b.getValue2sComp());
    }

    /**
     * performing the AND operation
     */
    public void executeAND(){
        BitString destBS = mIR.substring(4,3);
        BitString sourceBS1 = mIR.substring(7,3);
        BitString string1 = mRegisters[sourceBS1.getValue()];

        if(mIR.substring(10,1).getValue() == 0){        //seeing what bit 5 is
            BitString sourceBS2 = mIR.substring(13,3);      //getting the second register
            BitString string2 = mRegisters[sourceBS2.getValue()];

            andHelperMethod(destBS, string1, string2);
        }else{
            BitString imm = mIR.substring(11, 5);          //getting the immediate bits

            andHelperMethod(destBS, string1, imm);
        }

    }

    /**
     * helps the and complete its calculation
     * @param destBS        the destination register
     * @param string1       the first bitString
     * @param string2       the secondBitstring
     */
    private void andHelperMethod(BitString destBS, BitString string1, BitString string2) {
        char[] array1 = string1.getBits();
        char[] array2 = string2.getBits();

        char[] and = new char[array1.length];

        for(int i = 0; i<string1.getLength(); i++){         //calculating the and
            if(array1[i] == '1' && array2[i] == '1'){
                and[i] = '1';
            }else{
                and[i] = '0';
            }
        }

        BitString result = new BitString();
        result.setBits(and);
        mRegisters[destBS.getValue()] = result;

        BitString b = mRegisters[destBS.getValue()];
        setCC(b.getValue2sComp());
    }


    /**
     * performs ADD operation
     */
    public void executeADD(){
        BitString destBS = mIR.substring(4,3);             //getting the destination register
        BitString sourceBS1 = mIR.substring(7,3);          //getting the source1 register
        BitString total = new BitString();
        BitString string1 = mRegisters[sourceBS1.getValue()];
        int source1Val = string1.getValue2sComp();                  //getting the value of the source1 register

        if(mIR.substring(10,1).getValue() == 0){           //if adding a register
            BitString sourceBS2 = mIR.substring(13,3);
            BitString string2 = mRegisters[sourceBS2.getValue()];//get source2 register
            int source2Val = string2.getValue2sComp();              //get value of source2 register
            total.setValue2sComp(source1Val + source2Val);            //adding
            mRegisters[destBS.getValue()] = total;                    //setting the destination register
        }else{
            BitString imm = mIR.substring(11, 5);          //getting the immediate bits
            int secondVal = imm.getValue2sComp();
            total.setValue2sComp(source1Val + secondVal);
            mRegisters[destBS.getValue()] = total;
        }

        BitString b = mRegisters[destBS.getValue()];
        setCC(b.getValue2sComp());
    }

    /**
     * performing the LD instruction
     */
    public void executeLD(){
        BitString destBS = mIR.substring(4, 3);
        BitString pcOffset = mIR.substring(7, 9);
        int currentAddress = 0;

        int offset = pcOffset.getValue2sComp();
        for(int i = 0; i<mMemory.length; i++){          //finding where the instruction is currently at in memory
            if(mMemory[i] == mIR){
                currentAddress = i;
                break;
            }
        }

        BitString address = mMemory[currentAddress + offset + 1];   //finding what to load
        mRegisters[destBS.getValue()] = address;

        BitString b = mRegisters[destBS.getValue()];
        setCC(b.getValue2sComp());

    }

    /**
     * this performs the branch instruction
     */
    public void executeBR(){
        BitString negative = mIR.substring(4,1);
        BitString zero = mIR.substring(5,1);
        BitString positive = mIR.substring(6,1);
        BitString pcOffset = mIR.substring(7,9);
        int offset = pcOffset.getValue2sComp();

        char[] neg = negative.getBits();
        char[] z = zero.getBits();
        char[] pos = positive.getBits();


        //finding the last register updated:
        int currentAddress = 0;
        for(int i = 0; i<mMemory.length; i++){          //finding where the instruction is currently at in memory
            if(mMemory[i] == mIR){
                currentAddress = i;
                break;
            }
        }

        boolean found = false;
        currentAddress--;
        while(!found){          //finds the last changed register
            BitString opcode = mMemory[currentAddress];
            opcode = opcode.substring(0,4);
            if(opcode.getValue() == 1 || opcode.getValue() == 5 ||opcode.getValue() == 2||opcode.getValue() == 10||opcode.getValue() == 6||opcode.getValue() == 14||
                    opcode.getValue() == 9){
                found = true;
            }else{
                currentAddress--;
            }
        }

        BitString lastChangedRegister = mMemory[currentAddress];
        BitString register = lastChangedRegister.substring(4,3);
        BitString string = mRegisters[register.getValue()];
        int value = string.getValue2sComp();        //getting the value of the last changed register
        int pc = mPC.getValue();

        if(neg[0] == '1' && value < 0){      //checking which cc is 1
                pc += offset;
        } else if(pos[0] == '1' && value > 0){
                pc += offset;
        } else if(z[0] == '1'&& value == 0){
                pc += offset;
        }

        mPC.setValue(pc);
    }

    /**
     * this performs the out operation
     */
    public void executeOUT(){
        BitString b = mRegisters[0];
        int value = b.getValue();
        System.out.println(Character.toString(value));  //outputting one character from R0

    }

    /**
     * sets the CC according to the value
     * @param theValue
     */
    private void setCC(int theValue) {
        if(theValue == 0) {
            this.mCC.setBits(new char[] {'0','0','0'});
        } else if(theValue > 0) {
            this.mCC.setBits(new char[] {'0','0','1'});
        } else if(theValue < 0) {
            this.mCC.setBits(new char[] {'1','0','0'});
        }
    }

    /**
     * This method will execute all the instructions starting at address 0
     * till HALT instruction is encountered.
     */
    public void execute() {
        BitString opCodeStr;
        int opCode;

        while (true) {
            // Fetch the instruction
            mIR = mMemory[mPC.getValue()];
            mPC.addOne();

            // Decode the instruction's first 4 bits
            // to figure out the opcode
            opCodeStr = mIR.substring(0, 4);
            opCode = opCodeStr.getValue();

            // What instruction is this?
            if (opCode == 9) { // NOT
                executeNot();
            }else if(opCode == 1){
                executeADD();
            }else if(opCode == 5){
                executeAND();
            }else if(opCode == 2){
                executeLD();
            }else if(opCode == 0){
                executeBR();
            }else if(opCode == 15){
                BitString vector = mIR.substring(8,8);
                if(vector.getValue() == 33){
                    executeOUT();
                }else if(vector.getValue() == 37){
                    return;
                }
            }

        }
    }

    /**
     * Displays the computer's state
     */
    public void display() {
        System.out.print("\nPC ");
        mPC.display(true);
        System.out.print("   ");

        System.out.print("IR ");
        mPC.display(true);
        System.out.print("   ");

        System.out.print("CC ");
        mCC.display(true);
        System.out.println("   ");

        for (int i = 0; i < MAX_REGISTERS; i++) {
            System.out.printf("R%d ", i);
            mRegisters[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        for (int i = 0; i < MAX_MEMORY; i++) {
            System.out.printf("%3d ", i);
            mMemory[i].display(true);
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

    }


}