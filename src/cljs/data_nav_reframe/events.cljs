(ns data-nav-reframe.events
    (:require [re-frame.core :refer [reg-event-db]]
              [data-nav-reframe.db :as db]
              [dirac.runtime]
              [cljs.core.async :as async]
              [ajax.core :refer [GET POST]]
              [re-frame.core :refer [dispatch]]
              )
    (:require-macros [data-nav-reframe.core :refer [log]]
                     [cljs.core.async.macros :as async-macros]
                     ))

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
 :get-data
 (fn [db _]
   (let [children (:show-panel-child db)
         id (allocate-next-id children)
         query (:input-text db)]
     (GET
      ;; "https://www.joudou.com/stockinfogate/test"
      "http://localhost:6787/stock/realtime?stockids=600123.SH"
      {
       :handler #(dispatch [:process-response % id])
       :error-handler #()
       })
     (update db :show-panel-child
             #(assoc % id
                     {:id id
                      :text ""})))))

(reg-event-db
 :process-response
 (fn [db [_ data id]]
   (assoc-in db [:show-panel-child id :text] data)))

(reg-event-db
 :delete-show-panel-child
 (fn [db [_ index]]
   (update db :show-panel-child
           #(dissoc % index))))


(reg-event-db
 :remember-stockid
 (fn [db [_ stockid]]
   (assoc db :stockid stockid)))

(reg-event-db
 :change-sel-info
 (fn [db [_ k v]]
   (.log js/console "change-sel-info" k v)
   (assoc-in db [:sel-info k] (or v ""))))

(reg-event-db
 :empty-sel-info
 (fn [db _]
   (assoc db :sel-info {})))
