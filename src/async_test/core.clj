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

(defn plot-series [title x-label y-label x-y-series]
  (let [first-series (first x-y-series)
        plott (scatter-plot (:x first-series)
                            (:y first-series)
                            :series-label (:label first-series)
                            :x-label x-label
                            :y-label y-label
                            :legend true)]
    (doseq [series (rest x-y-series)]
      (add-points plott
                  (:x series)
                  (:y series)
                  :series-label (:label series)))
    plott))

(defn box-plot-series [title y-series]
  (let [first-series (first y-series)
        plott (box-plot (:x first-series)
                        :series-label (:label first-series)
                        :legend true
                        :title title)]
    (doseq [series (rest y-series)]
      (add-box-plot plott
                    (:x series)
                    :series-label (:label series)))
    plott))

; -- loads

(defn increasing-load [n]
  (for [i (range n)]
    (math/round (math/expt 1.08 i))))

(defn decreasing-load [n]
  (reverse (increasing-load n)))

(defn random-load [n]
  (map (fn [_] (rand-int 2037))
       (range n)))

(defn constant-load [n]
  (map (constantly 500)
       (range n)))

(def loads-plot
  (plot-series "loads in ms" "run" "wait in ms"
               [{:label "incremental-load"
                 :x (range 100)
                 :y incremental-load}
                {:label "decremental-load"
                 :x (range 100)
                 :y decremental-load}
                {:label "randomized-load"
                 :x (range 100)
                 :y randomized-load}]))

(incanter/view loads-plot)

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
             (performance-test strat iterations load)))))

(def last-run (incanter/to-dataset (run-test-suit 2 3)))
(incanter/view last-run)

(defn plot-load-type [load-type data]
  (->> data
       (incanter/$where {:load-type load-type})
       (incanter/$ [:exec-strat :series])
       :rows
       (map #(rename-keys % {:exec-strat :label
                         :series :x}))
       (box-plot-series (str load-type " load"))
       view))


(plot-load-type :incremental last-run)
(plot-load-type :decremental last-run)
(plot-load-type :random last-run)
(plot-load-type :random last-run)
