(ns async-test.core
  (:require [criterium.core :as criterium]
            [async-test.execution-strategie :as exec-strat]
            [async-test.load :as work-load]))


; ---- helper functions

(defn slow-fn
  "Waits for wait-ms and returns the value itself."
  [wait-ms]
  (Thread/sleep wait-ms)
  wait-ms)

(defn fire []
  (criterium/with-progress-reporting
    (let [problem-size 5]
      (doall
        (for [[load-type load] [[:incremental (work-load/increasing problem-size)]
                                [:decremental (work-load/decreasing problem-size)]
                                [:random      (work-load/random     problem-size)]]
              [strat-type strat] [[:pmap  exec-strat/pmap-apply ]
                                  [:async exec-strat/async-apply]
                                  [:plain exec-strat/plain-apply]]]
          (do
            (println ">>>>> " problem-size "-" load-type "-" strat-type)
            (criterium/bench (strat slow-fn load))))))))


;--- run performance tests

; (defn performance-test [execution-strat iteration load-distribution]
;   (let [timings (for [i (range iteration)]
;                   (->> (with-times
;                            (doall
;                              (execution-strat slow-fn
;                                               load-distribution)))
;                        first
;                        :total
;                        first))]
;     {:min (reduce min timings)
;      :max (reduce max timings)
;      :mean (/ (reduce + timings)
;                     (count timings))
;      :median (nth (sort timings)
;                   (math/round (/ (count timings)
;                                  2)))
;      :series timings}))
;
; (defn run-test-suit[iterations problem-size]
;   (doall
;     (for [loads [[:incremental (increasing-load problem-size)]
;                  [:decremental (decreasing-load problem-size)]
;                  [:constant    (constant-load problem-size)  ]
;                  [:random      (random-load problem-size)    ]]
;           exec-strat [[:plain plain-apply]
;                       [:pmap  pmap-apply ]
;                       [:async async-apply]]]
;       (let [[load-type load] loads
;             [strat-desc strat] exec-strat]
;         (merge {:exec-strat strat-desc
;                 :load-type load-type}
;               (performance-test strat iterations load))))))
