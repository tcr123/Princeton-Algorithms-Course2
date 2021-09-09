import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;

public class SAP {
    private Digraph digraph;
    private HashMap<HashSet<Integer>, int[]> answer;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("");
        digraph = new Digraph(G);
        answer = new HashMap<>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        firstSap(v, w);
        HashSet<Integer> keys = new HashSet<>();
        keys.add(v);
        keys.add(w);
        int[] result = answer.get(keys);
        return result[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        firstSap(v, w);
        HashSet<Integer> keys = new HashSet<>();
        keys.add(v);
        keys.add(w);
        int[] result = answer.get(keys);
        return result[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("");
        return lastSap(v, w)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("");
        return lastSap(v, w)[1];
    }

    private void firstSap(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        HashSet<Integer> keys = new HashSet<>();
        keys.add(v);
        keys.add(w);

        if (answer.containsKey(keys)) {
            return;
        }

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(digraph, w);

        int maximum = Integer.MAX_VALUE;
        int ancestor = 0;

        for (int i = 0; i < digraph.V(); i++) {
            if (vPath.hasPathTo(i) && vPath.distTo(i) < maximum && wPath.hasPathTo(i)
                    && wPath.distTo(i) < maximum) {
                int sum = vPath.distTo(i) + wPath.distTo(i);
                if (sum < maximum) {
                    maximum = sum;
                    ancestor = i;
                }
            }
        }

        if (maximum == Integer.MAX_VALUE) {
            int[] value = { -1, -1 };
            answer.put(keys, value);
        }
        else {
            int[] value = { maximum, ancestor };
            answer.put(keys, value);
        }
    }

    private int[] lastSap(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(digraph, w);

        int maximum = Integer.MAX_VALUE;
        int ancestor = 0;

        for (int i = 0; i < digraph.V(); i++) {
            if (vPath.hasPathTo(i) && vPath.distTo(i) < maximum && wPath.hasPathTo(i)
                    && wPath.distTo(i) < maximum) {
                int sum = vPath.distTo(i) + wPath.distTo(i);
                if (sum < maximum) {
                    maximum = sum;
                    ancestor = i;
                }
            }
        }

        if (maximum == Integer.MAX_VALUE) {
            int[] value = { -1, -1 };
            return value;
        }
        else {
            int[] value = { maximum, ancestor };
            return value;
        }
    }

    private void validateVertex(int v) {
        int n = digraph.V();
        if (v < 0 || v >= n)
            throw new IllegalArgumentException("");
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) throw new IllegalArgumentException("");
        int n = digraph.V();
        for (int v : vertices) {
            if (v < 0 || v >= n)
                throw new IllegalArgumentException("");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

