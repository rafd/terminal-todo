(ns todo.core
  (:require 
    [lanterna.screen :as s]
    [todo.transact :as tx]
    [todo.state :refer [state]]))

; main logic

(defn handle-key! [key]
  (case key
    \k (tx/cursor-up!)
    \j (tx/cursor-down!)
    \h (tx/cursor-left!)
    \l (tx/cursor-right!)
    ; default 
    (tx/escape!)))

(defn draw! [scr]
  (s/put-string scr 10 10 "Hello, world!")
  (s/put-string scr 10 11 "Press any key to exit!")
  (when (get-in @state [:cursor :value])
    (s/put-string scr 
                  (get-in @state [:cursor :x]) 
                  (get-in @state [:cursor :y])
                  (get-in @state [:cursor :value])))
  (s/move-cursor scr 
                 (get-in @state [:cursor :x]) 
                 (get-in @state [:cursor :y])))

(defn run-loop! [scr]
  (draw! scr)
  (s/redraw scr)
  (handle-key! (s/get-key-blocking scr)))

(defn -main []
  (let [mode :text ; :swing
        scr (s/get-screen mode)]
    (s/start scr)

    (let [[width height] (s/get-size scr)]
      (swap! state assoc :screen {:width width
                                  :height height}))

    (while (@state :run?) 
      (run-loop! scr))

    (s/stop scr)))
