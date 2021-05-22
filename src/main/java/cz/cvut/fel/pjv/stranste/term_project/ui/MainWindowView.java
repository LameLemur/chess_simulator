package cz.cvut.fel.pjv.stranste.term_project.ui;

import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.board.Coord;
import cz.cvut.fel.pjv.stranste.term_project.controllers.MainWindowController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

public class MainWindowView extends JFrame {

    /**
     * A reference to a chess board class.
     */
    public ChessBoard chessBoard;

    /**
     * A reference to a chess clock class.
     */
    public ChessClock chessClock;

    private final MainWindowController controller;
    private JMenuBar menuBar;

    private static final Logger LOG = Logger.getLogger(MainWindowController.class.getName());

    /**
     * Creates a new view which is a child of a JFrame.
     * @param controller windows controller
     */
    public MainWindowView(MainWindowController controller) {
        super("Chess");
        this.controller = controller;
        this.controller.setView(this);
    }

    /**
     * Initializes the view.
     */
    public void init() {
        this.setSize(970, 870);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(this.getSize());
        this.setLayout(new FlowLayout());
        chessBoard = new ChessBoard();
        chessBoard.setSize(800, 800);
        chessBoard.setPreferredSize(chessBoard.getSize());
        chessClock = new ChessClock();
        chessClock.setSize(160, 80);
        chessClock.setPreferredSize(chessClock.getSize());
        this.add(chessClock);
        this.add(chessBoard);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenu();
        menuBar.setPreferredSize(new Dimension(970, 25));
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }

    private void initMenu() {
        menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem startGamewBot = new JMenuItem("Start game w/ bot");
        startGamewBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startBotGame();
            }
        });
        JMenuItem startLocalGame = new JMenuItem("Start local game");
        startLocalGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startLocalGame();
            }
        });
        JMenuItem hostLanGame = new JMenuItem("Host lan game");
        hostLanGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.hostGame();
            }
        });
        JMenuItem joinLanGame = new JMenuItem("Join lan game");
        joinLanGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.joinGame();
            }
        });
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JMenu boardMenu = new JMenu("Board");
        JMenuItem saveGame = new JMenuItem("Save board");
        saveGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.saveGame();
            }
        });
        JMenuItem exportToPgn = new JMenuItem("Export to pgn");
        exportToPgn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.exportToPgn();
            }
        });
        JMenuItem loadGame = new JMenuItem("Load saved board");
        loadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.loadGame();
            }
        });
        JMenuItem loadDefaultGame = new JMenuItem("Load new default board");
        loadDefaultGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.loadDefaultGame();
            }
        });
        gameMenu.add(startGamewBot);
        gameMenu.add(startLocalGame);
        gameMenu.add(hostLanGame);
        gameMenu.add(joinLanGame);
        gameMenu.add(quit);
        boardMenu.add(saveGame);
        boardMenu.add(exportToPgn);
        boardMenu.add(loadGame);
        boardMenu.add(loadDefaultGame);
        menuBar.add(gameMenu);
        menuBar.add(boardMenu);
    }

    public class ChessClock extends JPanel {
        JLabel whiteTime;
        JLabel blackTime;

        private ChessClock() {
            super(new GridLayout(1, 2));
            this.setBorder(new LineBorder(Color.BLACK));
            whiteTime = new JLabel("0:00", JLabel.CENTER);
            blackTime = new JLabel("0:00", JLabel.CENTER);
            whiteTime.setFont(new Font("Calibri", Font.PLAIN, 26));
            blackTime.setFont(new Font("Calibri", Font.PLAIN, 26));
            this.add(whiteTime);
            this.add(blackTime);
        }

        /**
         * Sets the time for white.
         * @param seconds time
         */
        public void setWhiteTime(int seconds) {
            whiteTime.setText((seconds / 60) + ":" + (((seconds % 60) >= 10) ? String.valueOf(seconds % 60) : "0" + seconds % 60));
        }

        /**
         * Sets the time for black.
         * @param seconds time
         */
        public void setBlackTime(int seconds) {
            blackTime.setText((seconds / 60) + ":" + (((seconds % 60) >= 10) ? String.valueOf(seconds % 60) : "0" + seconds % 60));
        }
    }

    public class ChessBoard extends JPanel {

        Tile[][] tiles;

        private ChessBoard() {
            super(new GridLayout(9, 9));
            this.setBorder(new LineBorder(Color.BLACK));
            tiles = new Tile[8][8];
            init();
        }

        private void init() {
            boolean white = true;
            int index = 0;
            for (int i = 8; i >= 0; i--) {
                for (int ii = 0; ii < 9; ii++) {
                    if (i == 0) {
                        if (ii == 0) {
                            this.add(new JLabel(""));
                        }
                        else {
                            this.add(new JLabel(String.valueOf(Board.letters[ii - 1]), SwingConstants.CENTER));
                        }
                    }
                    else if (ii == 0) {
                        this.add(new JLabel(String.valueOf(Board.numbers[(i - 1)]), SwingConstants.CENTER));
                    }
                    else {
                        Tile tile = new Tile(new Coord(Board.letters[ii - 1], Board.numbers[(i - 1)]));
                        tile.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                controller.tileClicked(tile.coord);
                            }
                        });
                        if (white) {
                            tile.setBackground(Color.WHITE);
                        }
                        else {
                            tile.setBackground(Color.DARK_GRAY);
                        }
                        white = !white;
                        this.add(tile);
                        tiles[index][ii - 1] = tile;
                    }
                }
                white = !white;
                index++;
            }
        }

        /**
         * Draws a BufferedImage to specifics tile determined by the given coordinate.
         * @param coord tile
         * @param img piece asset
         */
        public void drawPiece(BufferedImage img, Coord coord) {
            try {
                tiles[coord.firstIndex()][coord.secondIndex()].setIcon(new ImageIcon(resizeImage(img, 84, 84)));
            } catch (IOException e) {
                LOG.info("Chess piece image resizing error");
            }
        }

        /**
         * Draws a red background to specifics tile determined by the given coordinate.
         * @param coord tile
         */
        public void paintRed(Coord coord) {
            tiles[coord.firstIndex()][coord.secondIndex()].setBackground(Color.RED);
        }

        /**
         * Draws a green background to specifics tile determined by the given coordinate.
         * @param coord tile
         */
        public void paintGreen(Coord coord) {
            tiles[coord.firstIndex()][coord.secondIndex()].setBackground(Color.GREEN);
        }

        /**
         * Resets background of all tiles.
         */
        public void rePaintBoard() {
            boolean white = true;
            for (int i = 0; i < 8; i++) {
                for (int ii = 0; ii < 8; ii++) {
                    Tile tile = tiles[i][ii];
                    if (white) {
                        tile.setBackground(Color.WHITE);
                    }
                    else {
                        tile.setBackground(Color.DARK_GRAY);
                    }
                    tile.setOpaque(true);
                    white = !white;
                }
                white = !white;
            }
        }

        private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            graphics2D.dispose();
            return resizedImage;
        }
    }

    private class Tile extends JButton {
        private Coord coord;

        public Tile(Coord coord) {
            super();
            this.coord = coord;
        }
    }
}
