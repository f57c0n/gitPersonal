
import java.awt.geom.Ellipse2D ;
import javax.swing.JComponent;
import java.awt.Color ;
import java.awt.Rectangle ;
import java.awt.Graphics2D ;

/**
   Superclass of vehicles like train engine and rail car;
 */
abstract public class Vehicle 
{
    private int xLeft, yTop ; 				//top left corner of bounding box
    private int width ; 					//the width of the bounding box
    private int height ; 					//the height of the bounding box
    private Rectangle boundingBox ; 		//the bounding box
    
    private RailCar nextCarPointer;			//the pointer to attached rail car.
    boolean isTrailer = false;
    
    private Container aboveContainer;

    /**
    Constructs the vehicle by setting the instance variables
    @param x  position of left edge of bounding box
    @param y  position of top edge of bounding box
    @param w the width of the bounding box
    @param h the height of the bounding box
  */
   	public Vehicle(int x, int y, int w, int h)
    {
   		xLeft = x ; 
   		yTop = y ; 
   		width = w ; 
   		height = h ;
    	boundingBox = new Rectangle(xLeft, yTop, width, height) ;
    }
   	
    /**
    Sets the x, y coordinates
    @param newX the new x-coordinate
    @param newY the new y-coordinate
  */
   	public void setXY(int newX, int newY) 
    {
   		xLeft = newX ;
   		yTop = newY ;
   		boundingBox = new Rectangle(xLeft, yTop, width, height) ;
    }
    
   	/**
       Sets the x/y coordinate
       @param newX the new x-coordinate/y-coordinate
     */
    public void setX(int newX) 
    {
    	xLeft = newX ;
    	boundingBox = new Rectangle(xLeft, yTop, width, height) ;
    }
    
    public void setY(int newY) 
    {
    	yTop = newY ;
    	boundingBox = new Rectangle(xLeft, yTop, width, height) ;
    }
    
    public void moveTo(int x, int y)
    {
        xLeft = x;
        yTop = y;
    }
    
    public int getX() 								{ return xLeft ; }
    public int getY() 								{ return yTop ; }
    public int getWidth() 							{ return width ; }
    public int getHeight() 							{ return height ; }
    
    public void setTrailerStatus(boolean yesno)
    {
        isTrailer = yesno;
    }
   
    //methods to create a trailer
    public RailCar nextCar()						{ return nextCarPointer; }
    public void setNextCarPointer(RailCar railcar)	{ nextCarPointer = railcar; }
    public boolean hasTrailer()						{ return (nextCarPointer!=null); }
    public boolean checkIfTrailer()					{ return isTrailer; }
   
    public void linkSelectedCar(RailCar selected)
    {
        nextCarPointer = selected;
        nextCarPointer.setTrailerStatus(true);
    }
    
    //methods to carry the containers
    public void setAbove(Container a)				{ aboveContainer = a; }
    public void addAbove(Container b)				{ setAbove(b); }
    public boolean hasAbove()						{ return (aboveContainer !=null); }
    public Container returnAbove()					{ return aboveContainer ; }
   
    /**
       Checks if the x, y position is inside the bounding box
       @param x the x-coordinate to test
       @param y the x-coordinate to test
       @return true if (x,y) is inside the bounding box, else false
     */
    public boolean contains(int x, int y)
    {
    	if (boundingBox.contains(x,y)){ return true;}
       	return false ;
    }

    abstract void draw(Graphics2D g2);
    
}
