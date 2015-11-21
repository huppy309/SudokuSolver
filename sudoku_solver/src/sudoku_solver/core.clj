(ns sudoku-solver.core
  (:gen-class)
  (:require clojure.set))

;-------------------------------------------------------
;				HELPER FUNCTIONS
;-------------------------------------------------------

;--Block Numbering--
;	1 2 3
;	4 5 6
;	7 8 9
;-------------------

(defn topLeft [blockNumber]
  (def topLeftValues 
    [[0 0]
     [0 3]
     [0 6]
     [3 0]
     [3 3]
     [3 6]
     [6 0]
     [6 3]
     [6 6]])
  (get topLeftValues (- blockNumber 1)))

(defn getBlockCoordinates [blockNumber]
  (def zeroZero (topLeft blockNumber))
  (for [rowNumber [0 1 2]
        colNumber [0 1 2]]
    [(+ rowNumber (first zeroZero)) (+ colNumber (second zeroZero))]))

(defn blockNumber [coordinates]
  (def blockRowSets
    [[1 2 3]
     [4 5 6]
     [7 8 9]])
  (let [x (/ (first coordinates) 3)
        y (/ (second coordinates) 3)]

  (if (>= x 2)
    (if (>= y 2)
      (get-in blockRowSets [2 2])
      (if (>= y 1)
        (get-in blockRowSets [2 1])
        (get-in blockRowSets [2 0])))
    (if (>= x 1)
      (if (>= y 2)
        (get-in blockRowSets [1 2])
        (if (>= y 1)
          (get-in blockRowSets [1 1])
          (get-in blockRowSets[1 0])))
      (if (>= y 2)
        (get-in blockRowSets [0 2])
        (if (>= y 1)
          (get-in blockRowSets [0 1])
          (get-in blockRowSets [0 0])))))))


;--------------------------------------------------------

