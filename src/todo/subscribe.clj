(ns todo.subscribe
  (:require
    [todo.state :refer [state]]))

(defn tasks []
  (@state :tasks))
