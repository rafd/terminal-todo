(ns todo.views
  (:require
    [todo.subscribe :as sub]))

(defn task-view [task]
  [:div {:bg :default
         :fg :default}
   (task :description)])

(defn tasks-view [tasks]
  [:div {:bg :blue
         :fg :yellow
         :x 10
         :y 10
         :width 10
         :height 10}
   (for [task tasks]
     [task-view task])])

(defn app-view []
  [:div {:bg :red
         :height :stretch}
   [tasks-view (sub/tasks)]])
