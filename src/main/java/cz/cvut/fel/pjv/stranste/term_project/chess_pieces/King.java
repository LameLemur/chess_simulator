package cz.cvut.fel.pjv.stranste.term_project.chess_pieces;

import cz.cvut.fel.pjv.stranste.term_project.board.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King extends ChessPiece {

    private Boolean canCastle;

    public King(Boolean white) {
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
        int[][] steps = new int[][]{{1, 0}, {1, 1}, {0, 1}, {1, -1}, {-1, 1}, {-1, 0}, {0, -1}, {-1, -1}};
        for (int[] step : steps) {
            int firstIndex = tile.coord.firstIndex() + step[0];
            int secondIndex = tile.coord.secondIndex() + step[1];
            if (firstIndex > 7 || firstIndex < 0 || secondIndex > 7 || secondIndex < 0) continue;

            ChessPiece target = board.getTile(firstIndex, secondIndex).getPiece();
            if (target == null) {
                moves.add(new Move(white, false, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
            }
            else if (target.white != white) {
                moves.add(new Move(white, true, tile.coord, new Coord(firstIndex, secondIndex), MoveType.NORMAL));
            }
            else {
                continue;
            }
        }
        for (int i = 0; i < moves.size(); i++) {
            if (board.checkMoveForChecks(moves.get(i))) {
                moves.remove(i);
                i--;
            }
        }
        Move shortCastleMove = null;
        Move longCastleMove = null;
        if (canCastle) {
            for (Move move : moves) {
                if (move.end.equals(white ? new Coord('f', '1') : new Coord('f', '8'))) {
                    Coord endCord = white ? new Coord('g', '1') : new Coord('g', '8');
                    Move castleMove = new Move(white, false, tile.coord, endCord, MoveType.CASTLE);
                    ChessPiece rook = board.getTile(white ? new Coord('h', '1') : new Coord('h', '8')).getPiece();
                    if ((!board.checkMoveForChecks(castleMove)) && rook instanceof Rook && rook.canCastle() && board.getTile(endCord).getPiece() == null) {
                        shortCastleMove = castleMove;
                    }
                }
                if (move.end.equals(white ? new Coord('d', '1') : new Coord('d', '8'))) {
                    Coord endCord = white ? new Coord('c', '1') : new Coord('c', '8');
                    Coord bCol = white ? new Coord('b', '1') : new Coord('b', '8');
                    Move castleMove = new Move(white, false, tile.coord, endCord, MoveType.CASTLE);
                    ChessPiece rook = board.getTile(white ? new Coord('a', '1') : new Coord('a', '8')).getPiece();
                    if ((!board.checkMoveForChecks(castleMove)) && rook instanceof Rook && rook.canCastle() &&
                            board.getTile(endCord).getPiece() == null && board.getTile(bCol).getPiece() == null) {
                        longCastleMove = castleMove;
                    }
                }
            }
        }
        if (shortCastleMove != null) moves.add(shortCastleMove);
        if (longCastleMove != null) moves.add(longCastleMove);

        return moves;
    }

    @Override
    protected void loadImg() {
        try {
            whiteImg = ImageIO.read(this.getClass().getResource("/assets/white_king.png"));
            blackImg = ImageIO.read(this.getClass().getResource("/assets/black_king.png"));
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
