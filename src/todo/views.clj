(ns todo.views
  (:require
    [todo.subscribe :as sub]
    [todo.transact :as tx]))

(defn task-view [task]
  [:div {}
   (str "[" (task :tag) "]" " ")
   [:input {:value (task :description)
            :on-change (fn [value]
                         (tx/update-task-description! (task :id) value))}]])

(defn tasks-view [tasks]
  [:div {:x 1}
   (for [task tasks]
     [task-view task])])

(defn group-view [group]
  [:div {:bg :red}
   [:div {:bg :blue} (group :description)]
   [tasks-view (sub/group-tasks (group :id))]])

(defn groups-view [groups]
  [:div {:bg :yellow}
   (for [group groups]
     [group-view group])])

(defn app-view []
  [:div {:height :stretch
         :bg :green
         :on-keypress (fn [event]
                        (case (event :key)
                          :up (tx/cursor-up!)
                          :down (tx/cursor-down!)
                          :left (tx/cursor-left!)
                          :right (tx/cursor-right!)
                          :escape (tx/stop!)
                          ; default
                          nil))}
   [groups-view (sub/groups)]
   [:inspector {}]])
