(ns reterm.core
  (:require
    [lanterna.screen :as s]
    [reterm.draw :refer [draw!]]
    [reterm.state :as state]))

(defn handle-key! [key]
  (case key
    \k (state/cursor-up!)
    \j (state/cursor-down!)
    \h (state/cursor-left!)
    \l (state/cursor-right!)
    ; default 
    (state/escape!)))

(defn run-loop! [screen context component]
  (draw! screen context component)
  (let [cursor (state/cursor)]
    (when (cursor :value) 
      (s/put-string screen 
                    (cursor :x)
                    (cursor :y)
                    (cursor :value)))
    (s/move-cursor screen
                   (cursor :x)
                   (cursor :y)))
  (s/redraw screen)
  (handle-key! (s/get-key-blocking screen)))

(defn render [mode component]
  (let [screen (s/get-screen mode)
        _ (s/start screen)
        [width height] (s/get-size screen)
        context {:width width
                 :height height
                 :x 0
                 :y 0
                 :bg :default
                 :fg :default}]

    (state/store-screen-size! [width height])

    (while (state/running?)
      (run-loop! screen context component))

    (s/stop screen)))
