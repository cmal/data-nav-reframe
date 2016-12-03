(ns data-nav-reframe.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as re-com]
            [dirac.runtime]
            )
  (:require-macros [data-nav-reframe.core :refer [log]])
  )

(dirac.runtime/install!)

;; home

;; (defn home-title []
;;   (let [name (re-frame/subscribe [:name])]
;;     (fn []
;;       [re-com/title
;;        :label (str "Hello from " @name ". This is the Home Page.")
;;        :level :level1])))

;; (defn link-to-about-page []
;;   [re-com/hyperlink-href
;;    :label "go to About Page"
;;    :href "#/about"])

;; (defn home-panel []
;;   [re-com/v-box
;;    :gap "1em"
;;    :children [[home-title] [link-to-about-page]]])


;; (defn left-bar-icon [icon index]
;;   [re-com/box
;;    :height "80px"
;;    :width "80px"
;;    :child [re-com/md-icon-button
;;            :md-icon-name icon
;;            :size :larger
;;            :on-click #()
;;            ]
;;    :style {
;;            :border-bottom "1px solid white"
;;            :color "white"
;;            :position "absolute"
;;            :top (left-bar-icon-top index)
;;            }])

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
  {:position "absolute"
   :top (left-bar-icon-top index)
   :border-bottom "1px solid #999"
   :border-radius "0"
   :color "#ddd"
   :background-color (if active "rgba(0,0,0,0.25)" "transparent")
   :height "80px"
   :width "80px"
   :line-height "80px"
   }
  )

(defn left-bar-btn [name index active]
  (fn [name index active]
    [re-com/box
     :size "80px"
     :child [re-com/md-icon-button
             :md-icon-name name
             :size :larger
             :on-click #(dispatch [:left-bar-select index])
             :style (left-bar-btn-style index active)
             ]
     ])
  )


(defn left-bar []
  (let [selected (subscribe [:left-bar-active])
        ]
    [re-com/v-box
     :children (for [index (range (count icons))
                     :let [name (:id (nth icons index))
                           selected? (= index @selected)]]
                 ^{:key index}
                 [re-com/box
                  :size "80px"
                  :child [re-com/md-icon-button
                          :md-icon-name name
                          :size :larger
                          :on-click #(dispatch [:left-bar-select index])
                          :style (left-bar-btn-style index selected?)]
                  ])
     ]
))


;; (defn drawer []
;;   (let [selected (re-frame/subscribe [:left-bar-active])
;;         ]
;;     (fn []
;;       [re-com/box
;;        :child "dsfa"]))
;;   )

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
               :child "drawer"
               :size "300px"
               :style {
                       :padding "10px"
                       :background-color "#30333A"
                       }]
              [re-com/box
               :child "results"
               :size "1 2 200px"
               :style {
                       :padding "0 20px"
                       :background-color "#D2D5DA"
                       }]]])

;; about

;; (defn about-title []
;;   [re-com/title
;;    :label "This is the About Page."
;;    :level :level1])

;; (defn link-to-home-page []
;;   [re-com/hyperlink-href
;;    :label "go to Home Page"
;;    :href "#/"])

;; (defn about-panel []
;;   [re-com/v-box
;;    :gap "1em"
;;    :children [[about-title] [link-to-home-page]]])


;; ;; main

;; (defmulti panels identity)
;; (defmethod panels :home-panel [] [home-panel])
;; (defmethod panels :about-panel [] [about-panel])
;; (defmethod panels :default [] [:div])

;; (defn show-panel
;;   [panel-name]
;;   [panels panel-name])

;; (defn main-panel []
;;   (let [active-panel (re-frame/subscribe [:active-panel])]
;;     (fn []
;;       [re-com/v-box
;;        :height "100%"
;;        :children [[panels @active-panel]]])))
