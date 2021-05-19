package cz.cvut.fel.pjv.stranste.term_project.board;

import cz.cvut.fel.pjv.stranste.term_project.board.Coord;

import java.io.Serializable;

public final class Promotion implements Serializable {
    public final Coord coord;
    public final int piece;
    public final boolean white;

    public Promotion(Coord coord, int piece, boolean white) {
        this.coord = coord;
        this.piece = piece;
        this.white = white;
    }
}
