/**
	Name:	Dexter Falcon
	Student ID:	009 055 534
	CPS 209 Assignment 2
   
   	NOTE:
   	SELECTING ITEM		= Make sure to click/select the item by clicking at the front end of the item.
   	DRAGGING TO LINK 	= Make sure to drop close to the middle of the body of the last rail car in the trailer.
   	CONTAINER - ADD		= This removes the container from the pile and add to the rail car.
   	CONTAINER - REMOVE	= This removes the container from the rail car and return to the pile.
	Q IMPLEMENTATION	= First in, First out.  When a container is returned to the pile, it is will be added at the end.
	
    
*/

import java.awt.* ;
import java.awt.geom.* ;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
	Container Base is a linked list implementation of a Queue of containers.
 */

public class ContainerBase extends Vehicle
{
    final int SURFACEWIDTH=40;
    final int SURFACELENGTH=20;
 
    public ContainerBase(int x, int y, int w, int h)
    {
         super(x,y,w,h);  
         
         Container c1 =(new Container(x+32,y,w,h));
         c1.setLetter("A");
         this.addAbove(c1);

         Container c2 =(new Container(x,y,w,h));
         c2.setLetter("B");
         c1.addAbove(c2);

         Container c3 =(new Container(x,y,w,h));
         c3.setLetter("C");
         c2.addAbove(c3);
         
         Container c4 =(new Container(x,y,w,h));
         c4.setLetter("D");
         c3.addAbove(c4);
       
         Container c5 =(new Container(x,y,w,h));
         c5.setLetter("E");
         c4.addAbove(c5);
    }//constructor
    
     public void draw(Graphics2D g2)
    {
         int xLeft = getX();
         int yTop= getY();
         
          Rectangle2D.Double surface = new Rectangle2D.Double(xLeft,yTop,SURFACEWIDTH,SURFACELENGTH);
          g2.setColor(Color.ORANGE);
          g2.fill(surface);
          
          if(this.hasAbove()){

             returnAbove().setX(super.getX()+4);
             returnAbove().setY(super.getY()-32);
             returnAbove().draw(g2);
            }
    }  //draw
}
