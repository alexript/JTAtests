/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author malyshev
 */
public class LogPanel extends JPanel {

    private static final long serialVersionUID = 9104545903491724364L;
    private final LoggerDispatcher loggerDispatcher;
    private final JTextPane logTextPane;
    private final ActionListener clearListener;
    private final ActionListener resetListener;

    public LogPanel(final LoggerDispatcher dispatcher, String nameSpace, final Map<String, PrintWriter> scopedWriters) {
        super();
        logTextPane = new JTextPane();
        prepareTextPane();
        clearListener = (ActionEvent evt) -> {
            LogPanel.this.clean();
        };
        resetListener = (ActionEvent evt) -> {
            LogPanel.this.cleanAll();
        };
        this.loggerDispatcher = dispatcher;
        init(nameSpace, scopedWriters);
    }

    private void prepareTextPane() {
        logTextPane.setEditable(false);
    }

    protected void clean() {
        Document document = logTextPane.getDocument();
        try {
            if (document != null) {
                document.remove(0, document.getLength());
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(LogPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cleanAll() {
        loggerDispatcher.resetPanels();
    }

    private void init(String nameSpace, final Map<String, PrintWriter> scopedWriters) {
        setLayout(new BorderLayout());

        JButton resetLogButton = new JButton();
        resetLogButton.setText("<html>&#8855;");
        resetLogButton.setToolTipText("Clear <%s> log".formatted(nameSpace));
        resetLogButton.setFocusable(false);
        resetLogButton.setHorizontalTextPosition(SwingConstants.CENTER);
        resetLogButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        resetLogButton.addActionListener(clearListener);

        JButton removeAllLogButton = new JButton();
        removeAllLogButton.setText("<html>&#8416;");
        removeAllLogButton.setToolTipText("Clear all log");
        removeAllLogButton.setFocusable(false);
        removeAllLogButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeAllLogButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        removeAllLogButton.addActionListener(resetListener);

        JToolBar logToolBar = new JToolBar();
        logToolBar.setOrientation(SwingConstants.VERTICAL);
        logToolBar.setRollover(true);
        logToolBar.add(resetLogButton);
        logToolBar.add(removeAllLogButton);

        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setViewportView(logTextPane);

        add(logToolBar, BorderLayout.WEST);
        add(logScrollPane, BorderLayout.CENTER);

        Document document = logTextPane.getDocument();
        DocumentPrintStream outPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.out, false);
        scopedWriters.put(nameSpace, new PrintWriter(outPrintStream));
    }

}
