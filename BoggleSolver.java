import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int R = 26;

    private Node root = new Node();
    private int row;
    private int col;
    private char[] board;
    private boolean[] marked;
    private Cube[] adj;

    private static class Node {
        private boolean isWordInDict;
        private Node next[] = new Node[R];
    }

    private static class Cube {
        private int n = 0;
        private int[] neighbour = new int[8];
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("");

        for (String word : dictionary) {
            put(word);
        }
    }

    private void put(String word) {
        if (word == null) throw new IllegalArgumentException("");

        root = put(root, word, 0);
    }

    private Node put(Node x, String word, int d) {
        if (x == null) x = new Node();
        if (d == word.length()) {
            x.isWordInDict = true;
            return x;
        }
        char c = word.charAt(d);
        x.next[c - 'A'] = put(x.next[c - 'A'], word, d + 1);
        return x;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("");

        if (row != board.rows() || col != board.cols()) {
            row = board.rows();
            col = board.cols();
            marked = new boolean[row * col];
            this.board = new char[row * col];
            findAdjacent();
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int index = i * col + j;
                this.board[index] = board.getLetter(i, j);
            }
        }

        SET<String> validWords = dfs();
        return validWords;
    }

    private void findAdjacent() {
        adj = new Cube[row * col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int index = i * col + j;
                adj[index] = new Cube();
                // up
                if (i > 0) {
                    adj[index].neighbour[adj[index].n++] = (i - 1) * col + j;

                    // up right
                    if (j < col - 1)
                        adj[index].neighbour[adj[index].n++] = (i - 1) * col + j + 1;

                    // up left
                    if (j > 0)
                        adj[index].neighbour[adj[index].n++] = (i - 1) * col + j - 1;
                }

                // down
                if (i < row - 1) {
                    adj[index].neighbour[adj[index].n++] = (i + 1) * col + j;

                    // down right
                    if (j < col - 1)
                        adj[index].neighbour[adj[index].n++] = (i + 1) * col + j + 1;

                    // down left
                    if (j > 0)
                        adj[index].neighbour[adj[index].n++] = (i + 1) * col + j - 1;
                }

                // right
                if (j < col - 1)
                    adj[index].neighbour[adj[index].n++] = i * col + j + 1;

                // left
                if (j > 0)
                    adj[index].neighbour[adj[index].n++] = i * col + j - 1;
            }
        }
    }

    private SET<String> dfs() {
        SET<String> temp = new SET<String>();
        for (int i = 0; i < board.length; i++) {
            dfs(i, temp, new StringBuilder(), root);
        }
        return temp;
    }

    private void dfs(int index, SET<String> temp, StringBuilder str, Node x) {
        char c = board[index];
        Node next = x.next[c - 'A'];
        if (c == 'Q' && next != null) {
            next = next.next['U' - 'A'];
        }
        if (next == null) return;

        if (c == 'Q') str.append("QU");
        else str.append(c);

        if (str.length() > 2 && next.isWordInDict)
            temp.add(str.toString());

        marked[index] = true;
        for (int i = 0; i < adj[index].n; i++) {
            int position = adj[index].neighbour[i];
            if (!marked[position])
                dfs(position, temp, new StringBuilder(str), next);
        }

        marked[index] = false;
    }

    private boolean contains(String word) {
        return search(word);
    }

    private boolean search(String word) {
        Node x = search(root, word, 0);
        if (x == null) return false;
        return x.isWordInDict;
    }

    private Node search(Node x, String word, int d) {
        if (x == null) return null;
        if (word.length() == d) return x;
        char c = word.charAt(d);
        return search(x.next[c - 'A'], word, d + 1);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("");

        if (!contains(word)) return 0;
        else if (word.length() < 3) return 0;
        else if (word.length() < 5) return 1;
        else if (word.length() == 5) return 2;
        else if (word.length() == 6) return 3;
        else if (word.length() == 7) return 5;
        else return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}

