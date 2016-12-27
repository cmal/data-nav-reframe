(ns data-nav-reframe.views
  (:require-macros [data-nav-reframe.core :refer [log]])
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-com.core :as re-com]
            [data-nav-reframe.config :as conf]
            [dirac.runtime]
            [reagent.core :as reagent]
            [ajax.core :refer [GET POST]]
            [cljsjs.react.dom]
            [cljsjs.react-flip-move]
            )
  )

(dirac.runtime/install!)

(def flip-move (reagent/adapt-react-class js/FlipMove))

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


(def css-transition-group
  (reagent/adapt-react-class js/React.addons.CSSTransitionGroup))

(def transition-in  1)
(def transition-out 0.2)

(def style
  (str
   ".node-enter {
      opacity: 0.01;
      max-height: 0px;
    }

    .node-enter-active {
      opacity: 1;
      max-height: 999px;
      -moz-transition: all " transition-in "s ease-in-out;
      -webkit-transition: all " transition-in "s ease-in-out;
      -ms-transition: all " transition-in "s ease-in-out;
      -o-transition: all " transition-in "s ease-in-out;
      transition: all " transition-in "s ease-in-out;
    }

    .node-leave {
      opacity: 1;
      max-height: 999px;
    }

    .node-leave-active {
      opacity: 0.1;
      max-height: 1px;
      -moz-transition: all " transition-out "s ease-in-out;
      -webkit-transition: all " transition-out "s ease-in-out;
      -ms-transition: all " transition-out "s ease-in-out;
      -o-transition: all " transition-out "s ease-in-out;
      transition: all " transition-out "s ease-in-out;
  }"))

(defn tree-view [data tree-view-choice]
  ;; BUG if data change children will not change
  (let [children (reagent/atom [])
        open? (reagent/atom false)
        _ (.log js/console "re-rendering..." data)
        klass (reagent/atom "not-expand")
        ]
    (fn [{:keys [label items]} tree-view-choice]
      [:div.tree-node
       [:div
        [:style style]
        [:div.label
         {:class (str @klass
                      (when (= @tree-view-choice label)
                        " active"))
          :on-click
          #(do (reset! children (if @open? [] items))
               (swap! open? not)
               (if @open?
                 (if (or (nil? items) (empty? items))
                   (do (reset! tree-view-choice label)
                       (reset! klass "leaf")
                       (.log js/console @tree-view-choice label)
                       )
                   (reset! klass "expand"))
                 (reset! klass "not-expand")))}
         label]
        [css-transition-group
         {:transition-name "node"
          :transition-enter-timeout (* transition-in 1000)
          :transition-leave-timeout (* transition-out 1000)}
         (if @open?
           (for [child @children]
             ^{:key (str child)}
             [tree-view child tree-view-choice]))]]])))

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
        #_[re-com/box
         :child
         [re-com/single-dropdown
          :choices conf/home-choices
          :model nil
          :placeholder "Have a choice..."
          :on-change #(reset! query %)
          :width "100%"]
         ],
        [re-com/box
         :child [tree-view conf/tree tree-view-choice]
         :style {:width "60px"
                 :font-size "20px"}]
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
       [d3-inner data]])))

(defn highlight-code [html-node]
  (let [nodes (.querySelectorAll html-node "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

(defn show-panel-child []
  (let [display-delete (reagent/atom false)]
    (reagent/create-class
     {
      :component-did-mount #(highlight-code (reagent/dom-node %))
      :component-did-update #(highlight-code (reagent/dom-node %))
      :reagent-render
      (fn [{:keys [id text]}]
        [:div.show-panel-child
         {:on-mouse-over #(reset! display-delete true)
          :on-mouse-leave #(reset! display-delete false)}
         [:pre
          [:code.clojure (str text)]]
         [:div.child-delete
          {:style {:display (if @display-delete "block" "none")}}
          [re-com/md-icon-button
           :md-icon-name "zmdi-delete"
           :size :regular
           :on-click #(dispatch [:delete-show-panel-child id])
           ]]
         ])})))

(defn show-panel []
  (let [children (subscribe [:show-panel-child])]
    (fn []
      [:div.show-panel
       (for [child @children
             :let [[k v] child]
             ]
         ^{:key k}
         [re-com/box
          :child
          [show-panel-child v]
          ])
       ])))

(defn results []
  [:div.result-container
   [input]
   [re-com/button
    :label "查看结果"
    :on-click #(dispatch [:get-data])
    :class "btn-primary"
    ]
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
