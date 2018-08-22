(ns todo.core
  (:require
    [reterm.core :as r]
    [todo.views :refer [app-view]]
    [todo.transact :as tx]))

(defn start! [mode]
  (tx/init!)
  (r/start! mode [app-view]))

(defn -main [& args]
  (let [mode (if (= (first args) "swing")
               :swing
               :text)]
    (start! mode)))