(defn row-values [board coordinates]
  (loop [rowSet #{(get-in board coordinates)}          
         entireRow (get board (first coordinates))]
    (if (seq entireRow)
      (if (not= (first entireRow) 10)
        (recur (into rowSet #{(first entireRow)}) (rest entireRow))
        (recur rowSet (rest entireRow)))
      rowSet)))

(defn col-values [board coordinates]
  (loop [column #{}
         rowNumber 0]
    (if (not= rowNumber 9)
      (recur (into column #{(get-in board [rowNumber (second coordinates)])}) (inc rowNumber))
      column)))

;--------------------------------------------------------

(defn block-values [board coordinates]
  (loop [blockSet #{}
         coordinateSet (getBlockCoordinates (blockNumber coordinates))]
    (if (seq (first coordinateSet))
      (recur (into blockSet #{(get-in board (first coordinateSet))}) (rest coordinateSet))
      blockSet)))

;--------------------------------------------------------

(defn valid-values-for [board coordinates]
  (def all-values #{1 2 3 4 5 6 7 8 9})
  (if (= (get-in board coordinates) 0)
    (do
      (def allMinusRow (clojure.set/difference all-values (row-values board coordinates)))
      (def allMinusCol (clojure.set/difference allMinusRow (col-values board coordinates)))
      (def allMinusBlock (clojure.set/difference allMinusCol (block-values board coordinates)))
      allMinusBlock)
    #{}))

;--------------------------------------------------------

(defn filled? [board]
  (loop [board board]
    (if (seq (first board))
      (if (contains? (set (first board)) 0)
        false
        (recur (rest board)))
      true)))

(defn findEmptyPoint [board]
  (first (into [] (remove #(= nil %) (into [] (for [row [0 1 2 3 4 5 6 7 8]
    col [0 1 2 3 4 5 6 7 8]]
    (if (= (get-in board [row col]) 0)
      [row col])))))))

;--------------------------------------------------------

(defn rows [board]
  (loop [x 0
         valueSets [#{0} #{1} #{2} #{3} #{4} #{5} #{6} #{7} #{8}]]
    (if (not= x 9)
      (recur (inc x) (assoc valueSets x (row-values board [x 0])))
      valueSets)))

(defn cols [board]
  (loop [y 0
         valueSets [#{0} #{1} #{2} #{3} #{4} #{5} #{6} #{7} #{8}]]
    (if (not= y 9)
      (recur (inc y) (assoc valueSets y (col-values board [0 y])))
      valueSets)))

(defn blocks [board]
  (loop [i 1
         valueSets [#{0} #{1} #{2} #{3} #{4} #{5} #{6} #{7} #{8}]]
    (if (not= i 10)
      (recur (inc i) (assoc valueSets (- i 1) (block-values board (topLeft i))))
      valueSets)))

;--------------------------------------------------------

(defn valid-rows? [board]
  (def all-values #{1 2 3 4 5 6 7 8 9})
  (loop [x 0]
    (if (not= x 9)
      (if (not= (clojure.set/difference all-values (get (rows board) x)) #{})
        false
        (recur (inc x)))
      true)))

(defn valid-cols? [board]
  (def all-values #{1 2 3 4 5 6 7 8 9})
  (loop [x 0]
    (if (not= x 9)
      (if (not= (clojure.set/difference all-values (get (cols board) x)) #{})
        false
        (recur (inc x)))
      true)))

(defn valid-blocks? [board]
  (def all-values #{1 2 3 4 5 6 7 8 9})
  (loop [x 0]
    (if (not= x 9)
      (if (not= (clojure.set/difference all-values (get (blocks board) x)) #{})
        false
        (recur (inc x)))
      true)))

(defn valid-solution? [board]
  (if (valid-rows? board)
    (if (valid-cols? board)
      (if (valid-blocks? board)
        true
        false)
      false)
    false))

;--------------------------------------------------------

(defn sudokusHelper [board]
  (if (filled? board)
    (if (valid-solution? board)
      board
      nil)
     (let [validValues (valid-values-for board (findEmptyPoint board))]
     (for [tryValue validValues
           solution (sudokusHelper (assoc-in board (findEmptyPoint board) tryValue))]
       solution))))

(defn solve [board]
  (sudokusHelper board))

(defn printBoard [board]
  (doseq [board board]
    (prn board)))

;-------------MAIN--------------------------------------

(defn -main []
  
  (def sudoku-board 
    [[5 3 0 0 7 0 0 0 0]
    [6 0 0 1 9 5 0 0 0]
    [0 9 8 0 0 0 0 6 0]
    [8 0 0 0 6 0 0 0 3]
    [4 0 0 8 0 3 0 0 1]
    [7 0 0 0 2 0 0 0 6]
    [0 6 0 0 0 0 2 8 0]
    [0 0 0 4 1 9 0 0 5]
    [0 0 0 0 8 0 0 7 9]])
  
  (def solved-board
    [[5 3 4 6 7 8 9 1 2]
    [6 7 2 1 9 5 3 4 8]
    [1 9 8 3 4 2 5 6 7]
    [8 5 9 7 6 1 4 2 3]
    [4 2 6 8 5 3 7 9 1]
    [7 1 3 9 2 4 8 5 6]
    [9 6 1 5 3 7 2 8 4]
    [2 8 7 4 1 9 6 3 5]
    [3 4 5 2 8 6 1 7 9]])
  
  (println)
    
  ;(println (row-values sudoku-board [0 2]))
  ;(println (col-values sudoku-board [4 8]))
  ;(println (topLeft 9))
  ;(println (getBlockCoordinates 1))
  ;(println (block-values sudoku-board [0 2]))
  ;(println (valid-values-for sudoku-board [0 2]))
  ;(println (filled? sudoku-board))
  ;(println (filled? solved-board))
  ;(println (findEmptyPoint sudoku-board))
  ;(println (rows sudoku-board))
  ;(println (rows solved-board))
  ;(println (cols sudoku-board))
  ;(println (cols solved-board))
  ;(println (blocks sudoku-board))
  ;(println (blocks solved-board))
  ;(println (valid-rows? sudoku-board))
  ;(println (valid-rows? solved-board))
  ;(println (valid-cols? sudoku-board))
  ;(println (valid-cols? solved-board))
  ;(println (valid-blocks? sudoku-board))
  ;(println (valid-blocks? solved-board))
  ;(println (valid-solution? sudoku-board))
  ;(println (valid-solution? solved-board))
  (printBoard (solve sudoku-board))
   
   
  (println))
