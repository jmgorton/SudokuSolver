package sudokusolver;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import java.util.*;
import java.io.*;

// make this work for nxn sudoku puzzles (n must be a square)
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
	
	// not used yet
	public int[][] rowOptions = new int[9][9];	
//	public static Map<Integer, List<Integer>> rowOptions = new HashMap<Integer, List<Integer>>();
	public int[][] colOptions = new int[9][9];	
//	public static Map<Integer, List<Integer>> colOptions = new HashMap<Integer, List<Integer>>();
	public int[][] boxOptions = new int[9][9];
//	public static Map<Integer, List<Integer>> boxOptions = new HashMap<Integer, List<Integer>>();
	
	// working solution
	public int[][] sol = new int[9][9];
	// possible candidates for each cell
//	public int[][][] possible = new int[9][9][9];
	// achieve same as possible array but maybe more cleanly. map a coordinate to a list of possibilities
	// would need to keep a reference of each tuple instead of making new ones to use as keys, seems bad
//	public Map<Tuple<Integer, Integer>, List<Integer>> possibleMap = 
//			new HashMap<Tuple<Integer, Integer>, List<Integer>>();
	// use google's HashBasedTable to achieve same thing
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
	
	// instead of try-catch, could just throw FNF exception here
	public static void main(String[] args) {
//		String s = "Users/jaredgorton/eclipse-workspace/SudokuSolver/src/sudokusolver/sudokuboard.txt";
		String s = "./src/sudokusolver/sudokuboard.txt";	// project root directory base
		Board b = new Board(s);
		
		try {
			b.boardInit();
		}
		catch (FileNotFoundException e) {
			System.out.println("File does not exist. Exiting.\n");
			return;
		}
		
		Tuple<Integer, Integer> t = new Tuple<Integer, Integer>(1, 2);
//		if (b.possibleMap.get(t) == null) {
		if (b.possibleMapTable.get(1, 2) == null) {
			System.out.println("This is a problem");
		}
		
		// print out the starting board
//		b.printBoard();
		// match working solution to initial board state. now handled in boardInit()
//		b.solInit();
		
		// determine what could possibly be stored in each square
		// just based on checking row, col, and box
		b.fillPossible();
		
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
				
		
		
		int loop = 1;
		while (loop == 1) {
			loop = b.checkForSoleCandidate();
//			System.out.println("\n");
//			b.printSol();
		}
		
//		b.printSol();
		System.out.println("Solution is " + (b.checkSol() == 0 ? "correct." : "incorrect or incomplete."));
		
		// CHECK SOLUTIONS
//		b.printSol();
//		System.out.println();
		
//		b.printInitandSol();
		
	}
	
	private void boardInit() throws FileNotFoundException {
		File f = new File(boardFile);
		
		Scanner scanner = null;
		try {
			if (f.exists()) scanner = new Scanner(f);
			else return;
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
				if (next != 0) {
//					possible[i][j][0] = -1;
				} else {
//					for (int k = 0; k < 9; k++) {
//						possible[i][j][k] = 0;
//					}
//					Tuple<Integer, Integer> t = new Tuple<Integer, Integer>(i, j);
					// NOTE: must put t into the map. NOT a different Tuple made from i, j. Take care when using
					// mutable objects in a map, as the javadoc warns. this might cause problems later -- update: it did
//					possibleMap.put(t, new ArrayList<Integer>());
					possibleMapTable.put(i, j, new ArrayList<Integer>());
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
//				if (boardStart[i][j] != 0) possible[i][j][0] = -1;
			}
		}
	}
	
	private int fillPossible() {
		// cycle through squares on the board
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// skip the square if the value has already been correctly set
//				if (boardStart[i][j] != 0 || possible[i][j][0] == -1) continue;
				if (boardStart[i][j] != 0 || possibleMapTable.get(i, j) == null) continue;
				
				// populate what the square could possibly hold
				// simply based on checking row, col, and box
				for (int k = 1; k < 10; k++) {
					if(checkVal(i, j, k) == 0) {
//						int x = 0;
//						while (possible[i][j][x] > 0) x++;
//						possible[i][j][x] = k;
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
		//check row and col
		int replacingC = 0, replacingR = 0;
		int doneC = 0, doneR = 0;
		// search row and col
		// iterate through row/col of the modified square
		for (int i = 0; i < 9; i++) {
			// replacingR, replacingC get set after we've found an element of possible that needs to be trimmed
			// doneR, doneC are set after we've finished iterating through the array of possible candidates
//			replacingR = 0;
//			replacingC = 0;
//			doneR = 0;
//			doneC = 0;
			// check values within possible array. would be way easier with lists
//			for (int j = 0; j < 9; j++) {
//				// finish after doneR and doneC are both 1 also
//				if (doneR == 1 && doneC == 1) break;
//				
//				// when we find an element to replace, we delete and begin shifting elements down
//				// to fill the empty (0) space in the array, maintaining order -- although not necessary
//				if (possible[row][i][j] == val) replacingR = 1;
//				else if (possible[row][i][j] == 0) doneR = 1;
//				// by checking doneR, we improve efficiency, but don't affect correctness
//				if (replacingR == 1 & doneR == 0) {
//					if (j == 8) possible[row][i][j] = 0;
//					else possible[row][i][j] = possible[row][i][j + 1];
//				}
//				
//				// identical to above but for col instead of row
//				if (possible[i][col][j] == val) replacingC = 1;
//				else if (possible[i][col][j] == 0) doneC = 1;
//				if (replacingC == 1 & doneC == 0) {
//					if (j == 8) possible[i][col][j] = 0;
//					else possible[i][col][j] = possible[i][col][j + 1];
//				}
//			}
			
			// these two lines do what that whole for loop does
			// NOTE: if val is not an element of that list, remove does not modify the list
			if (possibleMapTable.get(row, i) != null) possibleMapTable.get(row, i).remove(Integer.valueOf(val));
			if (possibleMapTable.get(i, col) != null) possibleMapTable.get(i, col).remove(Integer.valueOf(val));
		}
		// search within the box
		int searchB = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				searchB = 0;
				// iterate through array of possibilities for the square
//				for (int k = 0; k < 9; k++) {
//					// do the same thing as above, except we don't have to wait for corresponding row/col to be done
//					if (possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] == val) searchB = 1;
//					else if (possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] == 0) break;
//					if (searchB == 1) {
//						if (k == 8) possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] = 0;
//						else possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] = 
//								possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k + 1];
//					}
//				}
				
				// this line does what that for loop does
				if (possibleMapTable.get(((row / 3) * 3) + i, ((col / 3) * 3) + j) != null) {
					possibleMapTable.get(((row / 3) * 3) + i, ((col / 3) * 3) + j).remove(Integer.valueOf(val));
				}
			}
		}
		return 0;
	}
	
	// look for squares where only 1 possible value could fill the square
	// just based on checking rows, cols, and boxes
	private int checkForSoleCandidate() {
		int modified = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// sol hasn't been filled in yet, but there is only 1 possibility for this square.
//				if (sol[i][j] == 0 && possible[i][j][1] == 0) {
				if (sol[i][j] == 0 && possibleMapTable.get(i, j).size() == 1) {
//					sol[i][j] = possible[i][j][0];
					sol[i][j] = possibleMapTable.get(i, j).get(0);
					modified = 1;
//					trimPossible(i, j, possible[i][j][0]);
					trimPossible(i, j, possibleMapTable.get(i, j).get(0));
				}
			}
		}
		return modified;
	}
	
	private int checkForUniqueCandidate() {
		int modified = 0;
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
	
	// for testing: print the current possibilities for a given cell
	private void printCellPoss(int row, int col) {
//		System.out.print("Possibilites for Row " + row + ", Column " + col + ": " + possible[row][col][0]);
		System.out.print("Possibilites for Row " + row + ", Column " + col + ": ");
		
//		int x = 1;
//		while (possible[row][col][x] > 0) {
//			System.out.print(", " + possible[row][col][x++]);
//		}
		
		if (possibleMapTable.get(row, col).isEmpty()) {
			System.out.print("None. Maybe it's already filled?");
		} else {
			System.out.print(possibleMapTable.get(row, col));
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
