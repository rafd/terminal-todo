(ns reterm.draw
  (:require
    [lanterna.screen :as s]))

(defmulti draw! (fn [_ {:keys [type]}] type))

(defmethod draw! :div
  [screen {:keys [opts context content]}]

    ; draw the background
    (when (context :bg)
      (doall
        (for [x (range (context :x) (+ (context :x)
                                       (context :width)))
              y (range (context :y) (+ (context :y)
                                       (context :height)))]
          (s/put-string screen
                        x y
                        " "
                        {:bg (context :bg)}))))

    ; draw content
    (doall
     (for [dom-node content]
       (draw! screen dom-node))))

(defmethod draw! :string
  [screen {:keys [context value]}]
  (s/put-string screen
                (context :x)
                (context :y)
                value
                ;; use :bg-fall-through instead of :bg
                ;; because :bg may be nil ("transparent")
                ;; but our strings need a background
                {:bg (context :bg-fall-through)
                 :fg (context :fg)}))

(defmethod draw! :default
  [_ dom-node]
  (println "Don't know how to handle node:" (pr-str dom-node)))
