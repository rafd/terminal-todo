(ns reterm.views
  (:require
    [reterm.state :as rs]))

(defn insert-char
  "Insert character i into string s at position i,
   if insertion point is beyond end of string, pads with spaces"
  [s c i]
  (if (<= i (count s))
   (str (subs s 0 i) c (subs s i))
   (str s (apply str (repeat (- i (count s)) " ")) c)))

(defn remove-char
  [s i]
  (if (< i (count s))
    (str (subs s 0 i) (subs s (inc i)))
    s))

(defn input-view
  [{:keys [value on-change]}]
  [:div {:on-keypress
         (fn [key {:keys [x]}]
           (if (char? key)
             (do
               (rs/cursor-right!)
               (on-change (insert-char value key x)))
             (case key
               :backspace
               (do
                 (when (< 0 x)
                   (rs/cursor-left!)
                   (on-change (remove-char value (dec x)))))
               nil)))}
   value])

