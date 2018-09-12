(ns reterm.views.inspector
  (:require
    [reterm.state :as state]))

(defonce last-key-event (atom nil))

(defn inspector-view
  [opts]
  [:div (merge {:position :absolute
                :width (get-in @state/state [:screen :width])
                :height (get-in @state/state [:screen :height])
                :on-keypress (fn [event] (reset! last-key-event event))}
               opts)
   ;; cursor xy
   [:div {:bg :black
          :y-offset (dec (get-in @state/state [:screen :height]))}
    ":x " (str (:x (state/cursor))) " "
    ":y " (str (:y (state/cursor))) " "
    ":last "
    (when (:ctrl @last-key-event) "ctrl-")
    (when (:alt @last-key-event) "alt-")
    (str (:key @last-key-event))]])

