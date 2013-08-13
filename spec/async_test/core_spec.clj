(ns async-test.core-spec
  (:use [async-test.core]
        [speclj.core]))

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
