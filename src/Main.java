import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main {
    private final int numRows;
    private final int numCols;
    private final int numBombs;
    private int numUncovered;
    private final boolean[][] bombLocations;
    private final boolean[][] uncovered;
    private final JButton[][] cells;
    private final JFrame frame;

    public Main(int numRows, int numCols, int numBombs) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.numBombs = numBombs;
        this.numUncovered = 0;
        this.bombLocations = new boolean[numRows][numCols];
        this.uncovered = new boolean[numRows][numCols];
        this.cells = new JButton[numRows][numCols];

        int numPlaced = 0;
        while (numPlaced < numBombs) {
            int row = (int) (Math.random() * numRows);
            int col = (int) (Math.random() * numCols);
            if (!bombLocations[row][col]) {
                bombLocations[row][col] = true;
                numPlaced++;
            }
        }

        frame = new JFrame("Minesweeper");
        JPanel panel = new JPanel(new GridLayout(numRows, numCols));
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                JButton cell = new JButton();
                cell.setPreferredSize(new Dimension(60, 60));
                cell.addActionListener(new CellActionListener(i, j));
                cells[i][j] = cell;
                panel.add(cell);
            }
        }
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private int countAdjacentBombs(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && j >= 0 && i < numRows && j < numCols && bombLocations[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void uncoverAdjacentCells(int row, int col) {
        if (uncovered[row][col]) {
            return;
        }

        uncovered[row][col] = true;
        numUncovered++;
        cells[row][col].setEnabled(false);

        if (bombLocations[row][col]) {
            cells[row][col].setText("X");
            JOptionPane.showMessageDialog(frame, "Unfortunately, you lost! Do you want to try again?", "Game Over", JOptionPane.PLAIN_MESSAGE);
            playAgain();
            return;
        }

        int adjacentBombs = countAdjacentBombs(row, col);
        if (adjacentBombs > 0) {
            cells[row][col].setText(Integer.toString(adjacentBombs));
        } else {
            cells[row][col].setText("");
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && j >= 0 && i < numRows && j < numCols && !uncovered[i][j]) {
                        uncoverAdjacentCells(i, j);
                    }
                }
            }
        }
        if (numUncovered == numRows * numCols - numBombs) {
            JOptionPane.showMessageDialog(frame, "You won!", "Congratulations", JOptionPane.PLAIN_MESSAGE);
            playAgain();
        }
    }

    private void playAgain() {
        int choice = JOptionPane.showConfirmDialog(frame, "Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            new Main(numRows, numCols, numBombs);
        } else {
            System.exit(0);
        }
    }

    private class CellActionListener implements ActionListener {
        private final int row;
        private final int col;

        public CellActionListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public void actionPerformed(ActionEvent e) {
            uncoverAdjacentCells(row, col);
        }
    }

    public static void main(String[] args) {
        int numRows = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of rows:"));
        int numCols = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of columns:"));
        int numBombs = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of bomb(s):"));

        new Main(numRows, numCols, numBombs);
    }
}
