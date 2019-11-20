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
		List<Integer> allFromZero = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
		
		for (int i = 0; i < 9; i++) {
			rowOptions.put(i, new HashSet<Integer>());
			rowOptions.get(i).addAll(all);
			colOptions.put(i, new HashSet<Integer>());
			colOptions.get(i).addAll(all);
			boxOptions.put(i, new HashSet<Integer>());
			boxOptions.get(i).addAll(all);
			
			possibleCoordsByRow.put(i + 1, new HashMap<Integer, Set<Integer>>());
			possibleCoordsByCol.put(i + 1, new HashMap<Integer, Set<Integer>>());
			
			for (int j = 0; j < 9; j++) {
				possibleCoordsByRow.get(i + 1).put(j, new HashSet<Integer>());
				possibleCoordsByRow.get(i + 1).get(j).addAll(allFromZero);
				possibleCoordsByCol.get(i + 1).put(j, new HashSet<Integer>());
				possibleCoordsByCol.get(i + 1).get(j).addAll(allFromZero);
				
				possibleBySquare.put(i, j, new HashSet<Integer>());
				possibleBySquare.get(i, j).addAll(all);
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
		Board current = b4;
//		Board current = new Board(s);
		
		
		
		
		// print out the starting board
		current.printBoard();
		
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

				if (next == 0) {
					sol[i][j] = next;
				} else {
					setSolCell(i, j, next);
				}
				
				// we're pretty much assuming the file is formatted correctly
				if (scanner.hasNextInt()) next = scanner.nextInt();
			}
		}
		
		scanner.close();
	}
	
	// set the working solution, maintain whatever other lists we're working with
	private void setSolCell(int row, int col, int val) {
		if (sol[row][col] == val) {
//			System.out.println("This could be a problem? -- no, it occurs when a RowCol method finds a value by both row and col");
//			System.out.println("Tried to place " + val + " in row " + row + ", col " + col + ", but it was already there.");
			return;
		}

		sol[row][col] = val;
		
		// now that we're using sets, all the times we used Integer.valueOf aren't necessary but oh well
		rowOptions.get(row).remove(Integer.valueOf(val));
		colOptions.get(col).remove(Integer.valueOf(val));
		boxOptions.get(((row / 3) * 3) + (col / 3)).remove(Integer.valueOf(val));
		
		trimPossibleAfterEntry(row, col, val);
	}
	
	// remove a single option from the possible lists/sets we are maintaining
	private int trimPossibleNoEntry(int row, int col, int toRemove) {
		possibleBySquare.get(row, col).remove(toRemove);
		possibleCoordsByRow.get(toRemove).get(row).remove(col);
		possibleCoordsByCol.get(toRemove).get(col).remove(row);
		
		return 1;
	}
	
	// remove a set of elements from the possible lists/sets we are maintaining
	private int trimPossibleNoEntry(int row, int col, Set<Integer> setToRemove) {
		if (possibleBySquare.get(row, col).removeAll(setToRemove)) {
			for (Integer toRemove : setToRemove) {
				if (possibleCoordsByRow.containsKey(toRemove)
						&& possibleCoordsByRow.get(toRemove).containsKey(row)) {
					possibleCoordsByRow.get(toRemove).get(row).remove(col);
				}
				if (possibleCoordsByCol.containsKey(toRemove)
						&& possibleCoordsByCol.get(toRemove).containsKey(col)) {
					possibleCoordsByCol.get(toRemove).get(col).remove(row);
				}
			}
			
			return 1;
		}
		
		return 0;
	}
	
	// the solution has been modified and now we have to update the
	// possibility array for other boxes. only updates within row, col, or box
	private int trimPossibleAfterEntry(int row, int col, int val) {
		if (sol[row][col] != val) System.out.println("trimPossibleAfterEntry called but sol[][] hasn't been updated. "
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
		
		possibleCoordsByRow.get(val).get(row).clear();
		possibleCoordsByRow.get(val).remove(Integer.valueOf(row));
		if (possibleCoordsByRow.get(val).size() == 0) {
			possibleCoordsByRow.remove(Integer.valueOf(val));
		}
		possibleCoordsByCol.get(val).get(col).clear();
		possibleCoordsByCol.get(val).remove(Integer.valueOf(col));
		if (possibleCoordsByCol.get(val).size() == 0) {
			possibleCoordsByCol.remove(Integer.valueOf(val));
		}
		for (int i = 1; i < 10; i++) {
			if (possibleCoordsByRow.get(val) != null) {
				if (possibleCoordsByRow.get(val).get(i - 1) != null)
					possibleCoordsByRow.get(val).get(i - 1).remove(col);
				if (possibleCoordsByRow.get(val).get((row / 3) * 3 + (i - 1) / 3) != null)
					possibleCoordsByRow.get(val).get((row / 3) * 3 + (i - 1) / 3).remove((col / 3) * 3 + (i - 1) % 3);
			}
			if (possibleCoordsByCol.get(val) != null) {
				if (possibleCoordsByCol.get(val).get(i - 1) != null)
					possibleCoordsByCol.get(val).get(i - 1).remove(row);
				if (possibleCoordsByCol.get(val).get((col / 3) * 3 + (i - 1) % 3) != null)
					possibleCoordsByCol.get(val).get((col / 3) * 3 + (i - 1) % 3).remove((row / 3) * 3 + (i - 1) / 3);
			}
			if (i == val) continue;
			if (possibleCoordsByRow.get(i) != null && possibleCoordsByRow.get(i).get(row) != null) {
				possibleCoordsByRow.get(i).get(row).remove(col);
			}
			if (possibleCoordsByCol.get(i) != null && possibleCoordsByCol.get(i).get(col) != null) {
				possibleCoordsByCol.get(i).get(col).remove(row);
			}
		}
		
		return 0;
	}

	private int trimPossibleBySubset() {
		int modified = 0;
		
		if (trimPossibleBySubsetNaked() != 0) modified = 1;
		if (trimPossibleBySubsetHidden() != 0) modified = 2;
		
		return modified;
	}
	
	// if there are cells within a row/col/box that can all only contain some subset of the options for that
	// respective row/col/box, then those options can't go anywhere else in the row/col/box, they must go
	// in those cells. remove them as options from other cells in the row/col/box
	private int trimPossibleBySubsetNaked() {
		int modified = 0;
		
		if (trimPossibleBySubsetNakedWithinBox() != 0) modified = 1;
		if (trimPossibleBySubsetNakedWithinRowCol() != 0) modified = 2;
		
		return modified;
	}
	
	// implementation of trimPossibleBySubsetNaked() concerning boxes
	private int trimPossibleBySubsetNakedWithinBox() {
		int modified = 0;
		
		List<Integer> pairs = new ArrayList<Integer>();
		
		for (int boxRow = 0; boxRow < 3; boxRow++) {
			for (int boxCol = 0; boxCol < 3; boxCol++) {
								
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						
						pairs.clear();
						
						// check a box for pairs, triples, etc.
						if (possibleBySquare.get((boxRow * 3) + i, (boxCol * 3) + j) != null
								&& possibleBySquare.get((boxRow * 3) + i, (boxCol * 3) + j).size() != boxOptions.get((boxRow * 3) + boxCol).size()) {
							int possibilitiesInSquare = possibleBySquare.get((boxRow * 3) + i, (boxCol * 3) + j).size();
							int squaresWithSubsetPossibilities = 1;
							
							pairs.add((i * 3) + j);
							
							for (int k = 0; k < 9; k++) {
								if (k == (i * 3) + j) continue;
								if (possibleBySquare.get((boxRow * 3) + k / 3, (boxCol * 3) + k % 3) != null 
										&& possibleBySquare.get((boxRow * 3) + i, (boxCol * 3) + j).containsAll(possibleBySquare.get((boxRow * 3) + k / 3, (boxCol * 3) + k % 3))) {
									squaresWithSubsetPossibilities++;
									pairs.add(k);
								}
								
								// a valid pair/triple/etc was found. now remove those options from other squares in the box
								if (squaresWithSubsetPossibilities == possibilitiesInSquare) {
									// now pairs contains the squares in the box which hold the naked pair/triple/etc squares
									Set<Integer> subsetToRemove = possibleBySquare.get((boxRow * 3) + i, (boxCol * 3) + j);
									for (int l = 0; l < 9; l++) {
										// a square that was a member of the pair/triple/etc should not be modified
										if (pairs.contains(l)) continue;
										// a square not a member of the pair/triple/etc should no longer have those options
										if (possibleBySquare.get((boxRow * 3) + l / 3, (boxCol * 3) + l % 3) != null) {
											if (trimPossibleNoEntry((boxRow * 3) + l / 3, (boxCol * 3) + l % 3, subsetToRemove) != 0) modified = 1;
										}
									}
									
									break;
								}
							}
						}
					}
				}
				
			}
		}
		
		return modified;
	}
	
	// implementation of trimPossibleBySubsetNaked() concerning rows/cols
	private int trimPossibleBySubsetNakedWithinRowCol() {
		int modified = 0;
		
		// the somewhat tricky thing is: squares with subsets of the subset count too
		// i.e. three squares with [5, 7], [5, 7, 8], [5, 8] as options count as a triple
		// and the options 5, 7, and 8 should be removed from other squares
		
		// NOTE: there can never be a set of squares matching this criterion that has less possibilities than number of squares
		// i.e. there can't be three or more squares in any row, col, or box all containing only the possibilities [5, 7]
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (trimPossibleBySubsetNakedWithinRowCol(i, j, true) != 0) modified = 1;
				if (trimPossibleBySubsetNakedWithinRowCol(j, i, false) != 0) modified = 2;
			}
		}
		
		return modified;
	}

	// overloaded function implementing logic similar to purpose of original to avoid duplicate code
	private int trimPossibleBySubsetNakedWithinRowCol(int row, int col, boolean byRow) {
		int modified = 0;
		
		List<Integer> pairs = new ArrayList<Integer>();
		
		// check a row for pairs, triples, etc.
		if (possibleBySquare.get(row, col) != null
				&& ((byRow && possibleBySquare.get(row, col).size() != rowOptions.get(row).size())
				|| (!byRow && possibleBySquare.get(row, col).size() != colOptions.get(col).size()))) {
			int possibilitiesInSquare = possibleBySquare.get(row, col).size();
			int squaresWithSubsetPossibilities = 1;
			
			if (byRow) pairs.add(col);
			else pairs.add(row);
			
			for (int k = 0; k < 9; k++) {
				if ((byRow && k == col) || (!byRow && k == row)) continue;
				if (byRow 
						&& possibleBySquare.get(row, k) != null 
						&& possibleBySquare.get(row, col).containsAll(possibleBySquare.get(row, k))) {
					squaresWithSubsetPossibilities++;
					pairs.add(k);
				} else if (!byRow 
						&& possibleBySquare.get(k, col) != null 
						&& possibleBySquare.get(row, col).containsAll(possibleBySquare.get(k, col))) {
					squaresWithSubsetPossibilities++;
					pairs.add(k);
				}

				// a valid pair/triple/etc was found. now remove those options from other squares in the row
				if (squaresWithSubsetPossibilities == possibilitiesInSquare) {
					// now rowPairs contains the columns in row i which hold the naked pair/triple/etc squares
					Set<Integer> subsetToRemove = possibleBySquare.get(row, col);
					for (int l = 0; l < 9; l++) {
						// a square that was a member of the pair/triple/etc should not be modified
						if (pairs.contains(l)) continue;
						// a square not a member of the pair/triple/etc should no longer have those options
						if (byRow && possibleBySquare.get(row, l) != null) {
							if (trimPossibleNoEntry(row, l, subsetToRemove) != 0) modified = 1;
						} else if (!byRow && possibleBySquare.get(l, col) != null) {
							if (trimPossibleNoEntry(l, col, subsetToRemove) != 0) modified = 1;
						}
					}
					
					break;
				}
			}
		}
		
		return modified;
	}
	
	// if there are N options within a row/col/box that all must fall into the same N cells, then no other options
	// are valid in those cells, since those N options will fill those N cells entirely. remove extra options
	private int trimPossibleBySubsetHidden() {
		int modified = 0;
		
		if (trimPossibleBySubsetHiddenWithinBox() != 0) modified = 1;
		if (trimPossibleBySubsetHiddenWithinRowCol() != 0) modified = 2;
		
		return modified;
	}
	
	// implementation of trimPossibleBySubsetHidden() concerning boxes
	private int trimPossibleBySubsetHiddenWithinBox() {
		int modified = 0;
		
		return modified;
	}
	
	// implementation of trimPossibleBySubsetHidden() concerning rows/cols
	private int trimPossibleBySubsetHiddenWithinRowCol() {
		int modified = 0;
		
		// TODO test
		
		Set<Integer> rowMatches = new HashSet<Integer>();
		Set<Integer> colMatches = new HashSet<Integer>();
//		Map<Integer, List<Integer>> optionLocations = new HashMap<Integer, List<Integer>>();
		Set<Integer> locations = new HashSet<Integer>();
		
		// the union of the sets of the pair/triple/etc options should have the same cardinality as there are squares
		// making up the pair/triple/etc. i.e. if there are 3 options that must go in 3 squares, then they must be
		// values for those squares and any other options can be removed. regardless of if the options are like
		// [1, 2], [2, 5], [1, 5] or if they're like [1, 2, 5], [1, 2, 5], [1, 2, 5] or something else
		
		for (int i = 0; i < 9; i++) {
//			for (int j = 0; j < 9; j++) {
////				if (trimPossibleBySubsetHiddenWithinRowCol(i, j, true) != 0) modified = 1;
////				if (trimPossibleBySubsetHiddenWithinRowCol(j, i, false) != 0) modified = 2;
//				
//				rowPairs.clear();
//				colPairs.clear();
//				
////				if (possibleBySquare.get(i, j) != null) {
////
////				}
//				for (Integer opt : rowOptions.get(i)) {
//					
//				}
//			}
			
//			for (Integer valueOption : rowOptions.get(i)) {
//				locations = possibleCoordsByRow.get(valueOption).get(i);
//				if (locations.size() == rowOptions.get(i).size()) continue;
//			}
			
			// don't focus on subsets yet, just perfect matches
			// start cycling through the options remaining in this row
			for (Integer startOption : rowOptions.get(i)) {
				rowMatches.clear();
				
				// get the possible locations of the element we're trying to find location matches for TODO is possibleCoords correct? 
				locations = possibleCoordsByRow.get(startOption).get(i);
				rowMatches.add(startOption);
				
				for (Integer matchOption : rowOptions.get(i)) {
					if (matchOption == startOption) continue;
					if (possibleCoordsByRow.get(matchOption).get(i).equals(locations)) {
						rowMatches.add(matchOption);
					}
				}
				
				if (locations.size() == rowMatches.size()) {
					// we've found a pair/triplet/etc
					for (Integer location : locations) {
//						possibleBySquare.get(i, location).removeIf(n -> !rowMatches.contains(n));
						Set<Integer> setToCheckForRemoval = possibleBySquare.get(i, location);
						for (Integer toCheckForRemoval : setToCheckForRemoval) {
							if (!rowMatches.contains(toCheckForRemoval)) {
								modified = 1;
								trimPossibleNoEntry(i, location, toCheckForRemoval);
							}
						}
					}
				}
			}
			
			// don't focus on subsets yet, just perfect matches
			// start cycling through the options remaining in this col
			for (Integer startOption : colOptions.get(i)) {
				colMatches.clear();
				
				// get the possible locations of the element we're trying to find location matches for
				locations = possibleCoordsByCol.get(startOption).get(i);
				colMatches.add(startOption);
				
				for (Integer matchOption : colOptions.get(i)) {
					if (matchOption == startOption) continue;
					if (possibleCoordsByCol.get(matchOption).get(i).equals(locations)) {
						colMatches.add(matchOption);
					}
				}
				
				if (locations.size() == colMatches.size()) {
					// we've found a pair/triplet/etc
					for (Integer location : locations) {
//									possibleBySquare.get(i, location).removeIf(n -> !rowMatches.contains(n));
						Set<Integer> setToCheckForRemoval = possibleBySquare.get(location, i);
						for (Integer toCheckForRemoval : setToCheckForRemoval) {
							if (!colMatches.contains(toCheckForRemoval)) {
								modified = 1;
								trimPossibleNoEntry(location, i, toCheckForRemoval);
							}
						}
					}
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
									trimPossibleNoEntry((boxRow * 3) + row, i, searchedNum);
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
									trimPossibleNoEntry(i, (boxCol * 3) + col, searchedNum);
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
									trimPossibleNoEntry(((i / 3) * 3) + _i, (block * 3) + _j, searchedNum);
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
									trimPossibleNoEntry((block * 3) + _j, ((i / 3) * 3) + _i, searchedNum);
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
		if (val == 0) return -1;
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
