(ns todo.transact
  (:require
    [todo.state :refer [state]]
    [todo.helpers :as helpers]))

(defn cursor-up! []
  (swap! state update-in [:cursor :y] 
         (fn [y]
           (helpers/bound 0 (dec y) (get-in @state [:screen :height])))))

(defn cursor-down! []
  (swap! state update-in [:cursor :y] 
         (fn [y]
           (helpers/bound 0 (inc y) (get-in @state [:screen :height])))))

(defn cursor-left! []
  (swap! state update-in [:cursor :x] 
         (fn [x]
           (helpers/bound 0 (dec x) (get-in @state [:screen :width])))))

(defn cursor-right! []
  (swap! state update-in [:cursor :x] 
         (fn [x]
           (helpers/bound 0 (inc x) (get-in @state [:screen :width])))))

(defn escape! []
  (swap! state assoc :run? false))
