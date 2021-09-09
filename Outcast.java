import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet netWork;

    public Outcast(WordNet wordnet) {
        this.netWork = wordnet;
    }

    public String outcast(String[] nouns) {
        String word = nouns[0];
        int max = totalDist(word, nouns);
        for (int i = 1; i < nouns.length; i++) {
            int tempSum = totalDist(nouns[i], nouns);
            if (tempSum > max) {
                max = tempSum;
                word = nouns[i];
            }
        }
        return word;
    }

    private int totalDist(String current, String[] nouns) {
        int sum = 0;
        for (String temp : nouns) {
            sum += netWork.distance(current, temp);
        }
        return sum;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
