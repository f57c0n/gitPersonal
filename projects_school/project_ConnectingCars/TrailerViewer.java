/**
	Name:	JJ Falcon
	CPS 209 Assignment 2
   
   	NOTE:
   	SELECTING ITEM		= Make sure to click/select the item by clicking at the front end of the item.
   	DRAGGING TO LINK 	= Make sure to drop close to the middle of the body of the last rail car in the trailer.
   	CONTAINER - ADD		= This removes the container from the pile and add to the rail car.
   	CONTAINER - REMOVE	= This removes the container from the rail car and return to the pile.
	  Q IMPLEMENTATION	= First in, First out.  When a container is returned to the pile, it will be added at the end.
	
    
*/

import javax.swing.JFrame;

// Viewer
public class TrailerViewer
{  
   public static void main(String[] args)
   {  
      JFrame frame = new TrailerFrame();
      TrailerComponent vc = new TrailerComponent();
      frame.add(vc);
      
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle("Loading the Train");
      frame.setVisible(true);      
   }
}