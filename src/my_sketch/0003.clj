;;;; Draws a "circle" of triangles, each of a random color.
;;;; With increasing circle, the chance of a "glitch" increases.
;;;; A glitch makes the triangle of different color and wrong
;;;; coordinates that are not on the circle.
(ns my-sketch.0003
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;;; Variables for this sketch.
(def triangles 80) ; How many triangles to draw.
(def initial-hue (rand-int 256)) ; The hue of the first triangle.

(defn angle
  "Calculates the angle in radians, based on a full circle for inedex == total."
  [index total]
  ;; Plus PI/2 to start at bottom, not right.
  (+ (* (/ (* Math/PI 2) total) index) (/ Math/PI 2)))

(defn circle-coords
  "Returns x and y coordinates based on a center, radius, and angle in radians."
  [angle center radius] ; center is a single integer for a canvas of equal lengths.
  (let [a (* (Math/sin angle) radius) ; a is the distance of the point on the y axis from the center.
        b (* (Math/cos angle) radius) ; b is the distance of the point on the x axis from the center.
        x (+ center b) ; x-coordinate of the point on the circle.
        y (+ center a)] ; y-coordinate of the point on the circle.
    [x y]))

(defn offset
  "Calculates a random offset from a given value up to max-offset (up and down)."
  [value max-offset]
  (+ value (- (rand-int (* max-offset 2)) max-offset)))

(defn setup []
  (q/no-loop)
  (q/color-mode :hsb)

  (q/background 0 0 255)
  (q/no-stroke)
  (q/no-fill))

(defn draw []
  ; Draw a circle manually from triangles.
  (let [center (/ (q/width) 2) ; Only one center for a canvas of equal lengths.
        radius (/ (q/width) 3)] ; How many triangles to draw.
    ; One more than triangles to close the circle with the last triangle.
    (dotimes [i (+ triangles 1)]
      (let [alpha1 (angle i triangles) ; alpha1 is the first angle since start in radians.
            ;; Coordinates of the first point of the triangle on the circle.
            [x1, y1] (circle-coords alpha1 center radius)

            ;; alpha2 is the second angle since start in radians.
            alpha2 (angle (+ i 1) triangles)
            ;; Coordinates of the second point of the triangle on the circle.
            [x2, y2] (circle-coords alpha2 center radius)
            ;; A random value between 0 and max 99, the limit increases linearly
            ;; from 0 to 99 based on the current index.
            weighted-random (rand-int (* i (/ 100 triangles)))
            ;; Is true, if the weighted random number is greater 40.
            ;; Cannot be the case for the first iterations, as i is too low and
            ;; therefore weighted random cannot be that great. Probability
            ;; increases with iterations.
            is-glitch (> weighted-random 40)
            ;; Based on whether or not this is a glitch, the hue is either the
            ;; original one, or turned 180 deg on the hue wheel, to the opposite
            ;; side - with a small variation.
            ;; Also, a glitch has less brightness and more saturation.
            hue (if is-glitch (mod (+ initial-hue (+ 110 (rand-int 37))) 255) initial-hue)
            saturation (if is-glitch 240 170)
            brightness (if is-glitch 200 255)]

        (q/fill hue saturation brightness)
        (q/stroke hue saturation brightness)
        (q/begin-shape) ; Draw the actual triangle that makes up part of the circle.
        (q/vertex center center)
        (q/vertex
         (if is-glitch (offset x1 (* 2 weighted-random)) x1)
         (if is-glitch (offset y1 weighted-random) y1))
        (q/vertex
         (if is-glitch (offset x2 weighted-random) x2)
         (if is-glitch (offset y2 (* 3 weighted-random)) y2))
        (q/end-shape))))

  (q/save "renders/0003.png"))

(q/defsketch my-sketch
  :title "0003"
  :size [800 800]
  ; Setup function called only once, during sketch initialization.
  :setup setup
  :draw draw)
