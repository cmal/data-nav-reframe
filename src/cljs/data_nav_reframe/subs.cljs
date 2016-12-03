(ns data-nav-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [data-nav-reframe.core :refer [log]])
  (:require [re-frame.core :refer [reg-sub]]
            [dirac.runtime]))

(dirac.runtime/install!)

(reg-sub
 :left-bar-active
 (fn [db _]
   (:left-bar-active db)))

(reg-sub
 :input-text
 (fn [db _]
   (:input-text db)))

(reg-sub
 :show-panel-child
 (fn [db _]
   (:show-panel-child db)))
