;; Draws a "circle" of a random color
(ns my-sketch.0001
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/no-loop)
  (q/color-mode :hsb)

  (q/background 0 0 255)
  (q/no-stroke)
  (q/no-fill))

(defn draw []
  ; Draw a circle manually.
  (let [center (/ (q/width) 2)
        radius (/ (q/width) 3)
        points 900]
    (q/begin-shape)
    ; One more than points to close the circle with the last point.
    (dotimes [i (+ points 1)]
      (q/fill (rand-int 255) 200 200)
      (let [alpha (* (/ (* Math/PI 2) points) i)
            a (* (Math/sin alpha) radius)
            b (* (Math/cos alpha) radius)
            x (+ center b)
            y (+ center a)]
        (q/vertex x y)))
    (q/end-shape))

  (q/save "renders/0001.png"))

(q/defsketch my-sketch
  :title "0001"
  :size [800 800]
  ; Setup function called only once, during sketch initialization.
  :setup setup
  :draw draw)
