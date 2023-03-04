package UI.V2;

import Utils.Utils;
import Utils.Debug;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class Logs {
    JPanel panel;
    private JButton clearLogsButton;
    private JCheckBox logLightEventsCheckBox;

    private JTextPane logs;
    private JScrollPane logScroll;

    Logs(){
        logs.setFont(new Font("Consolas",Font.PLAIN, 12));

        clearLogsButton.addActionListener(e -> {
            StyledDocument doc = logs.getStyledDocument();
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException ex) {
                Debug.log(ex);
            }
        });
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

    public void logEvent(Object o){
        if(!logLightEventsCheckBox.isSelected()) return;
        try {
            String className = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
            String print;
            String prefix = "(" + className + ")";
            if(o == null) print = "null";
            else print = o.toString();
            for (int i = 0; i < (40 - className.length()); i++) {
                prefix += " ";
            }
            if(Utils.ui != null) log(prefix + print);
        }catch(ClassNotFoundException e){
            System.out.println(e);
        }
    }
}
