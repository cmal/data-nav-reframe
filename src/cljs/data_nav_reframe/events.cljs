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
   (log index db)
   (assoc db :left-bar-active index)))

(reg-event-db
 :send-input
 (fn [db [_ name]]
   (let [text name]
     (assoc db :input-text text))))

(reg-event-db
 :change-input-text
 (fn [db [_ text]]
   (log db text)
   (assoc db :input-text text)))

(reg-event-db
 :gen-result
 (fn [db _]
   (log db)
   (update db :show-panel-child #(conj % (:input-text db)))))
