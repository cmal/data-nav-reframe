(ns data-nav-reframe.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as re-com]
            [dirac.runtime]
            )
  (:require-macros [data-nav-reframe.core :refer [log]])
  )

(dirac.runtime/install!)

(def icons
  [{:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}])



(defn left-bar-icon-top [index]
  (str (* index 80) "px"))

(defn left-bar-btn-style [index active]
  (log "left-bar-btn-style" index active)
  {:position "absolute"
   :top (left-bar-icon-top index)
   :border-bottom "1px solid #777"
   :border-radius "0"
   :color (if active "#FFAF00" "#777")
   :background-color (if active "rgba(0,0,0,0.25)" "transparent")
   :height "80px"
   :width "80px"
   :line-height "80px"
   }
  )

(defn left-bar []
  (let [selected @(subscribe [:left-bar-active])]
    [re-com/v-box
     :children (for [index (range (count icons))
                     :let [name (:id (nth icons index))
                           selected? (= selected index)]]
                 ^{:key index}
                 [re-com/box
                  :size "80px"
                  :child [re-com/md-icon-button
                          :md-icon-name name
                          :size :larger
                          :on-click #(dispatch [:left-bar-select index])
                          :style (left-bar-btn-style index selected?)]
                  ])]))


(defn drawer []
  (let [selected (subscribe [:left-bar-active])
        name (:id (nth icons @selected))]
    [re-com/v-box
     :children [
                [re-com/box
                 :child name]
                [re-com/button
                 :label "Send Input! =>>"
                 :on-click #(dispatch [:send-input name])]
                ]
     :gap "15px"
     :style {
             :padding "20px"
             }]))

(defn input []
  (let [query @(subscribe [:input-text])]
    [re-com/input-textarea
     :model query
     :on-change #(dispatch [:change-input-text %])
     :status (if true nil :error)
     :placeholder "Input a query and press the BUTTON below"
     :style {
             :width "100%"
             }]))

(defn show-panel []
  [:div.show-panel
   (let [children (subscribe [:show-panel-child])]
     (for [x (range (count @children))
           :let [child (nth @children x)]]
       ^{:key x}
       [re-com/box
        :child child
        :style {
                :background-color "white"
                :margin-bottom "10px"
                }]))])

(defn results []
  (fn []
    [:div.result-container
     [input]
     [re-com/button
      :label "Gen Result！"
      :on-click #(dispatch [:gen-result])
      :style {
              :margin-top "-10px"
              :margin-bottom "10px"
              }]
     [show-panel]]))

(defn main-panel []
  [re-com/h-box
   :min-height "100%"
   :min-width "100%"
   :children [[re-com/box
               :child [left-bar]
               :size "80px"
               :style {
                       :background-color "#4C4957"
                       }]
              [re-com/box
               :child [drawer]
               :size "300px"
               :style {
                       :padding "10px"
                       :background-color "#30333A"
                       }]
              [re-com/box
               :child [results]
               :size "1 2 200px"
               :style {
                       :padding "0 20px"
                       :background-color "#D2D5DA"
                       }]]])
