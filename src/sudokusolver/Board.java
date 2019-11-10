package sudokusolver;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import java.util.*;
import java.io.*;

// make this work for nxn sudoku puzzles (n must be a square)
// to understand the moves, visit https://www.kristanix.com/sudokuepic/sudoku-solving-techniques.php
@SuppressWarnings("unused")
public class Board {
	
	// possible ways to initialize board ??
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
	
	// not used yet. name self-explanatory. still haven't decided if these are useful
	// not yet fully maintained through the life-cycle of the program.
//	public int[][] rowOptions = new int[9][9];	
	public Map<Integer, List<Integer>> rowOptions = new HashMap<Integer, List<Integer>>();
//	public int[][] colOptions = new int[9][9];	
	public Map<Integer, List<Integer>> colOptions = new HashMap<Integer, List<Integer>>();
//	public int[][] boxOptions = new int[9][9];
	public Map<Integer, List<Integer>> boxOptions = new HashMap<Integer, List<Integer>>();
	
	// working solution
	public int[][] sol = new int[9][9];
	// possible candidates for each cell
	// used google's HashBasedTable to achieve
	Table<Integer, Integer, List<Integer>> possibleMapTable = HashBasedTable.create();
	
	
	
	// list of coordinates of existing locations for each number
	// not used yet
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
		
//		List<Integer> all = new ArrayList<Integer>();
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
//		String s = "Users/jaredgorton/eclipse-workspace/SudokuSolver/src/sudokusolver/sudokuboard.txt";
		String s = "./src/sudokusolver/board-easy-1.txt";	// project root directory base
		Board b = new Board(s);
		
		String s2 = "./src/sudokusolver/board-med-1.txt";
		Board b2 = new Board(s2);
		
		// this one introduces a bug!!
		String s3 = "./src/sudokusolver/board-hard-1.txt";
		Board b3 = new Board(s3);
				
		// print out the starting board
//		b.printBoard();
		// match working solution to initial board state. now handled in boardInit()
//		b.solInit();
		
		// determine what could possibly be stored in each square
		// just based on checking row, col, and box
		b.fillPossible();
		b2.fillPossible();
		b3.fillPossible();
		
		
//		b.printCellPoss(0, 0);
//		b.printCellPoss(0, 4);
//		b.printCellPoss(0, 7);
//		b.printCellPoss(4, 8);
//		System.out.println();
		
		// CHECKs INIT -- GOOD
//		b.printBoard();
//		b.printSol();
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
		
//		b.printBoard();
		
		boolean loopB = true;		
		// board b can be fully solved by either method alone

		int innerLoop1;
		int innerLoop2;
		int innerLoop3;
		int[] loop = new int[3];
		
		innerLoop3 = 1;
		while (innerLoop3 == 1) {
			innerLoop2 = 1;
			while (innerLoop2 == 1) {
				innerLoop1 = 1;
				while (innerLoop1 == 1) {
//					b3.printSol();
					innerLoop1 = b3.checkForSoleCandidate();
					if (innerLoop1 == 1) System.out.println("Added some sole candidates");
//					System.out.println("Added some sole candidates");
				}
//				b3.printSol();
				innerLoop2 = b3.checkForUniqueCandidate();
				if (innerLoop2 == 1) System.out.println("Added some unique candidates");
//				System.out.println("Added some unique candidates");
			}
//			b3.printSol();
			innerLoop3 = b3.trimPossibleByBlock();
			if (innerLoop3 == 1) System.out.println("Did some trimming (block)");
//			System.out.println("Did some trimming (block)");
		}
		
