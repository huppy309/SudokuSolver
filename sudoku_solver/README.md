# sudoku_solver

This is a Sudoku Solver implemented in the Clojure (functional) programming language. As an example, a main method is included which uses the functions therein to solve a given Sudoku board:

				[5 3 0 0 7 0 0 0 0]
			    [6 0 0 1 9 5 0 0 0]
			    [0 9 8 0 0 0 0 6 0]
			    [8 0 0 0 6 0 0 0 3]
			    [4 0 0 8 0 3 0 0 1]
			    [7 0 0 0 2 0 0 0 6]
			    [0 6 0 0 0 0 2 8 0]
			    [0 0 0 4 1 9 0 0 5]
			    [0 0 0 0 8 0 0 7 9]

All zeroes represent empty slots. Valid values are in the set [1-9]. The solution is obtained by trying various values that could potentially work for each empty slot. If a value is found to not lead to the solution, the algorithm backtracks to the remaining set of valid values and tries all of them recursively.

## Usage

Navigate to the sudoku_solver directory in a linux (bash) shell. To run the project you must have leiningen installed. Run the following command in the terminal to run the project:

```
	lein run 
```

Individual functions may also be tested by un-commenting the appropriate functions written in main.