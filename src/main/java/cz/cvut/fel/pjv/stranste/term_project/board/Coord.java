package cz.cvut.fel.pjv.stranste.term_project.board;

import java.io.Serializable;

public class Coord implements Serializable {
    private final char letter;
    private final char number;

    /**
     * Creates a Coord with given letter and number.
     */
    public Coord(char letter, char number) {

        this.letter = Character.toLowerCase(letter);
        this.number = number;
    }

    /**
     * Creates a Coord with given indexes.
     */
    public Coord(int number, int letter) {

        this.letter = Board.letters[letter];
        this.number = Board.numbers[7 - number];
    }

    /**
     * Returns first index on board as if chessBoard was a 2D array
     */
    public int firstIndex() {
        return 56 - ((int) number);
    }

    /**
     * Returns second index on board as if chessBoard was a 2D array
     */
    public int secondIndex() {
        return ((int) letter) - 97;
    }

    /**
     * Returns the letter part of a chess coordinate.
     */
    public char firstCoord() {
        return letter;
    }

    /**
     * Returns the number part of a chess coordinate.
     */
    public char secondCoord() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Coord)) {
            return false;
        }
        return this.letter == ((Coord) o).letter && this.number == ((Coord) o).number;
    }
}
