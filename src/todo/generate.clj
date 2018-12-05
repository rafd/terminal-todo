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

(defn- generate-task []
  {:id (random-id)
   :description (str "task " (random-text 5))
   :tag (str "tag-" (random-text 1))})

(defn- generate-group [task-ids]
  {:id (random-id)
   :description (str "group " (random-text 5) ":")
   :task-ids task-ids})

(defn- key-by-id [coll]
  (reduce (fn [memo i]
            (assoc memo (i :id) i))
          {}
          coll))

#_{:tasks {:task1id {:id :task1id
                     :description "task1"
                     :tag "foo"}}
   :groups {:group1id {:id :group1id
                       :description "group1"
                       :tasks-ids [:task1id]}}}

(defn generate-state []
  (->> (repeatedly (fn []
                     (let [tasks (->> (repeatedly
                                        (fn []
                                          (generate-task)))
                                      (take 5))]
                       {:tasks tasks
                        :group (generate-group (mapv :id tasks))})))
       (take 3)
       (reduce (fn [memo {:keys [tasks group]}]
                 (-> memo
                     (update :groups assoc (group :id) group)
                     (update :tasks merge (key-by-id tasks))))
               {:groups {}
                :tasks {}})))
