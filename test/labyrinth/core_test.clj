(ns labyrinth.core-test
  (:require [clojure.test :refer :all]
            [labyrinth.core :refer :all]))

;; Ensure the tests also pass on a Windows system.

(def abs-path     (str root "foo" seperator "bar"))
(def abs-bar-path (str root "bar"))
(def rel-path     (str "foo" seperator "bar"))

(deftest path-tests
  (testing "path"
    (is (= abs-path (str (path (str root "foo/bar")))))
    (is (= abs-path (str (path (str root "foo/bar/")))))
    (is (= abs-path (str (path (str root "foo//bar")))))

    (is (= abs-path (str (path (str root "foo")  "bar"))))
    (is (= abs-path (str (path (str root "foo")  "bar"))))
    (is (= abs-path (str (path (str root "foo/") "bar/"))))

    (is (= rel-path     (str (path "foo"  "bar"))))
    (is (= rel-path     (str (path "foo/" "bar"))))
    (is (= abs-bar-path (str (path "foo"  (str root "bar"))))))

  (testing "join"
    (is (= rel-path     (str (join (path "foo")            (path "bar")))))
    (is (= rel-path     (str (join (path "foo/")           (path "bar")))))
    (is (= abs-bar-path (str (join (path "foo/")           (path (str root "bar"))))))
    (is (= abs-path     (str (join (path (str root "foo")) (path "bar"))))))

  (testing "remove-root"
    (is (= "foo" (str (remove-root (path (str root "foo")))))))

  (testing "end"
    (is (= "bar" (str (end (path "foo/bar")))))
    (is (= "foo" (str (end (path "foo"))))))

  (testing "trail"
    (is (= rel-path (str (trail (path "foo/bar/baz"))))))


  )
