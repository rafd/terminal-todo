(ns reterm.state)

; .------> x
; |
; |
; V y

; state

(def state
  (atom {:cursor {:x 0
                  :y 0
                  :value nil}
         :screen {:width nil
                  :height nil}
         :run? true}))

; helpers

(defn bound [lower value upper]
  (cond
    (< value lower)
    lower
    (> value upper)
    upper
    :else
    value))

; subscriptions

(defn cursor []
  (@state :cursor))

(defn running? []
  (@state :run?))

; transactions

(defn cursor-set!
  "Expects a map with :x and :y
   Values can be nil, an integer or a function"
  [{:keys [x y]}]
  (swap! state update :cursor
         (fn [cursor]
           (assoc cursor
                  :x (let [x' (cond
                                (nil? x) (cursor :x)
                                (fn? x) (x (cursor :x))
                                (int? x) x)]
                      (bound 0 x' (get-in @state [:screen :width])))
                  :y (let [y' (cond
                                (nil? y) (cursor :y)
                                (fn? y) (y (cursor :y))
                                (int? y) y)]
                      (bound 0 y' (get-in @state [:screen :height])))))))

(defn cursor-up! []
  (cursor-set! {:y dec}))

(defn cursor-down! []
  (cursor-set! {:y inc}))

(defn cursor-left! []
  (cursor-set! {:x dec}))

(defn cursor-right! []
  (cursor-set! {:x inc}))

(defn set-running-state! [bool]
  (swap! state assoc :run? bool))

(defn store-screen-size! [[width height]]
  (swap! state assoc :screen {:width width
                              :height height}))

(defn ->nodes
  "Given dom hierarchy, convert it to a single list of nodes"
  [dom-node]
  (if (dom-node :content)
    (concat [(dissoc dom-node :content)]
            (mapcat ->nodes (dom-node :content)))
    [dom-node]))

(defn handle-keypress!
  [event root-dom-node]
  (let [containing-nodes
        (->> root-dom-node
             ->nodes
             ;; only keep nodes with an :on-keypress
             (filter (fn [dom-node]
                       (get-in dom-node [:opts :on-keypress])))
             ;; only keep nodes whose bounds contain the cursor
             (filter (fn [dom-node]
                       (and (<= (get-in dom-node [:context :x])
                                (get-in (cursor) [:x])
                                (+ (get-in dom-node [:context :x])
                                   (get-in dom-node [:context :width])
                                   -1))
                            (<= (get-in dom-node [:context :y])
                                (get-in (cursor) [:y])
                                (+ (get-in dom-node [:context :y])
                                   (get-in dom-node [:context :height])
                                   -1)))))
             reverse)]
    ;; call :on-keypress on the nodes (in order of child->parent->root, ie. capture)
    (doseq [dom-node containing-nodes]
      (let [on-keypress-fn (get-in dom-node [:opts :on-keypress])
            relative-cursor-position {:x (- (get (cursor) :x)
                                            (get-in dom-node [:context :x]))
                                      :y (- (get (cursor) :y)
                                            (get-in dom-node [:context :y]))}]
        (on-keypress-fn (merge event relative-cursor-position))))))

