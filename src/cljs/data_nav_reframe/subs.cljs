(ns data-nav-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [data-nav-reframe.core :refer [log]])
  (:require [re-frame.core :refer [reg-sub]]
            [dirac.runtime]
            [data-nav-reframe.config :as conf]
            ))

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

(reg-sub
 :circles
 (fn [db _]
   (:circles db)))

(reg-sub
 :sel-info
 (fn [db _]
   (:sel-info db)))

(reg-sub
 :sel-info-url
 :<- [:sel-info]
 (fn [info _]
   (get info :url)))

(reg-sub
 :sel-info-keys
 :<- [:sel-info]
 (fn [info _]
   (concat
    (get info :params)
    (conf/get-keywords info))))
