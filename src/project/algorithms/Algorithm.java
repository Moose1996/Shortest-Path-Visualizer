package project.algorithms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import project.Settings;
import project.Table;

public abstract class Algorithm implements Runnable {
	public enum Direction {
		EAST,
		NORTH,
		SOUTH,
		WEST,
		NORTHWEST,
		NORTHEAST,
		SOUTHWEST,
		SOUTHEAST,
		NULL;
	}
	
	public Point pointForDirection(Point p, Direction d) { //Returns the x,y pair at direction d of the x,y pair p
		if(d == Direction.EAST)
			return new Point(p.x + 1, p.y);
		if(d == Direction.SOUTH)
			return new Point(p.x, p.y - 1);
		if(d == Direction.NORTH)
			return new Point(p.x, p.y + 1);
		if(d == Direction.WEST)
			return new Point(p.x - 1, p.y);
		if(Settings.ALLOW_DIAGONAL) {
			if(d == Direction.SOUTHEAST)
				return new Point(p.x + 1, p.y - 1);		
			if(d == Direction.SOUTHWEST)
				return new Point(p.x - 1, p.y - 1);
			if(d == Direction.NORTHEAST)
				return new Point(p.x + 1, p.y + 1);
			if(d == Direction.NORTHWEST)
				return new Point(p.x - 1, p.y + 1);	
		}
		return new Point(-1, -1);
	}
	
	public Point getNextTileinDirection(Point p, Direction d) {
		while(t.checkValid(p) && !isTransversable(p)) {
			
		}
		return p;
	}
	
	public Point pointForDirection(Direction d) { //Returns the x,y pair at direction d relative to the current location
		return pointForDirection(curr, d);
	}
	
	public boolean move(Point p) {
		if(!p.equals(curr) && !p.equals(last) && isTransversable(p)) {
			t.putColor(curr, Settings.DEFAULT_TILE_COLOR);
			print("Moved: " +curr+ " -> " +p);
			last = curr;
			curr = p;
			t.putColor(curr, Color.BLUE);
			return true;
		}
		return false;
	}
	
	public boolean isTransversable(Point p) {
		return t.get(p) != Integer.MAX_VALUE;
	}

	public enum Heuristic {
		COST_FROM_START_POINT, //Djikstra's
		COST_FROM_START_AND_END, //Primitive A*
		EUCLIDEAN, //(Probably) best A*
		MANHATTAN,
		DIAGONAL,
		BFS,
		DFS;
	}


	/**
	 * @Method getSurroundingArea
	 * @param p the point to get the tiles around
	 * @param dist the radius from point p
	 * @param diagonal count diagonal tiles
	 * @return
	 */
	public ArrayList<Point> getSurroundingArea(Point p, int dist) {
		ArrayList<Point> ret = new ArrayList<Point>();
			if(Settings.ALLOW_DIAGONAL) { //Count diagonal tiles
				for(int i = -1*(dist); i <= (dist); i++) {
					if(i == 0)
						continue;
					ret.add(new Point(p.x-i, p.y+i));	
					ret.add(new Point(p.x+i, p.y-i));		
				}
			}
			for(int j = -1*dist; j <= dist; j++) {
				if(j == 0)
					continue;
				ret.add(new Point(p.x, p.y+j));
			}
			for(int k = -1*dist; k <= dist; k++) {
				if(k == 0)
					continue;
				ret.add(new Point(p.x+k, p.y));
			}
		ret.removeIf(pt -> !t.checkValid(pt)); //Kill the invalid vertices before they get to us
		return ret;
	}
	
	public ArrayList<Point> getSurroundingArea(int dist) { //Points at radius dist from current location
		return getSurroundingArea(curr, dist);
	}
	
