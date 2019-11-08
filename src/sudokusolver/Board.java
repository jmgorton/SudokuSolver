package sudokusolver;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

// make this work for nxn sudoku puzzles (n must be a square i think)
public class Board {
	
	// possible ways to initialize board ??
//	List<List<Integer>> board = new ArrayList<List<Integer>>();
//	ArrayList<Integer>[][] board = (ArrayList<Integer>[][])new ArrayList[9][9];
//	public static final int[][] board = new int[9][9];
	
	int[][] boardStart = new int[9][9];
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
	
	public int[][] rowOptions = new int[9][9];	
//	public static Map<Integer, List<Integer>> rowOptions = new HashMap<Integer, List<Integer>>();
	public int[][] colOptions = new int[9][9];	
//	public static Map<Integer, List<Integer>> colOptions = new HashMap<Integer, List<Integer>>();
	public int[][] boxOptions = new int[9][9];
//	public static Map<Integer, List<Integer>> boxOptions = new HashMap<Integer, List<Integer>>();
	
	// working solution
	public int[][] sol = new int[9][9];
	// possible candidates for each cell
	public int[][][] possible = new int[9][9][9];	// a list would probably be more convenient
//	public Map<Tuple<Integer, Integer>, List<Integer>> possible = 
//			new HashMap<Tuple<Integer, Integer>, List<Integer>>();
	
	
	
	// list of coordinates of existing locations for each number
	public Map<Integer, List<Tuple<Integer, Integer>>> coords = 
			new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	// list of available coordinates???
//	public Map<Integer, List<Tuple<Integer, Integer>>> avail = 
//			new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	
	// if i want to include notes
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
		
		b.printBoard();
		b.solInit();
		b.fillPossible();
		
//		b.printCellPoss(0, 0);
//		b.printCellPoss(0, 4);
//		b.printCellPoss(0, 7);
//		b.printCellPoss(4, 8);
//		System.out.println();
		
//		@SuppressWarnings("unchecked")
		Tuple<Integer, Integer> t = new Tuple<Integer, Integer>(1, 1);
		
		
		b.logicP1();
		
//		b.checkPossible();
//		b.printSol();
		
//		b.printCellPoss(2, 0);
		
		
		// CHECKs INIT -- GOOD
//		b.printBoard();
////		printSol();
//		b.printSol();
		///////////////
		
		int loop = 1;
		while (loop == 1) {
			loop = b.checkPossible();
//			System.out.println("\n");
//			b.printSol();
		}
		
		b.printSol();
		
		// CHECK SOLUTIONS
//		printSol();
//		System.out.println();
//		printLogicSol();
//		System.out.println();
		
		//printInitandSol();
		
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
				if (scanner.hasNextInt()) next = scanner.nextInt();
			}
		}
		
		scanner.close();
	}
	
	private void solInit() {
		//sol = board;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sol[i][j] = boardStart[i][j];
				if (boardStart[i][j] != 0) possible[i][j][0] = -1;
				//possible[i][j][0] = board[i][j];
			}
		}
	}
	
	private int fillPossible() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (boardStart[i][j] != 0) continue;	// does this assure we skip squares w -1 in possible?
				for (int k = 1; k < 10; k++) {
					if(checkSol(i, j, k) == 0) {
						int x = 0;
						while (possible[i][j][x] > 0) x++;
						possible[i][j][x] = k;
					}
				}
			}
		}
		return 0;
	}
	
	private int checkPossible() {
		int modified = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sol[i][j] == 0 && possible[i][j][1] == 0) {
					sol[i][j] = possible[i][j][0];	// potential to overwrite previous logicSol value...
														// but not sure if it's possible to overwrite incorrectly
					modified = 1;
					trimPossible(i, j, possible[i][j][0]);
				}
			}
		}
		return modified;
	}
	
	private int trimPossible(int row, int col, int val) {
		//check row and col
		int replacingC = 0, replacingR = 0; // for efficiency
		int doneC = 0, doneR = 0;
		for (int i = 0; i < 9; i++) {
			replacingR = 0;
			replacingC = 0;
			doneR = 0;
			doneC = 0;
			for (int j = 0; j < 9; j++) { // finish after doneR and doneC are both 1 also
				if (doneR == 1 && doneC == 1) break;
				if (possible[row][i][j] == val) replacingR = 1;
				else if (possible[row][i][j] == 0) doneR = 1;
				if (replacingR == 1 & doneR == 0) { // check of done may be redundant
					if (j == 8) possible[row][i][j] = 0;
					else possible[row][i][j] = possible[row][i][j + 1];
				}
				if (possible[i][col][j] == val) replacingC = 1;
				else if (possible[i][col][j] == 0) doneC = 1;
				if (replacingC == 1 & doneC == 0) {
					if (j == 8) possible[i][col][j] = 0;
					else possible[i][col][j] = possible[i][col][j + 1];
				}
			}
		}
		int searchB = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				searchB = 0;
				for (int k = 0; k < 9; k++) {
					if (possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] == val) searchB = 1;
					else if (possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] == 0) break;
					if (searchB == 1) {
						if (k == 8) possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] = 0;
						else possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k] = 
								possible[((row / 3) * 3) + i][((col / 3) * 3) + j][k + 1];
					}
				}
			}
		}
		return 0;
	}
	
	private int logicP1() {
		return 0;
	}
	
	private int checkSol(int row, int col, int val) {
		//check row and col
		for (int i = 0; i < 9; i++) {
			if (sol[row][i] == val) return 1;
			else if (sol[i][col] == val) return 2;
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (sol[((row / 3) * 3) + i][((col / 3) * 3) + j] == val) return 3;
			}
		}
		return 0;
	}
	
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
	
	private void printCellPoss(int row, int col) {
		System.out.print("Possibilites for Row " + row + ", Column " + col + ": " + possible[row][col][0]);
		
		int x = 1;
		while (possible[row][col][x] > 0) {
			System.out.print(", " + possible[row][col][x++]);
		}
		
		System.out.println();
	}
	
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
