(ns reterm.draw
  (:require
    [clojure.string :as string]
    [lanterna.screen :as s]))

(declare draw!)

(defn draw-div! 
  [screen context [_ opts & content] draw?]
  (let [x (+ (context :x)
             (or (opts :x) 0))
        y (+ (context :y)
             (or (opts :y) 0))
        context {:x x
                 :y y
                 :width (or (opts :width)
                            (- (context :width) 
                               (or (opts :x) 0)))
                 :height (cond
                           (int? (opts :height))
                           (opts :height)

                           (= :stretch (opts :height)) 
                           (context :height)

                           :else
                           1) 
                 :fg (or (opts :fg)
                         (context :fg))
                 :bg (or (opts :bg)
                         (context :bg))}]

    ; call draw! w/o actually drawing (final argument is false)
    ; to figure out the content height
    ; so we can draw the background
    (let [wh (draw! screen context content false)]
      (doall
        (for [x (range (context :x) (+ (context :x) 
                                       (context :width)))
              y (range (context :y) (+ (context :y) 
                                       (max (context :height)
                                            (wh :height))))]
          (when draw?
            (s/put-string screen 
                          x y
                          " "
                          {:bg (context :bg)})))))

    ; draw content
    (let [wh (draw! screen context content draw?)]
      {:width (context :width)
       :height (wh :height)})))

(defn draw-string! [screen context string draw?]
  (when draw?
    (s/put-string screen
                  (context :x)
                  (context :y)
                  string
                  {:bg (context :bg)
                   :fg (context :fg)}))
  {:width (count string)
   :height 1})

(defn node->type [node]
  (cond
    (and (coll? node) (keyword? (first node)) (= :div (first node)))
    :div-list

    (and (coll? node) (fn? (first node)))
    :fn-list

    (coll? node)
    :node-list

    (string? node)
    :string

    (nil? node)
    :nil))

(defn draw-list! [screen original-context node-list draw?]
  (let [node-data (reduce 
                    (fn [{:keys [context wh]} node]
                      (let [node-type (node->type node)
                            new-wh (draw! screen context node draw?)]
                        {:context (case node-type
                                    :string
                                    (-> context 
                                        (assoc :x (+ (context :x) (new-wh :width)))
                                        (assoc :y (+ (context :y) (new-wh :height) -1)))
                                    ; default (clear line)
                                    (-> context 
                                        (assoc :x (original-context :x))
                                        (assoc :y (+ (context :y) (new-wh :height))))) 
                         :wh {:width (+ (wh :width) (new-wh :width))
                              :height (+ (wh :height) (new-wh :height))}})) 
                    {:context original-context 
                     :wh {:width 0
                          :height 0}}
                    node-list)]
    {:width (-> node-data :wh :width)
     :height (-> node-data :wh :height)}))

(defn draw-nothing! [screen context _ draw?]
  {:width 0
   :height 0})

(defn draw! [screen context node draw?]
  (case (node->type node)
    :div-list
    (draw-div! screen context node draw?)

    :fn-list
    (draw! screen context (apply (first node) (rest node)) draw?)

    :node-list
    (draw-list! screen context node draw?)

    :string
    (draw-string! screen context node draw?)

    :nil
    (draw-nothing! screen context node draw?)))
