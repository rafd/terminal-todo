(ns todo.views
  (:require
    [todo.subscribe :as sub]
    [todo.transact :as tx]))

(defn insert-char
  "Insert character i into string s at position i,
   if insertion point is beyond end of string, pads with spaces"
  [s c i]
  (if (<= i (count s))
   (str (subs s 0 i) c (subs s i))
   (str s (apply str (repeat (- i (count s)) " ")) c)))

(defn input-view [{:keys [value on-change]}]
  [:div {:bg :green
         :on-keypress
         (fn [key {:keys [x]}]
           (when (char? key)
             (tx/cursor-right!)
             (on-change (insert-char value key x))))}
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
         :on-keypress (fn [key _]
                        (case key
                          :up (tx/cursor-up!)
                          :down (tx/cursor-down!)
                          :left (tx/cursor-left!)
                          :right (tx/cursor-right!)
                          :escape (tx/stop!)
                          ; default
                          nil))}
   [groups-view (sub/groups)]])
