import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;

public class Maze {
	public static final int WIDTH = 51;
	public static final int HEIGHT = 50;
	public static final int WALL_THICKNESS = 2;
	public static final int CELL_THICKNESS = 14;
	public static final int IMAGE_WIDTH = WIDTH * (WALL_THICKNESS + CELL_THICKNESS) + WALL_THICKNESS;
	public static final int IMAGE_HEIGHT = HEIGHT * (WALL_THICKNESS + CELL_THICKNESS) + WALL_THICKNESS;
	public static final String[] MAZES = { "./maze1.png", "./maze2.png", "./maze3.png", "./maze4.png", "./maze5.png" };
	public static final String OUTPUT_PATH = "./output.txt";

	public static void main(String[] args) {
		try {
			Integer[][][] mazeImages = new Integer[5][IMAGE_HEIGHT][IMAGE_WIDTH];
			System.out.println("reading maze images");
			for (int i = 0; i < 5; i++)
				mazeImages[i] = readImage(MAZES[i]);
			System.out.println("done reading maze images");

			System.out.println("pre-processing cells");
			Cell[] start = new Cell[5];
			for (int i = 0; i < 5; i++)
				start[i] = new Cell(0, (WIDTH - 1) / 2, HEIGHT, WIDTH, mazeImages[i], CELL_THICKNESS, WALL_THICKNESS);
			System.out.println("done pre-processing cells");

			// IDS
			System.out.println("starting IDS");
			Solution[] idsSolutions = new Solution[5];
			for (int i = 0; i < 5; i++)
				idsSolutions[i] = ids(start[i]);
			System.out.println("done IDS");

			// A*
			System.out.println("starting A*");
			Solution[] aStarSolutions = new Solution[5];
			for (int i = 0; i < 5; i++)
				aStarSolutions[i] = aStar(start[i]);
			System.out.println("done A*");

			// write output
			BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_PATH));
			for (int i = 0; i < 5; i++) {
				bw.write("" + idsSolutions[i].steps + ", " + aStarSolutions[i].steps + ", " + idsSolutions[i].moves);
				bw.newLine();
			}
			bw.close();
			System.out.println("Wrote Output File. Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// converts to a binary image where 1 means pixel is inside a cell, and 0 means
	// pixel is a wall
	public static Integer[][] readImage(String path) throws IOException {
		BufferedImage bi = ImageIO.read(new File(path));
		Integer[][] out = new Integer[IMAGE_HEIGHT][IMAGE_WIDTH];
		for (int i = 0; i < IMAGE_HEIGHT; i++) {
			for (int j = 0; j < IMAGE_WIDTH; j++) {
				Color color = new Color(bi.getRGB(j, i));
				out[i][j] = (color.getRed() + color.getBlue() + color.getGreen()) / (3 * 255);
			}
		}
		return out;
	}

	public static Solution ids(Cell start) {
		Integer[] steps = { 0 }; // array so passed by reference
		Cell goal = null;
		for (int i = HEIGHT; i < HEIGHT * WIDTH && goal == null; i++)
			goal = idsHelper(i, start, steps);
		// Go backwards from goal to find moves to complete maze
		String moves = "";
		Cell curr = goal;
		while (!curr.previous.equals(curr)) {
			if (curr.previous.vertIndex < curr.vertIndex) // previous move down
				moves = "D".concat(moves);
			else if (curr.previous.vertIndex > curr.vertIndex) // previous move up
				moves = "U".concat(moves);
			else if (curr.previous.horzIndex > curr.horzIndex) // previous move left
				moves = "L".concat(moves);
			else // previous move right
				moves = "R".concat(moves);
			curr = curr.previous;
		}
		return new Solution(goal, steps[0], moves);
	}

	// return null if solution can't be found, otherwise return goal cell
	public static Cell idsHelper(int finalDepth, Cell curr, Integer[] steps) {
		if (curr.h == 0) // boundary condition, goal found
			return curr;
		if (curr.g > finalDepth)
			return null;
		steps[0]++;
		ListIterator<Cell> iter = curr.successors.listIterator();
		Cell last = null;
		while (iter.hasNext() && (last == null || last.h != 0)) {
			last = idsHelper(finalDepth, iter.next(), steps);
		}
		return last;
	}

	public static Solution aStar(Cell start) {
		PriorityQueue<Cell> queue = new PriorityQueue<Cell>(WIDTH * HEIGHT, new Comparator<Cell>() {
			public int compare(Cell cell1, Cell cell2) {
				if (cell1.g + cell1.h > cell2.g + cell2.h)
					return 1;
				if (cell1.g + cell2.h < cell2.g + cell2.h)
					return -1;
				return 0;
			}
		});

		queue.add(start);
		Cell curr = start;
		int[] steps = { 1 };

		while (!queue.isEmpty() && curr.h != 0) {
			curr = queue.poll();
			for (Cell next : curr.successors) {
				queue.add(next);
				steps[0]++;
			}
		}
		Cell goal = curr;

		String moves = "";
		while (!curr.previous.equals(curr)) {
			if (curr.previous.vertIndex < curr.vertIndex) // previous move down
				moves = "D".concat(moves);
			else if (curr.previous.vertIndex > curr.vertIndex) // previous move up
				moves = "U".concat(moves);
			else if (curr.previous.horzIndex > curr.horzIndex) // previous move left
				moves = "L".concat(moves);
			else // previous move right
				moves = "R".concat(moves);
			curr = curr.previous;
		}
		return new Solution(goal, steps[0], moves);
	}
}

// object to hold goal state and number of steps to reach it
class Solution {
	Cell goal;
	int steps;
	String moves;

	public Solution(Cell goal, int steps, String moves) {
		this.goal = goal;
		this.steps = steps;
		this.moves = moves;
	}
}