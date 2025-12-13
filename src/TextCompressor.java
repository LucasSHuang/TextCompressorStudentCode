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

    // Compress the text
    private static void compress() {

        String text = BinaryStdIn.readString();

        // Create a tst and implement it will all the 7 bit ASCII values
        TST tst= new TST();
        for (int i = 0; i < END_FILE; i++) {
            tst.insert("" + (char) i, i);
        }

        int index = 0;
        int largestCode = END_FILE + 1;

        // Go until end of text
        while (index < text.length()) {

            // Get the longest possible prefix
            String prefix = tst.getLongestPrefix(text, index);
            int plength = prefix.length();

            // Get the code for that prefix and write it out
            int code = tst.lookup(prefix);
            BinaryStdOut.write(code, BIT_VALUE);

            // If haven't already reach the max amount of 8 bit values and going further won't overflow the text
            // insert prefix plus the next char
            if (largestCode <= MAX_VALUES && index + plength < text.length()) {
                // Insert new code
                tst.insert(prefix + text.charAt(index + plength), largestCode);
                largestCode++;
            }

            // Update index by the length of the prefix
            index += prefix.length();
        }
        BinaryStdOut.write(END_FILE, BIT_VALUE);
        BinaryStdOut.close();
    }

    // Expand the text
    private static void expand() {

        // Create a map with all the 7 bit ASCII values
        String[] prefixes = new String[MAX_VALUES + 1];
        for (int i = 0; i < END_FILE; i++) {
            prefixes[i] = "" + (char) i;
        }

        // Keep track of largest code and current code
        int largestCode = END_FILE + 1;
        int code = BinaryStdIn.readInt(BIT_VALUE);

        // Make it so that the loop keeps going until the file ends
        while (code != END_FILE) {

            // Get the prefix of that code value and print it out
            String prefix = prefixes[code];
            BinaryStdOut.write(prefix);

            // Peek at the next code
            int nextCode = BinaryStdIn.readInt(BIT_VALUE);

            // Check to see if I can add more values to the map
            if (largestCode <= MAX_VALUES) {

                // Base case where you just set the next prefix to the code associated with it in the map
                String nextPrefix = prefixes[nextCode];
                // Edge case where if you don't know the next prefix you add the prefix and the first letter of the prefix
                if (nextPrefix == null) {
                    nextPrefix = prefix + prefix.charAt(0);
                }

                // Add the newest prefix to the next possible code value
                prefixes[largestCode] = prefix + nextPrefix.charAt(0);
                largestCode++;
            }
            // Update the new code
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
