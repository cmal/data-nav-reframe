(ns data-nav-reframe.css
  (:require [garden.def :refer [defstyles]]))


(defstyles screen
  [:body {:color "red"}]
  [:div.result-container {:width "100%"}]
  [:div.rc-input-text {:width "100% !important"}]
  [:div.show-panel-child {:width "100%"}
   [:.child-text {:float "left"
                  :color "blue"}]
   [:.child-delete {:float "right"
                    :color "#999"}]]
)
