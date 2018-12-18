package project;
import java.awt.*; 

import java.awt.event.*; 
import java.util.ArrayList;

import javax.swing.*;

import project.algorithms.AStar;
import project.algorithms.Algorithm;
import project.algorithms.Algorithm.Heuristic;
 

public class Paint extends JFrame implements Runnable, ActionListener {
	Table t;
	Dimension size;
	private TileGrid tg;
	JMenuItem load, save, reset, clear, run; //Drop-down menu buttons
	JCheckBox allowDiagonal;
	JMenuItem[] algorithms;
	public Algorithm running;
	String name;
	
	public String getMapName() {
		return name;
	}

   public Paint(Dimension d, Table t)  {
	   	  this.t = t;
	   	  name = "New map";
	   	  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		  Container cp = getContentPane();
		  cp.setBackground(Color.BLACK);
		  tg = new TileGrid();
		  
		  /** The top menu bar*/
		  JMenuBar menuBar = new JMenuBar();
		  
		  /** The "File" menu*/
		  JMenu menu = new JMenu("File");
		  JMenu algorithmMenu = new JMenu("Algorithms");

		  menu.setMnemonic(KeyEvent.VK_A);

		/** New */
		   reset = new JMenuItem("New",
	                  KeyEvent.VK_T);
			reset.setAccelerator(KeyStroke.getKeyStroke(
			 KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			menu.add(reset);
			reset.addActionListener(this);
		/** Clear(reset) */
		   clear = new JMenuItem("Clear",
	                  KeyEvent.VK_T);
			clear.setAccelerator(KeyStroke.getKeyStroke(
			 KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			menu.add(clear);
			clear.addActionListener(this);	
		/** Save */
		   save = new JMenuItem("Save",
	                  KeyEvent.VK_T);
			save.setAccelerator(KeyStroke.getKeyStroke(
			 KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menu.add(save);		
			save.addActionListener(this);
			  /** Load... */
			   load = new JMenuItem("Load",
	                  KeyEvent.VK_T);
			load.setAccelerator(KeyStroke.getKeyStroke(
			 KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			load.addActionListener(this);
			menu.add(load);	
			
			menuBar.add(menu);
			/** End "File" menu */
			
			/** Run... */
			   /*run = new JMenuItem("Run",
		                  KeyEvent.VK_T);
				run.setAccelerator(KeyStroke.getKeyStroke(
				 KeyEvent.VK_R, ActionEvent.CTRL_MASK));
				run.addActionListener(this);*/
		   	    algorithms = new JMenuItem[Settings.ALGORITHM_NAMES.length];
				for(int i = 0; i < Settings.ALGORITHM_NAMES.length; i++) {
					algorithms[i] = new JMenuItem(Settings.ALGORITHM_NAMES[i]);
					algorithms[i].addActionListener(this);
					algorithmMenu.add(algorithms[i]);			
				}
			  /** Allow diagonal traversal... */
			   allowDiagonal = new JCheckBox("Diagonal");
			   allowDiagonal.setMnemonic(KeyEvent.VK_D);
			   allowDiagonal.addActionListener(this);
			   algorithmMenu.add(allowDiagonal);
		  

		menu.add(load);	
		
		menuBar.add(menu);
		menuBar.add(algorithmMenu);
		  setJMenuBar(menuBar);
		  /** END menu code */
	 		 //add(menuBar);

 		 add(tg);
	      
	      pack();
	      setTitle(name);  // Change to something more interesting later
	      //setLocationRelativeTo(null);
	      setVisible(true);
	      //setResizable(true);

   }
   
   class TileGrid extends JPanel implements MouseListener, ActionListener {
       public TileGrid(){
           setPreferredSize(Settings.DEFAULT_CANVAS_SIZE);
		  // Rectangle size = getBounds();
           addMouseListener(this);
           addComponentListener(new ComponentAdapter() {
	    	    public void componentResized(ComponentEvent componentEvent) {
					   t.setTileDimensionsforCanvasSize(getBounds());
	    	    }
	    	});
       }
       
       public void resize() {
		   t.setTileDimensionsforCanvasSize(getBounds());
       }
       
       public void mouseClicked(MouseEvent e) {
           Point p = e.getPoint();
           //System.out.println(p);
		   for(int i = 0; i < t.getTable().length; i++) {
			   for(int j = 0; j < t.getTable().length; j++) {
				   //Rectangle r = t.getGraphics().getBoundingBox(i, j, getBounds());
				   if(!t.getBoundingBox(i, j).contains(p))
					   continue;
		        		  if(e.getButton()== 1)
			              	  t.setStartPoint(new Point(i,j)); 
		        		  else if(e.getButton()== 3)
			              	  t.setEndPoint(new Point(i,j)); 
				   }
		   }
       }
       
	   /* Draw the tiles */
       public void paintComponent(Graphics g){
    	   //System.out.println("Painting tile grid...");
    	   //System.out.println(Table.getCaller(5));
		   for(int i = 0; i < t.getTable().length; i++) {
			   for(int j = 0; j < t.getTable()[0].length; j++) {
				   t.paint(i, j, g);
			   }
		   }
       }
       Point pressed;
       public void mousePressed(MouseEvent e) {
    	   pressed = e.getPoint(); //click-and-drag functionality for inserting obstacles
    	   ////System.out.println(e);
       }
       public void mouseReleased(MouseEvent e) {
    	   ////System.out.println(e);
    	   int bigX = Integer.max(pressed.x, e.getX());
    	   int bigY = Integer.max(pressed.y, e.getY());
    	   int smallX = Integer.min(pressed.x, e.getX());
    	   int smallY = Integer.min(pressed.y, e.getY());
    	   Rectangle r = new Rectangle(smallX, smallY, bigX-smallX, bigY-smallY);
    	   ////System.out.println(r);
    	   for(int i = 0; i < t.getTable().length; i++) {
    		   for(int j = 0; j < t.getTable().length; j++) {
    			   if(r.intersects(t.getBoundingBox(i, j))) {
    				  if(e.getButton() == 1)
		              	  t.put(i, j, Integer.MAX_VALUE);
    				  else if(e.getButton() == 3)
    					  t.put(i,  j,  0);
		             	  t.paint(i, j);
    			   }
    			   ////System.out.println(r);
    			   //g.drawRect(r.x, r.y, r.width, r.height);
    		   }
    	   }
    	   
       }

       
       public void mouseEntered(MouseEvent e) {

       }
       public void mouseExited(MouseEvent e) {
       }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


   }
 

   
   @Override
   public void run() {
	   while(true) {
		   ////System.out.println("PAINT");
		   //repaint();
		   try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
   }

   
@Override
public void actionPerformed(ActionEvent e) {
   //System.out.println("A: " +e);
   if(e.getActionCommand().equals("Diagonal")) {
	   Settings.ALLOW_DIAGONAL = !Settings.ALLOW_DIAGONAL;
   }else if(e.getActionCommand().equals("New")) {
		//int[] sizes = getPossibleGridSizes(40);
		String[] sizesStrings = getPossibleGridSizes(40);
	    String input = (String) JOptionPane.showInputDialog(null, "Map size",
	            "Select a size", JOptionPane.QUESTION_MESSAGE, null, sizesStrings,  sizesStrings[0]);
	    int size = Integer.parseInt(input.split(" ")[0]);
		dispose();
		t = new Table(new Dimension(size, size));
	}else if(e.getActionCommand().equals("Clear")) { //Clean the map after an algorithm runs
		//Or stop the current algorithm from running
		if(running != null)
			running.kill = true;
		t.reset();
	}else if(e.getActionCommand().equals("Save")) {
		if(name.contains(".map"))
			name = name.split(".map")[0];
		name = JOptionPane.showInputDialog(null, "Map name", "Enter a name",
				 JOptionPane.QUESTION_MESSAGE, null, null, name).toString() + ".map";
		t.save(name);
	    setTitle(name);
	}else if(e.getActionCommand().equals("Load")) {
		String[] files = t.getMapFiles();
		/*if(files[0].contains(".map"))
			files[0] = files[0].split(".map")[0];*/
	    name = (String) JOptionPane.showInputDialog(null, "Map name",
	            "Choose a map", JOptionPane.QUESTION_MESSAGE, null, files,  files[0]);
	    t.load(name);
	    setTitle(name);
	    /*super.setSize(Settings.DEFAULT_CANVAS_SIZE);
	    t.setTileDimensionsforCanvasSize(tg.getBounds());
		tg.paintAll(tg.getGraphics());*/
	}else {
			switch(e.getActionCommand()) {
		/* Run the algorithms on a separate thread or otherwise the GUI locks up*/
				case "BFS":
					running = new AStar(t, Heuristic.BFS);
					new Thread(running).start();
					break;
				case "DFS":
					running = new AStar(t, Heuristic.DFS);
					new Thread(running).start();
					break;
				case "Dijkstra's":
					running = new AStar(t, Heuristic.DIAGONAL);
					new Thread(running).start();
					break;
				case "A*":
					running = new AStar(t, Heuristic.COST_FROM_START_AND_END);
					new Thread(running).start();
					break;
				case "A*-EUCLIDEAN":
					running = new AStar(t, Heuristic.EUCLIDEAN);
					new Thread(running).start();
					break;
				case "A*-MANHATTAN":
					running = new AStar(t, Heuristic.MANHATTAN);
					new Thread(running).start();
					break;
				case "A*-DIAGONAL":
					running = new AStar(t, Heuristic.DIAGONAL);
					new Thread(running).start();
					break;
		}
	}
}

public TileGrid getGrid() {
	return tg;
}

public String[] getPossibleGridSizes(int i) {
	String[] ret = new String[i];
	for(int j = 1; j <= (i); j++) {
		ret[j-1] = (""+j*5+" x "+j*5+"");
		//System.out.println(ret[j]);
	}
	return ret;
}

}