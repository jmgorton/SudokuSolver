# SudokuSolver

A program for solving sudoku boards. Can currently solve virtually every easy, medium, and hard puzzles, and can solve most evil puzzles. Can not solve even a single square on the "world's hardest" sudoku, which was created by Arto Inkala in 2012. Adding logical techniques.

Will solve typical boards far faster than a recursive algorithm like backtracking, Ariadne's thread, which can potentially take many minutes or even hours to solve a board, or to discover that a board can't be solved.

To try a board, add it as a file to the project directory, where each square is just a number separated by a space, left to right and top
to bottom like how you read a book. Then in the main method, make a new Board() constructor following the format of
previous Boards.
