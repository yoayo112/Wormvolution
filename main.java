/**
 * The interface that handles state, event and action listeners. 
 * Responsible for construction, start, restart, tick, stop.
 * The wiring that holds everything together. It is the root of the program.
 * 
 * a combination of GUI, Frame and Controls from version 1. 
 *
 * @author Sean Thornton and Sky Vercauteren
 * @version 2.0 December 2025
 */

//generic library
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.*;

//packages
import views.*;

public class Main{

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new views.Window());
    }

}