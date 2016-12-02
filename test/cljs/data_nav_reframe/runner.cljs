(ns data-nav-reframe.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [data-nav-reframe.core-test]))

(doo-tests 'data-nav-reframe.core-test)
