package cz.cvut.fel.pjv.stranste.term_project.chess_pieces;

import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.board.Move;
import cz.cvut.fel.pjv.stranste.term_project.board.Tile;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class ChessPiece implements Serializable {

    public Tile tile;
    protected abstract void loadImg();

    /**
     * True if piece is white, false otherwise.
     */
    public boolean white;

    /**
     * Returns all moves possible by this piece in given chess board.
     */
    public abstract ArrayList<Move> getMoves(Board board);

    /**
     * Returns
     */
    public abstract BufferedImage getImg();

    protected ChessPiece(Boolean white, Tile tile) {
        this.white = white;
        this.tile = tile;
        loadImg();
    }

    /**
     * Setter for the Tile variable.
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     * Getter for the Tile variable.
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Cancels the option of castling.
     */
    public void forbidCastle() {

    }

    /**
     * Returns true if the piece can castle, returns false otherwise.
     */
    public boolean canCastle() {
        return false;
    }
}
