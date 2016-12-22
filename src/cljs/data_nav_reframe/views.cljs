(ns data-nav-reframe.views
  (:require-macros [data-nav-reframe.core :refer [log]])
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-com.core :as re-com]
            [data-nav-reframe.config :as conf]
            [dirac.runtime]
            [reagent.core :as reagent]
            [ajax.core :refer [GET POST]]
            )
  )

(dirac.runtime/install!)



(defn left-bar-btn [index]
  (let [selected (subscribe [:left-bar-active])]
    (fn [index]
      [re-com/md-icon-button
       :md-icon-name (:id (nth conf/icons index))
       :size :larger
       :on-click #(dispatch [:left-bar-select index])
       :style
       {:position "absolute"
        :top (str (* index 80) "px")
        :border-bottom "1px solid #777"
        :border-radius "0"
        :color (if (= @selected index) "#FFAF00" "#777")
        :background-color (if (= @selected index)
                            "rgba(0,0,0,0.25)"
                            "transparent")
        :height "80px"
        :width "80px"
        :line-height "80px"
        }])))

(defn left-bar []
  [re-com/v-box
   :children
   (for [index (range (count conf/icons))
         ]
     ^{:key index}
     [re-com/box
      :size "80px"
      :child [left-bar-btn index]])])


(def tree
  {:label "APIs"
   :items
   [{:label "dashboard"
     :items
     [{:label "stock"
       :items
       [{:label "kchart"}
        {:label "realtime info"}
        {:label "fenshitu"}
        {:label "klinedata"}
        {:label "pepbs"}
        {:label "logpepbs"}
        {:label "earning"}
        {:label "revenue"}
        {:label "announcement"}
        {:label "stockid list"}
        ]}
      {:label "fund"
       :items
       [{:label "basic info"}
        {:label "gradefund"}
        {:label "closeprices"}
        {:label "netvalues"}
        ]}
      {:label "bond"}
      {:label "licai"}
      {:label "user"}
      ]}
    {:label "stock summary"}]})


(defn tree-view [data tree-view-choice]
  ;; BUG if data change children will not change
  (let [children (reagent/atom [])
        open? (reagent/atom false)]
    (fn [{:keys [label items]} tree-view-choice]
      [:div.tree-node {:style {:position "relative" :left "10px"}}
       [:div
        {:on-click
         #(do (reset! children (if @open? [] items))
              (swap! open? not)
              (.log js/console tree-view-choice))}
        [:div.label
         ;; need to change to class -"open" +"close" o"leaf"
         (if @open?
           (if (or (nil? items) (empty? items))
             (do
               (reset! tree-view-choice label) "o ")
             "- ")
           "+ ")
         label]]
       (if @open?
         (for [child @children]
           ^{:key (str child)}
           [tree-view child tree-view-choice]))])))

(defn drawer []
  (let [query (reagent/atom "")
        stockid (reagent/atom "")
        tree-view-choice (reagent/atom "")
        ticked? (reagent/atom false)
        ]
    (fn [{:keys [id label]}]
      [re-com/v-box
       :children
       [[re-com/box
         :child
         [:div.stockid-input
          [:div.stockid-label
           "股票代码:"
           [re-com/checkbox
            :label "remember"
            :model ticked?
            :on-change
            #(do (reset! ticked? %)
                 (if % (dispatch [:remember-stockid @stockid]))) ;; % is true/false
            ]
           ]
          [re-com/input-text
           :class (log @query @stockid)
           :model stockid
           :on-change #(reset! stockid %)
           :placeholder "600123.SH"]]
         ],
        [re-com/box
         :child
         [re-com/single-dropdown
          :choices conf/home-choices
          :model nil
          :placeholder "Have a choice..."
          :on-change #(reset! query %)
          :width "100%"]
         ],
        [re-com/box
         :child [tree-view tree tree-view-choice]]
        ;; send input button
        [re-com/box
         :child
         [re-com/button
          :label "生成查询语句"
          :on-click #(dispatch
                      [:send-input
                       {:query @query
                        :choice @tree-view-choice
                        :stockid @stockid}])
          :class "btn-warning"
          ]
         ],

        ]
       :width "100%"
       :gap "10px"
       :style {:padding "20px"}
       ]
      ))
  )


#_(defn drawer []
  [re-com/v-box
   :children [[drawer-child]]
   :gap "10px"
   :width "100%"
   ])

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
    :component-did-mount
    (fn []
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

    :component-did-update
    (fn [this]
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
    :reagent-render
    (fn [] [:div [:svg {:width 150 :height 150}]])
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
     #_[chart-1]])
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
  [:div.result-container
   [input]
   [re-com/button
    :label "查看结果"
    :on-click #(dispatch [:get-data])
    :class "btn-primary"
    :style
    {
     :margin-top "-10px"
     :margin-bottom "10px"
     }]
   [:pre [:code.json "{status:true, data:[]}"]]
   [show-panel]])


(defn main-panel []
  [re-com/h-box
   :min-height "100%"
   :min-width "100%"
   :children
   [[re-com/box
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
