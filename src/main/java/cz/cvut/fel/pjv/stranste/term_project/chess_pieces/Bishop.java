package cz.cvut.fel.pjv.stranste.term_project.chess_pieces;

import cz.cvut.fel.pjv.stranste.term_project.board.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Bishop extends ChessPiece {

    public Bishop(Boolean white) {
        super(white, null);
    }

    private static BufferedImage whiteImg;
    private static BufferedImage blackImg;

    @Override
    public BufferedImage getImg() {
        if (whiteImg == null || blackImg == null) loadImg();
        return white ? whiteImg : blackImg;
    }

    @Override
    public ArrayList<Move> getMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<Move>();
        int[][] steps = new int[][]{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}};
        int[] coefficients = new int[]{-1, 1};
        for (int coefficient1 : coefficients) {
            for (int coefficient2 : coefficients) {
                for (int[] step : steps) {
                    int firstIndex = tile.coord.firstIndex() + (coefficient1 * step[0]);
                    int secondIndex = tile.coord.secondIndex() + (coefficient2 * step[1]);
                    if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

                    ChessPiece target = board.getTile(firstIndex, secondIndex).getPiece();
                    if (target == null) {
                        moves.add(new Move(white, false, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
                    }
                    else if (target.white != white) {
                        moves.add(new Move(white, true, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
                        break;
                    }
                    else {
                        break;
                    }
                }

            }
        }

        for (int i = 0; i < moves.size(); i++) {
            if (board.checkMoveForChecks(moves.get(i))) {
                moves.remove(i);
                i--;
            }
        }

        return moves;
    }

    @Override
    protected void loadImg() {
        try {
            whiteImg = ImageIO.read(new File("assets/white_bishop.png"));
            blackImg = ImageIO.read(new File("assets/black_bishop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
