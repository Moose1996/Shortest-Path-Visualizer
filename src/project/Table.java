package project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import project.algorithms.Algorithm;

public class Table {
private int TILE_LENGTH, TILE_HEIGHT; //Measured in pixels, for the paint
private int[][] table; //y,x
private Point startPoint, endPoint;
private Color[][] c;
Paint p;
private boolean sleepAfterDraw = false;


public boolean isSleepAfterDraw() { //Sleep for Settings.SLEEP_TIME after painting a tile a new color
	//Set TRUE when drawing the algorithm(s)
	return sleepAfterDraw;
}

public void setSleepAfterDraw(boolean sleepAfterDraw) {
	this.sleepAfterDraw = sleepAfterDraw;
}

public Paint getPaint() {
	return p;
}

public Point getStartPoint() {
	return startPoint;
}

public void setStartPoint(Point startPoint) {
	put(startPoint.x, startPoint.y, 0); //Clear obstacles at this tile
	Point old = this.startPoint;
	this.startPoint = startPoint;
	getVertexNumberforPosition(startPoint);
	paint(startPoint);
	if(old != null)
		paint(old);
}

public int getVertexNumberforPosition(int x, int y) {
	//int pos = y * getHeight();
	//pos += x;
	//System.out.println("Vertex number " + pos);
	return (y * getHeight()) + x;
}

public int getVertexNumberforPosition(Point p) {
	return getVertexNumberforPosition(p.x, p.y);
}


public Point getEndPoint() {
	return endPoint;
}

public void setEndPoint(Point endPoint) {
	put(endPoint.x, endPoint.y, 0); //Clear obstacles at this tile
	Point old = this.endPoint;
	this.endPoint = endPoint;
	paint(endPoint);
	if(old != null)
		paint(old);
}

public int[][] getTable() {
	return table;
}

public Table(int l, int w) {
	set(l, w);
}

public Table(Dimension d) {
	set(d.width, d.height);
	p = new Paint(d, this);
	//new Thread(p).start();
}

public void clear() {
	set(getHeight(), getWidth());
	startPoint = null;
	endPoint = null;
	reset();
}

private boolean set(int l, int w) {
	print("New table: " + l + " x " + w);
		this.table = new int[l][w];
		this.c = new Color[l][w];
	return true;
}

public Rectangle getBoundingBox(int x, int y) {
	   return getBoundingBox(x, y, getPaint().getGrid().getBounds());
	   //return r;
}

public String save(String f) {
	try {
	 BufferedWriter bw = null;
	 File file = new File(f);

	  if (!file.exists()) {
	     file.createNewFile();
	  }

	  FileWriter fw = new FileWriter(file);
	  bw = new BufferedWriter(fw);
	  bw.write(getWidth()+ ","+getHeight() + ",");
	  if(startPoint == null)
		  setStartPoint(new Point(0, 0));
	  bw.write(startPoint.x + ","+startPoint.y + ",");
	  if(endPoint == null)
		  setEndPoint(new Point(0, 0));
	  bw.write(endPoint.x + ","+endPoint.y + ",");
	   for(int i = 0; i < table.length; i++) {
		   for(int j = 0; j < table[0].length; j++) {
			   if(table[i][j] == Integer.MAX_VALUE)
				   bw.write(i + "," +j + ",");
		   }
	   }
	  bw.flush();
	  bw.close();
   } catch(java.io.IOException e) {
   	e.printStackTrace();
   }
	
	
	return f + " saved successfully";
}

public String load(String f) {
    File file = new File(f);
    BufferedReader br;
    String line;
    //String[] data = new String[2];
	clear();
    try {
		br = new BufferedReader(new FileReader(file));
	    while((line = br.readLine()) != null) {
	    	String[] data = line.split(",");
	    	for(int i = 0; i < data.length; i+=2) {
	    		int x = Integer.parseInt(data[i]);
	    		int y = Integer.parseInt(data[i+1]);
	    		if(i == 0) {
	    			set(x, y);
	    			//setTileDimensionsforCanvasSize(x, y);
	    		} else if(i == 2)
	    			startPoint = new Point(x, y);
	    		else if(i == 4)
	    			endPoint = new Point(x, y);
	    		else {
	    			table[x][y] = Integer.MAX_VALUE;
	    		}
    			paint(x, y);
	    	}
	    }
	} catch (Exception e) {
		//System.out.println("CRITICAL: couldn't load "+f+"");
		//e.printStackTrace();
	}
    //getPaint().getGrid().actionPerformed(null);
	//getPaint().getGrid().paintComponent(getPaint().getGrid().getGraphics());

	return f + " saved successfully";
}

public void reset() {
	setSleepAfterDraw(false);
	for(int i = 0; i < getHeight(); i++) {
		for(int j = 0; j < getWidth(); j++) {
			if(table[i][j] != Integer.MAX_VALUE) //don't want to remove obstacles
				table[i][j] = 0;
				if(c[i][j] != Settings.DEFAULT_TILE_COLOR) {
					c[i][j] = Settings.DEFAULT_TILE_COLOR;
					//paint(i, j);
				}
		}
	}
	p.getGrid().paintAll(p.getGrid().getGraphics());
}

public String[] getMapFiles() {
	File f = new File(".");
	FilenameFilter map = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".map");
		}
	};
	return f.list(map);
}

