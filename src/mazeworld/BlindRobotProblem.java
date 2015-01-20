package mazeworld;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Find a path for a single agent to get from a start location (xStart, yStart)
//  to a goal location (xGoal, yGoal)

public class BlindRobotProblem extends InformedSearchProblem {

	private static int actions[][] = { Maze.NORTH, Maze.EAST, Maze.SOUTH,
			Maze.WEST };

	Set<List<Integer>> start = new HashSet<List<Integer>>();
	
	Set<List<Integer>> goal = new HashSet<List<Integer>>();

	private MultiMaze maze;

		public BlindRobotProblem(MultiMaze m, Set<List<Integer>> sxy, Set<List<Integer>> gxy) {
			startNode = new BlindMazeNode(sxy, 0, null);
			start=sxy;
			goal = gxy;
		    maze = m;
		}

	// node class used by searches. Searches themselves are implemented
	// in SearchProblem.
	public class BlindMazeNode implements SearchNode {

		// location of the agent in the maze
		Set<List<Integer>> stateSet = new HashSet<List<Integer>>();

		// how far the current node is from the start. Not strictly required
		// for uninformed search, but useful information for debugging,
		// and for comparing paths
		private double cost;

		
		private BlindMazeNode parent;

		

		public BlindMazeNode(Set<List<Integer>> sxy, double c, BlindMazeNode pre) {
			stateSet = sxy;
			parent = pre;
			cost = c;

		}

		public int getX(int robot) {
			return 0;
		}

		public int getY(int robot) {
			return 0;
		}

		public SearchNode getParent() {
			return parent;
		}

		public ArrayList<SearchNode> getSuccessors() {
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
			for (int[] action : actions) {
				Set<List<Integer>> newSet = new HashSet<List<Integer>>();
				for (List<Integer> state : stateSet){	
					int newX = state.get(0)+action[0];
					int newY = state.get(1)+action[1];
					if (maze.isBlindLegal(newX, newY)) {
						System.out.println("legal successor found " + " " + newX + " " + newY);
						List<Integer> xy = new ArrayList<Integer>();
						xy.add(newX);
						xy.add(newY);
						newSet.add(xy);
					}
					else{
					newSet.add(state);
					}
				}		
				SearchNode succ = new BlindMazeNode(newSet, getCost() + 1.0,this);
				successors.add(succ);	
			}
			return successors;
		}

		@Override
		public boolean goalTest() {
			if(stateSet.equals(goal))
				return true;
			else
				return false;
		}

		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return stateSet.equals(((BlindMazeNode) other).stateSet);
		}

		@Override
		public int hashCode() {
			return stateSet.hashCode();
		}

		/*@Override
		public String toString() {
			return new String("Maze state " + state[0] + ", " + state[1] + " "
					+ " depth " + getCost());
		}
		*/

		@Override
		public double getCost() {
			return cost;
		}

		@Override
		public double heuristic() {
			// manhattan distance metric for simple maze with one agent:

			double dx = 0;
			double dy = 0;

			if (stateSet.size() == 1) {
				dx = Math.abs(goal.iterator().next().get(0) - stateSet.iterator().next().get(0));
				dx = Math.abs(goal.iterator().next().get(1) - stateSet.iterator().next().get(1));
				return (dx + dy) / 10000.0; // arbitrarily high number
			}

			// otherwise still reducing number of belief states
			return (stateSet.size() * 10000);

		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}

		@Override
		public double priority() {
			return heuristic() + getCost();
		}

	}

}
