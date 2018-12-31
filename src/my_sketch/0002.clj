;; Draws a "circle" of triangles, each of a random color.
(ns my-sketch.0002
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn angle [index total]
  (- (* (/ (* Math/PI 2) total) index) (/ Math/PI 2))) ; Minus PI/2 to start at top, not right.

(defn circle-coords [angle center radius] ; center is a single integer for a canvas of equal lengths.
  (let [a (* (Math/sin angle) radius) ; a is the distance of the point on the y axis from the center.
        b (* (Math/cos angle) radius) ; b is the distance of the point on the x axis from the center.
        x (+ center b) ; x-coordinate of the point on the circle.
        y (+ center a)] ; y-coordinate of the point on the circle.
    [x y]))

(defn setup []
  (q/no-loop)
  (q/color-mode :hsb)

  (q/background 0 0 255)
  (q/no-stroke)
  (q/no-fill))

(defn draw []
  ; Draw a circle manually from triangles.
  (let [center (/ (q/width) 2) ; Only one center for a canvas of equal lengths.
        radius (/ (q/width) 3)
        triangles 300] ; How many triangles to draw.
    (def start-fill (rand-int 255))
    ; One more than triangles to close the circle with the last triangle.
    (dotimes [i (+ triangles 1)]
      ; Todo: extract x/y into function
      (let [alpha1 (angle i triangles) ; alpha1 is the first angle since start in radians.
            [x1, y1] (circle-coords alpha1 center radius)

            alpha2 (angle (+ i 1) triangles) ; alpha2 is the second angle since start in radians.
            [x2, y2] (circle-coords alpha2 center radius)]

        (def hue-distance (rand-int (* i (/ 100 triangles)))) ; Max hue-distance is 100.
        (def hue (mod (+ start-fill hue-distance) 255))
        (q/fill hue 255 255) ; Random fill hue for this triangle.
        (q/begin-shape)
        (q/vertex center center)
        (q/vertex x1 y1)
        (q/vertex x2 y2)
        (q/end-shape))))

  (q/save "renders/0002.png"))

(q/defsketch my-sketch
  :title "0002"
  :size [800 800]
  ; Setup function called only once, during sketch initialization.
  :setup setup
  :draw draw)
