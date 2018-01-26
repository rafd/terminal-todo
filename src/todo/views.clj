(ns todo.views
  (:require
    [todo.subscribe :as sub]))

(defn task-view [task]
  [:div {}
   (str "[" (task :tag) "]" " " (task :description))])

(defn tasks-view [tasks]
  [:div {:x 1}
   (for [task tasks]
     [task-view task])])

(defn group-view [group]
  [:div {:bg :red}
   [:div {:bg :blue} (group :description)]
   [tasks-view (group :tasks)]])

(defn groups-view [groups]
  [:div {:bg :yellow}
   (for [group groups]
     [group-view group])])

(defn app-view []
  [:div {:height :stretch
         :bg :green}
   [groups-view (sub/tasks)]])
