(ns todo.state)

(def state 
  (atom {:cursor {:x 0
                  :y 0
                  :value nil}
         :screen {:width nil
                  :height nil}
         :run? true}))
