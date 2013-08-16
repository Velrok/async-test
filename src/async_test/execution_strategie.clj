(ns async-test.execution-strategie
  (:require [clojure.core.async :refer :all]))


(defn plain-apply [f coll]
  (for [i coll]
    (f i)))

(defn pmap-apply [f coll]
  (pmap f coll))


(defn async-apply [f coll]
  (let [r-chans (doall
                  (for [i coll]
                    (go (f i))))]
    (for [c r-chans]
      (<!! c))))
