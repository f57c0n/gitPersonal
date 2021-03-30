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



public class Container extends Vehicle
{
	final int ContainerWIDTH=30;
    final int ContainerLENGTH=30;
    private Rectangle2D.Double Container;
    private String letter="";

    private static Container belowContainer;			//use for drawing purposes only

    public Container(int x, int y, int w, int h)
    {
    	super(x,y,w,h);  
    }

    public Rectangle2D.Double returnContainer()			{return Container;}
    
    public void setLetter(String s)						{this.letter = s;}
    public  String getLetter()							{return letter;}
 
    public static void setBelow(Container b)			{belowContainer = b;}
    public static Container returnBelow(Container b)	{return belowContainer;}
    
    public void draw(Graphics2D g2)
    {
        int xLeft = getX();
        int yTop= getY();
      
        Container = new Rectangle2D.Double(xLeft,yTop,ContainerWIDTH,ContainerLENGTH);

        g2.drawString(letter, getX() + 12, yTop + 22) ;
        g2.draw(Container);
        
        if(this.hasAbove())
        {
        	returnAbove().setX(super.getX());
        	returnAbove().setY(super.getY()-32);
        	returnAbove().draw(g2);
        }    
    }      
}

