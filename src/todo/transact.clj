(ns todo.transact
  (:require
    [todo.state :refer [state]]))

(defn init! []
  (swap! state assoc :tasks
         [{:description "task 1"
           :tag "tag 1"}
          {:description "task 2"
           :tag "tag 2"}
          {:description "task 3"
           :tag "tag 3"}]))
