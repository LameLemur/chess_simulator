package cz.cvut.fel.pjv.stranste.term_project.board;


import java.io.Serializable;

public final class Move implements Serializable {
    public final Boolean white;
    public final Boolean captures;
    public final Coord start;
    public final Coord end;
    public final MoveType type;

    /**
     * @param white true if move was played by white, false otherwise.
     * @param captures true if move captures, false otherwise.
     * @param start coordinate of the start of the move.
     * @param end coordinate of the start of the move.
     * @param type Type of move: NORMAL, CASTLE, EN_PASSANT
     */
    public Move(Boolean white, Boolean captures, Coord start, Coord end, MoveType type) {
        this.white = white;
        this.captures = captures;
        this.start = start;
        this.end = end;
        this.type = type;
    }
}
