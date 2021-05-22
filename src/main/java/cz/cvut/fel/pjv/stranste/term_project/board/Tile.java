package cz.cvut.fel.pjv.stranste.term_project.board;

import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.ChessPiece;

import java.io.Serializable;

public class Tile implements Serializable {
    private ChessPiece piece;
    public final Coord coord;

    /**
     * A class that hold a chess piece and has a coordinate.
     * @param chessPiece chess piece to be held
     * @param coord possiotion on board
     */
    public Tile(ChessPiece chessPiece, Coord coord) {
        this.coord = coord;
        this.piece = chessPiece;
    }

    /**
     * Sets a given chess piece to this tile.
     * @param piece chess piece to be held
     */
    public void setPiece(ChessPiece piece) {
        this.piece = piece;
        if (piece != null) {
            piece.setTile(this);
        }
    }

    /**
     * @return the chess piece this tile holds, return null if empty.
     */
    public ChessPiece getPiece() {
        return piece;
    }
}

