import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;

public class WordNet {
    // constructor takes the name of the two input files
    private Digraph graph;
    private HashMap<String, Bag<Integer>> synMap;
    private SAP sap;

    private HashMap<Integer, String> synSet;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("");
        synMap = new HashMap<String, Bag<Integer>>();
        synSet = new HashMap<Integer, String>();
        int sizeOfGraph = readAllSynsets(synsets);
        graph = new Digraph(sizeOfGraph);
        readAllHypernyms(hypernyms);

        DirectedCycle hasCycle = new DirectedCycle(graph);
        if (hasCycle.hasCycle()) {
            throw new IllegalArgumentException("");
        }
        sap = new SAP(graph);

        int rootNum = 0;
        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (graph.outdegree(vertex) == 0)
                rootNum++;
        }

        if (rootNum != 1) throw new IllegalArgumentException("");
    }

    private int readAllSynsets(String synsets) {
        int count = 0;
        In in = new In(synsets);
        while (in.hasNextLine()) {
            count++;
            String[] a = in.readLine().split(",");
            int id = Integer.parseInt(a[0]);
            synSet.put(id, a[1]);
            String[] synonyms = a[1].split(" ");
            for (String nouns : synonyms) {
                if (isNoun(nouns)) {
                    Bag<Integer> temp = synMap.get(nouns);
                    temp.add(id);
                    synMap.put(nouns, temp);
                }
                else {
                    Bag<Integer> temp = new Bag<Integer>();
                    temp.add(id);
                    synMap.put(nouns, temp);
                }
            }
        }
        return count;
    }

    private void readAllHypernyms(String hypernyms) {
        In in2 = new In(hypernyms);
        while (in2.hasNextLine()) {
            String[] a = in2.readLine().split(",");
            int no = Integer.parseInt(a[0]);
            Bag<Integer> temp = new Bag<Integer>();
            if (a.length >= 2) {
                for (int i = 1; i < a.length; i++) {
                    int value = Integer.parseInt(a[i]);
                    temp.add(value);
                    graph.addEdge(no, value);
                }
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        if (synMap.isEmpty()) throw new IllegalArgumentException("");
        return synMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("");
        return synMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateNouns(nounA);
        validateNouns(nounB);

        Bag<Integer> v = synMap.get(nounA);
        Bag<Integer> w = synMap.get(nounB);

        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateNouns(nounA);
        validateNouns(nounB);

        Bag<Integer> v = synMap.get(nounA);
        Bag<Integer> w = synMap.get(nounB);

        return synSet.get(sap.ancestor(v, w));
    }

    private void validateNouns(String noun) {
        if (!synMap.containsKey(noun)) {
            throw new IllegalArgumentException("");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
