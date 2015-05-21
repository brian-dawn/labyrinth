(ns labyrinth.core-test
  (:require [clojure.test :refer :all]
            [labyrinth.core :refer :all]))

(deftest path-tests
  (testing "Path creation on UNIX"
    (is (= "/foo/bar" (str (path "/foo/bar"))))
    (is (= "/foo/bar" (str (path "/foo/bar/"))))
    (is (= "/foo/bar" (str (path "/foo//bar"))))

    (is (= "/foo/bar" (str (path "/foo"  "bar"))))
    (is (= "/foo/bar" (str (path "/foo"  "bar"))))
    (is (= "/foo/bar" (str (path "/foo/" "bar/"))))

    (is (= "foo/bar" (str (path "foo"  "bar"))))
    (is (= "foo/bar" (str (path "foo/" "bar"))))
    (is (= "/bar"    (str (path "foo"  "/bar")))))

  (testing "Path joining"
    (is (= "foo/bar"  (str (join (path "foo")  (path "bar")))))
    (is (= "foo/bar"  (str (join (path "foo/") (path "bar")))))
    (is (= "/bar"     (str (join (path "foo/") (path "/bar")))))
    (is (= "/foo/bar" (str (join (path "/foo") (path "bar"))))))

  (testing "remove-root"
    (is (= "foo" (str (remove-root (path "/foo"))))))

  (testing "path-child"
    (is (= "bar" (str (path-child (path "foo/bar")))))
    (is (= "foo" (str (path-child (path "foo"))))))

  (testing "path-parents"
    (is (= "foo/bar" (str (path-parents (path "foo/bar/baz"))))))


  )
