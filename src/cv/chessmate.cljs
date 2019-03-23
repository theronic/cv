(ns cv.chessmate
  ; feels messy to include reagent here,
  ; but we are coupled to it for now.
  (:require [reagent.core :refer [atom]]))

; how to model pawn forward-only moves?

(def union clojure.set/union)

;0.0f, 1.0f, 3.0f, 3.2f, 5.0f, 9.0f, 300.0f

; Think of moves as raycasting in a direction until they hit something, with
; the exclusion of kings (one move, any direction) and pawns (special semantics).

;	private final static int [] initialBoard = {
;		4, 2, 3, 6, 5, 3, 2, 4,	7, 7,   // white pieces
;		1, 1, 1, 1, 1, 1, 1, 1, 7, 7,   // white pawns
;		0, 0, 0, 0, 0, 0, 0, 0, 7, 7,   // 8 blank squares, 2 off board
;		0, 0, 0, 0, 0, 0, 0, 0, 7, 7,   // 8 blank squares, 2 off board
;		0, 0, 0, 0, 0, 0, 0, 0, 7, 7,   // 8 blank squares, 2 off board
;		0, 0, 0, 0, 0, 0, 0, 0, 7, 7,   // 8 blank squares, 2 off board
;		-1,-1,-1,-1,-1,-1,-1,-1,7, 7,  // black pawns
;		-4,-2,-3,-6,-5,-3,-2,-4,7, 7  // black pieces
;	};

; let's build the board conveniently

;(def pieces "♚♛♜♝♞♟♔♕♖♗♘♙")
;(def pieces-map (zipmap [:k :q :r :b :n :p
;                         :K :Q :R :B :N :P] pieces))

(defrecord Piece [pic letter side name])

; type for side?
(def BLACK :dark)
(def WHITE :white)

(def pieces
  ;♔♕♖♗♘♙
  {'♔ (Piece. \♔ \K WHITE "King")
   '♕ (Piece. \♕ \Q WHITE "Queen")
   '♖ (Piece. \♖ \R WHITE "Rook")
   '♗ (Piece. \♗ \B WHITE "Bishop")
   '♘ (Piece. \♘ \N WHITE "Knight")
   '♙ (Piece. \♙ \P WHITE "Pawn")
   ;♚♛♜♝♞♟
   '♚ (Piece. \♚ \k BLACK "King")
   '♛ (Piece. \♛ \q BLACK "Queen")
   '♜ (Piece. \♜ \r BLACK "Rook")
   '♝ (Piece. \♝ \b BLACK "Bishop")
   '♞ (Piece. \♞ \n BLACK "Knight")
   '♟ (Piece. \♟ \p BLACK "Pawn")})

(defn make-board
  "Pads each rank with two zeroes, then joins them together"
  [squares]
  ^{:pre [(= 64 (count squares))]}
  ; ensure each square has valid thing on it
  {:squares (->> (partition 8 squares)
                 (interpose [0 0])
                 (apply concat)
                 (apply vector))})

; todo perf. impl: use array in JS.

(def initial-board
  (make-board
    '[♜ ♞ ♝ ♛ ♚ ♝ ♞ ♜
      ♟ ♟ ♟ ♟ ♟ ♟ ♟ ♟
      0 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 0
      ♙ ♙ ♙ ♙ ♙ ♙ ♙ ♙
      ♖ ♘ ♗ ♕ ♔ ♗ ♘ ♖]))

(def width 10)
(def up (- width))
(def down (+ width))
(def left (- 1))
(def right (+ 1))

(def file #{up down})
(def rank #{left right})

(def diag #{(+ up left)
            (+ up right)
            (+ down left)
            (+ down right)})

(def king-moves (union file rank diag))

;(def piece-def
;  (let [
;
;        up         (- width)
;        down       (+ width)
;        left       (- 1)
;        right      (+ 1)
;
;        file       #{up down}
;        rank       #{left right}
;
;        ; how to union combine?
;        up-left    (+ up left)
;        up-right   (+ up right)
;        down-left  (+ down left)
;        down-right (+ down right)
;
;        diag       #{(+ up left)
;                     (+ up right)
;                     (+ down left)
;                     (+ down right)}
;        king-moves (clojure.set/union file rank diag)]
;    {:pawn   {:symbol :p
;              :moves  king-moves
;              :value  1.0}
;     :knight {:symbol :n
;              :moves  #{8 -8 12 -12 19 -19 21 -21}
;              :value  3.0}
;     :bishop {:symbol :b
;              :moves  #{-9 -11 9 11}
;              :value  3.2}
;     :rook   {:symbol :r
;              :moves  #{-1 1 10 -10}
;              :value  5.0}
;     :queen  {:symbol :q
;              :moves  king-moves
;              :value  9.0}
;     :king   {:symbol :k
;              :moves  king-moves
;              :value  300.0}}))

;(def piece-def
;  (let [w          10
;
;        up         (- w)
;        down       (+ w)
;        left       (- 1)
;        right      (+ 1)
;
;        file       #{up down}
;        rank       #{left right}
;
;        ; how to union combine?
;        up-left    (+ up left)
;        up-right   (+ up right)
;        down-left  (+ down left)
;        down-right (+ down right)
;
;        diag       #{(+ up left)
;                     (+ up right)
;                     (+ down left)
;                     (+ down right)}
;        king-moves (clojure.set/union file rank diag)]
;    {:pawn   {:symbol :p
;              :moves  king-moves
;              :value  1.0}
;     :knight {:symbol :n
;              :moves  #{8 -8 12 -12 19 -19 21 -21}
;              :value  3.0}
;     :bishop {:symbol :b
;              :moves  #{-9 -11 9 11}
;              :value  3.2}
;     :rook   {:symbol :r
;              :moves  #{-1 1 10 -10}
;              :value  5.0}
;     :queen  {:symbol :q
;              :moves  king-moves
;              :value  9.0}
;     :king   {:symbol :k
;              :moves  king-moves
;              :value  300.0}}))

