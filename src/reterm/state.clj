(ns reterm.state) 

; .------> x
; |
; |
; V y

; state

(def state 
  (atom {:cursor {:x 0
                  :y 0
                  :value nil}
         :screen {:width nil
                  :height nil}
         :run? true}))

; helpers

(defn bound [lower value upper]
  (cond 
    (< value lower)
    lower
    (> value upper)
    upper
    :else
    value)) 

; subscriptions

(defn cursor []
  (@state :cursor))

(defn running? []
  (@state :run?))

; transactions

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

(defn store-screen-size! [[width height]]
  (swap! state assoc :screen {:width width
                              :height height}))
