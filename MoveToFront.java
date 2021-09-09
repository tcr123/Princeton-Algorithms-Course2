import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] position = new int[R];
        int[] ch = new int[R];

        for (int i = 0; i < R; i++) {
            position[i] = i;
            ch[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write((char) position[c]);

            for (int i = position[c]; i > 0; i--) {
                position[ch[i - 1]]++;
                ch[i] = ch[i - 1];
            }

            ch[0] = c;
            position[c] = 0;
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        ArrayList<Character> ascii = new ArrayList<Character>();

        for (int i = 0; i < R; i++) {
            ascii.add((char) (255 - i));
        }

        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar();
            char output = ascii.remove(255 - c);

            BinaryStdOut.write(output);

            ascii.add(output);
        }

        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-"))
            MoveToFront.encode();
        if (args[0].equals("+"))
            MoveToFront.decode();
    }
}
