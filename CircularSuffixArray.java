import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private int length;
    private char[] sentences;
    private Integer[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("");

        length = s.length();
        sentences = new char[length];
        index = new Integer[length];
        for (int i = 0; i < length; i++) {
            index[i] = i;
            sentences[i] = s.charAt(i);
        }

        Arrays.sort(index, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                for (int i = 0; i < length; i++) {
                    int c1 = (a + i) % length;
                    int c2 = (b + i) % length;

                    char first = sentences[c1];
                    char second = sentences[c2];
                    if (first < second) return -1;
                    else if (first > second) return 1;
                }
                return 0;
            }
        });
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length() - 1) throw new IllegalArgumentException("");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String testing = "ABRACADABRA!";

        CircularSuffixArray suffix = new CircularSuffixArray(testing);
        for (int i = 0; i < suffix.length; i++) {
            System.out.println(suffix.index(i));
        }
    }
}
