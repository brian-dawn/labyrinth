(ns labyrinth.core-test
  (:require [clojure.test :refer :all]
            [labyrinth.core :refer :all]))

(deftest path-tests
  (testing "Path creation"
    (is (= "/foo/bar" (str (path "/foo/bar"))))
    (is (= "/foo/bar" (str (path "/foo/bar/"))))
    (is (= "/foo/bar" (str (path "/foo//bar"))))
    ))
