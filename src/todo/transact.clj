(ns todo.transact
  (:require
    [reterm.state :as r]
    [todo.state :refer [state]]
    [todo.generate :refer [generate-state]]))

(defn update-task-description! 
  [id description]
  (swap! state assoc-in [:tasks id :description] description))

(defn init! []
  (reset! state (generate-state)))

; TODO should find a better way to do this
(def cursor-up! r/cursor-up!)
(def cursor-down! r/cursor-down!)
(def cursor-left! r/cursor-left!)
(def cursor-right! r/cursor-right!)
(def set-running-state! r/set-running-state!)
