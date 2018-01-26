(ns todo.views
  (:require
    [todo.subscribe :as sub]))

(defn task-view [task]
  [:div {}
   (str "[" (task :tag) "]" " " (task :description))])

(defn tasks-view [tasks]
  [:div {:height :stretch}
   (for [task tasks]
     [task-view task])])

(defn app-view []
  [:div {:height :stretch}
   [tasks-view (sub/tasks)]])
