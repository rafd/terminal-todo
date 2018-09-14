(ns reterm.dom
  (:require
    [clojure.walk :as walk]
    [reterm.specs :as spec]
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

(def opts-default
  {:position :clear ;; :absolute :relative
   :width :content ;; :stretch integer
   :height :content ;; :stretch integer
   :x-offset 0
   :y-offset 0
   :fg nil
   :bg nil})

(defn extract-opts
  "Gets opts from a node, with defaults set"
  [[_ opts & _ :as node]]
  (spec/validate! :reterm.specs/div-opts opts)
  (merge-with (fn [a b]
                (if (nil? b)
                  a
                  b))
              opts-default
              opts))

(defmulti calculate
  (fn [parent-context node]
    (node->type node)))

(defmethod calculate :keyword-list
  [parent-context [type opts & nodes :as node]]
  (let [[type opts & nodes :as node] (case type
                                       :input
                                       (input-view opts)
                                       :inspector
                                       (inspector-view opts)
                                       ; else
                                       (concat [type opts] nodes))
        opts (extract-opts node)
        ; adjust context based on opts
        context {:x (case (opts :position)
                      :absolute
                      (opts :x-offset)
                      (list :relative :clear)
                      (+ (parent-context :x)
                         (opts :x-offset)))
                 :y (case (opts :position)
                      :absolute
                      (opts :y-offset)
                      (list :relative :clear)
                      (+ (parent-context :y)
                         (opts :y-offset)))
                 :position (opts :position)
                 :width (case (opts :width)
                          :content
                          :content ; will be replaced later with children's width
                          :stretch
                          (if (= :content (parent-context :width))
                            :content
                            (- (parent-context :width)
                               (opts :x-offset)))
                          ;; else, integer
                          (opts :width))
                 :height (case (opts :height)
                           :content
                           :content ; will be replaced later with children's width
                           :stretch
                           (if (= :content (parent-context :height))
                             :content
                             (- (parent-context :height)
                                (opts :y-offset)))
                           ;; else, integer
                           (opts :height))
                 :fg (or (opts :fg)
                         (parent-context :fg))
                 ;; need to keep track of last non-nil :bg
                 ;; so that strings can be drawn properly
                 :bg-fall-through (or (opts :bg)
                                      (parent-context :bg-fall-through))
                 :bg (opts :bg)}
        ;; unwrap any nested lists
        nodes (mapcat (fn [node]
                        (if (nil? (node->type node))
                          node
                          [node]))
                      nodes)
        ;; layout children and calculate size of this div
        node-data (reduce
                    (fn [memo node]
                      (let [initial-context (:next-initial-context (last memo))
                            total-wh (:total-wh (last memo))
                            clear? (and
                                     ;; node is configured to clear
                                     ;; TODO: figure out a way to avoid running calculate here
                                     ;; because we're also calculating below
                                     (= :clear (:position (:context (calculate initial-context node))))
                                     ;; node is not at left-most edge
                                     (not= (context :x) (initial-context :x)))
                            initial-context (if clear?
                                              ;; bump node to a new line
                                              (-> initial-context
                                                  (assoc :x (context :x))
                                                  (assoc :width (context :width))
                                                  (assoc :y (+ (context :y)
                                                               (total-wh :height))))
                                              initial-context)
                            node-type (node->type node)
                            node-info (calculate initial-context node)
                            child-context (:context node-info)]
                        (conj memo
                              {:node node-info
                               :next-initial-context (cond
                                                       (= :absolute (child-context :position))
                                                       initial-context

                                                       :else
                                                       (-> initial-context
                                                           (assoc :width
                                                                  (if (= :content (initial-context :width))
                                                                    :content
                                                                    (- (initial-context :width)
                                                                       (child-context :width))))
                                                           (assoc :x (+ (initial-context :x)
                                                                        (child-context :width)))))
                               :total-wh {:width (case (child-context :position)
                                                   :absolute
                                                   (total-wh :width)

                                                   :clear
                                                   (max (total-wh :width) (child-context :width))

                                                   :relative
                                                   (+ (total-wh :width)
                                                      (child-context :width)))
                                          :height (case (child-context :position)
                                                    :absolute
                                                    (total-wh :height)

                                                    :clear
                                                    (+ (total-wh :height) (child-context :height))

                                                    :relative
                                                    (max (total-wh :height) (child-context :height)))}})))
                    [{:next-initial-context context
                      :total-wh {:width 0
                                 :height 0}}]
                    nodes)]
    {:type type
     :opts opts
     :context (assoc context
                     :width (if (= (context :width) :content)
                              (-> node-data last :total-wh :width)
                              (context :width))
                     :height (if (= (context :height) :content)
                               (-> node-data last :total-wh :height)
                               (context :height)))
     :content (mapv :node (rest node-data))}))

(defmethod calculate :fn-list
  [parent-context [f & args]]
  (calculate parent-context (apply f args)))

(defmethod calculate :string
  [parent-context value]
  {:type :string
   :context (merge parent-context
                   {:position :relative
                    :width (count value)
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