		// CHECK SOLUTIONS
//		b.printSol();
//		System.out.println();
		
//		System.out.println("Solution is " + (b.checkSol() == 0 ? "correct." : "incorrect or incomplete."));
//		System.out.println("Solution is " + (b2.checkSol() == 0 ? "correct." : "incorrect or incomplete."));
		System.out.println("Solution is " + (b3.checkSol() == 0 ? "correct." : "incorrect or incomplete."));
		
//		b.printInitandSol();
		
	}
	
	// TODO make the return types smart
	
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
//				possibleMapTable.put(i, j, new ArrayList<Integer>());
				if (next == 0) {
					possibleMapTable.put(i, j, new ArrayList<Integer>());
				} else {
					rowOptions.get(i).remove(Integer.valueOf(next));
					colOptions.get(j).remove(Integer.valueOf(next));
					// figure this out later
//					boxOptions
				}
				if (scanner.hasNextInt()) next = scanner.nextInt();
			}
		}
		
		scanner.close();
	}
	
	// not used anymore
	private void solInit() {
		//sol = board;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sol[i][j] = boardStart[i][j];
			}
		}
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
	
	// likely, the solution has been modified and now we have to update the
	// possibility array for other boxes. only updates within row, col, or box
	private int trimPossible(int row, int col, int val) {
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

	private int trimPossibleByBlock() {
		int modified = 0;
		
		if (trimPossibleByBlockPositive() != 0) modified = 1;
		if (trimPossibleByBlockNegative() != 0) modified = 1;
		
		return modified;
	}
	
	// if, for some number, all the candidates within a certain block are within the same row or col,
	// that number within that block will be in that row or col,
	// and any other candidates for that number in that row or col outside of the block can be removed
	private int trimPossibleByBlockPositive() {
		int modified = 0;
		
		
		
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
				for (int j = 0; j < 9; j++) {
					if (possibleMapTable.get(i, j) != null && possibleMapTable.get(i, j).contains(searchedNum)) {
						toCheckRow.add(j);
					}
					if (possibleMapTable.get(j, i) != null && possibleMapTable.get(j, i).contains(searchedNum)) {
						toCheckCol.add(j);
					}
				}
				
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
					if (block != 1) {
						for (int _i = 0; _i < 3; _i++) {
							// i is the row to avoid in this case. i is the row that we are preserving the possible map
							if (_i == i % 3) continue;
							for (int _j = 0; _j < 3; _j++) {
								if (possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j) != null 
										&& possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j).contains(searchedNum)) {
									possibleMapTable.get(((i / 3) * 3) + _i, (block * 3) + _j).remove(searchedNum);
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
					if (block != 1) {
						for (int _i = 0; _i < 3; _i++) {
							// i is the col to avoid in this case. i is the col that we are preserving the possible map
							if (_i == i % 3) continue;
							for (int _j = 0; _j < 3; _j++) {
								if (possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i) != null 
										&& possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i).contains(searchedNum)) {
									possibleMapTable.get((block * 3) + _j, ((i / 3) * 3) + _i).remove(searchedNum);
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
					sol[i][j] = possibleMapTable.get(i, j).get(0);
					modified = 1;
					trimPossible(i, j, possibleMapTable.get(i, j).get(0));
				}
			}
		}
		return modified;
	}
	
	// look for rows, cols, and boxes in which some number has only a single option for placement
	// and place it there
	// this has some bugs, uncovered with board-hard-1
	private int checkForUniqueCandidate() {
		int modified = 0;
		
		printSol();
		if (checkForUniqueCandidateByRowCol() != 0) modified = 1; // after this stage, it is correct, but how?
		printSol();
		if (checkForUniqueCandidateByBox() != 0) modified = 1; // after this stage, it is incorrect
		printSol();
		
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
				for (int j = 0; j < 9; j++) {
					if (possibleMapTable.get(i, j) != null && possibleMapTable.get(i, j).contains(searchedNum)) {
						toCheckRow.add(j);
					}
					if (possibleMapTable.get(j, i) != null && possibleMapTable.get(j, i).contains(searchedNum)) {
						toCheckCol.add(j);
					}
				}
				if (toCheckRow.size() == 1) {
					sol[i][toCheckRow.get(0)] = searchedNum;
					modified = 1;
					trimPossible(i, toCheckRow.get(0), searchedNum);
				}
				if (toCheckCol.size() == 1) {
					sol[toCheckCol.get(0)][i] = searchedNum;
					modified = 1;
					trimPossible(toCheckCol.get(0), i, searchedNum);
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
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					toCheck.clear();
					
					// search within the box
					for (int row = 0; row < 3; row++) {
						for (int col = 0; col < 3; col++) {
							// iterate through array of possibilities for the square				
							if (possibleMapTable.get((3 * i) + row, (3 * j) + col) != null
									&& possibleMapTable.get((3 * i) + row, (3 * j) + col).contains(searchedNum)) {
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
					
					if (toCheck.size() == 1) {
						// decodes the thing from right up there ^^
						sol[(3 * i) + (toCheck.get(0) % 3)][(3 * j) + (toCheck.get(0) / 3)] = searchedNum;
						modified = 1;
						trimPossible((3 * i) + (toCheck.get(0) % 3), (3 * j) + (toCheck.get(0) / 3), searchedNum);
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
