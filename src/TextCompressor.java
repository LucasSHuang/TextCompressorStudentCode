/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Lucas Huang
 */
public class TextCompressor {

    public static final int END_FILE = 128;
    public static final int MAX_VALUES = 255;
    public static final int BIT_VALUE = 8;

    private static void compress() {
        String text = BinaryStdIn.readString();
        TST tst= new TST();
        for (int i = 0; i < END_FILE; i++) {
            tst.insert("" + (char) i, i);
        }

        int index = 0;
        int largestCode = END_FILE + 1;
        while (index < text.length()) {
            String prefix = tst.getLongestPrefix(text, index);
            int plength = prefix.length();
            int code = tst.lookup(prefix);
            BinaryStdOut.write(code, BIT_VALUE);
            if (largestCode <= MAX_VALUES && index + plength < text.length()) {
                // Insert new code
                tst.insert(prefix + text.charAt(index + plength), largestCode);
                largestCode++;
            }
            index += prefix.length();
        }
        BinaryStdOut.write(END_FILE, BIT_VALUE);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] prefixes = new String[MAX_VALUES + 1];
        for (int i = 0; i < END_FILE; i++) {
            prefixes[i] = "" + (char) i;
        }
        int largestCode = END_FILE + 1;
        int code = BinaryStdIn.readInt(BIT_VALUE);
        while (code != END_FILE) {
            String prefix = prefixes[code];
            BinaryStdOut.write(prefix);
            int nextCode = BinaryStdIn.readInt(BIT_VALUE);
            String nextPrefix = prefixes[nextCode];
            if (nextCode == largestCode) {
                nextPrefix = prefix + prefix.charAt(0);
            }
            if (largestCode <= MAX_VALUES) {
                prefixes[largestCode] = prefix + nextPrefix.charAt(0);
                largestCode++;
            }
            code = nextCode;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
