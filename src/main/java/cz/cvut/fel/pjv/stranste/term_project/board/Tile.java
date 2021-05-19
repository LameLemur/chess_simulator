package cz.cvut.fel.pjv.stranste.term_project.board;

import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.ChessPiece;

import java.io.Serializable;

public class Tile implements Serializable {
    private ChessPiece piece;
    public final Coord coord;

    /**
     * A class that hold a chess piece and has a coordinate.
     */
    public Tile(ChessPiece chessPiece, Coord coord) {
        this.coord = coord;
        this.piece = chessPiece;
    }

    /**
     * Sets a given chess piece to this tile.
     */
    public void setPiece(ChessPiece piece) {
        this.piece = piece;
        if (piece != null) {
            piece.setTile(this);
        }
    }

    /**
     * Returns the chess piece this tile holds.
     */
    public ChessPiece getPiece() {
        return piece;
    }
}

