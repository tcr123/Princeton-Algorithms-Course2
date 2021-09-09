import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Iterator;

public class BaseballElimination {
    private HashMap<Integer, String> teams;
    private HashMap<String, int[]> infoTeam;
    private int[] win;
    private int[][] match;
    private Bag<String> subset;
    private int size; // represent number of team

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        teams = new HashMap<Integer, String>();
        infoTeam = new HashMap<String, int[]>();

        In in = new In(filename);
        size = in.readInt();
        win = new int[size];
        match = new int[size][size];
        int count = 0;

        while (!in.isEmpty()) {
            String teamName = in.readString();
            int[] result = new int[4];
            result[0] = count;
            result[1] = in.readInt();
            result[2] = in.readInt();
            result[3] = in.readInt();
            win[count] = result[1];
            teams.put(count, teamName);
            infoTeam.put(teamName, result);

            for (int i = 0; i < size; i++) {
                match[count][i] = in.readInt();
            }
            count++;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return size;
    }

    // all teams
    public Iterable<String> teams() {
        return infoTeam.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validString(team);
        return infoTeam.get(team)[1];
    }

    // number of losses for given team
    public int losses(String team) {
        validString(team);
        return infoTeam.get(team)[2];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validString(team);
        return infoTeam.get(team)[3];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validString(team1);
        validString(team2);

        int row = infoTeam.get(team1)[0];
        int col = infoTeam.get(team2)[0];
        return match[row][col];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validString(team);
        subset = new Bag<String>();

        int numWin = wins(team) + remaining(team);
        for (Iterator<String> teamName = teams().iterator(); teamName.hasNext(); ) {
            String currentTeam = teamName.next();
            if (numWin < wins(currentTeam)) {
                subset.add(currentTeam);
                return true;
            }
        }

        return flowChecking(team);
    }

    private boolean flowChecking(String team) {
        int chosen = calculateChosen(size);
        int capacity = 2 + chosen + size - 1;
        int whichTeam = infoTeam.get(team)[0];
        FlowNetwork network = new FlowNetwork(capacity);
        int starterNode = chosen + 1;

        int x = 1, y = 0, z = 0, sum = 0;
        String[] nameOfTeam = new String[size - 1];
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i != whichTeam) {
                for (int j = i + 1; j < numberOfTeams(); j++) {
                    if (j != whichTeam) {
                        sum += match[i][j];
                        network.addEdge(new FlowEdge(0, x, match[i][j]));
                        network.addEdge(new FlowEdge(x, y + chosen + 1, Double.POSITIVE_INFINITY));
                        network.addEdge(
                                new FlowEdge(x, capacity - size + z + 1, Double.POSITIVE_INFINITY));
                        x++;
                        z++;
                    }
                }
                int chanceForWin = wins(team) + remaining(team) - win[i];
                if (chanceForWin < 0) chanceForWin = 0;
                network.addEdge(new FlowEdge(y + chosen + 1, capacity - 1, chanceForWin));
                nameOfTeam[y] = teams.get(i);
                y++;
                z = y;
            }
        }

        FordFulkerson ff = new FordFulkerson(network, 0, capacity - 1);
        // System.out.println(sum);
        // System.out.println(ff.value());
        if (sum == ff.value()) {
            return false;
        }
        else {
            for (int i = starterNode; i < capacity - 1; i++) {
                if (ff.inCut(i)) {
                    subset.add(nameOfTeam[i - starterNode]);
                }
            }
            return true;
        }
    }

    private int calculateChosen(int a) {
        int n = a - 1;
        return n * (n - 1) / 2;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validString(team);

        boolean eliminated = isEliminated(team);
        if (eliminated) return subset;
        else return null;
    }

    private void validString(String check) {
        if (!infoTeam.containsKey(check)) throw new IllegalArgumentException("");
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
