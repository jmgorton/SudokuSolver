package sudokusolver;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

import java.util.*;
import java.io.*;

// make this work for nxn sudoku puzzles (n must be a square)
// to understand the moves, visit https://www.kristanix.com/sudokuepic/sudoku-solving-techniques.php
@SuppressWarnings("unused")
public class Board {
	
	// possible ways to declare board ... ?
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
	
	
	// working solution
	public int[][] sol = new int[9][9];
	
	
	// name self-explanatory. only need to be updated when a square is actually filled
	public Map<Integer, Set<Integer>> rowOptions = new HashMap<Integer, Set<Integer>>();
	public Map<Integer, Set<Integer>> colOptions = new HashMap<Integer, Set<Integer>>();
	// boxes are arranged like:
	//		1 2 3
	//		4 5 6
	//		7 8 9
	// like reading a book
	public Map<Integer, Set<Integer>> boxOptions = new HashMap<Integer, Set<Integer>>();
	
	
	// possible candidates for each cell
	// used google's HashBasedTable to achieve
	Table<Integer, Integer, Set<Integer>> possibleBySquare = HashBasedTable.create();
	
	// list of available locations to place a number
	// this map would be pretty big, but pretty useful
	// TODO i think these are fully maintained now, but we need to test
	// numberToPlace -> rowToPlaceNumber -> (List) columnsAvailableForNumber
	public Map<Integer, Map<Integer, Set<Integer>>> possibleCoordsByRow = 
			new HashMap<Integer, Map<Integer, Set<Integer>>>();
	// numberToPlace -> colToPlaceNumber -> (List) rowsAvailableForNumber
	public Map<Integer, Map<Integer, Set<Integer>>> possibleCoordsByCol = 
			new HashMap<Integer, Map<Integer, Set<Integer>>>();
//	public Map<Integer, List<Tuple<Integer, Integer>>> avail = 
//			new HashMap<Integer, List<Tuple<Integer, Integer>>>();
	// by box too?
	
