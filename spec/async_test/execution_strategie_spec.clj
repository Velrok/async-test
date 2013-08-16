(ns async-test.execution-strategie-spec
  (:require [async-test.core :refer [slow-fn]]
            [speclj.core :refer :all]
            [async-test.execution-strategie :refer :all]))

(describe "test funtions"
  (with input (range 30))
  (it "returns the identity"
    (should= @input
            (plain-apply slow-fn @input)))
  (it "plain-apply equals pmap-apply"
    (should= (plain-apply slow-fn @input)
             (pmap-apply  slow-fn @input)))
  (it "plain-apply equals async-apply"
    (should= (plain-apply slow-fn @input)
             (async-apply slow-fn @input))))
