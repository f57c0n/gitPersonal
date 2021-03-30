
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
   This frame has a menu with commands to add/remove rail cars from a trailer 
   and to add/remove containers on/from rail-cars.
   
*/
public class TrailerFrame extends JFrame
{
   private static final int FRAME_WIDTH = 700;
   private static final int FRAME_HEIGHT = 900;
   
   TrailerComponent tc = new TrailerComponent();

   /**
      Constructs the frame.
   */
   public TrailerFrame()
   {  
      // Construct menu      
      JMenuBar menuBar = new JMenuBar();     
      setJMenuBar(menuBar);
      menuBar.add(createFileMenu());
      menuBar.add(createQueueMenu());
      menuBar.add(createListMenu());
      setSize(FRAME_WIDTH, FRAME_HEIGHT);
   }

   class ExitItemListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         System.exit(0);
      }
   }      
   
   /**
      Creates the main menus.
      @return the menu
   */
   
   public JMenu createFileMenu()
   {
      JMenu menu = new JMenu("File");
      menu.add(createNewItem());
      JMenuItem exitItem = new JMenuItem("Exit");      
      ActionListener listener = new ExitItemListener();
      exitItem.addActionListener(listener);
      menu.add(exitItem);
      return menu;
   }

   public JMenu createQueueMenu()
   {
      JMenu menu = new JMenu("Queue");
      menu.add(createAddItem());
      menu.add(createRemoveItem());
      return menu;
   }  
   
   public JMenu createListMenu()
   {
      JMenu menu = new JMenu("List");
      menu.add(createAddFirstItem());
      menu.add(createAddLastItem());
      menu.add(createRemoveFirstItem());
      menu.add(createRemoveLastItem());
      return menu;
   }  
      
      /**
      Creates the sub-menus.
      @return the menu
   */
      
   public JMenuItem createNewItem()
   {
      class AddNewListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {
            tc.restart();
            repaint();
         }
      }   
      JMenuItem item = new JMenuItem("New");      
      ActionListener listener = new AddNewListener();
      item.addActionListener(listener);
      return item;
   }//createNew
   
   
   public JMenuItem createAddItem()
   {
	   class AddItemListener implements ActionListener
	   {
		   public void actionPerformed(ActionEvent event)
		   {    
			   tc.add();
			   repaint();
		   }	
	   }  
    
	   JMenuItem item = new JMenuItem("Add");      
	   ActionListener listener = new AddItemListener();
	   item.addActionListener(listener);
	   return item;
   }//createAdd
   
   public JMenuItem createRemoveItem()
   {
      class RemoveItemListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {  
           tc.remove();
           repaint();
         }
      }
      
      JMenuItem item = new JMenuItem("Remove");      
      ActionListener listener = new RemoveItemListener();
      item.addActionListener(listener);
      return item;
    }//createRemove
   
   
    public JMenuItem createAddFirstItem()
    {
      class AddFirstItemListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {
           tc.addFirst();
           repaint();
         }
      } 
      
      JMenuItem item = new JMenuItem("AddFirst");      
      ActionListener listener = new AddFirstItemListener();
      item.addActionListener(listener);
      return item;
    }//createAddFirst
    

    public JMenuItem createAddLastItem()
    {
      class AddLastItemListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {
           tc.addLast();
           repaint();           
         }
      }   
      
      JMenuItem item = new JMenuItem("AddLast");      
      ActionListener listener = new AddLastItemListener();
      item.addActionListener(listener);
      return item;
    }//createAddLast
    
    
    public JMenuItem createRemoveFirstItem()
   {
      class RemoveFirstItemListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {
        	 tc.removeFirst();
        	 repaint();
         }
      }   
      
      JMenuItem item = new JMenuItem("RemoveFirst");      
      ActionListener listener = new RemoveFirstItemListener();
      item.addActionListener(listener);
      return item;
    }//createRemoveFirst
    

    public JMenuItem createRemoveLastItem()
   {
      class RemoveLastItemListener implements ActionListener
      {
         public void actionPerformed(ActionEvent event)
         {
             tc.removeLast();
             repaint();
         }
      }   
      
      JMenuItem item = new JMenuItem("RemoveLast");      
      ActionListener listener = new RemoveLastItemListener();
      item.addActionListener(listener);
      return item;
    }//createRemoveLast

}