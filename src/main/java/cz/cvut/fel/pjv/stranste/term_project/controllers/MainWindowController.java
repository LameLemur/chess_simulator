package cz.cvut.fel.pjv.stranste.term_project.controllers;

import com.sun.tools.javac.Main;
import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.board.Coord;
import cz.cvut.fel.pjv.stranste.term_project.board.Move;
import cz.cvut.fel.pjv.stranste.term_project.board.Promotion;
import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.*;
import cz.cvut.fel.pjv.stranste.term_project.ui.Dialog;
import cz.cvut.fel.pjv.stranste.term_project.ui.MainWindowView;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class MainWindowController {

    private enum GameState {
        NORMAL,
        MATE,
        STALEMATE
    }

    private enum GameMode {
        EDITOR,
        BOT,
        LOCAL,
        LAN
    }

    private static final Logger LOG = Logger.getLogger(MainWindowController.class.getName());

    private Board board;
    private MainWindowView view;
    private ArrayList<Move> moves;
    private NetworkController netController;
    private GameMode gameMode;
    private boolean runClock;

    private static MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance(Board board)
    {
        instance.board = board;
        return instance;
    }

    private MainWindowController() {
        moves = new ArrayList<>();
    }

    /**
     * Setter for a board variable.
     */
    public void setBoard(Board board) {
        this.board = board;
        drawBoard();
        checkForEnd();
    }

    /**
     * Setter for a view variable.
     */
    public void setView(MainWindowView view) {
        this.view = view;
    }

    /**
     * Initialize the controller and view.
     */
    public void init() {
        board.setDefaultBoard();
        view.init();
        drawBoard();
        gameMode = GameMode.EDITOR;
    }

    private void drawBoard() {
        for (char letter : Board.letters) {
            for (char number : Board.numbers) {
                Coord coord = new Coord(letter, number);
                if (board.getTile(coord).getPiece() != null) {
                    view.chessBoard.drawPiece(board.getTile(coord).getPiece().getImg(), coord);
                }
                else {
                    view.chessBoard.drawPiece(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), coord);
                }
            }
        }
        view.chessBoard.rePaintBoard();
        LOG.finer("board painted");
    }

    private void drawMoves(ArrayList<Move> moves) {
        for (Move move : moves) {
            Coord coord = move.end;
            if (move.captures) {
                view.chessBoard.paintRed(coord);
            }
            else {
                view.chessBoard.paintGreen(coord);
            }
        }
        LOG.finer("board painted");
    }

    private void promoteUi(Move move) {
        String[] promotePossibilities = {"Knight", "Bishop", "Rook", "Queen"};
        int returnValue = -1;
        while (returnValue < 0) {
            returnValue = JOptionPane.showOptionDialog(null, "Promote to", "Promotion",
                    JOptionPane.DEFAULT_OPTION, 0, null, promotePossibilities, promotePossibilities);
        }
        Promotion promotion = new Promotion(move.end, returnValue, move.white);
        promote(promotion);
        if (gameMode == GameMode.LAN && board.localWhite == board.whiteOnMove) netController.sendPromotion(promotion);
    }

    /**
     * Promotes a chess piece.
     */
    public void promote(Promotion promotion) {
        switch (promotion.piece) {
            case 0:
                board.getTile(promotion.coord).setPiece(new Knight(promotion.white));
                break;
            case 1:
                board.getTile(promotion.coord).setPiece(new Bishop(promotion.white));
                break;
            case 2:
                board.getTile(promotion.coord).setPiece(new Rook(promotion.white));
                break;
            case 3:
                board.getTile(promotion.coord).setPiece(new Queen(promotion.white));
                break;
        }
        drawBoard();
        board.recordPgnPromotion(promotion);
        LOG.finer("promotion happened successfully");
    }

    /**
     * Plays a given move.
     * @param move move to be played
     */
    public void playMove(Move move) {
        board.movePiece(move);
        if ((move.white ? move.end.secondCoord() == '8' : move.end.secondCoord() == '1') && board.getTile(move.end).getPiece() instanceof Pawn) {
            if (gameMode == GameMode.LAN) {
                if (board.localWhite == board.whiteOnMove) {
                    promoteUi(move);
                }
            }
            else {
                promoteUi(move);
            }
        }
        LOG.finer("move " + move.start.firstCoord() + " " + move.start.secondCoord() + " -> " + move.end.firstCoord() + " " + move.end.secondCoord());
        drawBoard();
        board.whiteOnMove = !board.whiteOnMove;
        checkForEnd();
        if (move.white) {
            board.whiteTime += board.clockIncrement;
            view.chessClock.setWhiteTime(board.whiteTime);
        }
        else {
            board.blackTime += board.clockIncrement;
            view.chessClock.setBlackTime(board.blackTime);
        }

    }

    /**
     * Responses to a chess board tile click.
     */
    public void tileClicked(Coord coord) {
        boolean moved = false;
        if (gameMode == GameMode.LOCAL || gameMode == GameMode.EDITOR || board.whiteOnMove == board.localWhite) {
            for (Move move : moves) {
                if (move.end.equals(coord)) {
                    if (gameMode == GameMode.LAN) netController.sendMove(move);
                    playMove(move);
                    moved = true;
                    moves = new ArrayList<>();
                    break;
                }
            }
            if (board.getTile(coord).getPiece() != null && !moved) {
                if (board.getTile(coord).getPiece().white == board.whiteOnMove || gameMode == GameMode.EDITOR) {
                    view.chessBoard.rePaintBoard();
                    moves = (ArrayList<Move>) board.getTile(coord).getPiece().getMoves(board);
                    drawMoves(moves);
                }
            }
        }
        if (gameMode == GameMode.BOT && board.whiteOnMove != board.localWhite) {
            botMove();
        }
    }

    private void botMove() {
        ArrayList<Move> allMoves = new ArrayList<Move>();
        for (char letter : Board.letters) {
            for (char number : Board.numbers) {
                Coord coord = new Coord(letter, number);
                if (board.getTile(coord).getPiece() != null && board.getTile(coord).getPiece().white != board.localWhite) {
                    allMoves.addAll(board.getTile(coord).getPiece().getMoves(board));
                }
            }
        }
        Random rand = new Random();
        Move move = allMoves.get(rand.nextInt(allMoves.size()));

        board.movePiece(move);
        if ((move.white ? move.end.secondCoord() == '8' : move.end.secondCoord() == '1') && board.getTile(move.end).getPiece() instanceof Pawn) {
            board.getTile(move.end).setPiece(new Queen(move.white));
        }
        drawBoard();
        board.whiteOnMove = !board.whiteOnMove;
        checkForEnd();
    }

    /**
     * Creates a filename dialog and then saves current game state to a file.
     */
    public void saveGame() {
        boolean success = false;
        ObjectOutputStream oos;
        while (!success) {
            try {
                oos = new ObjectOutputStream(new FileOutputStream(new File(JOptionPane.showInputDialog("Please enter a fileName: "))));
                oos.writeObject(board);
                oos.close();
                success = true;
            } catch (IOException e) {
                LOG.info("saving file error");
            }
        }
    }

    public void exportToPgn() {
        boolean success = false;
        BufferedWriter bw;
        while (!success) {
            try {
                bw = new BufferedWriter(new FileWriter(new File(JOptionPane.showInputDialog("Please enter a fileName: "))));
                bw.write(board.getPgn() + "\n");
                bw.close();
                success = true;
            } catch (IOException e) {
                LOG.info("saving file error");
            }
        }
    }

    /**
     * Creates a filename dialog and then loads game state from a file.
     */
    public void loadGame() {
        boolean success = false;
        ObjectInputStream oos = null;
        try {
            oos = new ObjectInputStream(new FileInputStream(new File(JOptionPane.showInputDialog("Please enter a fileName: "))));
            board = (Board) oos.readObject();
            oos.close();
            success = true;
        } catch (IOException | ClassNotFoundException e) {
            LOG.info("loading file error");
        }
        if (!success) {
            JOptionPane.showMessageDialog(view, "File error!");
        }
        else {
            stopGame();
            drawBoard();
        }
    }

    /**
     * Loads a default game state.
     */
    public void loadDefaultGame() {
        board = new Board();
        board.setDefaultBoard();
        stopGame();
        drawBoard();
    }

    private void checkForEnd() {
        GameState gameState;
        for (char letter : Board.letters) {
            for (char number : Board.numbers) {
                Coord coord = new Coord(letter, number);
                if (board.getTile(coord).getPiece() != null && board.getTile(coord).getPiece().white == board.whiteOnMove) {
                    if (board.getTile(coord).getPiece().getMoves(board).size() > 0) {
                        return;
                    }
                }
            }
        }

        gameState = GameState.STALEMATE;
        if (board.kingInCheckMate()) {
            gameState = GameState.MATE;
        }
        stopGame();
        switch (gameState) {
            case MATE:
                JOptionPane.showMessageDialog(view, (board.whiteOnMove ? "Black" : "White") + " won! (checkmate)", "Game end", JOptionPane.PLAIN_MESSAGE);
                break;
            case STALEMATE:
                JOptionPane.showMessageDialog(view, "It is a draw! (stalemate)", "Game end", JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }

    /**
     * Starts a new thread which run the chess clock.
     */
    public void startChessClock(int base, int increment) {
        board.clockIncrement = increment;
        runClock = true;
        board.blackTime = board.whiteTime = base;
        view.chessClock.setWhiteTime(base);
        view.chessClock.setBlackTime(base);
        Thread thread = new Thread() {
            public void run() {
                long lastTimeChanged = System.currentTimeMillis();
                while (runClock) {
                    try {
                        long currentTime = System.currentTimeMillis();
                        if (lastTimeChanged + 1000 <= currentTime) {
                            lastTimeChanged = currentTime;
                            if (board.whiteOnMove) {
                                board.whiteTime--;
                                view.chessClock.setWhiteTime(board.whiteTime);
                            }
                            else {
                                board.blackTime--;
                                view.chessClock.setBlackTime(board.blackTime);
                            }
                        }
                        if (board.whiteTime == 0) {
                            runClock = false;
                            JOptionPane.showMessageDialog(view, "Black won! (timeout)", "Game end", JOptionPane.PLAIN_MESSAGE);
                            break;
                        }
                        if (board.blackTime == 0) {
                            runClock = false;
                            JOptionPane.showMessageDialog(view, "White won! (timeout)", "Game end", JOptionPane.PLAIN_MESSAGE);
                            break;
                        }
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopGame();
            }
        };
        thread.start();
    }

    /**
     * Starts a new game against bot.
     */
    public void startBotGame() {
        stopGame();
        Dialog dialog = Dialog.showBotDialog(view);
        if (dialog != null) {
            startChessClock(dialog.time, dialog.increment);
            board.localWhite = dialog.whiteLocal;
            board.whiteOnMove = dialog.whiteStarts;
            gameMode = GameMode.BOT;
            if (board.localWhite != board.whiteOnMove) {
                botMove();
            }
            checkForEnd();
        }
    }

    /**
     * Starts a new local game.
     */
    public void startLocalGame() {
        stopGame();
        Dialog dialog = Dialog.showLocalDialog(view);
        if (dialog != null) {
            startChessClock(dialog.time, dialog.increment);
            board.localWhite = dialog.whiteLocal;
            board.whiteOnMove = dialog.whiteStarts;
            gameMode = GameMode.LOCAL;
            checkForEnd();
        }
    }

    /**
     * Starts a new thread which waits for a client connection a then start a new lan game.
     */
    public void hostGame() {
        stopGame();
        Dialog dialog = Dialog.showHostDialog(view);
        if (dialog != null) {
            board.clockIncrement = dialog.increment;
            board.blackTime = board.whiteTime = dialog.time;
            board.localWhite = dialog.whiteLocal;
            board.whiteOnMove = dialog.whiteStarts;
            gameMode = GameMode.LAN;
            checkForEnd();

            netController = new NetworkController(view, this, board);
            netController.startServer(dialog.port);

            if (board.whiteOnMove == board.localWhite) board.localWhite = !board.localWhite;
        }
    }

    /**
     * Starts a new lan game after an opponent joined.
     */
    public void opponentJoined() {
        startChessClock(board.whiteTime, board.clockIncrement);
        if (board.whiteOnMove != board.localWhite) board.localWhite = !board.localWhite;
    }

    /**
     * Joins a lan game.
     */
    public void joinGame() {
        stopGame();
        Dialog dialog = Dialog.showJoinDialog(view);
        if (dialog != null) {
            gameMode = GameMode.LAN;
            netController = new NetworkController(view, this, board);
            netController.startClient(dialog.address, dialog.port);
        }
        gameMode = GameMode.LAN;
    }

    /**
     * Stops any game and goes to original settings.
     * This method is called every time you win/lose a game, start a new game or load a new board layout.
     */
    public void stopGame() {
        runClock = false;
        gameMode = GameMode.EDITOR;
        board.clockIncrement = 0;
        board.whiteTime = 0;
        board.blackTime = 0;
        view.chessClock.setBlackTime(0);
        view.chessClock.setWhiteTime(0);
        if (netController != null) netController.stop();
    }
}
