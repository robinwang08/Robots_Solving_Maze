package mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mazeworld.BlindRobotProblem.BlindMazeNode;
import mazeworld.MultiRobotProblem.MultiMazeNode;
import mazeworld.SearchProblem.SearchNode;
import mazeworld.SimpleMazeProblem.SimpleMazeNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BlindRobotDriver extends Application {

	MultiMaze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 60;
	BlindMazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = MultiMaze.readFromFile("/Users/robin/Documents/learn/mazeworld/src/assignment_mazeworld/simple.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new BlindMazeView(maze, PIXELS_PER_SQUARE);
		
	
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		
		Set<List<Integer>> sxy = new HashSet<List<Integer>>();
		Set<List<Integer>> gxy = new HashSet<List<Integer>>();
		
		
		for (int i = 0; i < maze.width; i++){
			for (int j =0; j < maze.height; j++){
				if(maze.isBlindLegal(i,j)){
			
					List<Integer> xy = new ArrayList<Integer>();
					xy.add(i);
					xy.add(j);
					sxy.add(xy);
				}
			}
		}

		List<Integer> xy = new ArrayList<Integer>();
		xy.add(6);
		xy.add(0);
		gxy.add(xy);

		BlindRobotProblem mazeProblem = new BlindRobotProblem(maze, sxy, gxy);

		/*
		 * 
		List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		animationPathList.add(new AnimationPath(mazeView, bfsPath));
		System.out.println("DFS:  ");
		mazeProblem.printStats();


		List<SearchNode> dfsPath = mazeProblem
				.depthFirstPathCheckingSearch(5000);
		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		System.out.println("BFS:  ");
		mazeProblem.printStats();
		
	
*/
		
		List<SearchNode> astarPath = mazeProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, astarPath));
		System.out.println("A*:  ");
		mazeProblem.printStats();
		

	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {
		
		initMazeView();
	
		primaryStage.setTitle("Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.05), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private List<Node> piece;
		private List<SearchNode> searchPath;
		private int currentMove = 1;

		
		boolean animationDone = true;

		public AnimationPath(BlindMazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			
			BlindMazeNode firstNode = (BlindMazeNode) searchPath.get(0);
			
			piece = new ArrayList<Node>();
			
			for (List<Integer> states:firstNode.stateSet){
				
			                        	piece.add(mazeView.addPiece(states.get(0), states.get(1)));
				
				
			}
		
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
				
			if (currentMove < searchPath.size() && animationDone) {
			
				BlindMazeNode prevNode = (BlindMazeNode) searchPath.get(currentMove-1);
				BlindMazeNode currNode = (BlindMazeNode) searchPath.get(currentMove);
				
				for(List<Integer> states:prevNode.stateSet){
					piece.add(mazeView.rPiece(states.get(0), states.get(1)));
					animateMove(piece.get(0), 0, 0);
				}

				for (List<Integer> states:currNode.stateSet){
					piece.add(mazeView.addPiece(states.get(0), states.get(1)));
					animateMove(piece.get(0), 0, 0);
				}
				
				
				currentMove++;
			}

		}

		// move the piece n by dx, dy cells
		public void animateMove(Node n, int dx, int dy) {
			animationDone = false;
			TranslateTransition tt = new TranslateTransition(
					Duration.millis(300), n);
			tt.setByX(PIXELS_PER_SQUARE * dx);
			tt.setByY(-PIXELS_PER_SQUARE * dy);
			// set a callback to trigger when animation is finished
			tt.setOnFinished(new AnimationFinished());

			tt.play();

		}

		// when the animation is finished, set an instance variable flag
		//  that is used to see if the path is ready for the next step in the
		//  animation
		private class AnimationFinished implements EventHandler<ActionEvent> {
			@Override
			public void handle(ActionEvent event) {
				animationDone = true;
			}
		}
	}
}