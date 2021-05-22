package cz.cvut.fel.pjv.stranste.term_project.chess_pieces;

import cz.cvut.fel.pjv.stranste.term_project.board.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rook extends ChessPiece {
    private Boolean canCastle = false;

    public Rook(Boolean white) {
        super(white, null);
        canCastle = true;
    }

    private static BufferedImage whiteImg;
    private static BufferedImage blackImg;

    @Override
    public BufferedImage getImg() {
        if (whiteImg == null || blackImg == null) loadImg();
        return white ? whiteImg : blackImg;
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<Move>();
        int[][] steps = new int[][]{{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};
        int[] coefficients = new int[]{-1, 1};
        for (int coefficient1 : coefficients) {
            for (int i = 0; i < 2; i++) {
                for (int[] step : steps) {
                    int firstIndex = tile.coord.firstIndex() + (coefficient1 * step[1 - i]);
                    int secondIndex = tile.coord.secondIndex() + (coefficient1 * step[i]);
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
            whiteImg = ImageIO.read(this.getClass().getResource("/assets/white_rook.png"));
            blackImg = ImageIO.read(this.getClass().getResource("/assets/black_rook.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forbidCastle() {
        canCastle = false;
    }

    @Override
    public boolean canCastle() {
        return canCastle;
    }
}
