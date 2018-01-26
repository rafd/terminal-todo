(ns todo.subscribe
  (:require
    [todo.state :refer [state]]))

(defn cursor []
  (@state :cursor))

(defn running? []
  (@state :run?))
