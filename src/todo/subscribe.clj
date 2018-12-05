(ns todo.subscribe
  (:require
    [todo.state :refer [state]]))

(defn groups []
  (vals (@state :groups)))

(defn group-tasks [group-id]
  (let [task-ids (get-in @state [:groups group-id :task-ids])]
    (vals (select-keys (@state :tasks) task-ids))))
