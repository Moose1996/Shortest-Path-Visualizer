package project.algorithms;

import java.util.*;
import java.util.Map.Entry;

import project.Table;
import project.algorithms.Algorithm.Heuristic;
import project.Settings;

import java.lang.*;
import java.awt.Color;
import java.awt.Point;
import java.io.*;

public class AStar extends Algorithm {

	public AStar(Table t, Heuristic h) {
		super(t, h); //Algorithm class's constructor holds an instance of table amongst other things
		open = new ArrayList<Point>();
		addOpen(curr);
		closed = new boolean[t.getHeight()][t.getWidth()];
	}

	/**
	 * @method solve
	 * @return the distance in tiles from start -> end(non-inclusive)
	 */
	@Override
	public int solve() {
		Point prospect = null;
		while(!open.isEmpty() && !kill) {
			//if(prospect != null)
			//	t.putColor(prospect, Color.CYAN);
			prospect = getBest(open, h); //the most promising node in the 'open' list according to our heuristic
			if(prospect.equals(t.getEndPoint())) {
				print("Found solution!~");
				return parents.size();
			}
			t.putColor(prospect, Settings.CURRENT_TILE_COLOR);
			open.remove(prospect);
			setClosed(prospect);
			
			for(Point p: getSurroundingArea(prospect, 1)) { //The area within 1 tile of prospect
				t.putColor(p, Settings.EXPLORED_TILE_COLOR);
				if(p.equals(t.getEndPoint())) {
					print("Found solution!~");
					return parents.size();
				}
				//print(p);
				int cost = getVertexCostforHeuristic(p, h);
				if(!isClosed(p)) { //Not closed/blocked
					if(!isOpen(p) || cost < t.get(p)) { //Haven't explored around this vertex yet, let's invest in that...
						addOpen(p);
						parents.put(p, prospect); //Parent of p is the current tile
						if(cost != Integer.MAX_VALUE)
							t.put(p, cost);
					}
				}
				
			}
		}
		return -1;
	}
	

	
	public void getPath() {
		Point p = parents.get(t.getEndPoint());
		System.out.println(p);		
		/*for(Entry<Point, Point> p: parents.entrySet()) {
			System.out.println(p);
		}*/
	}
	
	public void setClosed(Point p) {
		if(!t.checkValid(p))
			return;
		closed[p.x][p.y] = true;
	}
	
	public boolean addOpen(Point p) {
		if(!t.checkValid(p))
			return false;
		return open.add(p);
	}
	
	public boolean isOpen(Point p) {
		return open.contains(p);
	}
	
	public boolean isClosed(Point p) {
		if(!t.checkValid(p))
			return false;
		return closed[p.x][p.y] || t.get(p) == Integer.MAX_VALUE;
	}

	ArrayList<Point> open;
	boolean[][] closed;
	HashMap<Point, Point> parents = new HashMap<>();

}