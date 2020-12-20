import java.util.ArrayList;

//Constructor initializes the start cell of the maze, and finds all successors in maze using a conditioned DFS, keeping track of cost and heuristic along the way.
//Does not construct a successor if another path has already reached that successor at a lower cost during the process (prevent infinite loops)
//start cell is own previous cell

//This will already find the solution to the maze (and the optimal solution to the maze if given a maze with loops/multiple ways to reach the same cell
// during the final visit to the goal cell... but the mazes we deal with for this homework do not contain such things)
public class Cell {
	public int vertIndex;
	public int horzIndex;
	public Cell previous;
	public int g;
	public int h;
	public ArrayList<Cell> successors;
	private static int[][] gVisited; // store lowest cost to reach each cell

	// constructor for start cell
	public Cell(int vertIndex, int horzIndex, int mazeHeight, int mazeWidth, Integer[][] maze, int cellThickness,
			int wallThickness) {
		this.vertIndex = vertIndex;
		this.horzIndex = horzIndex;
		this.h = Math.abs(mazeHeight - 1 - vertIndex) + Math.abs(mazeWidth / 2 - horzIndex); // integer division takes
																								// care of even and
		// odd width cases
		this.g = 0;
		this.successors = new ArrayList<Cell>();
		this.previous = this; // start cell is its own previous cell

		// initialize gVisited
		gVisited = new int[mazeHeight][mazeWidth];
		for (int i = 0; i < mazeHeight; i++)
			for (int j = 0; j < mazeWidth; j++)
				gVisited[i][j] = mazeHeight * mazeWidth; // upper bound on cost
		gVisited[vertIndex][horzIndex] = 0;

		// compute neighbors if should continue looking
		int[] centerPixel = { (cellThickness + wallThickness) * vertIndex + (cellThickness + wallThickness) / 2,
				(cellThickness + wallThickness) * horzIndex + (cellThickness + wallThickness) / 2 };
		// check left
		if (horzIndex > 0 && maze[centerPixel[0]][centerPixel[1] - cellThickness / 2] == 1 && maze[centerPixel[0]][centerPixel[1] - cellThickness / 2 - 1] == 1)
			this.successors.add(new Cell(vertIndex, horzIndex - 1, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check right
		if (horzIndex < mazeWidth - 1 && maze[centerPixel[0]][centerPixel[1] + cellThickness / 2] == 1 && maze[centerPixel[0]][centerPixel[1] + cellThickness / 2 + 1] == 1)
			this.successors.add(new Cell(vertIndex, horzIndex + 1, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check above
		if (vertIndex > 0 && maze[centerPixel[0] - cellThickness / 2][centerPixel[1]] == 1 && maze[centerPixel[0] - cellThickness / 2 - 1][centerPixel[1]] == 1)
			this.successors.add(new Cell(vertIndex - 1, horzIndex, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check below
		if (vertIndex < mazeHeight - 1 && maze[centerPixel[0] + cellThickness / 2][centerPixel[1]] == 1 && maze[centerPixel[0] + cellThickness / 2 + 1][centerPixel[1]] == 1)
			this.successors.add(new Cell(vertIndex + 1, horzIndex, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
	}

	// constructor for non-initial cells
	public Cell(int vertIndex, int horzIndex, int mazeHeight, int mazeWidth, Integer[][] maze, int cellThickness,
			int wallThickness, int g, Cell previous) {
		this.vertIndex = vertIndex;
		this.horzIndex = horzIndex;
		this.previous = previous;
		this.h = Math.abs(mazeHeight - 1 - vertIndex) + Math.abs(mazeWidth / 2 - horzIndex); // integer division takes
																								// care of even and
		// odd width cases
		this.g = g;

		if (gVisited[vertIndex][horzIndex] > g)
			gVisited[vertIndex][horzIndex] = g;

		this.successors = new ArrayList<Cell>();
		// compute neighbors. Do not add a neighbor if it has been previously visited
		// with a higher cost to prevent infinite loops
		int[] centerPixel = { (cellThickness + wallThickness) * vertIndex + (cellThickness + wallThickness) / 2,
				(cellThickness + wallThickness) * horzIndex + (cellThickness + wallThickness) / 2 };
		// check left
		if (horzIndex > 0 && gVisited[vertIndex][horzIndex - 1] > g + 1
				&& maze[centerPixel[0]][centerPixel[1] - cellThickness / 2] == 1 && maze[centerPixel[0]][centerPixel[1] - cellThickness / 2 - 1] == 1)
			this.successors.add(new Cell(vertIndex, horzIndex - 1, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check right
		if (horzIndex < mazeWidth - 1 && gVisited[vertIndex][horzIndex + 1] > g + 1
				&& maze[centerPixel[0]][centerPixel[1] + cellThickness / 2] == 1  && maze[centerPixel[0]][centerPixel[1] + cellThickness / 2 + 1] == 1)
			this.successors.add(new Cell(vertIndex, horzIndex + 1, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check above
		if (vertIndex > 0 && gVisited[vertIndex - 1][horzIndex] > g + 1
				&& maze[centerPixel[0] - cellThickness / 2][centerPixel[1]] == 1 && maze[centerPixel[0] - cellThickness / 2 - 1][centerPixel[1]] == 1)
			this.successors.add(new Cell(vertIndex - 1, horzIndex, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
		// check below
		if (vertIndex < mazeHeight - 1 && gVisited[vertIndex + 1][horzIndex] > g + 1
				&& maze[centerPixel[0] + cellThickness / 2][centerPixel[1]] == 1 && maze[centerPixel[0] + cellThickness / 2 + 1][centerPixel[1]] == 1)
			this.successors.add(new Cell(vertIndex + 1, horzIndex, mazeHeight, mazeWidth, maze, cellThickness,
					wallThickness, g + 1, this));
	}

}
