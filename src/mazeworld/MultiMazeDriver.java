package mazeworld;

import java.util.ArrayList;
import java.util.List;

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

public class MultiMazeDriver extends Application {

	MultiMaze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 15;
	MultiMazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = MultiMaze.readFromFile("/Users/robin/Documents/learn/mazeworld/src/assignment_mazeworld/simple.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MultiMazeView(maze, PIXELS_PER_SQUARE);
		
		
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
//0, 39
		//5,5
		
		//13, 14
		
		//21 21
		int[] sx = new int[] {0,39,39};
		int[] sy = new int[] {5,5,21};
		
		
		int[] gx = new int[] {14,13,13};
		int[] gy = new int[] {21,19,21};

		MultiRobotProblem mazeProblem = new MultiRobotProblem(maze, sx, sy, gx,gy);

	List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		animationPathList.add(new AnimationPath(mazeView, bfsPath));
		System.out.println("BFS:  ");
		mazeProblem.printStats();
/*

		List<SearchNode> dfsPath = mazeProblem
				.depthFirstPathCheckingSearch(5000);
		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		System.out.println("DFS:  ");
		mazeProblem.printStats();
		
		

	
		List<SearchNode> astarPath = mazeProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, astarPath));
		System.out.println("A*:  ");
		mazeProblem.printStats();
		
		*/
		
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
		private Node[] piece;
		private List<SearchNode> searchPath;
		private int currentMove = 0;

		private int[] lastX;
		private int[] lastY;
		
		private int numRobots;

		boolean animationDone = true;

		public AnimationPath(MultiMazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			
			MultiMazeNode firstNode = (MultiMazeNode) searchPath.get(0);
			
			piece = new Node[firstNode.state[0].length];
			numRobots = firstNode.state[0].length;
			System.out.println(numRobots);
			
			lastX = new int[numRobots];
			lastY = new int[numRobots];
			
			for (int i = 0; i < numRobots; i++){
				
				piece[i] = mazeView.addPiece(firstNode.getX(i), firstNode.getY(i));
				lastX[i] = firstNode.getX(i);
				lastY[i] = firstNode.getY(i);
				
			}
		
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			
			
			
			if (currentMove < searchPath.size() && animationDone) {
			
				for(int i = 0; i < numRobots; i++ ){
				
				MultiMazeNode mazeNode = (MultiMazeNode) searchPath.get(currentMove);
				int dx = mazeNode.getX(i) - lastX[i];
				int dy = mazeNode.getY(i) - lastY[i];
				// System.out.println("animating " + dx + " " + dy);
				animateMove(piece[i], dx, dy);
				lastX[i] = mazeNode.getX(i);
				lastY[i] = mazeNode.getY(i);

				
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