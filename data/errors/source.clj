(ns data.errors.source)

(defmacro good [& rest] `(do ~@rest))
(defmacro bad [& rest] `(do ~@rest))
(def something true)
(defn something-else [])

;; 2 space indentation
(good
 (when something
   (something-else)))

(good
 (with-out-str
   (println "Hello, ")
   (println "world!")))

(bad
  (when something
      (something-else)))

(bad
  (with-out-str
      (println "Hello, ")
      (println "world!")))

;; Align arguments
(good
 (filter even?
         (range 1 10)))

(bad
 (filter even?
    (range 1 10)))


;; Single space args not fn/macro name
(good
 (filter
  even?
  (range 1 10)))

(good
 (or
  'ala
  'bala
  'portokala))

(bad
 (filter
   even?
    (range 1 10)))

(bad
 (or
   'ala
   'bala
   'portokala))

;; Align args and keymaps

(good
 (let [thing1 "some stuff"
       thing2 "other stuff"]
   {:thing1 thing1
    :thing2 thing2}))

(bad
 (let [thing1 "some stuff"
    thing2 "other stuff"]
   {:thing1 thing1
   :thing2 thing2}))

;; Align no docstring

(good
 (defn foo
   [x]
   (identity x)))

(good
 (defn foo [x]
   (identity x)))

(bad
 (defn foo
   [x] (identity x)))

; multi-methods

(good
 (defmethod foo :bar [x] (baz x)))

(good
 (defmethod foo :bar
   [x]
   (baz x)))

(bad
 (defmethod foo
   :bar
   [x]
   (baz x)))

(bad
 (defmethod foo
   :bar [x]
   (baz x)))


(bad
 (if (odd? 6)
   (println "hey!")
   nil))


(defn my-function [thing stuff]
  "Do the thing, with the stuff.  Fast."
  (conj stuff thing))

(defn my-function2 [x]
  (defn my-function-3 [] x))
