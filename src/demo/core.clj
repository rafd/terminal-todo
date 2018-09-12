(ns demo.core
  (:require
    [reterm.core :as r]))

(defn task-view [task]
  [:div {:bg :default
         :fg :default}
   task])

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
         :width :stretch
         :height :stretch
         :on-keypress (fn [event]
                        (case (event :key)
                          :escape (r/stop!)
                          nil))}
   "hello" " " "world"
   [:div {:bg :green} "test"]
   [:div {:bg :yellow} "test"]
   "test"
   [tasks-view ["foo" "bar" "baz"]]
   "after"])

(defn start! []
  (r/start! :swing [app-view]))
