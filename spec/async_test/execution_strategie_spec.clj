(ns async-test.execution-strategie-spec
  (:require [speclj.core :refer :all]
            [async-test.execution-strategie :refer :all]))

(describe "test funtions"
  (with input (range 30))
  (it "returns the identity"
    (should= @input
              (plain-apply identity @input)))
  (it "plain-apply equals pmap-apply"
    (should= (plain-apply identity @input)
             (pmap-apply  identity @input)))
  (it "plain-apply equals async-apply"
    (should= (plain-apply identity @input)
             (async-apply identity @input))))
