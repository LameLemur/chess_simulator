package cz.cvut.fel.pjv.stranste.term_project.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Dialog {

    public int time;
    public int increment;
    public boolean whiteStarts;
    public boolean whiteLocal;
    public int port;
    public String address;

    private Dialog(boolean whiteStarts, boolean whiteLocal, int time, int increment) {
        this.time = time;
        this.increment = increment;
        this.whiteStarts = whiteStarts;
        this.whiteLocal = whiteLocal;
    }

    /**
     * Show a dialog to setup a game against bot.
     */
    public static Dialog showBotDialog(MainWindowView view)
    {
        final JPanel panel = new JPanel(new GridLayout(3,1));

        //Start color
        JPanel startColor = new JPanel((new GridLayout(2,1)));
        startColor.add(new JLabel("Starts:"));
        JPanel startColorSelect = new JPanel((new GridLayout(1,2)));
        ButtonGroup startButtonGroup = new ButtonGroup();
        final JRadioButton whiteStarts = new JRadioButton("White");
        whiteStarts.setSelected(true);
        final JRadioButton blackStarts = new JRadioButton("Black");
        startButtonGroup.add(whiteStarts);
        startButtonGroup.add(blackStarts);
        startColorSelect.add(whiteStarts);
        startColorSelect.add(blackStarts);
        startColor.add(startColorSelect);

        panel.add(startColor);

        JPanel botColor = new JPanel((new GridLayout(2,1)));
        botColor.add(new JLabel("Bot:"));
        JPanel botColorSelect = new JPanel((new GridLayout(1,2)));
        ButtonGroup botButtonGroup = new ButtonGroup();
        final JRadioButton botStartsWhite = new JRadioButton("White");
        botStartsWhite.setSelected(true);
        final JRadioButton botStartsBlack = new JRadioButton("Black");
        botButtonGroup.add(botStartsWhite);
        botButtonGroup.add(botStartsBlack);
        botColorSelect.add(botStartsWhite);
        botColorSelect.add(botStartsBlack);
        botColor.add(botColorSelect);

        panel.add(botColor);

        //time control
        String[] timeBases = {"1m","5m","10m","30m"};
        int[] intTimeBases = {60,300,600,1800};
        JPanel timeControl = new JPanel((new GridLayout(2,1)));
        JPanel baseTimeSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox timeBase = new JComboBox(timeBases);
        baseTimeSelect.add(new JLabel("Base time:"));
        baseTimeSelect.add(timeBase);
        timeControl.add(baseTimeSelect);

        String[] increments = {"+1s","+5s","+10s","+30s"};
        int[] intIncrements = {1,5,10,30};
        JPanel incrementSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox increment = new JComboBox(increments);
        incrementSelect.add(new JLabel("Increment:"));
        incrementSelect.add(increment);
        timeControl.add(incrementSelect);

        panel.add(timeControl);

        int retVal = JOptionPane.showConfirmDialog(view, panel, "Game settings", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);


        return retVal == 0 ? new Dialog(whiteStarts.isSelected(), botStartsBlack.isSelected(), intTimeBases[timeBase.getSelectedIndex()], intIncrements[increment.getSelectedIndex()]) : null;
    }

    /**
     * Show a dialog to setup a local game.
     */
    public static Dialog showLocalDialog (MainWindowView view)
    {
        final JPanel panel = new JPanel(new GridLayout(2,1));

        //Start color
        JPanel startColor = new JPanel((new GridLayout(2,1)));
        startColor.add(new JLabel("Starts:"));
        JPanel startColorSelect = new JPanel((new GridLayout(1,2)));
        ButtonGroup startButtonGroup = new ButtonGroup();
        final JRadioButton whiteStarts = new JRadioButton("White");
        whiteStarts.setSelected(true);
        final JRadioButton blackStarts = new JRadioButton("Black");
        startButtonGroup.add(whiteStarts);
        startButtonGroup.add(blackStarts);
        startColorSelect.add(whiteStarts);
        startColorSelect.add(blackStarts);
        startColor.add(startColorSelect);

        panel.add(startColor);

        //time control
        String[] timeBases = {"1m","5m","10m","30m"};
        int[] intTimeBases = {60,300,600,1800};
        JPanel timeControl = new JPanel((new GridLayout(2,1)));
        JPanel baseTimeSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox timeBase = new JComboBox(timeBases);
        baseTimeSelect.add(new JLabel("Base time:"));
        baseTimeSelect.add(timeBase);
        timeControl.add(baseTimeSelect);

        String[] increments = {"+1s","+5s","+10s","+30s"};
        int[] intIncrements = {1,5,10,30};
        JPanel incrementSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox increment = new JComboBox(increments);
        incrementSelect.add(new JLabel("Increment:"));
        incrementSelect.add(increment);
        timeControl.add(incrementSelect);

        panel.add(timeControl);

        int retVal = JOptionPane.showConfirmDialog(view, panel, "Game settings", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

        return retVal == 0 ? new Dialog(whiteStarts.isSelected(), true, intTimeBases[timeBase.getSelectedIndex()], intIncrements[increment.getSelectedIndex()]) : null;
    }

    /**
     * Show a dialog to setup a lan game.
     */
    public static Dialog showHostDialog (MainWindowView view)
    {
        final JPanel panel = new JPanel(new GridLayout(4,1));

        //Start color
        JPanel startColor = new JPanel((new GridLayout(2,1)));
        startColor.add(new JLabel("Starts:"));
        JPanel startColorSelect = new JPanel((new GridLayout(1,2)));
        ButtonGroup startButtonGroup = new ButtonGroup();
        final JRadioButton whiteStarts = new JRadioButton("White");
        whiteStarts.setSelected(true);
        final JRadioButton blackStarts = new JRadioButton("Black");
        startButtonGroup.add(whiteStarts);
        startButtonGroup.add(blackStarts);
        startColorSelect.add(whiteStarts);
        startColorSelect.add(blackStarts);
        startColor.add(startColorSelect);

        panel.add(startColor);

        //localwhite
        JPanel localColor = new JPanel((new GridLayout(2,1)));
        localColor.add(new JLabel("You:"));
        JPanel localColorSelect = new JPanel((new GridLayout(1,2)));
        ButtonGroup localButtonGroup = new ButtonGroup();
        final JRadioButton localStartsWhite = new JRadioButton("White");
        localStartsWhite.setSelected(true);
        final JRadioButton localStartsBlack = new JRadioButton("Black");
        localButtonGroup.add(localStartsWhite);
        localButtonGroup.add(localStartsBlack);
        localColorSelect.add(localStartsWhite);
        localColorSelect.add(localStartsBlack);
        localColor.add(localColorSelect);

        panel.add(localColor);

        //time control
        String[] timeBases = {"1m","5m","10m","30m"};
        int[] intTimeBases = {60,300,600,1800};
        JPanel timeControl = new JPanel((new GridLayout(2,1)));
        JPanel baseTimeSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox timeBase = new JComboBox(timeBases);
        baseTimeSelect.add(new JLabel("Base time:"));
        baseTimeSelect.add(timeBase);
        timeControl.add(baseTimeSelect);

        String[] increments = {"+1s","+5s","+10s","+30s"};
        int[] intIncrements = {1,5,10,30};
        JPanel incrementSelect = new JPanel((new GridLayout(1,2)));
        final JComboBox increment = new JComboBox(increments);
        incrementSelect.add(new JLabel("Increment:"));
        incrementSelect.add(increment);
        timeControl.add(incrementSelect);

        panel.add(timeControl);

        JPanel portPanel = new JPanel((new GridLayout(2,1)));
        JPanel portSelect = new JPanel((new GridLayout(1,2)));
        final JTextField portText = new JTextField("20101");
        portSelect.add(new JLabel("Port:"));
        portSelect.add(portText);
        portPanel.add(portSelect);

        panel.add(portPanel);

        boolean success = false;
        int retVal = -1;
        int port = 20101;
        while(!success) {
            try {
                retVal = JOptionPane.showConfirmDialog(view, panel, "Game settings", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(retVal == 0) {
                    port = Integer.parseInt(portText.getText());
                }
                success = true;
            } catch (NumberFormatException e) {
                //log wrong port format
            }
        }

        Dialog dialog = new Dialog(whiteStarts.isSelected(), localStartsWhite.isSelected(), intTimeBases[timeBase.getSelectedIndex()], intIncrements[increment.getSelectedIndex()]);
        dialog.port = port;
        return retVal == 0 ? dialog : null;
    }

    /**
     * Show a dialog to join a lan game.
     */
    public static Dialog showJoinDialog (MainWindowView view)
    {
        final JPanel panel = new JPanel(new GridLayout(2,1));

        //Start color
        JPanel startColor = new JPanel((new GridLayout(2,1)));

        JPanel addressPanel = new JPanel((new GridLayout(2,1)));
        JPanel addressSelect = new JPanel((new GridLayout(1,2)));
        final JTextField addressText = new JTextField("localhost");
        addressSelect.add(new JLabel("Address:"));
        addressSelect.add(addressText);
        addressPanel.add(addressSelect);

        panel.add(addressPanel);

        JPanel portPanel = new JPanel((new GridLayout(2,1)));
        JPanel portSelect = new JPanel((new GridLayout(1,2)));
        final JTextField portText = new JTextField("20101");
        portSelect.add(new JLabel("Port:"));
        portSelect.add(portText);
        portPanel.add(portSelect);

        panel.add(portPanel);

        boolean success = false;
        int retVal = -1;
        int port = 20101;
        while(!success) {
            try {
                retVal = JOptionPane.showConfirmDialog(view, panel, "Game settings", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(retVal == 0) {
                    port = Integer.parseInt(portText.getText());
                }
                success = true;
            } catch (NumberFormatException e) {
                //log wrong port format
            }
        }

        Dialog dialog = new Dialog(false, false, 0, 0);
        dialog.port = port;
        dialog.address = addressText.getText();
        return retVal == 0 ? dialog : null;
    }
}
