(ns data-nav-reframe.events
    (:require [re-frame.core :refer [reg-event-db]]
              [data-nav-reframe.db :as db]
              [dirac.runtime])
    (:require-macros [data-nav-reframe.core :refer [log]]))

(dirac.runtime/install!)


;; -- Helpers -------------------

(defn allocate-next-id
  "Returns the next show panel id.
  Assumes show panels are sorted.
  Returns one more than the current largest id."
  [items]
  ((fnil inc 0) (apply max (keys items))))

;; -- Event Handlers ------------
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

(reg-event-db
 :send-input
 (fn [db [_ query]]
   (log query)
   (assoc db :input-text (str query))))

(reg-event-db
 :change-input-text
 (fn [db [_ text]]
   (assoc db :input-text text)))

(reg-event-db
 :add-result
 (fn [db _]
   (let [children (:show-panel-child db)
         id (allocate-next-id children)]
     (update db :show-panel-child
             #(assoc % id
                     {:id id
                      :text (:input-text db)})))))

(reg-event-db
 :delete-show-panel-child
 (fn [db [_ index]]
   (update db :show-panel-child
           #(dissoc % index))))
