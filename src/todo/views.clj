(ns todo.views
  (:require
    [todo.subscribe :as sub]
    [todo.transact :as tx]))


(defn input-view [{:keys [value on-change]}]
  [:div {:on-keypress 
         (fn [key]
           (when (= key \d)
             (on-change "CHANGED")))}
   value])


(defn task-view [task]
  [:div {}
   (str "[" (task :tag) "]" " ") 
   [input-view {:value (task :description)
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
         :on-keypress (fn [key]
                        (case key
                          \k (tx/cursor-up!)
                          \j (tx/cursor-down!)
                          \h (tx/cursor-left!)
                          \l (tx/cursor-right!)
                          :escape (tx/set-running-state! false)
                          ; default 
                          nil))}
   [groups-view (sub/groups)]])
