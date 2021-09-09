import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray suffix = new CircularSuffixArray(input);

        int first = 0;
        int len = input.length();
        for (int i = 0; i < len; i++) {
            if (suffix.index(i) == 0) {
                first = i;
                break;
            }
        }

        BinaryStdOut.write(first);

        for (int i = 0; i < len; i++) {
            int index = (suffix.index(i) + len - 1) % len;
            BinaryStdOut.write(input.charAt(index));
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();

        int len = input.length();
        int[] next = new int[len];
        int[] count = new int[R + 1];
        char[] firstCol = new char[len];

        for (int i = 0; i < len; i++) {
            count[input.charAt(i) + 1]++;
        }
        for (int i = 0; i < R; i++) {
            count[i + 1] += count[i];
        }
        for (int i = 0; i < len; i++) {
            int position = count[input.charAt(i)]++;
            firstCol[position] = input.charAt(i);
            next[position] = i;
        }

        for (int i = 0; i < len; i++) {
            BinaryStdOut.write(firstCol[first]);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        else if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }

}

