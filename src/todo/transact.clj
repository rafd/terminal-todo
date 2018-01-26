(ns todo.transact
  (:require
    [todo.state :refer [state]]))

(defn init! []
  (swap! state assoc :tasks
         [{:description "task 1"}
          {:description "task 2"}
          {:description "task 3"}]))