; how to model direction?

(defn with-move*
  [board {:as move :keys [from to]}]
  ^{:pre [(array? board)
          (int? from)
          (int? to)]}
  (let [piece (aget board from)]
    (aset board from 0)
    (aset board to piece)))

(defn with-move
  [board {:as move :keys [from to]}]
  (let [piece (aget board from)]
    (assoc board from 0)
    (assoc board to piece)))

(def moves1 {:r          (union rank file)
             #{:n}       #{8 -8 12 -12 19 -19 21 -21}
             #{:q :b :k} #{-1 1 10 -10 -9 -11 9 11}})

(def moves {#{:r}       #{-1 1 10 -10}
            #{:n}       #{8 -8 12 -12 19 -19 21 -21}
            #{:q :b :k} #{-1 1 10 -10 -9 -11 9 11}})

(defn allow-drag-fn [e]
  ;; because DnD in HTMl5 is crazy...
  (.preventDefault e)
  true)

; we'll need to impl. ChessBoard with some index accessors.
; also, should benchmark having the board margin.
; I don't think we're memory constrained in JS. (maybe I'm wrong about that)

(defn render-square [piece-key]
  (let [!dragging?    (atom false)
        on-drag-start (fn [ev]
                        ; figure out what we started to drag
                        (reset! !dragging? true)
                        (.setData (.-dataTransfer ev) "item" 123))
        on-drag-end   (fn [e] true
                        (reset! !dragging? false))]
    (fn [piece-key]
      [:div
       {
        ;:display             "grid"
        :draggable     true
        :style {:display "block"}
                ;:background "red"}
        :on-drag-over  allow-drag-fn
        :on-drag-enter allow-drag-fn
        :on-drag-start on-drag-start
        :on-drag-end   on-drag-end
        :on-drop       (fn [ev]
                         ; todo handle
                         ;(reset! !crane? false)
                         ;(reset! !expand? true)
                         (let [dropped-item (js/parseInt (.getData (.-dataTransfer ev) "item"))]
                           (prn "dropped:" dropped-item))
                         ;(log/info "dropped: " dropped-item " onto " container))
                         ;
                         ;(pack! dropped-item container))
                         (.preventDefault ev))}

       (if-let [piece (get pieces piece-key)]
         (:pic piece))])))

(defn chess-board [board]
  (let [!dragging? (atom false)
        on-drag-start (fn [ev]
                        ; figure out what we started to drag
                        (reset! !dragging? true)
                        (.setData (.-dataTransfer ev) "item" 123))
        on-drag-end (fn [e] true
                      (reset! !dragging? false))]
    (fn [{:as board :keys [squares]}]
      [:div
       {:style {:display             "grid"
                ;:draggable           true
                ;:on-drag-over        allow-drag-fn
                ;:on-drag-enter       allow-drag-fn
                ;;:on-drag-start on-drag-start
                ;;:on-drag-end on-drag-end
                ;:on-drop             (fn [ev]
                ;                       ; todo handle
                ;                       ;(reset! !crane? false)
                ;                       ;(reset! !expand? true)
                ;                       (let [dropped-item (js/parseInt (.getData (.-dataTransfer ev) "item"))]
                ;                         (prn "dropped:" dropped-item))
                ;                       ;(log/info "dropped: " dropped-item " onto " container))
                ;                       ;
                ;                       ;(pack! dropped-item container))
                ;                       (.preventDefault ev))
                :font-family         "sans-serif"
                :height              "200px"
                :width               "200px"
                :font-size           "20px"
                :gridTemplateColumns "repeat(8, 1fr)"
                :grid-auto-flow      "rows"
                ;:align-items "center"
                :gridTemplateRows    "repeat(8, 1fr)"}}
       ;(pr-str "sqs:" squares)
       ;; how to do alternating?
       (for [y (range 8)
             x (range 8)
             :let [n      (+ (* y 10) x)
                   piece  (nth squares n)
                   black? (odd? (+ y x))]]
         [:div {:style {:align-items "center"
                        :text-align "center"
                        :background (if black? "#8DA2AC" "#DEE3E6")}}
          [render-square piece]])])))
   ;(for [(partition 8 board)])])


; hierarchy?

; we need to distinguish between captures and moves.
; you can only capture enemy pieces
; pawns have different capture semantics

(defmulti moves :piece)
;(defmethod moves \♚ [{:keys [piece board]}]
;  ; conditional castle?
;  king-moves)
;(defmethod moves \♛ [{:keys [piece board]}])
;(defmethod moves \♙ [{:keys [piece board]}]
;  ; pawn moves consist of:
;  ; initial 2 squares + 1 move forward + diagonal take + en passant
;  "initial move")

; to calc queen, we expand in all directions

;♜

(defn eval-position
  "Returns a rating of the board."
  ; track human control, computer control
  [board]
  0.0)

;(def rank-names "abcdefgh")
;(def rank-names (apply str (reverse rank-names)))

(defn calc-piece-moves
  "look up piece at idx.
  join against move table
  ditch blocking moves"
  [board idx]
  nil)

(defn calc-moves
  [board player]
  nil)