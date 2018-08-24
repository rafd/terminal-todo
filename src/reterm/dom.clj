(ns reterm.dom
  (:require
    [clojure.walk :as walk]
    [reterm.views.input :refer [input-view]]
    [reterm.views.inspector :refer [inspector-view]]))

(defn- node->type [node]
  (cond
    ; [:keyword opts & body]
    (and (coll? node) (keyword? (first node)))
    :keyword-list

    ; [fn & args]
    (and (coll? node) (fn? (first node)))
    :fn-list

    ; "foo""
    (string? node)
    :string))

; calculate takes a context and component, and returns the resulting "DOM" hierarchy
; (including evaluating any components, and unraveling lists)

; for example...
; input:

[:div {}
 [:div {}
  "foo"
  [:input {} "bar"]
  (for [word ["hello" "world"]]
    word)]]

; output:

{:type :div
 :opts {}
 :context {:x "..."}
 :content [{:type :div
            :opts {}
            :context {:x "..."}
            :content [{:type :string
                       :context {:x "..."}
                       :value "foo"}
                      {:type :input
                       :context {:x "..."}
                       :opts {}
                       :content [{:type :string
                                  :context {:x "..."}
                                  :value "bar"}]}
                      {:type :string
                       :context {:x "..."}
                       :value "hello"}
                      {:type :string
                       :context {:x "..."}
                       :value "world"}]}]}

(defmulti calculate
  (fn [parent-context node]
    (node->type node)))

(defmethod calculate :keyword-list
  [parent-context [type opts & nodes]]
  (let [[type opts & nodes] (case type
                              :input
                              (input-view opts)
                              :inspector
                              (inspector-view opts)
                              ; else
                              (concat [type opts] nodes))
        ; adjust context based on opts
        context {:x (+ (parent-context :x)
                       (or (opts :x) 0))
                 :y (+ (parent-context :y)
                       (or (opts :y) 0))
                 :width (or (opts :width)
                            (- (parent-context :width)
                               (or (opts :x) 0)))
                 :height (cond
                           (int? (opts :height))
                           (opts :height)

                           (= :stretch (opts :height))
                           (parent-context :height)

                           :else
                           1)
                 :fg (or (opts :fg)
                         (parent-context :fg))
                 :bg (or (opts :bg)
                         (parent-context :bg))}
        ; unwrap any nested lists
        nodes (mapcat (fn [node]
                        (if (nil? (node->type node))
                          node
                          [node]))
                      nodes)
        ; layout children and calculate size of this div
        node-data (reduce
                    (fn [memo node]
                      (let [initial-context (:next-initial-context (last memo))
                            div-wh (:div-wh (last memo))
                            node-type (node->type node)
                            node-info (calculate initial-context node)
                            new-context (:context node-info)]
                        (conj memo (case node-type
                                     :string ; inline-like
                                     {:node node-info
                                      :next-initial-context
                                      (-> initial-context
                                          (assoc :x (+ (initial-context :x) (new-context :width)))
                                          (assoc :y (+ (initial-context :y) (new-context :height) -1)))
                                      :div-wh
                                      {:width (+ (div-wh :width) (new-context :width))
                                       :height (+ (div-wh :height) (new-context :height) -1)}}
                                     ; default ; block-like
                                     {:node node-info
                                      :next-initial-context
                                      (-> initial-context
                                          (assoc :x (initial-context :x))
                                          (assoc :y (+ (initial-context :y) (new-context :height))))
                                      :div-wh
                                      {:width (+ (div-wh :width) (new-context :width))
                                       :height (+ (div-wh :height) (new-context :height))}}))))
                    [{:next-initial-context context
                      :div-wh {:width 0
                               :height 0}}]
                    nodes)]
    {:type type
     :opts opts
     :context (assoc context
                     :width (max (context :width) (-> node-data last :div-wh :width))
                     :height (max (context :height) (-> node-data last :div-wh :height)))
     :content (mapv :node (rest node-data))}))

(defmethod calculate :fn-list
  [parent-context [f & args]]
  (calculate parent-context (apply f args)))

(defmethod calculate :string
  [parent-context value]
  {:type :string
   :context (merge parent-context
                   {:width (count value)
                    :height 1})
   :value value})

(defn calculate-root
  [{:keys [width height]} root-component]
  (let [root-context {:width width
                      :height height
                      :x 0
                      :y 0
                      :bg :default
                      :fg :default}]
    (calculate root-context root-component)))
