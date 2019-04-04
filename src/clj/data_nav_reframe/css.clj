(ns data-nav-reframe.css
  (:require [garden.def :refer [defstyles]]))


(defstyles screen
  [:div.result-container
   {:width "100%"}]
  [:div.rc-input-text
   {:width "100% !important"}]
  [:div.show-panel-child
   {:width "100%"
    :position "relative"
    :transition "all 2s linear 2s"
    }
   [:pre {:transition "all 2s linear 0s"}]
   [:.child-delete
    {
     :background-color "#999"
     :color "#36f"
     :right "10px"
     :top "10px"
     :position "absolute"
     :z-index "1"
     :border-radius "4px"
     :box-shadow "2px 2px 2px rgba(0,0,0,.2)"
     }
    [:&:hover
     {
      :background-color "#cff"
      }]
    [:&:active {:box-shadow "1px 1px 1px rgba(0,0,0,0.2)"}]
    ]]
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
  [:div.chart
   {
    :clear "both"
    }]
  #_[:div.label
   {
    ;; :height "24px" not working
    :background-color "#6c6"
    :border-radius "3px"
    :color "#c03"
    }]
  #_[:div.tree-node
   {:position "relative"
    :margin-left "10px"
    :margin-top "3px"}
   ]
  )