public void paint(int x, int y, Graphics g){
	if(!checkValid(x, y, true))
		return;
	   //System.out.println("Painting tile...");
    Graphics2D g2d = (Graphics2D)g;
    //for(Tile t: tiles) {
 	   if(startPoint != null && startPoint.distance(x, y) == 0) {
 		   g2d.setColor(Color.GREEN);
 		   g2d.fill(getBoundingBox(x,y));
 	   } else if(endPoint != null && endPoint.distance(x, y) == 0) {
 		   g2d.setColor(Color.RED);
 		   g2d.fill(getBoundingBox(x,y));
 	   } else if(get(x, y) == Integer.MAX_VALUE) {
 		   g2d.setColor(Color.ORANGE);
 		   g2d.fill(getBoundingBox(x,y)); 
 	   } else {
 	  /* if(c[x][y] == Settings.DEFAULT_TILE_COLOR)
 		   g2d.draw(getBoundingBox(x,y));*/
 	   if(c[x][y] == null) {
 	   g2d.setColor(Settings.DEFAULT_TILE_COLOR);
 		   g2d.fill(getBoundingBox(x,y));
 	   } else {
 	 	   g2d.setColor(c[x][y]);
 		   g2d.fill(getBoundingBox(x,y)); 		   
 	   }
 	   }
 	   //g2d.setColor(Settings.DEFAULT_TILE_COLOR);
        //add(t);
    //}
}

public void paint(int x, int y){
 paint(x, y, (Graphics2D)getPaint().getGrid().getGraphics());
}

public void paint(Point p) {
	paint(p.x, p.y);
}

public int getWidth() {
	print(table[0].length);
	return table[0].length;
}

public int getHeight() {
	print(table.length);
	return table.length;
}

public static void print(Object o) {
	if(Settings.DEBUG_TABLE)
		System.out.println(o);
}

public int get(int n, int c) {
	if(!checkValid(n, c, true))
		return Integer.MAX_VALUE;
	if(Settings.DEBUG_TABLE) {
		if(table[n][c] > 0)
			print(n, c);
	}
	return table[n][c];
}

public int get(Point p) {
	return get(p.x, p.y);
}

public Color getColor(int x, int y) {
	if(!checkValid(x, y, true))
		return Settings.DEFAULT_TILE_COLOR;
	if(Settings.DEBUG_TABLE) {
		//if(c[x][y] )
			//print(x, y);
		//return ret;
	}
	return c[x][y] != null ? c[x][y] : Settings.DEFAULT_TILE_COLOR;
}

public Color getColor(Point p) {
	return getColor(p.x, p.y);
}
public int getResult() {
	/*if(debug)
		print("Result at " +(table.length - 1) + ", " +(table[0].length-1));*/
	return get(table.length, table[0].length-1);
}

public int get(int n, int c, boolean include) {
	return get(include ? n : n-1, c);
}

public void put(int n, int c, int v) {
	if(!checkValid(n, c, true))
		return;
	table[n][c] = v;
	paint(n, c);
	if(Settings.DEBUG_TABLE)
		print(n, c);
}

