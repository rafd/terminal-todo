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
  {:description (random-text 5)
   :tag (random-text 1)})

(defn init! []
  (swap! state assoc :tasks
         (->> (repeatedly generate-task)
              (take 50))))
