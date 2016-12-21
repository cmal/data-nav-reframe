(ns data-nav-reframe.views
  (:require-macros [data-nav-reframe.core :refer [log]])
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-com.core :as re-com]
            [data-nav-reframe.config :as conf]
            [dirac.runtime]
            [reagent.core :as reagent]
            )
  )

(dirac.runtime/install!)


(defn left-bar-icon-top [index]
  (str (* index 80) "px"))

(defn left-bar-btn-style [index active]
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
     :children (for [index (range (count conf/icons))
                     :let [name (:id (nth conf/icons index))
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


(defn drawer-child []
  (let [query (reagent/atom "")
        stockid (reagent/atom "")]
    (fn [{:keys [id label]}]
      [re-com/v-box
       ;; :div.drawer-child
       :children
       [[re-com/box
         :child
         [:div.drawer-child-title id]],
        [re-com/box
         :child
         [re-com/single-dropdown
          :choices conf/home-choices
          :model nil
          :placeholder "Have a choice..."
          :on-change #(reset! query %)
          :width "100%"]
         :style
         {
          :padding "0 10px"
          }],
        [re-com/box
         :child
         [:div.stockid-input
          [:div.stockid-label "股票代码:"]
          [re-com/input-text
           :class (log @query @stockid)
           :model stockid
           :on-change #(reset! stockid %)
           :placeholder "600123.SH"]]
         :style
         {
          :padding "0 10px"
          }],
        ;; send input button
        [re-com/box
         :child
         [re-com/button
          :label "生成查询语句"
          :on-click #(dispatch
                      [:send-input
                       {:query @query
                        :stockid @stockid}])
          :class "btn-warning"
          ]
         :style
         {
          :padding "0 10px"
          }],

        ]
       :gap "10px"
       ]
      ))
  )


(defn drawer []
  (let [index (subscribe [:left-bar-active])
        item (nth conf/icons @index)
        name (:id (nth conf/icons @index))
        box [drawer-child item]
        ]
    [re-com/v-box
     :children [box, ]
     :gap "10px"
     :width "100%"
     ]))

(defn input []
  (let [query @(subscribe [:input-text])]
    [re-com/input-textarea
     :model query
     :on-change #(dispatch-sync [:change-input-text %])
     :status (if true nil :error)
     :placeholder "Input a query and press the BUTTON below"
     :style
     {
      :position "relative"
      :width "100%"
      :top "10px"
      :outline "10px solid rgba(172,172,172,0.25)"
      :outline-offset "-10px"
      :margin-bottom "10px"
      :border "0"
      :border-radius "0"
      :font-size "24px"
      }]))

(defn d3-inner [data]
  (log data)
  (reagent/create-class
   {
    :component-did-mount (fn []
                           (let [d3data (clj->js data)]
                             (.. js/d3
                                 (select "svg")
                                 (selectAll "circle")
                                 (data d3data)
                                 enter
                                 (append "svg:circle")
                                 (attr "cx" (fn [d] (.-x d)))
                                 (attr "cy" (fn [d] (.-y d)))
                                 (attr "r" (fn [d] (.-r d)))
                                 (attr "fill" (fn [d] (.-color d))))))

    :component-did-update (fn [this]
                            (let [[_ data] (reagent/argv this)
                                  d3data (clj->js data)
                                  _ (log data)]
                              (.. js/d3
                                  (selectAll "circle")
                                  (data d3data)
                                  (attr "cx" (fn [d] (log d) (.-x d)))
                                  (attr "cy" (fn [d] (.-y d)))
                                  (attr "r" (fn [d] (.-r d))))))
    :display-name "d3-inner"
    :reagent-render (fn [] [:div [:svg {:width 150 :height 150}]])
    }))

(defn chart-1 []
  (let [data @(subscribe [:circles])]
    (fn []
      [:div.chart
       ;; [re-com/throbber
       ;;  :size :small
       ;;  :color "gray"]
       [d3-inner data]])))

(defn show-panel-child []
  (fn [{:keys [id text]}]
    [:div.show-panel-child
     [:div.child-text
      text]
     [:div.child-delete
      [re-com/md-icon-button
       :md-icon-name "zmdi-delete"
       :size :smaller
       :on-click #(dispatch [:delete-show-panel-child id])
       ]]
     [chart-1]])
  )

(defn show-panel []
  [:div.show-panel
   (let [children @(subscribe [:show-panel-child])]
     (for [child children
           :let [[k v] child]
           ]
       ^{:key k}
       [re-com/box
        :child [show-panel-child v]
        :style
        {
         :background-color "white"
         :margin-bottom "10px"
         }])
     )])

(defn results []
  (fn []
    [:div.result-container
     [input]
     [re-com/button
      :label "查看结果"
      :on-click #(dispatch [:add-result])
      :class "btn-primary"
      :style
      {
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
               :style
               {
                :background-color "#4C4957"
                }]
              [re-com/box
               :child [drawer]
               :size "300px"
               :style
               {
                :background-color "#30333A"
                }]
              [re-com/box
               :child [results]
               :size "1 2 200px"
               :style
               {
                :padding "0 20px"
                :background-color "#D2D5DA"
                }]]])
