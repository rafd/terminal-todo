(ns todo.transact
  (:require
    [reterm.core :as r]
    [reterm.state :as rs]
    [todo.state :refer [state]]
    [todo.generate :refer [generate-state]]))

(defn update-task-description!
  [id description]
  (swap! state assoc-in [:tasks id :description] description))

(defn update-task-tag!
  [id tag]
  (swap! state assoc-in [:tasks id :tag] tag))

(defn new-task!
  [group-id]
  (let [task {:id (gensym)
              :description ""
              :tag ""}]
    (swap! state assoc-in [:tasks (task :id)] task)
    (swap! state update-in [:groups group-id :tasks-ids] conj (task :id))
    (rs/cursor-jump! (str "task-description-" (task :id)))))

(defn- vec-move [v index offset]
  (let [destination-index (+ index offset)]
    (if (and (not= offset 0)
             (<= 0 index (dec (count v)))
             (<= 0 destination-index (dec (count v))))
      (let [target-item (v index)
            swap-item (v destination-index)]
        (assoc v index swap-item destination-index target-item))
      v)))

(defn delete-task!
  [id]
  (swap! state update :tasks dissoc id))

(defn move-task!
  [group-id task-id direction]
  (let [task-index (.indexOf (get-in @state [:groups group-id :task-ids]) task-id)]
    (case direction
      :up (swap! state update-in [:groups group-id :task-ids] (fn [task-ids] (vec-move task-ids task-index 1)))
      :down (swap! state update-in [:groups group-id :task-ids] (fn [task-ids] (vec-move task-ids task-index -1))))))

(defn init! []
  (reset! state (generate-state)))

; TODO should find a better way to do this
(def cursor-up! rs/cursor-up!)
(def cursor-down! rs/cursor-down!)
(def cursor-left! rs/cursor-left!)
(def cursor-right! rs/cursor-right!)
(def cursor-set! rs/cursor-set!)
(def stop! r/stop!)
