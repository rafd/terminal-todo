(ns todo.generate
  (:require
    [clojure.string :as string]))

(defn- random-id []
  (str (gensym)))

(defn- random-word []
  (rand-nth ["hello" "world" "foo" "bar" "baz"]))
 
(defn- random-text [word-count]
  (->> (repeatedly random-word)
       (take word-count)
       (string/join " ")))

(defn- generate-task [group-id]
  {:id (random-id)
   :description (str "task " (random-text 5))
   :tag (str "tag-" (random-text 1))
   :group-id group-id})

(defn- generate-group []
  {:id (random-id)
   :description (str "group " (random-text 5) ":")})

(defn- key-by-id [coll]
  (reduce (fn [memo i]
            (assoc memo (i :id) i))
          {}
          coll))

#_{:tasks [:task1id {:id :task1id
                     :description "task1"
                     :tag "foo"
                     :group-id :group1id}]
   :groups [:group1id {:id :group1id
                       :description "group1"}]}

(defn generate-state []
  (let [groups (->> (repeatedly generate-group)
                    (take 3))
        tasks (->> (repeatedly 
                     (fn [] 
                       (let [group-id (rand-nth (map :id groups))]
                       (generate-task group-id))))
                   (take 15))]
    {:groups (key-by-id groups)
     :tasks (key-by-id tasks)}))
