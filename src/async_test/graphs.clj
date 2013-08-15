(ns async-test.graphs
  (:require [clojure.core.async :refer :all]
            [async-test.core :as core]
            [incanter.charts :refer :all]
            [incanter.core :as incanter]
            [clojure.set :refer [rename-keys]]
            [extra-time.core :refer :all]
            [clojure.math.numeric-tower :as math]))


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

(defn create-loads-plot [problem-size]
  (plot-series "loads in ms" "run" "wait in ms"
               [{:label "incremental-load"
                 :x (range problem-size)
                 :y (core/increasing-load problem-size)}
                {:label "decremental-load"
                 :x (range problem-size)
                 :y (core/decreasing-load problem-size)}
                {:label "randomized-load"
                 :x (range problem-size)
                 :y (core/random-load problem-size)}]))

(defn plot-load-type [load-type data]
  (->> data
       (incanter/$where {:load-type load-type})
       (incanter/$ [:exec-strat :series])
       :rows
       (map #(rename-keys % {:exec-strat :label
                         :series :x}))
       (box-plot-series (str load-type " load"))
       incanter/view))

(defn fire []
  (let [problem-size 10
        iterations 5
        last-run (incanter/to-dataset
                   (core/run-test-suit iterations
                                       problem-size))]
    (incanter/view (create-loads-plot problem-size))
    (incanter/view last-run)
    (plot-load-type :incremental last-run)
    (plot-load-type :decremental last-run)
    (plot-load-type :random last-run)
    (plot-load-type :random last-run)))
