(ns todo.helpers)

; .------> x
; |
; |
; V y

(defn bound [lower value upper]
  (cond 
    (< value lower)
    lower
    (> value upper)
    upper
    :else
    value)) 
