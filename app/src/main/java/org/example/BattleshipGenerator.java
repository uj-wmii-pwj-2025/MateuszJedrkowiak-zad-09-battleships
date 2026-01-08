package org.example;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class RandomBattleShipGenerator implements BattleshipGenerator
{
    private String battleField;
    private  char[][] board;
    private final int rows = 10;
    private final int cols = 10;
    private final Random rd = new Random();

    public RandomBattleShipGenerator() {
        board = initBoard(rows, cols);
    }
    private char[][] initBoard(int rows, int cols) {
        char[][] tmp = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(tmp[i], '.');
        }

        return tmp;
    }

    static void main(String[] args) {

        String fileName = args[0];
        try(PrintWriter writer = new PrintWriter(fileName)) {
            BattleshipGenerator generator = BattleshipGenerator.defaultInstance();
            String newMap = generator.generateMap();
            writer.println(newMap);
            System.out.println(newMap);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateMap() {
        int numOfMasts = 4;
        while(numOfMasts >= 1) {
            int numOfShips = 5 - numOfMasts;
            while(numOfShips >= 1) {
                placeShip(numOfMasts);
                numOfShips--;
            }
        numOfMasts--;
        }
        return convertBoardToString(board);
    }

    private void placeShip(int numOfMasts) {
        int[] firstCell = getRandomFirstCell();
        int row = firstCell[0];
        int col = firstCell[1];

        List<int[]> shipCells = new ArrayList<>();
        shipCells.add(firstCell);
        board[row][col] = '#';

        int currShipMasts = 1;
        while (currShipMasts < numOfMasts) {
            List<int[]> available = getAllAvailableOrthogonalCells(row, col, shipCells);
            if(available.isEmpty()) {
                clearShip(shipCells);

                firstCell = getRandomFirstCell();
                row = firstCell[0];
                col = firstCell[1];
                board[row][col] = '#';
                shipCells.add(firstCell);
                currShipMasts = 1;
                continue;
            }
            int randomCell = rd.nextInt(available.size());
            row = available.get(randomCell)[0];
            col = available.get(randomCell)[1];
            board[row][col] = '#';
            shipCells.add(available.get(randomCell));
            currShipMasts++;
        }

    }

    private void clearShip(List<int[]> shipCells)
    {
        for(int[] cells: shipCells) {
            board[cells[0]][cells[1]] = '.';
        }
        shipCells.clear();
    }
    private String convertBoardToString(char[][] board)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    private int[] getRandomFirstCell() {
        int[] result = new int[2];
        do
        {
            result[0] = rd.nextInt(rows);
            result[1] = rd.nextInt(cols);
        } while(!cellIsAvailable(result[0], result[1], null));

        return result;
    }
    private int getRandomRow() {
        return rd.nextInt(rows);
    }

    private int getRandomCol() {
        return rd.nextInt(cols);
    }

    private boolean containsCell(int row, int col, List<int[]> shipCells)
    {
        for(int[] shipCell : shipCells) {
            if(shipCell[0] == row && shipCell[1] == col) return true;
        }
        return false;
    }
    private List<int[]> getAllAvailableOrthogonalCells(int row, int col, List<int[]> shipCells) {
        List<int[]> availableCells = new ArrayList<>();
        if(!isInBoundsCell(row, col)) return availableCells;

        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];
            if(cellIsAvailable(r, c, shipCells)) {
                availableCells.add(new int[]{r, c});
            }
        }

        return availableCells;
    }

    private boolean cellIsAvailable(int row, int col, List<int[]> shipCells) {
        if(!isInBoundsCell(row, col)) return false;
        if(board[row][col] == '#') return false;
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}, {1,-1}, {1, 1}};
        for(int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];
            if(!isInBoundsCell(r,c)) continue;
            if(board[r][c] == '#' && (shipCells == null || !containsCell(r,c,shipCells))) {
                return false;
            }
        }

        return true;
    }
    private boolean isMastCell(int row, int col) {
        return board[row][col] == '#';
    }
    private boolean isInBoundsCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new RandomBattleShipGenerator();
    }

}
