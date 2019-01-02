;;;; Draws triangles in rows and columns with varying colors.
(ns my-sketch.0004
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;;; Variables to set up this sketch.
(def rows 30)
(def columns 15)
;; Hue can be random or a fixed number.
(def initial-hue (rand-int 206))
(def max-hue-offset 50)

(defn column-width
  "Returns the width of a single column."
  []
  (/ (q/width) columns))

(defn row-height
  "Returns the height of a single row."
  []
  (/ (q/height) rows))

(defn to-radians
  "Convert current row and column to radians with a chance of extreme 1 or 0
  for multiplier."
  [row column]
  (if (> (rand-int 100) 90)
    (* 0.85 Math/PI)
    (+
     (* 0.3 Math/PI (/ row rows))
     (* 0.7 Math/PI (/ column columns)))))

(defn hue
  "Calculates the hue for this row/column."
  [row column]
  (+ initial-hue (* max-hue-offset (Math/sin (to-radians row column)))))

(defn triangle
  "Draws the triangle at the given row and column."
  [row column]
  (q/triangle
   (* column (column-width)) (* row (row-height))
   (* (+ 1 column) (column-width)) (* row (row-height))
   (* (+ 0.5 column) (column-width)) (* (+ 1 row) (row-height))))

(defn conter-triangle
  "Draws the counter part to the triangle drawn with `triangle`."
  [row column]
  (q/triangle
   (* (+ 0.5 column) (column-width)) (* (+ 1 row) (row-height))
   (* (+ 1.5 column) (column-width)) (* (+ 1 row) (row-height))
   (* (+ 1 column) (column-width)) (* row (row-height)))

  ;; If this is the left-most column, we must also draw the extra triangle
  ;; on the left side of the canvas.
  (if (= 0 column) (conter-triangle row -1)))

(defn setup []
  (q/no-loop)
  (q/color-mode :hsb)

  (q/background 0 0 255)
  (q/no-stroke)
  (q/no-fill))

(defn draw []
  (dotimes [column columns]
    (dotimes [row rows]
      (def hue1 (hue row column))
      (def hue2 (hue row column))
      (q/fill hue1 200 200)
      (q/stroke hue1 200 200)
      (triangle row column)
      (q/fill hue2 200 200)
      (q/stroke hue2 200 200)
      (conter-triangle row column)))

  (q/save "renders/0004.png"))

(q/defsketch my-sketch
  :title "0004"
  :size [800 800]
  ; Setup function called only once, during sketch initialization.
  :setup setup
  :draw draw)
