package sudokusolver;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import java.util.*;
import java.io.*;

// make this work for nxn sudoku puzzles (n must be a square)
// to understand the moves, visit https://www.kristanix.com/sudokuepic/sudoku-solving-techniques.php
@SuppressWarnings("unused")
public class Board {
	
	// possible ways to initialize board ... ?
//	List<List<Integer>> board = new ArrayList<List<Integer>>();
//	ArrayList<Integer>[][] board = (ArrayList<Integer>[][])new ArrayList[9][9];
//	public static final int[][] board = new int[9][9];
	
	// the starting board is held here. to be read from a file
	int[][] boardStart = new int[9][9];
	// file that will contain the starting board
	String boardFile;
	
	// initial board, not to be modified
//	public static int[][] boardStart =		// can not make the contents of an array immutable
//		{
//				{6, 1, 2, 7, 0, 3, 8, 0, 5},		// arrays are objects and objects are
//				{0, 0, 0, 0, 0, 0, 4, 1, 0},		// always references -- 'final' would do nothing
//				{0, 5, 0, 0, 1, 0, 7, 0, 6},
//				{0, 0, 9, 5, 0, 7, 0, 8, 0},
//				{0, 0, 0, 0, 9, 0, 0, 0, 0},
//				{0, 7, 0, 1, 0, 4, 5, 0, 0},
//				{2, 0, 1, 0, 5, 0, 0, 4, 0},
//				{0, 8, 3, 0, 0, 0, 0, 0, 0},
//				{7, 0, 4, 9, 0, 1, 2, 5, 8}
//		};
	
	// other ways to initialize
//	//board[3] = {0, 0, 9, 5, 0, 7, 0, 8, 0};
//	board[3][2] = 9;
//	board[3][3] = 5;
//	board[3][5] = 7;
//	board[3][7] = 8;
	
	// name self-explanatory
	public Map<Integer, List<Integer>> rowOptions = new HashMap<Integer, List<Integer>>();
	public Map<Integer, List<Integer>> colOptions = new HashMap<Integer, List<Integer>>();
	// boxes are arranged like:
	//		1 2 3
	//		4 5 6
	//		7 8 9
	// like reading a book
	public Map<Integer, List<Integer>> boxOptions = new HashMap<Integer, List<Integer>>();
	
	// working solution
	public int[][] sol = new int[9][9];
	// possible candidates for each cell
	// used google's HashBasedTable to achieve
	Table<Integer, Integer, List<Integer>> possibleMapTable = HashBasedTable.create();
	
	
	
	// list of coordinates of existing locations for each number
	// not used yet. if you wanted to use this, probably better to use HashBasedTable again like possibleMapTable
	// maybe not actually? cause the key is only an integer
	// actually maybe something like:
	// public Map<Integer, Map<Integer, List<Integer>>> coords;
	// to be able to get coords by row or col or whatever - one or the other but not both?
	public Map<Integer, List<Tuple<Integer, Integer>>> coords = 
			new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	// list of available coordinates??? also not used
	public Map<Integer, List<Tuple<Integer, Integer>>> avail = 
			new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	
	// if i want to include notes. but isn't this pretty much what possible and possibleMap are?
//	public static ArrayList<Integer>[][] notes = (ArrayList<Integer>[][])new ArrayList[9][9];
//	public static Map<Integer, Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>>> notes =
//	new HashMap<Integer, Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>>>();
	