	// constructor with given start board
	public Board(String boardData) {
		
		boardFile = boardData;
		List<Integer> all = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		
		for (int i = 0; i < 9; i++) {
			rowOptions.put(i, new HashSet<Integer>());
			rowOptions.get(i).addAll(all);
			colOptions.put(i, new HashSet<Integer>());
			colOptions.get(i).addAll(all);
			boxOptions.put(i, new HashSet<Integer>());
			boxOptions.get(i).addAll(all);
			
			possibleCoordsByRow.put(i, new HashMap<Integer, Set<Integer>>());
			possibleCoordsByCol.put(i, new HashMap<Integer, Set<Integer>>());
			
			for (int j = 0; j < 9; j++) {
				possibleCoordsByRow.get(i).put(j, new HashSet<Integer>());
				possibleCoordsByRow.get(i).get(j).addAll(all);
				possibleCoordsByCol.get(i).put(j, new HashSet<Integer>());
				possibleCoordsByCol.get(i).get(j).addAll(all);
			}
		}
		
		try {
			boardInit();
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist?");
			throw new RuntimeException(e);
		}
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
		Board current = b3;
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
		// each possibility must lead to a specific result for some other cell, or one choice leads to a contradiction
		
//		current.printBoard();
		
		// board b can be fully solved by either sole candidate or unique candidate alone

		int[] loop = new int[4];
		
		loop[3] = 1;
		outer:
		while (loop[3] != 0) {
			loop[2] = 1;
			while (loop[2] != 0) {
				loop[1] = 1;
				while (loop[1] != 0) {
					loop[0] = 1;
					while (loop[0] != 0) {
						current.printSol();
						loop[0] = current.checkForSoleCandidate();
						if (loop[0] != 0) System.out.println("Added some sole candidates");
						if (current.checkSol() == 0) break outer;
					}
					current.printSol();
					loop[1] = current.checkForUniqueCandidate();
					if (loop[1] != 0) System.out.println("Added some unique candidates");
					if (current.checkSol() == 0) break outer;
				}
				current.printSol();
				loop[2] = current.trimPossibleByBlock();
				if (loop[2] != 0) System.out.println("Did some trimming (block)");
				if (current.checkSol() == 0) break outer;
			}
			current.printSol();
			loop[3] = current.trimPossibleBySubset();
			if (loop[3] != 0) System.out.println("Did some trimming (subset)");
//			if (current.checkSol() == 0) break outer;
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
					possibleBySquare.put(i, j, new HashSet<Integer>());
				} else {
					rowOptions.get(i).remove(Integer.valueOf(next));
					colOptions.get(j).remove(Integer.valueOf(next));

					int row = i / 3;
					int col = j / 3;
					boxOptions.get((row * 3) + col).remove(Integer.valueOf(next));
					
					if (possibleCoordsByRow.get(next) != null && possibleCoordsByRow.get(next).get(i) != null) {
						possibleCoordsByRow.get(next).get(i).clear();
						possibleCoordsByRow.get(next).remove(Integer.valueOf(i));
						if (possibleCoordsByRow.get(next).size() == 0) {
							possibleCoordsByRow.remove(Integer.valueOf(next));
						}
					}
					if (possibleCoordsByCol.get(next) != null && possibleCoordsByCol.get(next).get(j) != null) {
						possibleCoordsByCol.get(next).get(j).clear();
						possibleCoordsByCol.get(next).remove(Integer.valueOf(j));
						if (possibleCoordsByCol.get(next).size() == 0) {
							possibleCoordsByCol.remove(Integer.valueOf(next));
						}
					}
				}
				
				// we're pretty much assuming the file is formatted correctly
				if (scanner.hasNextInt()) next = scanner.nextInt();
			}
		}
		
		scanner.close();
	}
	
	// set the working solution, maintain whatever other lists we're working with
	private void setSolCell(int row, int col, int val) {
		// TODO hit this one time?? but still got right answer, haven't tried to reproduce yet
		if (sol[row][col] == val) System.out.println("This could be a problem?");

		sol[row][col] = val;
		
		// now that we're using sets, all the times we used Integer.valueOf aren't necessary but oh well
		rowOptions.get(row).remove(Integer.valueOf(val));
		colOptions.get(col).remove(Integer.valueOf(val));
		boxOptions.get(((row / 3) * 3) + (col / 3)).remove(Integer.valueOf(val));
		
		trimPossible(row, col, val);
	}
	
	// TODO put trimming a cell into it's own function as well
	
	// determine what could possibly be stored in each square
	// just based on checking row, col, and box
	private int fillPossible() {
		// cycle through squares on the board
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// skip the square if the value has already been correctly set
				if (boardStart[i][j] != 0 || possibleBySquare.get(i, j) == null) continue;
				
				// populate what the square could possibly hold
				// simply based on checking row, col, and box
				for (int k = 1; k < 10; k++) {
					if(checkVal(i, j, k) == 0) {
						possibleBySquare.get(i, j).add(k);
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
			if (possibleBySquare.get(row, i) != null) possibleBySquare.get(row, i).remove(Integer.valueOf(val));
			if (possibleBySquare.get(i, col) != null) possibleBySquare.get(i, col).remove(Integer.valueOf(val));
		}
		// search within the box
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// iterate through array of possibilities for the square				
				if (possibleBySquare.get(((row / 3) * 3) + i, ((col / 3) * 3) + j) != null) {
					possibleBySquare.get(((row / 3) * 3) + i, ((col / 3) * 3) + j).remove(Integer.valueOf(val));
				}
			}
		}
		
		if (possibleBySquare.get(row, col) != null) {
			possibleBySquare.get(row, col).clear();
			possibleBySquare.remove(row, col);
		}
		
		if (possibleCoordsByRow.get(val) != null && possibleCoordsByRow.get(val).get(row) != null) {
			possibleCoordsByRow.get(val).get(row).clear();
			possibleCoordsByRow.get(val).remove(Integer.valueOf(row));
			if (possibleCoordsByRow.get(val).size() == 0) {
				possibleCoordsByRow.remove(Integer.valueOf(val));
			}
		}
		if (possibleCoordsByCol.get(val) != null && possibleCoordsByCol.get(val).get(col) != null) {
			possibleCoordsByCol.get(val).get(col).clear();
			possibleCoordsByCol.get(val).remove(Integer.valueOf(col));
			if (possibleCoordsByCol.get(val).size() == 0) {
				possibleCoordsByCol.remove(Integer.valueOf(val));
			}
		}
		return 0;
	}

	private int trimPossibleBySubset() {
		// TODO test
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
		
		// the somewhat tricky thing is: squares with subsets of the subset count too
		// i.e. three squares with [5, 7], [5, 7, 8], [5, 8] as options count as a triple
		// and the options 5, 7, and 8 should be removed from other squares
		
		// NOTE: there can never be a set of squares matching this criterion that has less possibilities than number of squares
		// i.e. there can't be three or more squares in any row, col, or box all containing only the possibilities [5, 7]
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// not correctly implemented
//				if (trimPossibleBySubsetNakedWithinRowCol(i, j, true) != 0) modified = 1;
//				if (trimPossibleBySubsetNakedWithinRowCol(j, i, false) != 0) modified = 2;
				
				rowPairs.clear();
				colPairs.clear();
				
				// check a row for pairs, triples, etc.
				if (possibleBySquare.get(i, j) != null) {
					int possibilitiesInSquare = possibleBySquare.get(i, j).size();
					int squaresWithSubsetPossibilities = 1;
					rowPairs.add(j);
					
					for (int k = 0; k < 9; k++) {
						if (k == j) continue;
						if (possibleBySquare.get(i, j).containsAll(possibleBySquare.get(i, k))) {
							squaresWithSubsetPossibilities++;
							rowPairs.add(k);
						}
						// should be able to break; after reaching squaresWithSubsetPossibilities == possibilitiesInSquare
					}
					
					// a valid pair/triple/etc was found. now remove those options from other squares in the row
					if (squaresWithSubsetPossibilities == possibilitiesInSquare) {
						// now rowPairs contains the columns in row i which hold the naked pair/triple/etc squares
						Set<Integer> subsetToRemove = possibleBySquare.get(i, j);
						for (int k = 0; k < 9; k++) {
							// a square that was a member of the pair/triple/etc should not be modified
							if (rowPairs.contains(k)) continue;
							// a square not a member of the pair/triple/etc should no longer have those options
							if (possibleBySquare.get(i, k) != null) {
								if (possibleBySquare.get(i, k).removeAll(subsetToRemove)) {
									modified = 1;
									for (Integer toRemove : subsetToRemove) {
										if (possibleCoordsByRow.containsKey(toRemove)
												&& possibleCoordsByRow.get(toRemove).containsKey(i)) {
											possibleCoordsByRow.get(toRemove).get(i).remove(k);
										}
										if (possibleCoordsByCol.containsKey(toRemove)
												&& possibleCoordsByCol.get(toRemove).containsKey(k)) {
											possibleCoordsByCol.get(toRemove).get(k).remove(i);
										}
									}
								}
							}
						}
					}
					
					// TODO after testing we can prob remove this
					if (squaresWithSubsetPossibilities > possibilitiesInSquare) {
						System.out.println("I don't think this should be possible -- ERROR");
					}
				}
				
				// same check as above but for cols
				if (possibleBySquare.get(j, i) != null) {
					int possibilitiesInSquare = possibleBySquare.get(j, i).size();
					int squaresWithSubsetPossibilities = 1;
					colPairs.add(j);
					
					for (int k = 0; k < 9; k++) {
						if (k == j) continue;
						if (possibleBySquare.get(j, i).containsAll(possibleBySquare.get(k, i))) {
							squaresWithSubsetPossibilities++;
							colPairs.add(k);
						}
						// should be able to break; after reaching squaresWithSubsetPossibilities == possibilitiesInSquare
					}
					
					// a valid pair/triple/etc was found. now remove those options from other squares in the col
					if (squaresWithSubsetPossibilities == possibilitiesInSquare) {
						// now colPairs contains the rows in col i which hold the naked pair/triple/etc squares
						Set<Integer> subsetToRemove = possibleBySquare.get(j, i);
						for (int k = 0; k < 9; k++) {
							// a square that was a member of the pair/triple/etc should not be modified
							if (colPairs.contains(k)) continue;
							// a square not a member of the pair/triple/etc should no longer have those options
							if (possibleBySquare.get(k, i) != null) {
								if (possibleBySquare.get(k, i).removeAll(subsetToRemove)) {
									modified = 1;
									for (Integer toRemove : subsetToRemove) {
										if (possibleCoordsByRow.containsKey(toRemove)
												&& possibleCoordsByRow.get(toRemove).containsKey(k)) {
											possibleCoordsByRow.get(toRemove).get(k).remove(i);
										}
										if (possibleCoordsByCol.containsKey(toRemove)
												&& possibleCoordsByCol.get(toRemove).containsKey(i)) {
											possibleCoordsByCol.get(toRemove).get(i).remove(k);
										}
									}
								}
							}
						}
					}
					
					// TODO after testing we can prob remove this
					if (squaresWithSubsetPossibilities > possibilitiesInSquare) {
						System.out.println("I don't think this should be possible -- ERROR");
					}
				}
			}
		}
		
		return modified;
	}
	
	// might work now?
	private int trimPossibleBySubsetNakedWithinRowCol(int row, int col, boolean byRow) {
		int modified = 0;
		
		List<Integer> pairs = new ArrayList<Integer>();
		
		// check a row for pairs, triples, etc.
		if (possibleBySquare.get(row, col) != null) {
			int possibilitiesInSquare = possibleBySquare.get(row, col).size();
			int squaresWithSubsetPossibilities = 1;
			
			if (byRow) pairs.add(col);
			else pairs.add(row);
			
			for (int k = 0; k < 9; k++) {
				if ((byRow && k == col) || (!byRow && k == row)) continue;
				if (byRow && possibleBySquare.get(row, col).containsAll(possibleBySquare.get(row, k))) {
					squaresWithSubsetPossibilities++;
					pairs.add(k);
				} else if (!byRow && possibleBySquare.get(row, col).containsAll(possibleBySquare.get(k, col))) {
					squaresWithSubsetPossibilities++;
					pairs.add(k);
				}
				// should be able to break; after reaching squaresWithSubsetPossibilities == possibilitiesInSquare
			}
			
			// a valid pair/triple/etc was found. now remove those options from other squares in the row
			if (squaresWithSubsetPossibilities == possibilitiesInSquare) {
				// now rowPairs contains the columns in row i which hold the naked pair/triple/etc squares
				Set<Integer> subsetToRemove = possibleBySquare.get(row, col);
				for (int k = 0; k < 9; k++) {
					// a square that was a member of the pair/triple/etc should not be modified
					if (pairs.contains(k)) continue;
					// a square not a member of the pair/triple/etc should no longer have those options
					if (byRow && possibleBySquare.get(row, k) != null) {
						if (possibleBySquare.get(row, k).removeAll(subsetToRemove)) {
							modified = 1;
							for (Integer toRemove : subsetToRemove) {
								if (possibleCoordsByRow.containsKey(toRemove)
										&& possibleCoordsByRow.get(toRemove).containsKey(row)) {
									possibleCoordsByRow.get(toRemove).get(row).remove(k);
								}
								if (possibleCoordsByCol.containsKey(toRemove)
										&& possibleCoordsByCol.get(toRemove).containsKey(k)) {
									possibleCoordsByCol.get(toRemove).get(k).remove(row);
								}
							}
						}
					} else if (!byRow && possibleBySquare.get(k, col) != null) {
						if (possibleBySquare.get(k, col).removeAll(subsetToRemove)) {
							modified = 1;
							for (Integer toRemove : subsetToRemove) {
								if (possibleCoordsByRow.containsKey(toRemove)
										&& possibleCoordsByRow.get(toRemove).containsKey(k)) {
									possibleCoordsByRow.get(toRemove).get(k).remove(col);
								}
								if (possibleCoordsByCol.containsKey(toRemove)
										&& possibleCoordsByCol.get(toRemove).containsKey(col)) {
									possibleCoordsByCol.get(toRemove).get(col).remove(k);
								}
							}
						}
					}
				}
			}
			
			// TODO after testing we can prob remove this
			if (squaresWithSubsetPossibilities > possibilitiesInSquare) {
				System.out.println("I don't think this should be possible -- ERROR");
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
		
		List<Integer> rowPairs = new ArrayList<Integer>();
		List<Integer> colPairs = new ArrayList<Integer>();
		Map<Integer, List<Integer>> optionLocations = new HashMap<Integer, List<Integer>>();
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
//				if (trimPossibleBySubsetHiddenWithinRowCol(i, j, true) != 0) modified = 1;
//				if (trimPossibleBySubsetHiddenWithinRowCol(j, i, false) != 0) modified = 2;
				
				rowPairs.clear();
				colPairs.clear();
				
//				if (possibleBySquare.get(i, j) != null) {
//
//				}
				for (Integer opt : rowOptions.get(i)) {
					
				}
			}
		}
		
		return modified;
	}
	
	// not working correctly yet.
	private int trimPossibleBySubsetHiddenWithinRowCol(int row, int col, boolean byRow) {
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
							if (possibleBySquare.get((3 * boxRow) + row, (3 * boxCol) + col) != null
									&& possibleBySquare.get((3 * boxRow) + row, (3 * boxCol) + col).contains(searchedNum)) {
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
								if (possibleBySquare.get((boxRow * 3) + row, i) != null
										&& possibleBySquare.get((boxRow * 3) + row, i).contains(searchedNum)) {
									possibleBySquare.get((boxRow * 3) + row, i).remove(Integer.valueOf(searchedNum));
									possibleCoordsByRow.get(searchedNum).get((boxRow * 3) + row).remove(i);
									possibleCoordsByCol.get(searchedNum).get(i).remove((boxRow * 3) + row);
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
								if (possibleBySquare.get(i, (boxCol * 3) + col) != null
										&& possibleBySquare.get(i, (boxCol * 3) + col).contains(searchedNum)) {
									possibleBySquare.get(i, (boxCol * 3) + col).remove(Integer.valueOf(searchedNum));
									possibleCoordsByRow.get(searchedNum).get(i).remove((boxCol * 3) + col);
									possibleCoordsByCol.get(searchedNum).get((boxCol * 3) + col).remove(i);
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
						if (possibleBySquare.get(i, j) != null && possibleBySquare.get(i, j).contains(searchedNum)) {
							toCheckRow.add(j);
						}
					}
				}
				if (colOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleBySquare.get(j, i) != null && possibleBySquare.get(j, i).contains(searchedNum)) {
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
								if (possibleBySquare.get(((i / 3) * 3) + _i, (block * 3) + _j) != null 
										&& possibleBySquare.get(((i / 3) * 3) + _i, (block * 3) + _j).contains(searchedNum)) {
									possibleBySquare.get(((i / 3) * 3) + _i, (block * 3) + _j).remove(Integer.valueOf(searchedNum));
									possibleCoordsByRow.get(searchedNum).get(((i / 3) * 3) + _i).remove((block * 3) + _j);
									possibleCoordsByCol.get(searchedNum).get((block * 3) + _j).remove(((i / 3) * 3) + _i);
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
								if (possibleBySquare.get((block * 3) + _j, ((i / 3) * 3) + _i) != null 
										&& possibleBySquare.get((block * 3) + _j, ((i / 3) * 3) + _i).contains(searchedNum)) {
									possibleBySquare.get((block * 3) + _j, ((i / 3) * 3) + _i).remove(Integer.valueOf(searchedNum));
									possibleCoordsByRow.get(searchedNum).get((block * 3) + _j).remove(((i / 3) * 3) + _i);
									possibleCoordsByCol.get(searchedNum).get(((i / 3) * 3) + _i).remove((block * 3) + _j);
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
				if (sol[i][j] == 0 && possibleBySquare.get(i, j).size() == 1) {
//					setSolCell(i, j, possibleBySquare.get(i, j).get(0));
					setSolCell(i, j, possibleBySquare.get(i, j).iterator().next());
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
						if (possibleBySquare.get(i, j) != null && possibleBySquare.get(i, j).contains(searchedNum)) {
							toCheckRow.add(j);
						}
					}
				}
				if (colOptions.get(i).contains(searchedNum)) {
					for (int j = 0; j < 9; j++) {
						if (possibleBySquare.get(j, i) != null && possibleBySquare.get(j, i).contains(searchedNum)) {
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
							if (possibleBySquare.get((3 * boxRow) + row, (3 * boxCol) + col) != null
									&& possibleBySquare.get((3 * boxRow) + row, (3 * boxCol) + col).contains(searchedNum)) {
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
		
		if (possibleBySquare.get(row, col).isEmpty()) {
			System.out.print("None. Maybe it's already filled?");
		} else {
			System.out.print(possibleBySquare.get(row, col));
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
