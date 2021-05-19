package cz.cvut.fel.pjv.stranste.term_project;

import cz.cvut.fel.pjv.stranste.term_project.board.Board;
import cz.cvut.fel.pjv.stranste.term_project.controllers.MainWindowController;
import cz.cvut.fel.pjv.stranste.term_project.ui.MainWindowView;

public class Start {

    public static void main(String[] args) {
        Board board = new Board();
        MainWindowController controller = new MainWindowController(board);
        MainWindowView view = new MainWindowView(controller);
        controller.init();
    }
}