	// constructor with given start board
	public Board(String boardData) {
		
		boardFile = boardData;
		
		List<Integer> all = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		for (int i = 0; i < 9; i++) {
			rowOptions.put(i, new ArrayList<Integer>());
			rowOptions.get(i).addAll(all);
			colOptions.put(i, new ArrayList<Integer>());
			colOptions.get(i).addAll(all);
			boxOptions.put(i, new ArrayList<Integer>());
			boxOptions.get(i).addAll(all);
		}
		
		try {
			boardInit();
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist?");
			throw new RuntimeException(e);
		}
		
		// working solution
//		int[][] sol = new int[9][9];
		// possible candidates for each cell
//		int[][][] possible = new int[9][9][9];	// a list would probably be more convenient
//		public Map<Tuple<Integer, Integer>, List<Integer>> possible = 
//				new HashMap<Tuple<Integer, Integer>, List<Integer>>();
		
//		Map<Integer, List<Integer>> rowOptions = new HashMap<Integer, List<Integer>>();
//		Map<Integer, List<Integer>> colOptions = new HashMap<Integer, List<Integer>>();
//		Map<Integer, List<Integer>> boxOptions = new HashMap<Integer, List<Integer>>();
		
		
//		Map<Integer, List<Tuple<Integer, Integer>>> coords = 
//				new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	}
	
	// instead of try-catch, could just throw FNF exception here?
	public static void main(String[] args) {
		String s = "./boards/board-easy-1.txt";
		Board b = new Board(s);
		
		String s2 = "./boards/board-med-1.txt";
		Board b2 = new Board(s2);
		
		String s3 = "./boards/board-hard-1.txt";
		Board b3 = new Board(s3);
		
		// on this board, after the first trimPossibleByBlock -- at least before Positive was implemented --
		// we get a hidden pair of 6/7 in row 3 cols 7 & 8. investigate, use for testing
		String s4 = "./boards/board-evil-1.txt";
		Board b4 = new Board(s4);

		
		
		
		// ***** simplify testing new boards *****
		Board current = b4;
//		Board current = new Board(s);
		
		
		
		
		// print out the starting board
		current.printBoard();
		
		// determine what could possibly be stored in each square
		// just based on checking row, col, and box
		// cmd-opt-r to rename all in this block simultaneously in eclipse - be careful
		current.fillPossible();
		
//		current.printCellPoss(0, 0);
//		current.printCellPoss(0, 4);
//		current.printCellPoss(0, 7);
//		current.printCellPoss(4, 8);
//		System.out.println();
		
		// CHECKs INIT -- GOOD
//		current.printBoard();
//		current.printSol();
		///////////////
				
		// try to categorize logic by difficulty?
		// fill easy squares first, when possible
		// start with checkForSoleCandidate()
		// then maybe checkForUniqueCandidate()
		// move up to checking if a certain number in some box is only possible in a certain row or col
		// check for naked/hidden subsets
		// finally implement x-wing
		// if one becomes enlightened, one might try "forcing chain" -- see if, for a cell with only two possibilities,
		// each possibility must lead to a specific result for some other cell
		
//		current.printBoard();
		
		boolean loopB = true;		
		// board b can be fully solved by either method alone

		int innerLoop1;
		int innerLoop2;
		int innerLoop3;
		int[] loop = new int[3];
		
		innerLoop3 = 1;
		while (innerLoop3 != 0) {
			innerLoop2 = 1;
			while (innerLoop2 != 0) {
				innerLoop1 = 1;
				while (innerLoop1 != 0) {
					current.printSol();
					innerLoop1 = current.checkForSoleCandidate();
					if (innerLoop1 != 0) System.out.println("Added some sole candidates");
				}
				current.printSol();
				innerLoop2 = current.checkForUniqueCandidate();
				if (innerLoop2 != 0) System.out.println("Added some unique candidates");
			}
			current.printSol();
			innerLoop3 = current.trimPossibleByBlock();
			if (innerLoop3 != 0) System.out.println("Did some trimming (block)");
		}
		
		// CHECK SOLUTIONS
//		current.printSol();
		System.out.println();
		System.out.println("Solution is " + (current.checkSol() == 0 ? "correct." : "incorrect or incomplete."));
		
//		current.printInitandSol();
		
	}
		
	// set up the board initially
	private void boardInit() throws FileNotFoundException {
		File f = new File(boardFile);
		
		Scanner scanner = null;
		try {
//			if (f.exists()) scanner = new Scanner(f);
//			else return;
			scanner = new Scanner(f);
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		int next = 0;
		if (scanner.hasNextInt()) next = scanner.nextInt();
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				boardStart[i][j] = next;
				sol[i][j] = next;

				if (next == 0) {
					possibleMapTable.put(i, j, new ArrayList<Integer>());
				} else {
					rowOptions.get(i).remove(Integer.valueOf(next));
					colOptions.get(j).remove(Integer.valueOf(next));

					int row = i / 3;
					int col = j / 3;
					boxOptions.get((row * 3) + col).remove(Integer.valueOf(next));
				}
				
				// we're pretty much assuming the file is formatted correctly
				if (scanner.hasNextInt()) next = scanner.nextInt();
			}
		}
		
		scanner.close();
	}
	
