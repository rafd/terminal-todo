(ns todo.state)

(def state 
  (atom {:tasks [{:description "task 1"}
                 {:description "task 2"}
                 {:description "task 3"}]
         :cursor {:x 0
                  :y 0
                  :value nil}
         :screen {:width nil
                  :height nil}
         :run? true}))
