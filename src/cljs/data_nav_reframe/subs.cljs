(ns data-nav-reframe.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :refer [reg-sub]]))

;; (re-frame/reg-sub
;;  :name
;;  (fn [db]
;;    (:name db)))

;; (re-frame/reg-sub
;;  :active-panel
;;  (fn [db _]
;;    (:active-panel db)))


(reg-sub
 :left-bar-active
 (fn [db _]
   (:left-bar-active db)))
