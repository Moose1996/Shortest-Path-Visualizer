package project;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import project.algorithms.Algorithm;
import project.algorithms.Algorithm.Direction;

public class Settings {
	
/** Table(coordinate system) settings **/
public static Dimension TABLE_SIZE = new Dimension(30,30); //h*w
public static Direction TIEBREAKER_DIRECTION = Algorithm.Direction.EAST;
public static boolean ALLOW_DIAGONAL = false;
public static final String[] ALGORITHM_NAMES = {"BFS", "DFS", "Dijkstra's", "A*", "A*-EUCLIDEAN", "A*-MANHATTAN", "A*-DIAGONAL"}; //Algorithms are started in Paint.java

/** Canvas (Graphics) Settings **/
public static int TILE_PADDING = 1; //The "space" between the tiles
public static Dimension DEFAULT_CANVAS_SIZE = new Dimension(600, 600);
public static Color DEFAULT_TILE_COLOR = Color.BLACK;
public static Color EXPLORED_TILE_COLOR = Color.BLUE;
public static Color CURRENT_TILE_COLOR = Color.CYAN;
public static long SLEEP_TIME = 1; //ms, time to wait between drawing each tile
public static int DESIRED_RUNTIME = 10; //seconds, the max amount of time we want to spend drawing any given algorithm

/** Debugging **/
public static boolean DEBUG_TABLE = false;
public static boolean DEBUG_PAINT = false;
}
