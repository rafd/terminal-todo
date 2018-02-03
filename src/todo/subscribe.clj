(ns todo.subscribe
  (:require
    [todo.state :refer [state]]))

(defn groups []
  (vals (@state :groups)))

(defn group-tasks [group-id]
  (->> @state
       :tasks
       vals
       (filter (fn [task]
                 (= group-id (task :group-id))))))
