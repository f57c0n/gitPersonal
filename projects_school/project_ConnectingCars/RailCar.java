import java.awt.* ;
import java.awt.geom.* ;

/**
   Rail-car is a platform bed that is labeled with a number and can carry a container above.
 */

public class RailCar extends Vehicle
{
    public static final int UNIT = 10 ;
    public static final int U6 = 6 * UNIT ;
    public static final int U5 = 5 * UNIT ;
    public static final int U4 = 4 * UNIT ;
    public static final int U3 = 3 * UNIT ;
    public static final int U2 = 2 * UNIT ;
    public static final int U15 = UNIT + UNIT / 2 ;
    public static final int U05 =  UNIT / 2 ;
    public static final int BODY_WIDTH = U3 ;
    public static final int BODY_HEIGHT = U2 ;;
    
    private int number;    
    
    public RailCar(int x, int y, int w, int h)
    {
        super(x,y,w,h);
    }
    
    public void setNumber(int number)  		{ this.number = number; }
    int getNumber()							{ return number; }

    
    /**
       Draw the rail car
       @param g2 the graphics context
     */
    public void draw(Graphics2D g2)
    {
    
    	int x = getX() ;
    	int y = getY() ;
    
    	Rectangle2D.Double body 
    		= new Rectangle2D.Double(getX(), y + UNIT, U6, UNIT);      
    	Ellipse2D.Double frontTire 
    		= new Ellipse2D.Double(getX() + UNIT, y + U2, UNIT, UNIT);
    	Ellipse2D.Double rearTire
    		= new Ellipse2D.Double(getX() + U4, y + U2, UNIT, UNIT);
    
    	// the bottom of the front windshield
    	Point2D.Double r1 
    		= new Point2D.Double(getX() + UNIT, y + UNIT);
    	// the front of the roof
    	Point2D.Double r2 
    		= new Point2D.Double(getX() + U2, y);
    	// the rear of the roof
    	Point2D.Double r3 
    		= new Point2D.Double(getX() + U4, y);
    	// the bottom of the rear windshield
    	Point2D.Double r4 
    		= new Point2D.Double(getX() + U5, y + UNIT);

    	// the right end of the hitch
    	Point2D.Double r5 
    		= new Point2D.Double(getX() + U6, y + U15);
    	// the left end of the hitch
    	Point2D.Double r6 
    		= new Point2D.Double(getX() + U6 + U05, y + U15);
    
    	Line2D.Double frontWindshield 
    		= new Line2D.Double(r1, r2);
    	Line2D.Double roofTop 
    		= new Line2D.Double(r2, r3);
    	Line2D.Double rearWindshield
    		= new Line2D.Double(r3, r4);
    	Line2D.Double hitch
    		= new Line2D.Double(r5, r6);

    	g2.draw(body);
    	g2.draw(hitch);
    	g2.draw(frontTire);
    	g2.draw(rearTire);
    	g2.draw(body) ;

    	g2.drawString("" + getNumber(), getX() + U2, y + U2) ;
   
    	if(this.hasTrailer())
    	{
    		RailCar last = this.nextCar();
    		while(last.hasTrailer())
    			last=last.nextCar();
         
    		this.nextCar().setX(super.getX()+65);
    		this.nextCar().setY(super.getY());
    		this.nextCar().draw(g2);
    	}
       
    	if(this.hasAbove())
    	{
    		returnAbove().setX(super.getX()+4);
    		returnAbove().setY(super.getY()-22);
    		returnAbove().draw(g2);
    	}
      
    }
    
}

    