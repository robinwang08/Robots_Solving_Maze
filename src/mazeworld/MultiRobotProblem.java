package mazeworld;

import java.util.ArrayList;
import java.util.Arrays;

// Find a path for a single agent to get from a start location (xStart, yStart)
//  to a goal location (xGoal, yGoal)

public class MultiRobotProblem extends InformedSearchProblem {

	private static int actions[][] = {  MultiMaze.NORTH,
			MultiMaze.EAST, MultiMaze.SOUTH, MultiMaze.WEST,MultiMaze.WAIT, };

	private int[] xStart, yStart, xGoal, yGoal;

	private MultiMaze maze;

	public MultiRobotProblem(MultiMaze m, int[] sx, int[] sy, int[] gx, int[] gy) {
		startNode = new MultiMazeNode(sx, sy, 0, null, 0);
		xStart = sx;
		yStart = sy;
		xGoal = gx;
		yGoal = gy;

		maze = m;
	}

	// node class used by searches. Searches themselves are implemented
	// in SearchProblem.
	public class MultiMazeNode implements SearchNode {

		// location of the agent in the maze
		protected int[][] state;

		// how far the current node is from the start. Not strictly required
		// for uninformed search, but useful information for debugging,
		// and for comparing paths
		private double cost;

		private int turn;

		private MultiMazeNode parent;

		public MultiMazeNode(int[] x, int[] y, double c, MultiMazeNode pre,
				int robotTurn) {
			state = new int[2][x.length];
			this.state[0] = x;
			this.state[1] = y;

			parent = pre;
			cost = c;
			turn = robotTurn;

		}

		public int getX(int robot) {
			return state[0][robot];
		}

		public int getY(int robot) {
			return state[1][robot];
		}

		public SearchNode getParent() {
			return parent;
		}

		public ArrayList<SearchNode> getSuccessors() {

			//List of all possible successors for current robot
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			
			// Change robots' turn
			int newTurn = 0; 
			if (turn < this.state[0].length - 1)
				newTurn = turn + 1;
			else
				newTurn = 0;

			
			// For each possible action: north, east, west, south, and wait
			for (int[] action : actions) {

				// Get the X,Y of all robots
				int newX[] = new int[this.state[0].length];
				int newY[] = new int[this.state[1].length];

				// Prevent memory aliasing
				System.arraycopy(state[0], 0, newX, 0, state[0].length);
				System.arraycopy(state[1], 0, newY, 0, state[1].length);

				// Change the current robot's coordinates to reflect action
				newX[turn] = this.state[0][turn] + action[0];
				newY[turn] = this.state[1][turn] + action[1];

					// Check to see if such an action is legal
					if (maze.isLegal(newX, newY)) {
						// Add to list if it is
						
						
						
					SearchNode succ = new MultiMazeNode(newX, newY,
							getCost() + 1.0, this, newTurn);
					successors.add(succ);
					

				}

			}
			return successors;

		}

		@Override
		public boolean goalTest() {
			for (int i = 0; i < state[0].length; i++) {
				if (state[0][i] != xGoal[i])
					return false;
				if (state[1][i] != yGoal[i])
					return false;
			}
			return true;
		}

		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state[0], ((MultiMazeNode) other).state[0])
					&& Arrays
							.equals(state[1], ((MultiMazeNode) other).state[1]);
		}

		@Override
		public int hashCode() {

			int hashed = 0;

			ArrayList<Integer> hashing = new ArrayList<Integer>();

			for (int i = 0; i < state[0].length; i++) {
				hashing.add(state[0][i]);
				hashing.add(state[1][i]);
			}

			for (int j = 0; j < hashing.size(); j++) {
				int timez = (int) Math.pow(10, j);
				hashed += hashing.get(j) * timez;
			}
			return hashed+turn;
		}

		@Override
		public String toString() {
			return new String("Maze state " + state[0] + ", " + state[1] + " "
					+ " depth " + getCost());
		}

		@Override
		public double getCost() {
			return cost;
		}

		@Override
		public double heuristic() {
			// manhattan distance metric for simple maze with one agent:
			// change this

			double dx = 0;
			double dy = 0;

			for (int i = 0; i < state[0].length; i++) {
				dx += Math.abs(xGoal[i] - state[0][i]);
				dy += Math.abs(yGoal[i] - state[1][i]);
			}
			// return Math.abs(dx) + Math.abs(dy);
			return 2*Math.max(dx, dy);
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
