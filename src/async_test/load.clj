(ns async-test.load
  (:require [clojure.math.numeric-tower :as math]))


(defn increasing [n]
  (for [i (range n)]
    (math/round (math/expt 1.08 i))))

(defn decreasing [n]
 (reverse (increasing n)))

(defn random [n]
  (let [max-load (reduce max (increasing n))]
    (map (fn [_] (rand-int max-load))
         (range n))))

(defn constant [n & {:keys [work-load]
                          :or {work-load 100}}]
  (map (constantly work-load)
       (range n)))