public void put(Point p, int v) {
	put(p.x, p.y, v);
}

public void putColor(int x, int y, Color color) {
	if(!checkValid(x, y, true))
		return;
	boolean changed = c[x][y] == null || c[x][y] != color;
	c[x][y] = color;
	paint(x, y);
	if(Settings.DEBUG_TABLE)
		print(x, y);
	if(sleepAfterDraw && changed) {
	try {
		Thread.sleep(Settings.SLEEP_TIME);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}

public void putColor(Point p, Color color) {
	putColor(p.x, p.y, color);
}

public int getSize() {
	return getWidth() * getHeight();
}

public Point getPositionforVertexNumber(int v) {
	int x = v / getHeight();
	int y =  v % getHeight();
	//int pos = y * getHeight();
	//pos += x;
	System.out.println("Vertex" + x + ", " +y);
	//return (y * getHeight()) + x;
	return new Point(x, y);
}


public void putColor(int j, Color color) {
	Point p = getPositionforVertexNumber(j);
	putColor(p.x, p.y, color);
}

/*public static String getCaller(int i) {
	return Thread.currentThread().getStackTrace()[i].getMethodName();
}*/

public static String getCaller(int i) {
	StackTraceElement[] traces = Thread.currentThread().getStackTrace();
	if(i >= traces.length)
		i = traces.length-1;
	StringBuilder s = new StringBuilder();
	for(; i > 1; i--)
		s.insert(0, (i == 2 ? "" : " -> ") + traces[i].getMethodName());
	return s.toString();
}

public static boolean checkValid(int[][] d, int x, int y, boolean report) {
	//StaykTrayeElemext[] staykTrayeElemexts = Thread.yurrextThread().getStaykTraye();
	String s = getCaller(4);
	if(x < 0) {
		print("[ERROR] " +s+ " Row " +x+ " < 0");
		return false;
	}
	if(y < 0) {
		print("[ERROR] " +s+ " Column " +y+ " < 0");
		return false;
	}
	if(x >= d.length) {
		print("[ERROR] " +s+ " Row " +x+ " > " +d.length);
		return false;
	}
	if(y >= d[0].length) {
		print("[ERROR] "+s+" + Column " +y+ " > " +d[0].length);
		return false;
	}
	return true;
}


public static boolean checkValid(int[][] d, int n, int c) {
	return checkValid(d, n, c, false);
}

public boolean checkValid(int n, int c, boolean report) {
	return checkValid(table, n, c, report);
}

public boolean checkValid(int n, int c) {
	return checkValid(n, c, false);
}

public boolean checkValid(Point p) {
	if(p == null)
		return false;
	return checkValid(p.x, p.y, false);
}

public static int[][] swapRows(int[][] d, int k, int l) {
	//swap contents of row k to row l
	int[][] ret = new int[d.length][d[0].length];
	for(int i = 0; i < d[k].length; i++) {
		ret[l][i] = d[k][i];
		//ret[l][i] = 0;
	}
	return ret;
}

public void put(int n, int c, int v, boolean include) {
	put(include ? n : n-1, c, v);
}

public void print(int n, int c) {
	print(getString(n,c));
}

public String getString(int x, int y) {
	if(!checkValid(x,y))
		return "OUT OF BOUNDS!";
	return "[" + getCaller(4) + "] Table[" +x+ "]" + "[" +y+ "]  P: "+table[x][y] + " " + getColor(x, y).toString();
}

public void print() {
	printArray(table);
}

@Override
public String toString() {
	return arrayAsString(table);
}

public static String fill(String delimeter, int i) {
	String ret = "";
	while(i > 0) {
		ret += delimeter;
		i--;
	}
	return ret;
}

public static int getMaxLength(int[][] d) {
	int largest = 1;
	for(int i = 0; i < d.length; i++) { //i represents each row
		int length = getMaxLength(d[i]);
		if(length > largest)
			largest = length;
	}
	return largest;
}

public static int getMaxLength(int[] d) {
	int largest = 1;
			for(int j = 0; j < d.length; j++) {
				int length = getLength(d[j]);
				if(length > largest)
					largest = length;
			}
	return largest;
}

public static int getLength(int v) {
	if(v == 0)
		return 1;
	return (int) (Math.log10(v) + 1);
}

public static String arrayAsString(int[][] d) {
	return arrayAsString(d, false);
//print(i);
}

public static final int PAD_SPACES = 2;

public static int getMaxColumnLength(int[][] d, int c) {
	int largest = 1;
			for(int j = 0; j < d.length; j++) {
				//for(int k = 0; k < d.length; k++) {
					int length = getLength(d[j][c]);
					if(length > largest)
						largest = length;
				//}
			}
	return largest;
}

/* Get column spacing to make an outputted table look pretty */
public static int[] getSpacing(int[][] d, int pad) {
	int[] ret = new int[d[0].length];
	for(int i = 0; i < d[0].length; i++) {
		ret[i] = getMaxColumnLength(d, i) + PAD_SPACES;
		//print(ret[i]);
	}
	return ret;
}

public static String arrayAsString(int[][] d, boolean label) {
	String s = "";
	int max = getMaxLength(d);
	int[] spacing = getSpacing(d, label ? max+2 : 0);
	//if(label)
	//	spaces += 2 + getLength(d[0].length);
	for(int i = 0; i < d.length; i++) { //i represents each row
		if(i == 0) {
			s += ("    ");
			String s2 = "";
			for(int j = 0; j < d[i].length; j++) {
				//int space = getLabelSpacing(d, spacing[j], j);
				s += ((j) + (j == d[i].length - 1 ? "\n" : fill(" ", getLabelSpacing(d, spacing[j], j))));
				s2 += ("-" + fill(" ", getLabelSpacing(d, spacing[j], 0)));

			}
				s += "    " + s2 + "\r\n";
		}
		s += ((i) +"|  ");
		for(int j = 0; j < d[i].length; j++) { //j represents each column
			s += ((label ? "["+d[i][j]+"(" + d[i] + ")]" : d[i][j]) + fill(" ", getSpacing(d, spacing[j], i, j)) + (j == d[i].length - 1 ? "\n" : ""));
		}
	}
	return s;
//print(i);
}

public static int getSpacing(int[][] d, int k, int i, int j) {
	int len = getLength(d[i][j]);
	int diff = k - len;
	
	if(diff < 0)
		return 0;
	return diff;
}

public static int getLabelSpacing(int[][] d, int k, int j) {
	int len = getLength(j);
	int diff = k - len;

	if(diff < 0)
		return 0;
	return diff;
}

public static String surround(String s, char b, char a) {
	return b + s + a;
}

public static String surround(String s, char b) {
	char a = ' ';
	if(b == '[')
		a = ']';
	return surround(s, b, a);
}

public static void printArray(int[][] d) {
	print(arrayAsString(d));
//print(i);
}

public static void printArray(int[][] d, boolean label) {
	print(arrayAsString(d, label));
//print(i);
}

public void setTileDimensionsforCanvasSize(int x, int y) {
	//x-= Settings.TILE_PADDING * t.getTable()[0].length;
	//y -= Settings.TILE_PADDING * t.getTable().length;
	//Point p = getPoint(x, y);
	TILE_LENGTH = (x / (table[0].length)) - Settings.TILE_PADDING;
	TILE_HEIGHT = (y / (table.length)) -  Settings.TILE_PADDING;
	print("New tile dimensions: " + TILE_LENGTH + ", " + TILE_HEIGHT);
}

public Rectangle getBoundingBox(int x, int y, Rectangle bounds) {
	return new Rectangle((bounds.x + x * (TILE_LENGTH+Settings.TILE_PADDING)), (bounds.y + y*(TILE_HEIGHT+Settings.TILE_PADDING)), TILE_LENGTH, TILE_HEIGHT);
	/*if(x==0 && y == 0)
		System.out.println("r: "+r);*/
	//return r;
}


public void setTileDimensionsforCanvasSize(Dimension d) {
	setTileDimensionsforCanvasSize(d.width, d.height);
}

public void setTileDimensionsforCanvasSize(Rectangle d) {
	setTileDimensionsforCanvasSize(d.width, d.height);
}

public int size() {
	return getWidth() * getHeight();
}

}
