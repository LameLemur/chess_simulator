package cz.cvut.fel.pjv.stranste.term_project.chess_pieces;

import cz.cvut.fel.pjv.stranste.term_project.board.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends ChessPiece  {

    public Pawn(Boolean white) {
        super(white, null);
    }

    private static BufferedImage whiteImg;
    private static BufferedImage blackImg;

    @Override
    public BufferedImage getImg() {
        if(whiteImg == null || blackImg == null)loadImg();
        return white ? whiteImg : blackImg;
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<Move>();
        int[][] steps = white ? new int[][]{{-1, 0}, {-2, 0}} : new int[][]{{1, 0}, {2, 0}};
        for (int[] step : steps) {
            int firstIndex = tile.coord.firstIndex() + step[0];
            int secondIndex = tile.coord.secondIndex() + step[1];
            if(firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

            ChessPiece target = board.getTile(firstIndex, secondIndex).getPiece();
            if (target == null) {
                moves.add(new Move(white,false, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
                if((white && tile.coord.secondCoord() != '2') || (!white && tile.coord.secondCoord() != '7'))
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        steps = !white ? new int[][]{{1, 1}, {1, -1}} : new int[][]{{-1, 1}, {-1, -1}};
        for (int[] step : steps) {
            int firstIndex = tile.coord.firstIndex() + step[0];
            int secondIndex = tile.coord.secondIndex() + step[1];
            if(firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

            ChessPiece target = board.getTile(firstIndex, secondIndex).getPiece();
            if (target != null && target.white != white) {
                moves.add(new Move(white,true, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
            }
        }
        if((white && tile.coord.secondCoord() == '5') || (!white && tile.coord.secondCoord() == '4'))
        {
            Move lastMove = board.getLastMove();
            if(lastMove != null)
            {
                ChessPiece left = null;
                ChessPiece right = null;
                if(tile.coord.secondIndex() - 1 >= 0)
                {
                    left = board.getTile(tile.coord.firstIndex(),tile.coord.secondIndex() - 1).getPiece();
                }
                if(tile.coord.secondIndex() + 1 <= 7)
                {
                    right = board.getTile(tile.coord.firstIndex(),tile.coord.secondIndex() + 1).getPiece();
                }
                if(left instanceof Pawn && lastMove.start.equals(new Coord(white ? 1 : 6,tile.coord.secondIndex() - 1)) && lastMove.end.equals(new Coord(white ? 3 : 4,tile.coord.secondIndex() - 1)))
                {
                    moves.add(new Move(white,true, tile.coord, new Coord(white ? 2 : 5,tile.coord.secondIndex() - 1), MoveType.EN_PASSANT));
                }
                if(right instanceof Pawn && lastMove.start.equals(new Coord(white ? 1 : 6,tile.coord.secondIndex() + 1)) && lastMove.end.equals(new Coord(white ? 3 : 4,tile.coord.secondIndex() + 1)))
                {
                    moves.add(new Move(white,true, tile.coord, new Coord(white ? 2 : 5,tile.coord.secondIndex() + 1), MoveType.EN_PASSANT));
                }
            }
        }
        for(int i = 0; i < moves.size();i++)
        {
            if(board.checkMoveForChecks(moves.get(i)))
            {
                moves.remove(i);
                i--;
            }
        }
        return moves;
    }

    @Override
    protected void loadImg() {
        try {
            whiteImg = ImageIO.read(this.getClass().getResource("/assets/white_pawn.png"));
            blackImg = ImageIO.read(this.getClass().getResource("/assets/black_pawn.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
