package UI.V2.CustomComponents;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ScrollPaneTouch extends JScrollPane implements MouseListener,   MouseMotionListener {
    private JScrollBar vbar = this.getVerticalScrollBar();

    public ScrollPaneTouch(Component view) { // 1-arity CONSTRUCTOR
        super(view);
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }

    public ScrollPaneTouch() {
        super();
    }   // 0-arity CONSTRUCTOR

    public void setViewportView(Component view) {
        super.setViewportView(view);
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }

    public void createUIComponents(){

    }

    private static boolean wasdragged = false;  // other MouseListeners may need to know this ...

    public boolean wasDragged() {
        return wasdragged;
    }   // ... this gives them safe access

    static int lastY = 0, distY = 0;
    double momentum = 0;    // not really physical momentum but it will be used to 'throw' the scroll when the mouse button is released
    static boolean lbdown = false;

    @Override
    public void mouseDragged(MouseEvent e) {
        wasdragged = true;
        distY = 0;
        int currentY = e.getYOnScreen();
        if (lbdown) {
            distY = lastY - currentY;
            vbar.setValue(distY + vbar.getValue());
            if (Math.abs(distY) > 1)
                momentum = distY + distY;   // magnify and log the momentum for later use
        }
        lastY = currentY;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            lastY = e.getYOnScreen();
            lbdown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            lbdown = false;
        if (wasdragged)
            wasdragged = false;

        if (Math.abs(momentum) <= 4.0)   // then assume that the mouse wasn't moving (much) when the button was released
            return;
        // otherwise 'throw' the scroll
        int max = vbar.getMaximum();
        int count;
        double brakingforce = 1.04;     // set an initial braking force
        for (count = 1000; count > 0; count--) {  // don't allow it to accidentally go on forever
            momentum = momentum / brakingforce; // apply the brake
            if (Math.abs(momentum) < 1.5)
                brakingforce = 1.02;    // ease off the brake towards the end (gives a slight overrun ala iOS)
            if (Math.abs(momentum) < 1.0)    // bring it to a halt
                break;
            int val = vbar.getValue();
            if (val < 0 || val > max)    // prevent overrun
                break;
            vbar.setValue((int) momentum + val);    // increment the scroll bar
            try {
                Thread.sleep(10);       // slow the loop down so the user can see the scroll
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}