(ns data-nav-reframe.core)

(defmacro log [& args]
  `(do (.log js/console ~@args) nil))
