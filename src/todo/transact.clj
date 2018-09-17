(ns todo.transact
  (:require
    [reterm.core :as r]
    [reterm.state :as rs]
    [todo.state :refer [state]]
    [todo.generate :refer [generate-state]]))

(defn update-task-description!
  [id description]
  (swap! state assoc-in [:tasks id :description] description))

(defn update-task-tag!
  [id tag]
  (swap! state assoc-in [:tasks id :tag] tag))

(defn new-task!
  [group-id]
  (let [task {:id (gensym)
              :description ""
              :tag ""
              :group-id group-id}]
    (swap! state assoc-in [:tasks (task :id)] task)))

(defn delete-task!
  [id]
  (swap! state update :tasks dissoc id))

(defn init! []
  (reset! state (generate-state)))

; TODO should find a better way to do this
(def cursor-up! rs/cursor-up!)
(def cursor-down! rs/cursor-down!)
(def cursor-left! rs/cursor-left!)
(def cursor-right! rs/cursor-right!)
(def cursor-set! rs/cursor-set!)
(def stop! r/stop!)
