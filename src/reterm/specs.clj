(ns reterm.specs
  (:require
    [clojure.spec.alpha :as s]
    [spell-spec.alpha :as spell]
    [expound.alpha :as exp]
    [spell-spec.expound]))

(s/def ::color
  (s/spec #{:black
            :blue
            :cyan
            :default
            :green
            :magenta
            :red
            :white
            :yellow}))

(s/def ::label string?)
(s/def ::position (s/spec #{:relative :absolute :clear}))
(s/def ::width (s/or :integer pos-int?
                     :special (s/spec #{:stretch :content})))
(s/def ::height (s/or :integer pos-int?
                      :special (s/spec #{:stretch :content})))
(s/def ::x-offset int?)
(s/def ::y-offset int?)
(s/def ::fg ::color)
(s/def ::bg ::color)
(s/def ::on-change fn?)
(s/def ::on-keypress fn?)
(s/def ::id string?)

(s/def ::div-opts
  (spell/strict-keys
    :opt-un [::label
             ::position
             ::width
             ::id
             ::height
             ::x-offset
             ::y-offset
             ::on-keypress
             ::on-change ;; only input
             ::value ;; only input
             ::fg
             ::bg]))

(defn validate! [spec data]
  (when-not (s/valid? spec data)
    (exp/expound spec data)
    (println "Exit")
    (System/exit 0)))