	public int getVertexCostforHeuristic(Point to, Heuristic h) { //Get the cost to travel to some vertex
		if(t.get(to) == Integer.MAX_VALUE) //The grid location has an obstacle or is invalid
			return Integer.MAX_VALUE;
		//int ret = 0;
		else if(h == Heuristic.COST_FROM_START_POINT) { //Djikstra's (g)
			return (int)Math.round(t.getStartPoint().distance(to));
		}
		else if(h == Heuristic.COST_FROM_START_AND_END) {// A* (h)
			return (int)Math.round(t.getEndPoint().distance(to)) + getVertexCostforHeuristic(to, Heuristic.COST_FROM_START_POINT);
		}
		//to = t.getEndPoint();
		else if(h == Heuristic.EUCLIDEAN) {
			return (int)(Math.sqrt(2*(to.x - t.getEndPoint().x) + (2*(to.y - t.getEndPoint().y)))) + getVertexCostforHeuristic(to, Heuristic.COST_FROM_START_POINT);
		}
		else if(h == Heuristic.DIAGONAL) {
			return Integer.max(Math.abs(to.x - t.getEndPoint().x), Math.abs(to.y - t.getEndPoint().y)) + getVertexCostforHeuristic(to, Heuristic.COST_FROM_START_POINT);
		}
		else if(h == Heuristic.MANHATTAN) {
			return Math.abs(to.x - t.getEndPoint().x) + Math.abs(to.y - t.getEndPoint().y)+ getVertexCostforHeuristic(to, Heuristic.COST_FROM_START_POINT);
		}
		return Integer.MAX_VALUE;
	}
	
	public Direction getBest(Point p, Heuristic h) { //Returns a cardinal direction representing the most promising path to traverse
		int min = Integer.MAX_VALUE;
		Direction best = Direction.NULL;
			for(int i = 0; i < Direction.values().length-1; i++) { //Length-1 because we don't use NULL
				int val = getVertexCostforHeuristic(pointForDirection(p, Direction.values()[i]), h);
				if(val < min || (val <= min && Direction.values()[i].equals(Settings.TIEBREAKER_DIRECTION))) {
					min = val;
					best = Direction.values()[i];
				}
			}
		//}
		return best;
	}
	
	public Point getBest(ArrayList<Point> p, Heuristic h) {
		Collections.sort(p, (o, o2) -> Integer.compare(getVertexCostforHeuristic(o, h), (getVertexCostforHeuristic(o2, h))));
		if(h == Heuristic.DFS)
			Collections.reverse(p);
		return p.get(0);
	}

	
	public Direction getBest(Heuristic h) {
		return getBest(curr, h);
	}	
	
	public void print(Object o) {
		System.out.println(o);
	}
	
	public int worstCaseRuntime() { //Runtime(ms) if every tile is drawn on
		return (int) (t.size() * Settings.SLEEP_TIME);
	}
	
	public Algorithm(Table t, Heuristic h) {
		this.t = t;
		if(t.getPaint().running != null)
			t.reset();
		t.setSleepAfterDraw(true);
		//print("Predicted runtime: " +worstCaseRuntime());
		if((worstCaseRuntime()/1000) > Settings.DESIRED_RUNTIME) { //It'll take too long to draw this so let's speed it up
			long newSleepTime = (long) (((double)Settings.DESIRED_RUNTIME / (double)t.size()) * 1000);
			if(newSleepTime <= 0) //Because long...
				newSleepTime = 1;
				Settings.SLEEP_TIME = newSleepTime;
				print("Set paint delay:" +Settings.SLEEP_TIME);
		}
		this.h = h;
		this.curr = t.getStartPoint();
		this.last = curr;
	}
	
	public void run() {
		int verticesConsidered = solve();
		t.setSleepAfterDraw(false);
		System.out.println(verticesConsidered);
		//t.getPaint().getGrid().getGraphics().drawString("Vertices explored: "+verticesConsidered+"", 0, 0);
		//t.getPaint().getGraphics().drawString("Vertices explored: "+verticesConsidered+"", 20, 20);
		t.getPaint().setTitle(t.getPaint().getMapName() + ": " +verticesConsidered + " vertices explored");
	}

	/**
	 * @method solve
	 */
	public abstract int solve();
	
	public Point curr; //Current "location" on the grid -> point.x, point.y
	private Point last; //The location before we got to current location -- prevent back-and-forth endless loop
	public Table t;
	public Heuristic h;
	public boolean kill = false; //Set true to kill the thread mid-algorithm
}

