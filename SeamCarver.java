import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture image;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("");
        image = picture;
    }

    // current picture
    public Picture picture() {
        return image;
    }

    // width of current picture
    public int width() {
        return image.width();
    }

    // height of current picture
    public int height() {
        return image.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateXIndex(x);
        validateYIndex(y);

        return findEnergy(x, y);
    }

    // for column
    private void validateXIndex(int x) {
        if (x < 0 || x >= width())
            throw new IllegalArgumentException("");
    }

    // for row
    private void validateYIndex(int y) {
        if (y < 0 || y >= height())
            throw new IllegalArgumentException("");
    }

    private double findEnergy(int x, int y) {
        double diffXRed, diffXGreen, diffXBlue;
        double diffYRed, diffYGreen, diffYBlue;
        if (x - 1 < 0 || x + 1 >= width() || y - 1 < 0 || y + 1 >= height()) {
            return 1000.00;
        }
        else {
            Color leftPoint = getColor(x - 1, y);
            Color rightPoint = getColor(x + 1, y);
            diffXRed = leftPoint.getRed() - rightPoint.getRed();
            diffXGreen = leftPoint.getGreen() - rightPoint.getGreen();
            diffXBlue = leftPoint.getBlue() - rightPoint.getBlue();
            double sumOfPowX = powSum(diffXRed, diffXGreen, diffXBlue);

            Color upPoint = getColor(x, y - 1);
            Color downPoint = getColor(x, y + 1);
            diffYRed = upPoint.getRed() - downPoint.getRed();
            diffYGreen = upPoint.getGreen() - downPoint.getGreen();
            diffYBlue = upPoint.getBlue() - downPoint.getBlue();
            double sumOfPowY = powSum(diffYRed, diffYGreen, diffYBlue);

            return Math.sqrt(sumOfPowX + sumOfPowY);
        }
    }

    private double powSum(double red, double green, double blue) {
        double redPow = Math.pow(red, 2);
        double greenPow = Math.pow(green, 2);
        double bluePow = Math.pow(blue, 2);
        return redPow + greenPow + bluePow;
    }

    private Color getColor(int x, int y) {
        return picture().get(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] energyPixel = reformMatrix(height(), width(), true);
        return findSeam(energyPixel);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energyPixel = reformMatrix(width(), height(), false);
        return findSeam(energyPixel);
    }

    private int[] findSeam(double[][] p) {
        int height = p[0].length;
        int width = p.length;

        double[][] energyTo = new double[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 0) energyTo[j][i] = 1000;
                else energyTo[j][i] = Double.POSITIVE_INFINITY;
            }
        }

        int[] seam = new int[height];
        int[][] edgeTo = new int[width][height];

        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = j - 1; k <= j + 1; k++) {
                    if (k >= 0 && k < width) {
                        if (energyTo[k][i + 1] > energyTo[j][i] + p[k][i + 1]) {
                            energyTo[k][i + 1] = energyTo[j][i] + p[k][i + 1];
                            edgeTo[k][i + 1] = changeIndex(i, j, p);
                        }
                    }
                }
            }
        }

        IndexMinPQ pq = new IndexMinPQ(width);
        for (int i = 0; i < width; i++)
            pq.insert(i, energyTo[i][height - 1]);
        seam[height - 1] = pq.minIndex();

        for (int x = height - 1; x > 0; x--) {
            seam[x - 1] = edgeTo[seam[x]][x] % width;
        }
        return seam;
    }

    private double[][] reformMatrix(int w, int h, boolean transpose) {
        double[][] matrix = new double[w][h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (transpose) matrix[j][i] = energy(i, j);
                else matrix[j][i] = energy(j, i);
            }
        }
        return matrix;
    }

    private int changeIndex(int row, int col, double[][] p) {
        return row * p.length + col;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("");
        if (seam.length != picture().width() || height() <= 1) {
            throw new IllegalArgumentException("");
        }

        int yLast = seam[0];
        for (int y : seam) {
            if (y >= height() || y < 0)
                throw new java.lang.IllegalArgumentException("");
            if (Math.abs(y - yLast) > 1)
                throw new java.lang.IllegalArgumentException("");
            yLast = y;
        }

        Picture cPic = new Picture(width(), height() - 1);
        for (int x = 0; x < picture().width(); x++) {
            for (int y = 0; y < picture().height(); y++) {
                if (y == seam[x])
                    continue;
                int z = y;
                if (z > seam[x])
                    z--;
                cPic.set(x, z, picture().get(x, y));
            }
        }
        this.image = cPic;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("");
        if (seam.length != picture().height() || width() <= 1) {
            throw new IllegalArgumentException("");
        }

        int xLast = seam[0];
        for (int x : seam) {
            if (x >= width() || x < 0)
                throw new java.lang.IllegalArgumentException("");
            if (Math.abs(x - xLast) > 1)
                throw new java.lang.IllegalArgumentException("");
            xLast = x;
        }

        Picture cPic = new Picture(width() - 1, height());
        for (int x = 0; x < picture().height(); x++) {
            for (int y = 0; y < picture().width(); y++) {
                if (y == seam[x])
                    continue;
                int z = y;
                if (z > seam[x])
                    z--;
                cPic.set(z, x, picture().get(y, x));
            }
        }
        this.image = cPic;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // Picture picture = new Picture(args[0]);
        // SeamCarver sc = new SeamCarver(picture);
        // double[][] energyPixel = sc.reformMatrix(sc.height(), sc.width(), true); // horizontal test
        // double[][] energyPixel2 = sc
        //         .reformMatrix(sc.width(), sc.height(), false); // vertical change to horizontal
        //
        // for (int row = 0; row < energyPixel.length; row++) {
        //     for (int col = 0; col < energyPixel[0].length; col++)
        //         StdOut.printf("%9.2f ", energyPixel[row][col]);
        //     StdOut.println();
        // }
        //
        // StdOut.println();
        //
        // for (int row = 0; row < energyPixel2.length; row++) {
        //     for (int col = 0; col < energyPixel2[0].length; col++)
        //         StdOut.printf("%9.2f ", energyPixel2[row][col]);
        //     StdOut.println();
        // }
        //
        // StdOut.println();

        // double[][] findSeam = sc.findSeam(energyPixel);
        // for (int row = 0; row < findSeam.length; row++) {
        //     for (int col = 0; col < findSeam[0].length; col++)
        //         StdOut.printf("%9.2f ", findSeam[row][col]);
        //     StdOut.println();
        // }
        //
        // StdOut.println();
        //
        // double[][] findSeam2 = sc.findSeam(energyPixel2);
        // for (int row = 0; row < findSeam2.length; row++) {
        //     for (int col = 0; col < findSeam2[0].length; col++)
        //         StdOut.printf("%9.2f ", findSeam2[row][col]);
        //     StdOut.println();
        // }

        // int[][] findSeam = sc.findSeam(energyPixel);
        // for (int row = 0; row < findSeam.length; row++) {
        //     for (int col = 0; col < findSeam[0].length; col++)
        //         StdOut.printf("%d  ", findSeam[row][col]);
        //     StdOut.println();
        // }
        //
        // StdOut.println();
        //
        // int[][] findSeam2 = sc.findSeam(energyPixel2);
        // for (int row = 0; row < findSeam2.length; row++) {
        //     for (int col = 0; col < findSeam2[0].length; col++)
        //         StdOut.printf("%d  ", findSeam2[row][col]);
        //     StdOut.println();
        // }
    }
}
