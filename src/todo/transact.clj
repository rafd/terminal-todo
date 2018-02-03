(ns todo.transact
  (:require
    [todo.state :refer [state]]
    [todo.generate :refer [generate-state]]))



(defn init! []
  (reset! state (generate-state)))
