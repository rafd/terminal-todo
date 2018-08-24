(ns reterm.views.inspector
  (:require
    [reterm.state :as state]))

(defn inspector-view
  [opts]
  [:div (merge {:bg :black}
               opts)
   ;; cursor xy
   [:div {}
    ":x " (str (:x (state/cursor))) " "
    ":y " (str (:y (state/cursor)))]])

