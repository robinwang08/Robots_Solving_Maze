package mazeworld;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MultiMaze {
	final static Charset ENCODING = StandardCharsets.UTF_8;

	// A few useful constants to describe actions
	public static int[] NORTH = {0, 1};
	public static int[] EAST = {1, 0};
	public static int[] SOUTH = {0, -1};
	public static int[] WEST = {-1, 0};
	//robot waits a turn
	public static int[] WAIT = {0, 0};
	
	public int width;
	public int height;
	
	private char[][] grid;

	public static MultiMaze readFromFile(String filename) {
		MultiMaze m = new MultiMaze();

		try {
			List<String> lines = readFile(filename);
			m.height = lines.size();

			int y = 0;
			m.grid = new char[m.height][];
			for (String line : lines) {
				m.width = line.length();
				m.grid[m.height - y - 1] = new char[m.width];
				for (int x = 0; x < line.length(); x++) {
					// (0, 0) should be bottom left, so flip y as 
					//  we read from file into array:
					m.grid[m.height - y - 1][x] = line.charAt(x);
				}
				y++;

				// System.out.println(line.length());
			}

			return m;
		} catch (IOException E) {
			return null;
		}
	}

	private static List<String> readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllLines(path, ENCODING);
	}

	public char getChar(int x, int y) {
		return grid[y][x];
	}
	
	// is the location x, y on the map, and also a legal floor tile (not a wall)?
	public boolean isLegal(int[] x, int[] y) {
		// on the map
		

		//check for all
		for (int i=0;i<x.length;i++){
			
			//is in grid
			if(x[i] < 0 || x[i] >= width || y[i] < 0 || y[i] >= height)
				return false;
				
				//is floor tile?
				if( getChar(x[i],y[i]) != '.')
				return false;
				
				
				//is other robot
				int count = 0;
				for (int j = 0; j<x.length;j++){
					if(x[j]==x[i] && y[j]==y[i])
						count++;
					if(count > 1)
						return false;	
				}
				
			}
		
		return true;
	}
	
	
	public boolean isBlindLegal(int x, int y) {
		// on the map
				if(x >= 0 && x < width && y >= 0 && y < height) {
					// and it's a floor tile, not a wall tile:
					return getChar(x, y) == '.';
				}
				return false;
	}
	
	
	
	public String toString() {
		String s = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				s += grid[y][x];
			}
			s += "\n";
		}
		return s;
	}

	public static void main(String args[]) {
		Maze m = Maze.readFromFile("simple.maz");
		System.out.println(m);
	}

}
