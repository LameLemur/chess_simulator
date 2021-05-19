package cz.cvut.fel.pjv.stranste.term_project.testing;

import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.board.Coord;
import cz.cvut.fel.pjv.stranste.term_project.board.Tile;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoordTest {

    @Test
    public void testCoordIndexConstructor()
    {
        Coord coord = new Coord('d','6');
        assertEquals(coord.firstIndex(), 2);
        assertEquals(coord.secondIndex(), 3);
    }

    @Test
    public void testCoordNumberAndLetterConstructor()
    {
        Coord coord = new Coord(2,3);
        assertEquals(coord.firstCoord(), 'd');
        assertEquals(coord.secondCoord(), '6');
    }

    @Test
    public void testCoordEquals()
    {
        Coord coord1 = new Coord('d','6');
        Coord coord2 = new Coord(2,3);
        Coord coord3 = new Coord(3,2);
        assertTrue(coord1.equals(coord2));
        assertFalse(coord1.equals(coord3));
    }


}
