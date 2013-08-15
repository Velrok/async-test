(ns async-test.core
  (:require [clojure.core.async :refer :all]
            [incanter.charts :refer :all]
            [incanter.core :as incanter]
            [clojure.set :refer [rename-keys]]
            [extra-time.core :refer :all]
            [clojure.math.numeric-tower :as math]))


; ---- helper functions

(defn slow-fn
  "Waits for wait-ms and returns the value itself."
  [wait-ms]
  (Thread/sleep wait-ms)
  wait-ms)


; -- loads

(defn increasing-load [n]
  (for [i (range n)]
    (math/round (math/expt 1.08 i))))

(defn decreasing-load [n]
  (reverse (increasing-load n)))

(defn random-load [n]
  (let [max-load (reduce max (increasing-load n))]
    (map (fn [_] (rand-int max-load))
         (range n))))

(defn constant-load [n]
  (map (constantly 500)
       (range n)))


; -- parallel implementations

(defn plain-apply [f coll]
  (for [i coll]
    (f i)))

(defn pmap-apply [f coll]
  (pmap f coll))

(defn async-apply [f coll]
  (let [r-chans (for [i coll]
                  (go (f i)))]
    (for [c r-chans]
      (<!! c))))


;--- run performance tests

(defn performance-test [execution-strat iteration load-distribution]
  (let [timings (for [i (range iteration)]
                  (->> (with-times
                           (doall
                             (execution-strat slow-fn
                                              load-distribution)))
                       first
                       :total
                       first))]
    {:min (reduce min timings)
     :max (reduce max timings)
     :mean (/ (reduce + timings)
                    (count timings))
     :median (nth (sort timings)
                  (math/round (/ (count timings)
                                 2)))
     :series timings}))

(defn run-test-suit[iterations problem-size]
  (doall
    (for [loads [[:incremental (increasing-load problem-size)]
                 [:decremental (decreasing-load problem-size)]
                 [:constant    (constant-load problem-size)  ]
                 [:random      (random-load problem-size)    ]]
          exec-strat [[:plain plain-apply]
                      [:pmap  pmap-apply ]
                      [:async async-apply]]]
      (let [[load-type load] loads
            [strat-desc strat] exec-strat]
        (merge {:exec-strat strat-desc
                :load-type load-type}
              (performance-test strat iterations load))))))
