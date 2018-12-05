(ns todo.views
  (:require
    [todo.subscribe :as sub]
    [todo.transact :as tx]))

(defn tag-view
  [task]
  [:div {:position :relative}
   "["
   [:input {:position :relative
            :value (task :tag)
            :on-change (fn [tag]
                         (tx/update-task-tag! (task :id) tag))}]
   "]"])

(defn task-view [group-id task]
  [:div {:width :stretch
         :on-keypress (fn [event]
                        (cond
                          (and (= \d (event :key))
                               (event :ctrl))
                          (tx/delete-task! (task :id))

                          (and (= \j (event :key))
                               (event :ctrl)
                               (event :alt))
                          (tx/move-task! group-id (task :id) :up)

                          (and (= \k (event :key))
                               (event :ctrl)
                               (event :alt))
                          (tx/move-task! group-id (task :id) :down)))}
   " "
   [tag-view task]
   " "
   [:input {:id (str "task-description-" (task :id))
            :value (task :description)
            :position :relative
            :width :stretch
            :on-change (fn [value]
                         (tx/update-task-description! (task :id) value))}]])

(defn tasks-view [group-id tasks]
  [:div {:bg :red
         :width :stretch}
   (for [task tasks]
     [task-view group-id task])])

(defn group-view [group]
  [:div {:label "group-view"
         :bg :black
         :width :stretch
         :on-keypress (fn [event]
                        (when (and (= \i (event :key))
                                   (event :ctrl))
                          (tx/new-task! (group :id))))}
   [:div {:bg :blue
          :width :stretch}
    (group :description)]
   [tasks-view (group :id) (sub/group-tasks (group :id))]])

(defn groups-view [groups]
  [:div {:label "groups-view"
         :width :stretch}
   (for [group groups]
     [group-view group])])

(defn app-view []
  [:div {:label "app-view"
         :height :stretch
         :width :stretch
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

