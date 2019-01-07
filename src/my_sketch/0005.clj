;;;; Draws triangles inside each other.
(ns my-sketch.0005
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;;; Variables to set up this sketch.
;; Height will be derived from the width to make the triangles equilateral.
(def sketch-width 900)
;; How many triangles to draw in total.
(def triangles 8)
;; Hue can be random or a fixed number.
(def triangle-hue (rand-int 256))
(def triangle-saturation 255)
(def triangle-brightness 255)
;; Hue increase with every subsequent triangle. One third of the color wheel
;; would be 85.
(def hue-increase 134)

;;; Global variables for the entire drawing.
;; Current triangle to draw and derive the next one from.
;; Both initialized in the set-up function.
;; base is the y coordinate and the two x coordinates of the base.
;; Initially bottom left and right.
(def triangle-base [0 0 0])
; ;; tip is the x and y coordinates of the triangle tip.
; ;; Initially top middle.
(def triangle-tip [0 0])

;;; Sketch relevant functions.
(defn draw-triangle
  "Draws a triangle at the global coordinates."
  []
  (q/triangle
   (get triangle-base 1) (get triangle-base 0)
   (get triangle-base 2) (get triangle-base 0)
   (get triangle-tip 0) (get triangle-tip 1)))

(defn triangle-base-offset
  "Returns the x-offset for the base for the next, inner triangle."
  []
  (/ (- (get triangle-base 2) (get triangle-base 1)) 4))

(defn triangle-y-offset
  "Returns the vertical offset to arrange the triangles more evenly on the sketch."
  []
  (/ (- (get triangle-tip 1) (get triangle-base 0)) 10))

(defn update-triangle-coordinates
  "Updates the triangle coordinates to be inside the previous triangle."
  []
  (def new-triangle-tip [(/ (+ (get triangle-base 1) (get triangle-base 2)) 2)
                         (+ (get triangle-base 0) (triangle-y-offset))])
  (def new-triangle-base [(+ (/ (+ (get triangle-base 0) (get triangle-tip 1)) 2) (triangle-y-offset))
                          (+ (get triangle-base 1) (triangle-base-offset))
                          (- (get triangle-base 2) (triangle-base-offset))])
  (def triangle-base new-triangle-base)
  (def triangle-tip new-triangle-tip))

(defn update-triangle-color
  "Increases the hue amount by the given variable, wraps around."
  []
  (def triangle-hue (mod (+ triangle-hue hue-increase) 256))
  (q/stroke triangle-hue triangle-saturation triangle-brightness)
  (q/fill triangle-hue triangle-saturation triangle-brightness))

;;; Quil and general functions.
(defn setup []
  (q/no-loop)
  (q/color-mode :hsb)

  (q/background triangle-hue triangle-saturation triangle-brightness)
  (def triangle-base [(q/height) 0 (q/width)])
  (def triangle-tip [(/ (q/width) 2) 0]))

(defn draw []
  (dotimes [_ triangles]
    (update-triangle-color)
    (draw-triangle)
    (update-triangle-coordinates))

  (q/save "renders/0005.png"))

(q/defsketch my-sketch
  :title "0005"
  :size [sketch-width (Math/sqrt (* (* sketch-width sketch-width) (/ 3 4)))]
  ; Setup function called only once, during sketch initialization.
  :setup setup
  :draw draw)
