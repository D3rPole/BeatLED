package UI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import static java.awt.MouseInfo.getPointerInfo;

public class Logs {
    private JTextPane logs;
    private JButton clear;
    private JPanel logPanel;
    private JScrollPane logScroll;

    private final JFrame frame;

    Logs() {
        frame = new JFrame();
        frame.setContentPane(logPanel);
        frame.setTitle("BeatLED | Event logs");
        frame.setSize(800,600);
        frame.setLocation(getPointerInfo().getLocation());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        clear.addActionListener(e -> {
            try {
                StyledDocument doc = logs.getStyledDocument();
                doc.remove(0,doc.getLength());
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    public boolean isOpen(){
        return frame.isVisible();
    }
    public void log(Object o){
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = logs.getStyledDocument();
            try {
                String str = "\n";
                if(o == null) str += "null";
                else str += o.toString();
                doc.insertString(doc.getLength(), str, doc.getStyle(""));
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
            if(doc.getLength() > 10000){
                try {
                    doc.remove(0,doc.getLength() - 10000);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }

            JScrollBar bar = logScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}
