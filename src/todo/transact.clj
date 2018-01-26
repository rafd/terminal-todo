(ns todo.transact
  (:require
    [clojure.string :as string]
    [todo.state :refer [state]]))

(defn- random-word []
  (rand-nth ["hello" "world" "foo" "bar" "baz"]))
 
(defn- random-text [word-count]
  (->> (repeatedly random-word)
       (take word-count)
       (string/join " ")))

(defn- generate-task []
  {:description (str "task " (random-text 5))
   :tag (str "tag-" (random-text 1))})

(defn- generate-task-group []
  {:description (str "group " (random-text 5) ":")
   :tasks (->> (repeatedly generate-task)
               (take (rand-int 10)))})

(defn init! []
  (swap! state assoc :tasks
         (->> (repeatedly generate-task-group)
              (take 3))))
