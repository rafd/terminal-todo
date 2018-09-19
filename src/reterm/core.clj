(ns reterm.core
  (:require
    [lanterna.screen :as s]
    [reterm.draw :refer [draw!]]
    [reterm.dom :as dom]
    [reterm.state :as state]))

(defn- run-loop! [screen root-component]
  (let [[width height] (s/get-size screen)
        ;; store size immediately so that it
        ;; can be accessed by certain views
        _ (state/store-screen-size! [width height])
        root-dom-node (dom/calculate-root {:width width
                                           :height height}
                                          root-component)]

    (state/run-pre-draw-queue! root-dom-node)

    (draw! screen root-dom-node)

    (let [cursor (state/cursor)]
      (when (cursor :value)
        (s/put-string screen
                      (cursor :x)
                      (cursor :y)
                      (cursor :value)))
      (s/move-cursor screen
                     (cursor :x)
                     (cursor :y)))

    (s/redraw screen)

    (state/handle-keypress!
      (s/get-key-blocking-with-modifiers screen)
      root-dom-node)))

(defn start! [mode root-component]
  (let [screen (s/get-screen mode)]
    (s/start screen)
    (state/set-running-state! true)

    (while (state/running?)
      (run-loop! screen root-component))

    (s/stop screen)))

(defn stop! []
  (state/set-running-state! false))
