package org.example;

public class BattleShipManager {

    private static final int DIMENSIONS = 10;
    private char[][] myMap;
    private char[][] enemyMap;
    private int lastMoveRow;
    private int lastMoveCol;
    private Result result;

    public enum Result {
        WON,
        LOST;
    }

    public enum Cell {
        MAST('#'),
        EMPTY('.'),
        UNKNOWN('?'),
        SUNK_MAST('@'),
        ENEMY_MISS('~');


        private final char value;
        Cell(char value) {
            this.value = value;
        }
    }

    public BattleShipManager() {
        myMap = new char[DIMENSIONS][DIMENSIONS];
        enemyMap = new char[DIMENSIONS][DIMENSIONS];
    }

    public Message checkEnemyShot(String shot) {
        int row = shot.charAt(0) - 'A';
        int col = Integer.parseInt(shot.substring(1)) - 1;


        if(myMap[row][col] == Cell.MAST.value || myMap[row][col] == Cell.SUNK_MAST.value) {
            myMap[row][col] = Cell.SUNK_MAST.value;
            if(shipsAreDestroyed()) {
                result = Result.LOST;
                return Message.LAST_SUNK;
            }
            if(isLastMast(row, col, new boolean[DIMENSIONS][DIMENSIONS])) {
                return Message.SUNK;
            }

            return Message.HIT;
        }
        else {
            myMap[row][col] = Cell.ENEMY_MISS.value;
            return Message.MISS;
        }
    }

    private boolean shipsAreDestroyed() {
        for(int i = 0; i < DIMENSIONS; i++) {
            for(int j = 0; j < DIMENSIONS; j++) {
                if(myMap[i][j] == Cell.MAST.value) return false;
            }
        }

        return true;
    }

    private boolean isLastMast(int row, int col, boolean[][] visited) {

        if(row >= DIMENSIONS || row < 0 || col >= DIMENSIONS || col < 0 ||
                                    myMap[row][col] == Cell.EMPTY.value ||
                                    myMap[row][col] == Cell.ENEMY_MISS.value ||
                                    visited[row][col]) return true;

        if(myMap[row][col] == Cell.MAST.value) {
            return false;
        }

        visited[row][col] = true;

        return isLastMast(row + 1, col, visited)  &&
                isLastMast(row - 1, col, visited) &&
                isLastMast(row, col + 1, visited) &&
                isLastMast(row, col - 1, visited);
    }


    public void updateEnemyMap(Message msg) {
        if(msg == Message.HIT) {
            enemyMap[lastMoveRow][lastMoveCol] = Cell.MAST.value;
        }
        else if(msg == Message.MISS) {
            enemyMap[lastMoveRow][lastMoveCol] = Cell.EMPTY.value;
        }
        else if(msg == Message.SUNK) {
            enemyMap[lastMoveRow][lastMoveCol] = Cell.MAST.value;
            updateEnemyMapDfs(lastMoveRow, lastMoveCol, new boolean[DIMENSIONS][DIMENSIONS]);
        }
        else if(msg == Message.LAST_SUNK) {
            enemyMap[lastMoveRow][lastMoveCol] = Cell.MAST.value;
            updateEnemyMapDfs(lastMoveRow, lastMoveCol, new boolean[DIMENSIONS][DIMENSIONS]);
            result = Result.WON;
        }



    }
    public void setEnemyMapAfterWinning() {
        for (int i = 0; i < DIMENSIONS; i++) {
            for(int j = 0; j < DIMENSIONS; j++) {
                if(enemyMap[i][j] == Cell.UNKNOWN.value) {
                    enemyMap[i][j] = Cell.EMPTY.value;
                }
            }
        }
    }
    private void updateEnemyMapDfs(int row, int col, boolean[][] visited) {
        if(row >= DIMENSIONS || row < 0 || col >= DIMENSIONS || col < 0) return;

        if(visited[row][col]) return;
        visited[row][col] = true;

        if(enemyMap[row][col] == Cell.UNKNOWN.value) {
            enemyMap[row][col] = Cell.EMPTY.value;
        }

        if(enemyMap[row][col] == Cell.MAST.value) {
            updateEnemyMapDfs(row + 1, col ,visited);
            updateEnemyMapDfs(row - 1, col, visited);
            updateEnemyMapDfs(row, col + 1, visited);
            updateEnemyMapDfs(row, col - 1, visited);
        }

    }

    public void initMyMap(String mapAsString) {
        myMap = new char[DIMENSIONS][DIMENSIONS];
        int row = -1;
        int col = -1;

        for(int i = 0; i < mapAsString.length(); i++) {

            if(i % DIMENSIONS == 0) {
                row++;
                col = 0;
            }

            if(row < DIMENSIONS) {
                myMap[row][col] = mapAsString.charAt(i);
            }
            col++;
        }
    }

    public void initEnemyMap() {
        enemyMap = new char[DIMENSIONS][DIMENSIONS];
        for(int i = 0; i < DIMENSIONS; i++) {
            for(int j = 0; j < DIMENSIONS; j++) {
                enemyMap[i][j] = Cell.UNKNOWN.value;
            }
        }
    }

    public boolean isGameOver() {

        return result != null;
    }

    public void printBoard(char[][] board) {
        System.out.println("   1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < DIMENSIONS; i++) {
            System.out.print((char) ('A' + i) + "  ");
            for (int j = 0; j < DIMENSIONS; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Result getResult() {
        return result;
    }

    public char[][] getMyMap() {
        return myMap;
    }

    public char[][] getEnemyMap() {
        return enemyMap;
    }

    public void setLastMove(String move) {
        lastMoveRow = move.charAt(0) - 'A';
        lastMoveCol = Integer.parseInt(move.substring(1)) - 1;
    }


}
