package cz.cvut.fel.pjv.stranste.term_project.board;

import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private String pgn = "";
    private int moveCounter = 1;

    private Tile[][] tiles;
    private ChessPiece whiteKing = null;
    private ChessPiece blackKing = null;
    private List<Move> moveHistory;

    /**
     * All possible letters on a standard chess
     */
    final public static char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    /**
     * All possible numbers on a standard chess
     */
    final public static char[] numbers = {'1', '2', '3', '4', '5', '6', '7', '8'};

    /**
     * Remaining chess clock time for white player.
     */
    public int whiteTime;
    /**
     * Remaining chess clock time for black player.
     */
    public int blackTime;
    /**
     * True if it is whites move, false if it is blacks move.
     */
    public boolean whiteOnMove;
    /**
     * Time which is added to player time each time they play a move.
     */
    public int clockIncrement;
    /**
     * Only for bot and lan game modes. True if white is local player. False if black is local player.
     */
    public boolean localWhite;

    /**
     * Creates an empty chess
     */
    public Board() {
        tiles = new Tile[8][8];
        whiteOnMove = true;
        int index = 0;
        for (int i = 7; i >= 0; i--) {

            for (int ii = 0; ii < 8; ii++) {

                tiles[index][ii] = new Tile(null, new Coord(letters[ii], numbers[i]));
            }
            index += 1;
        }
        moveHistory = new ArrayList<Move>();
    }

    /**
     * Return a Tile based on a given Coord. Returns null if Tile is empty.
     * @param coord coordinate of a demanded tile
     * @return Tile based on a given coord
     */
    public Tile getTile(Coord coord) {
        return tiles[coord.firstIndex()][coord.secondIndex()];
    }

    /**
     * Return a Tile on a certain indexes. Returns null if Tile is empty.
     * @param firstIndex first index of a chess board as a 2D array
     * @param secondIndex second index of a chess board as a 2D array
     * @return Tile based on a given coord
     */
    public Tile getTile(int firstIndex, int secondIndex) {
        return tiles[firstIndex][secondIndex];
    }

    /**
     * Takes a Move and plays it.
     * @param move move to played
     */
    public void movePiece(Move move) {
        Tile startTile = tiles[move.start.firstIndex()][move.start.secondIndex()];
        Tile endTile = tiles[move.end.firstIndex()][move.end.secondIndex()];
        moveHistory.add(move);
        if (startTile.getPiece() instanceof Rook || startTile.getPiece() instanceof King) {
            startTile.getPiece().forbidCastle();
        }
        switch (move.type) {
            case NORMAL: {
                endTile.setPiece(startTile.getPiece());
                startTile.setPiece(null);
                break;
            }
            case CASTLE: {
                Coord rookPoss;
                Coord rookNewPoss;
                if (move.white) {
                    if (move.end.firstCoord() == 'g') {
                        rookPoss = new Coord('h', '1');
                        rookNewPoss = new Coord('f', '1');
                    }
                    else {
                        rookPoss = new Coord('a', '1');
                        rookNewPoss = new Coord('d', '1');
                    }

                }
                else {
                    if (move.end.firstCoord() == 'g') {
                        rookPoss = new Coord('h', '8');
                        rookNewPoss = new Coord('f', '8');
                    }
                    else {
                        rookPoss = new Coord('a', '8');
                        rookNewPoss = new Coord('d', '8');
                    }
                }
                setPiece(getTile(rookPoss).getPiece(),rookNewPoss);
                setPiece(null,rookPoss);
                endTile.setPiece(startTile.getPiece());
                startTile.setPiece(null);
                break;
            }
            case EN_PASSANT: {
                Coord takenPiece;
                if (move.white) {
                    takenPiece = new Coord(move.end.firstCoord(), '5');
                }
                else {
                    takenPiece = new Coord(move.end.firstCoord(), '4');
                }
                tiles[takenPiece.firstIndex()][takenPiece.secondIndex()].setPiece(null);
                endTile.setPiece(startTile.getPiece());
                startTile.setPiece(null);
            }
        }
        recordPgn(move);
    }

    /**
     * Places a ChessPiece on a specific Tile based on a given Coord.
     * @param piece chess piece instance
     * @param coord coordinate of a tile
     */
    public void setPiece(ChessPiece piece, Coord coord) {
        Tile tile = tiles[coord.firstIndex()][coord.secondIndex()];
        tile.setPiece(piece);
        if (piece instanceof King) {
            if (piece.white) {
                whiteKing = piece;
            }
            else {
                blackKing = piece;
            }
        }
    }

    @Deprecated
    /**
     * Prints a board to a console.
     */
    public void printBoard() {
        for (int ii = 7; ii >= 0; ii--) {
            System.out.print("|");
            for (int i = 0; i < 8; i++) {
                if (tiles[i][ii].getPiece() == null) {
                    System.out.print("  |");
                }
                else {
                    if (tiles[i][ii].getPiece().white) {
                        System.out.print(tiles[i][ii].getPiece().getClass().getSimpleName().charAt(0) + " |");
                    }
                    else {
                        System.out.print(ANSI_BLACK + tiles[i][ii].getPiece().getClass().getSimpleName().charAt(0) + ANSI_RESET + " |");
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Standard chess piece set up is placed on the
     */
    public void setDefaultBoard() {
        this.setPiece(new King(false), new Coord('e', '8'));
        this.setPiece(new King(true), new Coord('e', '1'));
        this.setPiece(new Queen(true), new Coord('d', '1'));
        this.setPiece(new Queen(false), new Coord('d', '8'));
        this.setPiece(new Rook(true), new Coord('a', '1'));
        this.setPiece(new Rook(true), new Coord('h', '1'));
        this.setPiece(new Rook(false), new Coord('h', '8'));
        this.setPiece(new Rook(false), new Coord('a', '8'));
        this.setPiece(new Knight(true), new Coord('b', '1'));
        this.setPiece(new Knight(true), new Coord('g', '1'));
        this.setPiece(new Knight(false), new Coord('b', '8'));
        this.setPiece(new Knight(false), new Coord('g', '8'));
        this.setPiece(new Bishop(true), new Coord('c', '1'));
        this.setPiece(new Bishop(true), new Coord('f', '1'));
        this.setPiece(new Bishop(false), new Coord('c', '8'));
        this.setPiece(new Bishop(false), new Coord('f', '8'));
        for (int i = 0; i < 8; i++) {
            this.setPiece(new Pawn(true), new Coord((char) ('a' + i), '2'));
            this.setPiece(new Pawn(false), new Coord((char) ('a' + i), '7'));
        }
    }

    /**
     * Returns last move played. Returns null if no move was played.
     */
    public Move getLastMove() {
        return moveHistory.size() == 0 ? null : moveHistory.get(moveHistory.size() - 1);
    }

    /**
     * Returns true if the Move places the player in check. Returns false if the Move is valid.
     * @param move
     */
    public boolean checkMoveForChecks(Move move) {

        Coord kingPoss = move.white ? whiteKing.getTile().coord : blackKing.getTile().coord;
        if (move.start.equals(kingPoss)) {
            kingPoss = move.end;
        }

        //knight
        int[][] steps = new int[][]{{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        for (int[] step : steps) {
            int firstIndex = kingPoss.firstIndex() + step[0];
            int secondIndex = kingPoss.secondIndex() + step[1];
            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

            ChessPiece attacker = getTile(firstIndex, secondIndex).getPiece();
            if (attacker instanceof Knight && attacker.white != move.white) {
                boolean knightTaken = move.captures && move.end.equals(new Coord(firstIndex, secondIndex));
                boolean kingMoved = move.start.equals(kingPoss);
                if (!(knightTaken || kingMoved)) {
                    return true;
                }
            }
        }

        //rooks and queens
        steps = new int[][]{{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};
        int[] coefficients = new int[]{-1, 1};
        for (int coefficient1 : coefficients) {
            for (int i = 0; i < 2; i++) {
                for (int[] step : steps) {
                    int firstIndex = kingPoss.firstIndex() + (coefficient1 * step[1 - i]);
                    int secondIndex = kingPoss.secondIndex() + (coefficient1 * step[i]);
                    if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;
                    ChessPiece attacker = getTile(firstIndex, secondIndex).getPiece();
                    if (attacker != null && (attacker.white == move.white)) {
                        if (!(move.start.equals(new Coord(firstIndex, secondIndex)))) {
                            break;
                        }
                    }
                    else if (move.end.equals(new Coord(firstIndex, secondIndex)) && !move.start.equals(kingPoss)) {
                        break;
                    }
                    else if (attacker instanceof Rook || attacker instanceof Queen) {
                        boolean pieceTaken = move.captures && move.end.equals(new Coord(firstIndex, secondIndex));
                        boolean kingMoved = move.start.equals(kingPoss) && move.end.firstIndex() != firstIndex && move.end.secondIndex() != secondIndex;
                        if (!(pieceTaken || kingMoved)) {
                            return true;
                        }
                    }
                    else if (attacker != null) {
                        break;
                    }
                }
            }
        }

        //bishops and pawn
        steps = new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}};
        coefficients = new int[]{-1, 1};
        for (int coefficient1 : coefficients) {
            for (int coefficient2 : coefficients) {
                int count = 1;
                for (int[] step : steps) {
                    int firstIndex = kingPoss.firstIndex() + (coefficient1 * step[0]);
                    int secondIndex = kingPoss.secondIndex() + (coefficient2 * step[1]);
                    if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                    ChessPiece attacker = getTile(firstIndex, secondIndex).getPiece();
                    if (attacker != null && (attacker.white == move.white)) {
                        if (!(move.start.equals(new Coord(firstIndex, secondIndex)))) {
                            break;
                        }
                    }
                    else if (move.end.equals(new Coord(firstIndex, secondIndex)) && !move.start.equals(kingPoss)) {
                        break;
                    }
                    else if (attacker instanceof Bishop || attacker instanceof Queen || (count == 1 && attacker instanceof Pawn)) {
                        if (attacker instanceof Pawn) {
                            if (move.white && firstIndex > kingPoss.firstIndex()) continue;
                            if ((!move.white) && firstIndex < kingPoss.firstIndex()) continue;
                        }
                        boolean pieceTaken = move.captures && move.end.equals(new Coord(firstIndex, secondIndex));
                        boolean kingMoved = move.start.equals(kingPoss) &&
                                (move.end.firstIndex() + move.end.secondIndex() != firstIndex + secondIndex) &&
                                (move.end.firstIndex() - move.end.secondIndex() != firstIndex - secondIndex);
                        if (!(pieceTaken || kingMoved)) {
                            return true;
                        }
                    }
                    else if (attacker != null) {
                        break;
                    }
                    count++;
                }

            }
        }

        //king
        steps = new int[][]{{1, 0}, {1, 1}, {0, 1}, {1, -1}, {-1, 1}, {-1, 0}, {0, -1}, {-1, -1}};
        if (getTile(move.start).getPiece() instanceof King) {
            Coord otherKing = !move.white ? whiteKing.getTile().coord : blackKing.getTile().coord;
            for (int[] step : steps) {
                int firstIndex = otherKing.firstIndex() + step[0];
                int secondIndex = otherKing.secondIndex() + step[1];
                if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;
                if (move.end.equals(new Coord(firstIndex, secondIndex))) {
                    return true;
                }
            }
        }
        if (getTile(move.end).getPiece() instanceof King) return true;
        return false;
    }

    /**
     * @return true if king is in check, returns false otherwise
     */
    public boolean kingInCheckMate() {
        if (getLastMove() != null && getLastMove().end.equals(new Coord('a', '1'))) {
            return checkMoveForChecks(new Move(whiteOnMove, false, new Coord('h', '1'), new Coord('h', '1'), MoveType.NORMAL));
        }
        return checkMoveForChecks(new Move(whiteOnMove, false, new Coord('a', '1'), new Coord('a', '1'), MoveType.NORMAL));
    }

    private boolean kingInCheck() {
        if (getLastMove() != null && getLastMove().end.equals(new Coord('a', '1'))) {
            return checkMoveForChecks(new Move(!whiteOnMove, false, new Coord('h', '1'), new Coord('h', '1'), MoveType.NORMAL));
        }
        return checkMoveForChecks(new Move(!whiteOnMove, false, new Coord('a', '1'), new Coord('a', '1'), MoveType.NORMAL));
    }

    private void recordPgn(Move move) {
        if (moveCounter > 1) pgn += " ";
        if (move.white) {
            pgn += moveCounter + ". ";
            moveCounter++;
        }
        if(move.type == MoveType.CASTLE)
        {
            if(move.end.firstCoord() == 'c')
            {
                pgn += "O-O-O";
            }
            else
            {
                pgn += "O-O";
            }
        }
        else
        {
            ChessPiece piece = getTile(move.end).getPiece();
            if (piece instanceof Pawn) {
                if(move.captures) {
                    int[][] steps = move.white ? new int[][]{{1, 1}, {1, -1}} : new int[][]{{-1, 1}, {-1, -1}};
                    for(int[] step : steps)
                    {
                        int firstIndex = move.end.firstIndex() + step[0];
                        int secondIndex = move.end.secondIndex() + step[1];
                        if(getTile(firstIndex,secondIndex).getPiece() instanceof Pawn && getTile(firstIndex,secondIndex).getPiece().white == move.white)
                        {
                            pgn += move.start.firstCoord();
                        }
                    }
                }
            }
            else if (piece instanceof Knight) {
                pgn += "N";
                int[][] steps = new int[][]{{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
                boolean sameLetter = false;
                boolean sameNumber = false;
                boolean found = false;
                for (int[] step : steps) {
                    int firstIndex = move.end.firstIndex() + step[0];
                    int secondIndex = move.end.secondIndex() + step[1];
                    if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                    ChessPiece otherPiece = getTile(firstIndex, secondIndex).getPiece();
                    if (otherPiece instanceof Knight && otherPiece.white == move.white) {
                        sameNumber = move.start.firstIndex() == firstIndex;
                        sameLetter = move.start.secondIndex() == secondIndex;
                        found = true;
                    }
                }
                if(found) pgnFoundPiece(move, sameLetter, sameNumber);
            }
            else if (piece instanceof Bishop) {
                pgn += "B";
                boolean sameLetter = false;
                boolean sameNumber = false;
                boolean found = false;
                int[][] steps = new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}};
                int[] coefficients = new int[]{-1, 1};
                for (int coefficient1 : coefficients) {
                    for (int coefficient2 : coefficients) {
                        for (int[] step : steps) {
                            int firstIndex = move.end.firstIndex() + (coefficient1 * step[0]);
                            int secondIndex = move.end.secondIndex() + (coefficient2 * step[1]);
                            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                            ChessPiece otherPiece = getTile(firstIndex, secondIndex).getPiece();
                            if(otherPiece != null && otherPiece.white != move.white)
                            {
                                break;
                            }
                            else if (otherPiece instanceof Bishop) {
                                sameNumber = move.start.firstIndex() == firstIndex;
                                sameLetter = move.start.secondIndex() == secondIndex;
                                found = true;
                            }
                        }
                    }
                }
                if(found) pgnFoundPiece(move, sameLetter, sameNumber);
            }
            else if (piece instanceof Rook) {
                pgn += "R";
                boolean sameLetter = false;
                boolean sameNumber = false;
                boolean found = false;
                int[][] steps = new int[][]{{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};
                int[] coefficients = new int[]{-1, 1};
                for (int coefficient1 : coefficients) {
                    for (int i = 0; i < 2; i++) {
                        for (int[] step : steps) {
                            int firstIndex = move.end.firstIndex() + (coefficient1 * step[1 - i]);
                            int secondIndex = move.end.secondIndex() + (coefficient1 * step[i]);
                            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                            ChessPiece otherPiece = getTile(firstIndex, secondIndex).getPiece();
                            if(otherPiece != null && otherPiece.white != move.white)
                            {
                                break;
                            }
                            else if (otherPiece instanceof Rook) {
                                sameNumber = move.start.firstIndex() == firstIndex;
                                sameLetter = move.start.secondIndex() == secondIndex;
                                found = true;
                            }
                        }
                    }
                }
                if(found) pgnFoundPiece(move, sameLetter, sameNumber);
            }
            else if (piece instanceof Queen) {
                pgn += "Q";
                boolean sameLetter = false;
                boolean sameNumber = false;
                boolean found = false;
                int[][] steps = new int[][]{{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};
                int[] coefficients = new int[]{-1, 1};
                for (int coefficient1 : coefficients) {
                    for (int i = 0; i < 2; i++) {
                        for (int[] step : steps) {
                            int firstIndex = move.end.firstIndex() + (coefficient1 * step[1 - i]);
                            int secondIndex = move.end.secondIndex() + (coefficient1 * step[i]);
                            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                            ChessPiece otherPiece = getTile(firstIndex, secondIndex).getPiece();
                            if(otherPiece != null && otherPiece.white != move.white)
                            {
                                break;
                            }
                            else if (otherPiece instanceof Queen) {
                                sameNumber = move.start.firstIndex() == firstIndex;
                                sameLetter = move.start.secondIndex() == secondIndex;
                                found = true;
                            }
                        }
                    }
                }
                steps = new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}};
                coefficients = new int[]{-1, 1};
                for (int coefficient1 : coefficients) {
                    for (int coefficient2 : coefficients) {
                        for (int[] step : steps) {
                            int firstIndex = move.end.firstIndex() + (coefficient1 * step[0]);
                            int secondIndex = move.end.secondIndex() + (coefficient2 * step[1]);
                            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                            ChessPiece otherPiece = getTile(firstIndex, secondIndex).getPiece();
                            if(otherPiece != null && otherPiece.white != move.white)
                            {
                                break;
                            }
                            else if (otherPiece instanceof Queen) {
                                sameNumber = move.start.firstIndex() == firstIndex;
                                sameLetter = move.start.secondIndex() == secondIndex;
                                found = true;
                            }
                        }
                    }
                }
                if(found) pgnFoundPiece(move, sameLetter, sameNumber);
            }
            else if (piece instanceof King) {
                pgn += "K";
            }
            if(move.captures)
            {
                pgn += "x";
            }
            pgn += String.valueOf(move.end.firstCoord()) + String.valueOf(move.end.secondCoord());
        }

        if (kingInCheck()) {
            pgn += "+";
        }
    }

    private void pgnFoundPiece(Move move, boolean sameLetter, boolean sameNumber) {
        if(!sameLetter)
        {
            pgn += String.valueOf(move.start.firstCoord());
        }
        else if(sameLetter && !sameNumber)
        {
            pgn += String.valueOf(move.start.secondCoord());
        }
        else
        {
            pgn += String.valueOf(move.start.firstCoord()) + String.valueOf(move.start.secondCoord());
        }
    }

    public void recordPgnPromotion(Promotion promotion) {
        pgn += "=";
        switch (promotion.piece) {
            case 0:
                pgn += "N";
            case 1:
                pgn += "B";
            case 2:
                pgn += "R";
            case 3:
                pgn += "Q";
        }
    }

    public String getPgn() {
        return pgn;
    }
}
