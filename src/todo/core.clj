(ns todo.core
  (:require 
    [lanterna.screen :as s]
    [todo.transact :as tx]
    [todo.subscribe :as sub]))

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
  (let [cursor (sub/cursor)]
    (when (cursor :value) 
      (s/put-string scr 
                    (cursor :x)
                    (cursor :y)
                    (cursor :value)))
    (s/move-cursor scr 
                   (cursor :x)
                   (cursor :y))))

(defn run-loop! [scr]
  (draw! scr)
  (s/redraw scr)
  (handle-key! (s/get-key-blocking scr)))

(defn -main []
  (let [mode :text ; :swing
        scr (s/get-screen mode)]
    (s/start scr)

    (tx/store-screen-size! (s/get-size scr))

    (while (sub/running?)
      (run-loop! scr))

    (s/stop scr)))
