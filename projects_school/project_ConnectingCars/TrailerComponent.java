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

import javax.swing.* ;
import java.util.NoSuchElementException ;
import java.util.Queue;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.geom.Ellipse2D ;
import java.awt.event.MouseEvent ;
import java.awt.event.MouseListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseMotionListener ;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class TrailerComponent extends JComponent
{ 
    private static Vehicle selected = null;
    private static TrainEngine theEngine;
    private static ContainerBase theBase;
    
    private static int itemCounter = 0;
    
    private static ArrayList<Vehicle> vehicleList = new ArrayList<Vehicle>();
    //private static Queue<Container> containerPile = new LinkedList<>();
    
     /**
       Constructs the component and gives it a mouse listener
     */
    public TrailerComponent()
    {
    	 int size = 30; 
    	 
    	 // Listens to mouse clicks
    	 class MyMouseListener extends MouseAdapter
    	 {
    		 public void mousePressed(MouseEvent event)
    		 {
    			 int x = event.getX();
				 int y = event.getY();
    			 
    			 //creating train engine
    			 if(itemCounter == 0)
    			 {
    				 vehicleList.add(new TrainEngine(x,y,75, 75));
    				 theEngine = (TrainEngine)vehicleList.get(0);
    				 itemCounter++;
    				 repaint();
    			 }
          
    			 //creating the Queue
    			 else if(itemCounter == 6)
    			 {
    				  /*containerBase = new ContainerBase(xLeft,yTop,50,50);
    				  containerPile.add(new Container(xLeft,yTop-162,50,50,"A"));
    				  containerPile.add(new Container(xLeft,yTop-130,50,50,"B"));
    				  containerPile.add(new Container(xLeft,yTop-98,50,50,"C"));
    				  containerPile.add(new Container(xLeft,yTop-64,50,50,"D"));
    				  containerPile.add(new Container(xLeft,yTop-32,50,50,"E"));
    				  **/
    				 
    				 theBase = new ContainerBase(x,y,50,50);         
    				 vehicleList.add(theBase);
    				 itemCounter++;
    			 }
    			 
    			 //creating the rail cars
    			 else if(itemCounter > 0 && itemCounter < 7)
    			 {
    				 vehicleList.add(new RailCar(x,y,size, size));
    				 ((RailCar)vehicleList.get(itemCounter)).setNumber(itemCounter);

    				 repaint();
    				 itemCounter++;
    			 }
    			 
    			 //holding selected item
    			 else 
    			 {
    				 selected=null;

    				 for(int i=0; i<vehicleList.size();i++)
    				 {
    					 if(vehicleList.get(i).contains(x,y) && !vehicleList.get(i).checkIfTrailer()) 
    					 {
    						 selected = vehicleList.get(i);
    						 break;
    					 }
    				 }
    			 }
    			 repaint();
    		 }//mousePressed
    		 
    		 //to add selected rail-car by dragging at the end of the trailer.
    		 public void mouseReleased(MouseEvent event)
    		 {
    			 int x = event.getX();
    			 int y = event.getY();
          
    			 if(itemCounter == -1 && selected != null)
    				 for(Vehicle v : vehicleList)
    				 {
    					 Vehicle secondlast = null;
    					
    					 if(v != selected && !v.checkIfTrailer() && !v.hasTrailer() && v.contains(x,y))
    					 {
    						 v.linkSelectedCar((RailCar)selected);
    					 }
                
    					 else if (selected != theEngine && v.hasTrailer())
    					 {
    						 Vehicle last = v; 
    						 
    						 while(last.hasTrailer())
    						 {
    							 secondlast = last;
    							 last = last.nextCar();
    						 }
    						 
    						 if(last.contains(x,y))
    						 {
    							 RailCar temp = new RailCar(last.getX(),last.getY(),size, size);
                     
    							 temp.setNumber(((RailCar)last).getNumber());
    							 temp.setAbove(last.returnAbove());
    							 
    							 vehicleList.remove(selected);
    							 
    							 temp.linkSelectedCar((RailCar)selected);
                     
    							 secondlast.setNextCarPointer(temp);
    							 
    							 break;
    						 }
    					 }//else-if
    				 }//for Vehicle
    			 repaint();
           }//mouseReleased
    	 }//MouseListener
        
        addMouseListener(new MyMouseListener()); 
     
        
        class MyMotionListener implements MouseMotionListener
        {
        	public void mouseDragged(MouseEvent event)
        	{
        		if(selected!=null)
        		{ 
        			selected.setX(event.getX());
        			selected.setY(event.getY());
        			repaint();
        		}
        	}
        
        	public void mouseMoved(MouseEvent event){}
        }//MotionListener

        addMouseMotionListener(new MyMotionListener()) ;
   }//Vehicle Component constructor 
   
    
    //adds selected rail car to the Train 
    public void addFirst()
    {
    	if (selected == theEngine) throw new NoSuchElementException();
    	
    	RailCar tempPointer = (RailCar)selected;
    	
    	if(selected != null && selected != theEngine && !theEngine.hasTrailer())
    	{
    		theEngine.linkSelectedCar((RailCar)selected);
    	}
      
    	else if (selected != null && selected != theEngine && theEngine.hasTrailer())
    	{
    		//traverse till the last rail car
    		while(tempPointer.hasTrailer())
    		{
    			tempPointer = tempPointer.nextCar();
    		}
    		
    		tempPointer.linkSelectedCar(theEngine.nextCar());
    		theEngine.linkSelectedCar((RailCar)selected);
    		repaint();
         }
    	
    	repaint();
    	selected = null;
    	
    }//addFirst
      
    
    public void addLast()
    { 
    	if (selected == theEngine) throw new NoSuchElementException();

    	Vehicle tempPointer = theEngine;
      
    	if(theEngine.hasTrailer())
    	{
    		tempPointer = theEngine.nextCar();
    		
    		//traverse till the last rail car
    		while(tempPointer.hasTrailer())
    		{
              tempPointer = tempPointer.nextCar();
            } 
        }
      
        tempPointer.linkSelectedCar((RailCar)selected);
        repaint();
        selected = null;
        
    }//addLast
      
    
    public void removeFirst()
    {
    	Random random = new Random();
    	int x = 200 + random.nextInt(400);
    	int y = 500 + random.nextInt(100);
    	
    	if (theEngine.nextCar() == null)throw new NoSuchElementException();

        RailCar tempPointer = theEngine.nextCar(); 
        
        theEngine.setNextCarPointer(tempPointer.nextCar());
        tempPointer.setNextCarPointer(null);  
        
        tempPointer.setX(x);
        tempPointer.setY(y);
        tempPointer.setTrailerStatus(false);
        
        vehicleList.remove(tempPointer);
        vehicleList.add(tempPointer);
         
        selected=null; 
        
    }//removeFirst
        

    public void removeLast()
    {	
    	Random random = new Random();
        int x = 100 + random.nextInt(500);
        int y = 300 + random.nextInt(300);
        
        if (theEngine.nextCar() == null)throw new NoSuchElementException();

        RailCar tempPointer = theEngine.nextCar(); 
        Vehicle trailingPointer = theEngine;
        
        while (tempPointer.hasTrailer())
        {
        	tempPointer = tempPointer.nextCar();
        	trailingPointer = trailingPointer.nextCar();
        }
         
        trailingPointer.setNextCarPointer(null);
        
        tempPointer.setX(x);
        tempPointer.setY(y);
        tempPointer.setTrailerStatus(false);   

        vehicleList.remove(tempPointer);
    	vehicleList.add(tempPointer);
            
        selected = null;
         
    }//removeLast
       
    //remove from Q and add to rail car          
    public void add()
    {
    	Vehicle tempPointer = selected;
    	Container nextContainer;
    	
    	//locates the next empty rail-car in a trailer
    	while(tempPointer == theEngine || (tempPointer.hasAbove() && tempPointer.hasTrailer()))
    	{
    		tempPointer = tempPointer.nextCar();
    	}
            
    	if(theBase.hasAbove() && !tempPointer.hasAbove())
    	{
                nextContainer = theBase.returnAbove();
                theBase.setAbove(nextContainer.returnAbove());
                nextContainer.setAbove(null);
                
                tempPointer.setAbove(nextContainer);
    	}

    	/*RailCar selectedCar = (RailCar) selected;
    	Container movedContainer;  	
    	
    	if(selected == e && containerQ.isEmpty()) throw new NoSuchElementException();
    	
    	if (!containerPile.isEmpty())
    	{
    		movedContainer = containerPile.remove();
    		movedContainer.setX(selected.getX()+4);
    		movedContainer.setY(selected.getY()-22);
    		    	
            
    		while(selectedCar.hasAbove() && selectedCar.hasTrailer())
    		{
    			selectedCar = selectedCar.nextCar();
    		}
    		
    		if (selectedCar != null)
    			selectedCar.addAbove(movedContainer);
    		
    		qSize--;
    	  **/
    	
    	selected = null;
            
    }//addQ
  
    //remove from rail car and add to Q
    public void remove()
    {
    	Vehicle containerPointer = theBase;
         
    	//locates the spot to put added container
    	while(containerPointer.hasAbove())
    	{
    		containerPointer = containerPointer.returnAbove();
    	}
    
    	//locates the next loaded rail-car in a trailer
    	while(selected == theEngine || (!selected.hasAbove() && selected.hasTrailer()))
    	{
    		selected = selected.nextCar();
    	}    	
    	
    	if(selected.hasAbove())
    	{
    		Container selectedContainer = selected.returnAbove();
    		
    		selectedContainer.setX(0);
    		selectedContainer.setY(0);
    		repaint();
                
    		containerPointer.addAbove(selectedContainer);
    		selected.setAbove(null);   
    	}
    	
    	/*
    	RailCar selectedCar = (RailCar) selected;
    	Container movedContainer; 
  
       	if(selected == e || !selectedCar.hasAbove()) throw new NoSuchElementException();
       	
       	qSize++;
       	movedContainer = selectedCar.returnAbove();
       	movedContainer.setX(baseline.getX());
       	movedContainer.setY(baseline.getY()+(qSize*-32));
        containerPile.add(movedContainer);

        selectedCar.removeAbove();
    	 **/
            
    	selected = null;
            
    }//removeQ
                
    public void restart()
    {
    	itemCounter=0;
    	vehicleList = new ArrayList<Vehicle>();
    	theBase = null;  
    }
        

    public void paintComponent(Graphics g)
    {
    	Graphics2D g2 = (Graphics2D) g ;      
        
        for(int i =0;i<vehicleList.size();i++)
		{              
			if(selected == vehicleList.get(i))					{g2.setColor(Color.MAGENTA);}
			if(vehicleList.get(i).checkIfTrailer() == false)	{vehicleList.get(i).draw(g2);} 
			g2.setColor(Color.BLUE);
		}

        if(theBase!=null)										
        	{theBase.draw(g2);}
        
        /*for (Container q : containerQ)
    	{
     	  	q.draw(g2);
    	}**/  
        
    	if(itemCounter==7) 
    	{
    		itemCounter=-1;
    		repaint();           
    	}
       
    }//paint
                
}//Vehicle Component

      
    
    

