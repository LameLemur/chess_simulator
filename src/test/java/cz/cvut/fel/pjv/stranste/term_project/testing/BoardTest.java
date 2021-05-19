package cz.cvut.fel.pjv.stranste.term_project.testing;

import cz.cvut.fel.pjv.stranste.term_project.board.*;
import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BoardTest {


    @Test
    public void testGetTileCoord()
    {
        Board board = new Board();
        board.setDefaultBoard();
        Coord testedPossition = new Coord('e','2');
        Tile tile = board.getTile(testedPossition);
        assertEquals(tile.coord, testedPossition);
    }

    @Test
    public void testGetTileIndex()
    {
        Board board = new Board();
        board.setDefaultBoard();
        Coord testedPosition = new Coord('c','6');
        Tile tile = board.getTile(2,2);
        assertEquals(tile.coord, testedPosition);
    }

    @Test
    public void testMove()
    {
        Board board = new Board();
        board.setDefaultBoard();
        ChessPiece movedPiece = board.getTile(new Coord('e','2')).getPiece();
        board.movePiece(new Move(true,false,new Coord('e','2'),new Coord('e','4'), MoveType.NORMAL));
        assertEquals(movedPiece, board.getTile(new Coord('e','4')).getPiece());
    }

    @Test
    public void testSetPiece()
    {
        Board board = new Board();
        board.setDefaultBoard();
        ChessPiece testPiece = new Rook(true);
        Coord testedPosition = new Coord('c','6');
        board.setPiece(testPiece, testedPosition);
        assertEquals(testPiece, board.getTile(testedPosition).getPiece());
    }

    @Test
    public void testCheckMoveForChecks()
    {
        Board board = new Board();
        ChessPiece testedPiece = new King(true);
        board.setPiece(testedPiece, new Coord('a','1'));
        board.setPiece(new Queen(false), new Coord('c','2'));
        board.setPiece(new King(false), new Coord('g','7'));
        assertTrue(board.checkMoveForChecks(new Move(true,false, new Coord('a','1'),  new Coord('a','2'), MoveType.NORMAL)));
    }
}
