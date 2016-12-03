(ns data-nav-reframe.events
    (:require [re-frame.core :refer [reg-event-db]]
              [data-nav-reframe.db :as db]
              [dirac.runtime])
    (:require-macros [data-nav-reframe.core :refer [log]]))

(dirac.runtime/install!)

(reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(reg-event-db
 :left-bar-select
 (fn [db [_ index]]
   (assoc db :left-bar-active index)))
