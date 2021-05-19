package cz.cvut.fel.pjv.stranste.term_project.controllers;

import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.board.Move;
import cz.cvut.fel.pjv.stranste.term_project.board.Promotion;
import cz.cvut.fel.pjv.stranste.term_project.chess_pieces.Pawn;
import cz.cvut.fel.pjv.stranste.term_project.ui.MainWindowView;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class NetworkController {

    private MainWindowView view;
    private MainWindowController controller;
    private Board board;
    private Socket clientSocket;
    private ServerSocket serverSocket = null;
    private boolean keepConnection;
    private ObjectOutputStream ous;

    private static final Logger LOG = Logger.getLogger(NetworkController.class.getName());

    public NetworkController(MainWindowView view, MainWindowController controller, Board board) {
        this.view = view;
        this.controller = controller;
        this.board = board;
        keepConnection = true;
    }

    /**
     * Creates a new thread that listens for a connection on given port.
     */
    public void startServer(int port) {

        Thread thread = new Thread() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    clientSocket = serverSocket.accept();
                    controller.opponentJoined();
                    sendBoard();
                    while (keepConnection) {
                        recvMove();
                        Thread.sleep(100);
                    }

                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    controller.stopGame();
                    JOptionPane.showMessageDialog(view, "Server closed.");
                }
                try {
                    if (serverSocket != null) serverSocket.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    LOG.info("closing socket exception");
                }
                controller.stopGame();
            }
        };
        thread.start();
    }

    /**
     * Creates a new thread that connects to a given address and port.
     */
    public void startClient(String host, int port) {

        Thread thread = new Thread() {
            public void run() {
                try {
                    clientSocket = new Socket(host, port);
                    recvBoard();
                    while (keepConnection) {
                        recvMove();
                        Thread.sleep(200);
                    }

                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    controller.stopGame();
                    JOptionPane.showMessageDialog(view, "Connection closed.");
                }
                try {
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    LOG.info("closing socket exception");
                }
                controller.stopGame();
            }
        };
        thread.start();
    }

    /**
     * Receives a move from opponent.
     */
    private void recvMove() throws IOException, ClassNotFoundException, InterruptedException {
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        Move move = (Move) ois.readObject();
        if (move != null) {
            LOG.fine("move received");
            if (move.white ? move.end.firstIndex() == 0 : move.end.firstIndex() == 7 && board.getTile(move.start).getPiece() instanceof Pawn) {
                while (true) {
                    Thread.sleep(100);
                    Promotion promotion = (Promotion) ois.readObject();
                    if (promotion != null) {
                        LOG.fine("promotion received");
                        controller.playMove(move);
                        controller.promote(promotion);
                        break;
                    }
                }
            }
            else {
                controller.playMove(move);
            }
        }
    }

    /**
     * Receives a board from opponent.
     */
    private void recvBoard() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        Board newBoard = (Board) ois.readObject();
        newBoard.localWhite = !newBoard.localWhite;
        controller.setBoard(newBoard);
        controller.startChessClock(newBoard.whiteTime, newBoard.clockIncrement);
        LOG.fine("board received");
    }

    /**
     * Sends a board to opponent.
     */
    private void sendBoard() throws IOException {
        ObjectOutputStream ous = new ObjectOutputStream(clientSocket.getOutputStream());
        ous.writeObject(board);
        ous.flush();
        LOG.fine("sending board");
    }

    /**
     * Sends a move to opponent.
     */
    public void sendMove(Move move) {
        try {
            ous = new ObjectOutputStream(clientSocket.getOutputStream());
            ous.writeObject(move);
            ous.flush();
        } catch (IOException e) {
            LOG.info("sending move exception");
        }
    }

    /**
     * Sends a promotion to opponent.
     */
    public void sendPromotion(Promotion promotion) {
        try {
            ous.writeObject(promotion);
            ous.flush();
        } catch (IOException e) {
            LOG.info("sending promotion exception");
        }
    }

    /**
     * Stops the server or client.
     */
    public void stop() {
        keepConnection = false;
        try {
            if (serverSocket != null) serverSocket.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            LOG.info("closing socket exception");
        }
    }
}
