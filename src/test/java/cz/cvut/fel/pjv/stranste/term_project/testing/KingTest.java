package cz.cvut.fel.pjv.stranste.term_project.testing;

import cz.cvut.fel.pjv.stranste.term_project.board.*;
import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.*;
import org.junit.*;
import static org.junit.Assert.*;

public class KingTest {

    @Test
    public void testCastleAfterForbid()
    {
        ChessPiece testedPiece = new King(false);
        testedPiece.forbidCastle();
        assertFalse(testedPiece.canCastle());
    }

    @Test
    public void testCastleAfterMove()
    {
        Board board = new Board();
        ChessPiece testedPiece = new King(false);
        board.setPiece(testedPiece,  new Coord('e','8'));
        board.movePiece(new Move(false,false,new Coord('e','8'), new Coord('d','8'),MoveType.NORMAL));
        assertFalse(testedPiece.canCastle());
    }

    @Test
    public void testGetMoves()
    {
        Board board = new Board();
        ChessPiece testedPiece = new King(true);
        board.setPiece(testedPiece, new Coord('a','1'));
        board.setPiece(new Queen(false), new Coord('c','2'));
        board.setPiece(new King(false), new Coord('g','7'));
        assertEquals(0, testedPiece.getMoves(board).size());
    }


}
