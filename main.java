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
    public static views.Theme mainTheme;
    public static views.Window mainWindow;
    public static views.Board mainBoard;

    public static void main(String[] args)
    {
        // new window
        mainTheme = new Theme();
        mainWindow =  new views.Window(mainTheme);

        // new board
        int constraint = mainWindow.getSizeConstraint(); 
        constraint = (constraint - (int)(constraint * 0.06));                            // regardless of screen size, every board and frame needs to be padded (in this case 6%).
        //int boardSize = 830;                                                             // uncomment for a small defualt board.
        int boardSize = constraint;                                                      // I prefer the largest possible board size as default, this should result in a "1" as pixelSize. 
        int pixelSize = constraint / boardSize;
        mainBoard = new Board(pixelSize, boardSize, constraint);
        mainWindow.addBoard(mainBoard);
        

        // build sim and controls, pack and display!
        //start(boardSize);
        
        // Start the main loop!! :D
        // threaded for OS resource management, efficiency and EDT event handling.
        new Thread(() -> {
            while (mainWindow.isOpen()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Bye Felicia.");
        }).start();

    }

}