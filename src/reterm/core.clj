(ns reterm.core
  (:require
    [clojure.string :as string]
    [lanterna.screen :as s]))

(declare draw!)

(defn draw-div! 
  [screen context [_ opts & content]]
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
    ; draw background
    (doall
      (for [x (range (context :x) (+ (context :x) 
                                     (context :width)))
            y (range (context :y) (+ (context :y) 
                                     (context :height)))]
        (s/put-string screen 
                      x y
                      " "
                      {:bg (context :bg)})))
    ; draw content
    (draw! screen context content)
    
    {:x (+ (context :x))
     :y (+ (context :y) (context :height))}))

(defn draw-string! [screen context string]
  (s/put-string screen
                (context :x)
                (context :y)
                string
                {:bg (context :bg)
                 :fg (context :fg)})
  
  {:x (+ (context :x) (count string))
   :y (context :y)})

(defn draw-list! [screen context node-list]
  (reduce 
    (fn [context node]
      (let [xy (draw! screen context node)]
        (merge context 
               xy))) 
    context 
    node-list))

(defn node->type [node]
  (cond
    (and (coll? node) (keyword? (first node)) (= :div (first node)))
    :div-list

    (and (coll? node) (fn? (first node)))
    :fn-list

    (coll? node)
    :node-list

    (string? node)
    :string))

(defn draw! [screen context node]
  ; (println (node->type node) node context)
  (case (node->type node)
    :div-list
    (draw-div! screen context node)

    :fn-list
    (draw! screen context (apply (first node) (rest node)))

    :node-list
    (draw-list! screen context node)

    :string
    (draw-string! screen context node)))

(defn render [screen component]
  (let [[width height] (s/get-size screen)
        context {:width width
                 :height height
                 :x 0
                 :y 0
                 :bg :default
                 :fg :default}]
    (draw! screen context component)))
