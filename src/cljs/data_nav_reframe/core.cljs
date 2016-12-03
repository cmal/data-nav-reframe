(ns data-nav-reframe.core
    (:require [reagent.core :as reagent]
              [re-frame.core :refer [dispatch-sync]]
              [re-frisk.core :refer [enable-re-frisk!]]
              [data-nav-reframe.events]
              [data-nav-reframe.subs]
              [data-nav-reframe.routes :as routes]
              [data-nav-reframe.views :as views]
              [data-nav-reframe.config :as config]
              [dirac.runtime]
              ))

(dirac.runtime/install!)

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