	// set the working solution, maintain whatever other lists we're working with
	private void setSolCell(int row, int col, int val) {
		if (sol[row][col] == val) System.out.println("This could be a problem?");
//		System.out.println("Row " + row + ", Column " + col + ": should now be " + val);
		sol[row][col] = val;
		
		rowOptions.get(row).remove(Integer.valueOf(val));
		colOptions.get(col).remove(Integer.valueOf(val));
		boxOptions.get(((row / 3) * 3) + (col / 3)).remove(Integer.valueOf(val));
		
		trimPossible(row, col, val);
	}
	
	// determine what could possibly be stored in each square
	// just based on checking row, col, and box
	private int fillPossible() {
		// cycle through squares on the board
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// skip the square if the value has already been correctly set
				if (boardStart[i][j] != 0 || possibleMapTable.get(i, j) == null) continue;
				
				// populate what the square could possibly hold
				// simply based on checking row, col, and box
				for (int k = 1; k < 10; k++) {
					if(checkVal(i, j, k) == 0) {
						possibleMapTable.get(i, j).add(k);
					}
				}
			}
		}
		return 0;
	}
	
	// the solution has been modified and now we have to update the
	// possibility array for other boxes. only updates within row, col, or box
	private int trimPossible(int row, int col, int val) {
		if (sol[row][col] != val) System.out.println("trimPossible called but sol[][] hasn't been updated. "
				+ "This would be a problem.");
		// check row and col
		// iterate through row/col of the modified square
		for (int i = 0; i < 9; i++) {
			// NOTE: if val is not an element of that list, remove does not modify the list
			if (possibleMapTable.get(row, i) != null) possibleMapTable.get(row, i).remove(Integer.valueOf(val));
			if (possibleMapTable.get(i, col) != null) possibleMapTable.get(i, col).remove(Integer.valueOf(val));
		}
		// search within the box
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// iterate through array of possibilities for the square				
				if (possibleMapTable.get(((row / 3) * 3) + i, ((col / 3) * 3) + j) != null) {
					possibleMapTable.get(((row / 3) * 3) + i, ((col / 3) * 3) + j).remove(Integer.valueOf(val));
				}
			}
		}
		if (possibleMapTable.get(row, col) != null) {
			possibleMapTable.get(row, col).clear();
			possibleMapTable.remove(row, col);
		}
		return 0;
	}

	private int trimPossibleBySubset() {
		int modified = 0;
		
		if (trimPossibleBySubsetNaked() != 0) modified = 1;
		if (trimPossibleBySubsetHidden() != 0) modified = 2;
		
		return modified;
	}
	
	
	private int trimPossibleBySubsetNaked() {
		int modified = 0;
		
		if (trimPossibleBySubsetNakedWithinBox() != 0) modified = 1;
		if (trimPossibleBySubsetNakedWithinRowCol() != 0) modified = 2;
		
		return modified;
	}
	
	private int trimPossibleBySubsetNakedWithinBox() {
		int modified = 0;
		
		
		
		return modified;
	}
	
	private int trimPossibleBySubsetNakedWithinRowCol() {
		int modified = 0;
		
		List<Integer> rowPairs = new ArrayList<Integer>();
		List<Integer> colPairs = new ArrayList<Integer>();
		// start with implementing pairs of 2, then generalize ?
		// the semi-tricky thing is: squares with subsets of the subset count too
		// i.e. three squares with [5, 7], [5, 7, 8], [5, 8] as options count as a triple
		// and the options 5, 7, and 8 should be removed from other squares
		for (int i = 0; i < 9; i++) {
			rowPairs.clear();
			colPairs.clear();
			for (int j = 0; j < 9; j++) {
				// what's an efficient way to do this
				if (possibleMapTable.get(i, j) != null) {
					
				}
			}
		}
		
		return modified;
	}
	
	private int trimPossibleBySubsetHidden() {
		int modified = 0;
		
		if (trimPossibleBySubsetHiddenWithinBox() != 0) modified = 1;
		if (trimPossibleBySubsetHiddenWithinRowCol() != 0) modified = 2;
		
		return modified;
	}
	
	private int trimPossibleBySubsetHiddenWithinBox() {
		int modified = 0;
		
		return modified;
	}
	
	private int trimPossibleBySubsetHiddenWithinRowCol() {
		int modified = 0;
		
		return modified;
	}
	
	private int trimPossibleByBlock() {
		int modified = 0;
		
		if (trimPossibleByBlockPositive() != 0) modified = 1;
		if (trimPossibleByBlockNegative() != 0) modified = 2;
		
		return modified;
	}
	
	// if, for some number, all the candidates within a certain block are within the same row or col,
	// that number within that block will be in that row or col,
	// and any other candidates for that number in that row or col outside of the block can be removed
	private int trimPossibleByBlockPositive() {
		int modified = 0;
		
		List<Integer> toCheck = new ArrayList<Integer>();
		
		for (int searchedNum = 1; searchedNum < 10; searchedNum++) {
			// cycle through the boxes
			for (int boxRow = 0; boxRow < 3; boxRow++) {
				for (int boxCol = 0; boxCol < 3; boxCol++) {
					// let's not even look in this box if the searchedNum is not an option here
					// meaning it's already placed somewhere within the box, and no other square will have it as an option
					if (!boxOptions.get(boxRow * 3 + boxCol).contains(searchedNum)) continue;
					toCheck.clear();
					
					// search within the box
					for (int row = 0; row < 3; row++) {
						for (int col = 0; col < 3; col++) {
							// iterate through array of possibilities for the square				
							if (possibleMapTable.get((3 * boxRow) + row, (3 * boxCol) + col) != null
									&& possibleMapTable.get((3 * boxRow) + row, (3 * boxCol) + col).contains(searchedNum)) {
								// encodes each square in the box with a value
								// like:
								//		0 1 2
								//		3 4 5
								//		6 7 8
								// there's probably an easier way
								toCheck.add((3 * row) + col);
							}
						}
					}
					
					if (!toCheck.isEmpty()) {
						// see if every option in toCheck is in the same row or column
						int row = toCheck.get(0) / 3;
						int col = toCheck.get(0) % 3;
						for (Integer elt : toCheck) {
							if (row != -1 && row != elt / 3) {
								row = -1;
							}
							if (col != -1 && col != elt % 3) {
								col = -1;
							}
							if (row == -1 && col == -1) {
								break;
							}
						}
						// if row is not -1, then all potential elements of searchedNum that are in this block 
						// are also within the same row.
						// if the other two boxes on the same boxRow already contain a placement of searchedNum,
						// then we can skip this because it won't do anything. false alarm
						if (row != -1
								&& (boxOptions.get((boxRow * 3) + ((boxCol + 1) % 3)).contains(searchedNum)
								|| boxOptions.get((boxRow * 3) + ((boxCol + 2) % 3)).contains(searchedNum))) {
							for (int i = 0; i < 9; i++) {
								// don't want to affect any of the items within this box. only outside
								if (i / 3 == boxCol) continue;
								if (possibleMapTable.get((boxRow * 3) + row, i) != null
										&& possibleMapTable.get((boxRow * 3) + row, i).contains(searchedNum)) {
									possibleMapTable.get((boxRow * 3) + row, i).remove(Integer.valueOf(searchedNum));
									modified = 1;
								}
							}
						}
						// if col is not -1, then all elements are in this block are within the same col
						// if the other two boxes in the same boxCol already contain a placement of searchedNum,
						// then we can skip this because it won't do anything. false alarm
						if (col != -1
								&& (boxOptions.get((((boxRow + 1) % 3) * 3) + boxCol).contains(searchedNum)
								|| boxOptions.get((((boxRow + 2) % 3) * 3) + boxCol).contains(searchedNum))) {
							for (int i = 0; i < 9; i++) {
								// don't want to affect any of the items within this box. only outside
								if (i / 3 == boxRow) continue;
								if (possibleMapTable.get(i, (boxCol * 3) + col) != null
										&& possibleMapTable.get(i, (boxCol * 3) + col).contains(searchedNum)) {
									possibleMapTable.get(i, (boxCol * 3) + col).remove(Integer.valueOf(searchedNum));
									modified = 1;
								}
							}
						}
					}
				}
			}
		}
		
		return modified;
	}
	
	// if, for some number, within some row or col all the candidates are within a single block,
	// that number within that row or col will be within that block,
	// any other candidates for that number in the block but outside the row or col can be removed.
	private int trimPossibleByBlockNegative() {
		int modified = 0;
		
		List<Integer> toCheckRow = new ArrayList<Integer>();
		List<Integer> toCheckCol = new ArrayList<Integer>();
		
		for (int searchedNum = 1; searchedNum < 10; searchedNum++) {
			for (int i = 0; i < 9; i++) {
				toCheckRow.clear();
				toCheckCol.clear();
				if (rowOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleMapTable.get(i, j) != null && possibleMapTable.get(i, j).contains(searchedNum)) {
							toCheckRow.add(j);
						}
					}
				}
				if (colOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleMapTable.get(j, i) != null && possibleMapTable.get(j, i).contains(searchedNum)) {
							toCheckCol.add(j);
						}
					}
				}
				
				// TODO ? could speed up more by calculating toCheckRowBlock and toCheckColBlock during the add step
				if (!toCheckRow.isEmpty()) {
					// block will be 0, 1, or 2. it will help us remember which block the element elt is in
					int block = toCheckRow.get(0) / 3;
					for (Integer elt : toCheckRow) {
						if (block != elt / 3) {
							block = -1;
							break;
						}
					}
					// if block is not -1, then all elements are in the same block, which is stored in block
					if (block != -1
							&& (boxOptions.get(((i / 3) * 3) + ((block + 1) % 3)).contains(searchedNum)
							|| boxOptions.get(((i / 3) * 3) + ((block + 2) % 3)).contains(searchedNum))) {
						for (int _i = 0; _i < 3; _i++) {
							// i is the row to avoid in this case. i is the row that we are preserving the possible map
							if (_i == i % 3) continue;
							for (int _j = 0; _j < 3; _j++) {
								if (possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j) != null 
										&& possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j).contains(searchedNum)) {
									possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j).remove(Integer.valueOf(searchedNum));
									modified = 1;
								}
							}
						}
					}
				}
				if (!toCheckCol.isEmpty()) {
					// do the same for columns
					int block = toCheckCol.get(0) / 3;
					for (Integer elt : toCheckCol) {
						if (block != elt / 3) {
							block = -1;
							break;
						}
					}
					// if block is not -1, then all elements are in the same block, which is stored in block
					if (block != -1
							&& (boxOptions.get((((block + 1) % 3) * 3) + (i / 3)).contains(searchedNum)
							|| boxOptions.get((((block + 2) % 3) * 3) + (i / 3)).contains(searchedNum))) {
						for (int _i = 0; _i < 3; _i++) {
							// i is the col to avoid in this case. i is the col that we are preserving the possible map
							if (_i == i % 3) continue;
							for (int _j = 0; _j < 3; _j++) {
								if (possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i) != null 
										&& possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i).contains(searchedNum)) {
									possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i).remove(Integer.valueOf(searchedNum));
									modified = 1;
								}
							}
						}
					}
				}
			}
		}
		
		return modified;
	}
	
	// look for squares where only 1 possible value could fill the square
	// just based on checking rows, cols, and boxes
	private int checkForSoleCandidate() {
		int modified = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// sol hasn't been filled in yet, but there is only 1 possibility for this square.
				if (sol[i][j] == 0 && possibleMapTable.get(i, j).size() == 1) {
//					sol[i][j] = possibleMapTable.get(i, j).get(0);
//					
//					rowOptions.get(i).remove(Integer.valueOf(possibleMapTable.get(i, j).get(0)));
//					colOptions.get(j).remove(Integer.valueOf(possibleMapTable.get(i, j).get(0)));
//					boxOptions.get(((i / 3) * 3) + (j / 3)).remove(Integer.valueOf(possibleMapTable.get(i, j).get(0)));
//					
//					modified = 1;
//					trimPossible(i, j, possibleMapTable.get(i, j).get(0));
					setSolCell(i, j, possibleMapTable.get(i, j).get(0));
					modified = 1;
				}
			}
		}
		return modified;
	}
	
	// look for rows, cols, and boxes in which some number has only a single option for placement
	// and place it there
	private int checkForUniqueCandidate() {
		int modified = 0;
		
		if (checkForUniqueCandidateByRowCol() != 0) modified = 1;
		if (checkForUniqueCandidateByBox() != 0) modified = 2;
		
		return modified;
	}
	
	// look for rows and cols in which some number has only a single option for placement
	// and place it there
	private int checkForUniqueCandidateByRowCol() {
		int modified = 0;
		
		List<Integer> toCheckRow = new ArrayList<Integer>();
		List<Integer> toCheckCol = new ArrayList<Integer>();
		
		for (int searchedNum = 1; searchedNum < 10; searchedNum++) {
			for (int i = 0; i < 9; i++) {
				toCheckRow.clear();
				toCheckCol.clear();
				if (rowOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleMapTable.get(i, j) != null && possibleMapTable.get(i, j).contains(searchedNum)) {
							toCheckRow.add(j);
						}
					}
				}
				if (colOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleMapTable.get(j, i) != null && possibleMapTable.get(j, i).contains(searchedNum)) {
							toCheckCol.add(j);
						}
					}
				}
				// TODO ? could speed this up even more if we also did the trimming search here i think
				if (toCheckRow.size() == 1) {
					setSolCell(i, toCheckRow.get(0), searchedNum);
					modified = 1;
				}
				if (toCheckCol.size() == 1) {
					setSolCell(toCheckCol.get(0), i, searchedNum);
					modified = 1;
				}
			}
		}
		
		return modified;
	}
	
	// look for boxes in which some number has only a single option for placement
	// and place it there
	private int checkForUniqueCandidateByBox() {
		int modified = 0;
		
		List<Integer> toCheck = new ArrayList<Integer>();
		
		for (int searchedNum = 1; searchedNum < 10; searchedNum++) {
			// cycle through the boxes
			for (int boxRow = 0; boxRow < 3; boxRow++) {
				for (int boxCol = 0; boxCol < 3; boxCol++) {
					if (!boxOptions.get(boxRow * 3 + boxCol).contains(searchedNum)) continue;

					toCheck.clear();
					
					// search within the box
					for (int row = 0; row < 3; row++) {
						for (int col = 0; col < 3; col++) {
							// iterate through array of possibilities for the square				
							if (possibleMapTable.get((3 * boxRow) + row, (3 * boxCol) + col) != null
									&& possibleMapTable.get((3 * boxRow) + row, (3 * boxCol) + col).contains(searchedNum)) {
								// encodes each square in the box with a value
								// like:
								//		0 1 2
								//		3 4 5
								//		6 7 8
								// there's probably an easier way
								toCheck.add((3 * row) + col);
							}
						}
					}
					
					if (toCheck.size() == 1) { // TODO ? could also speed up if we did the trim check here
						// decodes the thing from right up there ^^
						setSolCell(((3 * boxRow) + (toCheck.get(0) / 3)), ((3 * boxCol) + (toCheck.get(0) % 3)), searchedNum);
						modified = 1;
					}
				}
			}
		}
		
		return modified;
	}
	
	// returns non-zero if the val being checked for is already present in the same row, col, or box
	// in a position different from the one being checked.
	private int checkVal(int row, int col, int val) {
		//check row and col
		for (int i = 0; i < 9; i++) {
			if (sol[row][i] == val && i != col) return 1;
			else if (sol[i][col] == val && i != row) return 2;
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (sol[((row / 3) * 3) + i][((col / 3) * 3) + j] == val
						&& ((row / 3) * 3) + i != row
						&& ((col / 3) * 3) + j != col) return 3;
			}
		}
		return 0;
	}
	
	// check each value to make sure the solution is correct
	private int checkSol() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (checkVal(i, j, sol[i][j]) != 0) {
					System.out.println("i: " + i + ", j: " + j + ", val: " + sol[i][j]);
					printSol();
					return 1;
				}
			}
		}
		return 0;
	}
	
	// for testing: print the current possibilities for a given cell
	private void printCellPoss(int row, int col) {
		System.out.print("Possibilites for Row " + row + ", Column " + col + ": ");
		
		if (possibleMapTable.get(row, col).isEmpty()) {
			System.out.print("None. Maybe it's already filled?");
		} else {
			System.out.print(possibleMapTable.get(row, col));
		}
		
		System.out.println();
	}
	
	// just prints the initial board
	private void printBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(boardStart[i][j] + " ");
				if (j == 2 || j == 5) System.out.print("| ");
			}
			System.out.println();
			if (i == 2 || i == 5) System.out.println("---------------------");
		}
		System.out.println();
	}

	// prints the working solution as is
	private void printSol() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(sol[i][j] + " ");
				if (j == 2 || j == 5) System.out.print("| ");
			}
			System.out.println();
			if (i == 2 || i == 5) System.out.println("---------------------");
		}
		System.out.println();
	}
	
	// print initial board and working solution side by side
	private void printInitandSol() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 18; j++) {
				if (j < 9) System.out.print(boardStart[i][j] + " ");
				else if (j == 9) {
					System.out.print("\t\t" + sol[i][j % 9] + " ");
				}
				else System.out.print(sol[i][j % 9] + " ");
				
				if (j == 2 || j == 5 || j == 11 || j == 14) System.out.print("| ");
			}
			System.out.println();
			if (i == 2 || i == 5) System.out.println("---------------------\t\t---------------------");
		}
		System.out.println();
	}

}
