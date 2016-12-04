(ns data-nav-reframe.css
  (:require [garden.def :refer [defstyles]]))


(defstyles screen
  [:body
   {:color "red"}]
  [:div.result-container
   {:width "100%"}]
  [:div.rc-input-text
   {:width "100% !important"}]
  [:div.show-panel-child
   {:width "100%"}
   [:.child-text
    {:float "left"
     :color "blue"}]
   [:.child-delete
    {:float "right"
     :color "#999"}]]
  [:div.drawer-child-title
   {
    :background-color "rgba(0,0,0,0.25)"
    :width "100%"
    :height "80px"
    :line-height "80px"
    :font-size "40px"
    :padding "0 10px"
    :color "#deccaa"
    :margin-bottom "5px"
    }]
  [:div.stockid-label
   {
    :color "#c987d2"
    }]
  )
