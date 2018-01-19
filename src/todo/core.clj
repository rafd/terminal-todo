(ns todo.core
  (:require 
    [lanterna.screen :as s]))

; .------> x
; |
; |
; V y

; helper fns

(defn bound [lower value upper]
  (cond 
    (< value lower)
    lower
    (> value upper)
    upper
    :else
    value)) 

; state

(def state (atom {:cursor {:x 0
                           :y 0
                           :value nil}
                  :screen {:width nil
                           :height nil}
                  :run? true}))

; transaction fns

(defn cursor-up! []
  (swap! state update-in [:cursor :y] 
         (fn [y]
           (bound 0 (dec y) (get-in @state [:screen :height])))))

(defn cursor-down! []
  (swap! state update-in [:cursor :y] 
         (fn [y]
           (bound 0 (inc y) (get-in @state [:screen :height])))))

(defn cursor-left! []
  (swap! state update-in [:cursor :x] 
         (fn [x]
           (bound 0 (dec x) (get-in @state [:screen :width])))))

(defn cursor-right! []
  (swap! state update-in [:cursor :x] 
         (fn [x]
           (bound 0 (inc x) (get-in @state [:screen :width])))))

(defn escape! []
  (swap! state assoc :run? false))

; reader fns

; TODO

; main logic

(defn handle-key! [key]
  (case key
    \k (cursor-up!)
    \j (cursor-down!)
    \h (cursor-left!)
    \l (cursor-right!)
    ; default 
    (escape!)))

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
